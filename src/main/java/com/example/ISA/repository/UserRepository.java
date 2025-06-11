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

    /*
     * 申請一覧画面表示処理
     */
    @Query("SELECT " +
            "u.id as id, " +
            "u.account as account, " +
            "u.name as name, " +
            "w.status as status " +
            "FROM User u " +
            "LEFT OUTER JOIN Working w " +
            "ON u.id = w.userId " +
            "ORDER BY id ASC ")
    public List<Object[]> findAllUserDate();

    // 申請状況をチェック
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id ")
    public void saveStatusById(@Param("id") int id, @Param("status") int status);
}
