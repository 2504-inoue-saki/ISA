package com.example.ISA.service;

import com.example.ISA.controller.form.UserForm;
import com.example.ISA.repository.UserRepository;
import com.example.ISA.repository.entity.User;

import io.micrometer.common.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * ログイン処理
     */
    public UserForm findLoginUser(UserForm userForm) {
        //DBへのselect処理
        User selectedUser = userRepository.findByAccount(userForm.getAccount());
        //リクエストから取得したパスワード(平文)とDBから取得したパスワード(暗号化済み)を比較し、異なった場合はnullを返す
        if(selectedUser == null || !passwordEncoder.matches(userForm.getPassword(),selectedUser.getPassword())){
            return null;
        }
        //setUserFormメソッドを使いたいがためにリストに入れる
        List<User> users = new ArrayList<>();
        users.add(selectedUser);
        //引数の型をEntity→Formに変換するメソッド呼び出し
        List<UserForm> userForms = setUserForm(users);
        return userForms.get(0);
    }

    /*
     * ユーザー登録＆編集処理
     */
    public void saveUser(UserForm userForm) {
        //パスワードの入力がある場合は暗号化してセットし直す
        if(!StringUtils.isBlank(userForm.getPassword())){
            //パスワードを暗号化
            String encPassword = passwordEncoder.encode(userForm.getPassword());
            //暗号化したパスワードでセットし直す
            userForm.setPassword(encPassword);
        } else {
            //パスワードの入力ない場合は今のパスワードをDBから取得してセットする
            //DBへのselect処理
            User currentUser = userRepository.findById(userForm.getId()).orElse(null);
            //取得したパスワードをセット
            userForm.setPassword(currentUser.getPassword());
        }
        //引数の型をForm→Entityに変換するメソッド呼び出し
        User user = setUserEntity(userForm);
        //ユーザー情報を登録/更新
        userRepository.save(user);
    }

    /*
     * ●アカウント重複チェック
     */
    public boolean existCheck(String account){
        if (userRepository.existsByAccount(account)){
            return true;
        } else {
            return false;
        }
    }
    //IDが同じならアカウント名が同じでもOKのやつ
    public int findId(String account) {
        //DBへのselect処理
        User selectedUser = userRepository.findByAccount(account);
        return selectedUser.getId();
    }

    /*
     * ユーザ復活・停止処理
     */
    public void saveIsStopped(UserForm user){
        userRepository.saveIsStopped(user.getId(), user.getStopped());
    }

    /*
     * パスワード変更処理
     */
    public void savePassword(String pw,int id) {
        //パスワードを暗号化
        String encPassword = passwordEncoder.encode(pw);
        //ユーザー情報を登録/更新
        userRepository.savePassword(id,encPassword);
    }

    //型をEntity→Formに変換するメソッド
    private List<UserForm> setUserForm(List<User> users) {
        List<UserForm> userForms = new ArrayList<>();
        for (User value : users) {
            UserForm userForm = new UserForm();
            userForm.setId(value.getId());
            userForm.setAccount(value.getAccount());
            userForm.setName(value.getName());
            userForm.setPassword(value.getPassword());
            userForm.setCategory(value.getCategory());
            userForm.setStopped(value.isStopped());
            userForm.setStatus(value.getStatus());
            userForms.add(userForm);
        }
        return userForms;
    }
    //型をForm→Entityに変換するメソッド
    private User setUserEntity(UserForm reqUser) {
        User user = new User();
        user.setId(reqUser.getId());
        user.setAccount(reqUser.getAccount());
        user.setPassword(reqUser.getPassword());
        user.setName(reqUser.getName());
        user.setCategory(reqUser.getCategory());
        user.setStatus(reqUser.getStatus());
        user.setStopped(reqUser.getStopped());
        user.setCreatedDate(reqUser.getCreatedDate());
        user.setUpdatedDate(reqUser.getUpdatedDate());
        return user;
    }

    // 申請状況をチェック
    public void saveStatus(int id, int status){
        userRepository.saveStatusById(id, status);
    }

    // ユーザ情報の全件取得
    public List<UserForm> findUserData() {
        List<User> user = userRepository.findAll();
        //List<User>をList<UserForm>に詰め替えるメソッド呼び出し
        return setUserForm(user);
    }

    // ユーザ情報の単品取得
    public UserForm findUserDateById(int id) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null){
            return null;
        };
        List<User> users = new ArrayList<>();
        users.add(user);
        //List<User>をList<UserForm>に詰め替えるメソッド呼び出し
        return setUserForm(users).get(0);
    }

    /*
     * ユーザ削除処理
     */
    public void delete(int id) {
        userRepository.deleteById(id);
    }
}
