package accommodationfinder.data;

import accommodationfinder.auth.User;
import accommodationfinder.listing.Accommodation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SavedListingDAO {
    private  final DatabaseConnection dbConnection;
    private final UserDao userDao;

    public SavedListingDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.userDao = new UserDao(dbConnection);
    }

    /**
     * Saves accommodation for a user.
     * @param userId The ID of the user saving the listing.
     * @param accommodationId The ID of the accommodation to save.
     */
    public void createSavedListing(long userId, long accommodationId) throws SQLException {
        String sql = "INSERT INTO saved_listings(user_id, accommodation_id) values (?, ?)";

        try(Connection connection = dbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, accommodationId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving listing: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves all accommodation listings saved by a specific user.
     * @param userId The ID of the user.
     * @return A List of Accommodation objects.
     */
    public List<Accommodation> getSavedListingsForUser(long userId) throws SQLException {
        List<Accommodation> savedListings = new ArrayList<>();

        String sql = "SELECT a.* FROM ACCOMMODATIONS a JOIN SAVED_LISTINGS sl ON a.id = sl.accommodation_id " +
                "WHERE sl.user_id = ? ORDER BY sl.saved_at DESC";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Accommodation accommodation = mapRowToAccommodation(resultSet);
                    savedListings.add(accommodation);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting saved listings for user: " + e.getMessage());
            throw e;
        }
        return savedListings;
    }


    /**
     * Checks if a specific listing is already saved by a user.
     * @param userId The ID of the user.
     * @param accommodationId The ID of the accommodation.
     * @return true if the listing is saved, false otherwise.
     */
    public boolean isListingSaved(long userId, Long accommodationId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM SAVED_LISTINGS WHERE user_id = ? AND accommodation_id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, accommodationId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if saved listing exists: " + e.getMessage());
            throw e;
        }
        return false;
    }


    public void removeSavedListing(long userId, int accommodationId) throws SQLException {
        String sql = "DELETE FROM SAVED_LISTINGS WHERE user_id = ? AND accommodation_id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, userId);
            preparedStatement.setInt(2, accommodationId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error checking deleting saved listing: " + e.getMessage());
            throw e;
        }
    }

    // Helper method to map a ResultSet row to an Accommodation object.
    private Accommodation mapRowToAccommodation(ResultSet rs) throws SQLException {
        long listedByUserId = rs.getLong("listed_by_user_id");
        User listedBy = userDao.getUserById(listedByUserId);

        Accommodation acc = new Accommodation(
                rs.getString("title"),
                rs.getString("description"),
                Accommodation.AccommodationType.valueOf(rs.getString("type")),
                rs.getString("address"),
                rs.getString("city"),
                rs.getString("postal_code"),
                rs.getDouble("latitude"),
                rs.getDouble("longitude"),
                rs.getBigDecimal("price"),
                Accommodation.PriceFrequency.valueOf(rs.getString("price_frequency")),
                rs.getInt("bedrooms"),
                rs.getInt("bathrooms"),
                rs.getInt("max_occupancy"),
                rs.getBoolean("internet_included"),
                rs.getBoolean("utilities_included"),
                rs.getBoolean("parking_available"),
                rs.getString("lease_term"),
                rs.getTimestamp("available_from") != null ? rs.getTimestamp("available_from").toLocalDateTime() : null,
                rs.getTimestamp("available_until") != null ? rs.getTimestamp("available_until").toLocalDateTime() : null,
                rs.getBoolean("nsfas_accredited"),
                listedBy
        );
        acc.setId(rs.getLong("id"));
        acc.setListingDate(rs.getTimestamp("listing_date").toLocalDateTime());
        acc.setLastUpdatedDate(rs.getTimestamp("last_updated_date").toLocalDateTime());
        acc.setStatus(Accommodation.AccommodationStatus.valueOf(rs.getString("status")));

        // Handle image URLs (assuming comma-separated string)
        String imageUrlsStr = rs.getString("image_urls");
        if (imageUrlsStr != null && !imageUrlsStr.isEmpty()) {
            acc.getImageUrls().addAll(List.of(imageUrlsStr.split(",")));
        }

        return acc;
    }
}
