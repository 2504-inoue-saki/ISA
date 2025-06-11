package com.example.ISA.controller;

import com.example.ISA.Dto.AllForm;
import com.example.ISA.Dto.UserWorking;
import com.example.ISA.controller.form.CalendarForm;
import com.example.ISA.controller.form.UserForm;
import com.example.ISA.service.CalendarService;
import com.example.ISA.service.UserService;
import com.example.ISA.service.WorkingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Indexed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ApplicationController {
    @Autowired
    UserService userService;

    @Autowired
    WorkingService workingService;

    @Autowired
    HttpServletRequest request;

    /*
     * 申請一覧画面表示処理
     */
    @GetMapping("/application")
    public ModelAndView applicationView() {
        ModelAndView mav = new ModelAndView();

        // 登録してあるユーザー勤怠情報を取得
        List<UserWorking> userWorkingData = userService.findUserWorkingDate();

        // 申請状況をチェック
        int status = 0; //0は未申請
        for (int i = 1; i <= 100; i++){
            List<Integer> userIds = new ArrayList<>();
            for (UserWorking data: userWorkingData){
                if (data.getId() == i ){
                    userIds.add(i);
                }
            }
            int userStatus = 1;
            for (Integer userId: userIds){
                if (workingService.existCheckByUserIdAndStatus(userId,status)){
                    userStatus = 0;
                    break;
                }
            }
            userService.saveStatus(i,userStatus);
        }
        // ユーザ情報の取得
        List<UserForm> userData = userService.findUserDate();

        // viewするデータ
        mav.addObject("users", userData);
        // 画面遷移先指定
        mav.setViewName("/application");

        // ヘッダー表示処理
        // ログアウトボタンを表示する
        mav.addObject("isLoginPage", false);
        //フォワード
        return mav;
    }

    /*
     * 個人申請詳細画面表示処理
     */
    @GetMapping("/applicationPrivate/{checkId}")
    public ModelAndView applicationPrivateView(@PathVariable(required = false) String checkId) {
        ModelAndView mav = new ModelAndView();

        int id = Integer.parseInt(checkId);
        // id(ユーザテーブルの主キー)を使ってその人の表示させたい情報を取得する
        List<AllForm> userData = workingService.findWorkDate(id);
        // viewするデータ
        mav.addObject("userData", userData.get(0));
        mav.addObject("workingData", userData);
        //画面遷移先指定
        mav.setViewName("/applicationPrivate");

        // ヘッダー表示処理
        // ログアウトボタンを表示する
        mav.addObject("isLoginPage", false);
        //フォワード
        return mav;
    }
    /*
     * 個人申請承認処理
     */
}
