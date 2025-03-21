package accommodationfinder.ui;

import accommodationfinder.auth.UserService;
import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;

public class LoginPanel extends JPanel {
    private JLabel fullNameLbl, usernameLbl, emailLbl, passwordLbl, confirmPasswordLbl, errorMsgLbl, titleLabel;
    private JTextField fullNameField, usernameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, cancelButton;

    private final UserService userService;

    LoginPanel(UserService userService) {
        this.userService = userService;

        FormLayout layout = new FormLayout(
                "right:pref, 3dlu, 150dlu",
                "p, 7dlu, p, 7dlu, p"
        );

    }


}
