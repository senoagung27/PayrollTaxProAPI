package com.payrolltaxpro.service;

import com.payrolltaxpro.domain.TaxBracket;
import com.payrolltaxpro.repository.TaxBracketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxCalculationService {

    private final TaxBracketRepository taxBracketRepository;

    private static final BigDecimal ANNUAL_PTKP = BigDecimal.valueOf(54000000); // PTKP (Non-Taxable Income) for 2024
    private static final int MONTHS_PER_YEAR = 12;

    /**
     * Calculate progressive tax based on annual taxable income.
     * This implements the Indonesian progressive tax bracket system.
     *
     * @param annualTaxableIncome Annual taxable income
     * @return Monthly tax amount
     */
    public BigDecimal calculateTax(BigDecimal annualTaxableIncome) {
        if (annualTaxableIncome == null || annualTaxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        List<TaxBracket> brackets = getActiveTaxBrackets();
        BigDecimal totalTax = BigDecimal.ZERO;

        for (TaxBracket bracket : brackets) {
            if (annualTaxableIncome.compareTo(bracket.getMinIncome()) <= 0) {
                break;
            }

            BigDecimal taxableInThisBracket;
            if (bracket.getMaxIncome() == null) {
                // Highest bracket - no upper limit
                taxableInThisBracket = annualTaxableIncome.subtract(bracket.getMinIncome());
            } else if (annualTaxableIncome.compareTo(bracket.getMaxIncome()) <= 0) {
                // Income falls within this bracket
                taxableInThisBracket = annualTaxableIncome.subtract(bracket.getMinIncome());
            } else {
                // Income exceeds this bracket
                taxableInThisBracket = bracket.getMaxIncome().subtract(bracket.getMinIncome());
            }

            BigDecimal taxForBracket = taxableInThisBracket.multiply(bracket.getPercentage());
            totalTax = totalTax.add(taxForBracket);

            log.debug("Bracket {}: min={}, max={}, rate={}, taxable={}, tax={}",
                    bracket.getBracketOrder(),
                    bracket.getMinIncome(),
                    bracket.getMaxIncome(),
                    bracket.getPercentage(),
                    taxableInThisBracket,
                    taxForBracket);
        }

        // Convert annual tax to monthly tax
        BigDecimal monthlyTax = totalTax.divide(BigDecimal.valueOf(MONTHS_PER_YEAR), 2, RoundingMode.HALF_UP);

        log.info("Annual taxable income: {}, Annual tax: {}, Monthly tax: {}",
                annualTaxableIncome, totalTax, monthlyTax);

        return monthlyTax;
    }

    /**
     * Calculate taxable income from gross salary.
     * Applies PTKP (Non-Taxable Income) and other deductions.
     *
     * @param grossMonthlySalary Monthly gross salary
     * @return Annual taxable income
     */
    public BigDecimal calculateTaxableIncome(BigDecimal grossMonthlySalary) {
        if (grossMonthlySalary == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal annualGrossSalary = grossMonthlySalary.multiply(BigDecimal.valueOf(MONTHS_PER_YEAR));
        BigDecimal annualTaxableIncome = annualGrossSalary.subtract(ANNUAL_PTKP);

        return annualTaxableIncome.compareTo(BigDecimal.ZERO) > 0
                ? annualTaxableIncome
                : BigDecimal.ZERO;
    }

    /**
     * Calculate monthly tax directly from gross salary.
     *
     * @param grossMonthlySalary Monthly gross salary
     * @return Monthly tax amount
     */
    public BigDecimal calculateMonthlyTax(BigDecimal grossMonthlySalary) {
        BigDecimal annualTaxableIncome = calculateTaxableIncome(grossMonthlySalary);
        return calculateTax(annualTaxableIncome);
    }

    @Cacheable(value = "taxBrackets", key = "'active'")
    public List<TaxBracket> getActiveTaxBrackets() {
        return taxBracketRepository.findActiveTaxBracketsOrdered();
    }

    /**
     * Calculate PTKP based on tax status (TK0, TK1, TK2, TK3, K0, K1, K2, K3)
     * For now using standard PTKP. This can be enhanced based on tax status.
     */
    public BigDecimal calculatePTKP(String taxStatus) {
        return ANNUAL_PTKP; // Can be enhanced based on tax status
    }
}
