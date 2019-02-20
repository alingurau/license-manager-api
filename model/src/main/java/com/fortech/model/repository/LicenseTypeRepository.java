package com.fortech.model.repository;

import com.fortech.model.entity.LicenseType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseTypeRepository extends CrudRepository<LicenseType, Long> {
}
