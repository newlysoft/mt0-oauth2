package com.hw.aggregate.client;

import com.hw.aggregate.client.model.BizClient;
import com.hw.aggregate.client.representation.UserBizClientCardRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class UserBizClientApplicationService extends DefaultRoleBasedRestfulService<BizClient, UserBizClientCardRep, Void, VoidTypedClass>{

    @PostConstruct
    private void setUp() {
        entityClass = BizClient.class;
        role = RestfulQueryRegistry.RoleEnum.USER;
    }

    @Override
    public BizClient replaceEntity(BizClient stored, Object command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void preDelete(BizClient bizClient) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void postDelete(BizClient bizClient) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void prePatch(BizClient bizClient, Map<String, Object> params, VoidTypedClass middleLayer) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void postPatch(BizClient bizClient, Map<String, Object> params, VoidTypedClass middleLayer) {
        throw new UnsupportedOperationException();
    }


    @Override
    public UserBizClientCardRep getEntitySumRepresentation(BizClient client) {
        return new UserBizClientCardRep(client);
    }

    @Override
    public Void getEntityRepresentation(BizClient client) {
        return null;
    }

    @Override
    protected BizClient createEntity(long id, Object command) {
        throw new UnsupportedOperationException();
    }

}
