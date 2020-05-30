package com.asl.pms.viewmodels;

public interface DrugStock {

    Long getId();

    String getName();

    String getDosageForm();

    String getGeneric();

    String getStrength();

    String getCompany();

    String getPrice();
    
    String getUom();
    
    String getStock();

    String getLocation();
    
    String getRack();
    
    String getSafetyMargin();
    
    String getModifyTime();
}
