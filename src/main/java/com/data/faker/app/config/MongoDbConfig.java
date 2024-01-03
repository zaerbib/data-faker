package com.data.faker.app.config;

import com.data.faker.app.repository.DataFlowRepository;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = DataFlowRepository.class)
@Configuration
public class MongoDbConfig extends AbstractMongoClientConfiguration {

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/datafakerdb");
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(false)
                .retryReads(false)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Override
    public String getDatabaseName() {
        return "datafakerdb";
    }
}
