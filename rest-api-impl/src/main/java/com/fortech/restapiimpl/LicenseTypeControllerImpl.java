package com.fortech.restapiimpl;

import com.fortech.model.entity.LicenseType;
import com.fortech.model.repository.LicenseTypeRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/license-type")
public class LicenseTypeControllerImpl extends RestImplementation<LicenseTypeRepository, LicenseType> {

    private LicenseTypeRepository licenseTypeRepository;

    public LicenseTypeControllerImpl(
            LicenseTypeRepository licenseTypeRepository
    ) {
        super(licenseTypeRepository);
        this.licenseTypeRepository = licenseTypeRepository;
    }

}