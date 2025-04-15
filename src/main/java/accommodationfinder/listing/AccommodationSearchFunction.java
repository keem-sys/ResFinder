package accommodationfinder.listing;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class AccommodationSearchFunction {

    public List <Accommodation> searchFilterSort (List<Accommodation> allListings, String keyword, Accommodation.AccommodationType typeFilter, String cityFilter, BigDecimal minPrice, BigDecimal maxPrice, boolean sortByPriceAsc) {
        return allListings.stream()
                .filter(a -> keyword == null || keyword.isEmpty() ||
                        a.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        a.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
                        a.getCity().toLowerCase().contains(keyword.toLowerCase()) ||
                        a.getAddress().toLowerCase().contains(keyword.toLowerCase()))

                .filter(a -> typeFilter == null || a.getType() == typeFilter)

                .filter(a -> cityFilter == null || a.getCity().equalsIgnoreCase(cityFilter))

                .filter(a -> (minPrice == null || a.getPrice().compareTo(minPrice) >= 0) &&
                        (maxPrice == null || a.getPrice().compareTo(maxPrice) <= 0))

                .sorted((a1, a2) -> sortByPriceAsc
                        ? a1.getPrice().compareTo(a2.getPrice())
                        : a2.getPrice().compareTo(a1.getPrice()))

                .collect(Collectors.toList());
    }
}
