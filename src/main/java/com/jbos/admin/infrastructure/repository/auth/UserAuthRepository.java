package com.jbos.admin.infrastructure.repository.auth;

import com.jbos.admin.infrastructure.repository.auth.mapper.UserAuthMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserAuthRepository {
    @Autowired
    private UserAuthMapper userAuthMapper;
    /**
     * 用户认证
     * @param username
     * @return
     */
    public Map<String,Object> login(String username){
        Map<String, Object> parameterObject=new HashMap<String, Object>();
        parameterObject.put("username",username);
        return userAuthMapper.login(parameterObject);
    }

    /**
     * 得到认证用户角色
     * @param username
     * @return
     */
    public List<HashMap> getAuthUserRole(String username){
        Map<String, Object> parameterObject=new HashMap<String, Object>();
        parameterObject.put("username",username);
        return userAuthMapper.getAuthUserRole(parameterObject);
    }
    /**
     * 得到用户认证信息
     * @param username
     * @return
     */
    public Map<String,Object> getUserAuthInfo(String username){
        Map<String, Object> parameterObject=new HashMap<String, Object>();
        parameterObject.put("username",username);
        return userAuthMapper.getUserAuthInfo(parameterObject);
    }
}
