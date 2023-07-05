package com.jbos.admin.infrastructure.repository.mm;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jbos.admin.domain.entity.mm.Member;
import com.jbos.admin.infrastructure.repository.mm.mapper.MemberMapper;
import com.jbos.admin.common.page.PageParam;
import com.jbos.admin.common.page.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * MemberRepo
 * @author youfu.wang
 * @date 2021-08-19
 */
@Component
public class MemberRepo {
    @Autowired
    private MemberMapper memberMapper;
    @Paging
    public List<Member> getMemberList(PageParam pageParam, Map<String, Object> parameterObject){
        return this.memberMapper.getMemberList(parameterObject);
    }
    public Member getMemberInfo(Map<String, Object> parameterObject){
        return this.memberMapper.getMemberInfo(parameterObject);
    }
    public void addMemberInfo(Member member){
        this.memberMapper.insert(member);
    }
    public void updateMemberInfo(Member member){
        UpdateWrapper<Member> updateWrapper=new UpdateWrapper<Member>();
        updateWrapper.eq("account",member.getAccount());
        this.memberMapper.update(member,updateWrapper);
    }
}
