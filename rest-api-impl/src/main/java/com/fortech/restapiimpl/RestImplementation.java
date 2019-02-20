package com.fortech.restapiimpl;

import com.fortech.model.entity.BaseEntity;
import com.fortech.model.exceptions.RestExceptions;
import com.fortech.model.security.services.BaseLogger;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Component
public abstract class RestImplementation<R extends CrudRepository<T, Long>, T extends BaseEntity> {
    private R repository;

    @RequestMapping(method = GET)
    public Iterable<T> getAll() {

        return this.repository.findAll();

    }

    protected RestImplementation(R repository) {
        this.repository = repository;
    }

    @RequestMapping(method = GET, value = "/{id}")
    public Optional<T> getOne(@PathVariable(value = "id") long id) {
        return this.repository.findById(id);

    }

    @RequestMapping(method = POST)
    public T create(@RequestBody T data) {
        try {
            return this.repository.save(data);
        } catch (Exception e) {
            BaseLogger.log(RestImplementation.class).error(e.getMessage());
            throw new RestExceptions.OperationFailed(e.getMessage());
        }
    }

    @RequestMapping(method = {PUT, PATCH}, value = "/{id}")
    public T update(@RequestBody T data, @PathVariable(value = "id") long id) {

        Optional<T> entity = this.repository.findById(id);

        if (entity.isPresent() && data.getId() == entity.get().getId()) {
            try {
                data.setModified(new Date());
                return this.repository.save(data);
            } catch (Exception e) {
                BaseLogger.log(RestImplementation.class).error(e.getMessage());
                throw new RestExceptions.OperationFailed(e.getMessage());
            }
        } else {
            String msg = "Entity id does not match PUT parameter";
            BaseLogger.log(RestImplementation.class).error(msg);
            throw new RestExceptions.EntityNotFoundException(msg);
        }

    }

    @RequestMapping(method = DELETE, value = "/{id}")
    public T delete(@PathVariable(value = "id") long id) {

        Optional<T> entity = this.repository.findById(id);

        if (entity.isPresent()) {
            try {
                this.repository.delete(entity.get());
                return entity.get();
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
}
