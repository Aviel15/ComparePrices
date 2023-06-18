package com.example.project;

import androidx.annotation.NonNull;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Object of BasketList
 */
public class BasketList {
    //properties
    private String productName;
    private int amount;

    /**
     * insert product to basket list to data base
     */
    public void insertBasketList() {
        MySql.insertBasketList(productName, amount);
    }

    /**
     * check if product exist in basket list
     * @return return true if product exist else return false
     */
    public boolean basketListProductExist() {
        try {
            return MySql.basketListProductExist(productName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     *  insert product name to basket list
     */
    public void insertProductNameBasketList(){
        MySql.insertProductNameBasketList(productName);
    }

    /**
     * get all amounts in basket list from data base
     * @return - return all amounts in basket list
     */
    public ArrayList<Integer> getAllBasketListAmount() {
        return MySql.getAllBasketListAmount();
    }

    /**
     * get all product names in basket list from data base
     * @return - return all product names in basket list
     */
    public ArrayList<String> getAllBasketListName() {
        return MySql.getAllBasketListName();
    }

    /**
     * remove all values from basket list
     */
    public void removeAllBasketList()
    {
        MySql.removeAllBasketList();
    }

    /**
     * set product name
     * @param productName - product name
     */
    public void setProduct_name(String productName) {
        this.productName = productName;
    }

    /**
     * set amount
     * @param amount - amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * return the product name
     * @return - return the product name
     */
    @NonNull
    @Override
    public String toString() {
        return productName;
    }
}
