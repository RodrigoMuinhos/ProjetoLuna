package br.lunavita.totemapi.controller;

import br.lunavita.totemapi.model.Patient;
import br.lunavita.totemapi.repository.PatientRepository;
import br.lunavita.totemapi.security.UserContext;
import br.lunavita.totemapi.service.DataAccessAuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientRepository patientRepository;
    private final DataAccessAuditService auditService;

    public PatientController(PatientRepository patientRepository,
            DataAccessAuditService auditService) {
        this.patientRepository = patientRepository;
        this.auditService = auditService;
    }

    @GetMapping
    public List<Patient> list(HttpServletRequest request) {
        logAudit("LIST", "PATIENT", "all", request);
        UserContext userContext = getUserContext();
        if (userContext != null && userContext.getTenantId() != null) {
            return patientRepository.findAllByTenantId(userContext.getTenantId());
        }
        return patientRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> get(@PathVariable String id, HttpServletRequest request) {
        UserContext userContext = getUserContext();
        if (userContext != null && userContext.getTenantId() != null) {
            return patientRepository.findByTenantIdAndId(userContext.getTenantId(), id)
                    .map(patient -> {
                        auditService.logPatientRead(id, getUserEmail(), getUserRole(),
                                getIpAddress(request), getUserAgent(request));
                        return ResponseEntity.ok(patient);
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
        return patientRepository.findById(id)
                .map(patient -> {
                    auditService.logPatientRead(id, getUserEmail(), getUserRole(),
                            getIpAddress(request), getUserAgent(request));
                    return ResponseEntity.ok(patient);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Patient> getByCpf(@PathVariable String cpf, HttpServletRequest request) {
        UserContext userContext = getUserContext();
        if (userContext != null && userContext.getTenantId() != null) {
            return patientRepository.findByTenantIdAndCpf(userContext.getTenantId(), cpf)
                    .map(patient -> {
                        auditService.logPatientRead(patient.getId(), getUserEmail(), getUserRole(),
                                getIpAddress(request), getUserAgent(request));
                        return ResponseEntity.ok(patient);
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
        return patientRepository.findByCpf(cpf)
                .map(patient -> {
                    auditService.logPatientRead(patient.getId(), getUserEmail(), getUserRole(),
                            getIpAddress(request), getUserAgent(request));
                    return ResponseEntity.ok(patient);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Patient> create(@RequestBody Patient patient, HttpServletRequest request) {
        if (patient.getId() == null || patient.getId().isEmpty()) {
            patient.setId(UUID.randomUUID().toString());
        }
        
        UserContext userContext = getUserContext();
        if (userContext != null && userContext.getTenantId() != null) {
            patient.setTenantId(userContext.getTenantId());
        } else {
            return ResponseEntity.status(403).build();
        }
        
        Patient saved = patientRepository.save(patient);
        logAudit("CREATE", "PATIENT", saved.getId(), request);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> update(@PathVariable String id, @RequestBody Patient patient,
            HttpServletRequest request) {
        UserContext userContext = getUserContext();
        String tenantId = (userContext != null) ? userContext.getTenantId() : null;
        
        return patientRepository.findByTenantIdAndId(tenantId, id)
                .map(existing -> {
                    patient.setId(id);
                    patient.setTenantId(existing.getTenantId());
                    Patient updated = patientRepository.save(patient);
                    auditService.logPatientUpdate(id, getUserEmail(), getUserRole(),
                            getIpAddress(request), getUserAgent(request),
                            "[\"name\",\"email\",\"phone\",\"address\"]");
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, HttpServletRequest request) {
        UserContext userContext = getUserContext();
        String tenantId = (userContext != null) ? userContext.getTenantId() : null;
        
        if (tenantId != null) {
            if (patientRepository.findByTenantIdAndId(tenantId, id).isEmpty()) {
                return ResponseEntity.notFound().build();
            }
        } else if (!patientRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        auditService.logPatientDelete(id, getUserEmail(), getUserRole(),
                getIpAddress(request), getUserAgent(request));
        patientRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private UserContext getUserContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserContext) {
            return (UserContext) auth.getPrincipal();
        }
        return null;
    }

    private void logAudit(String action, String resourceType, String resourceId, HttpServletRequest request) {
        auditService.logAccess(null, getUserEmail(), getUserRole(), action, resourceType, resourceId,
                getIpAddress(request), getUserAgent(request), null);
    }

    private String getUserEmail() {
        UserContext ctx = getUserContext();
        return ctx != null ? ctx.getUserId() : "anonymous";
    }

    private String getUserRole() {
        UserContext ctx = getUserContext();
        return ctx != null ? ctx.getRole() : "UNKNOWN";
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
