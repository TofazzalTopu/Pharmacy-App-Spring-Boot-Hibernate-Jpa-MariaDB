package com.asl.pms.viewmodels;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Drug {

    @SerializedName("Name")
    private String name;

    @SerializedName("Type")
    private String type;

    @SerializedName("Generic")
    private String generic;

    @SerializedName("Strength")
    private String strength;

    @SerializedName("Company")
    private String company;

    @SerializedName("Price")
    private String price;
}
