package org.example.vroom.repositories;

import org.example.vroom.entities.FavoriteRoute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRouteRepository
        extends JpaRepository<FavoriteRoute, Long> {
}
