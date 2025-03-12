package com.kcb.recon.tool.configurations.services;

import com.kcb.recon.tool.authentication.models.ActivateDeactivateRequest;
import com.kcb.recon.tool.common.models.RecordsFilter;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.configurations.entities.UserAccountType;
import com.kcb.recon.tool.configurations.models.UserAccountTypeRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public interface UserAccountTypeService {
    ResponseMessage create(UserAccountTypeRequest request);
    ResponseMessage update(UserAccountTypeRequest request);
    Optional<UserAccountType> findById(Long id);
    Optional<UserAccountType> findByName(String name);
    List<UserAccountType> allWithoutPagination();
}
