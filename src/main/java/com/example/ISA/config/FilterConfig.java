//package com.example.ISA.config;
//
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FilterConfig {
//    @Bean
//    public FilterRegistrationBean<LoginFilter> loginFilter() {
//        FilterRegistrationBean<LoginFilter> bean = new FilterRegistrationBean<>();
//        bean.setFilter(new LoginFilter());
//        //ログイン情報が必要なURL
//        //bean.addUrlPatterns("/ISA/");
//
//        //フィルターの優先順位
//        bean.setOrder(1);
//        return bean;
//    }
//
//    @Bean
//    public FilterRegistrationBean<AdminFilter> adminFilter() {
//        FilterRegistrationBean<AdminFilter> bean = new FilterRegistrationBean<>();
//        bean.setFilter(new AdminFilter());
//        //管理者権限が必要なURL
//
//        bean.setOrder(2);
//        return bean;
//    }
//}
