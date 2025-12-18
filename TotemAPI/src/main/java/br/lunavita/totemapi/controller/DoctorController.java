package br.lunavita.totemapi.controller;

import br.lunavita.totemapi.model.Doctor;
import br.lunavita.totemapi.service.DataStoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DataStoreService store;

    public DoctorController(DataStoreService store) {
        this.store = store;
    }

    @GetMapping
    public List<Doctor> list() {
        return store.listDoctors();
    }

    @PostMapping
    public ResponseEntity<Doctor> create(@RequestBody Doctor doctor) {
        Doctor created = store.createDoctor(doctor);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doctor> update(@PathVariable String id, @RequestBody Doctor doctor) {
        return store.updateDoctor(id, doctor)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean deleted = store.deleteDoctor(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
