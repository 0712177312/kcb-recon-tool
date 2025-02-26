package com.kcb.recon.tool.configurations.services;

import com.kcb.recon.tool.authentication.models.ApproveRejectRequest;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.configurations.entities.Branch;
import com.kcb.recon.tool.configurations.models.BranchRequest;
import com.kcb.recon.tool.configurations.models.BranchesFilter;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public interface BranchesService {
    ResponseMessage createBranch(BranchRequest request);
    ResponseMessage createBranchNoApproval(BranchRequest request);
    ResponseMessage updateBranch(BranchRequest request);
    Optional<Branch> findById(Long id);
    Optional<Branch> findByName(String name);
    Optional<Branch> findByCode(String code);
    List<Branch> allBranchesWithoutPagination();
    List<Branch> allBranchesWithoutPaginationPerOrganization(Long organization);
    List<Branch> allBranchesWithoutPaginationPerOrganizationNoFilters(Long organization);
    List<Branch> findBranchesByRegionId(Long regionId);
    List<Branch> paginatedBranchesListWithFilters(BranchesFilter filter);
    List<Branch> paginatedBranchesListWithFiltersForReviewList(BranchesFilter filter);
    ResponseMessage approveRejectBranch(ApproveRejectRequest request);
    List<Branch> paginatedBranchesListWithFiltersForModificationsReviewList(BranchesFilter filter);
    ResponseMessage approveRejectBranchModifications(ApproveRejectRequest request);
}