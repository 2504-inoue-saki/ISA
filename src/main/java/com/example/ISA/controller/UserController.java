package com.example.ISA.controller;

import com.example.ISA.controller.form.UserForm;
import com.example.ISA.groups.AddGroup;
import com.example.ISA.groups.EditGroup;
import com.example.ISA.groups.LoginGroup;
import com.example.ISA.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    @GetMapping("/userAdmin")
    public ModelAndView adminView() {
        ModelAndView mav = new ModelAndView();
        //セッションの獲得
        HttpSession session = request.getSession(true);

        //存在しないidの編集を行う際のエラーメッセージ表示処理
        if (session.getAttribute("errorMessages") != null) {
            //フィルターメッセージをエラーメッセージ用リストに入れる（List<String>に合わせる）
            List<String> errorMessages = (List<String>) session.getAttribute("errorMessages");
            session.removeAttribute("errorMessages");
            //エラーメッセージが詰まったリストをviewに送る
            mav.addObject("errorMessages", errorMessages);
        }

        // 登録してあるユーザ情報を全取得
        List<UserForm> userData = userService.findUserData();
        mav.addObject("users", userData);

        // ヘッダー表示
        // ログイン状態とユーザ区分の判定
        mav.addObject("isLoginPage", false);

        boolean isLoggedIn = true;
        int userCategory = 3;
        mav.addObject("isLoggedIn", isLoggedIn);
        mav.addObject("userCategory", userCategory);

        //ユーザ状態を変更できないようにするためにログインユーザの情報を送る(旭)
        UserForm loginUser = (UserForm) session.getAttribute("loginUser");
        mav.addObject("loginUser", loginUser);

        //画面遷移先指定
        mav.setViewName("userAdmin");
        //フォワード
        return mav;
    }

    // ユーザ登録画面表示
    @GetMapping("/signup")
    public ModelAndView signup() {
        ModelAndView mav = new ModelAndView();
        //空のインスタンスを用意しておく
        UserForm addUser = new UserForm();
        mav.addObject("addUser", addUser);

        // ヘッダー表示
        // ログイン状態とユーザ区分の判定
        mav.addObject("isLoginPage", false);

        mav.setViewName("signup");
        return mav;
    }

    //ユーザ新規登録処理
    @PostMapping("/userAdd")
    //リクエストパラメータの取得
    public ModelAndView userAddProcessContent(@Validated({AddGroup.class}) @ModelAttribute("addUser") UserForm addUser, BindingResult result) {
        ModelAndView mav = new ModelAndView();

        //リクエストパラメータの必須＆文字数＆半角文字チェック
        if (result.hasErrors()) {
            //エラーメッセージを入れる用のリストを作っておく
            List<String> errorMessages = new ArrayList<String>();

            for (FieldError error : result.getFieldErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }

            mav.addObject("errorMessages", errorMessages);

            // 画面遷移先を指定
            mav.setViewName("/signup");
            return mav;
        }

        //妥当性チェック①パスワードと確認用パスワードが異なる時にエラーメッセージ
        if (!addUser.getPassword().equals(addUser.getCheckPassword())) {
            //エラーメッセージを入れる用のリストを作っておく
            List<String> errorMessages = new ArrayList<String>();
            errorMessages.add(E0010);
            //エラーメッセージが詰まったリストをviewに送る
            mav.addObject("errorMessages", errorMessages);
            // 画面遷移先を指定
            mav.setViewName("/signup");
            return mav;
        }

        // アカウント名の重複チェック
        if (userService.existCheck(addUser.getAccount())) {
            //エラーメッセージを入れる用のリストを作っておく
            List<String> errorMessages = new ArrayList<String>();
            errorMessages.add(E0009);
            //エラーメッセージが詰まったリストをviewに送る
            mav.addObject("errorMessages", errorMessages);
            // 画面遷移先を指定
            mav.setViewName("/signup");
            return mav;
        }

        addUser.setStopped(true);
        //入力された情報を登録しに行く
        userService.saveUser(addUser);
        //ユーザー管理画面へリダイレクト
        return new ModelAndView("redirect:/userAdmin");
    }

    // ユーザ編集画面表示
    @GetMapping({"/userEdit/{checkId}", "/userEdit/"})
    public ModelAndView editUser(@PathVariable(required = false) String checkId) {
        ModelAndView mav = new ModelAndView();
        HttpSession session = request.getSession(true);
        if (StringUtils.isBlank(checkId) || !checkId.matches("^[0-9]*$")) {
            //エラーメッセージを入れる用のリストを作っておく
            List<String> errorMessages = new ArrayList<String>();
            errorMessages.add(E0025);
            //エラーメッセージが詰まったセッションを用意
            session.setAttribute("errorMessages", errorMessages);
            //ユーザ管理画面へリダイレクト
            return new ModelAndView("redirect:/userAdmin");
        }

        int id = Integer.parseInt(checkId);
        UserForm editUser = userService.findUserDateById(id);

        if (editUser == null) {
            //エラーメッセージを入れる用のリストを作っておく
            List<String> errorMessages = new ArrayList<String>();
            errorMessages.add(E0025);
            //エラーメッセージが詰まったセッションを用意
            session.setAttribute("errorMessages", errorMessages);
            //ユーザ管理画面へリダイレクト
            return new ModelAndView("redirect:/userAdmin");
        }
        //ユーザ区分を変更できないようにするためにログインユーザの情報を送る(旭)
        UserForm loginUser = (UserForm) session.getAttribute("loginUser");
        mav.addObject("loginUser", loginUser);

        mav.addObject("editUser", editUser);
        mav.setViewName("userEdit");

        // ヘッダー表示
        // ログイン状態とユーザ区分の判定
        mav.addObject("isLoginPage", false);

        boolean isLoggedIn = true;
        int userCategory = 3;
        mav.addObject("isLoggedIn", isLoggedIn);
        mav.addObject("userCategory", userCategory);

        return mav;
    }

    /*
     * ユーザ編集処理
     */
    @PutMapping("/userEdit")
    public ModelAndView editUser(@Validated({EditGroup.class}) @ModelAttribute("editUser") UserForm editUser, BindingResult result) {
        ModelAndView mav = new ModelAndView();
        HttpSession session = request.getSession();
        //エラーメッセージを入れる用のリストを作っておく
        List<String> errorMessages = new ArrayList<String>();
        //リクエストパラメータの必須＆文字数＆半角文字チェック（パスワード以外）
        if (result.hasErrors()) {
            //result.getFieldErrors()はresultの持つ全エラーを要素にしたリスト→型はList<FieldError>
            //要素を1つ取り出してerrorに代入して処理→全ての要素が尽きるまで繰り返す
            for (FieldError error : result.getFieldErrors()) {
                //error.getDefaultMessage()で取得したエラーメッセージをリストに追加
                errorMessages.add(error.getDefaultMessage());
            }
        }

        //パスワードの入力がある場合は文字数&半角チェック
        String password = editUser.getPassword();
        if (!StringUtils.isEmpty(password) &&
                ((password.length() < 6 || password.length() > 20) || password.matches("^[!-~]$"))) {
            errorMessages.add(E0005);
        }

        if (errorMessages.size() >= 1){
            //エラーメッセージが詰まったリストをviewに送る
            mav.addObject("errorMessages", errorMessages);
            // 画面遷移先を指定
            mav.setViewName("/userEdit");
            return mav;
        }

        //妥当性チェック①パスワードと確認用パスワードが異なる時にエラーメッセージ
        if (!editUser.getPassword().equals(editUser.getCheckPassword())) {
            errorMessages.add(E0010);
            //エラーメッセージが詰まったリストをviewに送る
            mav.addObject("errorMessages", errorMessages);
            // 画面遷移先を指定
            mav.setViewName("/userEdit");
            return mav;
        }

        //●重複チェック→同じアカウント名が存在かつidが異なる場合エラーメッセージ
        if (userService.existCheck(editUser.getAccount()) && (editUser.getId() != userService.findId(editUser.getAccount()))) {
            errorMessages.add(E0009);
            //エラーメッセージが詰まったリストをviewに送る
            mav.addObject("errorMessages", errorMessages);
            // 画面遷移先を指定
            mav.setViewName("/userEdit");
            return mav;
        }

        //もしログインユーザの部署IDが1の時
        //→リクエストで取得した部署IDが1以外になっていたらOUT
        //→フォワードで編集画面のエラーメッセージを表示

        //入力された情報を更新しに行く
        userService.saveUser(editUser);
        //ユーザー管理画面へリダイレクト
        return new ModelAndView("redirect:/userAdmin");
    }

    /*
     * ユーザ復活・停止処理
     */
    @PutMapping("/switch/{id}")
    public ModelAndView switchStatus(@PathVariable Integer id,
                                     @RequestParam(name = "isStopped", required = false) boolean isStopped) {
        //取得したリクエストをuserにセットする
        UserForm user = new UserForm();
        user.setId(id);
        user.setStopped(isStopped);
        //ユーザー復活停止状態の更新
        userService.saveIsStopped(user);
        //ユーザー管理画面へリダイレクト
        return new ModelAndView("redirect:/userAdmin");
    }

    /*
     * ユーザ削除処理
     */
    @PostMapping("/userDelete/{id}")
    public ModelAndView delete(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        HttpSession session = request.getSession();

        //入力された情報を更新しに行く
        userService.delete(id);
        //ユーザー管理画面へリダイレクト
        return new ModelAndView("redirect:/userAdmin");
    }

}
