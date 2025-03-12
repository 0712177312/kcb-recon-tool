package com.kcb.recon.tool.authentication.services.impl;

import com.kcb.recon.tool.authentication.entities.*;
import com.kcb.recon.tool.authentication.models.*;
import com.kcb.recon.tool.authentication.repositories.PasswordChangeRepository;
import com.kcb.recon.tool.authentication.repositories.PasswordResetRepository;
import com.kcb.recon.tool.authentication.repositories.UsersRepository;
import com.kcb.recon.tool.authentication.security.JwtTokenService;
import com.kcb.recon.tool.authentication.services.PasswordHistoryService;
import com.kcb.recon.tool.authentication.services.RolesService;
import com.kcb.recon.tool.authentication.services.UserSessionsService;
import com.kcb.recon.tool.authentication.services.UsersService;
import com.kcb.recon.tool.common.enums.ChangeStatus;
import com.kcb.recon.tool.common.enums.RecordStatus;
import com.kcb.recon.tool.common.enums.ValidityStatus;
import com.kcb.recon.tool.common.models.AuthenticationResponse;
import com.kcb.recon.tool.common.models.NotificationsRequest;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.common.services.ConfigurationService;
import com.kcb.recon.tool.common.services.UtilitiesService;
import com.google.gson.Gson;
import com.kcb.recon.tool.configurations.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableBatchProcessing
public class UsersServiceImpl implements UsersService {

    @Value("${org.default.admin.role}")
    private String defaultAdminRole;

    @Value("${password.policy.regexp}")
    private String passwordPolicy;

    @Value("${login.2fa.validity-minutes}")
    private String login2FAvalidityMinutes;

    @Value("${password.reset-validity-hours}")
    private String passwordResetValidityHours;

    @Autowired
    private PasswordHistoryService passwordHistoryService;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final UsersRepository userRepository;
    private final JwtTokenService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RolesService rolesService;
    private final PasswordEncoder encoder;
    private final UtilitiesService utilitiesService;
    private final PasswordResetRepository passwordResetRepository;
    private final MenusService menusService;
    private final PasswordChangeRepository passwordChangeRepository;
    private final UserAccountTypeService userAccountTypeService;
    private final UserSessionsService userSessionsService;
    private final ConfigurationService configurationService;
    private final CountriesService countriesService;
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
        String password = utilitiesService.generatePassword(12, passwordPolicy);
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
        user.setRoles(roles);
        userRepository.save(user);
        res.setStatus(true);
        res.setData(user);
        return res;
    }

    @Override
    public ResponseMessage approveRejectUserAccount(ApproveRejectRequest request) {
        log.info("Inside approveRejectUserAccount(ApproveRejectRequest request) At {} ", new Date());
        log.info("Approving/Rejecting  user account request {} ", gson.toJson(request));
        ResponseMessage res = new ResponseMessage();
        Optional<User> existingUserById = userRepository.findById(request.getRecordId());
        if (existingUserById.isEmpty()) {
            log.warn("No record found matching the provided id! - {} ", request.getRecordId());
            res.setMessage("No record found matching the provided id!");
            res.setStatus(false);
            return res;
        } else {
            log.info("Generating User's One-Time-Password");
            String password = utilitiesService.generatePassword(12, passwordPolicy);
            log.info("Done Generating Password!");
            var user = existingUserById.get();
            user.setCheckedBy(request.getCheckerName());
            user.setCheckedOn(new Date());
            if (request.isApprove()) {
                user.setPassword(encoder.encode(password));
                user.setStatus(RecordStatus.Active.name());
                user.setValidityStatus(ValidityStatus.Approved.name());
                passwordHistoryService.addPasswordHistory(user, encoder.encode(password));
            } else {
                user.setStatus(RecordStatus.Inactive.name());
                user.setValidityStatus(ValidityStatus.Disapproved.name());
            }
            userRepository.save(user);

            if (request.isApprove()) {
                String sms = "Your credentials are : " +
                        "Username: " + user.getUsername() + " And " +
                        "Password: " + password + " .Kindly change the password after the first login.";
                String email = "Hi,<br/>Your credentials are:<br/>" +
                        "<h3>Username: " + user.getUsername() + "</h3>" +
                        "<h3>Password: " + password + "</h3>" +
                        "<br/>Kindly change the password after the first login.";
            }
            res.setStatus(true);
            res.setData(user);
            log.info("User Account Approve/Reject request processed successfully! {} ", gson.toJson(request));
            res.setMessage("Request Processed successfully!");
            return res;
        }
    }

    private NotificationsRequest getNotificationsRequest(User user, String smsBody, String emailBody, String subject) {
        var notificationRequest = new NotificationsRequest();
        notificationRequest.setEmailBody(emailBody);
        notificationRequest.setSmsBody(smsBody);
        notificationRequest.setSmsRecipient(user.getPhoneNumber());
        notificationRequest.setSubject(subject);
        notificationRequest.setAttach(false);
        notificationRequest.setAttachment(null);
        notificationRequest.setEmailRecipient(user.getEmailAddress());
        return notificationRequest;
    }

    @Override
    public ResponseMessage approveRejectUserAccountModification(ApproveRejectRequest request) {
        log.info("Inside approveRejectUserAccountModification(ApproveRejectRequest request) Method At {} ", new Date());
        log.info("Checker Request to Approve/Reject User Profile modification {} ", new Gson().toJson(request));
        var res = new ResponseMessage();
        var exists = userRepository.findById(request.getRecordId());
        if (exists.isPresent()) {
            var user = exists.get();
            UpdateProfileRequest rr = new Gson().fromJson(user.getNewValues(), UpdateProfileRequest.class);
            if (rr != null) {
                user.setModificationsCheckedOn(new Date());
                user.setModificationsCheckedBy(request.getCheckerName());

                if (request.isApprove()) {
                    if (rr.isAcctStatus()) {
                        user.setStatus(RecordStatus.Active.name());
                        user.setValidityStatus(ValidityStatus.Approved.name());
                    } else {
                        user.setStatus(RecordStatus.Inactive.name());
                    }
                    user.setUserId(rr.getUserId());
                    user.setFirstName(rr.getFirstName());
                    user.setOtherNames(rr.getOtherNames());
                    user.setGender(rr.getGender());
                    user.setEmailAddress(rr.getEmailAddress());
                    user.setPhoneNumber(rr.getPhoneNumber());
                    user.setChangeStatus(ChangeStatus.Approved.name());
                    user.setNewValues(null);
                    user.setRemarks("Approved - " + request.getCheckerName());
                } else {
                    user.setRemarks(request.getRemarks());
                    user.setChangeStatus(ChangeStatus.Disapproved.name());
                }

                userRepository.save(user);
                res.setStatus(true);
                res.setData(null);
                res.setMessage("Updated Successfully!");
                log.info("User Profile {} updated successfully!", rr.getFirstName());
            }
        } else {
            log.warn("Failed to approve/reject user profile | User does not exist!");
            res.setMessage("User does not exist!");
            res.setStatus(false);
        }
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
        String password = utilitiesService.generatePassword(12, passwordPolicy);
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
        var userType = userAccountTypeService.findByName("Country-Admin");
        userType.ifPresent(user::setAccountType);
        user.setRoles(roles);
        log.info("Saving admin account details");
        userRepository.save(user);
        passwordHistoryService.addPasswordHistory(user, encoder.encode(password));

        res.setStatus(true);
        res.setData(user);
        log.info("Admin Account created successfully! {} ", gson.toJson(request));
        res.setMessage("Account created successfully!");
        return res;
    }

    @Override
    public ResponseMessage registerSuperadmin(UserAccountRequest request) {
        log.info("Inside registerSuperadmin(UserAccountRequest request) At {} ", new Date());
        log.info("Create Super admin account request {} ", gson.toJson(request));
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
        String password = utilitiesService.generatePassword(12, passwordPolicy);

        String plainPassword = password;
        log.info("Done Generating Password for Superadmin!");
        var user = new User();
        user.setFirstName(request.getFirstName());
        user.setOtherNames(request.getOtherNames());
        user.setGender(request.getGender());
        user.setCreatedBy(request.getAdminName());
        user.setAdmin(true);
        user.setUsername(request.getUsername());
        user.setPlainPassword(plainPassword);
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
        var country = countriesService.findById(request.getOrganization());
        if (country != null) {
            user.setCountry(country);
        }
        log.info("Saving Superadmin account details");
        userRepository.save(user);
        passwordHistoryService.addPasswordHistory(user, encoder.encode(password));

        String sms = "Your credentials are : " +
                "Username: " + user.getUsername() + " And " +
                "Password: " + password + " .Kindly change the password after the first login.";
        String email = "Hi,<br/>Your credentials are:<br/>" +
                "<h3>Username: " + user.getUsername() + "</h3>" +
                "<h3>Password: " + password + "</h3>" +
                "<br/>Kindly change the password after the first login.";
        res.setStatus(true);
        res.setData(user);
        log.info("Superadmin Account created successfully! {} ", gson.toJson(request));
        res.setMessage("Account created successfully!");
        return res;
    }

    public AuthenticationResponse login(LoginRequest request) {
        log.info("Inside login(LoginRequest request) At {}", new Date());
        log.info("User login request -> {}", request.getUsername());

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("User with username {} does not exist!", request.getUsername());
                    return new RuntimeException("User does not exist!");
                });

        if (!isUserActive(user)) {
            return buildErrorResponse("User is inactive!");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (Exception e) {
            log.warn("Invalid credentials for username: {}", request.getUsername());
            return buildErrorResponse("Invalid username or password.");
        }

        if (user.isFirstTimeLogin()) {
            log.warn("User {} must change password before login.", request.getUsername());
            return AuthenticationResponse.builder()
                    .status(true)
                    .firstLogin(true)
                    .message("First-time login: Please change your password before logging in.")
                    .build();
        }

        var session = userSessionsService.findByIssuedTo(user.getUsername()).orElse(new UserSession());
//      uncomment this when pushing to prod
//        if (isSessionValid(session)) {
//            log.info("Login denied: User {} is already logged in.", request.getUsername());
//            return buildErrorResponse("User is already logged in.");
//        }

        return handleSuccessfulLogin(user, request.getUsername(), session);
    }

    private boolean isUserActive(User user) {
        return user.getStatus().equalsIgnoreCase(RecordStatus.Active.name()) &&
                user.getValidityStatus().equalsIgnoreCase(ValidityStatus.Approved.name());
    }

    private boolean isAgent(User user) {
        return "agent".equalsIgnoreCase(user.getAccountType().getName());
    }

    private boolean isSessionValid(UserSession session) {
        return session.getAccessToken() != null &&
                !jwtService.isTokenExpired(session.getAccessToken()) &&
                session.isLoggedIn();
    }

    private AuthenticationResponse handleSuccessfulLogin(User user, String username, UserSession session) {
        log.info("User {} login successful!", username);

        // Generate Tokens
        var lightweightToken = jwtService.generateLightweightToken(username);
        var refreshToken = jwtService.generateRefreshToken(username);
        var fullToken = jwtService.generateFullToken(username, user.getAuthorities());

        var menus = menusService.findMenusAndSubMenusByListOfRoles(
                user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
        );

        // Save full token in the database
        log.info("session details :: {},user details  :: {}, fullToken details :{} ", session, username, fullToken);
        updateSession(session, user, fullToken);

        return AuthenticationResponse.builder()
                .status(true)
                .message("Successful")
                .token(lightweightToken)
                .refreshToken(refreshToken)
                .menus(menus)
                .entity(user)
                .build();
    }

    private void updateSession(UserSession session, User user, String token) {
        log.info("Updating session details 1 before setting user :: {}", session.getUser());
        session.setAccessToken(token);
        session.setUser(user);
        session.setIssuedTo(user.getUsername());
//        session.setUserId(user.getId());
        session.setIssuedOn(new Date());
        session.setLoggedIn(true);
        log.info("Updating session details 2 after setting user :: {}", session.getUser());
        if (session.getUser().getUserId() == null) {
            log.info("User {} has no user session", user.getUsername());
            userSessionsService.createUserSession(session);
        } else {
            userSessionsService.updateUserSession(session);
        }
    }

    private void updateSession(User user, String fullToken) {
        var session = userSessionsService.findByIssuedTo(user.getUsername()).orElse(new UserSession());
        session.setAccessToken(fullToken);
        session.setUser(user);
        session.setLoggedIn(true);
        session.setIssuedOn(new Date());

        if (session.getId() == null) {
            userSessionsService.createUserSession(session);
        } else {
            userSessionsService.updateUserSession(session);
        }
    }


    private AuthenticationResponse buildErrorResponse(String message) {
        return AuthenticationResponse.builder()
                .status(false)
                .message(message)
                .build();
    }


    @Override
    public Page<User> superAdminAccountsWithPaginationAndUserTypeFilter(SuperAdminAccountsFilter request) {
        log.info("Inside superAdminAccountsWithPaginationAndUserTypeFilter(SuperAdminAccountsFilter request) At -> {} ", new Date());
        log.info("Get super admin accounts with pagination by status {} ", new Gson().toJson(request));
        String status = request.getStatus();
        Long acctType = 0L;
        var userType = userAccountTypeService.findByName("Superadmin");
        if (userType.isPresent()) {
            acctType = userType.get().getId();
        }

        log.info("this is the status and userType :{}, {}", status, userType);

        int page = Math.max(0, request.getPage() - 1);
        int size = Math.max(1, request.getSize());

        if (status == null || status.isEmpty()) {
            Page<User> result = userRepository.allSuperAdminAccountsWithPagination(acctType, PageRequest.of(request.getPage(), request.getSize()));
            return result;
        } else {
            return userRepository.allSuperAdminAccountsWithPagination(status, acctType, PageRequest.of(request.getPage(), request.getSize()));
        }
    }

    @Override
    public List<User> superAdminAccountsWithoutPagination() {
        return userRepository.allSuperAdminAccountsWithoutPagination();
    }

    @Override
    public Page<User> adminAccountsWithPaginationAndUserTypeFilter(AdminAccountsFilter request) {
        log.info("Inside adminAccountsWithPaginationAndUserTypeFilter(AdminAccountsFilter request) At -> {} ", new Date());
        log.info("Get admin accounts with pagination by status and organization{} ", new Gson().toJson(request));
        String status = request.getStatus();
        Long organization = request.getOrganization();
        Long acctType = 0L;
        var userType = userAccountTypeService.findByName("Country-Admin");
        if (userType.isPresent()) {
            acctType = userType.get().getId();
        }
        if ((organization == null || organization == 0) && (status == null || status.isEmpty())) {
            return userRepository.allAdminAccountsWithPagination(acctType, PageRequest.of(request.getPage(), request.getSize()));
        }

        if ((organization != null && organization > 0) && (status == null || status.isEmpty())) {
            return userRepository.allAdminAccountsByOrganizationWithPagination(organization, acctType, PageRequest.of(request.getPage(), request.getSize()));
        }

        if ((organization == null || organization == 0) && (status != null && !status.isEmpty())) {
            return userRepository.allAdminAccountsByStatusWithPagination(status, acctType, PageRequest.of(request.getPage(), request.getSize()));
        }
        return userRepository.allAdminAccountsByStatusWithPagination(status, organization, acctType, PageRequest.of(request.getPage(), request.getSize()));
    }

    @Override
    public List<User> adminAccountsWithoutPagination() {
        return userRepository.allAdminAccountsWithoutPagination();
    }

    @Override
    public List<User> userAccountsWithoutPagination() {
        log.info("Inside userAccountsWithoutPaginationPerOrganization() At {} ", new Date());
        Long acctType = 0L;
        var userType = userAccountTypeService.findByName("Agent");
        if (userType.isPresent()) {
            acctType = userType.get().getId();
        }
        return userRepository.allUserAccountsWithPagination(acctType);
    }

    @Override
    public Page<User> userAccountsWithPaginationAndUserTypeFilter(UserAccountsFilter request) {
        log.info("Inside userAccountsWithPaginationAndUserTypeFilter(UserAccountsFilter request) At -> {} ", new Date());
        log.info("Get user accounts with pagination by status, organization and branch {} ", new Gson().toJson(request));
        String status = request.getStatus();
        Long organization = request.getOrganization();
        Long branch = request.getBranch();
        Long acctType = 0L;
        var userType = userAccountTypeService.findByName("Agent");
        if (userType.isPresent()) {
            acctType = userType.get().getId();
        }
        if ((organization != null && organization > 0) && (status == null || status.isEmpty())) {
            return userRepository.allUserAccountsWithPaginationWithOrganizationOnly(PageRequest.of(request.getPage(), request.getSize()));
        }
        return userRepository.allUserAccountsWithPaginationWithStatusAndOrganization(status, PageRequest.of(request.getPage(), request.getSize()));
    }

    @Override
    public Page<User> userAccountsWithPaginationAndUserTypeFilterForReviewList(UserAccountsFilter request) {
        log.info("Inside userAccountsWithPaginationAndUserTypeFilterForReviewList(UserAccountsFilter request) At -> {} ", new Date());
        log.info("Fetch user accounts with pagination by status, organization and branch for review list {} ", new Gson().toJson(request));
        String status = request.getStatus();
        Long organization = request.getOrganization();
        Long branch = request.getBranch();
        Long acctType = 0L;
        var userType = userAccountTypeService.findByName("Agent");
        if (userType.isPresent()) {
            acctType = userType.get().getId();
        }
        if ((organization != null && organization > 0) && (status == null || status.isEmpty())) {
            return userRepository.allUserAccountsWithPaginationWithOrganizationOnlyForReviewList(organization, acctType, PageRequest.of(request.getPage(), request.getSize()));
        }
        return userRepository.allUserAccountsWithPaginationWithStatusAndOrganizationForReviewList(status, organization, acctType, PageRequest.of(request.getPage(), request.getSize()));
    }

    @Override
    public Page<User> userAccountsWithPaginationAndUserTypeFilterForModificationsReviewList(UserAccountsFilter request) {
        log.info("Inside userAccountsWithPaginationAndUserTypeFilterForModificationsReviewList(UserAccountsFilter request) At -> {} ", new Date());
        log.info("Fetch user accounts with pagination by status, organization and branch for modifications review list {} ", new Gson().toJson(request));
        String status = request.getStatus();
        Long organization = request.getOrganization();
        Long branch = request.getBranch();
        Long accountType = request.getAccountType();
        if ((organization != null && organization > 0) && (status == null || status.isEmpty())) {
            return userRepository.allUserAccountsWithPaginationWithOrganizationOnlyForModificationsReviewList(organization, accountType, PageRequest.of(request.getPage(), request.getSize()));
        }
        return userRepository.allUserAccountsWithPaginationWithStatusAndOrganizationForModificationsReviewList(status, organization, accountType, PageRequest.of(request.getPage(), request.getSize()));
    }


    @Override
    public User findByUserId(Long id) {
        log.info("Inside findByUserId(Long id) At -> {} ", id);
        log.info("Find user profile by id -> {} ", id);
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public ResponseMessage UserChangePassword(ChangePasswordRequest req) {
        log.info("Inside UserChangePassword(ChangePasswordRequest req) At -> {} ", new Date());
        log.info("user changing password");
        log.info("User Change Password Request -> {} ", gson.toJson(req));
        var res = new ResponseMessage();
        var exists = userRepository.findByUsername(req.getUsername());
        if (exists.isPresent()) {
            var user = exists.get();
            if (passwordHistoryService.isRecentPassword(user, req.getPassword())) {
                log.info("Cannot change password | Password Provided was used recently!");
                res.setMessage("Password Provided was used recently! Choose a new password!");
                res.setStatus(false);
            } else {
                user.setModifiedBy(req.getUsername());
                user.setModifiedOn(new Date());
                user.setPassword(encoder.encode(req.getPassword()));
                userRepository.save(user);
                passwordHistoryService.addPasswordHistory(user, encoder.encode(req.getPassword()));
                res.setStatus(true);
                String sms = "Your new credentials are : " +
                        "Username: " + user.getUsername() +
                        " Password: " + req.getPassword();
                String email = "Hi,<br/>Your new credentials are:<br/>" +
                        "<h3>Username: " + user.getUsername() + "</h3>" +
                        "<h3>Password: " + req.getPassword() + "</h3>";
                log.info("User has updated password successfully!");
                res.setMessage("Password Updated Successfully!");
            }
        } else {
            log.info("Cannot change password | User does not exist!");
            res.setMessage("User Does not exist!");
            res.setStatus(false);
        }
        return res;
    }

    @Override
    public ResponseMessage AdminChangeUserPassword(AdminChangeUserPasswordRequest req) {
        log.info("Inside AdminChangeUserPassword(AdminChangeUserPasswordRequest req) At -> {} ", new Date());
        log.info("Admin changing user password");
        log.info("Change Password Request -> {} ", gson.toJson(req));
        var res = new ResponseMessage();
        var password = utilitiesService.generatePassword(12, passwordPolicy);
        var exists = userRepository.findByUsername(req.getUsername());
        if (exists.isPresent()) {
            var user = exists.get();
            if (passwordHistoryService.isRecentPassword(user, password)) {
                log.info("Cannot change user password | Password Provided was used recently!");
                res.setMessage("Password Provided was used recently! Choose a new password!");
                res.setStatus(false);
            } else {
                user.setModifiedBy(req.getAdminName());
                user.setModifiedOn(new Date());
                user.setPassword(encoder.encode(password));
                userRepository.save(user);
                var passChange = passwordChangeRepository.findByUsername(req.getUsername());
                if (passChange.isPresent()) {
                    var pc = passChange.get();
                    pc.setDateReset(new Date());
                    pc.setStatus(true);
                    passwordChangeRepository.save(pc);
                }
                passwordHistoryService.addPasswordHistory(user, encoder.encode(password));
                res.setStatus(true);

                String sms = "Your new credentials are : " +
                        "Username: " + user.getUsername() +
                        " Password: " + password;
                String email = "Hi,<br/>Your new credentials are:<br/>" +
                        "<h3>Username: " + user.getUsername() + "</h3>" +
                        "<h3>Password: " + password + "</h3>";

                log.info("User password updated successfully!");
                res.setMessage("Password Updated Successfully!");
            }
        } else {
            log.info("User does not exist | Cannot change password");
            res.setMessage("User Does not exist!");
            res.setStatus(false);
        }
        return res;
    }

    @Override
    public ResponseMessage ChangeUserBranch(ChangeUserBranchRequest req) {
        log.info("Inside ChangeUserBranch(ChangeUserBranchRequest req) At -> {} ", new Date());
        log.info("Changing user's branch");
        log.info("Branch change Request Body -> {} ", gson.toJson(req));
        var res = new ResponseMessage();
        var exists = userRepository.findById(req.getUserId());
        if (exists.isPresent()) {
            var user = exists.get();
            user.setModifiedBy(req.getModifiedBy());
            user.setModifiedOn(new Date());
            userRepository.save(user);
            res.setStatus(true);
        } else {
            log.warn("User id provided does not exist! Cannot change branch!");
            res.setMessage("User Does not exist!");
            res.setStatus(false);
        }
        return res;
    }

    @Override
    public ResponseMessage ChangeUserAccountType(AdminChangeUserAccountType req) {
        log.info("Inside ChangeUserAccountType(AdminChangeUserAccountType req) At -> {} ", new Date());
        log.info("Changing user's account type");
        log.info("Account type change Request Body -> {} ", gson.toJson(req));
        var res = new ResponseMessage();
        var exists = userRepository.findById(req.getUserId());
        if (exists.isPresent()) {
            var br = userAccountTypeService.findById(req.getUserType());
            if (br.isPresent()) {
                var user = exists.get();
                user.setModifiedBy(req.getAdminName());
                user.setModifiedOn(new Date());
                user.setAccountType(br.get());
                user.setChangedValues(ChangeStatus.Approved.name());
                userRepository.save(user);
                res.setStatus(true);
                log.info("New user account type is -> {} ", br.get().getName());
                log.info("User account type changed successfully!");
                res.setMessage("Account Type Changed Successfully!");
            } else {
                log.warn("User Account type selected does not exist! Cannot change account type!");
                res.setMessage("Account type Does not exist!");
                res.setStatus(false);
            }
        } else {
            log.warn("User id provided does not exist! Cannot change user account type!");
            res.setMessage("User Does not exist!");
            res.setStatus(false);
        }
        return res;
    }

    @Override
    public ResponseMessage SuperAdminUpdateProfile(UpdateProfileRequest req) {
        log.info("Inside SuperAdminUpdateProfile(UpdateProfileRequest req) At {} ", new Date());
        log.info("Superadmin Updating user profile");
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
            var country = countriesService.findById(req.getCountry());
            if (country != null) {
                user.setCountry(country);
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
        log.info("Admin Updating user profile");
        log.info("Request (AdminUpdateUserProfile(UpdateProfileRequest req)) -> {} ", gson.toJson(req));
        var res = new ResponseMessage();
        var exists = userRepository.findById(req.getId());
        if (exists.isPresent()) {
            var user = exists.get();
            user.setChangeStatus(ChangeStatus.Pending.name());
            user.setModifiedBy(req.getModifiedBy());
            user.setModifiedOn(new Date());
            user.setNewValues(new Gson().toJson(req));
            var currRecord = new Gson().toJson(user);
            var updatedRecord = buildUpdatedRecord(currRecord, req);
            user.setChangedValues(updatedRecord);
            userRepository.save(user);
            res.setStatus(true);
            res.setMessage("Processed Successfully!");
            log.info("User Profile Update Initiated successfully!");
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
    public ResponseMessage ResetPassword(PasswordResetRequest req) {
        log.info("Inside ResetPassword(PasswordResetRequest req) At -> {} ", new Date());
        log.info("Reset Password Request -> {} ", gson.toJson(req));
        var res = new ResponseMessage();
        var user = userRepository.findByEmailAddress(req.getEmail());
        String username = "";
        if (user.isPresent()) {
            username = user.get().getUsername();
            var token = passwordResetRepository.findByUsernameAndToken(username, req.getToken());
            if (token.isPresent()) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime expiryDate = token.get().getExpiryDate();
                if (now.isAfter(expiryDate)) {
                    log.warn("Password reset token provided is expired! Cannot reset password!");
                    res.setMessage("Token Already Expired!");
                    res.setData(null);
                    res.setStatus(false);
                } else {
                    log.info("Password reset successfully!");
                    var u = user.get();
                    if (passwordHistoryService.isRecentPassword(u, req.getPassword())) {
                        log.info("Cannot Reset password | Password Provided was used recently!");
                        res.setMessage("Password Provided was used recently! Choose a new password!");
                        res.setStatus(false);
                    } else {
                        u.setPassword(encoder.encode(req.getPassword()));
                        passwordHistoryService.addPasswordHistory(u, encoder.encode(req.getPassword()));
                        userRepository.save(u);
                        var tk = token.get();
                        tk.setStatus(true);
                        passwordResetRepository.save(tk);
                        res.setMessage("Password Updated Successfully!");
                        res.setData(u);
                        res.setStatus(true);
                    }
                }
            } else {
                log.warn("The password reset token provided is invalid!");
                res.setMessage("Invalid Token!");
                res.setStatus(false);
            }
        } else {
            log.warn("{} Does not exist! Cannot reset password", req.getEmail());
            res.setMessage(req.getEmail() + " Does not exist!");
            res.setStatus(false);
        }
        return res;
    }

    @Override
    public ResponseMessage firstTimeLoginChangePassword(FirstTimePasswordChangeRequest req) {
        var res = new ResponseMessage();
        var exists = userRepository.findByUsername(req.getUsername());
        if (exists.isPresent()) {
            var user = exists.get();
            if (passwordHistoryService.isRecentPassword(user, req.getPassword())) {
                res.setMessage("Password Provided was used recently! Choose a new password!");
                res.setStatus(false);
            } else {
                user.setModifiedBy(req.getUsername());
                user.setModifiedOn(new Date());
                user.setPassword(encoder.encode(req.getPassword()));
                user.setFirstTimeLogin(false);
                userRepository.save(user);
                passwordHistoryService.addPasswordHistory(user, encoder.encode(req.getPassword()));
                res.setStatus(true);
                log.info("User has updated password successfully during first login!");
                res.setMessage("Password Updated Successfully!");
            }
        } else {
            log.info("Cannot change password during first time log in | User does not exist!");
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
    public ResponseMessage sendPasswordResetRequest(String email) {
        log.info("Inside sendPasswordResetRequest(String email) At {} ", new Date());
        log.info("User request to admin to change their password | Email {} ", email);
        var res = new ResponseMessage();
        var user = userRepository.findByEmailAddress(email);
        if (user.isPresent()) {
            Long acctType = 0L;
            var userType = userAccountTypeService.findByName("Country-Admin");
            if (userType.isPresent()) {
                acctType = userType.get().getId();
            }

            List<User> admins;
            admins = userRepository.allSuperAdminAccountsWithoutPagination();

            Random random = new Random();
            int randomIndex = random.nextInt(admins.size());
            var selectedAdmin = admins.get(randomIndex);
            var request = new PasswordChange();
            var data = passwordChangeRepository.findByUsername(user.get().getUsername());
            if (data.isPresent()) {
                request = data.get();
                request.setDateSent(new Date());
                request.setStatus(false);
                request.setSentTo(selectedAdmin.getUsername());
            } else {
                request.setDateSent(new Date());
                request.setUsername(user.get().getUsername());
                request.setStatus(false);
                request.setSentTo(selectedAdmin.getUsername());
            }
            passwordChangeRepository.save(request);
            res.setStatus(true);
            res.setMessage("Successful!");
            return res;

        }
        return null;
    }


    @Override
    public Page<PasswordChange> passwordChangeRequestsByUsername(PasswordResetFilter request) {
        log.info("Inside passwordChangeRequestsByUsername(PasswordResetFilter request) At {} ", new Date());
        log.info("User's request to fetch all password requests {} ", new Gson().toJson(request));
        return passwordChangeRepository.findByUsername(request.getUsername(), PageRequest.of(request.getPage(), request.getSize()));
    }

    @Override
    public Page<PasswordChange> passwordChangeRequestsByRecipient(PasswordResetFilter request) {
        log.info("Inside passwordChangeRequestsByRecipient(PasswordResetFilter request) At {} ", new Date());
        log.info("Admin request to fetch all password requests {} ", new Gson().toJson(request));
        return passwordChangeRepository.fetchBySentToAndStatus(request.getUsername(), PageRequest.of(request.getPage(), request.getSize()));
    }

    @Override
    public RefreshTokenResponse refreshAccessToken(String refreshToken) {
        log.info("Inside refreshAccessToken() - {}", new Date());

        if (jwtService.isTokenExpired(refreshToken)) {
            log.warn("Refresh token is expired!");
            return RefreshTokenResponse.builder()
                    .status(false)
                    .message("Refresh token is expired!")
                    .build();
        }

        String username = jwtService.extractUsername(refreshToken);
        var userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            log.warn("Username {} does not exist! Cannot refresh access token.", username);
            return RefreshTokenResponse.builder()
                    .status(false)
                    .message("Username does not exist!")
                    .build();
        }

        var user = userOptional.get();

        // Generate tokens
        String lightweightToken = jwtService.generateLightweightToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);
        String fullToken = jwtService.generateFullToken(username, user.getAuthorities());

        // Update or create session with full token
        updateSession(user, fullToken);

        return RefreshTokenResponse.builder()
                .status(true)
                .message("Token Refreshed Successfully!")
                .accessToken(lightweightToken)
                .refreshToken(newRefreshToken)
                .build();
    }


}