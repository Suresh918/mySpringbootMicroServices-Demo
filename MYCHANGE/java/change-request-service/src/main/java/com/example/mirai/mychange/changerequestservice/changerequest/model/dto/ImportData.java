package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImportData {
    List<Source> sources;

    @Getter
    @Setter
    public static class Source {
        private String id;
        private ImportAction action;
        private String type;
    }

    public enum ImportAction {
        LINK_ONLY, WRITE_IF_EMPTY, APPEND
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response extends ImportData.Source {
        private Status linkStatus;
        private Status importStatus;

        public Response(ImportData.Source source, Status importStatus, Status linkStatus) {
            this.setId(source.getId());
            this.setAction(source.getAction());
            this.setType(source.getType());
            this.importStatus = importStatus;
            this.linkStatus = linkStatus;
        }
    }
    public enum Status {
        SUCCESS, ERROR, NOT_TRIED
    }
}
