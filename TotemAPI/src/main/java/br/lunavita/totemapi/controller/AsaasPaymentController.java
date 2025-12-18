package br.lunavita.totemapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Integração Asaas descontinuada. Todos os pagamentos são feitos via LunaPay.
 * Os endpoints retornam 410 (Gone) para sinalizar a migração.
 */
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class AsaasPaymentController {

    @PostMapping("/pix")
    public ResponseEntity<String> createPixPayment() {
        return ResponseEntity.status(HttpStatus.GONE)
                .body("Integração Asaas desativada. Use LunaPay para gerar intents.");
    }

    @GetMapping("/status/{appointmentId}")
    public ResponseEntity<String> checkPaymentStatus() {
        return ResponseEntity.status(HttpStatus.GONE)
                .body("Status de pagamento agora deve ser consultado no LunaPay.");
    }

    @PostMapping("/asaas/webhook")
    public ResponseEntity<String> handleAsaasWebhook() {
        return ResponseEntity.status(HttpStatus.GONE)
                .body("Webhook Asaas desativado; pagamentos são processados pelo LunaPay.");
    }
}
