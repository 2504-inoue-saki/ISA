package com.example.ISA.service;

import com.example.ISA.controller.form.WorkingForm;
import com.example.ISA.repository.WorkingRepository;
import com.example.ISA.repository.entity.Working;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class WorkingService {

    @Autowired
    WorkingRepository workingRepository;
    /*
     * 月毎勤怠登録＆編集画面表示処理
     */
    public List<WorkingForm> findMonthWork(int userId,String startDate,String endDate){

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start;
        Date end;
        try {
            start = sdFormat.parse(startDate + " 00:00:00");
            end = sdFormat.parse(endDate + " 23:59:59");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        List<Working> results = workingRepository.findByUserIdAndDateBetween(userId,start,end);
        //List<Object[]>をList<WorkingForm>に詰め替えるメソッド呼び出し
        return setListWorkingForm(results);
    }
    //List<Object[]>をList<UserForm>に詰め替えるメソッド
    private List<WorkingForm> setListWorkingForm(List<Working> results) {
        List<WorkingForm> formMonth = new ArrayList<>();
        for (Working w: results) {
            WorkingForm formWorking = new WorkingForm();
            formWorking.setId(w.getId());
            formWorking.setUserId(w.getUserId());
            formWorking.setDate(w.getDate());
            formWorking.setAttend(w.getAttend());
            formWorking.setStartWork(w.getStartWork());
            formWorking.setEndWork(w.getEndWork());
            formWorking.setStartBreak(w.getStartBreak());
            formWorking.setEndBreak(w.getEndBreak());
            formWorking.setStatus(w.getStatus());
            formWorking.setMemo(w.getMemo());
            formMonth.add(formWorking);
        }
        return formMonth;
    }

    /*
     * 月毎勤怠登録＆編集処理
     */
    public void saveMonth(WorkingForm monthWork) {

        //引数の型をForm→Entityに変換するメソッド呼び出し
        Working month = setWorkingEntity(monthWork);
        //ユーザー情報を登録/更新
        workingRepository.save(month);
    }
    //型をForm→Entityに変換するメソッド
    private Working setWorkingEntity(WorkingForm monthWork) {
        Working month = new Working();
        month.setId(monthWork.getId());
        month.setUserId(monthWork.getUserId());
        month.setDate(monthWork.getDate());
        month.setAttend(monthWork.getAttend());
        month.setStartWork(monthWork.getStartWork());
        month.setEndWork(monthWork.getEndWork());
        month.setStartBreak(monthWork.getStartBreak());
        month.setEndBreak(monthWork.getEndBreak());
        month.setStatus(monthWork.getStatus());
        month.setMemo(monthWork.getMemo());
        return month;
    }
}
