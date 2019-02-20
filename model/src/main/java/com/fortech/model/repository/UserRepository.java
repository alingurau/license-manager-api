package com.fortech.model.repository;

import com.fortech.model.enums.Role;
import com.fortech.model.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);

    @Query(value = "SELECT p FROM User p WHERE p.role = :role AND (LOWER(p.username) LIKE CONCAT('%', LOWER(:term), '%') " +
            "OR LOWER(p.firstName) LIKE CONCAT('%', LOWER(:term), '%') " +
            "OR LOWER(p.lastName) LIKE CONCAT('%', LOWER(:term), '%')) order by p.id")
    Collection<User> findByTerm(@Param("term") String term, @Param("role") Role role);

}
