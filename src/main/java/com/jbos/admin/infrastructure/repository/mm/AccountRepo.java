package com.jbos.admin.infrastructure.repository.mm;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jbos.admin.domain.entity.mm.Account;
import com.jbos.admin.infrastructure.repository.mm.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AccountRepo
 * @author youfu.wang
 * @date 2021-08-19
 */
@Component
public class AccountRepo {
    @Autowired
    private AccountMapper accountMapper;

    public Map<String,Object> getAccount(Map<String,Object> parameterObject){
        return this.accountMapper.getAccount(parameterObject);
    }

    public void registry(Map<String,Object> parameterObject){
        this.accountMapper.registry(parameterObject);
    }

    public void updateAccountStatus(Account account){
        UpdateWrapper<Account> updateWrapper=new UpdateWrapper<Account>();
        updateWrapper.eq("account",account.getAccount());
        this.accountMapper.update(account,updateWrapper);
    }
}
