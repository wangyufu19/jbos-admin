package com.jbos.admin;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.jbos.admin.common.bigdata.BigDataProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.sql.SQLException;

/**
 * AdminApplication
 * @author youfu.wang
 * @date 2021-08-19
 */
@SpringBootApplication
@EnableAspectJAutoProxy
//@EnableApolloConfig
public class AdminApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AdminApplication.class);
    }

    public static void main(String[] args) throws SQLException {
        ApplicationContext applicationContext=SpringApplication.run(AdminApplication.class, args);
        BigDataProcessor bigDataProcessor=applicationContext.getBean(BigDataProcessor.class);
        long start = System.currentTimeMillis();
        bigDataProcessor.process();
        long end = System.currentTimeMillis();
        System.out.println("耗时: " +(end - start)/1000);
    }
}
