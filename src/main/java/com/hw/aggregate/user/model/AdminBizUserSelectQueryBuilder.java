package com.hw.aggregate.user.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldBooleanEqualClause;
import com.hw.shared.sql.clause.SelectFieldStringLikeClause;
import org.springframework.stereotype.Component;

import static com.hw.aggregate.user.model.BizUser.*;

@Component
public class AdminBizUserSelectQueryBuilder extends SelectQueryBuilder<BizUser> {
    {
        DEFAULT_PAGE_SIZE = 20;
        MAX_PAGE_SIZE = 50;
        mappedSortBy.put(ENTITY_EMAIL, ENTITY_EMAIL);
        mappedSortBy.put("createdAt", "createdAt");
        supportedWhereField.put(ENTITY_EMAIL, new SelectFieldStringLikeClause<>(ENTITY_EMAIL));
        supportedWhereField.put(ENTITY_SUBSCRIPTION, new SelectFieldBooleanEqualClause<>(ENTITY_SUBSCRIPTION));
        supportedWhereField.put(ENTITY_GRANTED_AUTHORITIES, new SelectFieldStringLikeClause<>(ENTITY_GRANTED_AUTHORITIES));
        allowEmptyClause = true;
    }

}