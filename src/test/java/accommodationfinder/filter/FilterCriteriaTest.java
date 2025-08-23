package accommodationfinder.filter;

import accommodationfinder.listing.Accommodation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the FilterCriteria class.
 * This class focuses on testing the logic of setting criteria and determining
 * if any filters are active.
 */
class FilterCriteriaTest {

    private FilterCriteria criteria;


    @BeforeEach
    void setUp() {
        criteria = new FilterCriteria();
    }

    @Test
    @DisplayName("A newly created FilterCriteria should have no active filters")
    void shouldNotHaveActiveFiltersWhenCreated() {
        // Act: Check the initial state of a new object
        boolean result = criteria.hasActiveFilters();

        // Assert: Verify that the result is false
        assertFalse(result, "A new FilterCriteria object should not report having active filters.");
    }

    @Test
    @DisplayName("Should have active filters after setting a max price")
    void shouldHaveActiveFiltersWhenMaxPriceIsSet() {
        // Arrange: Set a filter value on the criteria object
        criteria.setMaxPrice(new BigDecimal("5000.00"));

        // Act: Call the method being tested
        boolean result = criteria.hasActiveFilters();

        // Assert: Verify that the result is now true
        assertTrue(result, "hasActiveFilters() should return true after a max price is set.");
    }

    @Test
    @DisplayName("Should not have active filters after reset is called")
    void shouldNotHaveActiveFiltersAfterReset() {
        // Arrange: First, make a filter active
        criteria.setMinPrice(new BigDecimal("1000.00"));
        assertTrue(criteria.hasActiveFilters(), "Pre-condition failed: Filter should be active before reset.");

        // Act: Call the reset method
        criteria.reset();

        // Assert: Verify that filters are no longer active
        assertFalse(criteria.hasActiveFilters(), "hasActiveFilters() should return false after reset() is called.");
    }


    @Test
    @DisplayName("Should have active filters when a city is set")
    void shouldHaveActiveFiltersWhenCityIsSet() {
        // Arrange
        criteria.setCity("Cape Town");

        // Act & Assert
        assertTrue(criteria.hasActiveFilters(), "hasActiveFilters() should be true when a city is set.");
    }

    @Test
    @DisplayName("Should NOT have active filters if city is set to 'All Suburbs'")
    void shouldNotHaveActiveFiltersIfCityIsAllCities() {
        // Arrange: The setCity method should treat "All Cities" as null (no filter)
        criteria.setCity("All suburbs");

        // Act & Assert
        assertFalse(criteria.hasActiveFilters(), "hasActiveFilters() should be false if city is 'All Suburbs'.");
    }

    @Test
    @DisplayName("Should have active filters when a bedroom count > 0 is set")
    void shouldHaveActiveFiltersWhenBedroomsSet() {
        // Arrange
        criteria.setBedrooms(2);

        // Act & Assert
        assertTrue(criteria.hasActiveFilters(), "hasActiveFilters() should be true when bedrooms are set to > 0.");
    }

    @Test
    @DisplayName("Should NOT have active filters when a bedroom count is 0")
    void shouldNotHaveActiveFiltersWhenBedroomsSetToZero() {
        // Arrange
        criteria.setBedrooms(0);

        // Act & Assert
        assertFalse(criteria.hasActiveFilters(), "hasActiveFilters() should be false when bedrooms are set to 0.");
    }

    @Test
    @DisplayName("Should have active filters when an accommodation type is selected")
    void shouldHaveActiveFiltersWhenTypeIsSelected() {
        // Arrange
        Set<Accommodation.AccommodationType> types = new HashSet<>();
        types.add(Accommodation.AccommodationType.APARTMENT);
        criteria.setSelectedTypes(types);

        // Act & Assert
        assertTrue(criteria.hasActiveFilters(), "hasActiveFilters() should be true when accommodation types are selected.");
    }

    @Test
    @DisplayName("Should have active filters when a boolean flag is set")
    void shouldHaveActiveFiltersWhenBooleanFlagSet() {
        // Arrange
        criteria.setNsfasAccredited(true);

        // Act & Assert
        assertTrue(criteria.hasActiveFilters(), "hasActiveFilters() should be true when a boolean flag is set.");
    }
}