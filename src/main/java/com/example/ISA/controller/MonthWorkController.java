package com.example.ISA.controller;

import com.example.ISA.controller.form.WorkingForm;
import com.example.ISA.controller.form.WorkingMonthForm;
import com.example.ISA.service.WorkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class MonthWorkController {

    @Autowired
    WorkingService workingService;

    @GetMapping("/monthWork")
    public String showMonthlyInput(@RequestParam(name = "year") int year,
                                   @RequestParam(name = "month") int month,
                                   Model model) {
        // ここでは仮のユーザーIDを使用 (実際には認証情報から取得)
        Integer userId = 1;

        List<WorkingForm> monthlyWorkings = workingService.getMonthlyWorkings(userId, year, month);

        // WorkingFormのリストを保持するための新しいFormオブジェクト
        // これがないと、Thymeleafでリストをバインドするのが難しい
        WorkingMonthForm workingMonthForm = new WorkingMonthForm();
        workingMonthForm.setWorkings(monthlyWorkings);
        workingMonthForm.setUserId(userId); // ユーザーIDもセット
        workingMonthForm.setYear(year); // 年をセット
        workingMonthForm.setMonth(month); // 月をセット

        model.addAttribute("currentYear", year);
        model.addAttribute("currentMonth", month);
        model.addAttribute("userId", userId);
        model.addAttribute("workingMonthForm", workingMonthForm);
        return "monthWork";
    }

    // 月次勤怠データを保存する
    @PostMapping("/monthWork/save")
    public String saveMonthlyInput(@ModelAttribute WorkingMonthForm workingMonthForm,
                                   RedirectAttributes redirectAttributes) {
        // ユーザーID、年、月はWorkingMonthFormから取得
        Integer userId = workingMonthForm.getUserId();
        int year = workingMonthForm.getYear();
        int month = workingMonthForm.getMonth();

        // WorkingServiceにリストを渡して保存/更新処理を呼び出す
        workingService.saveOrUpdateMonthlyWorkings(userId, workingMonthForm.getWorkings());

        return "redirect:/";
    }
}
