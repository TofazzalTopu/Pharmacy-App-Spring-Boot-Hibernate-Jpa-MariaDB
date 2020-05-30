package com.asl.pms.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_details")
public class OrderDetails {
	

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
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)	
	private OrderMaster orderMaster;

	@Getter
	@Setter
	private String qty;

	@Getter
	@Setter
	private String uom;

	@Getter
	@Setter
	private double total;

	@Override
	public String toString() {
		return "OrderDetails [id=" + id + ", drug=" + drug.getName() + ", qty=" + qty + ", uom=" + uom + ", total=" + total + "]";
	}
	

}
