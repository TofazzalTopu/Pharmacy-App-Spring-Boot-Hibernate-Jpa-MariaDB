package com.asl.pms.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "sales_return")
public class SalesReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private SalesMaster salesMaster;

    private String createdBy;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    @Transient
    private List<SalesReturnDetails> salesReturnDetails;
}
