package com.example.ISA.service;

import com.example.ISA.controller.form.UserForm;
import com.example.ISA.repository.UserRepository;
import com.example.ISA.repository.entity.User;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

//    private final PasswordEncoder passwordEncoder;
//
//    @Autowired
//    public UserService(PasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }

    /*
     * パスワード変更処理
     */
    public void savePassword(UserForm userForm) {
//            //パスワードを暗号化
//            String encPassword = passwordEncoder.encode(userForm.getPassword());
//            //暗号化したパスワードでセットし直す
//            userForm.setPassword(encPassword);

        //引数の型をForm→Entityに変換するメソッド呼び出し
        User user = setUserEntity(userForm);
        //ユーザー情報を登録/更新
        userRepository.save(user);
    }
    //型をForm→Entityに変換するメソッド
    private User setUserEntity(UserForm reqUser) {
        User user = new User();
        user.setPassword(reqUser.getPassword());
        user.setUpdatedDate(reqUser.getUpdatedDate());
        return user;
    }



}
