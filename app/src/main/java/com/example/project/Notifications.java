package com.example.project;

/**
 * class to notifications methods
 */
public class Notifications {
    //properties
    private int row_num;

    /**
     * constructor
     * @param productName - product name
     * @param price - the update price
     * @param row_num - row number to know how much notifications bar to send
     */
    public Notifications(String productName, double price, int row_num) {
        this.row_num = row_num;
        MySql.insertNotifications(productName, price, row_num);
    }

    public Notifications() {
    }

    /**
     *
     * @return - the length of reports in data base
     */
    public int checkLengthNotifications() {
        return MySql.checkLengthNotifications();
    }

    /**
     * get product name from specific row notifications
     * @return - product name
     */
    public String getProductNameFromSpecificRowNotifications() {
        return MySql.getProductNameFromSpecificRowNotifications(row_num);
    }

    /**
     * set row num
     * @param row_num - row num
     */
    public void setRow_num(int row_num) {
        this.row_num = row_num;
    }
}
