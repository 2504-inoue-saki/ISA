package com.example.ISA.controller;


import com.example.ISA.controller.form.UserForm;
import com.example.ISA.service.UserService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static com.example.ISA.constfolder.ErrorMessage.*;

@Controller
public class MyInformationController {

    @Autowired
    UserService userService;

    /*
     *ユーザ情報編集画面表示処理(旭)
     */
    @GetMapping("/myInformation")
    public ModelAndView editPassword() {
        ModelAndView mav = new ModelAndView();
        UserForm password = new UserForm();
        mav.addObject("password",password);
        mav.setViewName("/myInformation");
        return mav;
    }

    @PutMapping("/myInformation")
    public ModelAndView editPassword(@ModelAttribute ("password")UserForm password){
        ModelAndView mav = new ModelAndView();
        //エラーメッセージを入れる用のリストを作っておく
        List<String> errorMessages = new ArrayList<String>();

        String newPassword = password.getPassword();
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

        userService.savePassword(password);
        return new ModelAndView("redirect:/ISA/");
    }
}
