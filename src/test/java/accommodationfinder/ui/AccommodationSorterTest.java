package accommodationfinder.ui;

import accommodationfinder.listing.Accommodation;
import accommodationfinder.listing.Accommodation.AccommodationStatus;
import accommodationfinder.listing.Accommodation.AccommodationType;
import accommodationfinder.listing.Accommodation.PriceFrequency;
import accommodationfinder.ui.MainApplicationPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccommodationSorterTest {

    private List<Accommodation> testAccommodations;

    @BeforeEach
    void setUp() {
        // Create test accommodations with different properties
        testAccommodations = new ArrayList<>();

        // Accommodation 1 - Newest, highest price
        testAccommodations.add(new Accommodation(
            "Apartment A",
            "Description",
            AccommodationType.APARTMENT,
            "Address",
            "City",
            "12345",
            0.0,
            0.0,
            new BigDecimal("20000"),
            PriceFrequency.PER_MONTH,
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
            null
        ));

        // Accommodation 2 - Middle price, middle date
        testAccommodations.add(new Accommodation(
            "House B",
            "Description",
            AccommodationType.HOUSE,
            "Address",
            "City",
            "12345",
            0.0,
            0.0,
            new BigDecimal("15000"),
            PriceFrequency.PER_MONTH,
            3,
            2,
            4,
            true,
            true,
            true,
            "12-month lease",
            LocalDateTime.now().minusDays(30),
            LocalDateTime.now().plusYears(1),
            true,
            null
        ));

        // Accommodation 3 - Oldest, lowest price
        testAccommodations.add(new Accommodation(
            "Flat C",
            "Description",
            AccommodationType.FLAT,
            "Address",
            "City",
            "12345",
            0.0,
            0.0,
            new BigDecimal("10000"),
            PriceFrequency.PER_MONTH,
            1,
            1,
            4,
            true,
            true,
            true,
            "12-month lease",
            LocalDateTime.now().minusDays(60),
            LocalDateTime.now().plusYears(1),
            true,
            null
        ));
    }

    @Test
    void testNullInput() {
        // Test with null input
        List<Accommodation> result = AccommodationSorter.sort(null, MainApplicationPanel.ORDER_BY_DEFAULT);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testPriceAscendingSort() {
        List<Accommodation> result = AccommodationSorter.sort(testAccommodations, MainApplicationPanel.ORDER_BY_PRICE_ASC);
        
        // Verify price order: 10000, 15000, 20000
        assertEquals(new BigDecimal("10000"), result.get(0).getPrice());
        assertEquals(new BigDecimal("15000"), result.get(1).getPrice());
        assertEquals(new BigDecimal("20000"), result.get(2).getPrice());
    }

    @Test
    void testPriceDescendingSort() {
        List<Accommodation> result = AccommodationSorter.sort(testAccommodations, MainApplicationPanel.ORDER_BY_PRICE_DESC);
        
        // Verify price order: 20000, 15000, 10000
        assertEquals(new BigDecimal("20000"), result.get(0).getPrice());
        assertEquals(new BigDecimal("15000"), result.get(1).getPrice());
        assertEquals(new BigDecimal("10000"), result.get(2).getPrice());
    }

    @Test
    void testDefaultSort() {
        List<Accommodation> result = AccommodationSorter.sort(testAccommodations, MainApplicationPanel.ORDER_BY_DEFAULT);
        
        // Verify date order: newest first
        assertTrue(result.get(0).getListingDate().isAfter(result.get(1).getListingDate()));
        assertTrue(result.get(1).getListingDate().isAfter(result.get(2).getListingDate()));
    }

    @Test
    void testDateOldestSort() {
        List<Accommodation> result = AccommodationSorter.sort(testAccommodations, MainApplicationPanel.ORDER_BY_DATE_OLDEST);
        
        // Verify date order: oldest first
        assertTrue(result.get(0).getListingDate().isBefore(result.get(1).getListingDate()));
        assertTrue(result.get(1).getListingDate().isBefore(result.get(2).getListingDate()));
    }

    @Test
    void testNullsLast() {
        // Add accommodation with null price
        testAccommodations.add(new Accommodation(
            "Null Price",
            "Description",
            AccommodationType.APARTMENT,
            "Address",
            "City",
            "12345",
            0.0,
            0.0,
            null,
            PriceFrequency.PER_MONTH,
            2,
            1,
            4,
            true,
            true,
            true,
            "12-month lease",
            LocalDateTime.now().minusDays(15),
            LocalDateTime.now().plusYears(1),
            true,
            null
        ));

        List<Accommodation> result = AccommodationSorter.sort(testAccommodations, MainApplicationPanel.ORDER_BY_PRICE_ASC);
        
        // Verify null is last in sorted list
        assertNull(result.get(result.size() - 1).getPrice());
    }

    @Test
    void testUnknownSortCriterion() {
        // Test with unknown sort criterion
        List<Accommodation> result = AccommodationSorter.sort(testAccommodations, "UNKNOWN_CRITERION");
        
        // Should return original order
        assertEquals(testAccommodations, result);
    }
}
