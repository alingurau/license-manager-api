package com.fortech.restapiimpl;

import com.fortech.serviceapiimpl.EntityUpdateServiceImpl;
import com.fortech.model.entity.User;
import com.fortech.model.enums.Role;
import com.fortech.model.exceptions.RestExceptions;
import com.fortech.model.message.response.ListBySalesManTerm;
import com.fortech.model.repository.UserRepository;
import com.fortech.model.security.services.BaseLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/user")
public class UserControllerImpl extends RestImplementation<UserRepository, User> {

    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private Authentication user;
    private EntityUpdateServiceImpl<User, UserRepository> reflection;

    public UserControllerImpl(
            UserRepository userRepository
    ) {
        super(userRepository);
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(method = POST, value = "/listSalesManByTerm")
    public Collection<User> listSalesManByTerm(@RequestBody @Valid ListBySalesManTerm data) {

        return this.userRepository.findByTerm(data.getTerm(), Role.SALES);

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(method = POST)
    @Override
    public User create(@RequestBody @Valid User data) {

        try {

            if (data.getPassword() != null) {
                throw new RestExceptions.BadRequest("Do not send the password directly. Use the changePassword property to assign a new password.");
            }

            if (data.getChangePassword() == null || !data.getChangePassword().isValid()) {
                throw new RestExceptions.BadRequest("Please specify a password in the correct format");
            }

            if (!data.getChangePassword().getConfirmPassword().equals(data.getChangePassword().getNewPassword())) {
                throw new RestExceptions.BadRequest("Passwords do not match");
            }

            data.setPassword(bCryptPasswordEncoder.encode(data.getChangePassword().getNewPassword()));

            return this.userRepository.save(data);

        } catch (Exception e) {
            String msg = "Entity could not be saved";
            BaseLogger.log(UserControllerImpl.class).error(msg);
            throw new RestExceptions.OperationFailed(msg);
        }

    }

    @RequestMapping(method = PATCH, value = "/{id}")
    @Override
    public User update(@RequestBody @Valid User data, @PathVariable(value = "id") long id) {

        Optional<User> entity = this.userRepository.findById(id);
        this.reflection = new EntityUpdateServiceImpl<>(this.userRepository);
        this.user = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> grantedAuthorities = user.getAuthorities();

        if (!entity.isPresent() || data.getId() != entity.get().getId()) {

            String msg = "Entity id does not match PUT parameter";
            BaseLogger.log(UserControllerImpl.class).error(msg);
            throw new RestExceptions.EntityNotFoundException(msg);

        }

        try {

            if (data.getPassword() != null) {
                throw new RestExceptions.BadRequest("Do not send the password directly. Use the changePassword property to assign a new password.");
            }

            if (data.getChangePassword() == null || !data.getChangePassword().isValid()) {
                return reflection.updateAndIgnoreNulls(data, id);
            }

            if (data.getChangePassword() != null) {
                for (GrantedAuthority role : grantedAuthorities) {
                    if (role.getAuthority().equals(Role.ADMIN.name())) {
                        return this.updateAsAdmin(data, entity.get());
                    }
                }
            }

            return this.reflection.updateAndIgnoreNulls(updateDataWithCurrentPassword(data, entity.get()), id);

        } catch (RestExceptions.BadRequest e) {
            BaseLogger.log(UserControllerImpl.class).error(e.getMessage());
            throw new RestExceptions.BadRequest(e.getMessage());
        }
    }

    private User updateAsAdmin(@NotNull User data, @NotNull User entity) {

        //Case 1 - Admin is updating another user = does NOT require current password
        if (data.getId() != this.userRepository.findByUsername(user.getPrincipal().toString()).getId()) {
            return this.reflection.updateAndIgnoreNulls(this.updateDataWithoutCurrentPassword(data), data.getId());
        }
        //Case 2 - Admin is updating his own profile = does require current password
        else {
            return this.reflection.updateAndIgnoreNulls(this.updateDataWithCurrentPassword(data, entity), data.getId());
        }

    }

    private User updateDataWithCurrentPassword(User data, User entity) {

        String currentPasswordHash = data.getChangePassword().getCurrentPassword();
        String systemPasswordHash = entity.getPassword();
        String newPassword = data.getChangePassword().getNewPassword();
        String confirmPassword = data.getChangePassword().getConfirmPassword();

        if (currentPasswordHash == null) {
            throw new RestExceptions.BadRequest("You must provide the current password");
        }

        if (bCryptPasswordEncoder.matches(currentPasswordHash, systemPasswordHash)
                && newPassword.equals(confirmPassword)) {

            data.setPassword(bCryptPasswordEncoder.encode(data.getChangePassword().getNewPassword()));

        } else {
            throw new RestExceptions.BadRequest("Incorrect system password or passwords do not match");
        }

        return data;

    }

    private User updateDataWithoutCurrentPassword(User data) {

        String newPassword = data.getChangePassword().getNewPassword();
        String confirmPassword = data.getChangePassword().getConfirmPassword();

        if (newPassword.equals(confirmPassword)) {

            data.setPassword(bCryptPasswordEncoder.encode(data.getChangePassword().getNewPassword()));

        } else {
            throw new RestExceptions.BadRequest("Incorrect system password or passwords do not match");
        }

        return data;

    }
}
