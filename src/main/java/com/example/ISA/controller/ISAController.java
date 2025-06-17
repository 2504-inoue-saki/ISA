package com.example.ISA.controller;

import com.example.ISA.controller.form.UserForm;
import com.example.ISA.controller.form.WorkingForm;
import com.example.ISA.controller.form.WorkingMonthForm;
import com.example.ISA.repository.entity.Calendar;
import com.example.ISA.service.WorkingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/ISA")
public class ISAController {

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @Autowired
    private WorkingService workingService;

    private static final Logger logger = LoggerFactory.getLogger(ISAController.class);


    // ホーム画面表示処理
    // URLに年月がない場合は現在の月を表示
    @GetMapping
    public String home(Model model) {
        LocalDate today = LocalDate.now();
        return "redirect:/ISA/" + today.getYear() + "/" + today.getMonthValue();
    }

    // 特定の月でのホーム画面表示処理
    @GetMapping("/{year}/{month}")
    public String showMonthlyWorking(@PathVariable int year, @PathVariable int month, Model model) {
        UserForm loginUser = (UserForm) session.getAttribute("loginUser");

        if (loginUser == null) {
            // ログインしていない場合はログインページへリダイレクト
            return "redirect:/login";
        }

        int loggedInUserId = loginUser.getId();

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("userCategory", loginUser.getCategory());
        model.addAttribute("currentPage", "/ISA/" + year + "/" + month); // ホーム画面のパス

        List<String> filterErrorMessages = (List<String>) session.getAttribute("filterErrorMessages");
        if (filterErrorMessages != null && !filterErrorMessages.isEmpty()) {
            model.addAttribute("filterErrorMessages", filterErrorMessages);
            session.removeAttribute("filterErrorMessages");
        }

        List<WorkingForm> monthlyWorkings = workingService.getMonthlyWorkings(loggedInUserId, year, month);
        WorkingMonthForm workingMonthForm = new WorkingMonthForm(year, month, monthlyWorkings);
        model.addAttribute("workingMonthForm", workingMonthForm);
        model.addAttribute("currentYear", year);
        model.addAttribute("currentMonth", month);

        LocalDate currentMonthDate = LocalDate.of(year, month, 1);
        model.addAttribute("prevYear", currentMonthDate.minusMonths(1).getYear());
        model.addAttribute("prevMonth", currentMonthDate.minusMonths(1).getMonthValue());
        model.addAttribute("nextYear", currentMonthDate.plusMonths(1).getYear());
        model.addAttribute("nextMonth", currentMonthDate.plusMonths(1).getMonthValue());

        logger.info("Fetched {} monthly workings for user ID {} in {}-{}:", monthlyWorkings.size(), loggedInUserId, year, month);
        if (monthlyWorkings.isEmpty()) {
            logger.warn("No working data found for user ID {} in {}-{}", loggedInUserId, year, month);
        } else {
            // 各勤怠データの詳細をログ出力して確認
            for (WorkingForm workingDatum : monthlyWorkings) {
                logger.debug("  Working ID: {}, Date: {}, Status: {}, StartWork: {}, EndWork: {}",
                        workingDatum.getId(), workingDatum.getDate(), workingDatum.getStatus(),
                        workingDatum.getStartWork(), workingDatum.getEndWork());
            }
        }

        return "ISA";
    }

    // 特定の日の勤怠フォームを取得するエンドポイント
    @GetMapping("/daily-form")
    public String getDailyWorkingForm(@RequestParam("date") LocalDate date, Model model) {
        UserForm loginUser = (UserForm) session.getAttribute("loginUser");
        if (loginUser == null) {
            // エラーハンドリング: ログインしていない場合は空のフラグメントを返すか、エラーメッセージを返す
            return "fragments/empty :: emptyFragment"; // 仮の空フラグメント
        }

        Integer loggedInUserId = loginUser.getId();

        Optional<WorkingForm> dailyWorkingOptional = workingService.getDailyWorking(loggedInUserId, date);

        WorkingForm workingForm;
        if (dailyWorkingOptional.isPresent()) {
            workingForm = dailyWorkingOptional.get();
        } else {
            // 新規入力の場合、初期値を設定したWorkingFormを作成
            workingForm = new WorkingForm();
            workingForm.setId(0); // 新規なのでIDは0
            workingForm.setUserId(loggedInUserId);
            workingForm.setDate(date);
            workingForm.setStatus(-1); // 未入力
            workingForm.setAttend(0);
            workingForm.setMemo("");
        }

        model.addAttribute("workingForm", workingForm);
        return "fragments/daily_working_form :: dailyWorkingForm"; // 作成したフラグメントを返す
    }
    
    // 勤怠データ保存処理
    @PostMapping("/monthWork/save")
    public String saveMonthlyWorking(@ModelAttribute("workingMonthForm") WorkingMonthForm form, Model model) {
        UserForm loginUser = (UserForm) session.getAttribute("loginUser");

        if (loginUser == null) {
            // ログインしていない場合はログインページへリダイレクト
            return "redirect:/ISA/login";
        }

        Integer loggedInUserId = loginUser.getId();

        workingService.saveOrUpdateMonthlyWorkings(loggedInUserId, form.getWorkings());

        // 保存後、同じ月（または保存した月）のホーム画面にリダイレクト
        return "redirect:/ISA/" + form.getYear() + "/" + form.getMonth();
    }
}
