package accommodationfinder.ui;

import accommodationfinder.listing.Accommodation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Provides static methods for sorting lists of Accommodation objects based on various criteria.
 */
public class AccommodationSorter {

    /**
     * Sorts a list of Accommodation objects based on the provided criteria string.
     * Returns a NEW sorted list, leaving the original list unmodified.
     *
     * @param originalList    The list of accommodations to sort.
     * @param sortByCriterion A string indicating the sort order (e.g., values from MainApplicationPanel.ORDER_BY_* constants).
     * @return A new list containing the sorted accommodations.
     */
    public static List<Accommodation> sort(List<Accommodation> originalList, String sortByCriterion) {
        if (originalList == null) {
            return new ArrayList<>(); // Return empty list if input is null
        }

        // Create a mutable copy to sort
        List<Accommodation> listToSort = new ArrayList<>(originalList);
        Comparator<Accommodation> comparator = null;

        // Determine the comparator based on the criterion string
        switch (sortByCriterion) {
            case MainApplicationPanel.ORDER_BY_PRICE_ASC:
                // Compare by price ascending, nulls last
                System.out.println("Sorter: Sorting by price ascending, nulls last");
                comparator = Comparator.comparing(Accommodation::getPrice,
                        Comparator.nullsLast(Comparator.naturalOrder()));
                break;

            case MainApplicationPanel.ORDER_BY_PRICE_DESC:
                // Compare by price descending, nulls last
                System.out.println("Sorter: Sorting by price descending, nulls last");
                comparator = Comparator.comparing(Accommodation::getPrice,
                        Comparator.nullsLast(Comparator.reverseOrder()));
                break;

            case MainApplicationPanel.ORDER_BY_DEFAULT: // Assumes Default is Newest First
                // Compare by date added newest, nulls last
                System.out.println("Sorter: Sorting by default (newest date), nulls last");
                comparator = Comparator.comparing(Accommodation::getListingDate, // Ensure this method exists in Accommodation
                        Comparator.nullsLast(Comparator.reverseOrder()));
                break;

            case MainApplicationPanel.ORDER_BY_DATE_OLDEST:
                // Compare by date added oldest, nulls last
                System.out.println("Sorter: Sorting by oldest date, nulls last");
                comparator = Comparator.comparing(Accommodation::getListingDate, // Ensure this method exists in Accommodation
                        Comparator.nullsLast(Comparator.naturalOrder()));
                break;

            default:
                // If criterion is unknown, don't sort (return the copy as is)
                System.out.println("Sorter: Unknown sort criterion '" + sortByCriterion + "', returning original order.");
                // Or perhaps default to a specific sort like 'newest'? For now, no sort.
                break;
        }

        // Apply the sorting if a valid comparator was found
        if (comparator != null) {
            listToSort.sort(comparator);
        }

        return listToSort;
    }
}