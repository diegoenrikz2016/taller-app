package com.taller.app.service;

import com.taller.app.domain.Cliente;
import com.taller.app.domain.DetalleOrden;
import com.taller.app.domain.OrdenTrabajo;
import com.taller.app.domain.OrdenTrabajoPdf;
import com.taller.app.domain.Vehiculo;
import com.taller.app.repository.OrdenTrabajoPdfRepository;
import com.taller.app.service.dto.OrdenTrabajoPdfRequestDTO;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrdenTrabajoPdfService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final List<String> PARTES = List.of(
        "Guardachoque D.",
        "Guardachoque T.",
        "Capó",
        "Techo",
        "Guardafango DR",
        "Guardafango DL",
        "Guardafango FR",
        "Guardafango FL",
        "Compuerta",
        "Estribo F",
        "Estribo L",
        "Puerta DR",
        "Puerta DL",
        "Puerta TR",
        "Puerta TL",
        "Espejo R",
        "Espejo L",
        "Piso",
        "Pintura Integral"
    );

    private static final List<String> INVENTARIO_ITEMS = List.of(
        "Gato",
        "Herramientas",
        "Triángulos",
        "Tapetes",
        "Llanta refacción",
        "Extintor",
        "Antena",
        "Emblemas",
        "Tapones de rueda",
        "Cables",
        "Estéreo",
        "Encendedor"
    );

    private final PdfService pdfService;
    private final OrdenTrabajoPdfRepository pdfRepository;

    public OrdenTrabajoPdfService(PdfService pdfService, OrdenTrabajoPdfRepository pdfRepository) {
        this.pdfService = pdfService;
        this.pdfRepository = pdfRepository;
    }

    public byte[] generarPdf(OrdenTrabajo orden, OrdenTrabajoPdfRequestDTO req) {
        try {
            String html = new String(
                Files.readAllBytes(Paths.get("src/main/resources/templates/orden-trabajo.html")),
                StandardCharsets.UTF_8
            );

            Vehiculo vehiculo = orden.getVehiculo();
            Cliente cliente = vehiculo != null ? vehiculo.getCliente() : null;

            String nombreCliente = safe(cliente != null ? cliente.getNombre() : null);
            String cedula = safe(cliente != null ? cliente.getCedula() : null);
            String telefono = safe(cliente != null ? cliente.getTelefono() : null);
            String direccion = safe(cliente != null ? cliente.getDireccion() : null);
            String email = safe(cliente != null ? cliente.getEmail() : null);

            String placa = safe(vehiculo != null ? vehiculo.getPlaca() : null);
            String marca = safe(vehiculo != null ? vehiculo.getMarca() : null);
            String modelo = safe(vehiculo != null ? vehiculo.getModelo() : null);
            String color = safe(vehiculo != null ? vehiculo.getColor() : null);

            String mecanico = safe(orden.getMecanico());
            String fecha = orden.getFecha() != null ? orden.getFecha().format(DATE_FMT) : "";
            String estadoRaw = orden.getEstado() != null ? orden.getEstado().name() : "";
            String estadoLabel = estadoRaw.replace("_", " ");
            String observaciones = safe(orden.getObservaciones());

            // ── Detalle de servicios + cálculo automático ──
            StringBuilder detalleHtml = new StringBuilder();
            BigDecimal subtotalCalc = BigDecimal.ZERO;
            for (DetalleOrden d : orden.getDetalleses()) {
                String desc = safe(d.getDescripcion());
                int cant = d.getCantidad() != null ? d.getCantidad() : 1;
                BigDecimal prec = d.getPrecio() != null ? d.getPrecio() : BigDecimal.ZERO;
                BigDecimal linea = prec.multiply(BigDecimal.valueOf(cant));
                subtotalCalc = subtotalCalc.add(linea);
                detalleHtml
                    .append("<tr>")
                    .append("<td>")
                    .append(desc)
                    .append("</td>")
                    .append("<td class=\"right\">")
                    .append(cant)
                    .append("</td>")
                    .append("<td class=\"right\">$ ")
                    .append(String.format("%.2f", linea))
                    .append("</td>")
                    .append("</tr>");
            }

            BigDecimal manoObraVal = BigDecimal.ZERO; // campo eliminado
            BigDecimal totalCalc = subtotalCalc;

            String manoObra = "";
            String subtotal = "$ " + String.format("%.2f", subtotalCalc);
            String total = "$ " + String.format("%.2f", totalCalc);

            List<String> enderezado = req.getTrabajoEnderezado() != null ? req.getTrabajoEnderezado() : List.of();
            List<String> pintura = req.getTrabajoPintura() != null ? req.getTrabajoPintura() : List.of();
            List<String> inventario = req.getInventario() != null ? req.getInventario() : List.of();

            String trabajoHtml = buildTrabajosHtml(enderezado, pintura);
            String inventarioHtmlCol1 = buildInventarioColHtml(inventario, 0);
            String inventarioHtmlCol2 = buildInventarioColHtml(inventario, 1);
            String combustibleHtml = buildCombustibleHtml(req.getNivelCombustible());

            // ── Valores de pago: prioridad BD, fallback formulario ──
            BigDecimal vpBD = orden.getValorPactado();
            BigDecimal abBD = orden.getAbono();

            String vPactado =
                req.getValorPactado() != null && !req.getValorPactado().isBlank()
                    ? req.getValorPactado()
                    : (vpBD != null ? String.format("%.2f", vpBD) : "");
            String abono =
                req.getAbono() != null && !req.getAbono().isBlank() ? req.getAbono() : (abBD != null ? String.format("%.2f", abBD) : "");

            // Calcular saldo
            double vp = 0,
                ab = 0;
            try {
                vp = Double.parseDouble(vPactado);
            } catch (Exception ignored) {}
            try {
                ab = Double.parseDouble(abono);
            } catch (Exception ignored) {}
            String saldo = String.format("%.2f", vp - ab);

            String extras =
                req.getTrabajosExtras() != null && !req.getTrabajosExtras().isBlank()
                    ? safe(req.getTrabajosExtras()).replace("\n", "<br/>")
                    : safe(orden.getTrabajosExtras());

            String km = safe(req.getKilometraje());
            String grua = Boolean.TRUE.equals(req.getIngresoGrua()) ? "&#10003;" : "";
            String noGrua = Boolean.TRUE.equals(req.getIngresoGrua()) ? "" : "&#10003;";

            String fechaGeneracion = LocalDate.now().format(DATE_FMT);
            String ordenPadded = String.format("%07d", orden.getId());

            html = html
                .replace("{{orden}}", String.valueOf(orden.getId()))
                .replace("{{ordenPadded}}", ordenPadded)
                .replace("{{cliente}}", nombreCliente)
                .replace("{{cedula}}", cedula)
                .replace("{{email}}", email)
                .replace("{{telefono}}", telefono)
                .replace("{{telefonoCliente}}", telefono)
                .replace("{{direccion}}", direccion)
                .replace("{{vehiculo}}", placa)
                .replace("{{marca}}", marca)
                .replace("{{modelo}}", modelo)
                .replace("{{color}}", color)
                .replace("{{mecanico}}", mecanico)
                .replace("{{fecha}}", fecha)
                .replace("{{estadoRaw}}", estadoRaw)
                .replace("{{estado}}", estadoLabel)
                .replace("{{observaciones}}", observaciones)
                .replace("{{detalle}}", detalleHtml.toString())
                .replace("{{manoObra}}", manoObra)
                .replace("{{subtotal}}", subtotal)
                .replace("{{total}}", total)
                .replace("{{trabajosHtml}}", trabajoHtml)
                .replace("{{inventarioHtml_col1}}", inventarioHtmlCol1)
                .replace("{{inventarioHtml_col2}}", inventarioHtmlCol2)
                .replace("{{combustibleHtml}}", combustibleHtml)
                .replace("{{kilometraje}}", km)
                .replace("{{grua}}", grua)
                .replace("{{noGrua}}", noGrua)
                .replace("{{valorPactado}}", vPactado)
                .replace("{{abono}}", abono)
                .replace("{{saldo}}", saldo)
                .replace("{{trabajosExtras}}", extras)
                .replace("{{fechaGeneracion}}", fechaGeneracion);

            byte[] pdfBytes = pdfService.generarPdf(html);

            // Guardar en BD
            String nombreArchivo = "orden-" + ordenPadded + "-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
            OrdenTrabajoPdf registro = new OrdenTrabajoPdf();
            registro.setOrdenTrabajo(orden);
            registro.setNombreArchivo(nombreArchivo);
            registro.setFechaGeneracion(Instant.now());
            registro.setContenido(pdfBytes);
            pdfRepository.save(registro);

            return pdfBytes;
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de orden", e);
        }
    }

    private String buildTrabajosHtml(List<String> enderezado, List<String> pintura) {
        int colSize = (int) Math.ceil(PARTES.size() / 3.0);
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"col-wrap\">");
        for (int c = 0; c < 3; c++) {
            sb.append("<div class=\"col-cell\"><table class=\"check-table\">");
            sb.append("<tr><th class=\"parte-col\">Parte</th><th class=\"chk\">E</th><th class=\"chk\">P</th></tr>");
            for (int i = c * colSize; i < Math.min((c + 1) * colSize, PARTES.size()); i++) {
                String parte = PARTES.get(i);
                String chkE = enderezado.contains(parte) ? "&#10003;" : "";
                String chkP = pintura.contains(parte) ? "&#10003;" : "";
                sb
                    .append("<tr>")
                    .append("<td>")
                    .append(parte)
                    .append("</td>")
                    .append("<td class=\"chk\"><div class=\"chk-box\">")
                    .append(chkE)
                    .append("</div></td>")
                    .append("<td class=\"chk\"><div class=\"chk-box\">")
                    .append(chkP)
                    .append("</div></td>")
                    .append("</tr>");
            }
            sb.append("</table></div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private String buildInventarioColHtml(List<String> marcados, int col) {
        int size = (int) Math.ceil(INVENTARIO_ITEMS.size() / 2.0);
        int start = col * size;
        int end = Math.min(start + size, INVENTARIO_ITEMS.size());
        StringBuilder sb = new StringBuilder();
        sb.append("<table class=\"check-table\">");
        for (int i = start; i < end; i++) {
            String item = INVENTARIO_ITEMS.get(i);
            String chk = marcados.contains(item) ? "&#10003;" : "";
            sb
                .append("<tr>")
                .append("<td>")
                .append(item)
                .append("</td>")
                .append("<td class=\"chk\"><div class=\"chk-box\">")
                .append(chk)
                .append("</div></td>")
                .append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    private String buildCombustibleHtml(Integer nivel) {
        int n = nivel != null ? Math.max(0, Math.min(8, nivel)) : 0;
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"fuel-gauge\">");
        sb.append("<span class=\"fuel-e\">E</span>");
        for (int i = 0; i < 8; i++) {
            String cls = i < n ? "fuel-seg filled" : "fuel-seg";
            sb.append("<div class=\"").append(cls).append("\"></div>");
        }
        sb.append("<span class=\"fuel-f\">F</span>");
        sb.append("</div>");
        return sb.toString();
    }

    private String safe(String value) {
        return value != null ? value : "";
    }
}
