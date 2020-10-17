package com.hw.aggregate.pending_user.model;

import com.hw.shared.sql.clause.SelectFieldEmailEqualClause;
import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.hw.aggregate.user.model.BizUser.ENTITY_EMAIL;

@Component
public class AppPendingUserSelectQueryBuilder extends SelectQueryBuilder<PendingUser> {
    AppPendingUserSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 1;
        MAX_PAGE_SIZE = 1;
        supportedWhereField.put("email", new SelectFieldEmailEqualClause<>(ENTITY_EMAIL));
    }
}
