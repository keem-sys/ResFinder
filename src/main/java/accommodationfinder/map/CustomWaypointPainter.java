package accommodationfinder.map;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.viewer.WaypointRenderer;
import java.awt.*;
import java.awt.geom.Point2D;

public class CustomWaypointPainter extends WaypointPainter<Waypoint> {

    public CustomWaypointPainter() {
        setRenderer(new CustomWaypointRenderer());
    }

    private static class CustomWaypointRenderer implements WaypointRenderer<Waypoint> {
        @Override
        public void paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint waypoint) {
            Point2D point = map.getTileFactory().geoToPixel(waypoint.getPosition(), map.getZoom());
            int x = (int) point.getX();
            int y = (int) point.getY();

            int pinWidth = 32;
            int pinHeight = 48;
            int pinX = x - pinWidth / 2;
            int pinY = y - pinHeight;

            g.setColor(new Color(0, 0, 0, 50));
            g.fillOval(pinX + 2, pinY + 2, pinWidth, pinWidth);

            g.setColor(Color.RED);
            g.fillOval(pinX, pinY, pinWidth, pinWidth);
            g.fillPolygon(new int[]{x, pinX, pinX + pinWidth}, new int[]{y, pinY + pinWidth / 2, pinY + pinWidth / 2}, 3);

            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3));
            g.drawOval(pinX, pinY, pinWidth, pinWidth);
            g.fillOval(x - 6, pinY + pinWidth / 2 - 6, 12, 12);
        }
    }
}