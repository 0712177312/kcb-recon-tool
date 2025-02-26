package com.kcb.recon.tool.configurations.services.impl;

import com.kcb.recon.tool.authentication.models.ActivateDeactivateRequest;
import com.kcb.recon.tool.common.enums.ChangeStatus;
import com.kcb.recon.tool.common.enums.RecordStatus;
import com.kcb.recon.tool.common.enums.ValidityStatus;
import com.kcb.recon.tool.common.models.RecordsFilter;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.configurations.entities.UserAccountType;
import com.kcb.recon.tool.configurations.models.UserAccountTypeRequest;
import com.kcb.recon.tool.configurations.repositories.UserAccountTypesRepository;
import com.kcb.recon.tool.configurations.services.UserAccountTypeService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserAccountTypesServiceImpl implements UserAccountTypeService {
    @Autowired
    private UserAccountTypesRepository accountTypesRepository;

    @Override
    public ResponseMessage create(UserAccountTypeRequest request) {
        log.info("Inside create(UserAccountTypeRequest request) Method At {} ", new Date());
        log.info("Create user account type request {} ", new Gson().toJson(request));
        var res = new ResponseMessage();
        var accountType = new UserAccountType();
        var exists = accountTypesRepository.findByName(request.getName());
        if (exists.isPresent()) {
            log.warn("Account Type {} Already exists!", request.getName());
            res.setMessage("Account Type " + request.getName() + " Already exists!");
            res.setStatus(false);
        } else {
            accountType.setCreatedBy(request.getUsername());
            accountType.setName(request.getName());
            accountType.setStatus(RecordStatus.Active.name());
            accountType.setValidityStatus(ValidityStatus.Approved.name());
            accountTypesRepository.save(accountType);
            res.setStatus(true);
            res.setData(null);
            res.setMessage("Created Successfully!");
            log.info("Account Type | {} ", request.getName() + " Created Successfully!");
        }
        return res;
    }

    @Override
    public ResponseMessage update(UserAccountTypeRequest request) {
        log.info("Inside update(UserAccountTypeRequest request) Method At {} ", new Date());
        log.info("Update user account type request {} ", new Gson().toJson(request));
        var res = new ResponseMessage();
        var exists = accountTypesRepository.findById(request.getId());
        if (exists.isPresent()) {
            var accountType = exists.get();
            accountType.setName(request.getName());
            accountType.setEnableOtp(request.isEnableOtp());
            accountType.setModifiedBy(request.getUsername());
            accountType.setModifiedOn(new Date());
            accountType.setChangeStatus(ChangeStatus.Approved.name());
            accountType.setNewValues(new Gson().toJson(request));
            if(request.isStatus()) {
                accountType.setStatus(RecordStatus.Active.name());
            }
            else{
                accountType.setStatus(RecordStatus.Inactive.name());
            }
            accountTypesRepository.save(accountType);
            res.setStatus(true);
            res.setData(null);
            res.setMessage("Updated Successfully!");
            log.info("{} successfully updated!", request.getName());
        } else {
            res.setMessage("Account Type Does not exist!");
            res.setStatus(false);
            log.warn("Account Type Does not exist!");
        }
        return res;
    }

    @Override
    public Optional<UserAccountType> findById(Long id) {
        log.info("Inside findById(Long id) At {} ", new Date());
        log.info("Fetching user account type details by id");
        return accountTypesRepository.findById(id);
    }

    @Override
    public UserAccountType findRecordById(Long id) {
        log.info("Inside findRecordById(Long id) {} ", new Date());
        log.info("Fetching User account type details by id");
        return accountTypesRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<UserAccountType> findByName(String name) {
        log.info("Inside findByName(String name) At {} ", new Date());
        log.info("Fetching user account type details by name");
        return accountTypesRepository.findByName(name);
    }

    @Override
    public List<UserAccountType> allWithoutPagination() {
        log.info("Inside allWithoutPagination() At {} ", new Date());
        log.info("Fetch all user account types without pagination");
        return accountTypesRepository.allWithoutPagination();
    }

    @Override
    public Page<UserAccountType> allWithPagination(RecordsFilter request) {
        log.info("Inside allWithPagination(RecordsFilter request) At {} ", new Date());
        log.info("Fetch all partner types with pagination with filters | Request {} ", new Gson().toJson(request));

        String status = request.getStatus();
        if(status!=null && !status.isEmpty()){
            return accountTypesRepository.filterWithPaginationStatusProvided(status,PageRequest.of(request.getPage(), request.getSize()));
        }
        return accountTypesRepository.allWithPagination(PageRequest.of(request.getPage(), request.getSize()));
    }

    @Override
    public ResponseMessage activateDeactivateUserAcccountType(ActivateDeactivateRequest request) {
        log.info("Inside activateDeactivateUserAcccountType(ActivateDeactivateRequest request) At {}", new Date());
        log.info("Request | {} ",new Gson().toJson(request));

        var res = new ResponseMessage();
        var exists = accountTypesRepository.findById(request.getId());
        if (exists.isPresent()) {
            var accountType = exists.get();
            accountType.setModifiedBy(request.getUserName());
            accountType.setModifiedOn(new Date());
            if(request.getAction().equalsIgnoreCase("Activate")) {
                accountType.setValidityStatus(ValidityStatus.Approved.name());
                accountType.setStatus(RecordStatus.Active.name());
                res.setStatus(true);
                log.info("User Account Type Activated Successfully!");
                res.setMessage("Activated Successfully!");
            }
            else{
                accountType.setValidityStatus(ValidityStatus.Disapproved.name());
                accountType.setStatus(RecordStatus.Inactive.name());
                res.setStatus(true);
                log.info("User Account Type Deactivated Successfully!");
                res.setMessage("Deactivated Successfully!");
            }
            accountTypesRepository.save(accountType);
        } else {
            log.warn("Failed to Activate/Deactivate User Account type | Account Type does not exist!");
            res.setMessage("User Account Type Does not exist!");
            res.setStatus(false);
        }
        return res;
    }
}
