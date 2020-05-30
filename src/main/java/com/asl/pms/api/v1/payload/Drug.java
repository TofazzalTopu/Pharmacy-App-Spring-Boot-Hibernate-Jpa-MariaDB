package com.asl.pms.api.v1.payload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class Drug {

    @NotEmpty
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    private Long dosageForm;

    @NotNull
    private Long generic;

    @Size(max = 255)
    private String strength;

    @NotNull
    private Long company;

    private double price;
    
    @NotNull
    private Long rack;
}
