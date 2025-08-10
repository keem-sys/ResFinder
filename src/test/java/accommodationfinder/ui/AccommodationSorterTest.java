package accommodationfinder.ui;

import accommodationfinder.listing.Accommodation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AccommodationSorter class.
 * These tests verify that the sort() method correctly orders a list of
 * Accommodation objects based on various criteria.
 */
class AccommodationSorterTest {

    private List<Accommodation> unsortedList;
    private Accommodation cheap, medium, expensive, noPrice;

    /**
     * This method runs before each test to set up a consistent, unsorted list of accommodations.
     */
    @BeforeEach
    void setUp() {
        unsortedList = new ArrayList<>();

        // Create accommodation objects with different prices and listing dates
        cheap = new Accommodation();
        cheap.setId(1L);
        cheap.setTitle("Cheap Room");
        cheap.setPrice(new BigDecimal("3000.00"));
        cheap.setListingDate(LocalDateTime.now().minusDays(10)); // Oldest

        medium = new Accommodation();
        medium.setId(2L);
        medium.setTitle("Medium Room");
        medium.setPrice(new BigDecimal("4500.00"));
        medium.setListingDate(LocalDateTime.now().minusDays(5)); // Medium date

        expensive = new Accommodation();
        expensive.setId(3L);
        expensive.setTitle("Expensive Room");
        expensive.setPrice(new BigDecimal("6000.00"));
        expensive.setListingDate(LocalDateTime.now().minusDays(1)); // Newest

        noPrice = new Accommodation();
        noPrice.setId(4L);
        noPrice.setTitle("Free Room (No Price)");
        noPrice.setPrice(null);
        noPrice.setListingDate(LocalDateTime.now().minusDays(2));

        unsortedList.add(medium);
        unsortedList.add(expensive);
        unsortedList.add(noPrice);
        unsortedList.add(cheap);
    }

    @Test
    @DisplayName("Should sort accommodations by price in ascending order")
    void shouldSortByPriceAscending() {
        // Act: Call the sort method with the "Price: Low to High" criterion
        List<Accommodation> sortedList = AccommodationSorter.sort(unsortedList, MainApplicationPanel.ORDER_BY_PRICE_ASC);

        // Assert:
        // Verify the size of the returned list is correct
        assertEquals(4, sortedList.size(), "The sorted list should contain all original items.");

        // Verify the order of elements.
        // Check IDs for precise ordering.
        assertAll("List should be sorted by price ascending",
                () -> assertEquals(cheap.getId(), sortedList.getFirst().getId()),
                () -> assertEquals(medium.getId(), sortedList.get(1).getId()),
                () -> assertEquals(expensive.getId(), sortedList.get(2).getId()),
                () -> assertEquals(noPrice.getId(), sortedList.get(3).getId())
        );
    }

    @Test
    @DisplayName("Should sort accommodations by price in descending order")
    void shouldSortByPriceDescending() {
        // Act: Call the sort method with the "Price: High to Low" criterion
        List<Accommodation> sortedList = AccommodationSorter.sort(unsortedList, MainApplicationPanel.ORDER_BY_PRICE_DESC);

        // Assert:
        // Verify list size
        assertEquals(4, sortedList.size());

        // Verify the descending order
        assertAll("List should be sorted by price descending",
                () -> assertEquals(expensive.getId(), sortedList.getFirst().getId()),
                () -> assertEquals(medium.getId(), sortedList.get(1).getId()),
                () -> assertEquals(cheap.getId(), sortedList.get(2).getId()),
                () -> assertEquals(noPrice.getId(), sortedList.get(3).getId())
        );
    }

    @Test
    @DisplayName("Should sort accommodations by newest listing date (Default)")
    void shouldSortByNewestDateAsDefault() {
        // Act: Call the sort method with the default criterion
        List<Accommodation> sortedList = AccommodationSorter.sort(unsortedList, MainApplicationPanel.ORDER_BY_DEFAULT);

        // Assert: Check the order based on the listing dates we set up
        assertEquals(4, sortedList.size());

        assertAll("List should be sorted by newest date first",
                () -> assertEquals(expensive.getId(), sortedList.get(0).getId()), // Newest
                () -> assertEquals(noPrice.getId(), sortedList.get(1).getId()),
                () -> assertEquals(medium.getId(), sortedList.get(2).getId()),
                () -> assertEquals(cheap.getId(), sortedList.get(3).getId())      // Oldest
        );
    }

    @Test
    @DisplayName("Should not modify the original unsorted list")
    void shouldNotModifyOriginalList() {
        // Arrange: Create a copy of the original list's order before sorting
        List<Long> originalOrderIds = new ArrayList<>();
        for (Accommodation acc : unsortedList) {
            originalOrderIds.add(acc.getId());
        }

        // Act: Call the sort method
        AccommodationSorter.sort(unsortedList, MainApplicationPanel.ORDER_BY_PRICE_ASC);

        // Assert: Verify that the original 'unsortedList' has not changed its order
        assertEquals(medium.getId(), unsortedList.get(0).getId(), "Original list first item should not change.");
        assertEquals(expensive.getId(), unsortedList.get(1).getId(), "Original list second item should not change.");
        assertEquals(noPrice.getId(), unsortedList.get(2).getId(), "Original list third item should not change.");
        assertEquals(cheap.getId(), unsortedList.get(3).getId(), "Original list fourth item should not change.");
    }

    @Test
    @DisplayName("Should return an empty list when given a null list")
    void shouldReturnEmptyListForNullInput() {
        // Act
        List<Accommodation> result = AccommodationSorter.sort(null, MainApplicationPanel.ORDER_BY_DEFAULT);

        // Assert
        assertNotNull(result, "The returned list should not be null.");
        assertTrue(result.isEmpty(), "The returned list should be empty.");
    }
}