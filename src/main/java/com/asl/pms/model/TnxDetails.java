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
@Table(name = "tnx_details")
public class TnxDetails {

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
	private TnxMaster tnxMaster;

	@Getter
	@Setter
	private double totalQty;

	@Getter
	@Setter
	private String refId;

}
