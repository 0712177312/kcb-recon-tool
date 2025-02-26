//package com.kcb.recon.tool.common.services.impl;
//
//import com.kcb.recon.tool.beneficiaries.entities.Beneficiary;
//import com.kcb.recon.tool.beneficiaries.entities.BeneficiaryDocument;
//import com.kcb.recon.tool.beneficiaries.repositories.BeneficiaryRepository;
//import com.kcb.recon.tool.common.services.ReportsService;
//import com.kcb.recon.tool.common.services.UtilitiesService;
//import lombok.extern.slf4j.Slf4j;
//import net.sf.jasperreports.engine.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.sql.Connection;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//@Service
//@Slf4j
//public class ReportsServiceImpl implements ReportsService {
//
//    @Value("${checked.box.path}")
//    private String checkedBoxPath;
//
//    @Value("${unchecked.box.path}")
//    private String unCheckedBoxPath;
//
//    @Value("${report.logo.path}")
//    private String reportLogo;
//
//    @Value("${report.file.path}")
//    private String reportJrxmlPath;
//
//    @Value("${docs.storage.location}")
//    private String storagePath;
//
//    @Autowired
//    private BeneficiaryRepository beneficiaryRepository;
//
//    @Autowired
//    private UtilitiesService utilitiesService;
//
//    @Override
//    public String generateAccountOpeningForm(Long beneficiaryId, String serialNo) {
//        try (Connection con = utilitiesService.getDatabaseConnection()) {
//            if (con != null) {
//                String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//                String reportDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//                String destination = storagePath + "account_opening_form_" + beneficiaryId + "_" + serialNo + "_" + date + ".pdf";
//
//                Optional<Beneficiary> beneficiaryOptional = beneficiaryRepository.findById(beneficiaryId);
//                if (beneficiaryOptional.isPresent()) {
//                    Beneficiary beneficiary = beneficiaryOptional.get();
//
//                    Map<String, Object> parameters = new HashMap<>();
//                    parameters.put("beneficiaryId", beneficiaryId);
//                    parameters.put("serialNo", serialNo);
//                    parameters.put("unchecked", unCheckedBoxPath);
//                    parameters.put("checked", checkedBoxPath);
//                    parameters.put("logo", reportLogo);
//                    parameters.put("date", reportDate);
//
//                    parameters.put("sign", getDocumentPath(beneficiary, "Beneficiary_Signature"));
//                    parameters.put("id-front", getDocumentPath(beneficiary, "ID_FRONT"));
//                    parameters.put("id-back", getDocumentPath(beneficiary, "ID_BACK"));
//                    parameters.put("passport", getDocumentPath(beneficiary, "PASSPORT_PHOTO"));
//
//                    String jasperPath = reportJrxmlPath + "account_opening_form.jasper";
//
//                    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, parameters, con);
//                    JasperExportManager.exportReportToPdfFile(jasperPrint, destination);
//
//                    return destination;
//                } else {
//                    log.error("Beneficiary with ID {} not found, cannot generate Account Opening Form.", beneficiaryId);
//                    return null;
//                }
//            } else {
//                log.error("Database connection is null, cannot generate Account Opening Form.");
//                return null;
//            }
//        } catch (Exception e) {
//            log.error("Error generating Account Opening Form for beneficiaryId: {}, serialNo: {}: {}", beneficiaryId, serialNo, e.getMessage(), e);
//            return null;
//        }
//    }
//
//
//    private String getDocumentPath(Beneficiary beneficiary, String documentType) {
//        return beneficiary.getBeneficiaryDocuments().stream()
//                .filter(doc -> documentType.equals(doc.getDocumentType()))
//                .map(BeneficiaryDocument::getDocumentPath)
//                .findFirst()
//                .orElse("Document not found for type: " + documentType);
//    }
//
//}
