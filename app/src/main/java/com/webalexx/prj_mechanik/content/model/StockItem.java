package com.webalexx.prj_mechanik.content.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Represents stock item as an object
 */
public class StockItem implements Serializable{
    @SerializedName(value = "ID")
    private long id;
    @SerializedName(value = "QUANTITY")
    private int quantity;
    @SerializedName(value = "PRICE")
    private float price;

    public long getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "StockItem{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
