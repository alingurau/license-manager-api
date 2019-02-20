package com.fortech.restapiimpl;

import com.fortech.model.entity.Client;
import com.fortech.model.entity.License;
import com.fortech.model.entity.LicenseType;
import com.fortech.model.entity.User;
import com.fortech.model.exceptions.RestExceptions;
import com.fortech.model.repository.ClientRepository;
import com.fortech.model.repository.LicenseRepository;
import com.fortech.model.repository.LicenseTypeRepository;
import com.fortech.model.repository.UserRepository;
import com.fortech.model.security.services.BaseLogger;
import com.fortech.serviceapiimpl.LicenseEncryptionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/license")
public class LicenseControllerImpl extends RestImplementation<LicenseRepository, License> {

    private LicenseRepository licenseRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LicenseTypeRepository licenseTypeRepository;

    private LicenseEncryptionServiceImpl licenseEncryptionService;

    public LicenseControllerImpl(
            LicenseRepository licenseRepository
    ) {
        super(licenseRepository);
        this.licenseRepository = licenseRepository;
    }

    @RequestMapping(method=GET, value = "/{id}")
    public Optional<License> getOne(@PathVariable(value = "id") long id) {
        Optional<License> license = this.licenseRepository.findById(id);

        if (!(this.hasAccessToEntity(license, null))) {
            throw new RestExceptions.BadRequest("License does not exist");
        }

        return license;
    }

    @RequestMapping(method = GET, value = "/listBySalesManId/{id}")
    public Collection<License> listBySalesManId(@PathVariable(value = "id") long id){

        Optional<User> user = this.userRepository.findById(id);

        if (!user.isPresent()) {
            throw new RestExceptions.BadRequest("User does not exist");
        }

        return this.licenseRepository.findAllLicensesBySalesMan(user.get());

    }

    @RequestMapping(method = POST)
    @Override
    public License create(@RequestBody License data){
        try {

            Optional<Client> client = this.clientRepository.findById(data.getClientId().getId());

            if(!client.isPresent()){
                throw new RestExceptions.BadRequest("Client does not exist");
            }

            Optional<LicenseType> licenseType = this.licenseTypeRepository.findById(data.getType().getId());

            if (!licenseType.isPresent()) {
                throw new RestExceptions.BadRequest("License type does not exist");
            }



            data.setClientId(client.get());
            data.setType(licenseType.get());

            License licenseWithValidation = this.licenseEncryptionService.generate(data);

            return this.licenseRepository.save(licenseWithValidation);

        } catch (Exception e) {
            BaseLogger.log(RestImplementation.class).error(e.getMessage());
            throw new RestExceptions.OperationFailed(e.getMessage());
        }
    }

    @RequestMapping(method = GET, value = "/listByClientId/{id}")
    public Collection<License> listByClientId(@PathVariable(value = "id") long id) throws Exception{

        Optional<Client> client = this.clientRepository.findById(id);

        LicenseEncryptionServiceImpl.encrypt("hello world");

        if(!client.isPresent()){
            throw new RestExceptions.BadRequest("Client does not exist");
        }

        return this.licenseRepository.findAllByClientId(client.get());

    }

    @RequestMapping(method = DELETE, value = "/{id}")
    public License delete(@PathVariable(value = "id") long id){

        Optional<License> license = this.licenseRepository.findById(id);

        if (!(this.hasAccessToEntity(license, null))) {
            throw new RestExceptions.BadRequest("License does not exist");
        }

        if(license.isPresent()){
            try{
                this.licenseRepository.delete(license.get());
                return license.get();
            } catch (Exception e){
                BaseLogger.log(RestImplementation.class).error(e.getMessage());
                throw new RestExceptions.OperationFailed(e.getMessage());
            }
        } else {
            String msg = "Entity does not exist";
            BaseLogger.log(RestImplementation.class).error(msg);
            throw new RestExceptions.EntityNotFoundException(msg);
        }

    }

    private boolean hasAccessToEntity(Optional<License> license, User user) {
        if (user == null) {
            String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            user = userRepository.findByUsername(username);
        }

        Optional<Client> client = this.clientRepository.findById(license.get().getClientId().getId());

        return client.isPresent() &&
                (
                        (client.get().getUserId().getId() == user.getId() && user.getRole().toString().equals("SALES")) ||
                                user.getRole().toString().equals("ADMIN")

                );
    }

    @RequestMapping(method = GET, value = "/listByInterval")
    public Collection<License> listAllLicensesByInterval(Integer start, Integer end){
        return this.licenseRepository.findAllLicensesByInterval(start, end);
    }
}
