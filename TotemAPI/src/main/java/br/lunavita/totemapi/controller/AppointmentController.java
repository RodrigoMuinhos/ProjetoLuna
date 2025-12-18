package br.lunavita.totemapi.controller;

import br.lunavita.totemapi.dto.AppointmentNotificationRequest;
import br.lunavita.totemapi.model.Appointment;
import br.lunavita.totemapi.model.AppointmentRequest;
import br.lunavita.totemapi.model.AppointmentStatusUpdate;
import br.lunavita.totemapi.service.DataStoreService;
import br.lunavita.totemapi.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    private final DataStoreService store;
    private final ReportService reportService;

    public AppointmentController(DataStoreService store, ReportService reportService) {
        this.store = store;
        this.reportService = reportService;
    }

    @GetMapping
    public List<Appointment> list() {
        return store.listAppointments();
    }

    @GetMapping("/upcoming")
    public List<Appointment> upcoming() {
        return store.listUpcomingAppointments();
    }

    @GetMapping("/search")
    public List<Appointment> search(@RequestParam("q") String q) {
        return store.searchUpcomingAppointments(q);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> get(@PathVariable String id) {
        return store.findAppointment(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Appointment> create(@RequestBody AppointmentRequest request) {
        Appointment appointment = store.createAppointment(request);
        return ResponseEntity.status(201).body(appointment);
    }

    @PostMapping("/{id}/notify")
    public ResponseEntity<Void> notify(@PathVariable String id, @RequestBody AppointmentNotificationRequest request) {
        logger.info("[CONTROLLER] POST /api/appointments/{}/notify - patientEmail={}, doctorEmail={}",
                id, request.patientEmail(), request.doctorEmail());
        try {
            boolean notified = store.sendAppointmentNotifications(id, request.patientEmail(), request.doctorEmail());
            if (!notified) {
                logger.warn("[CONTROLLER] Consulta não encontrada: {}", id);
                return ResponseEntity.notFound().build();
            }
            logger.info("[CONTROLLER] Notificação enviada com sucesso para consulta: {}", id);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            logger.error("[CONTROLLER] Erro ao enviar notificação: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Appointment> updateStatus(@PathVariable String id,
            @RequestBody AppointmentStatusUpdate update) {
        return store.updateStatus(id, update.getStatus())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> update(@PathVariable String id, @RequestBody AppointmentRequest request) {
        return store.updateAppointment(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (store.deleteAppointment(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Appointment> uploadPhoto(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        return store.uploadAppointmentPhoto(id, file)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/{id}/report", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getReport(@PathVariable String id) {
        return store.findAppointment(id)
                .map(apt -> ResponseEntity.ok()
                        .header("Content-Disposition", "inline; filename=\"relatorio-" + id + ".pdf\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(reportService.generateAppointmentReport(apt)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
