package com.hw.aggregate.client.representation;

import com.hw.aggregate.client.model.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class RootBizClientCardRep {

    private Long id;

    private String clientId;

    private String description;

    private Set<GrantTypeEnum> grantTypeEnums;

    private List<GrantedAuthorityImpl<BizClientAuthorityEnum>> grantedAuthorities;

    private Set<ScopeEnum> scopeEnums;

    private Integer accessTokenValiditySeconds;

    private Set<String> registeredRedirectUri;

    private Integer refreshTokenValiditySeconds;

    private Set<String> resourceIds;

    private Boolean resourceIndicator;

    private Boolean autoApprove;

    public RootBizClientCardRep(BizClient client) {
        this.id = client.getId();
        this.clientId = client.getClientId();
        this.description = client.getDescription();
        this.grantTypeEnums = client.getGrantTypeEnums();
        this.grantedAuthorities = client.getGrantedAuthorities();
        this.scopeEnums = client.getScopeEnums();
        this.accessTokenValiditySeconds = client.getAccessTokenValiditySeconds();
        this.registeredRedirectUri = client.getRegisteredRedirectUri();
        this.refreshTokenValiditySeconds = client.getRefreshTokenValiditySeconds();
        this.resourceIds = client.getResourceIds();
        this.resourceIndicator = client.getResourceIndicator();
        this.autoApprove = client.getAutoApprove();
    }
}