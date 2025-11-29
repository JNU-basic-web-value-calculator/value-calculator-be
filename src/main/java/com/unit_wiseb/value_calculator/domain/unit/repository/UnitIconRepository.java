package com.unit_wiseb.value_calculator.domain.unit.repository;

import com.unit_wiseb.value_calculator.domain.unit.entity.UnitIcon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitIconRepository extends JpaRepository<UnitIcon, Long> {

    List<UnitIcon> findAll();
}