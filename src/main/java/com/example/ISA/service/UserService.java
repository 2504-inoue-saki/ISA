package com.example.ISA.service;

import com.example.ISA.controller.form.UserForm;
import com.example.ISA.repository.UserRepository;
import com.example.ISA.repository.entity.User;
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
            userForms.add(userForm);
        }
        return userForms;
    }




}
