package com.kcb.recon.tool.authentication.services.impl;

import com.kcb.recon.tool.authentication.entities.*;
import com.kcb.recon.tool.authentication.models.*;
import com.kcb.recon.tool.authentication.repositories.UsersRepository;
import com.kcb.recon.tool.authentication.services.RolesService;
import com.kcb.recon.tool.authentication.services.UsersService;
import com.kcb.recon.tool.authentication.utils.ActiveDirectory;
import com.kcb.recon.tool.common.enums.ChangeStatus;
import com.kcb.recon.tool.common.enums.RecordStatus;
import com.kcb.recon.tool.common.enums.ValidityStatus;
import com.kcb.recon.tool.common.models.AuthenticationResponse;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.google.gson.Gson;
import com.kcb.recon.tool.configurations.repositories.CommonRepository;
import com.kcb.recon.tool.configurations.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import static com.kcb.recon.tool.authentication.utils.LdapErrorHandler.getErrorMessage;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableBatchProcessing
public class UsersServiceImpl implements UsersService {

    @Value("${org.default.admin.role}")
    private String defaultAdminRole;

    @Value("${password.policy.regexp}")
    private String passwordPolicy;

    @Value("${org.default.admin_password}")
    private String defaultAdminPassword;

    @Value("${org.default.users_password}")
    private String defaultUserPassword;

    private final SubsidiaryService subsidiaryService;
    private final UsersRepository userRepository;
    private final RolesService rolesService;
    private final PasswordEncoder encoder;
    private final MenusService menusService;
    private final ActiveDirectory activeDirectory;
    private final CommonRepository commonRepository;
    Gson gson = new Gson();

    @Override
    public ResponseMessage registerUser(UserAccountRequest request) {
        log.info("Inside registerUser(UserAccountRequest request) At {} ", new Date());
        log.info("Create user account request {} ", gson.toJson(request));
        ResponseMessage res = new ResponseMessage();
        Optional<User> existingUserByEmail = userRepository.findByEmailAddress(request.getEmailAddress());

        if (existingUserByEmail.isPresent()) {
            log.warn("Email {} already exists!", request.getEmailAddress());
            res.setMessage("Email " + request.getEmailAddress() + " already exists!");
            res.setStatus(false);
            return res;
        }

        Optional<User> existingUserByUsername = userRepository.findByUsername(request.getUsername());
        if (existingUserByUsername.isPresent()) {
            log.warn("Username {} already exists!", request.getUsername());
            res.setMessage("Username " + request.getUsername() + " already exists!");
            res.setStatus(false);
            return res;
        }
        String password = defaultUserPassword;
        var user = new User();
        user.setFirstName(request.getFirstName());
        user.setOtherNames(request.getOtherNames());
        user.setGender(request.getGender());
        user.setCreatedBy(request.getAdminName());
        user.setUsername(request.getUsername());
        user.setEmailAddress(request.getEmailAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPlainPassword(password);
        user.setAdmin(false);
        user.setPassword(encoder.encode(password));
        user.setStatus(RecordStatus.Active.name());
        user.setValidityStatus(ValidityStatus.Approved.name());

        log.info("Setting user's roles");
        Set<Role> roles = new HashSet<>();
        for (var r : request.getRoles()) {
            Role role = rolesService.findRoleById(r);
            if (role != null) {
                log.info("Role -> {} ", gson.toJson(role));
                roles.add(role);
            }
        }
        var subsidiary= subsidiaryService.findByCompanyCode(request.getCompanyCode());
        if (subsidiary != null) {
            user.setSubsidiary(subsidiary);
        }
        user.setRoles(roles);
        userRepository.save(user);
        res.setStatus(true);
        res.setData(user);
        return res;
    }

    @Override
    public ResponseMessage registerAdmin(UserAccountRequest request) {
        log.info("Inside registerAdmin(UserAccountRequest request) At {} ", new Date());
        log.info("Create admin account request {} ", gson.toJson(request));
        ResponseMessage res = new ResponseMessage();
        Optional<User> existingUserByEmail = userRepository.findByEmailAddress(request.getEmailAddress());

        if (existingUserByEmail.isPresent()) {
            log.warn("Email (For Admin User) {} already exists!", request.getEmailAddress());
            res.setMessage("Email " + request.getEmailAddress() + " already exists!");
            res.setStatus(false);
            return res;
        }

        Optional<User> existingUserByUsername = userRepository.findByUsername(request.getUsername());
        if (existingUserByUsername.isPresent()) {
            log.warn("Username for admin user {} already exists!", request.getUsername());
            res.setMessage("Username " + request.getUsername() + " already exists!");
            res.setStatus(false);
            return res;
        }
        log.info("Generating Admin's One-Time-Password");
        String password = defaultAdminPassword;
        log.info("Done Generating Password for admin!");
        var user = new User();
        user.setFirstName(request.getFirstName());
        user.setOtherNames(request.getOtherNames());
        user.setGender(request.getGender());
        user.setCreatedBy(request.getAdminName());
        user.setAdmin(true);
        user.setUsername(request.getUsername());
        user.setEmailAddress(request.getEmailAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(encoder.encode(password));
        user.setStatus(RecordStatus.Active.name());
        user.setValidityStatus(ValidityStatus.Approved.name());
        Set<Role> roles = new HashSet<>();
        var role = rolesService.findByRoleName(defaultAdminRole);
        role.ifPresent(roles::add);
        user.setRoles(roles);
        log.info("Saving admin account details");
        userRepository.save(user);
        res.setStatus(true);
        res.setData(user);
        log.info("Admin Account created successfully! {} ", gson.toJson(request));
        res.setMessage("Account created successfully!");
        return res;
    }

    @Override
    public ResponseMessage registerSuperadmin(UserAccountRequest request) {
        ResponseMessage res = new ResponseMessage();
        Optional<User> existingUserByEmail = userRepository.findByEmailAddress(request.getEmailAddress());

        if (existingUserByEmail.isPresent()) {
            log.warn("Email (For Superadmin User) {} already exists!", request.getEmailAddress());
            res.setMessage("Email " + request.getEmailAddress() + " already exists!");
            res.setStatus(false);
            return res;
        }

        Optional<User> existingUserByUsername = userRepository.findByUsername(request.getUsername());
        if (existingUserByUsername.isPresent()) {
            log.warn("Username for Superadmin user {} already exists!", request.getUsername());
            res.setMessage("Username " + request.getUsername() + " already exists!");
            res.setStatus(false);
            return res;
        }
        log.info("Generating Superadmin's One-Time-Password");
        String password = defaultAdminPassword;
        log.info("Done Generating Password for Superadmin!");
        var user = new User();
        user.setFirstName(request.getFirstName());
        user.setOtherNames(request.getOtherNames());
        user.setGender(request.getGender());
        user.setCreatedBy(request.getAdminName());
        user.setAdmin(true);
        user.setUsername(request.getUsername());
        user.setEmailAddress(request.getEmailAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(encoder.encode(password));
        user.setStatus(RecordStatus.Active.name());
        user.setValidityStatus(ValidityStatus.Approved.name());

        log.info("Setting Superadmin's role");
        Set<Role> roles = new HashSet<>();
        for (var r : request.getRoles()) {
            Role role = rolesService.findRoleById(r);
            if (role != null) {
                log.info("Superadmin Role -> {} ", gson.toJson(role));
                roles.add(role);
            }
        }

        user.setRoles(roles);
        log.info("Company code {}", request.getCountry());
        var subsidiary= subsidiaryService.findByCompanyName(request.getCountry());
        log.info("Subsidiary details -> {} ", gson.toJson(subsidiary));
        if (subsidiary != null) {
            user.setSubsidiary(subsidiary);
        }
        log.info("Saving Superadmin account details");
        userRepository.save(user);
        res.setStatus(true);
        res.setData(user);
        log.info("Superadmin Account created successfully! {} ", gson.toJson(request));
        res.setMessage("Account created successfully!");
        return res;
    }

    public AuthenticationResponse login(LoginRequest request) {
        log.info("Inside login(LoginRequest request) At {}", new Date());
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("User with username {} does not exist!", request.getUsername());
                    return new RuntimeException("User does not exist!");
                });
        if (!isUserActive(user)) {
            return buildErrorResponse("User is inactive!");
        }

        AdResponse adResponse = loginActiveDirectory(request.getUsername(), request.getPassword());
        log.info(" ad response | {}",adResponse);
        String errorMessage = getErrorMessage(adResponse);

        if (adResponse.getCode() != 200) {
            return AuthenticationResponse.builder()
                    .status(false)
                    .failedAdAuth(true)
                    .message(errorMessage)
                    .build();
        }
        return handleSuccessfulLogin(user, request.getUsername());
    }

    private boolean isUserActive(User user) {
        return user.getStatus().equalsIgnoreCase(RecordStatus.Active.name()) &&
                user.getValidityStatus().equalsIgnoreCase(ValidityStatus.Approved.name());
    }

    public AdResponse loginActiveDirectory(String userName, String password) {
        return activeDirectory.login(userName, password);
    }


    private AuthenticationResponse handleSuccessfulLogin(User user, String username) {
        log.info("User {} login successful!", username);

        var menus = menusService.findMenusAndSubMenusByListOfRoles(
                user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
        );
        return AuthenticationResponse.builder()
                .status(true)
                .message("Successful")
                .menus(menus)
                .entity(user)
                .build();
    }

    private AuthenticationResponse buildErrorResponse(String message) {
        return AuthenticationResponse.builder()
                .status(false)
                .message(message)
                .build();
    }


    @Override
    public Page<User> superAdminAccountsWithPaginationAndUserTypeFilter(SuperAdminAccountsFilter request) {
        String status = request.getStatus();
        log.info("this is the status : {}", status);
        if (status == null || status.isEmpty()) {
            Page<User> result = userRepository.allSuperAdminAccountsWithPagination(PageRequest.of(request.getPage(), request.getSize()));
            return result;
        } else {
            return userRepository.allSuperAdminAccountsWithPagination(status, PageRequest.of(request.getPage(), request.getSize()));
        }
    }


    @Override
    public Page<User> adminAccountsWithPaginationAndUserTypeFilter(AdminAccountsFilter request) {
        log.info("Inside adminAccountsWithPaginationAndUserTypeFilter(AdminAccountsFilter request) At -> {} ", new Date());
        log.info("Get admin accounts with pagination by status and organization{} ", new Gson().toJson(request));
        String status = request.getStatus();
        if (status == null || status.isEmpty()) {
            return userRepository.allAdminAccountsWithPagination(PageRequest.of(request.getPage(), request.getSize()));
        }

        return userRepository.allAdminAccountsByStatusWithPagination(status, PageRequest.of(request.getPage(), request.getSize()));
    }

    @Override
    public List<User> adminAccountsWithoutPagination() {
        return userRepository.allAdminAccountsWithoutPagination();
    }

    @Override
    public List<User> userAccountsWithoutPagination() {
        log.info("Inside userAccountsWithoutPaginationPerOrganization() At {} ", new Date());
        return userRepository.allUserAccountsWithPagination();
    }

    @Override
    public Page<User> userAccountsWithPaginationAndUserTypeFilter(UserAccountsFilter request) {
        log.info("Inside userAccountsWithPaginationAndUserTypeFilter(UserAccountsFilter request) At -> {} ", new Date());
        String status = request.getStatus();
        if (status == null || status.isEmpty()) {
            return userRepository.allUserAccountsWithPagination(PageRequest.of(request.getPage(), request.getSize()));
        }
        return userRepository.allUserAccountsWithPaginationWithStatus(status, PageRequest.of(request.getPage(), request.getSize()));
    }


    @Override
    public User findByUserId(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public ResponseMessage SuperAdminUpdateProfile(UpdateProfileRequest req) {
        log.info("Profile Request -> {} ", gson.toJson(req));
        var res = new ResponseMessage();
        var exists = userRepository.findById(req.getId());
        if (exists.isPresent()) {
            var user = exists.get();
            user.setModifiedBy(req.getModifiedBy());
            user.setModifiedOn(new Date());
            user.setNewValues(gson.toJson(req));
            user.setChangeStatus(ChangeStatus.Approved.name());
            if (req.isAcctStatus()) {
                user.setStatus(RecordStatus.Active.name());
            } else {
                user.setStatus(RecordStatus.Inactive.name());
            }
            user.setFirstName(req.getFirstName());
            user.setOtherNames(req.getOtherNames());
            user.setGender(req.getGender());
            user.setEmailAddress(req.getEmailAddress());
            user.setPhoneNumber(req.getPhoneNumber());
            var subsidiary= subsidiaryService.findByCompanyName(req.getCompanyName());
            if (subsidiary != null) {
                user.setSubsidiary(subsidiary);
            }
            userRepository.save(user);
            res.setStatus(true);
            res.setMessage("Profile Updated Successfully!");
            log.info("User Profile Updated successfully!");
        } else {
            log.warn("User does not exist! Cannot update profile!");
            res.setMessage("User Does not exist!");
            res.setStatus(false);
        }
        return res;
    }

    @Override
    public ResponseMessage AdminUpdateUserProfile(UpdateProfileRequest req) {
        log.info("Inside AdminUpdateUserProfile(UpdateProfileRequest req) At {} ", new Date());
        log.info("user details to be updated | {}",new Gson().toJson(req));
        var res = new ResponseMessage();
        var exists = userRepository.findById(req.getId());
        if (exists.isPresent()) {
            var user = exists.get();
            user.setChangeStatus(ChangeStatus.Approved.name());
            user.setModifiedBy(req.getModifiedBy());
            user.setModifiedOn(new Date());
            user.setNewValues(new Gson().toJson(req));
            if (req.isAcctStatus()) {
                user.setStatus(RecordStatus.Active.name());
            } else {
                user.setStatus(RecordStatus.Inactive.name());
            }
            user.setUserId(req.getUserId());
            user.setFirstName(req.getFirstName());
            for (var r : req.getRoles()) {
                commonRepository.updateUserRoles(r,req.getId());
            }
            var subsidiary= subsidiaryService.findByCompanyCode(req.getCompanyCode());
            if (subsidiary != null) {
                user.setSubsidiary(subsidiary);
            }
            user.setOtherNames(req.getOtherNames());
            user.setGender(req.getGender());
            user.setEmailAddress(req.getEmailAddress());
            user.setPhoneNumber(req.getPhoneNumber());
            userRepository.save(user);
            res.setStatus(true);
            res.setMessage("User Profile Updated successfully!!");
        } else {
            log.warn("User does not exist! Admin request to update user profile cannot be processed!");
            res.setMessage("User Does not exist!");
            res.setStatus(false);
        }
        return res;
    }

    private String buildUpdatedRecord(String currRecord, UpdateProfileRequest req) {
        User user = new Gson().fromJson(currRecord, User.class);
        if (req.isAcctStatus()) {
            user.setStatus(RecordStatus.Active.name());
        } else {
            user.setStatus(RecordStatus.Inactive.name());
        }
        user.setUserId(req.getUserId());
        user.setFirstName(req.getFirstName());
        for (var r : req.getRoles()) {
            commonRepository.updateUserRoles(r,req.getId());
        }
        var subsidiary= subsidiaryService.findByCompanyCode(req.getCompanyCode());
        if (subsidiary != null) {
            user.setSubsidiary(subsidiary);
        }
        user.setOtherNames(req.getOtherNames());
        user.setGender(req.getGender());
        user.setEmailAddress(req.getEmailAddress());
        user.setPhoneNumber(req.getPhoneNumber());
        return new Gson().toJson(user);
    }

    @Override
    public ResponseMessage activateDeactivateUserAccount(ActivateDeactivateRequest request) {
        log.info("Inside activateDeactivateUserAccount(ActivateDeactivateRequest request) At {}", new Date());
        log.info("Request to Activate / Deactivate User Account | {} ", new Gson().toJson(request));

        var res = new ResponseMessage();
        var exists = userRepository.findById(request.getId());
        if (exists.isPresent()) {
            var user = exists.get();
            user.setModifiedBy(request.getUserName());
            user.setModifiedOn(new Date());
            if (request.getAction().equalsIgnoreCase("Activate")) {
                user.setValidityStatus(ValidityStatus.Approved.name());
                user.setStatus(RecordStatus.Active.name());
                res.setStatus(true);
                log.info("Account Activated Successfully!");
                res.setMessage("Activated Successfully!");
            } else {
                user.setValidityStatus(ValidityStatus.Disapproved.name());
                user.setStatus(RecordStatus.Inactive.name());
                res.setStatus(true);
                log.info("Account Deactivated Successfully!");
                res.setMessage("Deactivated Successfully!");
            }
            userRepository.save(user);
        } else {
            log.warn("Failed to Activate/Deactivate User Account | It does not exist!");
            res.setMessage("User Does not exist!");
            res.setStatus(false);
        }
        return res;
    }



    @Override
    public List<User> allUserAccounts() {
        log.info("Inside allUserAccounts() At -> {} ", new Date());
        log.info("Fetching all user accounts");
        return userRepository.findAll();
    }

    @Override
    public void logout(String username) {
        log.info("Inside logout(String username) {} At {}", username, new Date());
        var user  =userRepository.findByUsername(username);
        user.ifPresent(value ->log.info("user-id |{}",user.get().getId()));
        user.ifPresent(value ->userRepository.updateLoggedIn(value.getId()));
    }
}
