package com.fortech.model.repository;

import com.fortech.model.entity.Client;
import com.fortech.model.entity.License;
import com.fortech.model.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface LicenseRepository extends CrudRepository<License, Long> {

    Collection<License> findAllByClientId(Client id);

    @Query(value = "SELECT l FROM license l left join l.clientId c WHERE c.userId = :user ORDER BY l.end desc")
    Collection<License> findAllLicensesBySalesMan(@Param("user") User user);

    @Query(
            value = "SELECT l.* FROM license l WHERE l.end BETWEEN DATE_ADD(CURRENT_DATE(), INTERVAL :start DAY) " +
                    "AND DATE_ADD(CURRENT_DATE(), INTERVAL :end DAY) ORDER BY l.end ASC",
            nativeQuery = true
    )
    Collection<License> findAllLicensesByInterval(@Param("start") Integer start, @Param("end") Integer end);
}
