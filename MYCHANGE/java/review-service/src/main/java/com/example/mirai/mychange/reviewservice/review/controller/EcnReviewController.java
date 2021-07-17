package com.example.mirai.projectname.reviewservice.review.controller;

import com.example.mirai.libraries.sapmdg.changerequest.model.DeleteMaterialResponse;
import com.example.mirai.projectname.reviewservice.review.model.dto.ecn.*;
import com.example.mirai.projectname.reviewservice.review.service.EcnReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


@AllArgsConstructor
@RestController
@RequestMapping("/reviews/{id}")
public class EcnReviewController {

    private EcnReviewService ecnReviewService;

    @GetMapping("/solution-items")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewSolutionItem> getSolutionItemList(@PathVariable Long id) {
        return ecnReviewService.getSolutionItemList(id);
    }

    @GetMapping("/bom-structure")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewSolutionItem> getReviewBomStructure(@PathVariable Long id) {
        return ecnReviewService.getReviewBomStructure(id);
    }

    @GetMapping("/tpds")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewTpd> getReviewTpds(@PathVariable Long id) {
        return ecnReviewService.getReviewTpds(id);
    }

    @GetMapping("/materials/delta")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewMaterialDelta> getReviewMaterialDeltaList(@PathVariable Long id) {
        return ecnReviewService.getReviewMaterialsDelta(id);
    }

    @GetMapping("/materials")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewMaterial> getMaterialList(@PathVariable Long id) {
        return ecnReviewService.getReviewMaterials(id);
    }

    @GetMapping("/material-delta")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<MaterialDelta> getMaterialDeltaList(@PathVariable Long id) {
        return ecnReviewService.getMaterialDeltaList(id);
    }

    @GetMapping("/solution-item-summary")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<SolutionItemSummary> getSolutionItemSummaryList(@PathVariable Long id) {
        return ecnReviewService.getSolutionItemSummary(id);
    }

    @DeleteMapping("/change-requests/materials")
    @ResponseBody
    public ResponseEntity<DeleteMaterialResponse> deleteMaterialByMaterialNumber(@PathVariable Long id, @RequestParam(name = "material-ids", defaultValue = "") List<String> materialIds) {
        DeleteMaterialResponse deleteMaterialResponse = ecnReviewService.deleteMaterialByMaterialNumber(id, materialIds);
        if (Objects.isNull(deleteMaterialResponse))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(deleteMaterialResponse);
    }
}
