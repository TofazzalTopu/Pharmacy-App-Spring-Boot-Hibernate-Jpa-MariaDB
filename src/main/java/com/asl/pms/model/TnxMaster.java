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
@Table(name = "tnx_master")

public class TnxMaster {

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Getter
	@Setter
	private String tnxType;
	
	@Getter
	@Setter
	private String tnxMode;

	@Getter
	@Setter
	private String refId;

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
	private LocalDateTime modifyTime;

	@Getter
	@Setter
	@Transient
	private List<TnxDetails> tnxDetails;

	
}
