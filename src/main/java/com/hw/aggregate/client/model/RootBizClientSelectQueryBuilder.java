package com.hw.aggregate.client.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldStringEqualClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.hw.aggregate.client.model.BizClient.*;

@Component
public class RootBizClientSelectQueryBuilder extends SelectQueryBuilder<BizClient> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

    RootBizClientSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 1000;
        MAX_PAGE_SIZE = 2000;
        mappedSortBy.put("clientId",ENTITY_CLIENT_ID);
        mappedSortBy.put("resourceIndicator",ENTITY_RESOURCE_INDICATOR);
        mappedSortBy.put("accessTokenValiditySeconds",ENTITY_ACCESS_TOKEN_VALIDITY_SECONDS);
        supportedWhereField.put("clientId", new SelectFieldStringEqualClause<>(ENTITY_CLIENT_ID));
        allowEmptyClause = true;
    }
}