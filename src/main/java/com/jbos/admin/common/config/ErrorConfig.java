package com.jbos.admin.common.config;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
/**
 * ErrorConfig
 *
 * @author youfu.wang
 * @date 2023/7/3
 **/
@Component
public class ErrorConfig implements ErrorPageRegistrar {

    /**
     * 404页面错误处理方法，重定向至index.html页面
     * @param registry
     */
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/index.html");
        registry.addErrorPages(error404Page);
    }
}
