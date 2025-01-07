package model;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:inventory.db";
    private Connection conn;

    public Database() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS inventory (
                item_name TEXT PRIMARY KEY,
                quantity REAL,
                unit TEXT
            )""";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public Map<String, InventoryItem> loadInventory() {
        Map<String, InventoryItem> inventory = new HashMap<>();
        String sql = "SELECT * FROM inventory";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String name = rs.getString("item_name");
                double quantity = rs.getDouble("quantity");
                String unit = rs.getString("unit");
                inventory.put(name, new InventoryItem(name, quantity, unit));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventory;
    }

    public void saveInventory(Map<String, InventoryItem> inventory) {
        try {
            conn.setAutoCommit(false);
            
            // Clear existing data
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DELETE FROM inventory");
            }
            
            // Insert new data
            String sql = "INSERT INTO inventory (item_name, quantity, unit) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (InventoryItem item : inventory.values()) {
                    pstmt.setString(1, item.getName());
                    pstmt.setDouble(2, item.getQuantity());
                    pstmt.setString(3, item.getUnit());
                    pstmt.executeUpdate();
                }
            }
            
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 