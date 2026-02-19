package com.payrolltaxpro.service;

import com.payrolltaxpro.domain.SalaryStructure;
import com.payrolltaxpro.repository.SalaryStructureRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalaryStructureService {

    private final SalaryStructureRepository salaryStructureRepository;

    public List<SalaryStructure> getAllSalaryStructures() {
        return salaryStructureRepository.findAll();
    }

    public Page<SalaryStructure> getSalaryStructures(Pageable pageable) {
        return salaryStructureRepository.findAll(pageable);
    }

    public List<SalaryStructure> getActiveSalaryStructures() {
        return salaryStructureRepository.findByActiveTrue();
    }

    public SalaryStructure getSalaryStructureById(Long id) {
        return salaryStructureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Salary structure not found: " + id));
    }

    @Transactional
    public SalaryStructure createSalaryStructure(SalaryStructure salaryStructure) {
        SalaryStructure saved = salaryStructureRepository.save(salaryStructure);
        log.info("Created salary structure: {} with grade: {}", saved.getName(), saved.getGrade());
        return saved;
    }

    @Transactional
    public SalaryStructure updateSalaryStructure(Long id, SalaryStructure salaryStructure) {
        SalaryStructure existing = getSalaryStructureById(id);

        existing.setName(salaryStructure.getName());
        existing.setGrade(salaryStructure.getGrade());
        existing.setBasicSalary(salaryStructure.getBasicSalary());
        existing.setAllowance(salaryStructure.getAllowance());
        existing.setDeduction(salaryStructure.getDeduction());
        existing.setActive(salaryStructure.getActive());

        SalaryStructure updated = salaryStructureRepository.save(existing);
        log.info("Updated salary structure: {} with grade: {}", updated.getName(), updated.getGrade());
        return updated;
    }

    @Transactional
    public void deleteSalaryStructure(Long id) {
        SalaryStructure salaryStructure = getSalaryStructureById(id);
        salaryStructureRepository.delete(salaryStructure);
        log.info("Deleted salary structure: {} with grade: {}", salaryStructure.getName(), salaryStructure.getGrade());
    }
}
