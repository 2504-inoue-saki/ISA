package com.example.ISA.controller;


import com.example.ISA.controller.form.UserForm;
import com.example.ISA.groups.EditPasswordGroup;
import com.example.ISA.groups.LoginGroup;
import com.example.ISA.service.UserService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
    @GetMapping("/myInformation/{id}")
    public ModelAndView editPassword(@PathVariable String id) {
        ModelAndView mav = new ModelAndView();
        UserForm password = new UserForm();
        mav.addObject("password", password);
        mav.addObject("id", id);
        mav.setViewName("/myInformation");
        return mav;
    }

    @PutMapping("/changePassword/{id}")
    public ModelAndView editPassword(@PathVariable int id, @Validated({EditPasswordGroup.class}) @ModelAttribute("password") UserForm password, BindingResult result) {
        ModelAndView mav = new ModelAndView();
        //エラーメッセージを入れる用のリストを作っておく
        List<String> errorMessages = new ArrayList<String>();

        String newPassword = password.getPassword();

//        if (!StringUtils.isEmpty(newPassword) &&
//                ((newPassword.length() < 6 || newPassword.length() > 20) || newPassword.matches("^[!-~]$"))) {
//            errorMessages.add(E0017);
//            //エラーメッセージが詰まったリストをviewに送る
//            mav.addObject("errorMessages", errorMessages);
//            // 画面遷移先を指定
//            mav.setViewName("/myInformation");
//            return mav;
//        }
        if (result.hasErrors()) {
            //result.getFieldErrors()はresultの持つ全エラーを要素にしたリスト→型はList<FieldError>
            //要素を1つ取り出してerrorに代入して処理→全ての要素が尽きるまで繰り返す
            for (FieldError error : result.getFieldErrors()) {
                //error.getDefaultMessage()で取得したエラーメッセージをリストに追加
                errorMessages.add(error.getDefaultMessage());
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

        }
        String pw = password.getPassword();
        userService.savePassword(pw, id);
        return new ModelAndView("redirect:/ISA/2025/6");
    }
}
