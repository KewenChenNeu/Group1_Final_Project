/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Product;

import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class ProductCatalog {
    
    private ArrayList<Product> productList;

    public ProductCatalog() {
        this.productList = new ArrayList<>();
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }
    
    /**
     * Create and add a new product to the catalog
     */
    public Product createProduct(String productCode, String productName, double unitPrice, String unit) {
        Product product = new Product(productCode, productName, unitPrice, unit);
        productList.add(product);
        return product;
    }
    
    /**
     * Add an existing product to the catalog
     */
    public void addProduct(Product product) {
        productList.add(product);
    }
    
    /**
     * Remove a product from the catalog
     */
    public void removeProduct(Product product) {
        productList.remove(product);
    }
    
    /**
     * Find product by code
     */
    public Product findByCode(String productCode) {
        for (Product product : productList) {
            if (product.getProductCode().equalsIgnoreCase(productCode)) {
                return product;
            }
        }
        return null;
    }
    
    /**
     * Find product by ID
     */
    public Product findById(int productId) {
        for (Product product : productList) {
            if (product.getProductId() == productId) {
                return product;
            }
        }
        return null;
    }
    
    /**
     * Find products by category
     */
    public ArrayList<Product> findByCategory(String category) {
        ArrayList<Product> result = new ArrayList<>();
        for (Product product : productList) {
            if (product.getCategory() != null && product.getCategory().equalsIgnoreCase(category)) {
                result.add(product);
            }
        }
        return result;
    }
    
    /**
     * Get all active products
     */
    public ArrayList<Product> getActiveProducts() {
        ArrayList<Product> result = new ArrayList<>();
        for (Product product : productList) {
            if (product.isActive()) {
                result.add(product);
            }
        }
        return result;
    }
    
    /**
     * Search products by name (partial match)
     */
    public ArrayList<Product> searchByName(String keyword) {
        ArrayList<Product> result = new ArrayList<>();
        for (Product product : productList) {
            if (product.getProductName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(product);
            }
        }
        return result;
    }
}

