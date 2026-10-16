package digitalsteganography;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private users currentUser;

    public DashboardFrame(users user) {

        currentUser = user;

        setTitle(
                "Digital Steganography - Dashboard"
        );

        setSize(700, 500);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        createUI();
    }

    private void createUI() {

        JPanel mainPanel =
                new JPanel(new BorderLayout());

        JLabel welcomeLabel =
                new JLabel(
                        "Welcome, "
                                + currentUser.getUsername(),
                        SwingConstants.CENTER
                );

        welcomeLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        welcomeLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 10, 20, 10
                )
        );

        mainPanel.add(
                welcomeLabel,
                BorderLayout.NORTH
        );

        JPanel buttonPanel =
                new JPanel(
                        new GridLayout(
                                2,
                                2,
                                20,
                                20
                        )
                );

        buttonPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        40, 60, 40, 60
                )
        );

        JButton encodeButton =
                new JButton("Encode Message");

        JButton decodeButton =
                new JButton("Decode Message");

        JButton historyButton =
                new JButton("View History");

        JButton logoutButton =
                new JButton("Logout");

        buttonPanel.add(encodeButton);
        buttonPanel.add(decodeButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(
                buttonPanel,
                BorderLayout.CENTER
        );

        add(mainPanel);

        encodeButton.addActionListener(e -> {

            new EncodeFrame(currentUser)
                    .setVisible(true);
        });

        decodeButton.addActionListener(e -> {

            new DecodeFrame(currentUser)
                    .setVisible(true);
        });

        historyButton.addActionListener(e -> {

            new HistoryFrame(currentUser)
                    .setVisible(true);
        });

        logoutButton.addActionListener(e -> {

            new LoginFrame().setVisible(true);

            dispose();
        });
    }
}
