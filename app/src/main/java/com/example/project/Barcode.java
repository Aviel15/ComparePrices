package com.example.project;

/**
 * Object of Barcode
 */
public class Barcode {
    //properties
    private String barcode;

    /**
     * constructor, insert barcode
     * @param barcode - the barcode number
     * @param product_name - the product name
     */
    public Barcode(String barcode, String product_name)
    {
        this.barcode = barcode;
        MySql.insertBarcode(barcode, product_name);
    }

    /**
     * constructor
     */
    public Barcode(){
        this.barcode = barcode;
    }

    public boolean barcodeExists(String barcode) {
        return MySql.barcodeExist(barcode);
    }

    /**
     * return the product name of barcode in param from the data base
     * @return - return the product name
     */
    public String getProductInBarcode() {
        return MySql.getProductInBarcode(barcode);
    }
}
