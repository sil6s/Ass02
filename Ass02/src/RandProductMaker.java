// Silas Curry
// Programming II
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class RandProductMaker extends JFrame {
    private static final int FIELD_WIDTH = 25;
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color SECONDARY_COLOR = new Color(245, 245, 245);
    private static final Color BUTTON_COLOR = new Color(51, 122, 183);
    private static final Color BUTTON_HOVER_COLOR = new Color(40, 96, 144);
    private static final Font MAIN_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BOLD_FONT = new Font("Arial", Font.BOLD, 14);

    private JTextField nameField, descriptionField, idField, costField;
    private JTextField recordCountField;
    private JButton addButton, quitButton;
    private RandomAccessFile file;
    private int recordCount = 0;

    public RandProductMaker() {
        super("Silas's Product Management System");
        setupFrame();
        initializeComponents();
        setupLayout();
        initializeFile();
    }

    private void setupFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(600, 450));
        getContentPane().setBackground(SECONDARY_COLOR);
        setResizable(true);
    }

    private void initializeComponents() {
        nameField = createStyledTextField();
        descriptionField = createStyledTextField();
        idField = createStyledTextField();
        costField = createStyledTextField();
        recordCountField = createStyledTextField();
        recordCountField.setEditable(false);
        recordCountField.setBackground(new Color(240, 240, 240));

        addButton = createStyledButton("Add Product");
        quitButton = createStyledButton("Exit");

        addButton.addActionListener(new AddButtonListener());
        quitButton.addActionListener(e -> System.exit(0));
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(FIELD_WIDTH);
        field.setFont(MAIN_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(BUTTON_HOVER_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(BUTTON_HOVER_COLOR);
                } else {
                    g2.setColor(BUTTON_COLOR);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(BOLD_FONT);
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        return button;
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(SECONDARY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(SECONDARY_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                        "Product Details",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        BOLD_FONT,
                        PRIMARY_COLOR
                ),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        addFormField(formPanel, "Product Name:", nameField, gbc, 0);
        addFormField(formPanel, "Description:", descriptionField, gbc, 1);
        addFormField(formPanel, "Product ID:", idField, gbc, 2);
        addFormField(formPanel, "Cost ($):", costField, gbc, 3);
        addFormField(formPanel, "Record Count:", recordCountField, gbc, 4);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(SECONDARY_COLOR);
        buttonPanel.add(addButton);
        buttonPanel.add(quitButton);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBackground(SECONDARY_COLOR);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addFormField(JPanel panel, String labelText, JTextField field,
                              GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(BOLD_FONT);
        label.setForeground(new Color(60, 60, 60));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        panel.add(field, gbc);
    }

    private void initializeFile() {
        try {
            file = new RandomAccessFile("products.dat", "rw");
            recordCount = (int) (file.length() / (35 + 75 + 6 + Double.BYTES));
            recordCountField.setText(String.valueOf(recordCount));
            file.seek(file.length());
        } catch (IOException e) {
            showError("File Error", "Unable to initialize product file: " + e.getMessage());
            System.exit(1);
        }
    }

    private class AddButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (!validateInputs()) {
                return;
            }

            try {
                Product product = new Product(
                        nameField.getText().trim(),
                        descriptionField.getText().trim(),
                        idField.getText().trim(),
                        Double.parseDouble(costField.getText().trim())
                );

                file.writeUTF(product.getFormattedName());
                file.writeUTF(product.getFormattedDescription());
                file.writeUTF(product.getFormattedID());
                file.writeDouble(product.getCost());

                recordCount++;
                recordCountField.setText(String.valueOf(recordCount));
                clearFields();
                showSuccess("Product Added", "Product has been successfully added to the database.");

            } catch (IOException ex) {
                showError("File Error", "Failed to write product data: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                showError("Invalid Input", "Please enter a valid number for cost.");
            }
        }
    }

    private boolean validateInputs() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Validation Error", "Product name is required");
            nameField.requestFocus();
            return false;
        }
        if (idField.getText().trim().isEmpty()) {
            showError("Validation Error", "Product ID is required");
            idField.requestFocus();
            return false;
        }
        try {
            double cost = Double.parseDouble(costField.getText().trim());
            if (cost < 0) {
                showError("Validation Error", "Cost cannot be negative");
                costField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Validation Error", "Please enter a valid cost");
            costField.requestFocus();
            return false;
        }
        return true;
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title,
                JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearFields() {
        nameField.setText("");
        descriptionField.setText("");
        idField.setText("");
        costField.setText("");
        nameField.requestFocus();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            UIManager.put("TextField.caretForeground", PRIMARY_COLOR);
            UIManager.put("TextField.selectionBackground", PRIMARY_COLOR);
            UIManager.put("TextField.selectionForeground", Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            RandProductMaker frame = new RandProductMaker();
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
