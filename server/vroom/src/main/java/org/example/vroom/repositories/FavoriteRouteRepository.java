package org.example.vroom.repositories;

import org.example.vroom.entities.FavoriteRoute;
import org.example.vroom.entities.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRouteRepository
        extends JpaRepository<FavoriteRoute, Long> {
    List<FavoriteRoute> findByUser(RegisteredUser user);
}
