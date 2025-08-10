package accommodationfinder.service;

import accommodationfinder.auth.User;
import accommodationfinder.data.AccommodationDao;
import accommodationfinder.data.UserDao;
import accommodationfinder.listing.Accommodation;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class AccommodationService {

    private final AccommodationDao accommodationDao;
    private final UserDao userDao;

    // Constructor for Dependency Injection
    public AccommodationService(AccommodationDao accommodationDao, UserDao userDao) {
        this.accommodationDao = accommodationDao;
        this.userDao = userDao; // Store the injected UserDao
    }

    /**
     * Creates a new accommodation listing after performing validation and setting defaults.
     * Ensures the listing user is valid.
     *
     * @param accommodation The accommodation data from the UI/caller.
     * @param listingUser The user attempting to create the listing.
     * @return The ID of the newly created accommodation.
     * @throws SQLException If a database error occurs.
     * @throws IllegalArgumentException If validation fails.
     * @throws SecurityException If the user doesn't have permission (if implemented).
     */
    public Long createListing(Accommodation accommodation, User listingUser) throws SQLException, IllegalArgumentException {
        if (listingUser == null || listingUser.getId() == null) {
            throw new IllegalArgumentException("A valid logged-in user is required to create a listing.");
        }

        // Validation Logic
        if (accommodation.getTitle() == null || accommodation.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Listing title cannot be empty.");
        }
        if (accommodation.getPrice() == null || accommodation.getPrice().signum() < 0) {
            throw new IllegalArgumentException("Price cannot be null or negative.");
        }
        // TODO: Add more validation rules


        // Set Service-Managed Fields
        accommodation.setListedBy(listingUser);
        accommodation.setListingDate(LocalDateTime.now());
        accommodation.setLastUpdatedDate(LocalDateTime.now());
        if (accommodation.getStatus() == null) {
            accommodation.setStatus(Accommodation.AccommodationStatus.ACTIVE);
        }


        // Delegate to DAO
        return accommodationDao.createAccommodation(accommodation);
    }

    /**
     * Retrieves a specific accommodation listing by its ID.
     *
     * @param id The ID of the listing.
     * @return The Accommodation object, or null if not found.
     * @throws SQLException If a database error occurs.
     */
    public Accommodation getListingById(Long id) throws SQLException {
        return accommodationDao.getAccommodationById(id);
    }

    /**
     * Retrieves all currently active accommodation listings.
     * (Or implement a more complex search method later)
     *
     * @return A list of active Accommodation objects.
     * @throws SQLException If a database error occurs.
     */
    public List<Accommodation> getAllActiveListings() throws SQLException {

        return accommodationDao.getAllAccommodations(); // TODO: Implement search
    }

    /**
     * Updates an existing accommodation listing.
     * Includes validation and permission checks (if implemented).
     *
     * @param accommodation The accommodation with updated data. Must have a valid ID.
     * @param currentUser The user attempting the update.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException If a database error occurs.
     * @throws IllegalArgumentException If validation fails.
     * @throws SecurityException If the user doesn't have permission.
     */
    public boolean updateListing(Accommodation accommodation, User currentUser) throws SQLException, IllegalArgumentException, SecurityException {
        if (accommodation == null || accommodation.getId() == null) {
            throw new IllegalArgumentException("Accommodation or its ID cannot be null for update.");
        }
        if (currentUser == null || currentUser.getId() == null) {
            throw new SecurityException("User must be logged in to update listings.");
        }

        // Permission Check
        Accommodation existingListing = accommodationDao.getAccommodationById(accommodation.getId());
        if (existingListing == null) {
            throw new IllegalArgumentException("Accommodation with ID " + accommodation.getId() + " not found.");
        }
        // Only the user who listed it can update
        if (!existingListing.getListedBy().getId().equals(currentUser.getId())) {
            throw new SecurityException("You do not have permission to update this listing.");
        }


        // Validation Logic
        if (accommodation.getTitle() == null || accommodation.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Listing title cannot be empty.");
        }


        // Prepare for Update
        accommodation.setListedBy(existingListing.getListedBy());
        accommodation.setListingDate(existingListing.getListingDate());
        accommodation.setLastUpdatedDate(LocalDateTime.now());

        // Delegate to DAO
        return accommodationDao.updateAccommodation(accommodation);
    }

    /**
     * Deletes an accommodation listing.
     * Includes permission checks (if implemented).
     *
     * @param accommodationId The ID of the listing to delete.
     * @param currentUser The user attempting the deletion.
     * @return true if deletion was successful, false otherwise.
     * @throws SQLException If a database error occurs.
     * @throws SecurityException If the user doesn't have permission.
     */
    public boolean deleteListing(Long accommodationId, User currentUser) throws SQLException, SecurityException {
        if (accommodationId == null) {
            throw new IllegalArgumentException("Accommodation ID cannot be null for deletion.");
        }
        if (currentUser == null || currentUser.getId() == null) {
            throw new SecurityException("User must be logged in to delete listings.");
        }

        // Permission Check
        Accommodation existingListing = accommodationDao.getAccommodationById(accommodationId);
        if (existingListing == null) {
            return false;
        }
        if (!existingListing.getListedBy().getId().equals(currentUser.getId())) {
            throw new SecurityException("You do not have permission to delete this listing.");
        }

        // Delegate to DAO
        return accommodationDao.deleteAccommodation(accommodationId);
    }

}