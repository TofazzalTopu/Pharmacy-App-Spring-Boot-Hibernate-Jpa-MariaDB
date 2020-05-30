package com.asl.pms.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sales_details")
public class SalesDetails {

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Drug drug;

	@Getter
	@Setter
	@Transient
	private String drugId;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private SalesMaster salesMaster;

	@Getter
	@Setter
	private String salesQty;

	@Getter
	@Setter
	private double salesAmount;

}
