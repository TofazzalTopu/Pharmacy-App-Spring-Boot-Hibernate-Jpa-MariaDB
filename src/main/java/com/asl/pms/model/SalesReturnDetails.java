package com.asl.pms.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "sales_return_details")
public @Data
class SalesReturnDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Drug drug;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private SalesReturn salesReturn;

    private String salesQty;

    private double salesAmount;
}
