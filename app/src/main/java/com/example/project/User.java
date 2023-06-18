package com.example.project;

/**
 * Object of User
 */
public class User {
    //properties
    private String username;
    private String password;
    private int canReport;

    /**
     * return if the user name and password is exist in db
     * @return - true if user exist else false
     */
    public boolean usernameAndPasswordExists()
    {
        return MySql.usernameAndPasswordExists(username, password);
    }

    /**
     * check if username exist in data
     * @return - return true if yes, else return false
     */
    public boolean usernameExists()
    {
        return MySql.usernameExists(username);
    }

    /**
     * insert user to db
     */
    public void insertLogIn() {
        MySql.insertLogIn(username, password, canReport);
    }

    /**
     * check if the user name has the permission to report
     * @return - return true if has permission else false
     */
    public boolean canReport() {
        return MySql.canReport(username);
    }

    /**
     * block the permission of specific user
     */
    public void blockFromPermission() {
        MySql.blockFromPermission(username);
    }

    /**
     * set username
     * @param username - user name
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * set password
     * @param password - password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * set can report
     * @param canReport -can report
     */
    public void setCanReport(int canReport) {
        this.canReport = canReport;
    }
}
