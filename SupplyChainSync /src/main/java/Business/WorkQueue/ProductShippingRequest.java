/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.WorkQueue;

import Business.Product.Product;
import java.util.Date;

/**
 *
 * @author chris
 */
public class ProductShippingRequest extends WorkRequest {
    
    // Shipping status constants
    public static final String SHIP_STATUS_PENDING = "Pending";
    public static final String SHIP_STATUS_PICKED_UP = "Picked Up";
    public static final String SHIP_STATUS_IN_TRANSIT = "In Transit";
    public static final String SHIP_STATUS_OUT_FOR_DELIVERY = "Out for Delivery";
    public static final String SHIP_STATUS_DELIVERED = "Delivered";
    
    // Product reference
    private Product product;
    
    // Product details
    private String productCode;
    private String productName;
    private int quantity;
    private String unit;
    
    // Address information
    private String originAddress;
    private String destinationAddress;
    private String destinationStoreName;
    
    // Shipping details
    private Date estimatedDeliveryDate;
    private Date actualDeliveryDate;
    private String trackingNumber;
    private String shippingStatus;
    private String carrierName;
    private double shippingCost;
    private double packageWeight;
    private String packageDimensions;
    
    // Related request
    private RetailPurchaseOrderRequest originalPurchaseOrder;
    
    public ProductShippingRequest() {
        super();
        this.shippingStatus = SHIP_STATUS_PENDING;
    }
    
    public ProductShippingRequest(Product product, int quantity) {
        this();
        setProduct(product);
        this.quantity = quantity;
    }
    
    public ProductShippingRequest(RetailPurchaseOrderRequest purchaseOrder) {
        this();
        this.originalPurchaseOrder = purchaseOrder;
        if (purchaseOrder != null) {
            setProduct(purchaseOrder.getProduct());
            this.quantity = purchaseOrder.getQuantity();
            this.productCode = purchaseOrder.getProductCode();
            this.productName = purchaseOrder.getProductName();
            this.unit = purchaseOrder.getUnit();
            this.destinationStoreName = purchaseOrder.getStoreName();
            this.destinationAddress = purchaseOrder.getStoreAddress();
        }
    }

    // ==================== Product Reference ====================
    
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.productCode = product.getProductCode();
            this.productName = product.getProductName();
            this.unit = product.getUnit();
        }
    }

    // ==================== Product Details ====================

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    // ==================== Address Information ====================

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getDestinationStoreName() {
        return destinationStoreName;
    }

    public void setDestinationStoreName(String destinationStoreName) {
        this.destinationStoreName = destinationStoreName;
    }

    // ==================== Shipping Details ====================

    public Date getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(Date estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public Date getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(Date actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
        // Update main status as well
        if (SHIP_STATUS_DELIVERED.equals(shippingStatus)) {
            setStatus(STATUS_DELIVERED);
            this.actualDeliveryDate = new Date();
        } else if (SHIP_STATUS_IN_TRANSIT.equals(shippingStatus) || 
                   SHIP_STATUS_OUT_FOR_DELIVERY.equals(shippingStatus)) {
            setStatus(STATUS_IN_PROGRESS);
        }
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public double getPackageWeight() {
        return packageWeight;
    }

    public void setPackageWeight(double packageWeight) {
        this.packageWeight = packageWeight;
    }

    public String getPackageDimensions() {
        return packageDimensions;
    }

    public void setPackageDimensions(String packageDimensions) {
        this.packageDimensions = packageDimensions;
    }

    // ==================== Related Request ====================

    public RetailPurchaseOrderRequest getOriginalPurchaseOrder() {
        return originalPurchaseOrder;
    }

    public void setOriginalPurchaseOrder(RetailPurchaseOrderRequest originalPurchaseOrder) {
        this.originalPurchaseOrder = originalPurchaseOrder;
    }
    
    // ==================== Utility Methods ====================
    
    public void markPickedUp() {
        setShippingStatus(SHIP_STATUS_PICKED_UP);
    }
    
    public void markInTransit() {
        setShippingStatus(SHIP_STATUS_IN_TRANSIT);
    }
    
    public void markOutForDelivery() {
        setShippingStatus(SHIP_STATUS_OUT_FOR_DELIVERY);
    }
    
    public void markDelivered() {
        setShippingStatus(SHIP_STATUS_DELIVERED);
    }
    
    public boolean isDelivered() {
        return SHIP_STATUS_DELIVERED.equals(shippingStatus);
    }
    
    @Override
    public String toString() {
        return "Product Shipping #" + getRequestId() + " - " + productName + " [" + shippingStatus + "]";
    }
}
