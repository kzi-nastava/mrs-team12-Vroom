package org.example.vroom.repositories;

import jakarta.transaction.Transactional;
import org.example.vroom.entities.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Long> {
    @Modifying
    @Transactional
    @Query("update RegisteredUser u set u.userStatus='ACTIVE' where u.id = :id and u.userStatus='INACTIVE' ")
    int activateUserById(@Param("id") Long id);

}
