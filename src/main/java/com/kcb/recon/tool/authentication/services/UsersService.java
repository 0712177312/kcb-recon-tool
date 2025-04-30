package com.kcb.recon.tool.authentication.services;

import com.kcb.recon.tool.authentication.entities.User;
import com.kcb.recon.tool.authentication.models.*;
import com.kcb.recon.tool.common.models.AuthenticationResponse;
import com.kcb.recon.tool.common.models.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UsersService {
    ResponseMessage registerUser(UserAccountRequest request);

    ResponseMessage registerAdmin(UserAccountRequest request);

    ResponseMessage registerSuperadmin(UserAccountRequest request);

    AuthenticationResponse login(LoginRequest request);

    Page<User> superAdminAccountsWithPaginationAndUserTypeFilter(SuperAdminAccountsFilter request);

    Page<User> adminAccountsWithPaginationAndUserTypeFilter(AdminAccountsFilter request);

    List<User> adminAccountsWithoutPagination();

    List<User> userAccountsWithoutPagination();

    Page<User> userAccountsWithPaginationAndUserTypeFilter(UserAccountsFilter request);

    User findByUserId(Long id);

    ResponseMessage SuperAdminUpdateProfile(UpdateProfileRequest req);

    ResponseMessage AdminUpdateUserProfile(UpdateProfileRequest req);


    ResponseMessage activateDeactivateUserAccount(ActivateDeactivateRequest request);


    List<User> allUserAccounts();


    void logout(String username);
}