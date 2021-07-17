
package com.example.mirai.projectname.releasepackageservice.releasepackage.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.mirai.libraries.core.exception.EntityIdNotFoundException;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.PrerequisiteReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.Overview;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.PrerequisitesOverview;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.ReleasePackageReorderPrerequisites;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
public class ReleasePackagePrerequisitesService  {

    @Autowired
    private ReleasePackageService releasePackageService;

    public BaseEntityList <Overview> add(Long id, List<PrerequisiteReleasePackage> perquisiteReleasePackages) {
        ReleasePackage releasePackage = (ReleasePackage) releasePackageService.getEntityById(id);
        if(Objects.isNull(releasePackage))
            throw new EntityIdNotFoundException();

        Optional<PrerequisiteReleasePackage> optionalPrequisiteReleasePackage = releasePackage.getPrerequisiteReleasePackages().stream().max(Comparator.comparing(PrerequisiteReleasePackage::getSequence));

        AtomicInteger sequence = new AtomicInteger(1);
        if(Objects.nonNull(optionalPrequisiteReleasePackage) && optionalPrequisiteReleasePackage.isPresent())
            sequence.set(optionalPrequisiteReleasePackage.get().getSequence() + 1);

        perquisiteReleasePackages.stream().forEach(prequisiteReleasePackage -> prequisiteReleasePackage.setSequence(sequence.getAndIncrement()));

        ReleasePackage updatedReleasePackage = new ReleasePackage();

        List<PrerequisiteReleasePackage> prerequisites = releasePackage.getPrerequisiteReleasePackages();
        if(Objects.isNull(prerequisites))
            prerequisites = new ArrayList<>();
        prerequisites.addAll(perquisiteReleasePackages);

        updatedReleasePackage.setId(id);
        updatedReleasePackage.setPrerequisiteReleasePackages(prerequisites);

        Map<String, Object> changedAttributes = new HashMap();
        changedAttributes.put("prerequisite_release_packages", prerequisites);
        releasePackageService.update(updatedReleasePackage, changedAttributes);


        return getOverviewResponse(id);
    }

    public ReleasePackageReorderPrerequisites reorder(Long id, PrerequisiteReleasePackage newPrerequisiteReleasePackage, Boolean isImpactCheckRequired) {
        ReleasePackage releasePackage = (ReleasePackage) releasePackageService.getEntityById(id);
        if(Objects.isNull(releasePackage))
            throw new EntityIdNotFoundException();

        List<PrerequisiteReleasePackage> prerequisiteReleasePackages = releasePackage.getPrerequisiteReleasePackages();
        Optional<PrerequisiteReleasePackage> optionalPrequisiteReleasePackage = prerequisiteReleasePackages.stream().filter(prerequisite -> prerequisite.getReleasePackageId().equals(newPrerequisiteReleasePackage.getReleasePackageId())).findFirst();
        if(optionalPrequisiteReleasePackage.isEmpty())
            throw new EntityIdNotFoundException();

        PrerequisiteReleasePackage currentPrerequisiteReleasePackage = optionalPrequisiteReleasePackage.get();
        List<PrerequisiteReleasePackage> impactedPrerequisiteReleasePackages;
        prerequisiteReleasePackages.sort(Comparator.comparing(item -> item.getSequence()));
        if(currentPrerequisiteReleasePackage.getSequence() < newPrerequisiteReleasePackage.getSequence()) {
            moveForward(prerequisiteReleasePackages, currentPrerequisiteReleasePackage.getSequence() - 1, newPrerequisiteReleasePackage.getSequence() - 1);
            impactedPrerequisiteReleasePackages =  prerequisiteReleasePackages.stream().filter(prequisiteReleasePackage -> prequisiteReleasePackage.getSequence() < newPrerequisiteReleasePackage.getSequence()).collect(Collectors.toList());
        } else {
            moveBackward(prerequisiteReleasePackages, currentPrerequisiteReleasePackage.getSequence() - 1, newPrerequisiteReleasePackage.getSequence() - 1);
            impactedPrerequisiteReleasePackages = prerequisiteReleasePackages.stream().filter(prequisiteReleasePackage -> prequisiteReleasePackage.getSequence() > newPrerequisiteReleasePackage.getSequence()).collect(Collectors.toList());
        }
        AtomicInteger idx= new AtomicInteger(1);
        prerequisiteReleasePackages.stream().forEach(prequisiteReleasePackage -> prequisiteReleasePackage.setSequence(idx.getAndIncrement()));
        if(isImpactCheckRequired && Objects.nonNull(impactedPrerequisiteReleasePackages) && impactedPrerequisiteReleasePackages.size() > 0)
            return getImpact(id, impactedPrerequisiteReleasePackages);

        if(!isImpactCheckRequired)
            return updatePrerequisites(id, prerequisiteReleasePackages);

        return null;
    }

    public ReleasePackageReorderPrerequisites delete(Long id, PrerequisiteReleasePackage newPrerequisiteReleasePackage, Boolean isImpactCheckRequired) {
        ReleasePackage releasePackage = (ReleasePackage) releasePackageService.getEntityById(id);
        if(Objects.isNull(releasePackage))
            throw new EntityIdNotFoundException();

        int sequenceOfLastItem = releasePackage.getPrerequisiteReleasePackages().size();
        newPrerequisiteReleasePackage.setSequence(sequenceOfLastItem);


        List<PrerequisiteReleasePackage> prerequisiteReleasePackages = releasePackage.getPrerequisiteReleasePackages();
        Optional<PrerequisiteReleasePackage> optionalPrequisiteReleasePackage = prerequisiteReleasePackages.stream().filter(prerequisite -> prerequisite.getReleasePackageId() .equals(newPrerequisiteReleasePackage.getReleasePackageId())).findFirst();
        if(optionalPrequisiteReleasePackage.isEmpty())
            throw new EntityIdNotFoundException();
        PrerequisiteReleasePackage currentPrerequisiteReleasePackage = optionalPrequisiteReleasePackage.get();

        List<PrerequisiteReleasePackage> impactedPrerequisiteReleasePackages;
        moveForward(prerequisiteReleasePackages, currentPrerequisiteReleasePackage.getSequence() - 1, newPrerequisiteReleasePackage.getSequence() - 1);
        impactedPrerequisiteReleasePackages =  prerequisiteReleasePackages.stream().filter(prequisiteReleasePackage -> prequisiteReleasePackage.getSequence() < newPrerequisiteReleasePackage.getSequence()).collect(Collectors.toList());

        if(isImpactCheckRequired && Objects.nonNull(impactedPrerequisiteReleasePackages) && impactedPrerequisiteReleasePackages.size() > 0)
            return getImpact(id, impactedPrerequisiteReleasePackages);

        if(!isImpactCheckRequired) {
            AtomicInteger idx= new AtomicInteger(1);
            prerequisiteReleasePackages.stream().forEach(prequisiteReleasePackage -> prequisiteReleasePackage.setSequence(idx.getAndIncrement()));
            prerequisiteReleasePackages.remove(currentPrerequisiteReleasePackage);
            return updatePrerequisites(id, prerequisiteReleasePackages);
        }
        return null;
    }


    public BaseEntityList<Overview> getOverview(Long id) {
        String criteria = "(id:" + id + ")";
        String viewCriteria = "";
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE - 1);
        Optional<String> sliceSelect = Optional.empty();
        Slice<BaseView> prerequisitesOverviewList = releasePackageService.getEntitiesFromView(criteria, viewCriteria, pageable, sliceSelect, PrerequisitesOverview.class);
        List<BaseView> prerequisites = new ArrayList<>(prerequisitesOverviewList.getContent());
        prerequisites.sort(Comparator.comparing(item -> ((PrerequisitesOverview)item).getSequenceNumber()));
        return new BaseEntityList(prerequisitesOverviewList, prerequisites);
    }

    public BaseEntityList<Overview> getOverviewResponse(Long id) {
        String criteria = "id:"+id;
        String viewCriteria="";
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE - 1);
        Optional<String> sliceSelect = Optional.empty();
        Slice<BaseView> prerequisitesOverviewList = releasePackageService.getEntitiesFromView(criteria, viewCriteria, pageable, sliceSelect, PrerequisitesOverview.class);
        List<BaseView> prerequisites = new ArrayList<>(prerequisitesOverviewList.getContent());
        prerequisites.sort(Comparator.comparing(item -> ((PrerequisitesOverview)item).getSequenceNumber()));
        return new BaseEntityList(prerequisitesOverviewList);
    }


    public List<String> getPrerequisiteReleasePackageNumbers(Long id) {
        ReleasePackage releasePackage = (ReleasePackage) releasePackageService.getEntityById(id);
        List<PrerequisiteReleasePackage> releasePackagePrerequisitesList = releasePackage.getPrerequisiteReleasePackages();
        List<String> releasePackageNumbers = new ArrayList<>();
        if(Objects.nonNull(releasePackagePrerequisitesList))
            releasePackageNumbers = releasePackagePrerequisitesList.stream().map(item -> item.getReleasePackageNumber()).collect(Collectors.toList());

        String criteria = "prerequisiteReleasePackages.releasePackageId:" + id;
        BaseEntityList baseEntityList = releasePackageService.filter(criteria, PageRequest.of(0, Integer.MAX_VALUE - 1));
        List<String> prerequisiteReleasePackageNumbers = (List<String>) baseEntityList.getResults().stream().map(item -> ((ReleasePackage) item).getReleasePackageNumber()).collect(Collectors.toList());

        return Stream.concat(releasePackageNumbers.stream(), prerequisiteReleasePackageNumbers.stream())
                .collect(Collectors.toList());
    }


    private ReleasePackageReorderPrerequisites getImpact(Long releasePackageId, List<PrerequisiteReleasePackage> impactedPrerequisiteReleasePackages) {
        ArrayList<String> warningMessage = new ArrayList<>();
        ReleasePackageReorderPrerequisites releasePackageReorderPrerequisites = new ReleasePackageReorderPrerequisites();

        impactedPrerequisiteReleasePackages.stream().forEach(prequisite -> {
            ReleasePackage prerequisiteReleasePackage = (ReleasePackage) releasePackageService.getEntityById(prequisite.getReleasePackageId());
            List<PrerequisiteReleasePackage> dependencies = prerequisiteReleasePackage.getPrerequisiteReleasePackages();
            List<String> parentReleasePackageNumbers = getParentReleasePackageOfPrerequisite(prerequisiteReleasePackage.getId(), releasePackageId);

            if ((dependencies != null && !dependencies.isEmpty()) && (parentReleasePackageNumbers != null && !parentReleasePackageNumbers.isEmpty()))
                warningMessage.add("Release Package " + prequisite.getReleasePackageNumber() + " has prerequisites and is a prerequisite in another ReleasePackage");
            else if ((dependencies != null && !dependencies.isEmpty()) && (parentReleasePackageNumbers == null || parentReleasePackageNumbers.isEmpty()))
                warningMessage.add("Release Package " + prequisite.getReleasePackageNumber() + " has prerequisites");
            else if ((dependencies == null || dependencies.isEmpty()) && (parentReleasePackageNumbers != null && !parentReleasePackageNumbers.isEmpty()))
                warningMessage.add("Release Package " + prequisite.getReleasePackageNumber() + " is a prerequisite in another ReleasePackage");
        });
        releasePackageReorderPrerequisites.setWarningMessages(warningMessage);
        return releasePackageReorderPrerequisites;
    }

    private ReleasePackageReorderPrerequisites updatePrerequisites(Long id, List<PrerequisiteReleasePackage> prerequisiteReleasePackages) {
        ReleasePackage updatedReleasePackage = new ReleasePackage();
        updatedReleasePackage.setId(id);
        updatedReleasePackage.setPrerequisiteReleasePackages(prerequisiteReleasePackages);

        Map<String, Object> changedAttributes = new HashMap();
        changedAttributes.put("prerequisite_release_packages", prerequisiteReleasePackages);
        releasePackageService.update(updatedReleasePackage, changedAttributes);
        ReleasePackageReorderPrerequisites releasePackageReorderPrerequisites = new ReleasePackageReorderPrerequisites();
        prerequisiteReleasePackages.sort(Comparator.comparing(item -> item.getSequence()));
       releasePackageReorderPrerequisites.setReleasePackagePrerequisites(getOverviewResponse(id));

        return releasePackageReorderPrerequisites;
    }

    private List<String> getParentReleasePackageOfPrerequisite(final long id, final long parentId) {
        return releasePackageService.getParentReleasePackageIdsOfPrerequisite(id, parentId);
    }


    private static void moveForward(List list, int indexOfElementToMove, int positionToMoveTheElementTo) {
        Collections.rotate(list.subList(indexOfElementToMove, positionToMoveTheElementTo+1), -1);
    }

    private static void moveBackward(List list, int indexOfElementToMove, int positionToMoveTheElementTo ) {
        Collections.rotate(list.subList(positionToMoveTheElementTo, indexOfElementToMove+1), 1);
    }

}

