package com.food.repository;

import com.food.model.Order;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}
