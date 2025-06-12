package com.example.ISA.repository;

import com.example.ISA.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /*
     * ログイン処理
     */
    public User findByAccount(String account);

    // 申請状況をチェック
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id ")
    public void saveStatusById(@Param("id") int id, @Param("status") int status);
}
