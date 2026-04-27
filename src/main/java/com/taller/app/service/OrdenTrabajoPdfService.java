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
            String html = new String(Files.readAllBytes(Paths.get("src/main/resources/templates/orden-trabajo.html")));

            String detalleHtml = "";
            double total = 0;

            for (DetalleOrden d : orden.getDetalleses()) {
                detalleHtml += "<tr>" + "<td>" + d.getDescripcion() + "</td>" + "<td>$ " + d.getPrecio() + "</td>" + "</tr>";

                total += d.getPrecio().doubleValue();
            }

            // 🔥 NUEVO: datos adicionales
            String telefono = orden.getVehiculo().getCliente().getTelefono() != null ? orden.getVehiculo().getCliente().getTelefono() : "";

            String email = orden.getVehiculo().getCliente().getCedula() != null ? orden.getVehiculo().getCliente().getCedula() : "";

            // 🔥 REEMPLAZOS COMPLETOS
            html = html
                .replace("{{orden}}", String.valueOf(orden.getId()))
                .replace("{{fecha}}", orden.getFecha().toString())
                .replace("{{cliente}}", orden.getVehiculo().getCliente().getNombre())
                .replace("{{vehiculo}}", orden.getVehiculo().getPlaca())
                .replace("{{telefono}}", telefono)
                .replace("{{email}}", email)
                .replace("{{detalle}}", detalleHtml)
                .replace("{{total}}", "$ " + total);

            return pdfService.generarPdf(html);
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de orden", e);
        }
    }
}
