package com.payrolltaxpro.service;

import com.payrolltaxpro.domain.TaxBracket;
import com.payrolltaxpro.repository.TaxBracketRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxBracketService {

    private final TaxBracketRepository taxBracketRepository;

    public List<TaxBracket> getAllTaxBrackets() {
        return taxBracketRepository.findAll();
    }

    public List<TaxBracket> getActiveTaxBrackets() {
        return taxBracketRepository.findActiveTaxBracketsOrdered();
    }

    public TaxBracket getTaxBracketById(Long id) {
        return taxBracketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tax bracket not found: " + id));
    }

    @Transactional
    public TaxBracket createTaxBracket(TaxBracket taxBracket) {
        TaxBracket saved = taxBracketRepository.save(taxBracket);
        log.info("Created tax bracket: {} with order: {}", saved.getId(), saved.getBracketOrder());
        return saved;
    }

    @Transactional
    public TaxBracket updateTaxBracket(Long id, TaxBracket taxBracket) {
        TaxBracket existing = getTaxBracketById(id);

        existing.setMinIncome(taxBracket.getMinIncome());
        existing.setMaxIncome(taxBracket.getMaxIncome());
        existing.setPercentage(taxBracket.getPercentage());
        existing.setBracketOrder(taxBracket.getBracketOrder());
        existing.setActive(taxBracket.getActive());

        TaxBracket updated = taxBracketRepository.save(existing);
        log.info("Updated tax bracket: {} with order: {}", updated.getId(), updated.getBracketOrder());
        return updated;
    }

    @Transactional
    public void deleteTaxBracket(Long id) {
        TaxBracket taxBracket = getTaxBracketById(id);
        taxBracketRepository.delete(taxBracket);
        log.info("Deleted tax bracket: {} with order: {}", taxBracket.getId(), taxBracket.getBracketOrder());
    }
}
