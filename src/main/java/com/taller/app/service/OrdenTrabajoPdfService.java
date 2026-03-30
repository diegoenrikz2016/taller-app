package com.taller.app.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.taller.app.service.dto.OrdenTrabajoDTO;
import java.io.ByteArrayOutputStream;
import org.springframework.stereotype.Service;

@Service
public class OrdenTrabajoPdfService {

    public byte[] generarPdf(OrdenTrabajoDTO orden) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            //CONTENIDO
            document.add(new Paragraph("ORDEN DE TRABAJO").setBold().setFontSize(18));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("ID: " + orden.getId()));
            document.add(new Paragraph("Fecha: " + orden.getFecha()));

            if (orden.getVehiculo() != null) {
                document.add(new Paragraph("Vehículo: " + orden.getVehiculo().getPlaca()));

                if (orden.getVehiculo().getCliente() != null) {
                    document.add(new Paragraph("Cliente: " + orden.getVehiculo().getCliente().getNombre()));
                }
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Observaciones: " + orden.getObservaciones()));

            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }
}
