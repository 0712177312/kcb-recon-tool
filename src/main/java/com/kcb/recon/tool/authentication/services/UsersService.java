package com.kcb.recon.tool.authentication.services;

import com.kcb.recon.tool.authentication.entities.PasswordChange;
import com.kcb.recon.tool.authentication.entities.UserPasswordReset;
import com.kcb.recon.tool.authentication.entities.User;
import com.kcb.recon.tool.authentication.models.*;
import com.kcb.recon.tool.common.models.AuthenticationResponse;
import com.kcb.recon.tool.common.models.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface UsersService {
    ResponseMessage registerUser(UserAccountRequest request);

    ResponseMessage registerAdmin(UserAccountRequest request);

    ResponseMessage registerSuperadmin(UserAccountRequest request);

    AuthenticationResponse login(LoginRequest request);

    Page<User> superAdminAccountsWithPaginationAndUserTypeFilter(SuperAdminAccountsFilter request);

    List<User> superAdminAccountsWithoutPagination();

    Page<User> adminAccountsWithPaginationAndUserTypeFilter(AdminAccountsFilter request);

    List<User> adminAccountsWithoutPagination();

    List<User> userAccountsWithoutPagination();

    Page<User> userAccountsWithPaginationAndUserTypeFilter(UserAccountsFilter request);

    User findByUserId(Long id);

    ResponseMessage UserChangePassword(ChangePasswordRequest req);

    ResponseMessage AdminChangeUserPassword(AdminChangeUserPasswordRequest req);

    ResponseMessage SuperAdminUpdateProfile(UpdateProfileRequest req);

    ResponseMessage AdminUpdateUserProfile(UpdateProfileRequest req);


    ResponseMessage activateDeactivateUserAccount(ActivateDeactivateRequest request);

    ResponseMessage ResetPassword(PasswordResetRequest req);

    ResponseMessage firstTimeLoginChangePassword(FirstTimePasswordChangeRequest req);

    List<User> allUserAccounts();

    ResponseMessage sendPasswordResetRequest(String username);

//    RefreshTokenResponse refreshAccessToken(String refreshToken);

    void logout(String username);
}