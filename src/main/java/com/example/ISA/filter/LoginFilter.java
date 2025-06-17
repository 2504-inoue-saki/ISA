package com.example.ISA.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import static com.example.ISA.constfolder.ErrorMessage.*;
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        //ServletRequestをHttpServletRequestに型変更
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        //セッションの獲得→trueだとセッションがない場合は作ってくれる
        HttpSession session = httpRequest.getSession(true);
        //ログインユーザが存在していない場合→ログイン画面にエラーメッセージ表示
        if (session.getAttribute("loginUser") == null) {
            session.setAttribute("filterMessage", E0024);
            // ログイン画面に遷移
            httpResponse.sendRedirect("/login");
        } else {
            // 通常実行
            chain.doFilter(httpRequest, httpResponse);
        }
    }

    @Override
    public void init(FilterConfig config) {
    }

    @Override
    public void destroy() {
    }
}
