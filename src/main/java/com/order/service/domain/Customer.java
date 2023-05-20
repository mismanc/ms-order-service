package com.order.service.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Customer extends BaseEntity {

    private String customerName;

    @Column(length = 36, columnDefinition = "varchar")
    private UUID apiKey;

    @OneToMany(mappedBy = "customer")
    private Set<SodaOrder> sodaOrders;


    @Builder
    public Customer(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String customerName,
                    UUID apiKey, Set<SodaOrder> sodaOrders) {
        super(id, version, createdDate, lastModifiedDate);
        this.customerName = customerName;
        this.apiKey = apiKey;
        this.sodaOrders = sodaOrders;
    }
}
