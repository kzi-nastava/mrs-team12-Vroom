package org.example.vroom.repositories;

import org.example.vroom.entities.FavoriteRoute;
import org.example.vroom.entities.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRouteRepository
        extends JpaRepository<FavoriteRoute, Long> {
    List<FavoriteRoute> findByUser(RegisteredUser user);

    List<FavoriteRoute> findByUserId(Long userId);

    Optional<FavoriteRoute> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndRouteId(Long userId, Long routeId);
}
