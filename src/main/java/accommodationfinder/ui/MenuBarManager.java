package accommodationfinder.ui;

import accommodationfinder.auth.User;

import javax.swing.*;

public class MenuBarManager {
    private final MainWindow mainWindow;

    private JMenuItem logoutMenuItem;
    private JMenuItem profileMenuItem;
    private JMenuItem logInMenuItem;
    private JMenuItem signUpMenuItem;

    public MenuBarManager(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");
        JMenu accountMenu = new JMenu("Account");
        JMenu helpMenu = new JMenu("Help");

        // File Menu
        logoutMenuItem = new JMenuItem("Log Out");
        logoutMenuItem.addActionListener(e -> mainWindow.handleLogout());
        fileMenu.add(logoutMenuItem);

        fileMenu.addSeparator();

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);

        // View menu
        JMenuItem refreshMenuItem = new JMenuItem("Refresh Listings");
        refreshMenuItem.addActionListener(e -> mainWindow.refreshMainViewListings());
        viewMenu.add(refreshMenuItem);

        JMenuItem clearFiltersMenuItem = new JMenuItem("Clear Filters");
        clearFiltersMenuItem.addActionListener(e -> mainWindow.clearMainViewFilters());
        viewMenu.add(clearFiltersMenuItem);

        viewMenu.addSeparator();

        JMenuItem goToMainViewItem = new JMenuItem("Go to Home Screen");
        goToMainViewItem.addActionListener(e -> mainWindow.showMainApplicationView());
        viewMenu.add(goToMainViewItem);

        // Account Menu
        logInMenuItem = new JMenuItem("Log In...");
        logInMenuItem.addActionListener(e -> mainWindow.switchToLoginPanel());
        accountMenu.add(logInMenuItem);

        signUpMenuItem = new JMenuItem("Sign Up...");
        signUpMenuItem.addActionListener(e -> mainWindow.switchToRegistrationPanel());
        accountMenu.add(signUpMenuItem);

        accountMenu.addSeparator();

        profileMenuItem = new JMenuItem("My Profile");
        profileMenuItem.addActionListener(e -> mainWindow.showUserProfileDialog());
        accountMenu.add(profileMenuItem);

        // Help Menu
        JMenuItem aboutItem = new JMenuItem("About ResFinder");
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
}
