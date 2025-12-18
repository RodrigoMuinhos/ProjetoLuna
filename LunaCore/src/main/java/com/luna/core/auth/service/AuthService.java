package com.luna.core.auth.service;

import com.luna.core.auth.dto.FirstAdminRequest;
import com.luna.core.auth.dto.LoginRequest;
import com.luna.core.auth.dto.LoginResponse;
import com.luna.core.common.enums.LicenseStatus;
import com.luna.core.common.enums.TenantStatus;
import com.luna.core.common.enums.UserRole;
import com.luna.core.common.enums.UserStatus;
import com.luna.core.license.entity.License;
import com.luna.core.license.repository.LicenseModuleRepository;
import com.luna.core.license.repository.LicenseRepository;
import com.luna.core.security.JwtUtil;
import com.luna.core.tenant.entity.Tenant;
import com.luna.core.tenant.repository.TenantRepository;
import com.luna.core.user.entity.User;
import com.luna.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final LicenseRepository licenseRepository;
    private final LicenseModuleRepository licenseModuleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void createFirstAdmin(FirstAdminRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant não encontrado"));

        // não deixar criar mais de um OWNER
        boolean hasOwner = userRepository.existsByTenantAndRole(tenant, UserRole.OWNER);
        if (hasOwner) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe um administrador principal");
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já em uso");
        }

        User user = User.builder()
                .tenant(tenant)
                .name(request.name())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.OWNER)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário bloqueado/inativo");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }

        Tenant tenant = user.getTenant();
        if (tenant.getStatus() != TenantStatus.ACTIVE && tenant.getStatus() != TenantStatus.TRIAL) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tenant inativo");
        }

        // checar se licença está ativa
        License license = licenseRepository.findFirstByTenantOrderByValidUntilDesc(tenant)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Licença não encontrada"));

        if (license.getStatus() != LicenseStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Licença não ativa");
        }

        List<String> modules = licenseModuleRepository
                .findByLicenseAndEnabledTrue(license)
                .stream()
                .map(lm -> lm.getModuleCode().name())
                .toList();

        String token = jwtUtil.generateToken(user, tenant, modules);

        return new LoginResponse(
                token,
                "Bearer",
                3600L, // 1h (deixa alinhado com jwt.expiration)
                user.getId(),
                tenant.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                modules
        );
    }
}
