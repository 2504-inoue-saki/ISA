package com.example.ISA.controller;

import com.example.ISA.controller.form.UserForm;
import com.example.ISA.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.ISA.constfolder.ErrorMessage.*;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    HttpServletRequest request;

    // ユーザ管理画面表示
//    @GetMapping("/userAdmin")
//    public ModelAndView adminView() {
//        ModelAndView mav = new ModelAndView();
//        //セッションの獲得
//        HttpSession session = request.getSession(true);
//        //存在しないidの編集を行う際のエラーメッセージ表示処理
//        if (session.getAttribute("errorMessages") != null) {
//            //フィルターメッセージをエラーメッセージ用リストに入れる（List<String>に合わせる）
//            List<String> errorMessages = (List<String>) session.getAttribute("errorMessages");
//            session.removeAttribute("errorMessages");
//            //エラーメッセージが詰まったリストをviewに送る
//            mav.addObject("errorMessages", errorMessages);
//        }
//        // 登録してあるユーザ情報を取得
//        List<UserForm> userData = userService.findUserData();
//        mav.addObject("users", userData);
//
//        // ヘッダー表示
//        // ログイン状態とユーザ区分の判定
//        mav.addObject("isLoginPage", false);
//
//        boolean isLoggedIn = true;
//        int userCategory = 3;
//        mav.addObject("isLoggedIn", isLoggedIn);
//        mav.addObject("userCategory", userCategory);
//
//        //画面遷移先指定
//        mav.setViewName("userAdmin");
//        //フォワード
//        return mav;
//    }
//
//    // ユーザ登録画面表示
//    @GetMapping("/signup")
//    public ModelAndView signup() {
//        ModelAndView mav = new ModelAndView();
//        //空のインスタンスを用意しておく
//        UserForm addUser = new UserForm();
//        mav.addObject("addUser", addUser);
//
//        // ヘッダー表示
//        // ログイン状態とユーザ区分の判定
//        mav.addObject("isLoginPage", false);
//
//        boolean isLoggedIn = true;
//        int userCategory = 3;
//        mav.addObject("isLoggedIn", isLoggedIn);
//        mav.addObject("userCategory", userCategory);
//
//        mav.setViewName("signup");
//        return mav;
//    }
//
//    // ユーザ編集画面表示
//    @GetMapping({"/userEdit/{checkId}", "/userEdit/"})
//    public ModelAndView editUser(@PathVariable(required = false) String checkId) {
//        ModelAndView mav = new ModelAndView();
//        HttpSession session = request.getSession(true);
//        if (StringUtils.isBlank(checkId) || !checkId.matches("^[0-9]*$")) {
//            //エラーメッセージを入れる用のリストを作っておく
//            List<String> errorMessages = new ArrayList<String>();
//            errorMessages.add(E0025);
//            //エラーメッセージが詰まったセッションを用意
//            session.setAttribute("errorMessages", errorMessages);
//            //ユーザ管理画面へリダイレクト
//            return new ModelAndView("redirect:/userAdmin");
//        }
//
//        int id = Integer.parseInt(checkId);
//        UserForm editUser = userService.findEditUser(id);
//
//        if (editUser == null) {
//            //エラーメッセージを入れる用のリストを作っておく
//            List<String> errorMessages = new ArrayList<String>();
//            errorMessages.add(E0025);
//            //エラーメッセージが詰まったセッションを用意
//            session.setAttribute("errorMessages", errorMessages);
//            //ユーザ管理画面へリダイレクト
//            return new ModelAndView("redirect:/userAdmin");
//        }
//
//        mav.addObject("editUser", editUser);
//        mav.setViewName("userEdit");
//
//        // ヘッダー表示
//        // ログイン状態とユーザ区分の判定
//        mav.addObject("isLoginPage", false);
//
//        boolean isLoggedIn = true;
//        int userCategory = 3;
//        mav.addObject("isLoggedIn", isLoggedIn);
//        mav.addObject("userCategory", userCategory);
//
//        return mav;
//    }

    // ユーザ新規登録処理
//    @PostMapping("/userAdd")
//    //リクエストパラメータの取得
//    public ModelAndView userAddProcessContent(@Validated({AddGroup.class}) @ModelAttribute("addUser") UserForm addUser, BindingResult result) {
//
//        ModelAndView mav = new ModelAndView();
//        //リクエストパラメータの必須＆文字数＆半角文字チェック
//        if (result.hasErrors()) {
//            //エラーメッセージを入れる用のリストを作っておく
//            List<String> errorMessages = new ArrayList<String>();
//
//            for (FieldError error : result.getFieldErrors()) {
//                errorMessages.add(error.getDefaultMessage());
//            }
//
//            mav.addObject("errorMessages", errorMessages);
//
//            // 画面遷移先を指定
//            mav.setViewName("/userAdd");
//            return mav;
//        }
//
//        //妥当性チェック①パスワードと確認用パスワードが異なる時にエラーメッセージ
//        if (!addUser.getPassword().equals(addUser.getCheckPassword())) {
//            //エラーメッセージを入れる用のリストを作っておく
//            List<String> errorMessages = new ArrayList<String>();
//            errorMessages.add(E0018);
//            //エラーメッセージが詰まったリストをviewに送る
//            mav.addObject("errorMessages", errorMessages);
//            // 画面遷移先を指定
//            mav.setViewName("/userAdd");
//            return mav;
//        }
//
//        // アカウント名の重複チェック
//        if (userService.existCheck(addUser.getAccount())) {
//            //エラーメッセージを入れる用のリストを作っておく
//            List<String> errorMessages = new ArrayList<String>();
//            errorMessages.add(E0015);
//            //エラーメッセージが詰まったリストをviewに送る
//            mav.addObject("errorMessages", errorMessages);
//            // 画面遷移先を指定
//            mav.setViewName("/userAdd");
//            return mav;
//        }
//
//        //今の時間をセット
//        LocalDateTime now = LocalDateTime.now();
//        addUser.setCreatedDate(now);
//        addUser.setUpdatedDate(addUser.getCreatedDate());
//
//        //入力された情報を登録しに行く
//        userService.saveUser(addUser);
//        //ユーザー管理画面へリダイレクト
//        return new ModelAndView("redirect:/userAdmin");
//    }
}
