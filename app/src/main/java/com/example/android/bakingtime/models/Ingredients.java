package com.example.android.bakingtime.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HARUN on 8/26/2017.
 */

public class Ingredients {

    @SerializedName("quantity")
    private final double quantity;

    @SerializedName("measure")
    private final String measure;

    @SerializedName("ingredient")
    private final String ingredient;

    public Ingredients(int quantity, String measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }
}
