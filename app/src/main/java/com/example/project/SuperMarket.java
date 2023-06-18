package com.example.project;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Object of Supermarket
 */
public class SuperMarket {
    //properties
    private String name;
    private LatLng latlng;
    private double latitude;
    private double longitude;
    private int icon;
    private Product product = new Product();
    private Location location;

    /**
     * constructor
     * @param name - supermarket name
     * @param latitude - latitude
     * @param longitude - longitude
     * @param icon - icon image of supermarket
     */
    public SuperMarket(String name, double latitude, double longitude, int icon)
    {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.latlng = new LatLng(latitude, longitude);
        this.icon = icon;
    }

    /**
     * need to create for superMarketLocation object in findSuperMarkets method
     */
    public SuperMarket() {

    }

    /**
     * find super markets in 5 kilometer radius, check if in these super markets have at least one product in each one
     */
    public void findSuperMarkets(){
        String name_supermarket;
        SuperMarket superMarketLocation = new SuperMarket();
        superMarketLocation.setLocation(ComparisonActivity.getMyLocation());
        for (SuperMarket superMarket : superMarketLocation.getSuperMarketsInRadius()) {
            LatLng location = superMarket.getLocation();
            product.setProductName(ComparisonActivity.getProductName());
            product.setLatitude(location.latitude);
            product.setLongitude(location.longitude);
            double price = product.getPriceOfProductInSuper();


            if(price > 0 && ComparisonActivity.getIndexOfArray() < 5) {                                           //can add only 5 super markets
                name_supermarket = product.getAddressNameLocationByLatLng();         //get the address via location
                product = new Product(product.getProductName(), price, name_supermarket);
                ComparisonActivity.getPriceAndSuperName().add(product);                                          //add product to array list
                int index = ComparisonActivity.getIndexOfArray();               //get index of array to continue check if the index is not more 5                                             //increase the index by one - the size of array list
                index++;
                ComparisonActivity.setIndexOfArray(index);
            }
        }
    }

    /**
     * after get the all supermarkets in radius, will check to which supers has most products available
     */
    public void findSuperMarketsWithMostProducts(){
        ArrayList<SuperMarket> updatePrice = new ArrayList<>();

        int index = 0, max_index = 0;                                                               //to check which super has most product available, index to current super market and max_index to super market with most available products
        double price = 0;
        //run on each super market
        for(int i = 0; i < BasketListActivity.basketListSuperMarkets.size(); i++)                   //run on all supermarkets in 5 km distance
        {
            //run on all products in super market
            for(int j = 0; j < BasketListActivity.productNameArrayList.size(); j++)                 //run on all products in basket list
            {
                try {
                    product.setProductName(BasketListActivity.productNameArrayList.get(j));
                    product.setLatitude(BasketListActivity.basketListSuperMarkets.get(i).getLatitude());
                    product.setLongitude(BasketListActivity.basketListSuperMarkets.get(i).getLongitude());
                    if(product.productExistByLocation())       //check if product exist in specific location - count how much products has to each super market
                        index++;
                    //calculate the sum of buy by (product price * amount)
                    price += (product.getPriceOfProductInSuper() * Double.parseDouble(Objects.requireNonNull(BasketListActivity.getTextEditText(j))));                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            /*
                there are two options to update the price
                1. if number of available products in current super market is most and then even not check the total price - like the first if condition
                2. if number of available products in current super market is equal to another super but the current super is cheaper - like the second if condition
             */
            //check if the current index is bigger the max index
            if(index > max_index) {
                max_index = index;                                          //update the max index
                BasketListActivity.total_price = price;                     //update ehe total price
                updatePrice.clear();
                updatePrice.add(BasketListActivity.basketListSuperMarkets.get(i));       //clear and update the new lowest price
            }
            else if (index == max_index && index != 0 && price < BasketListActivity.total_price) {                   //if have to supermarkets with same available product, will save the lower but one
                BasketListActivity.total_price = price;                      //update ehe total price
                updatePrice.clear();
                updatePrice.add(BasketListActivity.basketListSuperMarkets.get(i));
            }
            index = 0;
            price = 0;
        }
        BasketListActivity.basketListSuperMarkets = new ArrayList<>(updatePrice);          //save the final changes in basket list array list
    }

    /**
     * insert super market to db
     * @param name - super name
     * @param latitude - latitude
     * @param longitude - longitude
     * @param icon - icon image of super
     */
    public void insertSuper(String name, double latitude, double longitude, int icon)
    {
        MySql.insertSuper(name, latitude, longitude, icon);
    }

    /**
     * get super markets
     * @return - all super markets
     */
    public ArrayList<SuperMarket> getAllSuperMarkets() {
        return MySql.getAllSuperMarkets();
    }

    /**
     * get super markets by location
     * @return - all super markets by location
     */
    public SuperMarket getSuperMarketByLocation() {
        return MySql.getSuperMarketByLocation(latitude, longitude);
    }

    /**
     * check if super markets exist in db
     * @return - true if super exist else false
     */
    public boolean superExists() {
        return MySql.superExists(latitude, longitude);
    }

    /**
     * return super market name
     * @return - get super market name
     */
    public String getName() {
        return name;
    }

    /**
     * return super market location
     * @return - get super market location
     */
    public LatLng getLocation() {
        return latlng;
    }

    /**
     * return super market latitude
     * @return - get super market latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * return super market longitude
     * @return - get super market longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * return super market icon
     * @return - get super market icon
     */
    public int getIcon() {
        return icon;
    }

    /**
     * set super market name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * set super market location
     */
    public void setLocation(Location location) {
        this.location = location;
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
     * get supermarkets in radius of 5 km
     * @return - return all super markets that closer than 5 km to my current location
     */
    public ArrayList<SuperMarket> getSuperMarketsInRadius() {
        return MySql.getSuperMarketsInRadius(location);
    }

}
