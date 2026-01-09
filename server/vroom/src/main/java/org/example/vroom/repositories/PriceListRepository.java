package org.example.vroom.repositories;

import org.example.vroom.entities.Pricelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PriceListRepository extends JpaRepository<Pricelist, Long> {
    @Query("SELECT p.pricePerKm from Pricelist p where p.id=1")
    public float getPricePerKm();
}
