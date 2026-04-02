package com.taller.app.service;

import com.taller.app.domain.DetalleOrden;
import com.taller.app.domain.OrdenTrabajo;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;

@Service
public class OrdenTrabajoPdfService {

    private final PdfService pdfService;

    public OrdenTrabajoPdfService(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    public byte[] generarPdf(OrdenTrabajo orden) {
        try {
            // 🔹 1. Leer HTML
            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/templates/orden-trabajo.html")));

            // 🔹 2. Construir tabla dinámica
            String detalleHtml = "";
            double total = 0;

            for (DetalleOrden d : orden.getDetalleses()) {
                detalleHtml += "<tr>" + "<td>" + d.getDescripcion() + "</td>" + "<td>$ " + d.getPrecio() + "</td>" + "</tr>";

                total += d.getPrecio().doubleValue();
            }

            // 🔹 3. Reemplazar variables
            html = html
                .replace("{{orden}}", String.valueOf(orden.getId()))
                .replace("{{fecha}}", orden.getFecha().toString())
                .replace("{{cliente}}", orden.getVehiculo().getCliente().getNombre())
                .replace("{{vehiculo}}", orden.getVehiculo().getPlaca())
                .replace("{{detalle}}", detalleHtml)
                .replace("{{total}}", "$ " + total);

            // 🔹 4. Convertir a PDF
            return pdfService.generarPdf(html);
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de orden", e);
        }
    }
}
