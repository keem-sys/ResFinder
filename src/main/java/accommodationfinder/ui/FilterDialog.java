package accommodationfinder.ui;


import accommodationfinder.filter.FilterCriteria;
import accommodationfinder.listing.Accommodation;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FilterDialog extends JDialog {
    private final FilterCriteria currentFilterCriteria;
    private boolean filtersApplied;

    private JPanel typeCheckboxesPanel; // Panel to hold AccommodationType checkboxes
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
        populateFieldsFromCriteria(initialCriteria);
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

    }


    private Border createTitledSectionBorder(String accommodationType) {
        return null;
    }

}
