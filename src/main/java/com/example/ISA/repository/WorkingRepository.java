package com.example.ISA.repository;

import com.example.ISA.repository.entity.Working;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import java.util.Date;
import java.util.Optional;


@Repository
public interface WorkingRepository extends JpaRepository<Working, Integer> {

//     月毎勤怠（あさひ）
    public List<Working> findByUserIdAndDateBetween(int userId, Date start, Date end);

    /*
     * 個人申請詳細画面表示処理
     */
    @Query("SELECT " +
            "u.account as account, " +
            "u.name as name, " +
            "w.id as id, " +
            "w.userId as userId, " +
            "w.attend as attend, " +
            "w.startWork as startWork, " +
            "w.endWork as endWork, " +
            "w.startBreak as startBreak, " +
            "w.endBreak as endBreak, " +
            "w.status as status, " +
            "c.date as date, " +
            "c.fiscalYear as fiscalYear, " +
            "c.dayOfWeek as dayOfWeek, " +
            "w.memo as memo " +
            "FROM Calendar c " +
            "INNER JOIN Working w " +
            "ON w.date = c.date " +
            "INNER JOIN User u " +
            "ON w.userId = u.id " +
            "WHERE w.userId = :id " +
            "ORDER BY date ASC ")
    public List<Object[]> findUserDateById(@Param("id") Integer id);

    //▲ある1人のユーザの申請状況を確認している
    public boolean existsByUserIdAndStatus(int userId, int status);

    // ★勤怠登録していない新人さんを弾く用
    public boolean existsByUserId(int userId);

    //♥未申請→申請の更新
    @Transactional
    @Modifying
    @Query("UPDATE Working w SET w.status = 1 WHERE w.id = :id ")
    public void saveApplyById(@Param("id") int id);

    /*
     * 個人申請承認処理
     */
    @Transactional
    @Modifying
    @Query("UPDATE Working w SET w.status = :status, w.updatedDate = CURRENT_TIMESTAMP WHERE w.id = :id")
    public void saveStatus(@Param("id") Integer id, @Param("status") int status);

    //申請処理のヌルチェック
    public Working findById(int id);

    List<Working> findByUserIdAndDateBetweenOrderByDateAsc(Integer userId, LocalDate startDate, LocalDate endDate);

    Optional<Working> findByUserIdAndDate(Integer userId, LocalDate date);
}
