package com.example.ISA.controller;

import com.example.ISA.controller.form.UserForm;
import com.example.ISA.controller.form.WorkingForm;
import com.example.ISA.controller.form.WorkingMonthForm;
import com.example.ISA.service.WorkingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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

    @Autowired
    HttpSession session;

    @GetMapping("/monthWork")
    public String showMonthlyInput(@RequestParam(name = "year") int year,
                                   @RequestParam(name = "month") int month,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        UserForm loginUser = (UserForm) session.getAttribute("loginUser");
        Integer userId = loginUser.getId();

        WorkingMonthForm workingMonthForm;
        if (model.asMap().containsKey("workingMonthForm")) {
            workingMonthForm = (WorkingMonthForm) model.asMap().get("workingMonthForm");
            // URLパラメータのyear, monthとセッションのuserIdで再設定（または確認）
        } else {
            List<WorkingForm> monthlyWorkings = workingService.getMonthlyWorkings(userId, year, month);
            workingMonthForm = new WorkingMonthForm();
            workingMonthForm.setWorkings(monthlyWorkings);
        }
        workingMonthForm.setUserId(userId);
        workingMonthForm.setYear(year);
        workingMonthForm.setMonth(month);

        model.addAttribute("currentYear", year);
        model.addAttribute("currentMonth", month);
        model.addAttribute("userId", userId);
        model.addAttribute("workingMonthForm", workingMonthForm);

        // ヘッダー用の情報
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("userCategory", loginUser.getCategory());
        model.addAttribute("currentPage", "/monthWork"); // 現在のページのパス

        // リダイレクトされた際にエラーメッセージがあれば取得してModelに追加
        if (redirectAttributes.getFlashAttributes().containsKey("dateErrorMessages")) {
            model.addAttribute("dateErrorMessages", redirectAttributes.getFlashAttributes().get("dateErrorMessages"));
        }
        if (redirectAttributes.getFlashAttributes().containsKey("errorMessage")) {
            model.addAttribute("errorMessage", redirectAttributes.getFlashAttributes().get("errorMessage"));
        }

        return "monthWork";
    }

    // 月次勤怠データを保存する
    @PostMapping("/monthWork/save")
    public String saveMonthlyInput(@Validated
                                   @ModelAttribute WorkingMonthForm workingMonthForm,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        // ユーザーID、年、月はWorkingMonthFormから取得
        Integer userId = workingMonthForm.getUserId();
        int year = workingMonthForm.getYear();
        int month = workingMonthForm.getMonth();

        // WorkingServiceにリストを渡して保存/更新処理を呼び出す
        workingService.saveOrUpdateMonthlyWorkings(userId, workingMonthForm.getWorkings());

        return "redirect:/ISA/" + year + "/" + month;
    }
}
