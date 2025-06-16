package com.example.ISA.controller;

import com.example.ISA.controller.form.UserForm;
import com.example.ISA.controller.form.WorkingForm;
import com.example.ISA.service.WorkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static com.example.ISA.constfolder.ErrorMessage.E0025;
@Controller
public class MonthWorkController {
    @Autowired
    WorkingService workingService;
    /*
     * 月毎勤怠情報編集画面表示処理
     */
    @GetMapping("/monthWork/{id}")
    public ModelAndView monthWork(@PathVariable String id, @RequestParam (name = "start", defaultValue = "2025-06-01 ") String startDate, @RequestParam(name = "end", defaultValue = "2100-12-31 ") String endDate) {
        ModelAndView mav = new ModelAndView();
//        if(StringUtils.isBlank(id) || !id.matches("^[0-9]*$")) {
//            //エラーメッセージを入れる用のリストを作っておく
//            List<String> errorMessages = new ArrayList<String>();
//            errorMessages.add(E0025);
//            //エラーメッセージが詰まったセッションを用意
//            session.setAttribute("errorMessages", errorMessages);
//            //ユーザ管理画面へリダイレクト
//            return new ModelAndView("redirect:/userAdmin");
//        }

        int userID = Integer.parseInt(id);
        List<WorkingForm> monthWork = workingService.findMonthWork(userID,startDate,endDate);

        mav.addObject("monthWork", monthWork);
        mav.setViewName("/monthWork");

        return mav;
    }
    /*
     * 月毎勤怠情報登録処理
     */
    @PutMapping("/monthWork/save")
    public ModelAndView monthUpdate(@ModelAttribute("month")WorkingForm monthWork){
        ModelAndView mav = new ModelAndView();


        workingService.saveMonth(monthWork);

        //ユーザー管理画面へリダイレクト
        return new ModelAndView("redirect:/ISA/");
    }
}
