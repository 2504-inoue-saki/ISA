package com.example.ISA.filter;

import java.io.IOException;
import java.time.LocalDate;

import com.example.ISA.controller.form.UserForm;
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

public class ApproveFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        //ServletRequestをHttpServletRequestに型変更
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        //セッションの獲得→trueだとセッションがない場合は作ってくれる
        HttpSession session = httpRequest.getSession(true);
        //セッションからログインユーザの獲得
        Object loginUser = session.getAttribute("loginUser");
        //型変更
        UserForm loginUserForm = (UserForm) loginUser;

        //セッション内にログインユーザーがないorそのログインユーザが承認者権限を持たない(2)場合→ホーム画面にエラーメッセージ表示
        if (loginUser == null || loginUserForm.getCategory() != 2) {
            session.setAttribute("filterMessage", E0025);
            LocalDate today = LocalDate.now();
            httpResponse.sendRedirect("/ISA/" + today.getYear() + "/" + today.getMonthValue());
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
