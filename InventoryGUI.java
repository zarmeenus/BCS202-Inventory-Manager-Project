import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;



                                                                            /* CUSTOM EXCEPTIONS */
class ProductNotFoundException extends Exception
{
    public ProductNotFoundException()
    { super("Product not found!");}

    public ProductNotFoundException(String message)
    { super(message);}
}

class InvalidInputException extends Exception
{
    public InvalidInputException()
    { super("Invalid input detected!");}

    public InvalidInputException(String message)
    { super(message);}
}

class DuplicateProductException extends Exception
{
     public DuplicateProductException()
    {super("Duplicate product detected!");}

    public DuplicateProductException(String id)
    {super("Product ID " + id + " already exists. Please use a unique ID.");}
}





                                                                                /* PRODUCT CLASSES */
class Product
{
    private String productId;
    private String productName;
    private double price;
    private int quantity;

    public Product(String productId, String productName, double price, int quantity)
    {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    /* Getters */
    public String getProductId()
    { return productId; }

    public String getProductName()
    { return productName; }

    public double getPrice()
    { return price; }

    public int getQuantity()
    { return quantity; }


    /* Setters */
    public void setPrice(double price)
    { this.price = price; }

    public void setQuantity(int quantity)
    { this.quantity = quantity; }


    /* Display Information */
    public String getDisplayString()
    {
        return String.format("ID: %-5s | Name: %-15s | Price: AED%.2f | Qty: %d",
                                        productId, productName, price, quantity);
    }
}



                                                                          /* PERISHABLE PRODUCT CLASS */
class PerishableProduct extends Product
{
    private String expiryDate;

    public PerishableProduct(String productId, String productName, double price, int quantity, String expiryDate)
    {
        super(productId, productName, price, quantity);
        this.expiryDate = expiryDate;
    }

    public String getDisplayString()
    {
        return super.getDisplayString() + " | Expiry: " + expiryDate;
    }
}




                                                                                  /* INVENTORY MANAGER CLASS */
class InventoryManager
{
    private ArrayList<Product> products = new ArrayList<>();


    /* Method For Adding 5 Items */
    void addInitialProducts()
    {
        products.add(new Product("01", "Water", 1.00, 50));
        products.add(new PerishableProduct("02", "Biscuit", 3.00, 30, "15/11/2025"));
        products.add(new Product("03", "Chocolate", 3.00, 45));
        products.add(new PerishableProduct("04", "Ice Cream", 1.50, 29, "10/11/2025"));
        products.add(new Product("05", "Gummy Bears", 2.50, 45));
    }


    /* Add new product */
    private boolean isProductIdExists(String id) // helper method to detect duplicates
    {
        for (Product p : products)
        {
            if (p.getProductId().equals(id))
            {
                return true;
            }
        }
        return false;
    }

    void addProduct(Product p) throws InvalidInputException, DuplicateProductException
    {
        if (isProductIdExists(p.getProductId())) // Check for duplicate ID
        {
            throw new DuplicateProductException(p.getProductId());
        }

        if (p.getPrice() < 0 || p.getQuantity() < 0) // Check for negative values
        {
            throw new InvalidInputException("Price or Quantity cannot be negative!");
        }
        products.add(p); // Add product to the list
    }


    /* Search product by ID */
    Product findProduct(String id) throws ProductNotFoundException
    {
        for (Product p : products)
        {
            if (p.getProductId().equals(id))
            {
                return p; // Product found
            }
        }

        throw new ProductNotFoundException("Product not found in inventory!");
    }


    /* Update product */
    void updateProduct(String id, double newPrice, int newQuantity) throws ProductNotFoundException, InvalidInputException
    {
        Product p = findProduct(id); // Find product
        if (newPrice < 0 || newQuantity < 0) // Validate inputs
        {
            throw new InvalidInputException("Price or Quantity cannot be negative!");
        }
        p.setPrice(newPrice); // Update price
        p.setQuantity(newQuantity); // Update quantity
    }

    /* Delete product */
    void deleteProduct(String id) throws ProductNotFoundException
    {
        Product p = findProduct(id);// Find product
        products.remove(p); // Remove from list
    }


    /* Total number of products in inventory */
    int getProductCount()
    {
        return products.size();
    }

    /* View all products */
    List<Product> getProducts()
    {
        return Collections.unmodifiableList(products);
    }
}





                                                                             /* THE GUI CLASS (MainApp) */
public class InventoryGUI extends JFrame
{

    private InventoryManager manager;  // Inventory manager to handle product operations
    private JLabel totalLabel; // Displays total number of products
    private JPanel mainContentPanel; // Panel that switches between screens
    private CardLayout cardLayout; // Layout to swap panels
    private JTextArea viewTextArea; // Displays product list


    // UI styling
    private final Color paleBlue = new Color(220, 230, 255);
    private final Color darkBlue = new Color(0, 0, 102);
    private final Color white = Color.WHITE;
    private final Font mainFont = new Font("SansSerif", Font.PLAIN, 16);
    private final Font headingFont = new Font("SansSerif", Font.BOLD, 28);
    private final Font subHeadingFont = new Font("SansSerif", Font.BOLD, 20);
    private static final String viewCardName = "ViewProducts";



    public InventoryGUI() // Construtor sets window properties (size, title, layout, background color)
    {
        manager = new InventoryManager();
        manager.addInitialProducts(); // Preload existing products

        // Window Settings
        setTitle("Y.A.Z Inventory Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(paleBlue);

        // Header
        JLabel heading = new JLabel("Welcome to Y.A.Z Inventory Management System", JLabel.CENTER);
        heading.setFont(headingFont);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        heading.setForeground(darkBlue);
        add(heading, BorderLayout.NORTH);

        // Main panel with CardLayout for switching between screens
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(paleBlue);
        add(mainContentPanel, BorderLayout.CENTER);

        // Add individual screens/panels
        mainContentPanel.add(createMainMenuPanel(), "Menu");
        mainContentPanel.add(createAddProductPanel(), "AddProduct");
        mainContentPanel.add(createViewProductsPanel(), viewCardName);
        mainContentPanel.add(createUpdateProductPanel(), "UpdateProduct");
        mainContentPanel.add(createDeleteProductPanel(), "DeleteProduct");
        mainContentPanel.add(createSearchProductPanel(), "SearchProduct");

        // Footer displaying total product count
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(paleBlue);
        footerPanel.setBorder(new EmptyBorder(5, 5, 5, 10));
        totalLabel = new JLabel("Total Items: " + manager.getProductCount());
        totalLabel.setFont(mainFont);
        totalLabel.setForeground(darkBlue);
        footerPanel.add(totalLabel);
        add(footerPanel, BorderLayout.SOUTH);

        // Show main menu
        cardLayout.show(mainContentPanel, "Menu");
    }


    /* Method to refresh total product count label*/
    private void updateTotalLabel()
    {
        totalLabel.setText("Total Items: " + manager.getProductCount());
    }


    /* Create menu buttons that switch panels */
    private JButton createMenuButton(String text, String cardName)
    {
        JButton btn = new JButton(text);
        btn.setFont(mainFont.deriveFont(Font.BOLD, 18f));
        btn.setBackground(white);
        btn.setForeground(darkBlue);
        btn.setPreferredSize(new Dimension(300, 50));
        btn.setFocusPainted(false);

        btn.addActionListener(e -> {
            if (cardName.equals("Exit"))
            {
                System.exit(0);
            }


            if (cardName.equals(viewCardName))
            {
                updateViewProductsPanel(); // Refresh view panel before showing
                cardLayout.show(mainContentPanel, viewCardName);
            }

            else
            {
                cardLayout.show(mainContentPanel, cardName);
            }
        } );

            return btn;
    }


    /* Update the view products panel with current inventory */
    private void updateViewProductsPanel()
    {
        StringBuilder sb = new StringBuilder();
        if (manager.getProducts().isEmpty())
        {
            sb.append("No products in inventory.");
        }

        else
        {
            for (Product p : manager.getProducts())
            {
                sb.append(p.getDisplayString()).append("\n");
            }
        }

        if (viewTextArea != null)
        {
            viewTextArea.setText(sb.toString());
            viewTextArea.setCaretPosition(0);
        }
    }


    /* "Back to Menu" button */
    private JButton createBackButton()
    {
        JButton btn = new JButton("Back to Menu");
        btn.setFont(mainFont);
        btn.setBackground(white);
        btn.setForeground(darkBlue);
        btn.setFocusPainted(false);
        btn.addActionListener(e -> cardLayout.show(mainContentPanel, "Menu"));
        return btn;
    }


    /* Style labels consistently */
    private void styleLabel(JLabel label)
    {
        label.setFont(mainFont);
        label.setForeground(darkBlue);
    }


    /* Button styling for action forms */
    private JButton createActionFormButton(String text)
    {
        JButton btn = new JButton(text);
        btn.setFont(mainFont);
        btn.setBackground(white);
        btn.setForeground(darkBlue);
        return btn;
    }


    /* Validate Product ID input */
    private void validateProductId(String id) throws InvalidInputException
    {
        if (id.isEmpty())
        {
            throw new InvalidInputException("Product ID cannot be empty.");
        }

        try
        {
            Integer.parseInt(id);
        }

        catch (NumberFormatException ex)
        {
            throw new InvalidInputException("Product ID must be a numeric integer.");
        }
    }


    /* Main Menu Panel */
    private JPanel createMainMenuPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(paleBlue);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;

        panel.add(createMenuButton("1. Add Product", "AddProduct"), gbc);
        panel.add(createMenuButton("2. View All Products", viewCardName), gbc);
        panel.add(createMenuButton("3. Update Product", "UpdateProduct"), gbc);
        panel.add(createMenuButton("4. Delete Product", "DeleteProduct"), gbc);
        panel.add(createMenuButton("5. Search Product by ID", "SearchProduct"), gbc);
        panel.add(createMenuButton("6. Exit", "Exit"), gbc);

        return panel;
    }


    /* Add Product Panel */
    private JPanel createAddProductPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(paleBlue);
        panel.setBorder(new EmptyBorder(50, 100, 50, 100));
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(paleBlue);

        JTextField idField = new JTextField(15); idField.setFont(mainFont);
        JTextField nameField = new JTextField(15); nameField.setFont(mainFont);
        JTextField priceField = new JTextField(15); priceField.setFont(mainFont);
        JTextField qtyField = new JTextField(15); qtyField.setFont(mainFont);
        JCheckBox perishableCheck = new JCheckBox("Is Perishable?"); perishableCheck.setFont(mainFont);
        JTextField expiryField = new JTextField(15); expiryField.setFont(mainFont);

        expiryField.setEnabled(false);
        perishableCheck.setBackground(paleBlue);
        perishableCheck.setForeground(darkBlue);
        perishableCheck.addItemListener(e -> {
            expiryField.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
            if (e.getStateChange() != ItemEvent.SELECTED)
            {
                expiryField.setText("");
            }
        });

        // Labels
        JLabel idLabel = new JLabel("Product ID (0XX):"); styleLabel(idLabel);
        JLabel nameLabel = new JLabel("Name:"); styleLabel(nameLabel);
        JLabel priceLabel = new JLabel("Price (AED 0.00):"); styleLabel(priceLabel);
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
        controlPanel.setBackground(paleBlue);
        JButton saveButton = createActionFormButton("Save Product");

        controlPanel.add(saveButton);
        controlPanel.add(createBackButton());

        JLabel screenTitle = new JLabel("Add New Product", JLabel.CENTER);
        screenTitle.setFont(subHeadingFont);
        screenTitle.setForeground(darkBlue);

        panel.add(screenTitle, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                validateProductId(id);
                double price = Double.parseDouble(priceField.getText().trim());
                int qty = Integer.parseInt(qtyField.getText().trim());

                if (name.isEmpty()) throw new InvalidInputException("Product Name cannot be empty.");

                Product newProduct;
                if (perishableCheck.isSelected())
                {
                    if (expiryField.getText().trim().isEmpty())
                    {
                        throw new InvalidInputException("Perishable product requires an Expiry Date.");
                    }
                    newProduct = new PerishableProduct(id, name, price, qty, expiryField.getText().trim());
                }

                else
                {
                    newProduct = new Product(id, name, price, qty);
                }

                manager.addProduct(newProduct);

                JOptionPane.showMessageDialog(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateTotalLabel();
                cardLayout.show(mainContentPanel, "Menu");

                idField.setText(""); nameField.setText(""); priceField.setText("");
                qtyField.setText(""); expiryField.setText(""); perishableCheck.setSelected(false);

            }

            catch (NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(this, "Price and Quantity must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }

            catch (DuplicateProductException | InvalidInputException ex)
            {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }

            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }


    /* View Products Panel */
    private JPanel createViewProductsPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(paleBlue);
        panel.setBorder(new EmptyBorder(20, 50, 20, 50));

        JLabel titleLabel = new JLabel("Current Inventory List", JLabel.CENTER);
        titleLabel.setFont(subHeadingFont);
        titleLabel.setForeground(darkBlue);
        panel.add(titleLabel, BorderLayout.NORTH);

        StringBuilder sb = new StringBuilder();
        if (manager.getProducts().isEmpty())
        {
            sb.append("No products in inventory.");
        }

        else
        {
            for (Product p : manager.getProducts())
            {
                sb.append(p.getDisplayString()).append("\n");
            }
        }

        viewTextArea = new JTextArea(sb.toString());
        viewTextArea.setEditable(false);
        viewTextArea.setFont(mainFont);
        viewTextArea.setForeground(darkBlue);
        viewTextArea.setBackground(white);

        JScrollPane scroll = new JScrollPane(viewTextArea);
        scroll.setBorder(BorderFactory.createLineBorder(darkBlue));

        panel.add(scroll, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(paleBlue);
        controlPanel.add(createBackButton());
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }


    /* Update Product Panel */
    private JPanel createUpdateProductPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(paleBlue);
        panel.setBorder(new EmptyBorder(50, 100, 50, 100));

        JLabel titleLabel = new JLabel("Update Existing Product", JLabel.CENTER);
        titleLabel.setFont(subHeadingFont);
        titleLabel.setForeground(darkBlue);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBackground(paleBlue);

        JTextField idField = new JTextField(15); idField.setFont(mainFont);
        JTextField priceField = new JTextField(15); priceField.setFont(mainFont);
        JTextField qtyField = new JTextField(15); qtyField.setFont(mainFont);

        JLabel idLabel = new JLabel("Product ID (0XX):"); styleLabel(idLabel);
        JLabel priceLabel = new JLabel("New Price (AED 0.00):"); styleLabel(priceLabel);
        JLabel qtyLabel = new JLabel("New Quantity:"); styleLabel(qtyLabel);
        JLabel currentInfoLabel = new JLabel("Current Info: N/A", JLabel.CENTER); styleLabel(currentInfoLabel);

        formPanel.add(idLabel); formPanel.add(idField);
        formPanel.add(priceLabel); formPanel.add(priceField);
        formPanel.add(qtyLabel); formPanel.add(qtyField);
        formPanel.add(new JLabel("")); formPanel.add(currentInfoLabel);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(paleBlue);
        JButton updateButton = createActionFormButton("Update Product");

        controlPanel.add(updateButton);
        controlPanel.add(createBackButton());

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        idField.addFocusListener(new FocusAdapter()
        {
            public void focusLost(FocusEvent e)
            {
                try
                {
                    validateProductId(idField.getText().trim());
                    Product p = manager.findProduct(idField.getText().trim());
                    currentInfoLabel.setText("Current Price: AED" + String.format("%.2f", p.getPrice()) + ", Qty: " + p.getQuantity());
                }

                catch (ProductNotFoundException ex)
                {
                    currentInfoLabel.setText("Current Info: Product Not Found");
                }

                catch (InvalidInputException ex)
                {
                    currentInfoLabel.setText("Current Info: " + ex.getMessage());
                }
            }
        });

        updateButton.addActionListener(e -> {
            try
            {
                String id = idField.getText().trim();
                validateProductId(id);
                double newPrice = Double.parseDouble(priceField.getText().trim());
                int newQuantity = Integer.parseInt(qtyField.getText().trim());

                manager.updateProduct(id, newPrice, newQuantity);

                JOptionPane.showMessageDialog(this, "Product ID " + id + " updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainContentPanel, "Menu");
                idField.setText(""); priceField.setText(""); qtyField.setText(""); currentInfoLabel.setText("Current Info: N/A");

            }

            catch (ProductNotFoundException ex)
            {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Update Failed", JOptionPane.ERROR_MESSAGE);
            }

            catch (NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(this, "Price and Quantity must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }

            catch (InvalidInputException ex)
            {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }

            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }


    /* Delete Product Panel */
    private JPanel createDeleteProductPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(paleBlue);
        panel.setBorder(new EmptyBorder(100, 200, 100, 200));

        JLabel titleLabel = new JLabel("Delete Product by ID", JLabel.CENTER);
        titleLabel.setFont(subHeadingFont);
        titleLabel.setForeground(darkBlue);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        formPanel.setBackground(paleBlue);

        JTextField idField = new JTextField(15); idField.setFont(mainFont);
        JLabel idLabel = new JLabel("Enter Product ID (0XX):"); styleLabel(idLabel);
        JButton deleteButton = createActionFormButton("Delete Product");

        formPanel.add(idLabel); formPanel.add(idField);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(paleBlue);

        controlPanel.add(deleteButton);
        controlPanel.add(createBackButton());

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        deleteButton.addActionListener(e -> {
            try
            {
                String id = idField.getText().trim();
                validateProductId(id);

                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Product ID: " + id + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION)
                {
                    manager.deleteProduct(id);
                    updateTotalLabel();
                    JOptionPane.showMessageDialog(this, "Product ID " + id + " deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainContentPanel, "Menu");
                    idField.setText("");
                }
            }

            catch (InvalidInputException ex)
            {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }

            catch (ProductNotFoundException ex)
            {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Deletion Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }


    /* Search Product Panel */
    private JPanel createSearchProductPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(paleBlue);
        panel.setBorder(new EmptyBorder(50, 100, 50, 100));

        JLabel titleLabel = new JLabel("Search Product by ID", JLabel.CENTER);
        titleLabel.setFont(subHeadingFont);
        titleLabel.setForeground(darkBlue);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        formPanel.setBackground(paleBlue);

        JTextField idField = new JTextField(15); idField.setFont(mainFont);
        JLabel idLabel = new JLabel("Enter Product ID (0XX):"); styleLabel(idLabel);
        JButton searchButton = createActionFormButton("Search");

        formPanel.add(idLabel); formPanel.add(idField);

        JTextArea resultArea = new JTextArea(5, 40);
        resultArea.setEditable(false);
        resultArea.setFont(mainFont);
        resultArea.setForeground(darkBlue);
        resultArea.setBackground(white);
        JScrollPane scroll = new JScrollPane(resultArea);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(paleBlue);
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(scroll, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(paleBlue);

        controlPanel.add(searchButton);
        controlPanel.add(createBackButton());

        panel.add(controlPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> {
            try
            {
                String id = idField.getText().trim();
                validateProductId(id);
                resultArea.setText("");
                Product p = manager.findProduct(id);
                resultArea.setText("Product Found:\n" + p.getDisplayString());
            }

            catch (InvalidInputException ex)
            {
                resultArea.setText(ex.getMessage());
            }

            catch (ProductNotFoundException ex)
            {
                resultArea.setText(ex.getMessage());
            }
        });

        return panel;
    }


    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new InventoryGUI().setVisible(true)); // Start the inventory GUI
    }
}
