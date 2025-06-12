package com.example.ISA.service;

import com.example.ISA.Dto.AllForm;
import com.example.ISA.controller.form.WorkingForm;

import com.example.ISA.repository.WorkingRepository;
import com.example.ISA.repository.entity.Working;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class WorkingService {
    @Autowired
    WorkingRepository workingRepository;

    /*
     * 申請一覧画面表示処理
     */
    public List<WorkingForm> findWorkingDate() {
        List<Working> workings = workingRepository.findAll();
        //List<Working>をList<UserForm>に詰め替えるメソッド呼び出し
        return setWorkingForm(workings);
    }
    //型をEntity→Formに変換するメソッド
    private List<WorkingForm> setWorkingForm(List<Working> workings) {
        List<WorkingForm> workingForms = new ArrayList<>();
        for (Working value : workings) {
            WorkingForm workingForm = new WorkingForm();
            workingForm.setId(value.getId());
            workingForm.setUserId(value.getUserId());
            workingForm.setDate(value.getDate());
            workingForm.setAttend(value.getAttend());
            workingForm.setStartWork(value.getStartWork());
            workingForm.setEndWork(value.getEndWork());
            workingForm.setStartBreak(value.getStartBreak());
            workingForm.setEndBreak(value.getEndBreak());
            workingForm.setStatus(value.getStatus());
            workingForms.add(workingForm);
        }
        return workingForms;
    }

    /*
     * 個人申請詳細画面表示処理
     */
    public List<AllForm> findWorkDate(int userId){
        List<Object[]> results = workingRepository.findUserDateById(userId);
        //List<Object[]>をList<UserForm>に詰め替えるメソッド呼び出し
        return setListCalendarForm(results);
    }
    //List<Object[]>をList<UserForm>に詰め替えるメソッド
    private List<AllForm> setListCalendarForm(List<Object[]> results) {
        List<AllForm> formAlls = new ArrayList<>();
        for (Object[] objects : results) {
            AllForm formAll = new AllForm();
            formAll.setAccount((String) objects[0]); // account
            formAll.setName((String) objects[1]); // name
            formAll.setId((int) objects[2]); // ID
            formAll.setUserId((int) objects[3]); // userId
            formAll.setAttend((int) objects[4]); // attend
            formAll.setStartWork((String) objects[5]); // startWork
            formAll.setEndWork((String) objects[6]); // endWork
            formAll.setStartBreak((String) objects[7]); // startBreak
            formAll.setEndBreak((String) objects[8]); // endBreak
            //労働時間と休憩時間の計算
            String workDuration = convertToLocalTime(formAll.getStartWork(), formAll.getEndWork());
            String breakDuration = convertToLocalTime(formAll.getStartBreak(), formAll.getEndBreak());
            //formAllにセット
            formAll.setWorkDuration(workDuration);
            formAll.setBreakDuration(breakDuration);

            formAll.setStatus((int) objects[9]); // status

            formAll.setDate((Date) objects[10]); // date
            // Date→LocalDate型に変換してセット
            formAll.setLocalDate(LocalDate.ofInstant(formAll.getDate().toInstant(), ZoneId.systemDefault()));

            formAll.setFiscalYear((int) objects[11]); // fiscalYear
            formAll.setDayOfWeek((String) objects[12]); // dayOfWeek
            formAlls.add(formAll);
        }
        return formAlls;
    }
    //LocalTimeに型変換して差分をStringで取得するメソッド
    public static String convertToLocalTime(String start, String end){
        //LocalTimeに型変換
        LocalTime startTime = LocalTime.parse(start, DateTimeFormatter.ofPattern("[]H:mm"));
        LocalTime endTime = LocalTime.parse(end, DateTimeFormatter.ofPattern("[]H:mm"));
        //差分
        Duration duration = Duration.between(startTime, endTime);
        //Duration→LocalTime型変換
        LocalTime localTime = LocalTime.MIDNIGHT.plus(duration);
        //String→LocalTime型変換して返す
        return DateTimeFormatter.ofPattern("[]H:mm").format(localTime);
    }

    //▲ある1人のユーザの申請状況を確認している
    public boolean existCheckByUserIdAndStatus(int userId, int status){
        if (workingRepository.existsByUserIdAndStatus(userId, status)){
            return true;
        } else {
            return false;
        }
    }
    /*
     * 個人申請承認処理
     */
    public void saveStatus(WorkingForm workingForm){
        workingRepository.saveStatus(workingForm.getId(), workingForm.getStatus());
    }
}
