package com.kcb.recon.tool.configurations.services.impl;

import com.kcb.recon.tool.authentication.models.ApproveRejectRequest;
import com.kcb.recon.tool.common.enums.ChangeStatus;
import com.kcb.recon.tool.common.enums.RecordStatus;
import com.kcb.recon.tool.common.enums.ValidityStatus;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.configurations.entities.Branch;
import com.kcb.recon.tool.configurations.models.BranchRequest;
import com.kcb.recon.tool.configurations.models.BranchesFilter;
import com.kcb.recon.tool.configurations.repositories.BranchesRepository;
import com.kcb.recon.tool.configurations.services.BranchesService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BranchesServiceImpl implements BranchesService {

    @Autowired
    private BranchesRepository branchesRepository;


    @Override
    public ResponseMessage createBranch(BranchRequest request) {
        log.info("Inside createBranch(BranchRequest request) Method At {} ", new Date());
        log.info("Create branch request {} ", new Gson().toJson(request));
        var res = new ResponseMessage();
        var branch = new Branch();
        var exists = branchesRepository.findByName(request.getName());
        var existsByCode = branchesRepository.findByCode(request.getCode());
        if (exists.isPresent()) {
            log.warn("Branch {} Already exists!", request.getName());
            res.setMessage("Branch " + request.getName() + " Already exists!");
            res.setStatus(false);
        }
        else if (existsByCode.isPresent()) {
            log.warn("Branch {} Already exists with code !", request.getName());
            res.setMessage("Branch " + request.getCode() + " Already exists!");
            res.setStatus(false);
        }
        else {
            branch.setCreatedBy(request.getUser());
            branch.setName(request.getName());
            branch.setCode(request.getCode());
            branch.setStatus(RecordStatus.Inactive.name());
            branch.setValidityStatus(ValidityStatus.Pending.name());
            branchesRepository.save(branch);
            res.setStatus(true);
            res.setData(null);
            res.setMessage("Created Successfully!");
            log.info("Branch | {} ", request.getName() + " Created Successfully!");
        }
        log.info("Exiting createBranch(BranchRequest request)");
        return res;
    }

    @Override
    public ResponseMessage createBranchNoApproval(BranchRequest request) {
        log.info("Inside ResponseMessage createBranchNoApproval(BranchRequest request) Method At {} ", new Date());
        log.info("New branch request with no maker-checker {} ", new Gson().toJson(request));
        var res = new ResponseMessage();
        var branch = new Branch();
        var exists = branchesRepository.findByName(request.getName());
        var existsByCode = branchesRepository.findByCode(request.getCode());
        if (exists.isPresent()) {
            log.warn("Branch with name {} Already exists!", request.getName());
            res.setMessage("Branch " + request.getName() + " Already exists!");
            res.setStatus(false);
        }
        else if (existsByCode.isPresent()) {
            log.warn("Branch {} Already exists with branch code !", request.getName());
            res.setMessage("Branch " + request.getCode() + " Already exists!");
            res.setStatus(false);
        }else {
            branch.setCreatedBy(request.getUser());
            branch.setName(request.getName());
            branch.setCode(request.getCode());
            branch.setStatus(RecordStatus.Active.name());
            branch.setValidityStatus(ValidityStatus.Approved.name());
//            var region = regionsService.findById(request.getRegion());
//            region.ifPresent(branch::setRegion);
            branchesRepository.save(branch);
            res.setStatus(true);
            res.setData(null);
            res.setMessage("Created Successfully!");
        }
        return res;
    }

    @Override
    public ResponseMessage updateBranch(BranchRequest request) {
        log.info("Inside updateBranch(BranchRequest request) Method At {} ", new Date());
        log.info("Update branch request {} ", new Gson().toJson(request));
        var res = new ResponseMessage();
        var exists = branchesRepository.findById(request.getId());
        if (exists.isPresent()) {
            var branch = exists.get();
            branch.setChangeStatus(ChangeStatus.Pending.name());
            branch.setModifiedBy(request.getUser());
            branch.setModifiedOn(new Date());
            branch.setNewValues(new Gson().toJson(request));
            var currRecord = new Gson().toJson(branch);
            var updatedRecord = buildUpdatedRecord(currRecord, request);
            branch.setChangedValues(updatedRecord);
            branchesRepository.save(branch);
            res.setStatus(true);
            res.setData(null);
            res.setMessage("Updated Successfully!");
            log.info("{} successfully updated!", request.getName());
        } else {
            res.setMessage("Branch Does not exist!");
            res.setStatus(false);
            log.warn("Branch Does not exist!");
        }
        return res;
    }

    public String buildUpdatedRecord(String data, BranchRequest request) {
        Branch newRecord = new Gson().fromJson(data, Branch.class);
        newRecord.setName(request.getName());
        newRecord.setCode(request.getCode());
        newRecord.setNewValues(null);
        if (request.isStatus()) {
            newRecord.setStatus(RecordStatus.Active.name());
        } else {
            newRecord.setStatus(RecordStatus.Inactive.name());
        }

        return new Gson().toJson(newRecord);
    }

    @Override
    public Optional<Branch> findById(Long id) {
        log.info("Inside findById(Long id) At {} ", new Date());
        log.info("Fetching branch details by id");
        return branchesRepository.findById(id);
    }

    @Override
    public Optional<Branch> findByName(String name) {
        log.info("Inside findByName(String name) At {} ", new Date());
        log.info("Fetching branch details by branch name");
        return branchesRepository.findByName(name);
    }

    @Override
    public Optional<Branch> findByCode(String code) {
        log.info("Inside findByCode(String code) At {} ", new Date());
        log.info("Fetching branch details by branch code");
        return branchesRepository.findByCode(code);
    }

    @Override
    public List<Branch> allBranchesWithoutPagination() {
        log.info("Inside allBranchesWithoutPagination() At {} ", new Date());
        log.info("Fetching all branches without pagination");
        return branchesRepository.allWithoutPagination();
    }

    @Override
    public List<Branch> allBranchesWithoutPaginationPerOrganization(Long organization) {
        log.info("Inside allBranchesWithoutPaginationPerOrganization(Long organization) At {} ", new Date());
        log.info("Fetching all branches without pagination per organization");
        return branchesRepository.allWithoutPaginationPerOrganization(organization);
    }

    @Override
    public List<Branch> allBranchesWithoutPaginationPerOrganizationNoFilters(Long organization) {
        log.info("Inside allBranchesWithoutPaginationPerOrganizationNoFilters(Long organization) At {} ", new Date());
        log.info("Fetching all branches without pagination per organization both active/ inactive");
        return branchesRepository.allWithoutPaginationPerOrganizationNoFilters(organization);
    }

    @Override
    public List<Branch> findBranchesByRegionId(Long regionId) {
        log.info("Inside findBranchesByRegionId(Long regionId) At {} ", new Date());
        log.info("Fetching Regions By Region Id | {} ", regionId);
        return branchesRepository.allWithoutPaginationPerRegion(regionId);
    }

    @Override
    public List<Branch> paginatedBranchesListWithFilters(BranchesFilter request) {
        log.info("Inside paginatedBranchesListWithFilters(BranchesFilter filter) at -> {}", new Date());
        log.info("Get branches with pagination by status and region: {}", new Gson().toJson(request));

        String status = request.getStatus();
        Long regionId = request.getRegion();
        Long orgId = request.getOrganization();

        // Fetch branches based on filters
        if (regionId != null && regionId > 0) {
            if (status != null && !status.isBlank()) {
                log.info("Fetching branches with organization ID {}, region ID {}, and status {}", orgId, regionId, status);
                return branchesRepository.allWithPaginationByRegionAndStatus(regionId, status);
            }
            log.info("Fetching branches with region ID {} and no specific status", regionId);
            return branchesRepository.filterWithPaginationStatusProvided(regionId, status);
        }

        if (orgId != null && orgId > 0) {
            if (status != null && !status.isBlank()) {
                log.info("Fetching branches with organization ID {} and status {}", orgId, status);
                return branchesRepository.allWithPaginationPerOrganizationAndStatus(orgId, status);
            }
            log.info("Fetching branches with org ID {} and no specific status", orgId);
            return branchesRepository.allWithPaginationPerOrganization(orgId);
        }
        log.info("Fetching branches with organization ID {} and no region or status filters", orgId);
        return branchesRepository.allWithPaginationPerOrganization(orgId);
    }


    @Override
    public List<Branch> paginatedBranchesListWithFiltersForReviewList(BranchesFilter request) {
        log.info("Inside paginatedBranchesListWithFiltersForReviewList(BranchesFilter filter) At -> {} ", new Date());
        log.info("Fetching branches with pagination by status and region for review list {} ", new Gson().toJson(request));

        String status = request.getStatus();
        Long regionId = request.getRegion();
        Long organization = request.getOrganization();

        if((organization != null && organization > 0) && (status != null && !status.isEmpty()))
        {
            return branchesRepository.allWithPaginationPerOrganizationForReviewList(organization,status);
        }
        return branchesRepository.allWithPaginationPerOrganizationForReviewList(organization);
    }

    @Override
    public ResponseMessage approveRejectBranch(ApproveRejectRequest request) {
        log.info("Inside approveRejectBranch(ApproveRejectRequest request) Method At {}", new Date());
        log.info("Approving Branch Request {} ", new Gson().toJson(request));
        var exists = branchesRepository.findById(request.getRecordId());
        var res = new ResponseMessage();
        if (exists.isPresent()) {
            var branch = exists.get();
            branch.setCheckedBy(request.getCheckerName());
            branch.setCheckedOn(new Date());
            if (request.isApprove()) {
                branch.setStatus(RecordStatus.Active.name());
                branch.setValidityStatus(ValidityStatus.Approved.name());
                branch.setRemarks("Approved - " + request.getCheckerName());
            } else {
                branch.setStatus(RecordStatus.Inactive.name());
                branch.setValidityStatus(ValidityStatus.Disapproved.name());
                branch.setRemarks(request.getRemarks());
            }
            res.setMessage("Processed Successfully!");
            res.setStatus(true);
            res.setData(null);
            branchesRepository.save(branch);
            log.info("Approve/Reject Branch completed successfully by {} ", request.getCheckerName());
        } else {
            res.setMessage("Branch Does Not Exist!");
            res.setStatus(false);
            res.setData(null);
            log.warn("Failed to approve/reject branch | Branch does not exist! Cannot process request!");
        }
        return res;
    }

    @Override
    public List<Branch> paginatedBranchesListWithFiltersForModificationsReviewList(BranchesFilter request) {
        log.info("Inside paginatedBranchesListWithFiltersForModificationsReviewList(BranchesFilter filter) At -> {} ", new Date());
        log.info("Fetching branches with pagination by status and region for modifications review list {} ", new Gson().toJson(request));

        String status = request.getStatus();
        Long regionId = request.getRegion();
        Long organization = request.getOrganization();

        if((organization != null && organization > 0) && (status != null && !status.isEmpty()))
        {
            return branchesRepository.allWithPaginationPerOrgForModificationsReviewList(organization,status);
        }
        return branchesRepository.allWithPaginationPerOrgForModificationsReviewList(organization);
    }

    @Override
    public ResponseMessage approveRejectBranchModifications(ApproveRejectRequest request) {
        log.info("Inside approveRejectBranchModifications(ApproveRejectRequest request) Method At {} ", new Date());
        log.info("Checker Request to Update branch modification {} ", new Gson().toJson(request));
        var res = new ResponseMessage();
        var exists = branchesRepository.findById(request.getRecordId());
        if (exists.isPresent()) {
            var branch = exists.get();
            BranchRequest rr = new Gson().fromJson(branch.getNewValues(), BranchRequest.class);
            if (rr != null) {
                branch.setModificationsCheckedOn(new Date());
                branch.setModificationsCheckedBy(request.getCheckerName());

                if (request.isApprove()) {
                    if (rr.isStatus()) {
                        branch.setStatus(RecordStatus.Active.name());
                        branch.setValidityStatus(ValidityStatus.Approved.name());
                    } else {
                        branch.setStatus(RecordStatus.Inactive.name());
                    }
                    branch.setName(rr.getName());
                    branch.setCode(rr.getCode());
                    branch.setChangeStatus(ChangeStatus.Approved.name());
                    branch.setNewValues(null);
                    branch.setRemarks("Approved - " + request.getCheckerName());
                } else {
                    branch.setRemarks(request.getRemarks());
                    branch.setChangeStatus(ChangeStatus.Disapproved.name());
                }
                branchesRepository.save(branch);
                res.setStatus(true);
                res.setData(null);
                res.setMessage("Updated Successfully!");
                log.info("Branch {} updated successfully!", rr.getName());
            }
        } else {
            log.warn("Failed to approve/reject Branch | Record does not exist!");
            res.setMessage("Branch does not exist!");
            res.setStatus(false);
        }
        return res;
    }
}