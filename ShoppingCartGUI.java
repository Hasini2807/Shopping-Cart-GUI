import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ShoppingCartGUI extends JFrame {
    // Database connection variables
 private static final String URL = "jdbc:mysql://localhost:3306/shopping_cart_db";
private static final String USER = "root";
private static final String PASSWORD = "MyNewPass123!";


    private JTextField txtId, txtName, txtPrice, txtQuantity;
    private JTable table;
    private DefaultTableModel model;

    public ShoppingCartGUI() {
        setTitle("🛒 Online Shopping Cart");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ---------- TOP PANEL (Form) ----------
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Cart Details"));

        formPanel.add(new JLabel("Item ID:"));
        txtId = new JTextField();
        formPanel.add(txtId);

        formPanel.add(new JLabel("Item Name:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("Price:"));
        txtPrice = new JTextField();
        formPanel.add(txtPrice);

        formPanel.add(new JLabel("Quantity:"));
        txtQuantity = new JTextField();
        formPanel.add(txtQuantity);

        // Buttons
        JButton btnAdd = new JButton("Add Item");
        JButton btnUpdate = new JButton("Update Item");
        JButton btnDelete = new JButton("Delete Item");
        JButton btnLoad = new JButton("Load Cart");

        formPanel.add(btnAdd);
        formPanel.add(btnUpdate);
        formPanel.add(btnDelete);
        formPanel.add(btnLoad);

        add(formPanel, BorderLayout.NORTH);

        // ---------- TABLE ----------
        model = new DefaultTableModel(new String[]{"Item ID", "Item Name", "Price", "Quantity"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ---------- BUTTON ACTIONS ----------
        btnAdd.addActionListener(e -> addItem());
        btnUpdate.addActionListener(e -> updateItem());
        btnDelete.addActionListener(e -> deleteItem());
        btnLoad.addActionListener(e -> loadItems());

        setVisible(true);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void addItem() {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO Cart (item_id, item_name, price, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(txtId.getText()));
            pst.setString(2, txtName.getText());
            pst.setDouble(3, Double.parseDouble(txtPrice.getText()));
            pst.setInt(4, Integer.parseInt(txtQuantity.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item added successfully!");
            loadItems();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateItem() {
        try (Connection conn = getConnection()) {
            String sql = "UPDATE Cart SET item_name=?, price=?, quantity=? WHERE item_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtName.getText());
            pst.setDouble(2, Double.parseDouble(txtPrice.getText()));
            pst.setInt(3, Integer.parseInt(txtQuantity.getText()));
            pst.setInt(4, Integer.parseInt(txtId.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item updated successfully!");
            loadItems();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteItem() {
        try (Connection conn = getConnection()) {
            String sql = "DELETE FROM Cart WHERE item_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(txtId.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item deleted successfully!");
            loadItems();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadItems() {
        try (Connection conn = getConnection()) {
            model.setRowCount(0); // clear table
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Cart");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShoppingCartGUI());
    }
}