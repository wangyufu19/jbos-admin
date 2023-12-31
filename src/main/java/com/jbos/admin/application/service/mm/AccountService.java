package com.jbos.admin.application.service.mm;


import com.jbos.admin.domain.entity.mm.Account;
import com.jbos.admin.domain.entity.mm.Member;
import com.jbos.admin.infrastructure.repository.mm.AccountRepo;
import com.jbos.admin.infrastructure.repository.mm.MemberRepo;
import com.jbos.admin.infrastructure.repository.sm.UserMgrRepository;
import com.jbos.admin.common.utils.DateUtils;
import com.jbos.admin.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

/**
 * AccountRepo
 * @author youfu.wang
 * @date 2021-08-19
 */
@Service
public class AccountService {
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private MemberRepo memberRepo;
    @Autowired
    private UserMgrRepository userMgrRepository;
    /**
     * 得到会员账户
     * @param parameterObject
     * @return
     */
    public Map<String,Object> getAccount(Map<String,Object> parameterObject){
        return this.accountRepo.getAccount(parameterObject);
    }
    /**
     * 会员账户是否存在
     */
    public boolean isExists(Map<String,Object> parameterObject){
        Map<String,Object> retMap=this.getAccount(parameterObject);
        if(retMap!=null){
            return true;
        }
        return false;
    }

    /**
     * 注册会员账户
     * @param parameterObject
     */
    @Transactional
    public void registry(Map<String,Object> parameterObject){
        parameterObject.put("seqId", UUID.randomUUID().toString());
        PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        parameterObject.put("password",passwordEncoder.encode(StringUtils.replaceNull(parameterObject.get("password"))));
        this.accountRepo.registry(parameterObject);
        Member member=new Member();
        member.setSeqId(StringUtils.getUUID());
        member.setAccount(String.valueOf(parameterObject.get("account")));
        member.setRegistryTime(DateUtils.getCurrentDate());
        member.setCreateTime(DateUtils.getCurrentDate());
        this.memberRepo.addMemberInfo(member);

        String id= UUID.randomUUID().toString();
        parameterObject.put("id", id);
        parameterObject.put("userId", id);
        parameterObject.put("username",parameterObject.get("account"));
        parameterObject.put("nickname",parameterObject.get("account"));
        parameterObject.put("password",parameterObject.get("password"));
        userMgrRepository.addUserInfo(parameterObject);
        userMgrRepository.addUserDefaultRole(parameterObject);
    }

    /**
     * 修改会员状态
     * @param account
     */
    public void updateAccountStatus(Account account){
        accountRepo.updateAccountStatus(account);
    }
}
