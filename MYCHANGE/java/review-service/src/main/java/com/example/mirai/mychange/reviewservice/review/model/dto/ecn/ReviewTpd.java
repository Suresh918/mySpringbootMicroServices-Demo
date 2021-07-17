package com.example.mirai.projectname.reviewservice.review.model.dto.ecn;

//import com.example.mirai.libraries.teamcenter.ecn.model.Tpd;
import com.example.mirai.libraries.deltareport.model.dto.Tpd;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewTpd extends Tpd {
    Integer defectCount;

    public static ReviewTpd copy(Tpd sourceTpd, ReviewTpd targetTpd) {
        targetTpd.setDescription(sourceTpd.getDescription());
        targetTpd.setDocumentLink(sourceTpd.getDocumentLink());
        targetTpd.setDocumentPart(sourceTpd.getDocumentPart());
        targetTpd.setDocumentStatus(sourceTpd.getDocumentStatus());
        targetTpd.setDocumentType(sourceTpd.getDocumentType());
        targetTpd.setDocumentVersion(sourceTpd.getDocumentVersion());
        targetTpd.setId(sourceTpd.getId());
        targetTpd.setName(sourceTpd.getName());
        targetTpd.setNewChangeNumber(sourceTpd.getNewChangeNumber());
        targetTpd.setRevision(sourceTpd.getRevision());
        targetTpd.setUid(sourceTpd.getUid());
        targetTpd.setCompleteTpdId(sourceTpd.getCompleteTpdId());
        return targetTpd;
    }
}
