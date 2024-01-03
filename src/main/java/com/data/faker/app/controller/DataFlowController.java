package com.data.faker.app.controller;

import com.data.faker.app.document.DataFlow;
import com.data.faker.app.service.DataFlowService;
import com.mongodb.client.result.DeleteResult;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flow")
@Profile("datafaker")
public class DataFlowController {

    private final DataFlowService dataFlowService;

    public DataFlowController(DataFlowService dataFlowService) {
        this.dataFlowService = dataFlowService;
    }

    @GetMapping("one")
    public Mono<DataFlow> generateOne() {
        return dataFlowService.generateOne();
    }

    @GetMapping("10K")
    public Mono<Integer> generate10K() {
        return dataFlowService.generate10K();
    }

    @GetMapping("100K")
    public Mono<Integer> generate100K() {
        return dataFlowService.generate100K();
    }

    @GetMapping("1M")
    public Mono<Integer> generate1M() {
        return dataFlowService.generate1M();
    }

    @GetMapping("same")
    public Map<String, List<DataFlow>> getSameSymbol() {
        return dataFlowService.getDataFlowIdenticSymbol();
    }

    @GetMapping("v2/same")
    public Map<String, List<DataFlow>> getSameSymbolV2() {
        return dataFlowService.getDataFlowIndenticSymoblV2();
    }

    @DeleteMapping("deleteAll")
    public DeleteResult deleteAll() {
        return dataFlowService.delateAll();
    }
}
