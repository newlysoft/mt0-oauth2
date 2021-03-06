package com.hw.aggregate.client.model;

import com.hw.aggregate.client.AppBizClientApplicationService;
import com.hw.aggregate.client.RevokeBizClientTokenService;
import com.hw.aggregate.client.command.RootCreateBizClientCommand;
import com.hw.aggregate.client.command.RootUpdateBizClientCommand;
import com.hw.aggregate.client.exception.ClientAlreadyExistException;
import com.hw.aggregate.client.exception.RootClientDeleteException;
import com.hw.aggregate.client.representation.AppBizClientCardRep;
import com.hw.shared.Auditable;
import com.hw.shared.StringSetConverter;
import com.hw.shared.rest.Aggregate;
import com.hw.shared.sql.SumPagedRep;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * use different field name to make it more flexible also avoid copy properties type mismatch
 * e.g getting return string instead of enum
 */
@Entity
@Table
@Data
public class BizClient extends Auditable implements Aggregate {
    public static final String ENTITY_ACCESS_TOKEN_VALIDITY_SECONDS = "accessTokenValiditySeconds";
    public static final String ENTITY_RESOURCE_INDICATOR = "resourceIndicator";
    public static final String ENTITY_NAME = "name";
    public static final String ENTITY_GRANT_TYPE_ENUMS = "grantTypeEnums";
    public static final String ENTITY_GRANT_AUTHORITIES = "grantedAuthorities";
    public static final String ENTITY_SCOPE_ENUMS = "scopeEnums";
    public static final String ENTITY_RESOURCE_IDS = "resourceIds";
    @Id
    private Long id;
    private String description;
    @Column
    @Nullable
    private String name;
    @Nullable
    @Column
    private String clientSecret;
    @NotNull
    @NotEmpty
    @Column(nullable = false)
    @Convert(converter = GrantTypeEnum.GrantTypeSetConverter.class)
    private Set<GrantTypeEnum> grantTypeEnums;
    @NotNull
    @NotEmpty
    @Column(nullable = false)
    @Convert(converter = BizClientAuthorityEnum.ClientAuthorityConverter.class)
    private Set<BizClientAuthorityEnum> grantedAuthorities;
    @NotNull
    @NotEmpty
    @Column(nullable = false)
    @Convert(converter = ScopeEnum.ScopeSetConverter.class)
    private Set<ScopeEnum> scopeEnums;
    @Min(value = 0)
    @Column(nullable = false)
    private Integer accessTokenValiditySeconds;
    @Column
    @Nullable
    @Convert(converter = StringSetConverter.class)
    private Set<String> registeredRedirectUri;

    @Column
    @Nullable
    private Integer refreshTokenValiditySeconds;

    @Column
    @NotNull
    @Convert(converter = StringSetConverter.class)
    private Set<String> resourceIds;

    /**
     * indicates if a client is a resource, if true resource id will default to client id
     */
    @Column
    @Nullable
    private Boolean resourceIndicator;
    /**
     * indicates if a authorization_code client can be auto approved
     */
    @Column
    @Nullable
    private Boolean autoApprove;
    /**
     * this field is not used in spring oauth2,
     * client with no secret requires empty secret (mostly encoded)
     * below is empty string "" encoded, use if needed
     * $2a$10$KRp4.vK8F8MYLJGEz7im8.71T2.vFQj/rrNLQLOLPEADuv0Gdg.x6
     */
    @Column
    @NotNull
    private Boolean hasSecret;
    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

    public BizClient() {
    }

    private BizClient(long id, RootCreateBizClientCommand command) {
        this.id = id;
        this.clientSecret = command.getClientSecret();
        this.description = command.getDescription();
        this.grantTypeEnums = command.getGrantTypeEnums();
        this.grantedAuthorities = command.getGrantedAuthorities();
        this.scopeEnums = command.getScopeEnums();
        this.accessTokenValiditySeconds = command.getAccessTokenValiditySeconds();
        this.registeredRedirectUri = command.getRegisteredRedirectUri();
        this.refreshTokenValiditySeconds = command.getRefreshTokenValiditySeconds();
        this.resourceIds = command.getResourceIds();
        this.resourceIndicator = command.getResourceIndicator();
        this.autoApprove = command.getAutoApprove();
        this.hasSecret = command.isHasSecret();
        this.name = command.getName();
    }

    public static BizClient create(long id, RootCreateBizClientCommand command, AppBizClientApplicationService appBizClientApplicationService, BCryptPasswordEncoder encoder) {
        BizClient client = new BizClient(id, command);
        validateResourceId(client, appBizClientApplicationService);
        validateResourceIndicator(client);
        SumPagedRep<AppBizClientCardRep> appBizClientCardRepSumPagedRep = appBizClientApplicationService.readByQuery("id:" + client.getId(), null, null);
        if (appBizClientCardRepSumPagedRep.getData().isEmpty()) {
            if (null == client.getClientSecret()) {
                client.setClientSecret(encoder.encode(""));
            } else {
                client.setClientSecret(encoder.encode(client.getClientSecret().trim()));
            }
            return client;
        } else {
            throw new ClientAlreadyExistException();
        }
    }

    /**
     * selected resource ids should be eligible resource, nullable
     */
    public static void validateResourceId(BizClient client, AppBizClientApplicationService appBizClientApplicationService) {
        if (client.getResourceIds() != null && !client.getResourceIds().isEmpty()) {
            String join = String.join(".", client.getResourceIds());
            SumPagedRep<AppBizClientCardRep> appBizClientCardRepSumPagedRep = appBizClientApplicationService.readByQuery("id:" + join, null, null);
            if (appBizClientCardRepSumPagedRep.getData().size() != client.getResourceIds().size())
                throw new IllegalArgumentException("unable to find resourceId listed");
            if (appBizClientCardRepSumPagedRep.getData().stream().anyMatch(e -> !e.getResourceIndicator()))
                throw new IllegalArgumentException("invalid resourceId found");
        }
    }

    /**
     * if client is marked as resource then it must be a backend and first party application
     */
    public static void validateResourceIndicator(BizClient client) {
        if (Boolean.TRUE.equals(client.getResourceIndicator()) && (client.getGrantedAuthorities().stream().noneMatch(e -> e.equals(BizClientAuthorityEnum.ROLE_BACKEND))
                || client.getGrantedAuthorities().stream().noneMatch(e -> e.equals(BizClientAuthorityEnum.ROLE_FIRST_PARTY))))
            throw new IllegalArgumentException("invalid grantedAuthorities to be a resource, must be ROLE_FIRST_PARTY & ROLE_BACKEND");
    }

    public BizClient replace(RootUpdateBizClientCommand command, RevokeBizClientTokenService tokenRevocationService, AppBizClientApplicationService appBizClientApplicationService, BCryptPasswordEncoder encoder) {
        shouldRevoke(command, tokenRevocationService);
        validateResourceId(this, appBizClientApplicationService);
        if (StringUtils.hasText(command.getClientSecret())) {
            this.setClientSecret(encoder.encode(command.getClientSecret()));
        }
        this.grantTypeEnums = command.getGrantTypeEnums();
        this.description = command.getDescription();
        this.grantedAuthorities = command.getGrantedAuthorities();
        this.scopeEnums = command.getScopeEnums();
        this.accessTokenValiditySeconds = command.getAccessTokenValiditySeconds();
        this.registeredRedirectUri = command.getRegisteredRedirectUri();
        this.refreshTokenValiditySeconds = command.getRefreshTokenValiditySeconds();
        this.resourceIds = command.getResourceIds();
        this.resourceIndicator = command.getResourceIndicator();
        this.autoApprove = command.getAutoApprove();
        this.hasSecret = command.isHasSecret();
        this.name = command.getName();
        validateResourceIndicator(this);
        return this;
    }

    /**
     * root client can not be deleted
     */
    public void validateDelete() {
        if (this.getGrantedAuthorities().stream().anyMatch(BizClientAuthorityEnum.ROLE_ROOT::equals))
            throw new RootClientDeleteException();
    }

    public void shouldRevoke(RootUpdateBizClientCommand newClient, RevokeBizClientTokenService tokenRevocationService) {

        if (StringUtils.hasText(newClient.getClientSecret())
                || authorityChanged(this, newClient)
                || scopeChanged(this, newClient)
                || accessTokenChanged(this, newClient)
                || refreshTokenChanged(this, newClient)
                || grantTypeChanged(this, newClient)
                || resourceIdChanged(this, newClient)
                || redirectUrlChanged(this, newClient)

        ) {
            tokenRevocationService.blacklist(this.getId());
        }
    }

    private boolean authorityChanged(BizClient oldClient, RootUpdateBizClientCommand newClient) {
        return !oldClient.getGrantedAuthorities().equals(newClient.getGrantedAuthorities());
    }

    private boolean scopeChanged(BizClient oldClient, RootUpdateBizClientCommand newClient) {
        return !oldClient.getScopeEnums().equals(newClient.getScopeEnums());
    }

    /**
     * access token validity seconds can not be null
     *
     * @param oldClient
     * @param newClient
     * @return
     */
    private boolean accessTokenChanged(BizClient oldClient, RootUpdateBizClientCommand newClient) {
        return !oldClient.getAccessTokenValiditySeconds().equals(newClient.getAccessTokenValiditySeconds());
    }

    private boolean refreshTokenChanged(BizClient oldClient, RootUpdateBizClientCommand newClient) {
        if (oldClient.getRefreshTokenValiditySeconds() == null && newClient.getRefreshTokenValiditySeconds() == null) {
            return false;
        } else if (oldClient.getRefreshTokenValiditySeconds() != null && oldClient.getRefreshTokenValiditySeconds().equals(newClient.getRefreshTokenValiditySeconds())) {
            return false;
        } else
            return newClient.getRefreshTokenValiditySeconds() == null || !newClient.getRefreshTokenValiditySeconds().equals(oldClient.getRefreshTokenValiditySeconds());
    }

    private boolean grantTypeChanged(BizClient oldClient, RootUpdateBizClientCommand newClient) {
        return !oldClient.getGrantTypeEnums().equals(newClient.getGrantTypeEnums());
    }

    private boolean redirectUrlChanged(BizClient oldClient, RootUpdateBizClientCommand newClient) {
        if ((oldClient.getRegisteredRedirectUri() == null || oldClient.getRegisteredRedirectUri().isEmpty())
                && (newClient.getRegisteredRedirectUri() == null || newClient.getRegisteredRedirectUri().isEmpty())) {
            return false;
        } else if (oldClient.getRegisteredRedirectUri() != null && oldClient.getRegisteredRedirectUri().equals(newClient.getRegisteredRedirectUri())) {
            return false;
        } else
            return newClient.getRegisteredRedirectUri() == null || !newClient.getRegisteredRedirectUri().equals(oldClient.getRegisteredRedirectUri());
    }

    private boolean resourceIdChanged(BizClient oldClient, RootUpdateBizClientCommand newClient) {
        return !oldClient.getResourceIds().equals(newClient.getResourceIds());
    }

}
