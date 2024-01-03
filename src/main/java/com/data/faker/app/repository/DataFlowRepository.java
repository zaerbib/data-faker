package com.data.faker.app.repository;

import com.data.faker.app.document.DataFlow;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface DataFlowRepository extends MongoRepository<DataFlow, UUID> {
}
