package com.data.faker.app.changelogs;

import com.data.faker.app.document.Benef;
import com.data.faker.app.document.DataFlow;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.data.faker.app.utils.DataFlowChangeLogsUtils.getDataFlowChangeBatchVersion2;

@Profile("mongock")
@ChangeUnit(id="add-field-change", order = "001", author = "dev")
public class AddFieldChange {

    @Execution
    public void changeSet(MongoTemplate mongoTemplate) {
        getDataFlowChangeBatchVersion2(mongoTemplate, this::makeChange2);
    }

    @RollbackExecution
    public void rollbackExecution(MongoTemplate mongoTemplate) {
       // think about implementation
    }

    private Document makeChange(Document dataFlow) {
        Double diff = dataFlow.getDouble("close") - dataFlow.getDouble("open");
        Double invest = dataFlow.getDouble("volume") - (dataFlow.getDouble("volume") * dataFlow.getDouble("dividend"));

        Map<String, Double> benef = new HashMap<>();
        benef.put("diff", diff);
        benef.put("invest", invest);

        dataFlow.append("benef", benef);
        return dataFlow;
    }

    private DataFlow makeChange2(DataFlow dataFlow) {
        Double diff = dataFlow.getClose() - dataFlow.getOpen();
        Double invest = dataFlow.getVolume() - (dataFlow.getVolume() * dataFlow.getDividend());

        dataFlow.setBenef(Benef.builder().invest(invest).diff(diff).build());

        return dataFlow;
    }
}
