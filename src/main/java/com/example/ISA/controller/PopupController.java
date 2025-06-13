package com.example.ISA.controller;

import com.example.ISA.Dto.AllForm;
import com.example.ISA.controller.form.UserForm;
import com.example.ISA.controller.form.WorkingForm;
import com.example.ISA.groups.LoginGroup;
import com.example.ISA.service.UserService;
import com.example.ISA.service.WorkingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static com.example.ISA.constfolder.ErrorMessage.*;
@Controller
public class PopupController{
    @Autowired
    WorkingService workingService;

    @Autowired
    UserService userService;

    @Autowired
    HttpServletRequest request;
//    /*
//     * ポップアップ表示処理
//     */
//    @PutMapping("/dateWork/{checkId}")
//    public ModelAndView popupContent(@PathVariable(required = false) String checkId,
//                                     @RequestParam(name = "CheckUserId", required = false) String CheckUserId,
//                                     ModelAndView mav) {
//        // ユーザ情報の単品取得
//        int userId = Integer.parseInt(CheckUserId);
//        UserForm userDate = userService.findUserDateById(userId);
//
//        // ↓勤怠情報関連の操作
//        WorkingForm dateDate = new WorkingForm();
//        int id = Integer.parseInt(checkId);
//        // id(ユーザテーブルの主キー)を使ってその人の表示させたい情報を取得する
//        dateDate = workingService.findDateDate(id);
//        // viewするデータ
//        mav.addObject("dateDate", dateDate);
//        mav.addObject("userData", userDate);
//        return mav;
//    }

    /*
     * 日毎勤怠情報編集機能
     */
    @PostMapping("")
    public ModelAndView Content(@ModelAttribute("@@") WorkingForm workingForm) {

        workingService.saveForm(workingForm);
        return new ModelAndView("redirect:/ISA");
    }
}
