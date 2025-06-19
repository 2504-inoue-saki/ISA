package com.example.ISA.service;

import com.example.ISA.Dto.AllForm;
import com.example.ISA.controller.form.WorkingForm;
import com.example.ISA.repository.CalendarRepository;
import com.example.ISA.repository.WorkingRepository;
import com.example.ISA.repository.entity.Working;
import com.example.ISA.repository.entity.Calendar;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkingService {

    @Autowired
    private WorkingRepository workingRepository;

    @Autowired
    private CalendarRepository calendarRepository;

    // 休憩時間と労働時間を計算
    private void calculateAndSetTimes(WorkingForm form, Working existingWorking) {
        LocalTime startWork = null;
        LocalTime endWork = null;
        LocalTime startBreak = null;
        LocalTime endBreak = null;

        try {
            // StringからLocalTimeへ変換
            if (existingWorking.getStartWork() != null && !existingWorking.getStartWork().isEmpty()) {
                startWork = LocalTime.parse(existingWorking.getStartWork());
            }
            if (existingWorking.getEndWork() != null && !existingWorking.getEndWork().isEmpty()) {
                endWork = LocalTime.parse(existingWorking.getEndWork());
            }
            if (existingWorking.getStartBreak() != null && !existingWorking.getStartBreak().isEmpty()) {
                startBreak = LocalTime.parse(existingWorking.getStartBreak());
            }
            if (existingWorking.getEndBreak() != null && !existingWorking.getEndBreak().isEmpty()) {
                endBreak = LocalTime.parse(existingWorking.getEndBreak());
            }
        } catch (DateTimeParseException e) {
            // パースエラーが発生した場合のハンドリング
            System.err.println("時刻のパースエラー: " + e.getMessage() + " - データ: " +
                    existingWorking.getStartWork() + ", " +
                    existingWorking.getEndWork() + ", " +
                    existingWorking.getStartBreak() + ", " +
                    existingWorking.getEndBreak());
            form.setBreakTimeDisplay("エラー");
            form.setWorkingTimeDisplay("エラー");
            return; // 計算処理を中断
        } catch (Exception e) {
            form.setBreakTimeDisplay("エラー");
            form.setWorkingTimeDisplay("エラー");
            return;
        }

        // 休憩時間の計算
        long breakMinutes = 0;
        if (startBreak != null && endBreak != null && startBreak.isBefore(endBreak)) {
            Duration breakDuration = Duration.between(startBreak, endBreak);
            breakMinutes = breakDuration.toMinutes();

            long hours = breakMinutes / 60;
            long minutes = breakMinutes % 60;
            form.setBreakTimeDisplay(String.format("%d:%02d", hours, minutes));
        } else {
            form.setBreakTimeDisplay("0:00"); // 休憩がない場合
        }

        // 労働時間の計算
        if (startWork != null && endWork != null && startWork.isBefore(endWork)) {
            Duration totalWorkDuration = Duration.between(startWork, endWork);
            long totalWorkMinutes = totalWorkDuration.toMinutes();

            // 休憩時間は先に計算されているので、その結果を利用
            long actualWorkingMinutes = totalWorkMinutes - breakMinutes;

            if (actualWorkingMinutes < 0) actualWorkingMinutes = 0;

            long hours = actualWorkingMinutes / 60;
            long minutes = actualWorkingMinutes % 60;
            form.setWorkingTimeDisplay(String.format("%d:%02d", hours, minutes));
        } else {
            form.setWorkingTimeDisplay("0:00");
        }
    }

    public List<WorkingForm> getMonthlyWorkings(Integer userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        List<Working> existingWorkings = workingRepository.findByUserIdAndDateBetweenOrderByDateAsc(userId,
                startDate, endDate);

        Map<LocalDate, Working> workingMap = existingWorkings.stream()
                .collect(Collectors.toMap(
                        Working::getDate,
                        w -> w
                ));

        List<Calendar> monthlyCalendars = calendarRepository.findByDateBetweenOrderByDateAsc(startDate, endDate);
        Map<LocalDate, Calendar> calendarMap = monthlyCalendars.stream()
                .collect(Collectors.toMap(Calendar::getDate, c -> c));

        List<WorkingForm> monthlyWorkingForms = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            WorkingForm form = new WorkingForm();
            form.setUserId(userId);
            form.setDate(date);

            Calendar calendarEntry = calendarMap.get(date);
            if (calendarEntry != null) {
                form.setDayOfWeek(calendarEntry.getDayOfWeek());
            } else {
                form.setDayOfWeek(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPAN));
            }

            Working existingWorking = workingMap.get(date);
            if (existingWorking != null) {
                // BeanUtils.copyProperties で memo を含む既存のプロパティが form にコピーされる
                BeanUtils.copyProperties(existingWorking, form);
                // calculateAndSetTimes は時間表示フィールドのみを更新する
                calculateAndSetTimes(form, existingWorking);
            } else {
                // 初期値をセット
                form.setId(0);
                form.setStatus(0);
                form.setAttend(-1);
                form.setBreakTimeDisplay("0:00");
                form.setWorkingTimeDisplay("0:00");
                form.setMemo(""); // 新規作成時は空のメモ
            }
            monthlyWorkingForms.add(form);
        }

        return monthlyWorkingForms;
    }

    // 単一日の勤怠データを取得するメソッド
    public Optional<WorkingForm> getDailyWorking(Integer userId, LocalDate date) {
        Optional<Working> workingOptional = workingRepository.findByUserIdAndDate(userId, date);

        WorkingForm form = new WorkingForm();
        if (workingOptional.isPresent()) {
            Working existingWorking = workingOptional.get();
            BeanUtils.copyProperties(existingWorking, form);
            calculateAndSetTimes(form, existingWorking);
        } else {
            // 初期値をセット
            form.setId(0);
            form.setUserId(userId);
            form.setDate(date);
            form.setStatus(0);
            form.setAttend(-1);
            form.setBreakTimeDisplay("0:00");
            form.setWorkingTimeDisplay("0:00");
            form.setMemo("");
        }

        Optional<Calendar> calendarOptional = calendarRepository.findByDate(date);
        if (calendarOptional.isPresent()) {
            Calendar calendarEntry = calendarOptional.get();
            form.setDayOfWeek(calendarEntry.getDayOfWeek());
        } else {
            form.setDayOfWeek(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPAN));
        }

        return Optional.of(form);
    }

    // 月ごとの勤怠データ登録・更新処理
    @Transactional
    public void saveOrUpdateMonthlyWorkings(int userId, List<WorkingForm> dailyForms) {
        for (WorkingForm dailyForm : dailyForms) {
            LocalDate workingDate = dailyForm.getDate();

            Optional<Working> existingWorking = Optional.empty();
            if (dailyForm.getId() != null && dailyForm.getId() > 0) {
                existingWorking = workingRepository.findById(dailyForm.getId());
            } else {
                existingWorking = workingRepository.findByUserIdAndDate(userId, workingDate);
            }

            Working workingEntity;
            if (existingWorking.isPresent()) {
                workingEntity = existingWorking.get();
            } else {
                workingEntity = new Working();
                workingEntity.setUserId(userId);
            }
            workingEntity.setDate(workingDate);

            if (dailyForm.getAttend() == null) {
                workingEntity.setAttend(-1);
            } else {
                workingEntity.setAttend(dailyForm.getAttend());
            }

            workingEntity.setStartWork(dailyForm.getStartWork());
            workingEntity.setEndWork(dailyForm.getEndWork());
            workingEntity.setStartBreak(dailyForm.getStartBreak());
            workingEntity.setEndBreak(dailyForm.getEndBreak());

            if (dailyForm.getStatus() == null) {
                //変更箇所
                workingEntity.setStatus(-1);
            } else {
                // ★★★
                workingEntity.setStatus(dailyForm.getStatus());
            }

            workingEntity.setMemo(dailyForm.getMemo());

            workingRepository.save(workingEntity);
        }
    }

    //List<Object[]>をList<UserForm>に詰め替えるメソッド（旭）
    private List<WorkingForm> setListWorkingForm(List<Working> results) {
        List<WorkingForm> formMonth = new ArrayList<>();
        for (Working w : results) {
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

    /*
     * 申請一覧画面表示処理
     */
    public List<WorkingForm> findWorkingDate() {
        List<Working> workings = workingRepository.findAll();
        //List<Working>をList<UserForm>に詰め替えるメソッド呼び出し
        return setWorkingForm(workings);
    }

    /*
     * 個人申請詳細画面表示処理
     */
    public List<AllForm> findWorkDate(int userId) {
        List<Object[]> results = workingRepository.findUserDateById(userId);
        //★URLパターンのエラメ
        if(results.size() == 0){
            return null;
        }
        //List<Object[]>をList<UserForm>に詰め替えるメソッド呼び出し
        return setListCalendarForm(results);
    }

    //変更箇所■勤怠データのstatusが全部0の時(上のfindUserDateByIdは勤怠データのstatusが0以上を集めているベン図)
    public boolean existsByUserId(int userId) {
        if (workingRepository.existsByUserId(userId)){
            return true;
        }
        return false;
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

            //休憩時間の差分計算
            LocalTime breakDuration = convertToLocalTime(formAll.getStartBreak(), formAll.getEndBreak());
            //LocalTime→String型変換
            String breakTime = DateTimeFormatter.ofPattern("[]H:mm").format(breakDuration);
            //formAllにセット
            formAll.setBreakDuration(breakTime);

            //労働時間の差分計算
            LocalTime workDuration = convertToLocalTime(formAll.getStartWork(), formAll.getEndWork());
            //労働時間と休憩時間の差分
            Duration duration = Duration.between(breakDuration, workDuration);
            //LocalTime→String型変換して返す
            String workTime = DateTimeFormatter.ofPattern("[]H:mm").format(LocalTime.MIDNIGHT.plus(duration));
            //formAllにセット
            formAll.setWorkDuration(workTime);

            formAll.setStatus((int) objects[9]);

            formAll.setDate((LocalDate) objects[10]);

            formAll.setFiscalYear((int) objects[11]);
            formAll.setDayOfWeek((String) objects[12]);

            if (objects[13] == null) {
                objects[13] = "";
            }
            formAll.setMemo((String) objects[13]);
            formAlls.add(formAll);
        }
        return formAlls;
    }

    //LocalTimeに型変換して差分をStringで取得するメソッド
    public static LocalTime convertToLocalTime(String start, String end) {
        //LocalTimeに型変換
        LocalTime startTime = LocalTime.parse(start, DateTimeFormatter.ofPattern("[]H:[]m"));
        LocalTime endTime = LocalTime.parse(end, DateTimeFormatter.ofPattern("[]H:[]m"));
        //差分
        Duration duration = Duration.between(startTime, endTime);
        //Duration→LocalTime型変換
        return LocalTime.MIDNIGHT.plus(duration);
    }

    //変更箇所▼勤怠データ全てが-1の人
    public long count(int userId) {
        int status = -1;
        return workingRepository.countByUserIdAndStatus(userId, status);
    }

    //▲ある1人のユーザの申請状況を確認している
    public boolean existCheckByUserIdAndStatus(int userId, int status) {
        return workingRepository.existsByUserIdAndStatus(userId, status);
    }

    // ★勤怠登録していない新人さんを弾く用
    public boolean existCheckByUserId(int userId) {
        return workingRepository.existsByUserId(userId);
    }

    /*
     * 個人申請承認処理
     */
    public void saveStatus(WorkingForm workingForm) {
        workingRepository.saveStatus(workingForm.getId(), workingForm.getStatus());
    }

    //型をEntity→Formに変換するメソッド（鈴木）
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
     * 日毎勤怠情報編集機能
     */
    public void saveForm(WorkingForm workingForm) {
        //引数の型をForm→Entityに変換するメソッド呼び出し
        Working working = setWorkingEntity(workingForm);
        //ユーザー情報を登録/更新
        workingRepository.save(working);
    }

    //型をForm→Entityに変換するメソッド（鈴木）
    private Working setWorkingEntity(WorkingForm workingForm) {
        Working working = new Working();
        working.setId(workingForm.getId());
        working.setUserId(workingForm.getUserId());
        working.setDate(workingForm.getDate());
        working.setAttend(workingForm.getAttend());
        working.setStartWork(workingForm.getStartWork());
        working.setEndWork(workingForm.getEndWork());
        working.setStartBreak(workingForm.getStartBreak());
        //休憩時間を取っていない場合→休憩時間の入力欄が空
        if (StringUtils.isEmpty(working.getStartBreak())) {
            working.setStartBreak("00:00");
        }
        working.setEndBreak(workingForm.getEndBreak());
        //休憩時間を取っていない場合→休憩時間の入力欄が空
        if (StringUtils.isEmpty(working.getEndBreak())) {
            working.setEndBreak("00:00");
        }
        working.setStatus(workingForm.getStatus());
        working.setMemo(workingForm.getMemo());
        return working;
    }

    //申請処理のヌルチェック
    public WorkingForm findDateDate(int id) {
        Working working = workingRepository.findById(id);
        List<Working> workings = new ArrayList<>();
        workings.add(working);
        //List<Working>をList<UserForm>に詰め替えるメソッド呼び出し
        return setWorkingForm(workings).get(0);
    }

    //♥未申請→申請の更新
    public void saveUpdateStatusStatus(int id){
        workingRepository.saveApplyById(id);
    }
}
