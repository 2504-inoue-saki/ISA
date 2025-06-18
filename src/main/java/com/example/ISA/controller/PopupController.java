package com.example.ISA.controller;

import com.example.ISA.Dto.AllForm;
import com.example.ISA.controller.form.UserForm;
import com.example.ISA.controller.form.WorkingForm;
import com.example.ISA.groups.LoginGroup;
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
                                @ModelAttribute("workingDatum") WorkingForm workingForm,
                                @RequestParam(name = "checkId", required = false) int userId,
                                @RequestParam(name = "date") LocalDate date) throws ParseException {
        workingForm.setId(id);
        workingForm.setUserId(userId);
        workingForm.setStatus(0);
        workingService.saveForm(workingForm);
      
        int month = date.getMonthValue();
        int year = date.getYear();

        return new ModelAndView("redirect:/ISA/" + year + "/" + month);
    }

    /*
     * 申請処理
     */
    @PostMapping("/ISA/apply/{workingId}")
    public ModelAndView apply(@PathVariable(required = false) int workingId){
        //♥未申請→申請の更新
        workingService.saveUpdateStatusStatus(workingId);

        LocalDate today = LocalDate.now(); // 現在の年月を取得
        return new ModelAndView("redirect:/ISA/" + today.getYear() + "/" + today.getMonthValue());
    }

}
