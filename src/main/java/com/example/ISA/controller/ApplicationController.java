package com.example.ISA.controller;

import com.example.ISA.Dto.AllForm;
import com.example.ISA.controller.form.UserForm;
import com.example.ISA.controller.form.WorkingForm;
import com.example.ISA.repository.entity.Working;
import com.example.ISA.service.UserService;
import com.example.ISA.service.WorkingService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.example.ISA.constfolder.ErrorMessage.*;

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
    public ModelAndView applicationView(Model model) {
        ModelAndView mav = new ModelAndView();

        //▲自身の個人申請詳細画面のエラメ処理
        //★URLパターンのエラメ
        HttpSession session = request.getSession(true);
        //セッション内にエラーメッセージがある時
        if (session.getAttribute("ErrorMessage") != null) {
            //エラーメッセージを入れる用のリストを作っておく
            List<String> errorMessages = new ArrayList<>();
            //エラーメッセージをエラーメッセージ用リストに入れる（List<String>に合わせる）
            errorMessages.add((String) session.getAttribute("ErrorMessage"));
            //セッション内のエラーメッセージを消す
            session.removeAttribute("ErrorMessage");
            //エラーメッセージが詰まったリストをviewに送る
            mav.addObject("errorMessages", errorMessages);
        }

        // workingテーブルからユーザIDと申請ステータスを取得
        List<WorkingForm> workingDate = workingService.findWorkingDate();
        // 申請状況をチェック(100人までしか登録できない)
        for (int i = 1; i <= 100; i++){
            List<Integer> userIds = new ArrayList<>();
            //ユーザIDがiのデータ数を数える
            for (WorkingForm data: workingDate){
                if (data.getUserId() == i ){
                    //リストにユーザIDをデータ数分追加
                    userIds.add(i);
                }
            }

            int userStatus = 0; //statusが全て0→以下のifに引っかからず守り抜けば全て未申請になる
            // 勤怠登録の無い
            if (userIds.size() == 0){
                userStatus = -1;
            }
            for (Integer userId: userIds){
                int status = 1; //とある１日が申請状態
                if (workingService.existCheckByUserIdAndStatus(userId,status)){
                    userStatus = 1; //ユーザの申請が少なくとも1つは申請
                    break; //statusが1のものはここで打ち切り！
                }
                status = 3; //とある１日が差し戻し状態 statusが1のものはここまでこない
                if (workingService.existCheckByUserIdAndStatus(userId,status)){
                    userStatus = 3; //stetusの少なくとも1つは3
                }
                status = 2; //とある１日が承認状態 statusが1のものはここまでこない
                if (workingService.existCheckByUserIdAndStatus(userId,status)){
                    if (userStatus != 3){ //statusが3のものは除外
                        userStatus = 2; //stetusの少なくとも1つは2
                    }
                }
            }
            userService.saveStatus(i,userStatus);
        }
        // ユーザ情報の全権取得
        List<UserForm> userData = userService.findUserData();

        // viewするデータ
        mav.addObject("users", userData);
        // 画面遷移先指定
        mav.setViewName("/application");

        UserForm loginUser = (UserForm) session.getAttribute("loginUser");
        // ヘッダー用の情報
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("userCategory", loginUser.getCategory());
        model.addAttribute("currentPage", "/application"); // 現在のページのパス

        //フォワード
        return mav;
    }

    /*
     * 個人申請詳細画面表示処理
     */
    @GetMapping({"/applicationPrivate/{checkId}", "/applicationPrivate/"})
    public ModelAndView applicationPrivateView(@PathVariable(required = false) String checkId) {
        ModelAndView mav = new ModelAndView();
        //セッション獲得
        HttpSession session = request.getSession(true);

        //★URLパターンのエラメ
        if(StringUtils.isBlank(checkId) || !checkId.matches("^[0-9]*$")) {
            session.setAttribute("ErrorMessage", E0026);
            //申請一覧画面へリダイレクト
            return new ModelAndView("redirect:/application");
        }
        int id = Integer.parseInt(checkId);

        //▲自身の個人申請詳細画面は表示できないの処理
        Object loginUser = session.getAttribute("loginUser");
        //ログインユーザIDとリクエストのIDが同じ場合
        if (session.getAttribute("loginUser") != null ){
            //型変更
            UserForm loginUserForm = (UserForm) loginUser;
            if (loginUserForm.getId() == id){
                session.setAttribute("ErrorMessage", E0018);
                //申請一覧画面へリダイレクト
                return new ModelAndView("redirect:/application");
            }
        }

        // id(ユーザテーブルの主キー)を使ってその人の表示させたい情報を取得する
        List<AllForm> userData = workingService.findWorkDate(id);

        //★URLパターンのエラメ
        if(userData == null){
            session.setAttribute("ErrorMessage", E0026);
            //申請一覧画面へリダイレクト
            return new ModelAndView("redirect:/application");
        }

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
    @PostMapping("/approval/{id}")
    public ModelAndView approval(@PathVariable(required = false) int id,
                                 @RequestParam(name = "approval", required = false) int status,
                                 @RequestParam(name = "checkId", required = false) String checkId) {
        ModelAndView mav = new ModelAndView();
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
