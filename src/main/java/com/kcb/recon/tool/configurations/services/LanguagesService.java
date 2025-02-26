package com.kcb.recon.tool.configurations.services;

import com.kcb.recon.tool.common.models.RecordsFilter;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.configurations.entities.Language;
import com.kcb.recon.tool.configurations.models.LanguageRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public interface LanguagesService {
    ResponseMessage create(LanguageRequest request);
    ResponseMessage update(LanguageRequest request);
    Language findById(Long id);
    Optional<Language> findByName(String name);
    List<Language> allWithoutPagination();
    Page<Language> allWithPagination(RecordsFilter request);
}
