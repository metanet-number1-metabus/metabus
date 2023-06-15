package com.metanet.metabus.mileage.entity;


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
public class Mileage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_mileage_member"))
    private Member member;

    private Long point;

    @Enumerated(EnumType.STRING)
    private SaveStatus saveStatus = SaveStatus.UP;
}
