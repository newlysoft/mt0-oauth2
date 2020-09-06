package com.hw.aggregate.client;

import com.hw.aggregate.client.model.BizClient;
import com.hw.aggregate.client.model.BizClientQueryRegistry;
import com.hw.aggregate.client.representation.AppBizClientCardRep;
import com.hw.aggregate.client.representation.AppBizClientRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class AppBizClientApplicationService extends DefaultRoleBasedRestfulService<BizClient, AppBizClientCardRep, AppBizClientRep, VoidTypedClass> implements ClientDetailsService {
    @Autowired
    private BizClientRepo clientRepo;
    @Autowired
    private BizClientQueryRegistry clientQueryRegistry;

    @PostConstruct
    private void setUp() {
        repo = clientRepo;
        queryRegistry = clientQueryRegistry;
        entityClass = BizClient.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
    }

    @Override
    public BizClient replaceEntity(BizClient stored, Object command) {
        return null;
    }

    @Override
    public void preDelete(BizClient bizClient) {

    }

    @Override
    public void postDelete(BizClient bizClient) {

    }

    @Override
    protected void prePatch(BizClient bizClient, Map<String, Object> params, VoidTypedClass middleLayer) {

    }

    @Override
    protected void postPatch(BizClient bizClient, Map<String, Object> params, VoidTypedClass middleLayer) {

    }


    @Override
    public AppBizClientCardRep getEntitySumRepresentation(BizClient client) {
        return new AppBizClientCardRep(client);
    }

    @Override
    public AppBizClientRep getEntityRepresentation(BizClient client) {
        return new AppBizClientRep(client);
    }

    @Override
    protected BizClient createEntity(long id, Object command) {
        return null;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        AppBizClientRep appBizClientRep = readById(Long.parseLong(clientId));
        return appBizClientRep;
    }
}