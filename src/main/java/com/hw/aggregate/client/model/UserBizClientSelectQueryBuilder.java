package com.hw.aggregate.client.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldLongEqualClause;
import org.springframework.stereotype.Component;

import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;

@Component
public class UserBizClientSelectQueryBuilder extends SelectQueryBuilder<BizClient> {
    UserBizClientSelectQueryBuilder() {
        supportedWhereField.put("clientId", new SelectFieldLongEqualClause<>(COMMON_ENTITY_ID));
    }
}
