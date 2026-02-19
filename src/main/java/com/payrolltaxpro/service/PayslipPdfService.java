package com.payrolltaxpro.service;

import com.payrolltaxpro.domain.Payroll;
import com.lowagie.text.Anchor;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@Slf4j
public class PayslipPdfService {

    @Value("${payroll.pdf.company-name:PayrollTax Pro}")
    private String companyName;

    @Value("${payroll.pdf.company-address:}")
    private String companyAddress;

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    /**
     * Generate a PDF payslip for a payroll record.
     *
     * @param payroll The payroll record
     * @return PDF bytes
     */
    public byte[] generatePayslip(Payroll payroll) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            // Add header
            addHeader(document, payroll);

            // Add employee information
            addEmployeeInfo(document, payroll);

            // Add earnings section
            addEarningsSection(document, payroll);

            // Add deductions section
            addDeductionsSection(document, payroll);

            // Add summary section
            addSummarySection(document, payroll);

            // Add footer
            addFooter(document);

            document.close();
        } catch (Exception e) {
            log.error("Error generating payslip PDF for payroll {}", payroll.getId(), e);
            throw new IOException("Failed to generate payslip PDF", e);
        }

        log.info("Generated payslip PDF for payroll {}", payroll.getId());
        return byteArrayOutputStream.toByteArray();
    }

    private void addHeader(Document document, Payroll payroll) throws DocumentException {
        // Company name
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
        Paragraph title = new Paragraph(companyName, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Company address
        if (companyAddress != null && !companyAddress.isEmpty()) {
            Font addressFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Paragraph address = new Paragraph(companyAddress, addressFont);
            address.setAlignment(Element.ALIGN_CENTER);
            document.add(address);
        }

        // Payslip title
        Font payslipFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
        Paragraph payslipTitle = new Paragraph("Payslip / Slip Gaji", payslipFont);
        payslipTitle.setAlignment(Element.ALIGN_CENTER);
        payslipTitle.setSpacingBefore(20);
        document.add(payslipTitle);

        // Period
        Font periodFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.DARK_GRAY);
        String period = getMonthName(payroll.getMonth()) + " " + payroll.getYear();
        Paragraph periodParagraph = new Paragraph("Period: " + period, periodFont);
        periodParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(periodParagraph);

        // Line separator
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new Color(200, 200, 200));
        document.add(lineSeparator);
    }

    private void addEmployeeInfo(Document document, Payroll payroll) throws DocumentException {
        document.add(new Paragraph(" "));

        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.DARK_GRAY);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addTableRow(table, "Employee Name", payroll.getEmployee().getName(), sectionFont, valueFont);
        addTableRow(table, "Employee Code", payroll.getEmployee().getEmployeeCode(), sectionFont, valueFont);
        addTableRow(table, "NPWP", payroll.getEmployee().getNpwp() != null ? payroll.getEmployee().getNpwp() : "-", sectionFont, valueFont);
        addTableRow(table, "Tax Status", payroll.getEmployee().getTaxStatus(), sectionFont, valueFont);

        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addEarningsSection(Document document, Payroll payroll) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
        Paragraph sectionTitle = new Paragraph("Earnings / Pendapatan", sectionFont);
        sectionTitle.setSpacingBefore(10);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addTableAmountRow(table, "Basic Salary", payroll.getBasicSalary());
        addTableAmountRow(table, "Allowances", payroll.getAllowance());
        addTableAmountRow(table, "Overtime", payroll.getOvertime());

        // Gross total
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);
        PdfPCell totalLabel = new PdfPCell(new Phrase("GROSS SALARY", boldFont));
        totalLabel.setBorder(Rectangle.TOP);
        totalLabel.setBackgroundColor(new Color(240, 240, 240));
        table.addCell(totalLabel);

        PdfPCell totalValue = new PdfPCell(new Phrase(formatCurrency(payroll.getGrossSalary()), boldFont));
        totalValue.setBorder(Rectangle.TOP);
        totalValue.setBackgroundColor(new Color(240, 240, 240));
        totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(totalValue);

        document.add(table);
    }

    private void addDeductionsSection(Document document, Payroll payroll) throws DocumentException {
        document.add(new Paragraph(" "));

        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
        Paragraph sectionTitle = new Paragraph("Deductions / Potongan", sectionFont);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addTableAmountRow(table, "Income Tax (PPh 21)", payroll.getTax());
        addTableAmountRow(table, "BPJS", payroll.getBpjs());
        addTableAmountRow(table, "Other Deductions", payroll.getDeduction());

        document.add(table);
    }

    private void addSummarySection(Document document, Payroll payroll) throws DocumentException {
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        // Net salary
        Font netFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(0, 100, 0));
        PdfPCell netLabel = new PdfPCell(new Phrase("NET SALARY / TAKE HOME PAY", netFont));
        netLabel.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
        netLabel.setBackgroundColor(new Color(220, 255, 220));
        netLabel.setPadding(10);
        table.addCell(netLabel);

        PdfPCell netValue = new PdfPCell(new Phrase(formatCurrency(payroll.getNetSalary()), netFont));
        netValue.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
        netValue.setBackgroundColor(new Color(220, 255, 220));
        netValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        netValue.setPadding(10);
        table.addCell(netValue);

        document.add(table);
        document.add(new Paragraph(" "));

        // Additional info
        Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
        Paragraph info = new Paragraph("Taxable Income: " + formatCurrency(payroll.getTaxableIncome()) +
                " (annual)", infoFont);
        document.add(info);
    }

    private void addFooter(Document document) throws DocumentException {
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
        Paragraph footer = new Paragraph(
                "This is a computer-generated document. No signature required.\n" +
                "Generated by PayrollTax Pro on " + LocalDate.now().format(DATE_FORMATTER),
                footerFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }

    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    private void addTableAmountRow(PdfPTable table, String label, java.math.BigDecimal amount) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.DARK_GRAY);
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(formatCurrency(amount), font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    private String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null) {
            return CURRENCY_FORMAT.format(0);
        }
        return CURRENCY_FORMAT.format(amount);
    }

    private String getMonthName(int month) {
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        return months[month - 1];
    }
}
