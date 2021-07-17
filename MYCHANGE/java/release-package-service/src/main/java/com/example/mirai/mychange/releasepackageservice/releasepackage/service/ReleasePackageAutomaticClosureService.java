package com.example.mirai.projectname.releasepackageservice.releasepackage.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.example.mirai.libraries.audit.component.AuditableUserAware;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.event.annotation.PublishResponse;
import com.example.mirai.libraries.hana.er.ErService;
import com.example.mirai.libraries.hana.shared.exception.HanaEntityNotFoundException;
import com.example.mirai.libraries.hana.shared.exception.HanaException;
import com.example.mirai.libraries.hana.zecn.ZecnService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.AutomaticClosureAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.AutomaticClosure;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.AutomaticClosureError;
import com.example.mirai.projectname.releasepackageservice.shared.AggregateEventBuilder;
import com.example.mirai.projectname.releasepackageservice.shared.Constants;
import er.ErDto;
import lombok.extern.slf4j.Slf4j;
import zecn.ZecnDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReleasePackageAutomaticClosureService {
    @Autowired
    private ReleasePackageService releasePackageService;

    @Autowired
    private ErService erService;

    @Autowired
    private ZecnService zecnService;

    @Resource
    ReleasePackageAutomaticClosureService self;

    @Value("${mirai.projectname.releasepackageservice.automatic-closure-report.recipients}")
    private String[] reportRecipients;

    @Value("${mirai.projectname.releasepackageservice.system-account.user-id}")
    private String systemAccountUserId;

    @PublishResponse(eventType = "AUTOMATIC-CLOSURE", eventBuilder = AggregateEventBuilder.class,
            eventEntity = "com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.AutomaticClosureError",
            responseClass = AutomaticClosureAggregate.class, destination = "com.example.mirai.projectname.releasepackageservice.automaticclosure")
    public AutomaticClosureAggregate publishAutomaticClosure(AutomaticClosureAggregate automaticClosureAggregate) {
        return automaticClosureAggregate;
    }


    public void closeReleasePackages() {
         List<BaseView> releasePackageClosureCandidateList = getReleasePackageClosureList();
        if (releasePackageClosureCandidateList == null) return;

        List<BaseView> releasePackageClosureList = filterByEcAndErStatus(releasePackageClosureCandidateList);
        log.info("Number of RPs eligible for auto closure " + releasePackageClosureList.size());
        List<AutomaticClosure> releasePackageClosureErrorList = new ArrayList<>();
        HashMap<String, String> errorEcnMap = new HashMap();
        for (BaseView baseView : releasePackageClosureList) {
            Long id = ((AutomaticClosure) baseView).getId();
            String ecnId = ((AutomaticClosure) baseView).getEcnId();
            ReleasePackage releasePackage = (ReleasePackage) releasePackageService.getEntityById(id);
            try {
                log.info("auto closing release package " + releasePackage.getId());
                setAuditUserForAutomaticClosure();
                releasePackageService.closeReleasePackage(releasePackage);
            } catch (Exception exception) {
                log.error("error in auto closing release package " + releasePackage.getId(), exception);
                errorEcnMap.put(ecnId, exception.getMessage());
                releasePackageClosureErrorList.add((AutomaticClosure) baseView);
            }
        }
        processErrors(releasePackageClosureErrorList, errorEcnMap);
    }

    private void setAuditUserForAutomaticClosure() {
        User user = new User();
        user.setUserId(systemAccountUserId);
        AuditableUserAware.AuditableUserHolder.user().set(user);
    }


    public void processErrors(List<AutomaticClosure> releasePackageClosureErrorList, HashMap<String, String> errorEcnMap) {
        AutomaticClosureAggregate automaticClosureAggregate = new AutomaticClosureAggregate();

        List<AutomaticClosureError> automaticClosureDetailList = releasePackageClosureErrorList.stream().map(releasePackageClosureError ->
                new AutomaticClosureError(releasePackageClosureError.getReleasePackageNumber(),
                        releasePackageClosureError.getEcnId(), errorEcnMap.get(releasePackageClosureError.getEcnId()), Objects.nonNull(reportRecipients) ? Arrays.asList(reportRecipients) : new ArrayList<>())
        ).collect(Collectors.toList());

        automaticClosureAggregate.setAutomaticClosureErrors(automaticClosureDetailList);

        if (automaticClosureDetailList.size() > 0)
            self.publishAutomaticClosure(automaticClosureAggregate);
    }

    private List<BaseView> getReleasePackageClosureList() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE - 1);
        Optional<String> sliceSelect = Optional.empty();

        Slice<BaseView> releasePackageSlice = releasePackageService.getEntitiesFromView("", "openActionCount:0 and completedReviewCount:totalReviewCount",
                pageable, sliceSelect, AutomaticClosure.class);
        if (Objects.isNull(releasePackageSlice) || !releasePackageSlice.hasContent()) {
            log.info("There are no release packages for automatic closure");
            return null;
        }
        List<BaseView> releasePackagesClosureList = releasePackageSlice.getContent();
        return releasePackagesClosureList;
    }

    public List<BaseView> filterByEcAndErStatus(List<BaseView> releasePackageClosureCandidateList) {

        return releasePackageClosureCandidateList.stream().filter(releasePackageClosureCandidate -> {
            AutomaticClosure automaticClosureReleasePackageCandidate = (AutomaticClosure) releasePackageClosureCandidate;
            String ecnId = automaticClosureReleasePackageCandidate.getEcnId();
            String[] ecnIdArray = ecnId.split("-");
            try {
                ErDto erDto = null;
                ZecnDto zecnDto = null;
                boolean foundInEr = true;
                boolean foundInZecn = true;
                try {
                    erDto = this.erService.getErByErId(ecnIdArray[1]);
                } catch (HanaEntityNotFoundException hanaEntityNotFoundException) {
                    log.info(ecnIdArray[1] + " not found in Er");
                    foundInEr = false;
                } catch (HanaException hanaException) {
                    log.info("not considering " + ecnIdArray[1] + " for closure check as error in querying Er " + hanaException.getApplicationStatusCode() + " " + hanaException.getMessage());
                    return false;
                }

                try {
                    zecnDto = this.zecnService.getZecnByZecnId(ecnIdArray[1]);
                } catch (HanaEntityNotFoundException hanaEntityNotFoundException) {
                    log.info(ecnIdArray[1] + " not found in Zecn");
                    foundInZecn = false;
                } catch (HanaException hanaException) {
                    log.info("not considering " + ecnIdArray[1] + " for closure check as error in querying Zecn " + hanaException.getApplicationStatusCode() + " " + hanaException.getMessage());
                    return false;
                }

                if(foundInEr && foundInZecn) {
                    if (Objects.nonNull(erDto.getStatus()) && erDto.getStatus().equalsIgnoreCase(Constants.ER_STATUS)) {
                        log.info("considering for auto closure - found in ec and er " + ecnId);
                        return Objects.nonNull(zecnDto.getStatus()) && zecnDto.getStatus() != null && zecnDto.getStatus().equalsIgnoreCase(Constants.EC_STATUS);
                    }
                } else if(foundInEr && !foundInZecn) {
                    log.info("considering for auto closure - not found in ec and found in er " + ecnId);
                    return Objects.nonNull(erDto.getStatus()) && erDto.getStatus() != null && erDto.getStatus().equalsIgnoreCase(Constants.ER_STATUS);
                } else if(!foundInEr && foundInZecn) {
                    log.info("considering for auto closure - found in ec and not found in er " + ecnId);
                    return Objects.nonNull(zecnDto.getStatus()) && zecnDto.getStatus() != null && zecnDto.getStatus().equalsIgnoreCase(Constants.EC_STATUS);
                }
                return false;
            } catch (Exception exception) {
                log.info("not considering " + ecnIdArray[1] + " for closure check as error occurred in fetching er or zecn details " + exception.getMessage());
                return false;
            }
        }).collect(Collectors.toList());
    }

}
