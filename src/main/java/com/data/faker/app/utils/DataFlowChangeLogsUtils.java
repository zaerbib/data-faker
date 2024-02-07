package com.data.faker.app.utils;

import com.data.faker.app.document.DataFlow;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class DataFlowChangeLogsUtils {

    private static final String DATA_FLOW = "dataFlow";
    public static final String UP = "up";
    public static final String DOWN = "down";
    public static final String NORMAL = "normal";
    private static final int BATCH_SIZE = 5000;
    private static final int FIXED_THREAD = 15;
    private static final int CHUNK_SIZE = 5000;

    public static void getDataFlowChange(MongoTemplate mongoTemplate, UnaryOperator<DataFlow> update) {
        changeUnitOnListSimpleVersion(mongoTemplate, update);
    }

    public static void getDataFlowChangeBatchVersion(MongoTemplate mongoTemplate, UnaryOperator<Document> update) {
        changeUnitsOnListBatchVersion(mongoTemplate, update);
    }

    public static void getDataFlowChangeBatchVersion2(MongoTemplate mongoTemplate, UnaryOperator<DataFlow> update) {
        changeUnitsOnListBatchVersion2(mongoTemplate, update);
    }

    public static void getDataFlowChangeBatchVersion3(MongoTemplate mongoTemplate, UnaryOperator<DataFlow> update) {
        changeUnitsOnListAsync(mongoTemplate, update);
    }

    public static void changeUnitOnListSimpleVersion(MongoTemplate mongoTemplate,
                                                     Function<DataFlow, DataFlow> update) {
        List<Object> dataFlows = mongoTemplate.findDistinct("_id", DataFlow.class, Object.class);
        dataFlows.stream()
                .map(id -> mongoTemplate.findById(id, DataFlow.class, DATA_FLOW))
                .filter(Objects::nonNull)
                .map(update)
                .forEach(mongoTemplate::save);
    }

    public static void changeUnitsOnListBatchVersion(MongoTemplate mongoTemplate,
                                                     Function<Document, Document> update) {
        MongoDatabase mongoDatabase = mongoTemplate.getDb();
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(DATA_FLOW);
        FindIterable<Document> dataFlows = mongoCollection.find(Document.class);

        /*dataFlows.map(id -> Objects.requireNonNull(mongoTemplate.findById(id, DataFlow.class)))
                .map(update::apply)
                .batchSize(BATCH_SIZE)
                .iterator()
                .forEachRemaining(mongoTemplate::save);*/
        dataFlows.map(update::apply)
                .batchSize(BATCH_SIZE)
                .iterator()
                .forEachRemaining(item -> mongoTemplate.save(item, "dataFlow"));
    }

    public static void changeUnitsOnListBatchVersion2(MongoTemplate mongoTemplate,
                                                      Function<DataFlow, DataFlow> update) {
        List<DataFlow> dataFlows = mongoTemplate.findAll(DataFlow.class);

//        dataFlows.stream()
//                .map(update)
//                .filter(Objects::nonNull)
//                .forEach(mongoTemplate::save);
        if (!dataFlows.isEmpty()) {
            IntStream.rangeClosed(0, (dataFlows.size() - 1) / CHUNK_SIZE)
                    .mapToObj(cpt -> dataFlows.subList(cpt * CHUNK_SIZE, Math.max((cpt + 1) * CHUNK_SIZE, dataFlows.size())))
                    .map(item -> item.stream()
                            .map(update)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()))
                    .forEach(item -> item.forEach(mongoTemplate::save));
        }
    }

    public static void changeUnitsOnListAsync(MongoTemplate mongoTemplate,
                                              Function<DataFlow, DataFlow> update) {
        List<DataFlow> dataFlows = mongoTemplate.findAll(DataFlow.class);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);
        ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();

        if (!dataFlows.isEmpty()) {
            List<Void> list = dataFlows.stream()
                    .map(update)
                    .map(item ->
                            CompletableFuture
                                    .runAsync(() -> mongoTemplate.save(item), service).whenComplete((t, ex) -> {
                                        if(ex != null) {
                                            failed.incrementAndGet();
                                        }
                                        success.incrementAndGet();
                                    }).join())
                    .toList();


            log.info("Failed : " + failed.get());
            log.info("Success : " + success.get());
        }
    }
}
