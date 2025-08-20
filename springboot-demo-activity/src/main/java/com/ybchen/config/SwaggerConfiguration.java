package com.ybchen.config;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @description: Swagger配置类
 * @author: Alex
 * @create: 2023-08-16 23:04
 */
@Component
@Data
@EnableOpenApi
public class SwaggerConfiguration {
    /**
     * 是否开启swagger，生产环境一般关闭，所以这里定义一个变量
     */
    private Boolean enable = true;

    /**
     * 项目应用名
     */
    private String applicationName = "activiti";

    /**
     * 项目版本信息
     */
    private String applicationVersion = "v1.0.0";

    /**
     * 项目描述信息
     */
    private String applicationDescription = "SpringBoot整合Activiti";


    @Bean
    public Docket docket() {


        return new Docket(DocumentationType.OAS_30)
                .pathMapping("/")
                // 定义是否开启swagger，false为关闭，可以通过变量控制，线上关闭
                .enable(enable)
                //配置api文档元信息
                .apiInfo(apiInfo())
                // 选择哪些接口作为swagger的doc发布
                .select()
                //apis() 控制哪些接口暴露给swagger，
                .apis(RequestHandlerSelectors.basePackage("com.ybchen.controller"))
                .paths(PathSelectors.any())
                .build();

    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(applicationName)
                .description(applicationDescription)
                .contact(new Contact("陈彦斌", "https://www.cnblogs.com/chenyanbin/", "543210188@qq.com"))
                .version(applicationVersion)
                .build();
    }
}