package com.payrolltaxpro.service;

import com.opencsv.CSVWriter;
import com.payrolltaxpro.domain.Payroll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class CsvExportService {

    @Value("${payroll.csv.export-path:/tmp/payroll-exports/}")
    private String exportPath;

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    /**
     * Export payroll data to CSV for bank processing.
     *
     * @param payrolls List of payroll records to export
     * @param filename Name of the CSV file
     * @return Path to the generated CSV file
     */
    public Path exportBankTransferCsv(List<Payroll> payrolls, String filename) throws IOException {
        // Create export directory if it doesn't exist
        Path path = Paths.get(exportPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        Path csvPath = path.resolve(filename);

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvPath.toFile()))) {
            // Write header
            String[] header = {
                    "Sequence",
                    "Bank Code",
                    "Account Number",
                    "Account Name",
                    "Amount",
                    "Employee Code",
                    "Employee Name",
                    "Payment Reference",
                    "Email"
            };
            writer.writeNext(header);

            // Write data rows
            int sequence = 1;
            for (Payroll payroll : payrolls) {
                String[] row = {
                        String.valueOf(sequence++),
                        getBankCode(payroll.getEmployee().getBankName()),
                        payroll.getEmployee().getBankAccount(),
                        payroll.getEmployee().getName(),
                        formatAmount(payroll.getNetSalary()),
                        payroll.getEmployee().getEmployeeCode(),
                        payroll.getEmployee().getName(),
                        generatePaymentReference(payroll),
                        "" // Email - can be added if available
                };
                writer.writeNext(row);
            }

            log.info("Exported {} payroll records to CSV file: {}", payrolls.size(), csvPath);
        } catch (Exception e) {
            log.error("Error exporting payroll to CSV", e);
            throw new IOException("Failed to export payroll to CSV", e);
        }

        return csvPath;
    }

    /**
     * Export payroll data to CSV with full details.
     *
     * @param payrolls List of payroll records to export
     * @param filename Name of the CSV file
     * @return Path to the generated CSV file
     */
    public Path exportPayrollDetailsCsv(List<Payroll> payrolls, String filename) throws IOException {
        // Create export directory if it doesn't exist
        Path path = Paths.get(exportPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        Path csvPath = path.resolve(filename);

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvPath.toFile()))) {
            // Write header
            String[] header = {
                    "Employee Code",
                    "Employee Name",
                    "NPWP",
                    "Bank Name",
                    "Bank Account",
                    "Month",
                    "Year",
                    "Basic Salary",
                    "Allowance",
                    "Overtime",
                    "Gross Salary",
                    "Tax",
                    "BPJS",
                    "Deduction",
                    "Net Salary",
                    "Overtime Hours",
                    "Taxable Income",
                    "Status",
                    "Payment Date"
            };
            writer.writeNext(header);

            // Write data rows
            for (Payroll payroll : payrolls) {
                String[] row = {
                        payroll.getEmployee().getEmployeeCode(),
                        payroll.getEmployee().getName(),
                        payroll.getEmployee().getNpwp() != null ? payroll.getEmployee().getNpwp() : "",
                        payroll.getEmployee().getBankName() != null ? payroll.getEmployee().getBankName() : "",
                        payroll.getEmployee().getBankAccount() != null ? payroll.getEmployee().getBankAccount() : "",
                        String.valueOf(payroll.getMonth()),
                        String.valueOf(payroll.getYear()),
                        formatAmount(payroll.getBasicSalary()),
                        formatAmount(payroll.getAllowance()),
                        formatAmount(payroll.getOvertime()),
                        formatAmount(payroll.getGrossSalary()),
                        formatAmount(payroll.getTax()),
                        formatAmount(payroll.getBpjs()),
                        formatAmount(payroll.getDeduction()),
                        formatAmount(payroll.getNetSalary()),
                        formatAmount(payroll.getOvertimeHours()),
                        formatAmount(payroll.getTaxableIncome()),
                        payroll.getStatus().toString(),
                        payroll.getPaymentDate() != null ? payroll.getPaymentDate().toString() : ""
                };
                writer.writeNext(row);
            }

            log.info("Exported {} payroll detail records to CSV file: {}", payrolls.size(), csvPath);
        } catch (Exception e) {
            log.error("Error exporting payroll details to CSV", e);
            throw new IOException("Failed to export payroll details to CSV", e);
        }

        return csvPath;
    }

    /**
     * Generate CSV content as string for direct download.
     */
    public String generateBankTransferCsvContent(List<Payroll> payrolls) {
        StringBuilder csv = new StringBuilder();

        // Write header
        csv.append("Sequence,Bank Code,Account Number,Account Name,Amount,Employee Code,Employee Name,Payment Reference\n");

        // Write data rows
        int sequence = 1;
        for (Payroll payroll : payrolls) {
            csv.append(sequence++).append(",");
            csv.append(getBankCode(payroll.getEmployee().getBankName())).append(",");
            csv.append(escapeCsv(payroll.getEmployee().getBankAccount())).append(",");
            csv.append(escapeCsv(payroll.getEmployee().getName())).append(",");
            csv.append(formatAmount(payroll.getNetSalary())).append(",");
            csv.append(payroll.getEmployee().getEmployeeCode()).append(",");
            csv.append(escapeCsv(payroll.getEmployee().getName())).append(",");
            csv.append(generatePaymentReference(payroll)).append("\n");
        }

        return csv.toString();
    }

    private String getBankCode(String bankName) {
        if (bankName == null) {
            return "UNKNOWN";
        }
        // Simplified bank code mapping - in production, this should be from a database
        return bankName.toUpperCase().replaceAll("\\s+", "_");
    }

    private String formatAmount(java.math.BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        // Format without currency symbol for CSV
        return amount.toString();
    }

    private String generatePaymentReference(Payroll payroll) {
        return String.format("PAYROLL-%s-%02d-%d-%d",
                payroll.getEmployee().getEmployeeCode(),
                payroll.getMonth(),
                payroll.getYear(),
                payroll.getId());
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // Escape values containing commas or quotes
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
