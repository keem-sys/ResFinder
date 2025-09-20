package accommodationfinder.ui;

import accommodationfinder.auth.User;

import javax.swing.*;
import java.awt.*;

public class MenuBarManager {
    private final MainWindow mainWindow;

    private JMenuItem logoutMenuItem;
    private JMenuItem profileMenuItem;
    private JMenuItem logInMenuItem;
    private JMenuItem signUpMenuItem;
    private JMenuItem savedListingsMenuItem;

    public MenuBarManager(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        styleMenu(fileMenu);
        JMenu viewMenu = new JMenu("View");
        styleMenu(viewMenu);
        JMenu accountMenu = new JMenu("Account");
        styleMenu(accountMenu);
        JMenu helpMenu = new JMenu("Help");
        styleMenu(helpMenu);

        // File Menu
        logoutMenuItem = new JMenuItem("Log Out");
        styleMenuItem(logoutMenuItem);
        logoutMenuItem.addActionListener(e -> mainWindow.handleLogout());
        fileMenu.add(logoutMenuItem);

        fileMenu.addSeparator();

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        styleMenuItem(exitMenuItem);
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);

        // View menu
        JMenuItem refreshMenuItem = new JMenuItem("Refresh Listings");
        styleMenuItem(refreshMenuItem);
        refreshMenuItem.addActionListener(e -> mainWindow.refreshMainViewListings());
        viewMenu.add(refreshMenuItem);

        JMenuItem clearFiltersMenuItem = new JMenuItem("Clear Filters");
        styleMenuItem(clearFiltersMenuItem);
        clearFiltersMenuItem.addActionListener(e -> mainWindow.clearMainViewFilters());
        viewMenu.add(clearFiltersMenuItem);

        viewMenu.addSeparator();

        JMenuItem goToMainViewItem = new JMenuItem("Go to Home Screen");
        styleMenuItem(goToMainViewItem);
        goToMainViewItem.addActionListener(e -> mainWindow.showMainApplicationView());
        viewMenu.add(goToMainViewItem);

        // Account Menu
        logInMenuItem = new JMenuItem("Log In");
        styleMenuItem(logInMenuItem);
        logInMenuItem.addActionListener(e -> mainWindow.switchToLoginPanel());
        accountMenu.add(logInMenuItem);

        signUpMenuItem = new JMenuItem("Sign Up");
        styleMenuItem(signUpMenuItem);
        signUpMenuItem.addActionListener(e -> mainWindow.switchToRegistrationPanel());
        accountMenu.add(signUpMenuItem);

        accountMenu.addSeparator();
        profileMenuItem = new JMenuItem("My Profile");
        styleMenuItem(profileMenuItem);
        profileMenuItem.addActionListener(e -> mainWindow.showUserProfileDialog());
        accountMenu.add(profileMenuItem);

        savedListingsMenuItem = new JMenuItem("My Saved Listings");
        styleMenuItem(savedListingsMenuItem);
        savedListingsMenuItem.addActionListener(e -> mainWindow.showSavedListings());
        accountMenu.add(savedListingsMenuItem);

        // Help Menu
        JMenuItem contactMenuItem = new JMenuItem("Contact Us");
        styleMenuItem(contactMenuItem);
        contactMenuItem.addActionListener(e -> mainWindow.switchToContactPanel());
        helpMenu.add(contactMenuItem);

        JMenuItem faqMenuItem = new JMenuItem("View FAQ");
        styleMenuItem(faqMenuItem);
        faqMenuItem.addActionListener(e -> mainWindow.switchToFaqPanel());
        helpMenu.add(faqMenuItem);

        helpMenu.addSeparator();

        JMenuItem aboutItem = new JMenuItem("About ResFinder");
        styleMenuItem(aboutItem);
        aboutItem.addActionListener(e -> mainWindow.showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(accountMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    /**
     * Updates the enabled/disabled state of menu items based on the user's login status.
     * called whenever the user logs in or out.
     * @param currentUser The currently logged-in user, or null if logged out.
     */
    public void updateMenuState(User currentUser) {
        boolean isLoggedIn = (currentUser != null);

        // When user is logged in
        logoutMenuItem.setEnabled(isLoggedIn);
        profileMenuItem.setEnabled(isLoggedIn);

        // When user not logged in
        logInMenuItem.setEnabled(!isLoggedIn);
        signUpMenuItem.setEnabled(!isLoggedIn);
    }
    private void styleMenu(JMenu menu) {
        menu.setFont(new Font("SansSerif", Font.PLAIN, 13));
        menu.setMargin(new Insets(5, 8, 3, 11));
    }

    private void styleMenuItem(JMenuItem menuItem) {
        menuItem.setFont(new Font("SansSerif", Font.PLAIN, 12));
        menuItem.setMargin(new Insets(5, 5, 5, 5));
    }
}