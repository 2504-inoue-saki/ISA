package com.example.ISA.config;

import com.example.ISA.filter.AdminFilter;
import com.example.ISA.filter.ApproveFilter;
import com.example.ISA.filter.LoginFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<LoginFilter> loginFilter() {
        FilterRegistrationBean<LoginFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new LoginFilter());
        //ログイン情報が必要なURL
        bean.addUrlPatterns("/ISA/*"); //ホーム画面
        bean.addUrlPatterns("/myInformation/*"); //パスワード変更画面
        bean.addUrlPatterns("/monthWork"); //月毎勤怠情報編集画面

        //フィルターの優先順位
        bean.setOrder(1);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<ApproveFilter> approveFilter() {
        FilterRegistrationBean<ApproveFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new ApproveFilter());
        //承認者権限が必要なURL
        bean.addUrlPatterns("/application"); //申請一覧画面
        bean.addUrlPatterns("/applicationPrivate/*"); //個人申請承認画面

        bean.setOrder(2);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<AdminFilter> adminFilter() {
        FilterRegistrationBean<AdminFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new AdminFilter());
        //システム管理者権限が必要なURL
        bean.addUrlPatterns("/userAdmin"); //ユーザ管理画面
        bean.addUrlPatterns("/userEdit/*"); //ユーザ編集画面
        bean.addUrlPatterns("/signup"); //新規アカウント登録画面

        bean.setOrder(3);
        return bean;
    }
}
