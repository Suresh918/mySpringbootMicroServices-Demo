package com.example.mirai.projectname.changerequestservice.changerequest.helper;

import com.example.mirai.libraries.air.problem.model.Problem;
import com.example.mirai.libraries.air.problem.service.ProblemService;
import com.example.mirai.libraries.air.problem.shared.exception.AirException;
import com.example.mirai.libraries.cerberus.productbrakedownstructure.model.ProductBreakdownStructure;
import com.example.mirai.libraries.cerberus.productbrakedownstructure.service.ProductBreakdownStructureService;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestContext;
import com.example.mirai.projectname.changerequestservice.changerequest.model.dto.ImportData;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import com.example.mirai.projectname.changerequestservice.shared.util.Defaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AirPbsImportHelper {
    @Autowired
    ChangeRequestService changeRequestService;
    private final ProblemService problemService;
    private final ProductBreakdownStructureService productBreakdownStructureService;

    public AirPbsImportHelper(ProblemService problemService, ProductBreakdownStructureService productBreakdownStructureService) {
        this.problemService = problemService;
        this.productBreakdownStructureService = productBreakdownStructureService;
    }

    public List<ImportData.Response> linkAirItems(Long changeRequestId, ImportData importData) {
        ChangeRequest changeRequest = (ChangeRequest) changeRequestService.getEntityById(changeRequestId);
        ChangeRequest updatedchangeRequest = null;
        List<ImportData.Response> importResponses = new ArrayList<>();
        List<String> airIds = new ArrayList<>();
        boolean isLinkSuccess;
        importData.getSources().forEach(importItem -> {
            airIds.add(importItem.getId());
        });
        try {
            problemService.updateProblem(changeRequestId, changeRequest.getStatus().toString(), changeRequest.getTitle(), airIds);
            isLinkSuccess = true;
        } catch (AirException e) {
            isLinkSuccess = false;
        } catch (Exception e) {
            isLinkSuccess = false;
        }
        boolean finalIsLinkSuccess = isLinkSuccess;
        HashMap changedAttrs = new HashMap();
        importData.getSources().forEach(importItem -> {
            if (importItem.getAction().equals(ImportData.ImportAction.LINK_ONLY)) {
                importResponses.add(new ImportData.Response(importItem, ImportData.Status.NOT_TRIED, finalIsLinkSuccess ? ImportData.Status.SUCCESS : ImportData.Status.ERROR));
            }
            this.handleAirImportDataForItem(importItem, changeRequest, changedAttrs);
        });
        try {
            updatedchangeRequest = (ChangeRequest) changeRequestService.update(changeRequest, changedAttrs);
            importData.getSources().forEach(importItem -> {
                if (importResponses.stream().filter(responseItem -> responseItem.getId().equals(importItem.getId())).findFirst().isEmpty()) {
                    importResponses.add(new ImportData.Response(importItem, ImportData.Status.SUCCESS, ImportData.Status.SUCCESS));
                }
            });
        } catch (Exception e) {
            importData.getSources().forEach(importItem -> {
                if (importResponses.stream().filter(responseItem -> responseItem.getId().equals(importItem.getId())).findFirst().isEmpty()) {
                    importResponses.add(new ImportData.Response(importItem, ImportData.Status.ERROR, ImportData.Status.SUCCESS));
                }
            });
        }
        if (Objects.nonNull(updatedchangeRequest))
            changeRequestService.addProjectLeadToMyTeam(changeRequest);
        return importResponses;
    }

    public void handleAirImportDataForItem(ImportData.Source source, ChangeRequest changeRequest, HashMap changedAttrs) {
        Problem problem = problemService.getProblemByNumber(source.getId());
        if (Objects.isNull(problem)) {
            return;
        }
        String contextDescription = problem.getShortDescription().length() > 255 ? problem.getShortDescription().substring(0, 255) : problem.getShortDescription();
        checkAndAddContext(changeRequest, new ChangeRequestContext(source.getType(), problem.getNumber(),
                contextDescription, null), changedAttrs);
        if (source.getAction().equals(ImportData.ImportAction.APPEND)
                || source.getAction().equals(ImportData.ImportAction.WRITE_IF_EMPTY)) {
            if (changeRequest.getTitle().equals(Defaults.TITLE)) {
                changeRequest.setTitle(source.getType() + " " + source.getId() + " " + changeRequest.getTitle());
                changedAttrs.put("title", changeRequest.getTitle());
            }
            if ((Objects.isNull(changeRequest.getIssueTypes()) || changeRequest.getIssueTypes().size() == 0) && Objects.nonNull(problem.getIssueType())) {
                List<String> issueTypes = new ArrayList<>();
                issueTypes.add(problem.getIssueType());
                changeRequest.setIssueTypes(issueTypes);
                changedAttrs.put("issue_types", changeRequest.getIssueTypes());
            }
            if (Objects.isNull(changeRequest.getFunctionalClusterId()) && Objects.nonNull(problem.getFunctionalClusterId())) {
                changeRequest.setFunctionalClusterId(problem.getFunctionalClusterId());
                changedAttrs.put("functional_cluster_id", changeRequest.getFunctionalClusterId());
            }
            if (Objects.isNull(changeRequest.getProductId()) && Objects.nonNull(problem.getProductId())) {
                changeRequest.setProductId(problem.getProductId());
                changedAttrs.put("product_id", changeRequest.getProductId());
            }
            if (Objects.isNull(changeRequest.getProjectId()) && Objects.nonNull(problem.getProjectId())) {
                changeRequest.setProjectId(problem.getProjectId());
                changedAttrs.put("project_id", changeRequest.getProjectId());
            }
        }
        if (source.getAction().equals(ImportData.ImportAction.APPEND)) {
            if (Objects.nonNull(problem.getDescription())) {
                String updatedProblemDescription;
                if (Objects.isNull(changeRequest.getProblemDescription()))
                    updatedProblemDescription = "*" + source.getType() + " " + source.getId() + "* " + problem.getDescription();
                else
                    updatedProblemDescription = changeRequest.getProblemDescription() + "\n*" + source.getType() + " " + source.getId() + "* " + problem.getDescription();
                changeRequest.setProblemDescription(updatedProblemDescription);
                changedAttrs.put("problem_description", changeRequest.getProblemDescription());
            }
            if (Objects.nonNull(problem.getSolutionDescription())) {
                String updatedProposedSolution;
                if (Objects.isNull(changeRequest.getProposedSolution()))
                    updatedProposedSolution = "*" + source.getType() + " " + source.getId() + "* " + problem.getSolutionDescription();
                else
                    updatedProposedSolution = changeRequest.getProposedSolution() + "\n*" + source.getType() + " " + source.getId() + "* " + problem.getSolutionDescription();
                changeRequest.setProposedSolution(updatedProposedSolution);
                changedAttrs.put("proposed_solution", changeRequest.getProposedSolution());
            }
            if (Objects.nonNull(problem.getRootCause()) && Objects.isNull(changeRequest.getRootCause())) {
                changeRequest.setRootCause(problem.getRootCause());
                changedAttrs.put("root_cause", changeRequest.getRootCause());
            }
        }
        if (source.getAction().equals(ImportData.ImportAction.WRITE_IF_EMPTY)) {
            if (Objects.isNull(changeRequest.getProblemDescription()) && Objects.nonNull(problem.getDescription())) {
                changeRequest.setProblemDescription("*" + source.getType() + " " + source.getId() + "* " + problem.getDescription());
                changedAttrs.put("problem_description", changeRequest.getProblemDescription());
            }
            if (Objects.isNull(changeRequest.getProposedSolution()) && Objects.nonNull(problem.getSolutionDescription())) {
                changeRequest.setProposedSolution("*" + source.getType() + " " + source.getId() + "* " + problem.getSolutionDescription());
                changedAttrs.put("proposed_solution", changeRequest.getProposedSolution());
            }
            if (Objects.nonNull(problem.getRootCause()) && Objects.isNull(changeRequest.getRootCause())) {
                changeRequest.setRootCause(problem.getRootCause());
                changedAttrs.put("root_cause", changeRequest.getRootCause());
            }
        }
    }


    public List<ImportData.Response> linkPbsItems(Long id, ImportData importData) {
        ChangeRequest changeRequest = (ChangeRequest) changeRequestService.getEntityById(id);
        ChangeRequest updatedchangeRequest = null;
        HashMap<String, Object> changedAttrs = new HashMap<>();
        boolean isLinkSuccess = handlePbsImportDataForItem(importData.getSources().get(0), changeRequest, changedAttrs);
        ImportData.Status dataImportStatus;
        if (!importData.getSources().get(0).getAction().equals(ImportData.ImportAction.LINK_ONLY)) {
            try {
                updatedchangeRequest = (ChangeRequest) changeRequestService.update(changeRequest, changedAttrs);
                dataImportStatus = ImportData.Status.SUCCESS;
            } catch (Exception e) {
                dataImportStatus = ImportData.Status.ERROR;
            }
        } else {
            updatedchangeRequest = (ChangeRequest) changeRequestService.update(changeRequest, changedAttrs);
            dataImportStatus = ImportData.Status.NOT_TRIED;
        }
        List<ImportData.Response> importResponse = new ArrayList<>();
        importResponse.add(new ImportData.Response(importData.getSources().get(0), dataImportStatus, isLinkSuccess ? ImportData.Status.SUCCESS : ImportData.Status.ERROR));
        if (Objects.nonNull(updatedchangeRequest))
            changeRequestService.addProjectLeadToMyTeam(updatedchangeRequest);
        return importResponse;
    }

    public boolean handlePbsImportDataForItem(ImportData.Source source, ChangeRequest changeRequest, HashMap changedAttrs) {
        ProductBreakdownStructure productBreakdownStructure = productBreakdownStructureService.getProductBreakdownStructureById(source.getId());
        try {
            productBreakdownStructureService.updateProductBreakdownStructure(changeRequest.getId(), source.getId());
        } catch (Exception e) {
            log.error("IMPORT_PBS_CERBERUS_UPDATE_FAILED");
            return false;
        }
        String contextDescription = (Objects.nonNull(productBreakdownStructure.getDescription()) && productBreakdownStructure.getDescription().length() > 255) ? productBreakdownStructure.getDescription().substring(0, 255) : productBreakdownStructure.getDescription();
        checkAndAddContext(changeRequest, new ChangeRequestContext(source.getType(), productBreakdownStructure.getId(),
                contextDescription, productBreakdownStructure.getStatus()), changedAttrs);
        if (source.getAction().equals(ImportData.ImportAction.APPEND) || source.getAction().equals(ImportData.ImportAction.WRITE_IF_EMPTY)) {
            if (Objects.nonNull(changeRequest.getTitle()) || changeRequest.getTitle().equals(Defaults.TITLE)) {
                if (Objects.nonNull(productBreakdownStructure.getDeliverable())) {
                    String title = source.getType() + " " + source.getId() + " ";
                    if (productBreakdownStructure.getDeliverable().length() > 240) {
                        title = title + productBreakdownStructure.getDeliverable().substring(0, 240);
                    } else {
                        title = title + productBreakdownStructure.getDeliverable();
                    }
                    changeRequest.setTitle(title);
                } else {
                    changeRequest.setTitle(source.getType() + " " + source.getId());
                }
                changedAttrs.put("title", changeRequest.getTitle());
            }
            if ((Objects.isNull(changeRequest.getIssueTypes()) || changeRequest.getIssueTypes().size() == 0) && Objects.nonNull(productBreakdownStructure.getType())) {
                changeRequest.setIssueTypes(productBreakdownStructure.getType());
                changedAttrs.put("issue_types", changeRequest.getIssueTypes());
            }
            if (Objects.isNull(changeRequest.getFunctionalClusterId()) && Objects.nonNull(productBreakdownStructure.getFunctionalClusterId())) {
                changeRequest.setFunctionalClusterId(productBreakdownStructure.getFunctionalClusterId());
                changedAttrs.put("functional_cluster_id", changeRequest.getFunctionalClusterId());
            }
            if (Objects.isNull(changeRequest.getProductId()) && Objects.nonNull(productBreakdownStructure.getProductId())) {
                changeRequest.setProductId(productBreakdownStructure.getProductId());
                changedAttrs.put("product_id", changeRequest.getProductId());
            }
            if (Objects.isNull(changeRequest.getProjectId()) && Objects.nonNull(productBreakdownStructure.getProjectId())) {
                changeRequest.setProjectId(productBreakdownStructure.getProjectId());
                changedAttrs.put("project_id", changeRequest.getProjectId());
            }
        }
        if (source.getAction().equals(ImportData.ImportAction.APPEND)) {
            if (Objects.nonNull(productBreakdownStructure.getDescription())) {
                if (Objects.isNull(changeRequest.getProblemDescription())) {
                    changeRequest.setProblemDescription("*" + source.getType() + " " + source.getId() + "* " + productBreakdownStructure.getDescription());
                } else {
                    changeRequest.setProblemDescription(changeRequest.getProblemDescription() + "\n*" + source.getType() + " " + source.getId() + "* " + productBreakdownStructure.getDescription());
                }
                changedAttrs.put("problem_description", changeRequest.getProblemDescription());
            }
            if (Objects.nonNull(productBreakdownStructure.getDeliverable())) {
                if (Objects.isNull(changeRequest.getProposedSolution())) {
                    changeRequest.setProposedSolution("*" + source.getType() + " " + source.getId() + "* " + productBreakdownStructure.getDeliverable());
                } else {
                    changeRequest.setProposedSolution(changeRequest.getProposedSolution() + "\n*" + source.getType() + " " + source.getId() + "* " + productBreakdownStructure.getDeliverable());
                }
                changedAttrs.put("proposed_solution", changeRequest.getProposedSolution());
            }
        }
        if (source.getAction().equals(ImportData.ImportAction.WRITE_IF_EMPTY)) {
            if (Objects.nonNull(productBreakdownStructure.getDescription())) {
                if (Objects.isNull(changeRequest.getProblemDescription())) {
                    changeRequest.setProblemDescription("*" + source.getType() + source.getId() + "* " + productBreakdownStructure.getDescription());
                    changedAttrs.put("problem_description", changeRequest.getProblemDescription());
                }
            }
            if (Objects.nonNull(productBreakdownStructure.getDeliverable())) {
                if (Objects.isNull(changeRequest.getProposedSolution())) {
                    changeRequest.setProposedSolution("*" + source.getType() + source.getId() + "* " + productBreakdownStructure.getDeliverable());
                    changedAttrs.put("proposed_solution", changeRequest.getProposedSolution());
                }
            }
        }
        return true;
    }

    public void checkAndAddContext(ChangeRequest changeRequest, ChangeRequestContext changeRequestContext, Map changedAttrs) {
        List<ChangeRequestContext> contexts = changeRequest.getContexts();
        if (Objects.isNull(contexts)) {
            contexts = new ArrayList<>();
        }
        Optional<ChangeRequestContext> matchedContext = contexts.stream().filter(context -> context.getContextId().equals(changeRequestContext.getContextId())
                && context.getType().equals(changeRequestContext.getType())).findFirst();
        if (matchedContext.isEmpty()) {
            contexts.add(changeRequestContext);
        }
        changeRequest.setContexts(contexts);
        changedAttrs.put("contexts", changeRequest.getContexts());
    }

}
