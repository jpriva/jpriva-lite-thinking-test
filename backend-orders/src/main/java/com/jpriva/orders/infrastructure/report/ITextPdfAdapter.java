package com.jpriva.orders.infrastructure.report;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.ReportErrorCodes;
import com.jpriva.orders.domain.model.Product;
import com.jpriva.orders.domain.ports.report.ReportGeneratorPort;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class ITextPdfAdapter implements ReportGeneratorPort {
    @Override
    public byte[] generateProductReport(List<Product> products) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            document.add(new Paragraph("Product Inventory Report")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setMarginBottom(20));

            float[] columnWidths = {2, 4, 1, 5};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.useAllAvailableWidth();

            String[] headers = {"SKU", "Name", "Stock", "Description"};
            for (String header : headers) {
                table.addHeaderCell(new Cell()
                        .add(new Paragraph(header).setFont(boldFont))
                        .setBackgroundColor(new DeviceRgb(240, 240, 240)));
            }

            for (Product p : products) {
                table.addCell(new Paragraph(p.getSku() != null ? p.getSku() : "N/A"));
                table.addCell(new Paragraph(p.getName()));

                String stock = (p.getInventory() != null)
                        ? String.valueOf(p.getInventory().getQuantity())
                        : "0";
                table.addCell(new Paragraph(stock));

                table.addCell(new Paragraph(p.getDescription() != null ? p.getDescription() : ""));
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new DomainException(ReportErrorCodes.REPORT_GENERATE_ERROR, e);
        }
    }
}
