package com.asl.pms.viewmodels;

import com.asl.pms.model.Rack;

public interface IDrug {

    Long getId();

    String getName();

    IDosageForm getDosageForm();

    IGeneric getGeneric();

    String getStrength();

    ICompany getCompany();

    String getPrice();

    ILocation getLocation();
    
    Rack getRack();
}
