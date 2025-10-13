package accommodationfinder.service;

import accommodationfinder.listing.Accommodation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class GeocodingService {

    private static final String NOMINATIM_API_URL = "https://nominatim.openstreetmap.org/search";

    public GeoPosition geocodeAddress(Accommodation accommodation) {
        if (accommodation == null) {
            return null;
        }
        try {
            // Build the address string
            StringBuilder addressBuilder = new StringBuilder();
            addressBuilder.append(accommodation.getAddress());
            addressBuilder.append(", ").append(accommodation.getCity());
            if (accommodation.getPostalCode() != null && !accommodation.getPostalCode().trim().isEmpty()) {
                addressBuilder.append(", ").append(accommodation.getPostalCode());
            }
            addressBuilder.append(", South Africa");

            String fullAddress = addressBuilder.toString();
            System.out.println("GeocodingService: Geocoding address: " + fullAddress);

            String encodedAddress = URLEncoder.encode(fullAddress, StandardCharsets.UTF_8);
            String requestUrl = NOMINATIM_API_URL + "?q=" + encodedAddress +
                    "&format=json&limit=1&countrycodes=za";

            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "ResFinderApp/1.0 (iamwriter@regnum.slmail.me)");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            if (connection.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String jsonResponse = reader.lines().collect(Collectors.joining());
                    JSONArray results = new JSONArray(jsonResponse);

                    if (!results.isEmpty()) {
                        JSONObject firstResult = results.getJSONObject(0);
                        double lat = Double.parseDouble(firstResult.getString("lat"));
                        double lon = Double.parseDouble(firstResult.getString("lon"));
                        System.out.println("GeocodingService: Geocoded successfully to: " + lat + ", " + lon);
                        return new GeoPosition(lat, lon);
                    } else {
                        System.err.println("GeocodingService: No location found for address.");
                    }
                }
            } else {
                System.err.println("GeocodingService: API request failed with status code: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            System.err.println("GeocodingService: An exception occurred: " + e.getMessage());
        }
        return null;
    }
}