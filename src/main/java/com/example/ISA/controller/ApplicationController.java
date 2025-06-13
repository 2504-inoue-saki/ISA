package com.example.ISA.controller;

import com.example.ISA.Dto.AllForm;
import com.example.ISA.controller.form.UserForm;
import com.example.ISA.controller.form.WorkingForm;
import com.example.ISA.service.UserService;
import com.example.ISA.service.WorkingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
        // workingテーブルからユーザIDと申請ステータスを取得
        List<WorkingForm> workingDate = workingService.findWorkingDate();
        // 申請状況をチェック(100人までしか登録できない)
        for (int i = 1; i <= 100; i++){
            List<Integer> userIds = new ArrayList<>();
            for (WorkingForm data: workingDate){
                if (data.getId() == i ){
                    userIds.add(i);
                }
            }
            int userStatus = 2; //ユーザの申請が全て承認状態
            for (Integer userId: userIds){
                int status = 0; //とある１日が未申請状態
                if (workingService.existCheckByUserIdAndStatus(userId,status)){
                    userStatus = 0; //ユーザの申請が少なくとも1つは未申請
                    break;
                }
                status = 1; //とある１日が申請状態
                if (workingService.existCheckByUserIdAndStatus(userId,status)){
                    userStatus = 1; //ユーザの申請が少なくとも1つは申請
                }
                status = 3; //とある１日が差し戻し状態 userStatusが0でここに来る事はない
                if (workingService.existCheckByUserIdAndStatus(userId,status)){
                    if (userStatus != 1){
                        userStatus = 3; //ユーザの申請が少なくとも1つは差し戻し
                    }
                }
            }
            userService.saveStatus(i,userStatus);
        }
        // ユーザ情報の全権取得
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
        // 名前とアカウント名用
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
    @PostMapping("/approval/{subjectId}")
    public ModelAndView approval(@PathVariable(required = false) String subjectId,
                                 @RequestParam(name = "approval", required = false) int status,
                                 @RequestParam(name = "checkId", required = false) String checkId) {
        ModelAndView mav = new ModelAndView();
        int id = Integer.parseInt(subjectId);
        //取得したリクエストをworkingFormにセットする
        WorkingForm workingForm = new WorkingForm();
        workingForm.setId(id);
        workingForm.setStatus(status);
        //workingテーブルのステータスを更新
        workingService.saveStatus(workingForm);
        //個人申請詳細画面へリダイレクト
        return new ModelAndView("redirect:/applicationPrivate/" + checkId);
    }
}
