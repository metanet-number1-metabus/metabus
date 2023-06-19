package com.metanet.metabus.bus.entity;


import com.metanet.metabus.common.BaseEntity;
import com.metanet.metabus.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Payment extends BaseEntity {

    private String applyNum;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_payment_member"))
    private Member member;

    private String cardName;
    private String cardNum;

    @Id
    private String impUid;

    private String merchantName;
    private Long payment;
    private String payMethod;
    private Long usedMileage;

}
