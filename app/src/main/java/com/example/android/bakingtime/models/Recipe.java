package com.example.android.bakingtime.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by HARUN on 8/26/2017.
 */

public class Recipe implements Parcelable{

    @SerializedName("id")
    private final int id;

    @SerializedName("name")
    private final String name;

    @SerializedName("servings")
    private final int servings;

    @SerializedName("image")
    private final String imageRecipe;

    @SerializedName("ingredients")

    private List<Ingredients> ingredientsList;

    @SerializedName("steps")
    private List<Steps> steps;

    public Recipe(int id, String name, int servings, String imageRecipe) {
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.imageRecipe = imageRecipe;
    }

    private Recipe(Parcel source) {
        this(source.readInt(), source.readString(), source.readInt(), source.readString());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getServings() {
        return servings;
    }

    public String getImageRecipe() {
        return imageRecipe;
    }

    public List<Ingredients> getIngredientsList() {
        return ingredientsList;
    }

    public List<Steps> getSteps() {
        return steps;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(servings);
        dest.writeString(imageRecipe);
        dest.writeList(ingredientsList);
        dest.writeList(steps);
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[0];
        }
    };
}
