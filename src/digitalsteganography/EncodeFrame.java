package digitalsteganography;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class EncodeFrame extends JFrame {

    private users currentUser;

    private JTextField imageField;
    private JTextArea messageArea;

    private File selectedImage;

    public EncodeFrame(users user) {

        currentUser = user;

        setTitle(
                "Digital Steganography - Encode"
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
                new JButton("Select Image");

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

        messageArea.setLineWrap(true);

        messageArea.setWrapStyleWord(true);

        JScrollPane scrollPane =
                new JScrollPane(messageArea);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Enter Secret Message"
                )
        );

        JButton encodeButton =
                new JButton("Encode & Save");

        mainPanel.add(
                imagePanel,
                BorderLayout.NORTH
        );

        mainPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        mainPanel.add(
                encodeButton,
                BorderLayout.SOUTH
        );

        add(mainPanel);

        browseButton.addActionListener(
                e -> selectImage()
        );

        encodeButton.addActionListener(
                e -> encodeMessage()
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

    private void encodeMessage() {

        if (selectedImage == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an image."
            );

            return;
        }

        String message =
                messageArea.getText();

        if (message.trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a secret message."
            );

            return;
        }

        try {

            BufferedImage original =
                    ImageIO.read(selectedImage);

            if (original == null) {

                throw new Exception(
                        "Invalid image file."
                );
            }

            BufferedImage encoded =
                    Steganography.encode(
                            original,
                            message
                    );

            JFileChooser saveChooser =
                    new JFileChooser();

            saveChooser.setSelectedFile(
                    new File(
                            "encoded_image.png"
                    )
            );

            int result =
                    saveChooser.showSaveDialog(this);

            if (result !=
                    JFileChooser.APPROVE_OPTION) {

                return;
            }

            File output =
                    saveChooser.getSelectedFile();

            String outputPath =
                    output.getAbsolutePath();

            if (!outputPath
                    .toLowerCase()
                    .endsWith(".png")) {

                output =
                        new File(
                                outputPath + ".png"
                        );
            }

            ImageIO.write(
                    encoded,
                    "png",
                    output
            );

            saveHistory(
                    "ENCODE",
                    output.getName(),
                    message.length()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Message encoded successfully!\n\n"
                            + "Saved at:\n"
                            + output.getAbsolutePath()
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Encoding failed:\n"
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