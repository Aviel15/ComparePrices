package com.example.project;

import android.location.Location;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * SQL class - contain a lot of data about the app
 */
public class MySql {
    //properties
    private static final String user = "Aviel";
    private static final String pass = "avielSQL123";
    private static final String dbname = "ComparePrices";
    private static final String ip = "84.228.125.117";
    private static final String TAG ="AVIEL";
    private static final String BARCODE = "barcode_product_table";
    private static final String USER_NAME = "users_table";
    private static final String SUPER_MARKET = "supermarket_table";
    private static final String PRODUCT = "product_table";
    private static final String BASKET_LIST = "basket_list";
    private static final String NOTIFICATIONS = "NOTIFICATION";
    private static final String REPORT = "report_table";
    public static final float DISTANCE_TO_DEGREE = (float) 0.08499;        //5km - 0.04499
    private static Connection connection=null;


    /** connect to DB
     */
    public static void connect() {
        boolean success= true;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectionUrl = "jdbc:jtds:sqlserver://" + ip + ";databaseName="
                    + dbname + ";user=" + user + ";password=" + pass + ";";
            Log.d(TAG, "Connection url="+ connectionUrl);
            connection = DriverManager.getConnection(connectionUrl);
        } catch (Exception se) {
            Log.e(TAG, se.getMessage());
            success = false;
        }
        Log.d(TAG, "connection success " + success);
    }

    /** disconnect from DB
     * @return  - boolean success
     */
    public static boolean disconnect(){
        boolean success = true;
        if (connection!=null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                success=false;
            }
        }
        return success;
    }


    /** execute sql queries insert and update
     * @param   sqlString  - the query.
     * @return  num of affected (updates or inserted) rows
     */
    public static int executeSql(String sqlString) {
        int num = -200;
        try {
            Statement stmt = connection.createStatement();
            Log.d(TAG, "sqlSTRING=" + sqlString);
            num = stmt.executeUpdate(sqlString);
            Log.d(TAG, "executeSql: num=" + num);
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }
        return num;
    }

    /** sql query to retrieve data from DB
     * @param   sqlString  - the query.
     * @return  resultSet object, holds the queries result lines.
     */
    public static ResultSet getResultSet(String sqlString) {            // Create a variable for the connection string.
        ResultSet rs=null;
        try {
            Statement stmt = connection.createStatement();
            rs = stmt.executeQuery(sqlString);
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }
        return  rs;
    }


    /**
     *  create table for barcodes
     */
    public static void createBarcodeTable()
    {
        int n;
        String str="CREATE TABLE " + BARCODE + "(" +
                "barcode VARCHAR(255)," +
                "product_name VARCHAR(255)" +
                ");";
        n = executeSql(str);
        if (n==0) {
            Log.d(TAG, "Create table did not succeed: " + str);
        }
    }

    /** Sql find if a barcode exists in the barcode table
     * @param  barcode - String
     * @return true is exists, false - if not.
     */
    public static boolean barcodeExists(String barcode) throws SQLException {
        String retrieve="SELECT * FROM " + BARCODE + " WHERE barcode='"+barcode+"'";

        ResultSet rs = getResultSet(retrieve);
        return rs.next();
    }

    /**
     * sql query to insert new barcode to DB
     * @param barcode - barcode
     * @param product_name - product name
     */
    public static void insertBarcode(String barcode, String product_name) {
        boolean success = true;
        try {
            if (!barcodeExists(barcode)) {
                String insert = "INSERT INTO "+BARCODE+" Values('" +
                        barcode + "','" +
                        product_name + "');";
                int n = executeSql(insert);
                if (n == 0) {
                    Log.d(TAG, "Insert User did not succeed: " + insert);
                    success = false;
                }
                Log.d("TAG123", ""+success);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * songExists: Sql find if a barcode exists in the Songs table
     * @param  barcode - String
     * @return true is exists, false - if not.
     */
    public static boolean barcodeExist(String barcode) {
        String retrieve="SELECT * FROM " + BARCODE + " WHERE barcode = '"+barcode+"'";
        Log.d("retrieve12", retrieve);

        ResultSet rs = getResultSet(retrieve);
        try {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * sql query to remove barcode from DB
     * @param   barcode  - int barcode
     * @return  success - boolean
     */
    public static boolean removeBarcode(String barcode)
    {
        boolean success = true;
        String update= "DELETE FROM "+BARCODE+" WHERE barcode='"+barcode+"';";
        int n= executeSql(update);
        if (n==0) {
            Log.d(TAG, "Remove barcode did not succeed: " + update);
            success = false;
        }
        return  success;
    }

    /**
     * remove all barcode
     * @return - true if success , else false
     */
    public static boolean removeAllBarcodes()
    {
        boolean success = true;
        String update= "DELETE FROM "+BARCODE;
        int n= executeSql(update);
        if (n==0) {
            Log.d(TAG, "Remove barcode did not succeed: " + update);
            success = false;
        }
        return  success;
    }


    /**
     * get all barcodes
     * @return ArrayList of barcodes (String).
     */
    public static ArrayList<String> getAllBarcodes() {
        String retrieve= "SELECT * FROM "+BARCODE+" ORDER BY product_name, barcode";
        ResultSet rs = getResultSet(retrieve);
        ArrayList<String> result = new ArrayList<>();
        try {
            while(rs.next()) {
                String song=rs.getString("barcode")+","
                        +rs.getString("product_name");
                result.add(song);
            }
        } catch (Exception e) {
            Log.d(TAG, "Select barcodes did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * sql query to retrieve user data from DB
     * @param barcode - specific barcode
     * @return ArrayList of barcodes (String).
     */
    public static String getProductInBarcode(String barcode) {
        String retrieve="SELECT * FROM "+BARCODE+" WHERE barcode='" + barcode + "' ORDER BY product_name";
        ResultSet rs = getResultSet(retrieve);
        String product_mame = null;
        try {
            while(rs.next()) {
                product_mame=rs.getString("product_name");
            }
        } catch (Exception e) {
            Log.d(TAG, "Select barcodes did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return product_mame;
    }





    /**
     * create table for super markets
     */
    public static void createSuperMarketTable()
    {
        int n;
        String str="CREATE TABLE " + SUPER_MARKET + "(" +
                "name VARCHAR(255)," +
                "latitude FLOAT," +
                "longitude FLOAT," +
                "icon INT" +
                ");";
        n = executeSql(str);
        if (n==0) {
            Log.d(TAG, "Create table did not succeed: " + str);
        }
    }

    /**
     * superExists: Sql find if a supermarket exists in the Supermarket table
     * @param latitude - latitude
     * @param longitude - longitude
     * @return true is exists, false - if not.
     */
    public static boolean superExists(double latitude, double longitude)  {
        String retrieve="SELECT * FROM " + SUPER_MARKET + " WHERE latitude="+latitude+" AND longitude=" + longitude;

        ResultSet rs = getResultSet(retrieve);
        try {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * sql query to insert new user to DB
     * @param name - super name
     * @param latitude - latitude
     * @param longitude - longitude
     * @param icon - icon image
     */
    public static void insertSuper(String name, double latitude, double longitude, int icon) {
        boolean success = true;
        if (!(superExists(latitude, longitude))) {
            String insert = "INSERT INTO "+SUPER_MARKET+" Values('" +
                    name + "'," +
                    latitude + "," +
                    longitude + "," +
                    icon + "" +
                    ");";

            Log.d("TAG1235", ""+latitude);
            int n = executeSql(insert);
            if (n == 0) {
                Log.d(TAG, "Insert User did not succeed: " + insert);
                success = false;
            }
            Log.d("TAG1234", ""+success + " " + latitude);
        }
    }

    /**
     * return all super markets from super market table
     * @return - all super markets from super market table
     */
    public static ArrayList<SuperMarket> getAllSuperMarkets() {
        String retrieve= "SELECT * FROM "+SUPER_MARKET+" ORDER BY icon, longitude, latitude, name";
        ResultSet rs = getResultSet(retrieve);
        ArrayList<SuperMarket> result = new ArrayList<>();
        try {
            while(rs.next()) {
                SuperMarket superMarket = new SuperMarket(rs.getString("name"), rs.getDouble("latitude"), rs.getDouble("longitude"), rs.getInt("icon"));
                result.add(superMarket);
            }
        } catch (Exception e) {
            Log.d(TAG, "Select supermarkets did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * get all super markets in radius of 5 km
     * @param myLocation - my current location
     * @return - all super markets in radius of 5 km
     */
    public static ArrayList<SuperMarket> getSuperMarketsInRadius(Location myLocation) {
        String retrieve= "SELECT * FROM " + SUPER_MARKET + " WHERE latitude > " + myLocation.getLatitude() + " - " + DISTANCE_TO_DEGREE + " AND latitude < " + myLocation.getLongitude() + " + " + DISTANCE_TO_DEGREE + " AND longitude > " + myLocation.getLongitude() + " - " + DISTANCE_TO_DEGREE + " AND longitude < " + myLocation.getLongitude() + " + " + DISTANCE_TO_DEGREE;
        ResultSet rs = getResultSet(retrieve);
        ArrayList<SuperMarket> result = new ArrayList<>();
        try {
            while(rs.next()) {
                SuperMarket superMarket = new SuperMarket(rs.getString("name"), rs.getDouble("latitude"), rs.getDouble("longitude"), rs.getInt("icon"));
                result.add(superMarket);
            }
        } catch (Exception e) {
            Log.d(TAG, "Select supermarkets did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * get all super markets by location
     * @param latitude - latitude
     * @param longitude - longitude
     * @return - get all super markets by location
     */
    public static SuperMarket getSuperMarketByLocation(double latitude, double longitude) {
        String retrieve= "SELECT * FROM " + SUPER_MARKET + " WHERE latitude = " + latitude + " AND longitude = " + longitude;
        ResultSet rs = getResultSet(retrieve);
        try {
            if(rs.next()) {
                SuperMarket superMarket = new SuperMarket(rs.getString("name"), rs.getDouble("latitude"), rs.getDouble("longitude"), rs.getInt("icon"));
                return superMarket;
            }
        } catch (Exception e) {
            Log.d(TAG, "Select supermarkets did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return null;
    }


    /**
     * remove all super markets
     * @return - return true if success, else false
     */
    public static boolean removeAllSuperMarkets()
    {
        boolean success = true;
        String update= "DELETE FROM "+ SUPER_MARKET;
        int n= executeSql(update);
        if (n==0) {
            Log.d(TAG, "Remove barcode did not succeed: " + update);
            success = false;
        }
        return  success;
    }








    /**
     *  create table for user
     */
    public static void createUserTable()
    {
        int n;
        String str= "CREATE TABLE " + USER_NAME + "(" +
                "username VARCHAR(255)," +
                "password VARCHAR(255)," +
                "canReport INT," +
                "latitude FLOAT," +
                "longitude FLOAT" +
                ");";
        n = executeSql(str);
        if (n==0) {
            Log.d(TAG, "Create table did not succeed: " + str);
        }
    }

    /**
     * check if user exist
     * @param username - user name
     * @return - true if exist, else false
     */
    public static boolean usernameExists(String username) {
        String retrieve="SELECT * FROM " + USER_NAME + " WHERE username = '"+username+"'";
        Log.d("retrieve12", retrieve);

        ResultSet rs = getResultSet(retrieve);
        try {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * check if user name and password exist in user table
     * @param username - user name
     * @param password - password
     * @return - if exist return true, else false
     */
    public static boolean usernameAndPasswordExists(String username, String password) {
        String retrieve = "SELECT * FROM "+ USER_NAME +" WHERE username='" + username + "' AND password = '" + password + "'";
        Log.d("retrieve12", retrieve);

        ResultSet rs = getResultSet(retrieve);
        try {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * return true if to user has permission to report, else false
     * @param username - user name
     * @return - true if to user has permission to report, else false
     */
    public static boolean canReport(String username) {
        String retrieve = "SELECT canReport FROM "+ USER_NAME +" WHERE username='" + username + "'";
        ResultSet rs = getResultSet(retrieve);
        int canReport = 0;
        try {
            if(rs.next())
                canReport = rs.getInt("canReport");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return canReport == 1;
    }

    /**
     * block specific user from permission to report
     * @param username - user name
     */
    public static void blockFromPermission(String username) {
        boolean success = true;
        String retrieve = "UPDATE " + USER_NAME + " SET canReport = 0 " + "WHERE username = '" + username + "';";
        int n= executeSql(retrieve);
        if (n==0) {
            Log.d(TAG, "block did not succeed: " + retrieve);
        }
    }

    /**
     * insert to user table
     * @param username - user name
     * @param password - password
     * @param canReport - if can report, 1 is true, 0 is false
     */
    public static void insertLogIn(String username, String password, int canReport) {
        boolean success = true;
        if (!usernameExists(username)) {
            String insert = "INSERT INTO "+USER_NAME+" (username, password, canReport) Values('" +
                    username + "','" +
                    password + "'," +
                    canReport + ");";

            Log.d("TAG1235", ""+insert);
            int n = executeSql(insert);
            if (n == 0) {
                Log.d(TAG, "Insert User did not succeed: " + insert);
                success = false;
            }
            Log.d("TAG123", ""+success);
        }
    }

    /**
     * return all users
     * @return - all users
     */
    public static ArrayList<String> getAllUsers() {
        String retrieve= "SELECT * FROM "+USER_NAME+" ORDER BY username, password";
        ResultSet rs = getResultSet(retrieve);
        ArrayList<String> result = new ArrayList<>();
        try {
            while(rs.next()) {
                String user = rs.getString("username")+","
                        +rs.getString("password")
                        +","+rs.getInt("canReport");
                result.add(user);
            }
        } catch (Exception e) {
            Log.d(TAG, "Select barcodes did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * remove all users
     * @return - true is success, else false
     */
    public static boolean removeAllUsers()
    {
        boolean success = true;
        String update= "DELETE FROM "+ USER_NAME;
        int n= executeSql(update);
        if (n==0) {
            Log.d(TAG, "Remove barcode did not succeed: " + update);
            success = false;
        }
        return  success;
    }







    /**
     *  create table for products
     */
    public static void createProductTable()
    {
        int n ;
        String str="CREATE TABLE " + PRODUCT + "(" +
                "productName VARCHAR(255)," +
                "whoAdd VARCHAR(255)," +
                "price FLOAT," +
                "latitude FLOAT," +
                "longitude FLOAT," +
                "address_super_name VARCHAR(255)" +
                ");";
        n = executeSql(str);
        if (n==0) {
            Log.d(TAG, "Create table did not succeed: " + str);
        }
    }

    /**
     * return true if product exist in product table
     * @param productName - product name
     * @return - true if product exist in product table, else false
     * @throws SQLException - throw SQL exception
     */
    public static boolean productExist(String productName) throws SQLException {
        String retrieve="SELECT * FROM " + PRODUCT + " WHERE productName = '"+productName+"'";
        Log.d("retrieve12", retrieve);
        ResultSet rs = getResultSet(retrieve);
        return rs.next();
    }

    /**
     * check if product exist in super
     * @param latitude - latitude
     * @param longitude - longitude
     * @param productName - product name
     * @return - if exist return true, else return false
     * @throws SQLException - throw SQL exception
     */
    public static boolean productExistByLocation(double latitude, double longitude, String productName) throws SQLException {
        String retrieve="SELECT * FROM " + PRODUCT + " WHERE latitude = "+latitude+" AND longitude = " + longitude + " AND productName = '" + productName +"'";
        ResultSet rs = getResultSet(retrieve);
        return rs.next();
    }

    /**
     * check if product exist in super
     * @param productName - product name
     * @return - if exist return true, else return false
     * @throws SQLException - throw SQL exception
     */
    public static boolean productExistByAddressName(String productName) throws SQLException {
        String retrieve="SELECT * FROM " + PRODUCT + " WHERE productName = '" + productName +"'";
        ResultSet rs = getResultSet(retrieve);
        return rs.next();
    }

    /**
     * update product
     * @param location - address location
     * @param productName - product name
     * @param price - price
     */
    public static void updateProduct(String location, String productName, double price) {
        boolean success = true;
        String retrieve="UPDATE " + PRODUCT + " SET price = " + price +" WHERE productName = '" + productName + "' AND address_super_name = '" + location + "'";
        int n= executeSql(retrieve);
    }


    /**
     * insert product to db
     * @param product_name - product name
     * @param whoAdd - who add the product
     * @param price - price
     * @param latitude - latitude
     * @param longitude - longitude
     * @param address_super_name - address of super market
     */
    public static void insertProduct(String product_name, String whoAdd, double price, double latitude, double longitude, String address_super_name) {
        boolean success = true;
        if (!usernameExists(product_name)) {
            String insert = "INSERT INTO "+PRODUCT+" (productName, whoAdd, price, latitude, longitude, address_super_name) Values('" +
                    product_name + "','" +
                    whoAdd + "'," +
                    price + "," +
                    latitude + "," +
                    longitude + ",'" +
                    address_super_name + "');";

            Log.d("TAG173123", ""+latitude + " " + longitude);
            int n = executeSql(insert);
            if (n == 0) {
                Log.d(TAG, "Insert User did not succeed: " + insert);
                success = false;
            }
            Log.d("TAG173", ""+success);
        }
    }

    /**
     * return all products
     * @return - all products
     */
    public static ArrayList<String> getAllProducts() {
        String retrieve= "SELECT * FROM "+PRODUCT+" ORDER BY productName, whoAdd, price, latitude, longitude";
        ResultSet rs = getResultSet(retrieve);

        ArrayList<String> result = new ArrayList<>();
        try {
            while(rs.next()) {
                String productName=rs.getString("productName");
                if(!result.contains(productName))
                    result.add(productName);
                Log.d("TAG173123", ""+rs.getDouble("latitude") + " " + rs.getDouble("longitude"));
            }
        } catch (Exception e) {
            Log.d(TAG, "Select barcodes did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * return who add the specific product in specific super address
     * @param productName - product name
     * @param address_super_name - super address
     * @return - who add the specific product in specific super address
     */
    public static String getWhoAdd(String productName, String address_super_name) {
        String retrieve= "SELECT * FROM "+PRODUCT+" WHERE productName = '" + productName + "' AND address_super_name = '" + address_super_name + "'";
        ResultSet rs = getResultSet(retrieve);
        String whoAdd = "";
        try {
            if(rs.next()) {
                 whoAdd = rs.getString("whoAdd");
            }
        } catch (Exception e) {
            Log.d(TAG, "Select barcodes did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return whoAdd;
    }


    /**
     * return all prices with specific product name
     * @param productName - product name
     * @param latitude - latitude
     * @param longitude - longitude
     * @return - all prices with specific product name
     */
    public static double getPriceOfProductInSuper(String productName, double latitude, double longitude) {
        String retrieve= "SELECT * FROM "+PRODUCT+" WHERE productName = '" + productName + "' AND latitude = " + latitude + " AND longitude = " + longitude;
        ResultSet rs = getResultSet(retrieve);

        try {
            if(rs.next())
                return rs.getDouble("price");
        } catch (Exception e) {
            Log.d(TAG, "Select barcodes did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * return get address name via price
     * @param price - price
     * @return - get address name via price
     */
    public static String getAddressNamePrice(double price) {
        String retrieve= "SELECT * FROM "+PRODUCT+" WHERE price = " + price;
        ResultSet rs = getResultSet(retrieve);

        String address_name;
        try {
            if(rs.next()) {
                address_name = rs.getString("address_super_name");
                return address_name;
            }
        } catch (Exception e) {
            Log.d(TAG, "Select barcodes did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * return get address name via location
     * @param latitude - latitude
     * @param longitude - longitude
     * @return - get address name via location
     */
    public static String getAddressNameLocationByLatLng(double latitude, double longitude) {
        String retrieve= "SELECT * FROM "+PRODUCT+" WHERE latitude = " + latitude + " AND longitude = " + longitude;
        ResultSet rs = getResultSet(retrieve);

        String address_name;
        try {
            if(rs.next()) {
                address_name = rs.getString("address_super_name");
                return address_name;
            }
        } catch (Exception e) {
            Log.d(TAG, "Select barcodes did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * return all products specific price
     * @param price - price
     * @return - return all products specific price
     */
    public static String getProductWithPrice(double price) {
        String retrieve= "SELECT * FROM "+PRODUCT+" WHERE price = " + price;
        ResultSet rs = getResultSet(retrieve);

        String product_name = null;
        try {
            while(rs.next()) {
                product_name = rs.getString("productName");
            }
        } catch (Exception e) {
            Log.d(TAG, "Select barcodes did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return product_name;
    }

    /**
     * return price in specific location with specific product name
     * @param productName - product name
     * @param location - address location
     * @return - price in specific location with specific product name
     */
    public static double getPriceByProduct(String productName, String location) {
        String retrieve= "SELECT * FROM "+PRODUCT+" WHERE productName = '" + productName + "' AND address_super_name = '" + location + "'";
        ResultSet rs = getResultSet(retrieve);

        double price = 0;
        try {
            if(rs.next())
                price = rs.getDouble("price");
        } catch (Exception e) {
            Log.d(TAG, "Select barcodes did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return price;
    }

    /**
     * remove all product
     * @return - if success return true, else false
     */
    public static boolean removeAllProducts()
    {
        boolean success = true;
        String update= "DELETE FROM "+ PRODUCT;
        int n= executeSql(update);
        if (n==0) {
            Log.d(TAG, "Remove barcode did not succeed: " + update);
            success = false;
        }
        return  success;
    }






    /**
     *  create table for basket list
     */
    public static void createBasketListTable()
    {
        int n;
        String str="CREATE TABLE " + BASKET_LIST + "(" +
                "productName VARCHAR(255)," +
                "amount INT" +
                ");";
        n = executeSql(str);
        if (n==0) {
            Log.d(TAG, "Create table did not succeed: " + str);
        }
    }

    /**
     * return true if specific product exist in basket, else false
     * @param productName - product name
     * @return - true if specific product exist in basket, else false
     * @throws SQLException - throw SQL exception
     */
    public static boolean basketListProductExist(String productName) throws SQLException {
        String retrieve="SELECT * FROM " + BASKET_LIST + " WHERE productName='"+productName+"'";

        ResultSet rs = getResultSet(retrieve);
        return rs.next();
    }


    /**
     * insert product to basket list table
     * @param productName - product name
     */
    public static void insertProductNameBasketList(String productName) {
        boolean success = true;
        try {
            if (!basketListProductExist(productName)) {
                String insert = "INSERT INTO "+BASKET_LIST+" (productName) Values('" +
                        productName + "');";

                Log.d("TAG1735", ""+insert);
                int n = executeSql(insert);
                if (n == 0) {
                    Log.d(TAG, "Insert User did not succeed: " + insert);
                    success = false;
                }
                Log.d("TAG173", ""+success);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * update basket list table
     * @param productName - product name
     * @return - true if success, else false
     */
    public static boolean updateBasketList(String productName) {
        boolean success = true;
        try {
            if (!basketListProductExist(productName)) {
                String insert = "UPDATE "+BASKET_LIST+" (productName) Values('" +
                        productName + "');";

                Log.d("TAG1735", ""+insert);
                int n = executeSql(insert);
                if (n == 0) {
                    Log.d(TAG, "Insert User did not succeed: " + insert);
                    success = false;
                }
                Log.d("TAG173", ""+success);
                return success;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * insert product to basket list table
     * @param productName - product name
     * @param amount - amount
     */
    public static void insertBasketList(String productName, int amount) {
        boolean success = true;
        try {
            if (basketListProductExist(productName)) {
                String insert = "UPDATE " + BASKET_LIST + " SET amount = " + amount + " WHERE productName = '" + productName + "'";

                Log.d("TAG1735", "" + insert);
                int n = executeSql(insert);
                if (n == 0) {
                    Log.d(TAG, "Insert User did not succeed: " + insert);
                    success = false;
                }
                Log.d("TAG173", "" + success);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * return all products in basket list
     * @return - all products in basket list
     */
    public static ArrayList<String> getAllBasketListName() {
        String retrieve= "SELECT * FROM "+BASKET_LIST;
        ResultSet rs = getResultSet(retrieve);
        ArrayList<String> result = new ArrayList<>();
        try {
            while(rs.next()) {
                String productName=rs.getString("productName");
                result.add(productName);
            }
        } catch (Exception e) {
            Log.d(TAG, "Select barcodes did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * return all basket list, product name and amount
     * @return - all basket list, product name and amount
     */
    public static ArrayList<String> getAllBasketList() {
        String retrieve= "SELECT * FROM "+BASKET_LIST;
        ResultSet rs = getResultSet(retrieve);
        ArrayList<String> result = new ArrayList<>();
        try {
            while(rs.next()) {
                String productName=rs.getString("productName")+rs.getInt("amount");
                result.add(productName);
            }
        } catch (Exception e) {
            Log.d(TAG, "Select barcodes did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * return all amounts in basket list
     * @return - all amounts in basket list
     */
    public static ArrayList<Integer> getAllBasketListAmount() {
        String retrieve= "SELECT * FROM "+BASKET_LIST;
        ResultSet rs = getResultSet(retrieve);
        ArrayList<Integer> result = new ArrayList<>();
        try {
            while(rs.next()) {
                int amount=rs.getInt("amount");
                result.add(amount);
                Log.d("12221212", "Select barcodes did not succeed: " + retrieve + "  " + result);
            }
        } catch (Exception e) {
            Log.d("12221212", "Select barcodes did not succeed: " + retrieve + "  " + result);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * remove all basket list table
     */
    public static void removeAllBasketList()
    {
        boolean success = true;
        String update= "DELETE FROM "+ BASKET_LIST;
        int n= executeSql(update);
    }

    /**
     * remove only specific products from basket list
     * @param product_name - product name
     */
    public static void removeSpecificProductBasketList(String product_name)
    {
        boolean success = true;
        String update= "DELETE FROM "+ BASKET_LIST + " WHERE productName = '" + product_name + "'";
        int n= executeSql(update);
    }


    /**
     * create notification table
     */
    public static void createNotificationTable()
    {
        int n;
        String str="CREATE TABLE " + NOTIFICATIONS + "(" +
                "productName VARCHAR(255)," +
                "price FLOAT," +
                "row_num INT" +
                ");";
        n = executeSql(str);
        if (n==0) {
            Log.d(TAG, "Create table did not succeed: " + str);
        }
    }

    /**
     * insert notification to notification table
     * @param productName - product name
     * @param price - price
     * @param row_num - row number
     * @return - true is success, else false
     */
    public static boolean insertNotifications(String productName, double price, int row_num) {
        boolean success = true;
        String insert = "INSERT INTO "+NOTIFICATIONS+" Values('" +
                productName + "'," +
                price + "," +
                row_num + ");";

        Log.d("TAG1735", ""+insert);
        int n = executeSql(insert);
        if (n == 0) {
            Log.d(TAG, "Insert User did not succeed: " + insert);
            success = false;
        }
        Log.d("TAG173", ""+success);
        return success;
    }

    /**
     * return all notifications
     * @return - all notifications
     */
    public static ArrayList<String> getAllNotifications() {
        String retrieve= "SELECT * FROM "+NOTIFICATIONS;
        ResultSet rs = getResultSet(retrieve);
        ArrayList<String> result = new ArrayList<>();
        try {
            while(rs.next()) {
                String song = rs.getString("productName");
                result.add(song);
            }
        } catch (Exception e) {
            Log.d(TAG, "Select barcodes did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * get product name in specific row from notification table
     * @param specific_row - specific row number
     * @return - product name in specific row from notification table
     */
    public static String getProductNameFromSpecificRowNotifications(int specific_row) {
        String retrieve= "SELECT * FROM " + NOTIFICATIONS + " WHERE row_num = " + specific_row;
        ResultSet rs = getResultSet(retrieve);
        String product_name = null;
        try {
            if(rs.next()) {
                product_name = rs.getString("productName");
            }
        } catch (Exception e) {
            Log.d(TAG, "Select barcodes did not succeed: " + retrieve);
            e.printStackTrace();
        }
        return product_name;
    }

    /**
     * return the length of notification table
     * @return - the length of notification table
     */
    public static int checkLengthNotifications() {
        int rowCount = 0;
        String str="SELECT COUNT(*) FROM " + NOTIFICATIONS;

        ResultSet rs = getResultSet(str);
        try {
            if (rs.next()) {
                rowCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowCount;
    }


    /**
     * drop super market table
     */
    public static void dropTableSuper()
    {
        int n = 0;
        String str="DROP TABLE "+PRODUCT;
    }


    /**
     *  create table for reports
     */
    public static void createReportTable()
    {
        int n;
        String str="CREATE TABLE " + REPORT + "(" +
                "whoAdd VARCHAR(255)," +
                "whoReport VARCHAR(255)," +
                "PRIMARY KEY(whoAdd, whoReport)" +
                ");";
        n = executeSql(str);
        if (n==0) {
            Log.d(TAG, "Create table did not succeed: " + str);
        }
    }

    /**
     * insert reports to report table
     * @param whoAdd - who add the product, against who the report
     * @param whoReport - who report
     * @return - true if success, else false
     */
    public static boolean insertReports(String whoAdd, String whoReport) {
        boolean success = true;
        String insert = "INSERT INTO "+REPORT+" Values('" +
                whoAdd + "','" +
                whoReport + "');";

        Log.d("TAG1735", ""+insert);
        int n = executeSql(insert);
        if (n == 0) {
            Log.d(TAG, "Insert User did not succeed: " + insert);
            success = false;
        }
        Log.d("TAG173", ""+success);
        return success;
    }

    /**
     * return all reports
     * @return - all reports
     */
    public static ArrayList<String> getAllReports() {
        String retrieve= "SELECT * FROM "+REPORT;
        ResultSet rs = getResultSet(retrieve);
        ArrayList<String> result = new ArrayList<>();
        try {
            while(rs.next()) {
                String amount=rs.getString("whoAdd")+" "+rs.getString("whoReport");
                result.add(amount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * count how much reports has against specific user
     * @param whoAdd - user name
     * @return - how much reports has against specific user
     */
    public static int countWhoAdd(String whoAdd) {
        int rowCount = 0;
        boolean success = true;
        String retrieve= "SELECT COUNT(*) FROM " + REPORT + " WHERE whoAdd = '" + whoAdd + "'";
        ResultSet rs = getResultSet(retrieve);
        try {
            if (rs.next()) {
                rowCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowCount;
    }

}