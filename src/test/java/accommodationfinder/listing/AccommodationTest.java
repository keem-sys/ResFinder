package accommodationfinder.listing;

import accommodationfinder.auth.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AccommodationTest {

    private Accommodation accommodation;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setUsername("testUser");

        accommodation = new Accommodation(
                "Test Apartment",
                "A nice apartment for students",
                Accommodation.AccommodationType.APARTMENT,
                "123 Test Street",
                "Test City",
                "12345",
                -26.2041,
                28.0473,
                new BigDecimal("15000"),
                Accommodation.PriceFrequency.PER_MONTH,
                2,
                1,
                4,
                true,
                true,
                true,
                "12-month lease",
                LocalDateTime.now(),
                LocalDateTime.now().plusYears(1),
                true,
                testUser
        );
    }

    @Test
    public void testAccommodationCreation() {
        assertNotNull(accommodation);
        assertEquals("Test Apartment", accommodation.getTitle());
        assertEquals(Accommodation.AccommodationType.APARTMENT, accommodation.getType());
        assertEquals(new BigDecimal("15000"), accommodation.getPrice());
        assertEquals(Accommodation.PriceFrequency.PER_MONTH, accommodation.getPriceFrequency());
        assertEquals(2, accommodation.getBedrooms());
        assertEquals(1, accommodation.getBathrooms());
        assertTrue(accommodation.isInternetIncluded());
        assertTrue(accommodation.isUtilitiesIncluded());
        assertTrue(accommodation.isParkingAvailable());
        assertTrue(accommodation.isNsfasAccredited());
    }

    @Test
    public void testSetters() {
        accommodation.setTitle("Updated Title");
        accommodation.setType(Accommodation.AccommodationType.HOUSE);
        accommodation.setPrice(new BigDecimal("20000"));
        accommodation.setBedrooms(3);
        accommodation.setBathrooms(2);
        accommodation.setInternetIncluded(false);
        accommodation.setUtilitiesIncluded(false);
        accommodation.setParkingAvailable(false);
        accommodation.setNsfasAccredited(false);

        assertEquals("Updated Title", accommodation.getTitle());
        assertEquals(Accommodation.AccommodationType.HOUSE, accommodation.getType());
        assertEquals(new BigDecimal("20000"), accommodation.getPrice());
        assertEquals(3, accommodation.getBedrooms());
        assertEquals(2, accommodation.getBathrooms());
        assertFalse(accommodation.isInternetIncluded());
        assertFalse(accommodation.isUtilitiesIncluded());
        assertFalse(accommodation.isParkingAvailable());
        assertFalse(accommodation.isNsfasAccredited());
    }

    @Test
    public void testImageUrls() {
        List<String> imageUrls = Arrays.asList(
                "https://example.com/image1.jpg",
                "https://example.com/image2.jpg"
        );
        accommodation.setImageUrls(imageUrls);
        assertEquals(imageUrls, accommodation.getImageUrls());
    }

    @Test
    public void testStatus() {
        accommodation.setStatus(Accommodation.AccommodationStatus.INACTIVE);
        assertEquals(Accommodation.AccommodationStatus.INACTIVE, accommodation.getStatus());
    }

    @Test
    public void testEqualsAndHashCode() {
        Accommodation accommodation2 = new Accommodation(
                "Another Apartment",
                "Another nice apartment",
                Accommodation.AccommodationType.APARTMENT,
                "456 Test Street",
                "Test City",
                "12345",
                -26.2041,
                28.0473,
                new BigDecimal("15000"),
                Accommodation.PriceFrequency.PER_MONTH,
                2,
                1,
                4,
                true,
                true,
                true,
                "12-month lease",
                LocalDateTime.now(),
                LocalDateTime.now().plusYears(1),
                true,
                testUser
        );

        // Different accommodations should not be equal
        assertNotEquals(accommodation, accommodation2);

        // Set same ID to make them equal
        accommodation.setId(1L);
        accommodation2.setId(1L);
        assertEquals(accommodation, accommodation2);
        assertEquals(accommodation.hashCode(), accommodation2.hashCode());
    }

    @Test
    public void testToString() {
        String toString = accommodation.toString();
        assertTrue(toString.contains("Test Apartment"));
        assertTrue(toString.contains("APARTMENT"));
        assertTrue(toString.contains("15000"));
        assertTrue(toString.contains("PER_MONTH"));
        assertTrue(toString.contains("2")); // bedrooms
        assertTrue(toString.contains("1")); // bathrooms
        assertTrue(toString.contains("ACTIVE"));
        assertTrue(toString.contains("testUser"));
    }
}
