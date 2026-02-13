package org.example.vroom.repositories;

import org.example.vroom.entities.Pricelist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PriceListRepository extends JpaRepository<Pricelist, Long> {

    Optional<Pricelist> findFirstByValidTrue();

    Object findByValidTrue();
}
