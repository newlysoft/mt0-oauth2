package com.hw.aggregate.client.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BizClientQueryRegistry extends RestfulQueryRegistry<BizClient> {
    @Override
    public Class<BizClient> getEntityClass() {
        return BizClient.class;
    }
    {
        cacheable.put(RoleEnum.USER, true);
        cacheable.put(RoleEnum.ADMIN, true);
        cacheable.put(RoleEnum.APP, true);
        cacheable.put(RoleEnum.PUBLIC, true);
        cacheable.put(RoleEnum.ROOT, true);
    }
}
