package com.data.faker.app.changelogs;

import com.data.faker.app.document.Benef;
import com.data.faker.app.document.DataFlow;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Objects;

import static com.data.faker.app.utils.DataFlowChangeLogsUtils.*;

@Profile("mongock")
@ChangeUnit(id="update-field-change", order = "002", author = "dev")
public class UpdateFieldChange {

    @Execution
    public void changeSet(final MongoTemplate mongoTemplate) {
        //getDataFlowChangeBatchVersion(mongoTemplate, this::makeChange);
    }

    @RollbackExecution
    public void rollBack(final MongoTemplate mongoTemplate) {
        // think about roll back
    }

    private DataFlow makeChange(DataFlow dataFlow) {
        Benef benef = dataFlow.getBenef();
        return Objects.nonNull(benef)? computeState(dataFlow, benef): computeStateIfBenefNull(dataFlow);
    }
    private DataFlow computeStateIfBenefNull(DataFlow dataFlow) {
        double diff = dataFlow.getClose() - dataFlow.getOpen();
        double invest = dataFlow.getVolume() - (dataFlow.getVolume() * dataFlow.getDividend());
        Benef benef = Benef.builder().diff(diff).invest(invest).build();

        return computeState(dataFlow, benef);
    }

    private DataFlow computeState(DataFlow dataFlow, Benef benef) {
        if((benef.getDiff() > 0) && (benef.getInvest() > dataFlow.getVolume())){
            benef.setState(UP);
            dataFlow.setBenef(benef);
            return dataFlow;
        } else if ((benef.getDiff() < 0) && (benef.getInvest() < dataFlow.getVolume())) {
            benef.setState(DOWN);
            dataFlow.setBenef(benef);
            return dataFlow;
        }
        benef.setState(NORMAL);
        dataFlow.setBenef(benef);

        return dataFlow;
    }
}
