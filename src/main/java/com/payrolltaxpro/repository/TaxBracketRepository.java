package com.payrolltaxpro.repository;

import com.payrolltaxpro.domain.TaxBracket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxBracketRepository extends JpaRepository<TaxBracket, Long> {

    List<TaxBracket> findByActiveTrueOrderByBracketOrderAsc();

    @Query("SELECT tb FROM TaxBracket tb WHERE tb.active = true ORDER BY tb.bracketOrder ASC")
    List<TaxBracket> findActiveTaxBracketsOrdered();
}
