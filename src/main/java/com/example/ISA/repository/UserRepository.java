package com.example.ISA.repository;

import com.example.ISA.repository.entity.User;
import com.example.ISA.repository.entity.Working;
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

    /*
     * アカウント重複チェック
     */
    public boolean existsByAccount(String account);

    /*
     * ユーザ復活・停止処理
     */
    @Modifying
    @Query("UPDATE User u SET u.isStopped = :isStopped, u.updatedDate = CURRENT_TIMESTAMP WHERE u.id = :id")
    public void saveIsStopped(@Param("id") Integer id, @Param("isStopped") boolean isStopped);

    public User findById(int id);
}
