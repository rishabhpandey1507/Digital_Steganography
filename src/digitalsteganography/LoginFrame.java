package digitalsteganography;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {

        setTitle("Digital Steganography - Login");

        setSize(450, 350);

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
                        "DIGITAL STEGANOGRAPHY",
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
                                4,
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

        passwordField =
                new JPasswordField();

        JButton loginButton =
                new JButton("Login");

        JButton registerButton =
                new JButton("Register");

        formPanel.add(
                new JLabel("Username:")
        );

        formPanel.add(usernameField);

        formPanel.add(
                new JLabel("Password:")
        );

        formPanel.add(passwordField);

        formPanel.add(loginButton);

        formPanel.add(registerButton);

        mainPanel.add(
                title,
                BorderLayout.NORTH
        );

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        add(mainPanel);

        loginButton.addActionListener(
                e -> login()
        );

        registerButton.addActionListener(e -> {

            new RegisterFrame().setVisible(true);

            dispose();
        });
    }

    private void login() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                );

        if (username.isEmpty() ||
                password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password."
            );

            return;
        }

        String hashedPassword =
                PasswordUtil.hashPassword(password);

        String sql =
                "SELECT user_id, username, email " +
                        "FROM users " +
                        "WHERE username = ? AND password = ?";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, username);
            ps.setString(2, hashedPassword);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                users user =
                        new users(
                                rs.getInt("user_id"),
                                rs.getString("username"),
                                rs.getString("email")
                        );

                JOptionPane.showMessageDialog(
                        this,
                        "Login successful!"
                );

                new DashboardFrame(user)
                        .setVisible(true);

                dispose();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password."
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Login error:\n"
                            + e.getMessage()
            );
        }
    }
}