
/* package accommodationfinder.map;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkConnectivityTest {

    public static void main(String[] args) {
        System.setProperty("java.net.useSystemProxies", "true");

        String tileUrl = "https://tile.openstreetmap.org/15/18075/19671.png";

        System.out.println("Attempting to connect to: " + tileUrl);

        try {
            URL url = new URL(tileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "ResFinderApp/1.0 (iamwriter@regnum.slmail.me)");
            connection.setConnectTimeout(10000); // 10 second timeout
            connection.setReadTimeout(10000);

            System.out.println("Connection configured. Awaiting response...");

            int responseCode = connection.getResponseCode();
            System.out.println("Server responded with HTTP Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Connection successful. Downloading image...");

                // Read image data and save it to a file
                try (InputStream inputStream = connection.getInputStream();
                     FileOutputStream outputStream = new FileOutputStream("test_tile.png")) {

                    byte[] buffer = new byte[2048];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }

                System.out.println("\nSUCCESS! Image 'test_tile.png' was saved to project's root directory.");
                System.out.println("Image is valid, the network connection from Java is working.");

            } else {
                System.err.println("\nFAILED! The server responded with an error code.");
            }

        } catch (Exception e) {
            System.err.println("\nCRITICAL FAILURE! An exception occurred during the connection attempt.");
            System.err.println("Please see the details below:\n");

            e.printStackTrace();
        }
    }
} */