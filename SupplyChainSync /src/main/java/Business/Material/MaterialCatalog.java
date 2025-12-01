/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Material;

import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class MaterialCatalog {
    
    private ArrayList<Material> materialList;

    public MaterialCatalog() {
        this.materialList = new ArrayList<>();
    }

    public ArrayList<Material> getMaterialList() {
        return materialList;
    }
    
    /**
     * Create and add a new material to the catalog
     */
    public Material createMaterial(String materialCode, String materialName, double unitPrice, String unit) {
        Material material = new Material(materialCode, materialName, unitPrice, unit);
        materialList.add(material);
        return material;
    }
    
    /**
     * Add an existing material to the catalog
     */
    public void addMaterial(Material material) {
        materialList.add(material);
    }
    
    /**
     * Remove a material from the catalog
     */
    public void removeMaterial(Material material) {
        materialList.remove(material);
    }
    
    /**
     * Find material by code
     */
    public Material findByCode(String materialCode) {
        for (Material material : materialList) {
            if (material.getMaterialCode().equalsIgnoreCase(materialCode)) {
                return material;
            }
        }
        return null;
    }
    
    /**
     * Find material by ID
     */
    public Material findById(int materialId) {
        for (Material material : materialList) {
            if (material.getMaterialId() == materialId) {
                return material;
            }
        }
        return null;
    }
    
    /**
     * Find materials by category
     */
    public ArrayList<Material> findByCategory(String category) {
        ArrayList<Material> result = new ArrayList<>();
        for (Material material : materialList) {
            if (material.getCategory() != null && material.getCategory().equalsIgnoreCase(category)) {
                result.add(material);
            }
        }
        return result;
    }
    
    /**
     * Get all active materials
     */
    public ArrayList<Material> getActiveMaterials() {
        ArrayList<Material> result = new ArrayList<>();
        for (Material material : materialList) {
            if (material.isActive()) {
                result.add(material);
            }
        }
        return result;
    }
    
    /**
     * Search materials by name (partial match)
     */
    public ArrayList<Material> searchByName(String keyword) {
        ArrayList<Material> result = new ArrayList<>();
        for (Material material : materialList) {
            if (material.getMaterialName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(material);
            }
        }
        return result;
    }
}
