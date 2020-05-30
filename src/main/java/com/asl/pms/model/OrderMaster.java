package com.asl.pms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_master")
public class OrderMaster {
	
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

   
    @Getter
    @Setter   
    private String prescription1;
    
    @Getter
    @Setter   
    private String prescription2;
    
    @Getter
    @Setter   
    private String prescription3;
    
    
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST)
    @JsonIgnore
    private User customer;

    
    
    @Getter
    @Setter
    @Transient
    private List<OrderDetails> orderDetails;
    
    

 
    @Getter
    @Setter
    private LocalDate deliveryDate;

    @Getter
    @Setter
    private LocalDateTime createTime;
    
    @Getter
    @Setter
    private LocalDateTime modifyTime;

    @Getter
    @Setter
    private boolean recurring;

    
    @Getter
    @Setter
    private Status status;

    @Transient
    @Setter
    private DeliveryState deliveryState;

    @Getter
    @Setter
    private double total;

    public boolean isDelivered() {
        return this.status == Status.DELIVERED ||
                this.status == Status.SUCCESS;
    }

    public DeliveryState getDeliveryState() {
        
        if (isDelivered())
            return DeliveryState.SUCCESS;

        LocalDate today = LocalDate.now();
        if (this.recurring) {
            int month = today.getMonth().getValue();
            this.deliveryDate = this.deliveryDate.withMonth(month);
        }

        Period period = Period.between(
                today, this.deliveryDate);

        int days = period.getDays();
        if (days <= 7 && days > 3)
            return DeliveryState.NOTIFY;
        else if (days <= 3 && days > 1)
            return DeliveryState.WARN;
        else if (days <= 1)
            return DeliveryState.DANGER;

        return DeliveryState.OK;
    }

    public enum DeliveryState {
        OK,
        NOTIFY,
        WARN,
        DANGER,
        SUCCESS
    }

    public enum Status {
        PENDING,
        PROCESSING,
        DELIVERED,
        CANCELLED,
        SUCCESS
    }

   
}
