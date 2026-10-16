package digitalsteganography;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class DecodeFrame extends JFrame {

    private users currentUser;

    private JTextField imageField;
    private JTextArea messageArea;

    private File selectedImage;

    public DecodeFrame(users user) {

        currentUser = user;

        setTitle(
                "Digital Steganography - Decode"
        );

        setSize(650, 500);

        setLocationRelativeTo(null);

        createUI();
    }

    private void createUI() {

        JPanel mainPanel =
                new JPanel(new BorderLayout(10, 10));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 20, 20, 20
                )
        );

        JPanel imagePanel =
                new JPanel(new BorderLayout(5, 5));

        imageField =
                new JTextField();

        imageField.setEditable(false);

        JButton browseButton =
                new JButton("Select Stego Image");

        imagePanel.add(
                imageField,
                BorderLayout.CENTER
        );

        imagePanel.add(
                browseButton,
                BorderLayout.EAST
        );

        messageArea =
                new JTextArea();

        messageArea.setEditable(false);

        messageArea.setLineWrap(true);

        messageArea.setWrapStyleWord(true);

        JScrollPane scrollPane =
                new JScrollPane(messageArea);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Hidden Message"
                )
        );

        JButton decodeButton =
                new JButton("Decode Message");

        mainPanel.add(
                imagePanel,
                BorderLayout.NORTH
        );

        mainPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        mainPanel.add(
                decodeButton,
                BorderLayout.SOUTH
        );

        add(mainPanel);

        browseButton.addActionListener(
                e -> selectImage()
        );

        decodeButton.addActionListener(
                e -> decodeMessage()
        );
    }

    private void selectImage() {

        JFileChooser chooser =
                new JFileChooser();

        int result =
                chooser.showOpenDialog(this);

        if (result ==
                JFileChooser.APPROVE_OPTION) {

            selectedImage =
                    chooser.getSelectedFile();

            imageField.setText(
                    selectedImage.getAbsolutePath()
            );
        }
    }

    private void decodeMessage() {

        if (selectedImage == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a stego image."
            );

            return;
        }

        try {

            BufferedImage image =
                    ImageIO.read(selectedImage);

            if (image == null) {

                throw new Exception(
                        "Invalid image file."
                );
            }

            String message =
                    Steganography.decode(image);

            messageArea.setText(message);

            saveHistory(
                    "DECODE",
                    selectedImage.getName(),
                    message.length()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Message decoded successfully!"
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Decoding failed:\n"
                            + e.getMessage()
            );
        }
    }

    private void saveHistory(
            String operation,
            String imageName,
            int messageLength) {

        String sql =
                "INSERT INTO steganography_history "
                        + "(user_id, operation, image_name, "
                        + "message_length) "
                        + "VALUES (?, ?, ?, ?)";

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    currentUser.getUserId()
            );

            ps.setString(
                    2,
                    operation
            );

            ps.setString(
                    3,
                    imageName
            );

            ps.setInt(
                    4,
                    messageLength
            );

            ps.executeUpdate();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}