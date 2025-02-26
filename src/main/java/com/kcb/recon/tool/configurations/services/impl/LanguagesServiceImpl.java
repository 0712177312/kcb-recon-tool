package com.kcb.recon.tool.configurations.services.impl;

import com.kcb.recon.tool.common.enums.ChangeStatus;
import com.kcb.recon.tool.common.enums.RecordStatus;
import com.kcb.recon.tool.common.enums.ValidityStatus;
import com.kcb.recon.tool.common.models.RecordsFilter;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.configurations.entities.Language;
import com.kcb.recon.tool.configurations.models.LanguageRequest;
import com.kcb.recon.tool.configurations.repositories.LanguagesRepository;
import com.kcb.recon.tool.configurations.services.LanguagesService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LanguagesServiceImpl implements LanguagesService {

    @Autowired
    private LanguagesRepository languagesRepository;

    @Override
    public ResponseMessage create(LanguageRequest request) {
        log.info("Inside create(LanguageRequest request) Method At {} ", new Date());
        log.info("Create language request {} ", new Gson().toJson(request));
        var res = new ResponseMessage();
        var language = new Language();
        var exists = languagesRepository.findByName(request.getName());
        if (exists.isPresent()) {
            log.warn("Language {} Already exists!", request.getName());
            res.setMessage("Language " + request.getName() + " Already exists!");
            res.setStatus(false);
        } else {
            language.setCreatedBy(request.getUsername());
            language.setName(request.getName());
            language.setStatus(RecordStatus.Active.name());
            language.setValidityStatus(ValidityStatus.Approved.name());
            languagesRepository.save(language);
            res.setStatus(true);
            res.setData(null);
            res.setMessage("Created Successfully!");
            log.info("Language | {} ", request.getName() + " Created Successfully!");
        }
        return res;
    }

    @Override
    public ResponseMessage update(LanguageRequest request) {
        log.info("Inside update(LanguageRequest request) Method At {} ", new Date());
        log.info("Update Language request {} ", new Gson().toJson(request));
        var res = new ResponseMessage();
        var exists = languagesRepository.findById(request.getId());
        if (exists.isPresent()) {
            var language = exists.get();
            language.setName(request.getName());
            language.setModifiedBy(request.getUsername());
            language.setModifiedOn(new Date());
            language.setChangeStatus(ChangeStatus.Approved.name());
            language.setNewValues(new Gson().toJson(request));
            if(request.isStatus()) {
                language.setStatus(RecordStatus.Active.name());
            }
            else{
                language.setStatus(RecordStatus.Inactive.name());
            }
            languagesRepository.save(language);
            res.setStatus(true);
            res.setData(null);
            res.setMessage("Updated Successfully!");
            log.info("{} successfully updated!", request.getName());
        } else {
            res.setMessage("Language Does not exist!");
            res.setStatus(false);
            log.warn("Language Does not exist!");
        }
        return res;
    }

    @Override
    public Language findById(Long id) {
        log.info("Inside findById(Long id) At {} ", new Date());
        log.info("Fetching language details by id");
        return languagesRepository.findById(id)
                .orElse(null);
    }

    @Override
    public Optional<Language> findByName(String name) {
        log.info("Inside findByName(String name) At {} ", new Date());
        log.info("Fetching language details by name");
        return languagesRepository.findByName(name);
    }

    @Override
    public List<Language> allWithoutPagination() {
        log.info("Inside allWithoutPagination() At {} ", new Date());
        log.info("Fetch all languages without pagination");
        return languagesRepository.allWithoutPagination();
    }

    @Override
    public Page<Language> allWithPagination(RecordsFilter request) {
        log.info("Inside allWithPagination(RecordsFilter request) At {} ", new Date());
        log.info("Fetch all languages with pagination with filters | Request {} ", new Gson().toJson(request));

        String status = request.getStatus();
        if(status != null && !status.isEmpty()){
            return languagesRepository.filterWithPaginationStatusProvided(status, PageRequest.of(request.getPage(), request.getSize()));
        }
        return languagesRepository.allWithPagination(PageRequest.of(request.getPage(), request.getSize()));
    }
}
