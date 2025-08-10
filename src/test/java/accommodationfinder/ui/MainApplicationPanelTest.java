package accommodationfinder.ui;

import accommodationfinder.listing.Accommodation;
import accommodationfinder.service.AccommodationService;
import accommodationfinder.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MainApplicationPanel class.
 * This test suite focuses specifically on the client-side data processing methods
 * like performSearch().
 */
@ExtendWith(MockitoExtension.class) // Enables Mockito annotations
class MainApplicationPanelTest {

    // Mocked Dependencies
    @Mock
    private AccommodationService mockAccommodationService;
    @Mock
    private UserService mockUserService;
    @Mock
    private MainWindow mockMainWindow;

    private MainApplicationPanel panel;
    private List<Accommodation> sampleListings;

    @BeforeEach
    void setUp() {
        panel = new MainApplicationPanel(mockAccommodationService, mockUserService, mockMainWindow);
        sampleListings = new ArrayList<>();

        Accommodation acc1 = new Accommodation();
        acc1.setTitle("Shared Room Downtown");
        acc1.setDescription("A nice and affordable shared space.");
        acc1.setCity("Cape Town");
        sampleListings.add(acc1);

        Accommodation acc2 = new Accommodation();
        acc2.setTitle("Quiet Studio Apartment");
        acc2.setDescription("Perfect for a single student near campus.");
        acc2.setAddress("123 Main Road, Rondebosch");
        acc2.setCity("Cape Town");
        sampleListings.add(acc2);

        Accommodation acc3 = new Accommodation();
        acc3.setTitle("Luxury Loft");
        acc3.setDescription("A modern space with a great view.");
        acc3.setCity("Johannesburg");
        sampleListings.add(acc3);
    }

    @Test
    @DisplayName("Should return one result when searching for a keyword in a title")
    void performSearch_WhenKeywordInTitle_ShouldReturnMatchingItem() {
        // Act: Search for the keyword "shared"
        List<Accommodation> results = panel.performSearch(sampleListings, "shared");

        // Assert: Verify that only one item was returned and it's the correct one
        assertEquals(1, results.size(), "The result list should contain exactly one item.");
        assertEquals("Shared Room Downtown", results.getFirst().getTitle(), "The returned item " +
                "should be the 'Shared Room'.");
    }

    @Test
    @DisplayName("Should return one result when searching for a keyword in a description")
    void performSearch_WhenKeywordInDescription_ShouldReturnMatchingItem() {
        // Act: Search for the keyword "campus"
        List<Accommodation> results = panel.performSearch(sampleListings, "campus");

        // Assert
        assertEquals(1, results.size());
        assertEquals("Quiet Studio Apartment", results.getFirst().getTitle());
    }

    @Test
    @DisplayName("Should return multiple results when keyword matches multiple items")
    void performSearch_WhenKeywordMatchesMultiple_ShouldReturnAllMatches() {
        // Act: Search for "Cape Town", which is in two listings
        List<Accommodation> results = panel.performSearch(sampleListings, "Cape Town");

        // Assert
        assertEquals(2, results.size(), "Should find two accommodations in Cape Town.");
    }

    @Test
    @DisplayName("Should be case-insensitive when searching")
    void performSearch_ShouldBeCaseInsensitive() {
        // Act: Search for "downtown" in all lowercase
        List<Accommodation> results = panel.performSearch(sampleListings, "downtown");

        // Assert
        assertEquals(1, results.size());
        assertEquals("Shared Room Downtown", results.getFirst().getTitle());
    }

    @Test
    @DisplayName("Should return correct result for multi-word search (AND logic)")
    void performSearch_WhenMultiWordSearch_ShouldUseAndLogic() {
        // Act: Search for two words that are in the same item
        List<Accommodation> results = panel.performSearch(sampleListings, "quiet student");

        // Assert
        assertEquals(1, results.size(), "Should find the one studio that is for a student.");
        assertEquals("Quiet Studio Apartment", results.getFirst().getTitle());
    }

    @Test
    @DisplayName("Should return zero results if not all keywords match a single item")
    void performSearch_WhenNotAllKeywordsMatch_ShouldReturnEmptyList() {
        // Act: Search for words that exist, but in different items
        List<Accommodation> results = panel.performSearch(sampleListings, "shared loft");

        // Assert
        assertTrue(results.isEmpty(), "Should return an empty list as no item contains both 'shared' and 'loft'.");
    }

    @Test
    @DisplayName("Should return all items when the search keyword is empty or whitespace")
    void performSearch_WhenKeywordIsEmpty_ShouldReturnAllItems() {
        // Act
        List<Accommodation> resultsFromEmpty = panel.performSearch(sampleListings, "");
        List<Accommodation> resultsFromWhitespace = panel.performSearch(sampleListings, "   ");

        // Assert
        assertEquals(sampleListings.size(), resultsFromEmpty.size(), "An empty search should return all items.");
        assertEquals(sampleListings.size(), resultsFromWhitespace.size(), "A whitespace search should return all items.");
    }

    @Test
    @DisplayName("Should return an empty list when no accommodations match the keyword")
    void performSearch_WhenNoMatches_ShouldReturnEmptyList() {
        // Act
        List<Accommodation> results = panel.performSearch(sampleListings, "nonexistentkeyword");

        // Assert
        assertNotNull(results, "The result should not be null.");
        assertTrue(results.isEmpty(), "The result list should be empty for a keyword that doesn't match anything.");
    }
}