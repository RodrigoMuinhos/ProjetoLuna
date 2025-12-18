package br.lunavita.totemapi.service;

import br.lunavita.totemapi.model.Appointment;
import br.lunavita.totemapi.model.AppointmentRequest;
import br.lunavita.totemapi.model.Doctor;
import br.lunavita.totemapi.model.DashboardSummary;
import br.lunavita.totemapi.repository.AppointmentRepository;
import br.lunavita.totemapi.repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class DataStoreService {

    private static final Logger logger = LoggerFactory.getLogger(DataStoreService.class);

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final EmailService emailService;
    private final ResendEmailService resendEmailService;
    private final FileStorageService fileStorageService;

    public DataStoreService(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository,
            EmailService emailService, ResendEmailService resendEmailService, FileStorageService fileStorageService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.emailService = emailService;
        this.resendEmailService = resendEmailService;
        this.fileStorageService = fileStorageService;
    }

    public List<Appointment> listAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> listUpcomingAppointments() {
        LocalDate today = LocalDate.now();
        return appointmentRepository.findByDateGreaterThanEqualOrderByDateAscTimeAscPatientAsc(today);
    }

    public List<Appointment> searchUpcomingAppointments(String query) {
        LocalDate today = LocalDate.now();
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }
        return appointmentRepository
                .findByDateGreaterThanEqualAndPatientIgnoreCaseContainingOrderByDateAscTimeAscPatientAsc(today,
                        query.trim());
    }

    public Optional<Appointment> findAppointment(String id) {
        return appointmentRepository.findById(id);
    }

    public Appointment createAppointment(AppointmentRequest request) {
        Appointment apt = new Appointment();
        apt.setId(UUID.randomUUID().toString());
        apt.setPatient(request.getPatient());
        apt.setPatientId(request.getPatientId());
        apt.setDoctor(request.getDoctor());
        apt.setSpecialty(request.getSpecialty());
        apt.setDate(request.getDate());
        apt.setTime(request.getTime());
        apt.setStatus("aguardando");
        apt.setPaid(false);
        apt.setAmount(request.getAmount());
        apt.setCpf(request.getCpf());
        apt.setType(request.getType());
        apt.setPatientEmail(request.getPatientEmail());        apt.setTenantId(request.getTenantId());        return appointmentRepository.save(apt);
    }

    @Transactional
    public Optional<Appointment> updateStatus(String id, String status) {
        return appointmentRepository.findById(id)
                .map(apt -> {
                    apt.setStatus(status);
                    if ("confirmado".equalsIgnoreCase(status)) {
                        apt.setPaid(true);
                    }
                    return appointmentRepository.save(apt);
                });
    }

    public Optional<Appointment> updateAppointment(String id, AppointmentRequest request) {
        return appointmentRepository.findById(id).map(apt -> {
            apt.setPatient(request.getPatient());
            apt.setPatientId(request.getPatientId());
            apt.setDoctor(request.getDoctor());
            apt.setSpecialty(request.getSpecialty());
            apt.setDate(request.getDate());
            apt.setTime(request.getTime());
            apt.setType(request.getType());
            apt.setAmount(request.getAmount());
            apt.setCpf(request.getCpf());
            apt.setPatientEmail(request.getPatientEmail());
            return appointmentRepository.save(apt);
        });
    }

    public boolean sendAppointmentNotifications(String id, String patientEmail, String doctorEmail) {
        logger.info("[NOTIFY] Iniciando envio de notificação para consulta id={}, doctorEmail={}", id, doctorEmail);
        return appointmentRepository.findById(id).map(appointment -> {
            logger.info("[NOTIFY] Consulta encontrada: {}", appointment.getId());

            String resolvedDoctorEmail = (doctorEmail != null && !doctorEmail.isBlank())
                    ? doctorEmail
                    : resolveDoctorEmail(appointment.getDoctor());
            if (resolvedDoctorEmail != null && !resolvedDoctorEmail.isBlank()) {
                try {
                    logger.info("[NOTIFY] Enviando email para médico: {}", resolvedDoctorEmail);
                    // Usar Resend se configurado, senão tentar SMTP
                    if (resendEmailService.isConfigured()) {
                        logger.info("[NOTIFY] Usando Resend API para enviar email");
                        resendEmailService.sendAppointmentNotificationToDoctor(resolvedDoctorEmail, appointment);
                    } else {
                        logger.info("[NOTIFY] Usando SMTP para enviar email");
                        emailService.sendAppointmentHtmlToDoctor(resolvedDoctorEmail, appointment);
                    }
                    logger.info("[NOTIFY] Email enviado com sucesso para médico: {}", resolvedDoctorEmail);
                } catch (Exception e) {
                    logger.error("[NOTIFY] Erro ao enviar email para médico: {}", e.getMessage(), e);
                }
            } else {
                logger.warn("[NOTIFY] Email do médico não informado");
            }
            logger.info("[NOTIFY] Processo de notificação concluído para consulta id={}", id);
            return true;
        }).orElseGet(() -> {
            logger.warn("[NOTIFY] Consulta não encontrada: id={}", id);
            return false;
        });
    }

    private String resolveDoctorEmail(String doctorName) {
        if (doctorName == null || doctorName.isBlank()) {
            return null;
        }
        return doctorRepository.findFirstByNameIgnoreCase(doctorName)
                .map(Doctor::getEmail)
                .orElse(null);
    }

    public boolean deleteAppointment(String id) {
        if (!appointmentRepository.existsById(id)) {
            return false;
        }
        appointmentRepository.deleteById(id);
        return true;
    }

    public Optional<Appointment> uploadAppointmentPhoto(String id, MultipartFile file) {
        return appointmentRepository.findById(id).map(apt -> {
            try {
                String url = fileStorageService.saveAppointmentPhoto(id, file);
                apt.setPhotoUrl(url);
                return appointmentRepository.save(apt);
            } catch (Exception e) {
                throw new RuntimeException("Failed to save photo: " + e.getMessage(), e);
            }
        });
    }

    public List<Doctor> listDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor createDoctor(Doctor doctor) {
        if (doctor.getId() == null || doctor.getId().isBlank()) {
            doctor.setId(UUID.randomUUID().toString());
        }
        return doctorRepository.save(doctor);
    }

    public Optional<Doctor> updateDoctor(String id, Doctor doctor) {
        return doctorRepository.findById(id).map(existing -> {
            doctor.setId(id);
            return doctorRepository.save(doctor);
        });
    }

    public boolean deleteDoctor(String id) {
        if (!doctorRepository.existsById(id)) {
            return false;
        }
        doctorRepository.deleteById(id);
        return true;
    }

    public DashboardSummary getDashboardSummary() {
        List<Appointment> all = appointmentRepository.findAll();
        LocalDate today = LocalDate.now();

        // Consultas a partir de hoje (incluindo hoje)
        long scheduled = all.stream()
                .filter(a -> a.getDate() != null && !a.getDate().isBefore(today))
                .count();

        // Pacientes ativos últimos 30 dias
        LocalDate windowStart = today.minusDays(30);
        long activePatients = all.stream()
                .filter(a -> a.getPatientId() != null)
                .filter(a -> a.getDate() != null && !a.getDate().isBefore(windowStart))
                .map(Appointment::getPatientId)
                .distinct()
                .count();

        // Recebíveis = soma de valores não pagos
        BigDecimal receivables = all.stream()
                .filter(a -> !a.isPaid())
                .map(Appointment::getAmount)
                .filter(x -> x != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Consultas recentes (mais novas primeiro)
        List<Appointment> recent = all.stream()
                .sorted(Comparator
                        .comparing(Appointment::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(a -> parseTimeSafe(a.getTime()))
                        .reversed())
                .limit(10)
                .collect(Collectors.toList());

        // Cálculo simples de horários livres: assume 20 slots/dia por médico para dias
        // restantes do mês
        int doctorsCount = doctorRepository.findAll().size();
        int slotsPerDoctorPerDay = 20;
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
        long remainingDays = today.datesUntil(monthEnd.plusDays(1)).count();
        long potentialSlots = doctorsCount * slotsPerDoctorPerDay * remainingDays;
        long occupiedSlots = all.stream()
                .filter(a -> a.getDate() != null && !a.getDate().isBefore(today))
                .count();
        long freeSlots = Math.max(0, potentialSlots - occupiedSlots);

        DashboardSummary s = new DashboardSummary();
        s.setScheduledCount(scheduled);
        s.setActivePatients(activePatients);
        s.setFreeSlots(freeSlots);
        s.setReceivables(receivables);
        s.setRecentAppointments(recent);
        return s;
    }

    private LocalTime parseTimeSafe(String t) {
        try {
            return t == null ? LocalTime.MIDNIGHT : LocalTime.parse(t);
        } catch (Exception e) {
            return LocalTime.MIDNIGHT;
        }
    }
}
