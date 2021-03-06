package com.hw.aggregate.user;

import com.hw.aggregate.pending_user.AppPendingUserApplicationService;
import com.hw.aggregate.user.command.AppCreateBizUserCommand;
import com.hw.aggregate.user.command.AppForgetBizUserPasswordCommand;
import com.hw.aggregate.user.command.AppResetBizUserPasswordCommand;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.representation.AppBizUserCardRep;
import com.hw.aggregate.user.representation.AppBizUserRep;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.hw.shared.sql.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Service
public class AppBizUserApplicationService extends RoleBasedRestfulService<BizUser, AppBizUserCardRep, AppBizUserRep, VoidTypedClass> implements UserDetailsService {
    {
        entityClass = BizUser.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
    }
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private RevokeBizUserTokenService tokenRevocationService;

    @Autowired
    private PwdResetEmailService emailService;

    @Autowired
    private AppPendingUserApplicationService pendingUserApplicationService;


    @Override
    public BizUser replaceEntity(BizUser bizUser, Object command) {
        bizUser.replace(command, emailService, tokenRevocationService, encoder);
        return bizUser;
    }

    @Override
    public AppBizUserCardRep getEntitySumRepresentation(BizUser bizUser) {
        return new AppBizUserCardRep(bizUser);
    }

    @Override
    public AppBizUserRep getEntityRepresentation(BizUser bizUser) {
        return new AppBizUserRep(bizUser);
    }

    public void sendForgetPassword(AppForgetBizUserPasswordCommand command, String changeId) {
        BizUser.createForgetPwdToken(command, this, changeId);
    }

    public void resetPassword(AppResetBizUserPasswordCommand command, String changeId) {
        BizUser.resetPwd(command, this, changeId);
    }

    @Override
    protected BizUser createEntity(long id, Object command) {
        return BizUser.create(id, (AppCreateBizUserCommand) command, encoder, pendingUserApplicationService, this);

    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrId) {
        try {
            return readById(Long.parseLong(usernameOrId));
        } catch (NumberFormatException ex) {
            SumPagedRep<AppBizUserCardRep> appBizUserCardRepSumPagedRep = readByQuery("email:" + usernameOrId, null, null);
            if (appBizUserCardRepSumPagedRep.getData().isEmpty())
                return null;
            return readById(appBizUserCardRepSumPagedRep.getData().get(0).getId());
        }
    }
}
