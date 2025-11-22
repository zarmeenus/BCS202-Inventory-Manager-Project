import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/* CUSTOM EXCEPTIONS */
class ProductNotFoundException extends Exception {
    public ProductNotFoundException() { super("Product not found!"); }
    public ProductNotFoundException(String message) { super(message); }
}

class InvalidInputException extends Exception {
    public InvalidInputException() { super("Invalid input detected!"); }
    public InvalidInputException(String message) { super(message); }
}

class DuplicateProductException extends Exception {
    public DuplicateProductException(String id) { super("Product ID " + id + " already exists. Please use a unique ID."); }
}

/* PRODUCT CLASSES */
class Product {
    private String productId;
    private String productName;
    private double price;
    private int quantity;

    public Product(String productId, String productName, double price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    /* Getters & Setters */
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    /* Display Information */
    public String getDisplayString() {
        return String.format("ID: %-5s | Name: %-15s | Price: $%.2f | Qty: %d", 
                             productId, productName, price, quantity);
    }
}

class PerishableProduct extends Product {
    private String expiryDate;

    public PerishableProduct(String productId, String productName, double price, int quantity, String expiryDate) {
        super(productId, productName, price, quantity);
        this.expiryDate = expiryDate;
    }

    public String getDisplayString() {
        return super.getDisplayString() + " | Expiry: " + expiryDate;
    }
}

/* INVENTORY MANAGER */
class InventoryManager {
    public ArrayList<Product> products = new ArrayList<>();

    void addInitialProducts() {
        products.add(new Product("01", "Water", 1.00, 50));
        products.add(new PerishableProduct("02", "Biscuit", 3.00, 30, "15/11/2025"));
        products.add(new Product("03", "Chocolate", 3.00, 45));
        products.add(new PerishableProduct("04", "Ice Cream", 1.50, 29, "10/11/2025"));
        products.add(new Product("05", "Gummy Bears", 2.50, 45));
    }
    
    // HELPER METHOD
    private boolean isProductIdExists(String id) {
        for (Product p : products) {
            if (p.getProductId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    // MODIFIED METHOD
    void addProduct(Product p) throws InvalidInputException, DuplicateProductException {
        // Check for duplicate ID
        if (isProductIdExists(p.getProductId())) {
            throw new DuplicateProductException(p.getProductId());
        }
        
        // Check for negative values
        if (p.getPrice() < 0 || p.getQuantity() < 0) {
            throw new InvalidInputException("Price or Quantity cannot be negative!");
        }
        products.add(p);
    }

    Product findProduct(String id) throws ProductNotFoundException {
        for (Product p : products) {
            if (p.getProductId().equals(id)) return p;
        }
        throw new ProductNotFoundException("Product not found in inventory!");
    }

    void updateProduct(String id, double newPrice, int newQuantity) throws ProductNotFoundException, InvalidInputException {
        Product p = findProduct(id); 
        if (newPrice < 0 || newQuantity < 0) {
            throw new InvalidInputException("Price or Quantity cannot be negative!");
        }
        p.setPrice(newPrice);
        p.setQuantity(newQuantity);
    }

    void deleteProduct(String id) throws ProductNotFoundException {
        Product p = findProduct(id);
        products.remove(p);
    }

    int getProductCount() {
        return products.size();
    }
}


/* THE GUI CLASS (Main Application) */
public class InventoryGUI extends JFrame {
    
    private InventoryManager manager;
    private JLabel totalLabel;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    // NEW CLASS MEMBER FOR THE TEXT AREA
    private JTextArea viewTextArea; 
    
    // Theme Colors and Fonts (Changed to lowerCamelCase)
    private final Color paleBlue = new Color(220, 230, 255); 
    private final Color darkBlue = new Color(0, 0, 102);     
    private final Color white = Color.WHITE;
    private final Font mainFont = new Font("SansSerif", Font.PLAIN, 16); 
    private final Font headingFont = new Font("SansSerif", Font.BOLD, 28);
    private final Font subHeadingFont = new Font("SansSerif", Font.BOLD, 20);

    // Constant name for the View Products card (Changed to lowerCamelCase)
    private static final String viewCardName = "ViewProducts";

    public InventoryGUI() {
        manager = new InventoryManager();
        manager.addInitialProducts();

        // Window Settings
        setTitle("Y.A.Z Inventory Management System");
        setSize(1000, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(paleBlue); // Use paleBlue

        // HEADER 
        JLabel heading = new JLabel("Welcome to Y.A.Z Inventory Management System", JLabel.CENTER);
        heading.setFont(headingFont); // Use headingFont
        heading.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); 
        heading.setForeground(darkBlue); // Use darkBlue
        add(heading, BorderLayout.NORTH);

        // MAIN CENTER PANEL (CardLayout)
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(paleBlue); // Use paleBlue
        add(mainContentPanel, BorderLayout.CENTER);

        // All screen panels
        mainContentPanel.add(createMainMenuPanel(), "Menu");
        mainContentPanel.add(createAddProductPanel(), "AddProduct");
        // We create the view panel only once on startup
        mainContentPanel.add(createViewProductsPanel(), viewCardName); // Use viewCardName
        mainContentPanel.add(createUpdateProductPanel(), "UpdateProduct");
        mainContentPanel.add(createDeleteProductPanel(), "DeleteProduct");
        mainContentPanel.add(createSearchProductPanel(), "SearchProduct");


        // FOOTER 
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(paleBlue); // Use paleBlue
        footerPanel.setBorder(new EmptyBorder(5, 5, 5, 10)); 
        
        totalLabel = new JLabel("Total Items: " + manager.getProductCount());
        totalLabel.setFont(mainFont); // Use mainFont
        totalLabel.setForeground(darkBlue); // Use darkBlue
        footerPanel.add(totalLabel);
        
        add(footerPanel, BorderLayout.SOUTH);
        
        cardLayout.show(mainContentPanel, "Menu");
    }
    
    // HELPER METHODS

    private void updateTotalLabel() {
        totalLabel.setText("Total Items: " + manager.getProductCount());
    }

    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(mainFont.deriveFont(Font.BOLD, 18f)); // Use mainFont
        btn.setBackground(white); // Use white
        btn.setForeground(darkBlue); // Use darkBlue
        btn.setPreferredSize(new Dimension(300, 50)); 
        btn.setFocusPainted(false);
        
        btn.addActionListener(e -> {
            if (cardName.equals("Exit")) {
                System.exit(0);
            }
            if (cardName.equals(viewCardName)) { // Use viewCardName
                // MODIFIED: Call the simpler update method
                updateViewProductsPanel(); 
                cardLayout.show(mainContentPanel, viewCardName); // Use viewCardName
            } else {
                cardLayout.show(mainContentPanel, cardName);
            }
        });
        return btn;
    }
    
    // NEW METHOD: Updates the text area content directly
    private void updateViewProductsPanel() {
        StringBuilder sb = new StringBuilder();
        if (manager.products.isEmpty()) {
            sb.append("No products in inventory.");
        } else {
            for (Product p : manager.products) {
                sb.append(p.getDisplayString()).append("\n");
            }
        }
        // Set the text of the class member JTextArea
        if (viewTextArea != null) {
            viewTextArea.setText(sb.toString());
            viewTextArea.setCaretPosition(0); // Scroll to top
        }
    }


    private Component findComponentByName(String name) {
        // Keeping this method but it is no longer used for the refresh logic
        CardLayout cl = (CardLayout) mainContentPanel.getLayout();
        for (Component comp : mainContentPanel.getComponents()) {
            if (cl.getConstraints(comp) != null && name.equals(cl.getConstraints(comp))) {
                return comp;
            }
        }
        return null; 
    }

    private JButton createBackButton() {
        JButton btn = new JButton("Back to Menu");
        btn.setFont(mainFont); // Use mainFont
        btn.setBackground(white); // Use white
        btn.setForeground(darkBlue); // Use darkBlue
        btn.setFocusPainted(false);
        btn.addActionListener(e -> cardLayout.show(mainContentPanel, "Menu"));
        return btn;
    }
    
    private void styleLabel(JLabel label) {
        label.setFont(mainFont); // Use mainFont
        label.setForeground(darkBlue); // Use darkBlue
    }
    
    private JButton createActionFormButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(mainFont); // Use mainFont
        btn.setBackground(white); // Use white
        btn.setForeground(darkBlue); // Use darkBlue
        return btn;
    }

    // CARD PANELS (SCREENS)
    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout()); 
        panel.setBackground(paleBlue); // Use paleBlue
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); 
        gbc.gridx = 0; 
        
        panel.add(createMenuButton("1. Add Product", "AddProduct"), gbc);
        panel.add(createMenuButton("2. View All Products", viewCardName), gbc); // Use viewCardName
        panel.add(createMenuButton("3. Update Product", "UpdateProduct"), gbc);
        panel.add(createMenuButton("4. Delete Product", "DeleteProduct"), gbc);
        panel.add(createMenuButton("5. Search Product by ID", "SearchProduct"), gbc);
        panel.add(createMenuButton("6. Exit", "Exit"), gbc);

        return panel;
    }
    
    private JPanel createAddProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(paleBlue); // Use paleBlue
        panel.setBorder(new EmptyBorder(50, 100, 50, 100)); 

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(paleBlue); // Use paleBlue
        
        JTextField idField = new JTextField(15); idField.setFont(mainFont); // Use mainFont
        JTextField nameField = new JTextField(15); nameField.setFont(mainFont); // Use mainFont
        JTextField priceField = new JTextField(15); priceField.setFont(mainFont); // Use mainFont
        JTextField qtyField = new JTextField(15); qtyField.setFont(mainFont); // Use mainFont
        JCheckBox perishableCheck = new JCheckBox("Is Perishable?"); perishableCheck.setFont(mainFont); // Use mainFont
        JTextField expiryField = new JTextField(15); expiryField.setFont(mainFont); // Use mainFont
        
        expiryField.setEnabled(false);
        perishableCheck.setBackground(paleBlue); // Use paleBlue
        perishableCheck.setForeground(darkBlue); // Use darkBlue
        perishableCheck.addItemListener(e -> {
            expiryField.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
            if (e.getStateChange() != ItemEvent.SELECTED) {
                expiryField.setText(""); 
            }
        });

        // Labels
        JLabel idLabel = new JLabel("Product ID (0XX):"); styleLabel(idLabel);
        JLabel nameLabel = new JLabel("Name:"); styleLabel(nameLabel);
        JLabel priceLabel = new JLabel("Price ($0.00):"); styleLabel(priceLabel);
        JLabel qtyLabel = new JLabel("Quantity:"); styleLabel(qtyLabel);
        JLabel expiryLabel = new JLabel("Expiry Date (DD/MM/YYYY):"); styleLabel(expiryLabel);

        formPanel.add(idLabel); formPanel.add(idField);
        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(priceLabel); formPanel.add(priceField);
        formPanel.add(qtyLabel); formPanel.add(qtyField);
        formPanel.add(perishableCheck); formPanel.add(new JLabel("")); 
        formPanel.add(expiryLabel); formPanel.add(expiryField);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(paleBlue); // Use paleBlue
        JButton saveButton = createActionFormButton("Save Product");
        
        controlPanel.add(saveButton);
        controlPanel.add(createBackButton());
        
        JLabel screenTitle = new JLabel("Add New Product", JLabel.CENTER);
        screenTitle.setFont(subHeadingFont); // Use subHeadingFont
        screenTitle.setForeground(darkBlue); // Use darkBlue

        panel.add(screenTitle, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        // Save Button Action (Added new exception handling)
        saveButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int qty = Integer.parseInt(qtyField.getText().trim());
                
                if (id.isEmpty() || name.isEmpty()) throw new InvalidInputException("ID and Name cannot be empty.");

                Product newProduct;
                if (perishableCheck.isSelected()) {
                    if (expiryField.getText().trim().isEmpty()) {
                         throw new InvalidInputException("Perishable product requires an Expiry Date.");
                    }
                    newProduct = new PerishableProduct(id, name, price, qty, expiryField.getText().trim());
                } else {
                    newProduct = new Product(id, name, price, qty);
                }
                
                manager.addProduct(newProduct); // This throws DuplicateProductException
                
                JOptionPane.showMessageDialog(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateTotalLabel();
                cardLayout.show(mainContentPanel, "Menu"); 
                
                // Clear fields
                idField.setText(""); nameField.setText(""); priceField.setText("");
                qtyField.setText(""); expiryField.setText(""); perishableCheck.setSelected(false);
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: Price and Quantity must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (DuplicateProductException ex) { // NEW EXCEPTION CATCH
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return panel;
    }
    
    private JPanel createViewProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(paleBlue); // Use paleBlue
        panel.setBorder(new EmptyBorder(20, 50, 20, 50)); 
        
        JLabel titleLabel = new JLabel("Current Inventory List", JLabel.CENTER);
        titleLabel.setFont(subHeadingFont); // Use subHeadingFont
        titleLabel.setForeground(darkBlue); // Use darkBlue
        panel.add(titleLabel, BorderLayout.NORTH);

        // We call the update method to populate the StringBuilder for the initial view
        StringBuilder sb = new StringBuilder();
        if (manager.products.isEmpty()) {
            sb.append("No products in inventory.");
        } else {
            for (Product p : manager.products) {
                sb.append(p.getDisplayString()).append("\n");
            }
        }
        
        // ASSIGN JTEXTAREA TO THE CLASS MEMBER
        viewTextArea = new JTextArea(sb.toString());
        viewTextArea.setEditable(false);
        viewTextArea.setFont(mainFont); // Use mainFont
        viewTextArea.setForeground(darkBlue); // Use darkBlue
        viewTextArea.setBackground(white); // Use white

        JScrollPane scroll = new JScrollPane(viewTextArea);
        scroll.setBorder(BorderFactory.createLineBorder(darkBlue)); // Use darkBlue
        
        panel.add(scroll, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(paleBlue); // Use paleBlue
        controlPanel.add(createBackButton());
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createUpdateProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(paleBlue); // Use paleBlue
        panel.setBorder(new EmptyBorder(50, 100, 50, 100));

        JLabel titleLabel = new JLabel("Update Existing Product", JLabel.CENTER);
        titleLabel.setFont(subHeadingFont); // Use subHeadingFont
        titleLabel.setForeground(darkBlue); // Use darkBlue
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Form Setup 
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBackground(paleBlue); // Use paleBlue
        
        JTextField idField = new JTextField(15); idField.setFont(mainFont); // Use mainFont
        JTextField priceField = new JTextField(15); priceField.setFont(mainFont); // Use mainFont
        JTextField qtyField = new JTextField(15); qtyField.setFont(mainFont); // Use mainFont

        // MODIFIED LABELS
        JLabel idLabel = new JLabel("Product ID (0XX):"); styleLabel(idLabel);
        JLabel priceLabel = new JLabel("New Price ($0.00):"); styleLabel(priceLabel);
        JLabel qtyLabel = new JLabel("New Quantity:"); styleLabel(qtyLabel);
        
        JLabel currentInfoLabel = new JLabel("Current Info: N/A", JLabel.CENTER); styleLabel(currentInfoLabel);

        formPanel.add(idLabel); formPanel.add(idField);
        formPanel.add(priceLabel); formPanel.add(priceField);
        formPanel.add(qtyLabel); formPanel.add(qtyField);
        formPanel.add(new JLabel("")); formPanel.add(currentInfoLabel); 
        
        // Control Setup
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(paleBlue); // Use paleBlue
        JButton updateButton = createActionFormButton("Update Product");
        
        controlPanel.add(updateButton);
        controlPanel.add(createBackButton());
        
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        // Action Listeners 
        idField.addFocusListener(new FocusAdapter() 
        {
            public void focusLost(FocusEvent e) {
                try {
                    Product p = manager.findProduct(idField.getText().trim());
                    currentInfoLabel.setText("Current Price: $" + String.format("%.2f", p.getPrice()) + ", Qty: " + p.getQuantity());
                } catch (ProductNotFoundException ex) {
                    currentInfoLabel.setText("Current Info: Product Not Found");
                }
            }
        });

        updateButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                double newPrice = Double.parseDouble(priceField.getText().trim());
                int newQuantity = Integer.parseInt(qtyField.getText().trim());
                
                manager.updateProduct(id, newPrice, newQuantity);
                
                JOptionPane.showMessageDialog(this, "Product ID " + id + " updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainContentPanel, "Menu");
                idField.setText(""); priceField.setText(""); qtyField.setText(""); currentInfoLabel.setText("Current Info: N/A");
                
            } catch (ProductNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Update Failed", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: Price and Quantity must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return panel;
    }
    
    private JPanel createDeleteProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(paleBlue); // Use paleBlue
        panel.setBorder(new EmptyBorder(100, 200, 100, 200));

        JLabel titleLabel = new JLabel("Delete Product by ID", JLabel.CENTER);
        titleLabel.setFont(subHeadingFont); // Use subHeadingFont
        titleLabel.setForeground(darkBlue); // Use darkBlue
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form Setup 
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        formPanel.setBackground(paleBlue); // Use paleBlue
        
        JTextField idField = new JTextField(15); idField.setFont(mainFont); // Use mainFont
        // MODIFIED LABEL
        JLabel idLabel = new JLabel("Enter Product ID (0XX):"); styleLabel(idLabel);
        JButton deleteButton = createActionFormButton("Delete Product");
        
        formPanel.add(idLabel);
        formPanel.add(idField);
        
        // Control Setup 
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(paleBlue); // Use paleBlue
        
        controlPanel.add(deleteButton);
        controlPanel.add(createBackButton());
        
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        // Action Listener
        deleteButton.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Product ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Product ID: " + id + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    manager.deleteProduct(id);
                    updateTotalLabel();
                    JOptionPane.showMessageDialog(this, "Product ID " + id + " deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainContentPanel, "Menu");
                    idField.setText("");
                } catch (ProductNotFoundException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Deletion Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }
    
    private JPanel createSearchProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(paleBlue); // Use paleBlue
        panel.setBorder(new EmptyBorder(50, 100, 50, 100));

        JLabel titleLabel = new JLabel("Search Product by ID", JLabel.CENTER);
        titleLabel.setFont(subHeadingFont); // Use subHeadingFont
        titleLabel.setForeground(darkBlue); // Use darkBlue
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form Setup
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        formPanel.setBackground(paleBlue); // Use paleBlue
        
        JTextField idField = new JTextField(15); idField.setFont(mainFont); // Use mainFont
        // MODIFIED LABEL
        JLabel idLabel = new JLabel("Enter Product ID (0XX):"); styleLabel(idLabel);
        JButton searchButton = createActionFormButton("Search");
        
        formPanel.add(idLabel);
        formPanel.add(idField);
        
        // Results Panel 
        JTextArea resultArea = new JTextArea(5, 40);
        resultArea.setEditable(false);
        resultArea.setFont(mainFont); // Use mainFont
        resultArea.setForeground(darkBlue); // Use darkBlue
        resultArea.setBackground(white); // Use white
        JScrollPane scroll = new JScrollPane(resultArea);
        
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(paleBlue); // Use paleBlue
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(scroll, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Control Setup 
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(paleBlue); // Use paleBlue
        
        controlPanel.add(searchButton);
        controlPanel.add(createBackButton());
        
        panel.add(controlPanel, BorderLayout.SOUTH);

        // Action Listener
        searchButton.addActionListener(e -> {
            String id = idField.getText().trim();
            resultArea.setText(""); 
            if (id.isEmpty()) {
                resultArea.setText("Please enter a Product ID to search.");
                return;
            }
            try {
                Product p = manager.findProduct(id);
                resultArea.setText("Product Found:\n" + p.getDisplayString());
            } catch (ProductNotFoundException ex) {
                resultArea.setText(ex.getMessage());
            }
        });

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InventoryGUI().setVisible(true);
        });
    }
}