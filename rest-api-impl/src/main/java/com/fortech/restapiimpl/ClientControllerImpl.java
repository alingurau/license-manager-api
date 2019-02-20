package com.fortech.restapiimpl;

import com.fortech.model.entity.Client;
import com.fortech.model.entity.User;
import com.fortech.model.exceptions.RestExceptions;
import com.fortech.model.repository.ClientRepository;
import com.fortech.model.repository.UserRepository;
import com.fortech.model.security.services.BaseLogger;
import com.fortech.serviceapiimpl.EntityUpdateServiceImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping("/client")
public class ClientControllerImpl extends RestImplementation<ClientRepository, Client> {


    private ClientRepository clientRepository;

    private UserRepository userRepository;
    private EntityUpdateServiceImpl<Client, ClientRepository> reflection;

    public ClientControllerImpl(
            ClientRepository clientRepository
    ) {
        super(clientRepository);
        this.clientRepository = clientRepository;
    }

    @RequestMapping(method = POST)
    @Override
    public Client create(@RequestBody Client data) {
        try {

            Optional<User> user = this.userRepository.findById(data.getUserId().getId());

            if (!user.isPresent()) {
                throw new RestExceptions.BadRequest("User does not exist");
            }

            data.setUserId(user.get());

            return this.clientRepository.save(data);

        } catch (Exception e) {
            BaseLogger.log(ClientControllerImpl.class).error(e.getMessage());
            throw new RestExceptions.OperationFailed(e.getMessage());
        }
    }

    @RequestMapping(method = PATCH, value = "/{id}")
    @Override
    public Client update(@RequestBody Client data, @PathVariable(value = "id") long id) {

        Optional<Client> entity = this.clientRepository.findById(id);
        this.reflection = new EntityUpdateServiceImpl<>(this.clientRepository);

        if (!entity.isPresent() || data.getId() != entity.get().getId()) {

            String msg = "Entity id does not match PUT parameter";
            BaseLogger.log(ClientControllerImpl.class).error(msg);
            throw new RestExceptions.EntityNotFoundException(msg);

        }

        if (!(this.hasAccessToEntity(entity, null))) {
            throw new RestExceptions.BadRequest("Client does not exist");
        }

        Optional<User> user = this.userRepository.findById(data.getUserId().getId());

        if (!user.isPresent()) {
            throw new RestExceptions.BadRequest("User does not exist");
        }

        data.setUserId(user.get());

        return this.reflection.updateAndIgnoreNulls(data, id);

    }

    @RequestMapping(method = GET, value = "/listByUserId/{id}")
    public Collection<Client> listByUserId(@PathVariable(value = "id") long id) {

        Optional<User> user = this.userRepository.findById(id);

        if (!user.isPresent()) {
            throw new RestExceptions.BadRequest("User does not exist");
        }

        return this.clientRepository.findAllByUserId(user.get());

    }

    @RequestMapping(method = GET, value = "/{id}")
    @Override
    public Optional<Client> getOne(@PathVariable(value = "id") long id) {
        try {
            Optional<Client> client = clientRepository.findById(id);

            if (!(this.hasAccessToEntity(client, null))) {
                throw new RestExceptions.BadRequest("Client does not exist");
            }

            return client;
        } catch (Exception e) {
            BaseLogger.log(ClientControllerImpl.class).error(e.getMessage());
            throw new RestExceptions.OperationFailed(e.getMessage());
        }
    }

    @RequestMapping(method = DELETE, value = "/{id}")
    public Client delete(@PathVariable(value = "id") long id) {

        Optional<Client> client = this.clientRepository.findById(id);

        if (!(this.hasAccessToEntity(client, null))) {
            throw new RestExceptions.BadRequest("Client does not exist");
        }

        if (client.isPresent()) {
            try {
                this.clientRepository.delete(client.get());
                return client.get();
            } catch (Exception e) {
                BaseLogger.log(RestImplementation.class).error(e.getMessage());
                throw new RestExceptions.OperationFailed(e.getMessage());
            }
        } else {
            String msg = "Entity does not exist";
            BaseLogger.log(RestImplementation.class).error(msg);
            throw new RestExceptions.EntityNotFoundException(msg);
        }

    }

    private boolean hasAccessToEntity(Optional<Client> client, User user) {
        if (user == null) {
            String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            user = userRepository.findByUsername(username);
        }

        return client.isPresent() &&
                (
                        (client.get().getUserId().getId() == user.getId() && user.getRole().toString().equals("SALES")) ||
                                user.getRole().toString().equals("ADMIN")

                );
    }

}
