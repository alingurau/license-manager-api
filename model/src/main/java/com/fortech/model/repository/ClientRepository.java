package com.fortech.model.repository;

import com.fortech.model.entity.Client;
import com.fortech.model.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {
    Collection<Client> findAllByUserId(User id);
}

