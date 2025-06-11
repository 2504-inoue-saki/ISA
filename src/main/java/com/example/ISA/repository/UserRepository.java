package com.example.ISA.repository;

import com.example.ISA.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /*
     * ログイン処理
     */
    public User findByAccount(String account);


}
