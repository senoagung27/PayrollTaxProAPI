package com.payrolltaxpro.repository;

import com.payrolltaxpro.domain.SalaryStructure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, Long> {

    List<SalaryStructure> findByActiveTrue();

    Page<SalaryStructure> findByActive(Boolean active, Pageable pageable);

    List<SalaryStructure> findByGrade(String grade);

    boolean existsByName(String name);
}
