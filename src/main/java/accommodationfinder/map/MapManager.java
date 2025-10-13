package accommodationfinder.map;

import accommodationfinder.listing.Accommodation;
import accommodationfinder.service.GeocodingService;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

import java.util.Collections;
import java.util.Set;

public class MapManager {

    private final GeocodingService geocodingService;

    public MapManager() {
        this.geocodingService = new GeocodingService();
    }

    public JXMapViewer createMapViewer(Accommodation accommodation) {
        if (accommodation == null) {
            return createErrorViewer("No accommodation data provided.");
        }

        GeoPosition location = getGeoPosition(accommodation);
        if (location == null) {
            return createErrorViewer("Could not determine location.");
        }

        // Create and configure the map viewer
        JXMapViewer mapViewer = new JXMapViewer();
        OSMTileFactoryInfo info = new OSMTileFactoryInfo("OpenStreetMap", "https://tile.openstreetmap.org");
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
        mapViewer.setZoom(4);
        mapViewer.setAddressLocation(location);

        // Create and add the waypoint pin
        Waypoint waypoint = new DefaultWaypoint(location);
        CustomWaypointPainter waypointPainter = new CustomWaypointPainter();
        waypointPainter.setWaypoints(Collections.singleton(waypoint));

        mapViewer.setOverlayPainter(new CompoundPainter<>(waypointPainter));

        // Add interactivity listeners
        PanMouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        return mapViewer;
    }

    private GeoPosition getGeoPosition(Accommodation accommodation) {
        // Prioritize existing coordinates
        if (accommodation.getLatitude() != 0 && accommodation.getLongitude() != 0) {
            System.out.println("MapManager: Using pre-existing coordinates.");
            return new GeoPosition(accommodation.getLatitude(), accommodation.getLongitude());
        } else {
            // Fallback to geocoding the address
            System.out.println("MapManager: Geocoding address as a fallback.");
            return geocodingService.geocodeAddress(accommodation);
        }
    }

    private JXMapViewer createErrorViewer(String message) {
        JXMapViewer errorViewer = new JXMapViewer();
        System.err.println("MapManager: " + message);
        return errorViewer;
    }
}