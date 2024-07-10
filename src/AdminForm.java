import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminForm extends JFrame {
    private JTextField kodamNameField;
    private JTextField kodamTypeField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton backButton;
    private JTable kodamTable;
    private DefaultTableModel tableModel;
    private int selectedKodamId = -1;

    public AdminForm() {
        setTitle("Admin Form");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);

        loadKodamData();

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addKodam();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateKodam();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteKodam();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToLogin();
            }
        });

        kodamTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && kodamTable.getSelectedRow() != -1) {
                int selectedRow = kodamTable.getSelectedRow();
                selectedKodamId = (int) tableModel.getValueAt(selectedRow, 0);
                kodamNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                kodamTypeField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            }
        });
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel kodamNameLabel = new JLabel("Kodam Name:");
        kodamNameLabel.setBounds(10, 20, 100, 25);
        panel.add(kodamNameLabel);

        kodamNameField = new JTextField(20);
        kodamNameField.setBounds(120, 20, 160, 25);
        panel.add(kodamNameField);

        JLabel kodamTypeLabel = new JLabel("Kodam Type:");
        kodamTypeLabel.setBounds(10, 50, 100, 25);
        panel.add(kodamTypeLabel);

        kodamTypeField = new JTextField(20);
        kodamTypeField.setBounds(120, 50, 160, 25);
        panel.add(kodamTypeField);

        addButton = new JButton("Add Kodam");
        addButton.setBounds(10, 80, 120, 25);
        panel.add(addButton);

        updateButton = new JButton("Update Kodam");
        updateButton.setBounds(140, 80, 120, 25);
        panel.add(updateButton);

        deleteButton = new JButton("Delete Kodam");
        deleteButton.setBounds(270, 80, 120, 25);
        panel.add(deleteButton);

        backButton = new JButton("Back");
        backButton.setBounds(400, 80, 120, 25);
        panel.add(backButton);

        kodamTable = new JTable();
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Type"}, 0);
        kodamTable.setModel(tableModel);
        JScrollPane scrollPane = new JScrollPane(kodamTable);
        scrollPane.setBounds(10, 110, 560, 200);
        panel.add(scrollPane);
    }

    private void loadKodamData() {
        tableModel.setRowCount(0);
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT id, name, type FROM kodam";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String type = resultSet.getString("type");
                tableModel.addRow(new Object[]{id, name, type});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addKodam() {
        String name = kodamNameField.getText();
        String type = kodamTypeField.getText();

        if (name.isEmpty() || type.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO kodam (name, type) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, type);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kodam added successfully");
            loadKodamData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateKodam() {
        if (selectedKodamId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a kodam to update");
            return;
        }

        String name = kodamNameField.getText();
        String type = kodamTypeField.getText();

        if (name.isEmpty() || type.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE kodam SET name = ?, type = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, type);
            statement.setInt(3, selectedKodamId);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kodam updated successfully");
            loadKodamData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void deleteKodam() {
        if (selectedKodamId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a kodam to delete");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM kodam WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedKodamId);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kodam deleted successfully");
            loadKodamData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void backToLogin() {
        new LoginForm().setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdminForm().setVisible(true);
            }
        });
    }
}
