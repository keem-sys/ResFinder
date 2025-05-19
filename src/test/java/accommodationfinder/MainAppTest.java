package accommodationfinder;

import accommodationfinder.ui.MainWindow;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.assertj.swing.timing.Timeout.timeout;
import static java.util.concurrent.TimeUnit.SECONDS;

public class MainAppTest {

    private Robot robot;
    private FrameFixture window;

    @BeforeEach
    void setUp() {
        robot = BasicRobot.robotWithCurrentAwtHierarchy();
        // Run the MainApp in the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> MainApp.main(new String[]{}));
        // Find the MainWindow by its title with a timeout
        window = WindowFinder.findFrame(MainWindow.class).withTimeout(timeout(5, SECONDS).duration()).using(robot);
    }

    @AfterEach
    void tearDown() {
        if (window != null) {
            window.close();
            window.cleanUp();
        }
        if (robot != null) {
            robot.cleanUp();
        }
    }

    @Test
    void testMainWindowIsVisible() {
        window.requireVisible();
    }

    @Test
    void testMainWindowHasCorrectTitle() {
        // Assuming your MainWindow has a specific title, replace "Student Accommodation Finder"
        // with the actual title. If it doesn't have a title, you can remove this test.
        window.requireTitle("Student Accommodation Finder");
    }

    // You can add more tests here to interact with components within the MainWindow
    // once you have identified and named them.
}