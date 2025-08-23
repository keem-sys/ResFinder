package accommodationfinder.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

public class ContactGUI extends JFrame{
    private JLabel lblTitle, lblDescription, lblFirstName, lblLastName, lblEmail, lblMessage, lblCopyRight, lblSubTitle;
    private JTextField txtFirstName, txtLastName, txtEmail;
    private JTextArea txtMessage;
    private JPanel panelMain, panelWest, panelEast,panelSouth;
    private JButton btnSend;

    public ContactGUI() {

        lblFirstName = new JLabel("First Name");
        lblLastName = new JLabel("Last Name");
        lblEmail = new JLabel("Email");

        lblMessage = new JLabel("Message");

        lblTitle = new JLabel("Contact Us");
        lblSubTitle = new JLabel("Reach Out!");
        lblDescription = new JLabel("<html><div style='width:300px; text-align:left;'>"+"We’d love to hear from you! Whether you have questions, feedback, or need support, feel free to reach out using any of the methods below.</div></html>");

        int year = Calendar.getInstance().get(Calendar.YEAR);
        lblCopyRight = new JLabel("© "+year+" ResFinder ");

        lblTitle.setFont(new Font("Roboto", Font.BOLD, 36));
        lblSubTitle.setFont(new Font("Roboto", Font.BOLD, 16));
        lblDescription.setFont(new Font("Roboto", Font.PLAIN,16));
        lblFirstName.setFont(new Font("Roboto", Font.BOLD,14));
        lblLastName.setFont(new Font("Roboto", Font.BOLD,14));
        lblEmail.setFont(new Font("Roboto", Font.BOLD,14));
        lblMessage.setFont(new Font("Roboto", Font.BOLD,14));

        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        txtEmail = new JTextField();

        txtMessage = new JTextArea();
        txtMessage.setLineWrap(true);
        txtMessage.setWrapStyleWord(true);
        txtMessage.setRows(5);
        txtMessage.setColumns(30);

        int sizeField = Toolkit.getDefaultToolkit().getScreenResolution() / 8;
        txtFirstName.setColumns((int)(sizeField * 1.5));
        txtLastName.setColumns((int)(sizeField * 1.5));
        txtEmail.setColumns((sizeField) / 5);
        txtMessage.setRows(5);

        Color peach = new Color(199, 250, 250, 255);
        int padding = 100;
        panelMain = new JPanel();
        Border peachBorder = BorderFactory.createLineBorder(peach,20);
        Border padBorder = BorderFactory.createEmptyBorder(padding, padding, padding, padding);
        Border compundBorder = BorderFactory.createCompoundBorder(padBorder,peachBorder);
        panelMain.setBorder(compundBorder);
        panelMain.setBackground(Color.WHITE);


        panelWest = new JPanel();
        panelWest.setBorder(BorderFactory.createEmptyBorder(padding,padding,0,0));
        panelWest.setBackground(Color.WHITE);

        panelEast = new JPanel();
        panelEast.setBorder(BorderFactory.createEmptyBorder(padding,0,0,padding));
        panelEast.setBackground(Color.WHITE);

        panelSouth = new JPanel();
        panelSouth.setBackground(Color.WHITE);
        panelSouth.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
        btnSend = new JButton("Send");
        btnSend.setPreferredSize(new Dimension(80,30));

        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtFirstName.getText().isEmpty() || txtLastName.getText().isEmpty() || txtEmail.getText().isEmpty() || txtMessage.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No information entered, Please fill in all fields", "Input Error", JOptionPane.ERROR_MESSAGE);
                }else {
                    JOptionPane.showMessageDialog(null, "Message Successfully Sent");
                }
                txtFirstName.setText("");
                txtLastName.setText("");
                txtEmail.setText("");
                txtMessage.setText("");
                txtFirstName.requestFocus();
            }
        });

        }

        public void setGUI(){
        this.setTitle("Contact Page");

        JScrollPane message = new JScrollPane(txtMessage);
        message.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        panelWest.setLayout(new BoxLayout(panelWest, BoxLayout.Y_AXIS));
        panelWest.add(lblTitle);
        panelWest.add(Box.createVerticalStrut(10));
        panelWest.add(lblSubTitle);
        panelWest.add(Box.createVerticalStrut(15));
        panelWest.add(lblDescription);

        panelEast.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
            panelEast.add(lblFirstName, gbc);
            gbc.gridx = 1;
            panelEast.add(lblLastName, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            gbc.fill = GridBagConstraints.NONE;
            panelEast.add(txtFirstName, gbc);
            gbc.gridx = 1;
            panelEast.add(txtLastName, gbc);

            gbc.gridx = 0;gbc.gridy = 2;
            gbc.gridwidth = 1;
            panelEast.add(lblEmail, gbc);

            gbc.gridy = 3;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridwidth = 2;
            gbc.weighty = 1.0;
            panelEast.add(txtEmail, gbc);

            gbc.gridy = 4;
            gbc.fill = GridBagConstraints.NONE;
            gbc.gridwidth = 1;
            panelEast.add(lblMessage, gbc);

            gbc.gridy = 5;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridwidth = 1;
            gbc.weighty = 1.0;
            panelEast.add(message, gbc);

            gbc.gridy= 6;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.weightx = 1.0;
            panelEast.add(btnSend,gbc);

            panelSouth.setLayout(new GridBagLayout());
            GridBagConstraints gbcSouth = new GridBagConstraints();
            gbcSouth.anchor = GridBagConstraints.CENTER;
            panelSouth.add(lblCopyRight, gbcSouth);

        panelMain.setLayout(new BorderLayout());
        panelMain.add(panelWest, BorderLayout.WEST);
        panelMain.add(panelEast, BorderLayout.EAST);
        panelMain.add(panelSouth, BorderLayout.SOUTH);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        this.setLayout(new BorderLayout());
        this.add(panelMain);
        this.setMaximumSize(new Dimension(screen.width,screen.height));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        }

//    public static void main(String[] args) {
//        new ContactGUI().setGUI();
//    }
    }