package com.payrolltaxpro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OvertimeCalculationService {

    @Value("${payroll.overtime.weekday-multiplier:1.5}")
    private BigDecimal weekdayMultiplier;

    @Value("${payroll.overtime.weekend-multiplier:2.0}")
    private BigDecimal weekendMultiplier;

    @Value("${payroll.overtime.holiday-multiplier:2.0}")
    private BigDecimal holidayMultiplier;

    // Indonesian national holidays (simplified - in production, this should be loaded from a database or API)
    private final Set<LocalDate> holidays = new HashSet<>();

    /**
     * Calculate overtime pay for regular hours on weekdays.
     *
     * @param hourlyRate   Hourly rate of the employee
     * @param overtimeHours Number of overtime hours
     * @return Overtime pay amount
     */
    public BigDecimal calculateWeekdayOvertime(BigDecimal hourlyRate, BigDecimal overtimeHours) {
        if (hourlyRate == null || overtimeHours == null ||
            hourlyRate.compareTo(BigDecimal.ZERO) <= 0 ||
            overtimeHours.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal overtimePay = hourlyRate
                .multiply(overtimeHours)
                .multiply(weekdayMultiplier);

        log.debug("Weekday overtime - Hourly rate: {}, Hours: {}, Multiplier: {}, Pay: {}",
                hourlyRate, overtimeHours, weekdayMultiplier, overtimePay);

        return overtimePay.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate overtime pay for weekend work.
     *
     * @param hourlyRate   Hourly rate of the employee
     * @param overtimeHours Number of overtime hours
     * @return Overtime pay amount
     */
    public BigDecimal calculateWeekendOvertime(BigDecimal hourlyRate, BigDecimal overtimeHours) {
        if (hourlyRate == null || overtimeHours == null ||
            hourlyRate.compareTo(BigDecimal.ZERO) <= 0 ||
            overtimeHours.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal overtimePay = hourlyRate
                .multiply(overtimeHours)
                .multiply(weekendMultiplier);

        log.debug("Weekend overtime - Hourly rate: {}, Hours: {}, Multiplier: {}, Pay: {}",
                hourlyRate, overtimeHours, weekendMultiplier, overtimePay);

        return overtimePay.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate overtime pay for holiday work.
     *
     * @param hourlyRate   Hourly rate of the employee
     * @param overtimeHours Number of overtime hours
     * @return Overtime pay amount
     */
    public BigDecimal calculateHolidayOvertime(BigDecimal hourlyRate, BigDecimal overtimeHours) {
        if (hourlyRate == null || overtimeHours == null ||
            hourlyRate.compareTo(BigDecimal.ZERO) <= 0 ||
            overtimeHours.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal overtimePay = hourlyRate
                .multiply(overtimeHours)
                .multiply(holidayMultiplier);

        log.debug("Holiday overtime - Hourly rate: {}, Hours: {}, Multiplier: {}, Pay: {}",
                hourlyRate, overtimeHours, holidayMultiplier, overtimePay);

        return overtimePay.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate overtime pay based on the date.
     * Automatically determines if the date is a weekday, weekend, or holiday.
     *
     * @param hourlyRate   Hourly rate of the employee
     * @param overtimeHours Number of overtime hours
     * @param workDate     Date of overtime work
     * @return Overtime pay amount
     */
    public BigDecimal calculateOvertime(BigDecimal hourlyRate, BigDecimal overtimeHours, LocalDate workDate) {
        if (isHoliday(workDate)) {
            return calculateHolidayOvertime(hourlyRate, overtimeHours);
        } else if (isWeekend(workDate)) {
            return calculateWeekendOvertime(hourlyRate, overtimeHours);
        } else {
            return calculateWeekdayOvertime(hourlyRate, overtimeHours);
        }
    }

    /**
     * Calculate overtime pay with default weekday multiplier.
     *
     * @param hourlyRate   Hourly rate of the employee
     * @param overtimeHours Number of overtime hours
     * @return Overtime pay amount
     */
    public BigDecimal calculateOvertime(BigDecimal hourlyRate, BigDecimal overtimeHours) {
        return calculateWeekdayOvertime(hourlyRate, overtimeHours);
    }

    /**
     * Check if a date is a weekend (Saturday or Sunday in Indonesia).
     */
    public boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * Check if a date is a holiday.
     */
    public boolean isHoliday(LocalDate date) {
        return holidays.contains(date);
    }

    /**
     * Add a holiday date to the holidays set.
     */
    public void addHoliday(LocalDate date) {
        holidays.add(date);
    }

    /**
     * Get the weekday multiplier.
     */
    public BigDecimal getWeekdayMultiplier() {
        return weekdayMultiplier;
    }

    /**
     * Get the weekend multiplier.
     */
    public BigDecimal getWeekendMultiplier() {
        return weekendMultiplier;
    }

    /**
     * Get the holiday multiplier.
     */
    public BigDecimal getHolidayMultiplier() {
        return holidayMultiplier;
    }
}
