package accommodationfinder.filter;

import accommodationfinder.listing.Accommodation;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class FilterCriteria {
    private Set<Accommodation.AccommodationType> selectedTypes;
    private BigDecimal minPrice, maxPrice;
    private Integer bathrooms, bedrooms;
    private String city;
    private Boolean utilitiesIncluded;
    private Boolean nsfasAccredited;

    public FilterCriteria() {
        this.selectedTypes = new HashSet<>();
    }

    public Set<Accommodation.AccommodationType> getSelectedTypes() {
        return selectedTypes;
    }

    public void setSelectedTypes(Set<Accommodation.AccommodationType> selectedTypes) {
        this.selectedTypes = selectedTypes;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (city == null || city.trim().isEmpty() || "All Suburbs".equalsIgnoreCase(city.trim())) {
            this.city = null;
        } else {
            this.city = city.trim();
        }
    }

    public Boolean getUtilitiesIncluded() {
        return utilitiesIncluded;
    }

    public void setUtilitiesIncluded(Boolean utilitiesIncluded) {
        this.utilitiesIncluded = utilitiesIncluded;
    }

    public Boolean getNsfasAccredited() {
        return nsfasAccredited;
    }

    public void setNsfasAccredited(Boolean nsfasAccredited) {
        this.nsfasAccredited = nsfasAccredited;
    }

    public boolean hasActiveFilters() {
        return (selectedTypes != null && !selectedTypes.isEmpty()) ||
                minPrice != null || maxPrice != null ||
                (bedrooms != null && bedrooms > 0) ||
                (bathrooms != null && bathrooms > 0) ||
                (city != null) ||
                utilitiesIncluded != null || nsfasAccredited != null;
    }

    public void reset() {
        selectedTypes.clear();
        minPrice = null;
        maxPrice = null;
        bedrooms = 0;
        bathrooms = 0;
        city = null;
        utilitiesIncluded = null;
        nsfasAccredited = null;
    }
}



