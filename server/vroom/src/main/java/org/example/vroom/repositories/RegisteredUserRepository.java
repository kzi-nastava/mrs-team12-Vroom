package org.example.vroom.repositories;

import jakarta.transaction.Transactional;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Long> {
    @Modifying
    @Transactional
    @Query("update RegisteredUser u set u.userStatus='ACTIVE' where u.id = :id and u.userStatus='INACTIVE' ")
    int activateUserById(@Param("id") Long id);
    Optional<RegisteredUser> findByEmail(String email);

}
