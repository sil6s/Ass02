// Silas Curry
// Programming II
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.NumberFormat;
import java.util.Locale;

public class RandProductSearch extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color SECONDARY_COLOR = new Color(245, 245, 245);
    private static final Color BUTTON_COLOR = new Color(51, 122, 183);
    private static final Color BUTTON_HOVER_COLOR = new Color(40, 96, 144);
    private static final Color RESULT_HIGHLIGHT_COLOR = new Color(240, 240, 255);
    private static final Font MAIN_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BOLD_FONT = new Font("Arial", Font.BOLD, 14);

    private JTextField searchField;
    private JTextPane resultPane;
    private JButton searchButton, quitButton;
    private RandomAccessFile file;
    private NumberFormat currencyFormatter;

    public RandProductSearch() {
        super("Silas's Product Search System");
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        setupFrame();
        initializeComponents();
        setupLayout();
        initializeFile();
    }

    private void setupFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        getContentPane().setBackground(SECONDARY_COLOR);
        setResizable(true);
    }

    private void initializeComponents() {
        searchField = createStyledTextField();
        searchField.addActionListener(e -> searchButton.doClick());

        resultPane = createStyledTextPane();

        searchButton = createStyledButton("Search");
        quitButton = createStyledButton("Exit");

        searchButton.addActionListener(new SearchButtonListener());
        quitButton.addActionListener(e -> System.exit(0));
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(30);
        field.setFont(MAIN_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JTextPane createStyledTextPane() {
        JTextPane pane = new JTextPane();
        pane.setFont(MAIN_FONT);
        pane.setEditable(false);
        pane.setBackground(Color.WHITE);
        pane.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return pane;
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

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        searchPanel.setBackground(SECONDARY_COLOR);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                        "Search Products",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        BOLD_FONT,
                        PRIMARY_COLOR
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel searchLabel = new JLabel("Search Term:");
        searchLabel.setFont(BOLD_FONT);
        searchLabel.setForeground(new Color(60, 60, 60));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel resultsPanel = new JPanel(new BorderLayout(10, 10));
        resultsPanel.setBackground(SECONDARY_COLOR);
        resultsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                        "Search Results",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        BOLD_FONT,
                        PRIMARY_COLOR
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(resultPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(SECONDARY_COLOR);
        buttonPanel.add(quitButton);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(resultsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void initializeFile() {
        try {
            file = new RandomAccessFile("products.dat", "r");
        } catch (IOException e) {
            showError("File Error", "Unable to open product database: " + e.getMessage());
            System.exit(1);
        }
    }

    private class SearchButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String searchTerm = searchField.getText().toLowerCase().trim();

            if (searchTerm.isEmpty()) {
                showError("Search Error", "Please enter a search term");
                return;
            }

            try {
                boolean found = false;
                file.seek(0);

                StringBuilder results = new StringBuilder();
                results.append(String.format("Search results for \"%s\":\n\n", searchTerm));

                while (file.getFilePointer() < file.length()) {
                    String name = file.readUTF().trim();
                    String description = file.readUTF().trim();
                    String id = file.readUTF().trim();
                    double cost = file.readDouble();

                    if (name.toLowerCase().contains(searchTerm) ||
                            description.toLowerCase().contains(searchTerm) ||
                            id.toLowerCase().contains(searchTerm)) {

                        results.append("Product ID: ").append(id).append("\n");
                        results.append("Name: ").append(name).append("\n");
                        results.append("Description: ").append(description).append("\n");
                        results.append("Price: ").append(currencyFormatter.format(cost)).append("\n");
                        results.append("─".repeat(50)).append("\n\n");

                        found = true;
                    }
                }

                if (!found) {
                    results = new StringBuilder("No products found matching \"")
                            .append(searchTerm).append("\"");
                }

                resultPane.setText(results.toString());
                resultPane.setCaretPosition(0);

            } catch (IOException ex) {
                showError("Search Error", "Error reading product database: " + ex.getMessage());
            }
        }
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title,
                JOptionPane.ERROR_MESSAGE);
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
            RandProductSearch frame = new RandProductSearch();
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
