package com.hw.aggregate.client.representation;

import com.hw.aggregate.client.model.BizClient;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserBizClientCardRep {
    private Long id;
    private Boolean autoApprove;

    public UserBizClientCardRep(BizClient client) {
        BeanUtils.copyProperties(client, this);
    }
}
