package accommodationfinder.listing;

import accommodationfinder.auth.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Accommodation {

    private Long id; // Unique identifier for the accommodation listing
    private String title; // Title of the listing
    private String description; // Detailed description of the accommodation
    private AccommodationType type; // Enum for Accommodation Type (Apartment, House, Dorm, etc.)
    private String address; // Full address of the accommodation
    private String city;     // City
    private String postalCode; // Postal Code/Zip Code
    private double latitude;  // Geographic coordinates for the map integration
    private double longitude; // Geographic coordinates for the map integration
    private BigDecimal price; // Price/Rent amount (in ZAR)
    private PriceFrequency priceFrequency; // Enum for how often the price is (Per Month, Per Week, etc.)
    private int bedrooms; // Number of bedrooms
    private int bathrooms; // Number of bathrooms
    private int maxOccupancy; // Maximum number of people the accommodation can hold
    private boolean internetIncluded; // Is internet included in the price?
    private boolean utilitiesIncluded; // Are utilities (water, electricity, gas) included?
    private boolean parkingAvailable; // Is parking available?
    private String leaseTerm; // Description of the lease term for example "12-month lease", "Semester lease")
    private LocalDateTime availableFrom; // Date when the accommodation becomes available
    private LocalDateTime availableUntil; // Date when the accommodation is available until
    private List<String> imageUrls; // List of URLs to images of the accommodation
    private AccommodationStatus status; // Enum for Listing Status (ACTIVE, INACTIVE, PENDING, etc.)
    private LocalDateTime listingDate; // Date when the listing was created
    private LocalDateTime lastUpdatedDate; // Date when the listing was last updated
    private boolean nsfasAccredited; // Is the accommodation NSFAS accredited?

    private User listedBy; // User who listed this accommodation

    // Enums for Type, Price Frequency, and Status
    public enum AccommodationType {
        APARTMENT,
        HOUSE,
        DORM,
        SHARED_ROOM,
        STUDIO,
        OTHER
    }

    public enum PriceFrequency {
        PER_MONTH,
        PER_WEEK,
        PER_SEMESTER,
        PER_NIGHT,
        OTHER // For less common frequencies
    }

    public enum AccommodationStatus {
        ACTIVE,     // Listing is visible and available
        INACTIVE,   // Listing is hidden, no longer available
        PENDING,    // Listing is under review or pending approval
        RENTED,     // Accommodation is currently rented/occupied
        DRAFT      // Listing is being created but not yet published
    }

    // Constructors

    public Accommodation() {
        this.imageUrls = new ArrayList<>();
    }

    public Accommodation(String title, String description, AccommodationType type, String address,
                         String city, String postalCode, double latitude, double longitude, BigDecimal price,
                         PriceFrequency priceFrequency, int bedrooms, int bathrooms, int maxOccupancy,
                         boolean internetIncluded, boolean utilitiesIncluded, boolean parkingAvailable,
                         String leaseTerm, LocalDateTime availableFrom, LocalDateTime availableUntil,
                         boolean nsfasAccredited, User listedBy) {

        this();
        this.title = title;
        this.description = description;
        this.type = type;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.priceFrequency = priceFrequency;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.maxOccupancy = maxOccupancy;
        this.internetIncluded = internetIncluded;
        this.utilitiesIncluded = utilitiesIncluded;
        this.parkingAvailable = parkingAvailable;
        this.leaseTerm = leaseTerm;
        this.availableFrom = availableFrom;
        this.availableUntil = availableUntil;
        this.nsfasAccredited = nsfasAccredited;
        this.listedBy = listedBy;
        this.listingDate = LocalDateTime.now();
        this.lastUpdatedDate = LocalDateTime.now();
        this.status = AccommodationStatus.ACTIVE;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AccommodationType getType() {
        return type;
    }

    public void setType(AccommodationType type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public PriceFrequency getPriceFrequency() {
        return priceFrequency;
    }

    public void setPriceFrequency(PriceFrequency priceFrequency) {
        this.priceFrequency = priceFrequency;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }


    public boolean isInternetIncluded() {
        return internetIncluded;
    }

    public void setInternetIncluded(boolean internetIncluded) {
        this.internetIncluded = internetIncluded;
    }

    public boolean isUtilitiesIncluded() {
        return utilitiesIncluded;
    }

    public void setUtilitiesIncluded(boolean utilitiesIncluded) {
        this.utilitiesIncluded = utilitiesIncluded;
    }

    public boolean isParkingAvailable() {
        return parkingAvailable;
    }

    public void setParkingAvailable(boolean parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

    public String getLeaseTerm() {
        return leaseTerm;
    }

    public void setLeaseTerm(String leaseTerm) {
        this.leaseTerm = leaseTerm;
    }

    public LocalDateTime getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDateTime availableFrom) {
        this.availableFrom = availableFrom;
    }

    public LocalDateTime getAvailableUntil() {
        return availableUntil;
    }

    public void setAvailableUntil(LocalDateTime availableUntil) {
        this.availableUntil = availableUntil;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public AccommodationStatus getStatus() {
        return status;
    }

    public void setStatus(AccommodationStatus status) {
        this.status = status;
    }

    public LocalDateTime getListingDate() {
        return listingDate;
    }

    public void setListingDate(LocalDateTime listingDate) {
        this.listingDate = listingDate;
    }

    public LocalDateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public User getListedBy() {
        return listedBy;
    }

    public void setListedBy(User listedBy) {
        this.listedBy = listedBy;
    }

    public boolean isNsfasAccredited() {
        return nsfasAccredited;
    }

    public void setNsfasAccredited(boolean nsfasAccredited) {
        this.nsfasAccredited = nsfasAccredited;
    }

    // toString() method

    @Override
    public String toString() {
        return "Accommodation{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", price=" + price + " ZAR" +
                ", priceFrequency=" + priceFrequency +
                ", bedrooms=" + bedrooms +
                ", bathrooms=" + bathrooms +
                ", city='" + city + '\'' +
                ", status=" + status +
                ", nsfasAccredited=" + nsfasAccredited +
                ", listedBy=" + (listedBy != null ? listedBy.getUsername() : "null") +
                '}';
    }

    // equals() and hashCode() override
    @Override
    public boolean equals(Object object) {
        if (this == object)
            return  true;

        if (object == null || getClass() != object.getClass())
            return false;

        Accommodation that = (Accommodation) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}