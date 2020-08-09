package com.yangyi.qz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/08/07
 */
@MapperScan(value = "com.yangyi.qz.**.dao", annotationClass = Repository.class)
@SpringBootApplication
public class SpringBootQuartz {

    /**
     * 功能描述： 统一设置时区
     * @author yangyi
     * @date 2020/08/07
     */
    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootQuartz.class, args);
    }
}
