package com.jbos.admin.application.service.sm;

import com.jbos.admin.domain.entity.sm.UserToken;
import com.jbos.admin.infrastructure.repository.sm.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * UserTokenService
 * @author youfu.wang
 */
@Service
public class UserTokenService {
    @Autowired
    private UserTokenRepository userTokenRepository;
    /**
     * 创建用户Token
     * @param userid
     */
    @Transactional(rollbackFor=Exception.class)
    public String createToken(String userid){
        return userTokenRepository.createToken(userid);
    }
    /**
     * 失效用户Token
     * @param userid
     */
    public void invalidToken(String userid){
        userTokenRepository.invalidToken(userid);
    }
    /**
     * 查询ID用户Token
     * @param userid
     * @return
     */
    public UserToken getUserTokenByUserId(String userid){
        return userTokenRepository.getUserTokenByUserId(userid);
    }
    /**
     * 根据accessToken查询用户Token
     * @param accessToken
     * @return
     */
    public UserToken getUserTokenByAccessToken(String accessToken){
        return userTokenRepository.getUserTokenByAccessToken(accessToken);
    }
}
