package digitalsteganography;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterFrame extends JFrame {

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;

    public RegisterFrame() {

        setTitle(
                "Digital Steganography - Register"
        );

        setSize(450, 400);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        createUI();
    }

    private void createUI() {

        JPanel mainPanel =
                new JPanel(new BorderLayout());

        JLabel title =
                new JLabel(
                        "CREATE ACCOUNT",
                        SwingConstants.CENTER
                );

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        title.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 10, 20, 10
                )
        );

        JPanel formPanel =
                new JPanel(
                        new GridLayout(
                                5,
                                2,
                                10,
                                10
                        )
                );

        formPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 40, 20, 40
                )
        );

        usernameField =
                new JTextField();

        emailField =
                new JTextField();

        passwordField =
                new JPasswordField();

        JButton registerButton =
                new JButton("Register");

        JButton backButton =
                new JButton("Back to Login");

        formPanel.add(
                new JLabel("Username:")
        );

        formPanel.add(usernameField);

        formPanel.add(
                new JLabel("Email:")
        );

        formPanel.add(emailField);

        formPanel.add(
                new JLabel("Password:")
        );

        formPanel.add(passwordField);

        formPanel.add(registerButton);

        formPanel.add(backButton);

        mainPanel.add(
                title,
                BorderLayout.NORTH
        );

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        add(mainPanel);

        registerButton.addActionListener(
                e -> registerUser()
        );

        backButton.addActionListener(e -> {

            new LoginFrame().setVisible(true);

            dispose();
        });
    }

    private void registerUser() {

        String username =
                usernameField.getText().trim();

        String email =
                emailField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                );

        if (username.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields."
            );

            return;
        }

        String hashedPassword =
                PasswordUtil.hashPassword(password);

        String sql =
                "INSERT INTO users " +
                        "(username, email, password) " +
                        "VALUES (?, ?, ?)";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, hashedPassword);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Registration successful!"
            );

            new LoginFrame().setVisible(true);

            dispose();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Registration failed:\n"
                            + e.getMessage()
            );
        }
    }
}