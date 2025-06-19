package com.example.ISA.controller;

import com.example.ISA.Dto.AllForm;
import com.example.ISA.controller.form.UserForm;
import com.example.ISA.controller.form.WorkingForm;
import com.example.ISA.groups.PopupGroup;
import com.example.ISA.service.UserService;
import com.example.ISA.service.WorkingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.ISA.constfolder.ErrorMessage.*;
@Controller
public class PopupController{
    @Autowired
    WorkingService workingService;

    @Autowired
    UserService userService;

    @Autowired
    HttpServletRequest request;
    /*
     * 日毎勤怠情報編集機能
     */
    @PostMapping("/saveDate/{id}")
    public ModelAndView Content(@PathVariable(required = false) int id,
                                @Validated({PopupGroup.class}) @ModelAttribute("workingDatum") WorkingForm workingForm, BindingResult result,
                                @RequestParam(name = "checkId", required = false) int userId) throws ParseException {
        int month = workingForm.getDate().getMonthValue();
        int year = workingForm.getDate().getYear();

        //エラーメッセージを入れる用のリストを作っておく
        List<String> errorMessages = new ArrayList<>();
        //必須チェック
        if (result.hasErrors()) {
            //result.getFieldErrors()はresultの持つ全エラーを要素にしたリスト→型はList<FieldError>
            //要素を1つ取り出してerrorに代入して処理→全ての要素が尽きるまで繰り返す
            for (FieldError error : result.getFieldErrors()) {
                //error.getDefaultMessage()で取得したエラーメッセージをリストに追加
                errorMessages.add(error.getDefaultMessage());
            }
        }
        //休憩の入力がある場合は文字数&半角チェック
        String start = workingForm.getStartBreak();
        String end = workingForm.getEndBreak();
        if (!StringUtils.isEmpty(start) && !StringUtils.isEmpty(end)){
            if ((start.length() < 3 || start.length() > 5) || !start.matches("^([0-9]|[01][0-9]|2[0-3]):[0-5][0-9]$")
                    || (end.length() < 3 || end.length() > 5) || !end.matches("^([0-9]|[01][0-9]|2[0-3]):[0-5][0-9]$")){
                errorMessages.add(E0015);
            }
        } else if (StringUtils.isEmpty(start) && !StringUtils.isEmpty(end)) {
            errorMessages.add(E0013);
        } else if (!StringUtils.isEmpty(start) && StringUtils.isEmpty(end)) {
            errorMessages.add(E0014);
        }
        HttpSession session =request.getSession(true);
        if (errorMessages.size() >= 1){
            //エラーメッセージが詰まったリストをviewに送る
            session.setAttribute("popupErrorMessages", errorMessages);
            return new ModelAndView("redirect:/ISA/" + year + "/" + month);
        }
        workingForm.setUserId(userId);
        workingForm.setStatus(0);
        workingService.saveForm(workingForm);

        return new ModelAndView("redirect:/ISA/" + year + "/" + month);
    }

    /*
     * 申請処理
     */
    @PostMapping("/ISA/apply/{workingId}")
    public ModelAndView apply(@PathVariable(required = false) int workingId){

        //申請処理のヌルチェック
        WorkingForm workingDate = workingService.findDateDate(workingId);
        if (StringUtils.isEmpty(workingDate.getStartWork()) || StringUtils.isEmpty(workingDate.getEndWork()) || StringUtils.isEmpty(workingDate.getStartBreak()) || StringUtils.isEmpty(workingDate.getEndBreak())){
            HttpSession session =request.getSession(true);
            session.setAttribute("errorMessage", E0017);
            LocalDate today = LocalDate.now(); // 現在の年月を取得
            return new ModelAndView("redirect:/ISA/" + today.getYear() + "/" + today.getMonthValue());
        }

        //♥未申請→申請の更新
        workingService.saveUpdateStatusStatus(workingId);
        LocalDate today = LocalDate.now(); // 現在の年月を取得
        return new ModelAndView("redirect:/ISA/" + today.getYear() + "/" + today.getMonthValue());
    }

}
