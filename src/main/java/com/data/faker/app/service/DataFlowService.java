package com.data.faker.app.service;

import com.data.faker.app.document.DataFlow;
import com.data.faker.app.repository.DataFlowRepository;
import com.data.faker.app.utils.DataFlowGenerate;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentReferenceHashMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataFlowService {

    private final MongoTemplate mongoTemplate;
    private final Executor executor;
    private static final int BATCH_SIZE = 5000;

    public DataFlowService(MongoTemplate mongoTemplate,
                           @Qualifier("fakerExecutor") Executor executor) {
        this.mongoTemplate = mongoTemplate;
        this.executor = executor;
    }

    public Mono<DataFlow> generateOne() {
        return Mono.just(mongoTemplate.insert(DataFlowGenerate.generateOnDataFlow()));
    }

    public Mono<Integer> generate100K() {
        return generateNDataFlow(100000);
    }

    public Mono<Integer> generate10K() {
        return generateNDataFlow(10000);
    }

    public Mono<Integer> generate1M() {
        return generateNDataFlow(1000000);
    }

    public Map<String, List<DataFlow>> getDataFlowIdenticSymbol() {
        return mongoTemplate.findAll(DataFlow.class).stream()
                .collect(Collectors.groupingBy(DataFlow::getSymbol));
    }

    public Map<String, List<DataFlow>> getDataFlowIndenticSymoblV2() {
        Map<String, List<DataFlow>> result = new ConcurrentReferenceHashMap<>();
        Flux.fromIterable(mongoTemplate.findAll(DataFlow.class))
                .publishOn(Schedulers.boundedElastic())
                .collect(Collectors.groupingBy(DataFlow::getSymbol))
                .filter(item -> item.values().stream().findFirst().orElseGet(ArrayList::new).size() >= 2)
                .doOnNext(result::putAll)
                .subscribe(item -> log.info("Size response {}", item.size()));

        return result;
    }

    private Mono<Integer> generateNDataFlow(Integer number) {
        return Flux.fromIterable(DataFlowGenerate.paritionList(DataFlowGenerate.generateNDataFlow(number), 500))
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(item -> CompletableFuture.supplyAsync(() -> mongoTemplate.insertAll(item), executor))
                .map(List::size)
                .reduce(0, Integer::sum);
    }

    private long getTotalPagesOfCollection(Pageable pageable) {
        Query query = new Query().with(pageable);
        return mongoTemplate.count(query, DataFlow.class);
    }

    public DeleteResult delateAll() {
        return mongoTemplate.remove(DataFlow.class).all();
    }
}
