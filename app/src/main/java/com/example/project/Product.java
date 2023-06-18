package com.example.project;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Object of Product
 */
public class Product {
    //properties
    private String productName;
    private double price;
    private double latitude;
    private double longitude;
    private String address_super_name;

    /**
     * constructor
     * @param productName - product name
     * @param whoAdd - name's user that add the product to repository
     * @param price - price
     * @param latitude - latitude of the supermarket is the product found
     * @param longitude - longitude of the supermarket is the product found
     * @param address_super_name - address name of the supermarket is the product found
     */
    public Product(String productName, String whoAdd, double price, double latitude, double longitude, String address_super_name) {
        this.productName = productName;
        this.price = price;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address_super_name = address_super_name;
        MySql.insertProduct(productName, whoAdd, price, latitude, longitude, address_super_name);
    }

    /**
     * constructor
     * @param productName - product name
     * @param price - price
     */
    public Product(String productName, double price, String address_super_name)
    {
        this.productName = productName;
        this.price = price;
        this.address_super_name = address_super_name;
    }

    public Product() {
    }

    /**
     * return all products
     * @return - all products
     */
    public ArrayList<String> getAllProducts() {
        return MySql.getAllProducts();
    }

    /**
     * return the address name of this location
     * @return - the address name of location
     */
    public String getAddressNameLocationByLatLng() {
        return MySql.getAddressNameLocationByLatLng(latitude, longitude);
    }

    /**
     * return the address name of this location
     * @return - the address name of location
     */
    public String getAddressNameLocation() {
        return address_super_name;
    }

    /**
     * get price in specific product name and location
     * @return - return price
     */
    public double getPriceOfProductInSuper() {
        return MySql.getPriceOfProductInSuper(productName, latitude, longitude);
    }

    /**
     * get who add the product name in specific supermarket
     * @return - who add the specific product in specific supermarket
     */
    public String getWhoAdd() {
        return MySql.getWhoAdd(productName, address_super_name);
    }

    /**
     * return true if product exist in specific super market else return false
     * @return - true if product exist in specific super market else false
     * @throws SQLException - throw exception if has sql error
     */
    public boolean productExistByLocation() throws SQLException {
        return MySql.productExistByLocation(latitude, longitude, productName);
    }

    /**
     * return all prices from all supermarkets in specific product name
     * @return - return all prices in specific product name
     */
    public double getPriceByProduct() {
        return MySql.getPriceByProduct(productName, address_super_name);
    }

    /**
     * update the product details in db
     * @throws SQLException - throw exception if has sql error
     */
    public void updateProduct() throws SQLException {
        MySql.updateProduct(address_super_name, productName, price);
    }

    /**
     * return the product name
     * @return - product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * return the price
     * @return - price
     */
    public double getPrice() {
        return price;
    }

    /**
     * set product name
     * @param productName - productName
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * set price
     * @param price - price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * set latitude
     * @param latitude - latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * set longitude
     * @param longitude - longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * set super address
     * @param address_super_name - super address
     */
    public void setAddress_super_name(String address_super_name) {
        this.address_super_name = address_super_name;
    }
}
