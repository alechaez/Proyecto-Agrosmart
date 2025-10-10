package com.agrosmart.service;

import com.agrosmart.domain.Supplier;
import com.agrosmart.dto.SupplierRequest;
import com.agrosmart.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository repo;

    @Transactional
    public Supplier create(SupplierRequest req) {
        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body vacío");
        }
        if (req.name() == null || req.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name es requerido");
        }
        if (req.ruc() == null || !req.ruc().matches("\\d{11}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ruc debe tener 11 dígitos");
        }

        try {
            if (repo.existsByRuc(req.ruc().trim())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "RUC ya existe");
            }

            var s = Supplier.builder()
                    .name(req.name().trim())
                    .ruc(req.ruc().trim())
                    .contactName(req.contactName() == null ? null : req.contactName().trim())
                    .phone(req.phone() == null ? null : req.phone().trim())
                    .build();
            return repo.save(s);

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Violación de integridad (RUC duplicado o formato/longitud inválidos)"
            );
        }
    }

    @Transactional
    public Supplier update(Long id, SupplierRequest req) {
        var s = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));

        if (req.name() != null && !req.name().isBlank()) s.setName(req.name().trim());

        if (req.ruc() != null && !req.ruc().isBlank()) {
            var ruc = req.ruc().trim();
            if (!ruc.matches("\\d{11}")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ruc debe tener 11 dígitos");
            }
            if (repo.existsByRucAndIdNot(ruc, id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "RUC ya existe en otro proveedor");
            }
            s.setRuc(ruc);
        }

        if (req.contactName() != null) s.setContactName(req.contactName().trim());
        if (req.phone() != null) s.setPhone(req.phone().trim());

        try {
            return repo.save(s);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Violación de integridad");
        }
    }
}