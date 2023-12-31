package com.jbos.admin.application.api.auth;


import com.jbos.admin.common.jwt.JwtTokenProvider;
import com.jbos.admin.common.response.ResponseResult;
import com.jbos.admin.common.user.JwtUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Api("用户认证接口")
public class UserAuthApi {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private String getRequestToken() {
        // 获得request对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String accessToken = request.getHeader("accessToken");
        if (accessToken == null) {
            return request.getParameter("accessToken");
        } else {
            return accessToken;
        }
    }
    //@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ResponseBody
    @RequestMapping(value = "/getPrincipalInfo", method = RequestMethod.GET)
    @ApiOperation("得到用户凭据信息")
    public ResponseResult getPrincipalInfo(@RequestParam Map<String, Object> params) {
        ResponseResult res= ResponseResult.ok();
        //获取用户对象
        JwtUser principal = (JwtUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String nickName = jwtTokenProvider.getSignDataFromJWT(this.getRequestToken(), "nickName");
        String depId = jwtTokenProvider.getSignDataFromJWT(this.getRequestToken(), "depId");
        String depName = jwtTokenProvider.getSignDataFromJWT(this.getRequestToken(), "depName");
        String orgId = jwtTokenProvider.getSignDataFromJWT(this.getRequestToken(), "orgId");
        Map<String,Object> data=new HashMap<String,Object>();
        data.put("username",principal.getUsername());
        data.put("nickName",nickName);
        data.put("depId",depId);
        data.put("depName",depName);
        data.put("orgId",orgId);
        res.setData(data);
        return res;
    }
}
