package com.example.ISA.service;

import com.example.ISA.Dto.AllForm;
import com.example.ISA.controller.form.CalendarForm;
import com.example.ISA.controller.form.UserForm;
import com.example.ISA.repository.CalendarRepository;

import com.example.ISA.repository.WorkingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class WorkingService {
    @Autowired
    WorkingRepository workingRepository;

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
            formAll.setAccount((String) objects[0]);
            formAll.setName((String) objects[1]);
            formAll.setId((int) objects[2]);
            formAll.setUserId((int) objects[3]);
            formAll.setAttend((int) objects[4]);
            formAll.setStartWork((String) objects[5]);
            formAll.setEndWork((String) objects[6]);
            formAll.setStartBreak((String) objects[7]);
            formAll.setEndBreak((String) objects[8]);
            formAll.setStatus((int) objects[9]);
            formAll.setDate((Date) objects[10]);
            formAll.setFiscalYear((int) objects[11]);
            formAll.setDayOfWeek((String) objects[12]);
            formAlls.add(formAll);
        }
        return formAlls;
    }

    //▲ある1人のユーザの申請状況を確認している
    public boolean existCheckByUserIdAndStatus(int userId, int status){
        if (workingRepository.existsByUserIdAndStatus(userId, status)){
            return true;
        } else {
            return false;
        }
    }
}
