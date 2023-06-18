/*
 *  Copyright 2019 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.order.service.repositories;


import com.order.service.domain.SodaOrder;
import com.order.service.domain.SodaOrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Created by jt on 2019-01-26.
 */
public interface SodaOrderRepository extends JpaRepository<SodaOrder, UUID> {

    Page<SodaOrder> findAllByCustomer_Id(UUID customerId, Pageable pageable);

    Optional<SodaOrder> findByIdAndCustomer_Id(UUID id, UUID customerId);

    List<SodaOrder> findAllByOrderStatus(SodaOrderStatusEnum sodaOrderStatusEnum);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    SodaOrder findOneById(UUID id);
}
