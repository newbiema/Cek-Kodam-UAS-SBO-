import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserForm extends JFrame {
    private JTextField nameField;
    private JLabel kodamLabel;
    private JButton resultButton;
    private JButton convertToPdfButton;
    private JButton backButton;
    private String userName;
    private String kodamName;
    private String kodamType;

    public UserForm() {
        setTitle("User Form");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new BackgroundPanel();
        add(panel);
        placeComponents(panel);

        resultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRandomKodam();
            }
        });

        convertToPdfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertToPdf();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToLogin();
            }
        });
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(10, 20, 80, 25);
        nameLabel.setForeground(Color.WHITE);
        panel.add(nameLabel);

        nameField = new JTextField(20);
        nameField.setBounds(100, 20, 165, 25);
        panel.add(nameField);

        kodamLabel = new JLabel("Kodam:");
        kodamLabel.setBounds(10, 50, 300, 75);
        kodamLabel.setForeground(Color.WHITE);
        panel.add(kodamLabel);

        resultButton = new JButton("Lihat Kodam");
        resultButton.setBounds(10, 130, 150, 25);
        panel.add(resultButton);

        convertToPdfButton = new JButton("Print Hasil Kodam");
        convertToPdfButton.setBounds(10, 160, 150, 25);
        panel.add(convertToPdfButton);

        backButton = new JButton("Back");
        backButton.setBounds(10, 190, 150, 25);
        panel.add(backButton);
    }

    private void showRandomKodam() {
        userName = nameField.getText();
        if (userName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan nama!");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT name, type FROM kodam ORDER BY RAND() LIMIT 1";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                kodamName = resultSet.getString("name");
                kodamType = resultSet.getString("type");
                kodamLabel.setText("<html>Nama: " + userName + "<br/>Kodam: " + kodamName + "<br/>Type Kodam: " + kodamType + "</html>");
            } else {
                kodamLabel.setText("Kodam tidak ada");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void convertToPdf() {
        if (userName == null || kodamName == null || kodamType == null) {
            JOptionPane.showMessageDialog(this, "Please show a Kodam first");
            return;
        }

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("Kodam.pdf"));
            document.open();
            document.add(new Paragraph("Nama: " + userName));
            document.add(new Paragraph("Nama Kodam: " + kodamName));
            document.add(new Paragraph("Type Kodam: " + kodamType));
            document.close();
            JOptionPane.showMessageDialog(this, "PDF berhasil dibuat");
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    private void backToLogin() {
        new LoginForm().setVisible(true);
        dispose();
    }

    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                backgroundImage = new ImageIcon(getClass().getResource("img/background.jpg")).getImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UserForm().setVisible(true);
            }
        });
    }
}
