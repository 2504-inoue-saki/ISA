package com.example.ISA.controller;


import com.example.ISA.controller.form.UserForm;
import com.example.ISA.groups.EditPasswordGroup;
import com.example.ISA.groups.LoginGroup;
import com.example.ISA.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.ISA.constfolder.ErrorMessage.*;

@Controller
public class MyInformationController {

    @Autowired
    UserService userService;
    @Autowired
    HttpServletRequest request;

    @Autowired
    HttpSession session;

    /*
     *ユーザ情報編集画面表示処理(旭)
     */

    @GetMapping({"/myInformation/{id}", "/myInformation/"})
    public ModelAndView editPassword(@PathVariable(required = false) String id, Model model) {

        //★URLパターンのエラメ（鈴木）
        HttpSession session = request.getSession(true);
        if (StringUtils.isBlank(id) || !id.matches("^[0-9]*$")) {
            //エラーメッセージを入れる用のリストを作っておく
            List<String> errorMessages = new ArrayList<String>();
            errorMessages.add(E0026);
            //エラーメッセージが詰まったセッションを用意
            session.setAttribute("popupErrorMessages", errorMessages);
            //ホーム画面へリダイレクト
            LocalDate today = LocalDate.now(); // 現在の年月を取得
            return new ModelAndView("redirect:/ISA/" + today.getYear() + "/" + today.getMonthValue());
        }

        ModelAndView mav = new ModelAndView();
        UserForm password = new UserForm();
        //エラーメッセージを入れる用のリストを作っておく
        List<String> errorMessages = new ArrayList<String>();
        //ログインユーザとURLリクエストのidをチェック
        UserForm loginUser = (UserForm) session.getAttribute("loginUser");
        if(!id.equals(loginUser.getId())){
            session.setAttribute("errorMessage", E0026);
            LocalDate today = LocalDate.now(); // 現在の年月を取得
            return new ModelAndView("redirect:/ISA/" + today.getYear() + "/" + today.getMonthValue());
        }

        mav.addObject("password", password);
        mav.addObject("id", id);
        mav.setViewName("/myInformation");


        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("userCategory", loginUser.getCategory());

        return mav;
    }

    @PutMapping("/changePassword/{id}")
    public ModelAndView editPassword(@PathVariable int id, @Validated({EditPasswordGroup.class}) @ModelAttribute("password") UserForm password, BindingResult result) {
        ModelAndView mav = new ModelAndView();
        //エラーメッセージを入れる用のリストを作っておく
        List<String> errorMessages = new ArrayList<String>();

        String newPassword = password.getPassword();

        if (result.hasErrors()) {
            //result.getFieldErrors()はresultの持つ全エラーを要素にしたリスト→型はList<FieldError>
            //要素を1つ取り出してerrorに代入して処理→全ての要素が尽きるまで繰り返す
            for (FieldError error : result.getFieldErrors()) {
                //error.getDefaultMessage()で取得したエラーメッセージをリストに追加
                errorMessages.add(error.getDefaultMessage());
                //エラーメッセージが詰まったリストをviewに送る
            }
            mav.addObject("errorMessages", errorMessages);
            mav.setViewName("/myInformation");
            return mav;
        }

        if (!StringUtils.isEmpty(newPassword) &&
                ((newPassword.length() < 6 || newPassword.length() > 20) || newPassword.matches("^[!-~]$"))) {
            errorMessages.add(E0005);

            //エラーメッセージが詰まったリストをviewに送る
            mav.addObject("errorMessages", errorMessages);
            // 画面遷移先を指定
            mav.setViewName("/myInformation");
            return mav;
        }

        //妥当性チェック①パスワードと確認用パスワードが異なる時にエラーメッセージ

        if (!password.getPassword().equals(password.getCheckPassword())) {
            errorMessages.add(E0010);

            //エラーメッセージが詰まったリストをviewに送る
            mav.addObject("errorMessages", errorMessages);
            // 画面遷移先を指定
            mav.setViewName("/myInformation");
            return mav;
        }
        String pw = password.getPassword();
        userService.savePassword(pw, id);

        LocalDate today = LocalDate.now(); // 現在の年月を取得
        return new ModelAndView("redirect:/ISA/" + today.getYear() + "/" + today.getMonthValue());
    }
}

