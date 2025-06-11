package accommodationfinder.ui;


import accommodationfinder.filter.FilterCriteria;
import accommodationfinder.listing.Accommodation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class FilterDialog extends JDialog {
    private final FilterCriteria currentFilterCriteria;
    private boolean filtersApplied = false;

    private JPanel typeCheckboxesPanel;
    private List<JCheckBox> typeCheckBoxes;

    private JTextField minPriceField;
    private JTextField maxPriceField;
    private JSpinner bedroomsSpinner;
    private JSpinner bathroomsSpinner;
    private JComboBox<String> cityComboBox;
    private JCheckBox utilitiesIncludedCheckBox;
    private JCheckBox nsfasAccreditedCheckBox;

    private JButton applyButton;
    private JButton clearButton;
    private JButton cancelButton;

    private static final Color DIALOG_BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color SECTION_BORDER_COLOR = Color.LIGHT_GRAY;
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FIELD_FONT = new Font("SansSerif", Font.PLAIN, 13);

    public FilterDialog(Frame owner, FilterCriteria initialFilterCriteria, List<Accommodation> allListings) {
        super(owner, "Filter Accommodations", true);
        this.currentFilterCriteria = new FilterCriteria();

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(DIALOG_BACKGROUND_COLOR);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents(allListings);
        populateFieldsFromCriteria(initialFilterCriteria);
        addListeners();

        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents(List<Accommodation> allListings) {
        JPanel mainFilterPanel = new JPanel();
        mainFilterPanel.setLayout(new BoxLayout(mainFilterPanel, BoxLayout.Y_AXIS));
        mainFilterPanel.setOpaque(false);


        // Accommodation Type Section
        typeCheckBoxes = new ArrayList<>();
        typeCheckboxesPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        typeCheckboxesPanel.setOpaque(false);
        typeCheckboxesPanel.setBorder(createTitledSectionBorder("Accommodation Type"));

        for (Accommodation.AccommodationType type : Accommodation.AccommodationType.values()) {
            JCheckBox checkBox = new JCheckBox(formatTypeEnum(type.name()));
            checkBox.setFont(FIELD_FONT);
            checkBox.setOpaque(false);
            typeCheckBoxes.add(checkBox);
            typeCheckboxesPanel.add(checkBox);
        }
        mainFilterPanel.add(typeCheckboxesPanel);
        mainFilterPanel.add(Box.createVerticalStrut(15));

        // Price Range Section
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pricePanel.setOpaque(false);
        pricePanel.setBorder(createTitledSectionBorder("Price Range (ZAR)"));
        pricePanel.add(new JLabel("Min:"));
        minPriceField = new JTextField(8);
        minPriceField.setFont(FIELD_FONT);
        pricePanel.add(minPriceField);
        pricePanel.add(Box.createHorizontalStrut(10));
        pricePanel.add(new JLabel("Max:"));
        maxPriceField = new JTextField(8);
        maxPriceField.setFont(FIELD_FONT);
        pricePanel.add(maxPriceField);
        mainFilterPanel.add(pricePanel);
        mainFilterPanel.add(Box.createVerticalStrut(15));

        // Rooms Section
        JPanel roomsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        roomsPanel.setOpaque(false);
        roomsPanel.setBorder(createTitledSectionBorder("Rooms"));
        roomsPanel.add(new JLabel("Bedrooms (min): "));
        bedroomsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 8, 1));
        bedroomsSpinner.setFont(FIELD_FONT);
        roomsPanel.add(bedroomsSpinner);
        roomsPanel.add(Box.createHorizontalStrut(10));

        roomsPanel.add(new JLabel("Bathrooms (min): "));
        bathroomsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
        bathroomsSpinner.setFont(FIELD_FONT);
        roomsPanel.add(bathroomsSpinner);
        mainFilterPanel.add(roomsPanel);
        mainFilterPanel.add(Box.createVerticalStrut(15));

        // City Section
        JPanel cityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        cityPanel.setOpaque(false);
        cityPanel.setBorder(createTitledSectionBorder("Location"));
        cityPanel.add(new JLabel("City:"));
        Set<String> distinctCities = allListings.stream()
                .map(Accommodation::getCity)
                .filter(city -> city != null && !city.trim().isEmpty())
                .collect(Collectors.toSet());
        DefaultComboBoxModel<String> cityModel = new DefaultComboBoxModel<>();
        cityModel.addElement("All cities");
        distinctCities.stream().sorted().forEach(cityModel::addElement);
        cityComboBox = new JComboBox<>(cityModel);
        cityComboBox.setFont(FIELD_FONT);
        cityPanel.add(cityComboBox);
        mainFilterPanel.add(cityPanel);
        mainFilterPanel.add(Box.createVerticalStrut(15));

        // Features Section
        JPanel featuresPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        featuresPanel.setOpaque(false);
        featuresPanel.setBorder(createTitledSectionBorder("Features"));
        utilitiesIncludedCheckBox = new JCheckBox("Utilities Included");
        utilitiesIncludedCheckBox.setFont(FIELD_FONT);
        utilitiesIncludedCheckBox.setOpaque(false);
        featuresPanel.add(utilitiesIncludedCheckBox);
        nsfasAccreditedCheckBox = new JCheckBox("NSFAS Accredited");
        nsfasAccreditedCheckBox.setFont(FIELD_FONT);
        nsfasAccreditedCheckBox.setOpaque(false);
        featuresPanel.add(nsfasAccreditedCheckBox);
        mainFilterPanel.add(featuresPanel);
        mainFilterPanel.add(Box.createVerticalStrut(20));


        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        applyButton = new JButton("Apply Filters");
        clearButton = new JButton("Clear Filters");
        cancelButton = new JButton("Cancel");

        buttonPanel.add(clearButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(applyButton);

        getContentPane().add(mainFilterPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

    }


    private TitledBorder createTitledSectionBorder(String title) {
        TitledBorder titledBorder  = BorderFactory.createTitledBorder(BorderFactory.createLineBorder
                (SECTION_BORDER_COLOR), title);
        titledBorder.setTitleFont(LABEL_FONT.deriveFont(Font.BOLD));
        return titledBorder;
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        String[] words = str.split("\\s");
        StringBuilder capitalizedString = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                capitalizedString.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase()).append(" ");
            }
        }
        return capitalizedString.toString().trim();
    }

    private String formatTypeEnum(String enumName) {
        String s = enumName.replace('_', ' ').toLowerCase();
        return  FilterDialog.capitalize(s);
    }

    private void populateFieldsFromCriteria(FilterCriteria filterCriteria) {
        if (filterCriteria == null) return;
        if (filterCriteria.getSelectedTypes() != null) {
            for (JCheckBox checkBox: typeCheckBoxes) {
                Accommodation.AccommodationType type = Accommodation.AccommodationType.valueOf(checkBox.getText()
                        .toUpperCase().replace(' ', '_'));
                checkBox.setSelected(filterCriteria.getSelectedTypes().contains(type));
            }
        }

        minPriceField.setText(filterCriteria.getMinPrice() != null ? filterCriteria.getMinPrice().toPlainString() : "");
        maxPriceField.setText(filterCriteria.getMaxPrice() != null ? filterCriteria.getMaxPrice().toPlainString() : "");
        bedroomsSpinner.setValue(filterCriteria.getBedrooms() != null ? filterCriteria.getBedrooms() : 0);
        bathroomsSpinner.setValue(filterCriteria.getBathrooms() != null ? filterCriteria.getBathrooms() : 0);

        if (filterCriteria.getCity() != null && !filterCriteria.getCity().isEmpty()) {
            cityComboBox.setSelectedItem(filterCriteria.getCity());
        } else {
            cityComboBox.setSelectedItem(0);
        }

        utilitiesIncludedCheckBox.setSelected(filterCriteria.getUtilitiesIncluded() != null &&
                filterCriteria.getUtilitiesIncluded());
        nsfasAccreditedCheckBox.setSelected(filterCriteria.getNsfasAccredited() != null &&
                filterCriteria.getNsfasAccredited());
    }

    private void updateCriteriaFromFields() {
        Set<Accommodation.AccommodationType> selectedTypes = new HashSet<>();
        for (JCheckBox checkBox : typeCheckBoxes) {
            if (checkBox.isSelected()) {
                // Convert checkbox text back to enum value
                String enumName = checkBox.getText().toUpperCase().replace(' ', '_');
                try {
                    selectedTypes.add(Accommodation.AccommodationType.valueOf(enumName));
                } catch (IllegalArgumentException e) {
                    System.err.println("Error parsing type enum from checkbox: " + enumName);
                }
            }
        }
        currentFilterCriteria.setSelectedTypes(selectedTypes.isEmpty() ? null : selectedTypes);

        // Update Price
        try{
            String minText = minPriceField.getText().trim();
            currentFilterCriteria.setMinPrice(minText.isEmpty() ? null : new BigDecimal(minText));
        } catch (NumberFormatException e) {
            currentFilterCriteria.setMinPrice(null);
            JOptionPane.showMessageDialog(this, "Invalid Min Price format.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);

        }

        try {
            String maxText = maxPriceField.getText().trim();
            currentFilterCriteria.setMaxPrice(maxText.isEmpty() ? null : new BigDecimal(maxText));
        } catch (NumberFormatException e) {
            currentFilterCriteria.setMaxPrice(null);
            JOptionPane.showMessageDialog(this, "Invalid Max Price format.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }

        // Update Spinners
        int beds = (Integer) bedroomsSpinner.getValue();
        currentFilterCriteria.setBedrooms(beds > 0 ? beds : null);
        int baths = (Integer) bathroomsSpinner.getValue();
        currentFilterCriteria.setBathrooms(baths > 0 ? baths : null);

        // Update City
        String selectedCity  = (String) cityComboBox.getSelectedItem();
        currentFilterCriteria.setCity("All suburbs".equals(selectedCity) ? null : selectedCity);

        // Update Booleans for Checkboxes
        currentFilterCriteria.setUtilitiesIncluded(utilitiesIncludedCheckBox.isSelected() ? true : null);
        currentFilterCriteria.setNsfasAccredited(nsfasAccreditedCheckBox.isSelected() ? true : null);

    }

    private void addListeners() {
        applyButton.addActionListener(e -> {
            updateCriteriaFromFields();
            this.filtersApplied = true;
            setVisible(false);
            dispose();
        });

        clearButton.addActionListener(e -> {
            currentFilterCriteria.reset();
            populateFieldsFromCriteria(currentFilterCriteria);
        });

        cancelButton.addActionListener(e -> {
            this.filtersApplied = false;
            setVisible(false);
            dispose();
        });
    }

    public boolean wereFiltersApplied() {
        return filtersApplied;
    }

    public FilterCriteria getAppliedCriteria() {
        return currentFilterCriteria;
    }
}
