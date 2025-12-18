package br.lunavita.totemapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Pagamentos são processados exclusivamente pelo LunaPay.
 * Estes endpoints respondem 410 para sinalizar descontinuação local.
 */
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @PostMapping
    public ResponseEntity<String> capture() {
        return ResponseEntity.status(HttpStatus.GONE)
                .body("Processamento de pagamento agora é feito pelo LunaPay. Use o fluxo LunaPay.");
    }

    @GetMapping
    public ResponseEntity<String> list() {
        return ResponseEntity.status(HttpStatus.GONE)
                .body("Pagamentos não são mais armazenados localmente. Consultar LunaPay.");
    }
}
