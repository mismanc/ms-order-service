package com.order.service.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class SodaOrder extends BaseEntity{


    private String customerRef;
    private OrderStatusEnum orderStatus = OrderStatusEnum.NEW;
    private String orderStatusCallbackUrl;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "sodaOrder", cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private Set<SodaOrderLine> sodaOrderLines;

    @Builder
    public SodaOrder(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String customerRef, Customer customer,
                     Set<SodaOrderLine> sodaOrderLines, OrderStatusEnum orderStatus,
                     String orderStatusCallbackUrl) {
        super(id, version, createdDate, lastModifiedDate);
        this.customerRef = customerRef;
        this.customer = customer;
        this.sodaOrderLines = sodaOrderLines;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
    }

}
