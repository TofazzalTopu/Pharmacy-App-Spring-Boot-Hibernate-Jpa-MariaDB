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
@Table(name = "purchase_master")

public class PurchaseMaster {

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Getter
	@Setter
	private String purchaseNo;
	
	@Getter
	@Setter
	private String suplierName;

	@Getter
	@Setter
	private String spInvId;

	@Getter
	@Setter
	private String invDate;

	@Getter
	@Setter
	private String totalAmount;

	@Getter
	@Setter
	private String netPayable;

	@Getter
	@Setter
	private String totalVAT;

	@Getter
	@Setter
	private String totalDiscount;

	@Getter
	@Setter
	private String createdBy;

	@Getter
	@Setter
	private LocalDateTime createTime;

	@Getter
	@Setter
	private String modifydBy;
	

	@Getter
	@Setter
	private String returnFlag;

	@Getter
	@Setter
	private LocalDateTime modifyTime;

	@Getter
	@Setter
	@Transient
	private List<PurchaseDetails> purchaseDetails;

	@Override
	public String toString() {
		return "PurchaseMaster [id=" + id + ", purchaseNo=" + purchaseNo + ", suplierName=" + suplierName + ", spInvId="
				+ spInvId + ", invDate=" + invDate + ", totalAmount=" + totalAmount + ", netPayable=" + netPayable
				+ ", totalVAT=" + totalVAT + ", totalDiscount=" + totalDiscount + ", createdBy=" + createdBy
				+ ", createTime=" + createTime + ", modifydBy=" + modifydBy + ", modifyTime=" + modifyTime
				+ ", purchaseDetails=" + purchaseDetails + "]";
	}

	
}
