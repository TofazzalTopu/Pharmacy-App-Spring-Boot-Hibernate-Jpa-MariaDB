package com.asl.pms.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "purchase_return")
public class PurchaseReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private PurchaseMaster purchaseMaster;

    private String createdBy;

    private LocalDateTime createTime;

    @Transient
    private List<PurchaseReturnDetails> purchaseReturnDetails;


}
