package com.jbos.admin.application.service.mm;

import com.jbos.admin.domain.entity.mm.Member;
import com.jbos.admin.infrastructure.repository.mm.MemberRepo;
import com.jbos.admin.common.page.PageParam;
import com.jbos.admin.common.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * MemberService
 * @author youfu.wang
 * @date 2021-08-19
 */
@Component
public class MemberService {
    @Autowired
    private MemberRepo memberRepo;

    public ResponseResult getMemberList(PageParam pageParam, Map<String, Object> parameterObject){
        List<Member> memberList=this.memberRepo.getMemberList(pageParam,parameterObject);
        return ResponseResult.ok().isPage(true).data(memberList);
    }
    public Member getMemberInfo(Map<String, Object> parameterObject){
        return this.memberRepo.getMemberInfo(parameterObject);
    }
    public void addMemberInfo(Member member){
        this.memberRepo.addMemberInfo(member);
    }
    public void updateMemberInfo(Member member){
        this.memberRepo.updateMemberInfo(member);
    }
}
