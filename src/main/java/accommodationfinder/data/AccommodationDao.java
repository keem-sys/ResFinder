package accommodationfinder.data;

import accommodationfinder.auth.User;
import accommodationfinder.listing.Accommodation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccommodationDao {

    private final DatabaseConnection dbConnection;
    private final UserDao userDao;

    private static final String IMAGE_URL_DELIMITER = ";";

    public AccommodationDao(DatabaseConnection dbConnection, UserDao userDao) {
        this.dbConnection = dbConnection;
        this.userDao = userDao;
    }

    /**
     * Creates a new accommodation listing in the database.
     *
     * @param accommodation The Accommodation object to create. The listedBy User must have a valid ID.
     * @return The generated ID of the newly created accommodation.
     * @throws SQLException If a database access error occurs or the user ID is invalid.
     */
    public Long createAccommodation(Accommodation accommodation) throws SQLException {
        // Ensure the user who listed exists and has an ID
        if (accommodation.getListedBy() == null || accommodation.getListedBy().getId() == null) {
            throw new SQLException("Cannot create accommodation: listedBy User or User ID is missing.");
        }

        String sql = """
            INSERT INTO ACCOMMODATIONS (
                title, description, type, address, city, postal_code, latitude, longitude,
                price, price_frequency, bedrooms, bathrooms, max_occupancy,
                internet_included, utilities_included, parking_available, lease_term,
                available_from, available_until, image_urls, status,
                listing_date, last_updated_date, nsfas_accredited, listed_by_user_id
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, accommodation.getTitle());
            ps.setString(2, accommodation.getDescription());
            ps.setString(3, accommodation.getType().name()); // Store enum name as String
            ps.setString(4, accommodation.getAddress());
            ps.setString(5, accommodation.getCity());
            ps.setString(6, accommodation.getPostalCode());
            ps.setDouble(7, accommodation.getLatitude());
            ps.setDouble(8, accommodation.getLongitude());
            ps.setBigDecimal(9, accommodation.getPrice());
            ps.setString(10, accommodation.getPriceFrequency().name());
            ps.setInt(11, accommodation.getBedrooms());
            ps.setInt(12, accommodation.getBathrooms());
            ps.setInt(13, accommodation.getMaxOccupancy());
            ps.setBoolean(14, accommodation.isInternetIncluded());
            ps.setBoolean(15, accommodation.isUtilitiesIncluded());
            ps.setBoolean(16, accommodation.isParkingAvailable());
            ps.setString(17, accommodation.getLeaseTerm());

            // Convert LocalDateTime to Timestamp, handling nulls
            ps.setTimestamp(18, accommodation.getAvailableFrom() != null ?
                    Timestamp.valueOf(accommodation.getAvailableFrom()) : null);
            ps.setTimestamp(19, accommodation.getAvailableUntil() != null ?
                    Timestamp.valueOf(accommodation.getAvailableUntil()) : null);

            String imageUrlsString = accommodation.getImageUrls() != null ?
                    String.join(IMAGE_URL_DELIMITER, accommodation.getImageUrls()) : null;
            ps.setString(20, imageUrlsString);

            ps.setString(21, accommodation.getStatus().name());
            ps.setTimestamp(22, Timestamp.valueOf(accommodation.getListingDate()));
            ps.setTimestamp(23, Timestamp.valueOf(accommodation.getLastUpdatedDate()));
            ps.setBoolean(24, accommodation.isNsfasAccredited());
            ps.setLong(25, accommodation.getListedBy().getId()); // Use the User's ID

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating accommodation failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating accommodation failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating accommodation in database: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves an accommodation listing by its unique ID.
     *
     * @param id The ID of the accommodation to retrieve.
     * @return The Accommodation object if found, or null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public Accommodation getAccommodationById(Long id) throws SQLException {
        String sql = "SELECT * FROM ACCOMMODATIONS WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccommodation(rs);
                } else {
                    return null; // Not found
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting accommodation by ID: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves all accommodation listings from the database.
     * NOTE: For large datasets, consider adding pagination (LIMIT/OFFSET).
     *
     * @return A List of all Accommodation objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Accommodation> getAllAccommodations() throws SQLException {
        List<Accommodation> accommodations = new ArrayList<>();
        String sql = "SELECT * FROM ACCOMMODATIONS ORDER BY listing_date DESC"; // Example ordering

        try (Connection connection = dbConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                accommodations.add(mapResultSetToAccommodation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all accommodations: " + e.getMessage());
            throw e;
        }
        return accommodations;
    }

    /**
     * Updates an existing accommodation listing in the database.
     *
     * @param accommodation The Accommodation object with updated information. Must have a valid ID.
     * @return true if the update was successful (at least one row affected), false otherwise.
     * @throws SQLException If a database access error occurs or the accommodation ID is null.
     */
    public boolean updateAccommodation(Accommodation accommodation) throws SQLException {
        if (accommodation.getId() == null) {
            throw new SQLException("Cannot update accommodation: ID is missing.");
        }

        if (accommodation.getListedBy() == null || accommodation.getListedBy().getId() == null) {
            System.err.println("Warning: Updating accommodation with missing listedBy user ID.");
        }

        String sql = """
            UPDATE ACCOMMODATIONS SET
                title = ?, description = ?, type = ?, address = ?, city = ?, postal_code = ?,
                latitude = ?, longitude = ?, price = ?, price_frequency = ?, bedrooms = ?,
                bathrooms = ?, max_occupancy = ?, internet_included = ?, utilities_included = ?,
                parking_available = ?, lease_term = ?, available_from = ?, available_until = ?,
                image_urls = ?, status = ?, last_updated_date = ?, nsfas_accredited = ?,
                listed_by_user_id = ?
            WHERE id = ?
            """;

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, accommodation.getTitle());
            ps.setString(2, accommodation.getDescription());
            ps.setString(3, accommodation.getType().name());
            ps.setString(4, accommodation.getAddress());
            ps.setString(5, accommodation.getCity());
            ps.setString(6, accommodation.getPostalCode());
            ps.setDouble(7, accommodation.getLatitude());
            ps.setDouble(8, accommodation.getLongitude());
            ps.setBigDecimal(9, accommodation.getPrice());
            ps.setString(10, accommodation.getPriceFrequency().name());
            ps.setInt(11, accommodation.getBedrooms());
            ps.setInt(12, accommodation.getBathrooms());
            ps.setInt(13, accommodation.getMaxOccupancy());
            ps.setBoolean(14, accommodation.isInternetIncluded());
            ps.setBoolean(15, accommodation.isUtilitiesIncluded());
            ps.setBoolean(16, accommodation.isParkingAvailable());
            ps.setString(17, accommodation.getLeaseTerm());
            ps.setTimestamp(18, accommodation.getAvailableFrom() != null ?
                    Timestamp.valueOf(accommodation.getAvailableFrom()) : null);
            ps.setTimestamp(19, accommodation.getAvailableUntil() != null ?
                    Timestamp.valueOf(accommodation.getAvailableUntil()) : null);
            String imageUrlsString = accommodation.getImageUrls() != null ?
                    String.join(IMAGE_URL_DELIMITER, accommodation.getImageUrls()) : null;
            ps.setString(20, imageUrlsString);
            ps.setString(21, accommodation.getStatus().name());
            ps.setTimestamp(22, Timestamp.valueOf(LocalDateTime.now()));
            ps.setBoolean(23, accommodation.isNsfasAccredited());
            // Set listed_by_user_id even on update, in case it changes (though less likely)
            ps.setLong(24, accommodation.getListedBy() != null ?
                    accommodation.getListedBy().getId() : null);

            ps.setLong(25, accommodation.getId());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating accommodation: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Deletes an accommodation listing from the database by its ID.
     *
     * @param id The ID of the accommodation to delete.
     * @return true if the deletion was successful (at least one row affected), false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean deleteAccommodation(Long id) throws SQLException {
        String sql = "DELETE FROM ACCOMMODATIONS WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting accommodation: " + e.getMessage());
            throw e;
        }
    }


    // Helper Method to Map ResultSet to Accommodation Object
    private Accommodation mapResultSetToAccommodation(ResultSet rs) throws SQLException {
        Accommodation accommodation = new Accommodation();

        accommodation.setId(rs.getLong("id"));
        accommodation.setTitle(rs.getString("title"));
        accommodation.setDescription(rs.getString("description"));

        // Map Enums
        try {
            accommodation.setType(Accommodation.AccommodationType.valueOf(rs.getString("type")));
            accommodation.setPriceFrequency(Accommodation.PriceFrequency.valueOf(rs.getString("price_frequency")));
            accommodation.setStatus(Accommodation.AccommodationStatus.valueOf(rs.getString("status")));
        } catch (IllegalArgumentException | NullPointerException e) {
            System.err.println("Warning: Invalid enum value found in database for accommodation ID " +
                    rs.getLong("id") + ". Error: " + e.getMessage());

        }

        accommodation.setAddress(rs.getString("address"));
        accommodation.setCity(rs.getString("city"));
        accommodation.setPostalCode(rs.getString("postal_code"));
        accommodation.setLatitude(rs.getDouble("latitude"));
        accommodation.setLongitude(rs.getDouble("longitude"));
        accommodation.setPrice(rs.getBigDecimal("price"));
        accommodation.setBedrooms(rs.getInt("bedrooms"));
        accommodation.setBathrooms(rs.getInt("bathrooms"));
        accommodation.setMaxOccupancy(rs.getInt("max_occupancy"));
        accommodation.setInternetIncluded(rs.getBoolean("internet_included"));
        accommodation.setUtilitiesIncluded(rs.getBoolean("utilities_included"));
        accommodation.setParkingAvailable(rs.getBoolean("parking_available"));
        accommodation.setLeaseTerm(rs.getString("lease_term"));

        // Map Timestamps to LocalDateTime, handling nulls
        Timestamp availableFromTs = rs.getTimestamp("available_from");
        if (availableFromTs != null) {
            accommodation.setAvailableFrom(availableFromTs.toLocalDateTime());
        }
        Timestamp availableUntilTs = rs.getTimestamp("available_until");
        if (availableUntilTs != null) {
            accommodation.setAvailableUntil(availableUntilTs.toLocalDateTime());
        }
        Timestamp listingDateTs = rs.getTimestamp("listing_date");
        if (listingDateTs != null) {
            accommodation.setListingDate(listingDateTs.toLocalDateTime());
        }
        Timestamp lastUpdatedTs = rs.getTimestamp("last_updated_date");
        if (lastUpdatedTs != null) {
            accommodation.setLastUpdatedDate(lastUpdatedTs.toLocalDateTime());
        }

        // Split image URLs string back into a List
        String imageUrlsString = rs.getString("image_urls");
        if (imageUrlsString != null && !imageUrlsString.isEmpty()) {
            accommodation.setImageUrls(Arrays.asList(imageUrlsString.split(IMAGE_URL_DELIMITER)));
        } else {
            accommodation.setImageUrls(new ArrayList<>());
        }

        accommodation.setNsfasAccredited(rs.getBoolean("nsfas_accredited"));

        // Fetch associated User object
        long listedByUserId = rs.getLong("listed_by_user_id");
        if (!rs.wasNull()) { // Check if foreign key was actually present
            User listedBy = userDao.getUserById(listedByUserId);
            if (listedBy == null) {
                System.err.println("Warning: Could not find user with ID " + listedByUserId + " referenced by accommodation ID " + accommodation.getId());

            }
            accommodation.setListedBy(listedBy);
        }

        return accommodation;
    }

}