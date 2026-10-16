package digitalsteganography;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HistoryFrame extends JFrame {

    private users currentUser;
    private JTable table;

    public HistoryFrame(users user) {

        currentUser = user;

        setTitle(
                "Digital Steganography - History"
        );

        setSize(750, 450);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        createUI();

        loadHistory();
    }

    private void createUI() {

        JLabel title =
                new JLabel(
                        "Steganography History",
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
                        15, 10, 15, 10
                )
        );

        String[] columns = {
                "ID",
                "Operation",
                "Image Name",
                "Message Length",
                "Date"
        };

        DefaultTableModel model =
                new DefaultTableModel(
                        columns,
                        0
                );

        table =
                new JTable(model);

        JScrollPane scrollPane =
                new JScrollPane(table);

        add(
                title,
                BorderLayout.NORTH
        );

        add(
                scrollPane,
                BorderLayout.CENTER
        );
    }

    private void loadHistory() {

        String sql =
                "SELECT history_id, operation, "
                        + "image_name, message_length, "
                        + "operation_date "
                        + "FROM steganography_history "
                        + "WHERE user_id = ? "
                        + "ORDER BY operation_date DESC";

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

            ResultSet rs =
                    ps.executeQuery();

            DefaultTableModel model =
                    (DefaultTableModel)
                            table.getModel();

            while (rs.next()) {

                model.addRow(
                        new Object[]{
                                rs.getInt(
                                        "history_id"
                                ),

                                rs.getString(
                                        "operation"
                                ),

                                rs.getString(
                                        "image_name"
                                ),

                                rs.getInt(
                                        "message_length"
                                ),

                                rs.getTimestamp(
                                        "operation_date"
                                )
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load history:\n"
                            + e.getMessage()
            );
        }
    }
}
