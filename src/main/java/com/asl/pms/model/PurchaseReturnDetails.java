package com.asl.pms.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Data
@Entity
@Table(name = "purchase_return_details")
public class PurchaseReturnDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Drug drug;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private PurchaseReturn purchaseReturn;

    private String boxQty;

    private String packSize;

    private String bonusQty;

    private String extraQty;

    private double totalQty;

    private double vat;

    private double discount;

    private double tpAmount;

}
