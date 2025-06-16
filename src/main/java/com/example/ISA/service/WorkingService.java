package com.example.ISA.service;

import com.example.ISA.controller.form.WorkingForm;
import com.example.ISA.repository.CalendarRepository;
import com.example.ISA.repository.WorkingRepository;
import com.example.ISA.repository.entity.Working;
import com.example.ISA.repository.entity.Calendar;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
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
                // 初期値をセット (memoはここで空文字列に設定されている)
                form.setId(0);
                form.setStatus(-1);
                form.setAttend(0);
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
            form.setStatus(-1);
            form.setAttend(0);
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
                workingEntity.setAttend(0); // nullの場合はデフォルト値を設定
            } else {
                workingEntity.setAttend(dailyForm.getAttend());
            }

            workingEntity.setStartWork(dailyForm.getStartWork());
            workingEntity.setEndWork(dailyForm.getEndWork());
            workingEntity.setStartBreak(dailyForm.getStartBreak());
            workingEntity.setEndBreak(dailyForm.getEndBreak());

            if (dailyForm.getStatus() == null) {
                workingEntity.setStatus(0); // デフォルトを「未入力」に設定
            } else {
                workingEntity.setStatus(dailyForm.getStatus());
            }

            workingEntity.setMemo(dailyForm.getMemo());

            workingRepository.save(workingEntity);
        }
    }
}
