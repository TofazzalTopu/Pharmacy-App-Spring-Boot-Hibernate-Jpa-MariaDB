package com.asl.pms.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sales_master")

public class SalesMaster {

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Getter
	@Setter
	private String salesNo;

	@Getter
	@Setter
	private String orderId;

	@Getter
	@Setter
	private String salesDate;

	@Getter
	@Setter
	private String totalAmount;

	@Getter
	@Setter
	private String vat;

	@Getter
	@Setter
	private String discount;

	@Getter
	@Setter
	private String netPayable;

	@Getter
	@Setter
	private String paidAmount;

	@Getter
	@Setter
	private String dueAmount;

	@Getter
	@Setter
	private String returnFlag;

	@Getter
	@Setter
	private String createdBy;

	@Getter
	@Setter
	private String modifydBy;

	@Getter
	@Setter
	private LocalDateTime createTime;

	@Getter
	@Setter
	private LocalDateTime modifyTime;

	@Getter
	@Setter
	@Transient
	private List<SalesDetails> salesDetails;

}
