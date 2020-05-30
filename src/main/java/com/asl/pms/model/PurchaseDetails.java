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
@Table(name = "purchase_details")
public class PurchaseDetails {

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
	private PurchaseMaster purchaseMaster;

	@Getter
	@Setter
	private String boxQty;

	@Getter
	@Setter
	private String packSize;

	@Getter
	@Setter
	private String bonusQty;

	@Getter
	@Setter
	private String extraQty;

	@Getter
	@Setter
	private double totalQty;

	@Getter
	@Setter
	private double vat;

	@Getter
	@Setter
	private double discount;

	@Getter
	@Setter
	private double tpAmount;
	
	@Getter
	@Setter
	@Transient
	private double salePrice;

	@Override
	public String toString() {
		return "PurchaseDetails [id=" + id + ", drug=" + drug.getId() + ", drugId=" + drugId + ", purchaseMaster="
				+ purchaseMaster.getId() + ", boxQty=" + boxQty + ", packSize=" + packSize + ", bonusQty=" + bonusQty
				+ ", extraQty=" + extraQty + ", totalQty=" + totalQty + ", vat=" + vat + ", discount=" + discount
				+ ", tpAmount=" + tpAmount + "]";
	}

	
	
	

}
