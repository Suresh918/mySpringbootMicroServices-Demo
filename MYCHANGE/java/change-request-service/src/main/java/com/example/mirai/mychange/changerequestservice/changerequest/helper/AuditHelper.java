package com.example.mirai.projectname.changerequestservice.changerequest.helper;

import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestContext;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestChangeLogAggregate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AuditHelper {

    public static AggregateInterface handleAuditEntriesForContexts(ChangeRequestChangeLogAggregate changeRequestChangeLogAggregate) {
        List<ChangeLog.Entry> changeRequestChangeLogEntries = (List<ChangeLog.Entry>) ((ArrayList) changeRequestChangeLogAggregate.getChangeRequestChangeLog().getEntries()).clone();
        changeRequestChangeLogEntries.forEach(entry -> {
            if ((entry.getOldValue() != null && Collection.class.isAssignableFrom(entry.getOldValue().getClass())) ||
                    (entry.getValue() != null && Collection.class.isAssignableFrom(entry.getValue().getClass()))) {
                handleAuditEntriesForCollection(entry, changeRequestChangeLogAggregate.getChangeRequestChangeLog());
            }
        });
        return changeRequestChangeLogAggregate;
    }

    private static void handleAuditEntriesForCollection(ChangeLog.Entry entry, ChangeLog changeRequestChangeLog) {
        Collection value = (Collection) entry.getValue();
        Collection oldValue = (Collection) entry.getOldValue();
        if (Objects.nonNull(value)) {
            value.stream().forEach(item -> {
                if (item instanceof ChangeRequestContext) {
                    List<ChangeRequestContext> changeRequestOldContexts = (oldValue != null) ? (List<ChangeRequestContext>) oldValue.stream().filter((oldItem) -> oldItem.equals(item)).collect(Collectors.toList()) : null;
                    String entryValue = ((ChangeRequestContext) item).getContextId() + " - " + ((ChangeRequestContext) item).getName();
                    String entryOldValue = "";
                    if (changeRequestOldContexts != null && changeRequestOldContexts.size() > 0 && item.equals(changeRequestOldContexts.get(0))) {
                        entryOldValue = changeRequestOldContexts.get(0).getContextId() + " - " + changeRequestOldContexts.get(0).getName();
                    }
                    String property = entry.getProperty() + "." + ((ChangeRequestContext) item).getType().toLowerCase();
                    changeRequestChangeLog.addEntry(entry.getUpdater(), entry.getUpdatedOn(), entry.getRevision(), entry.getRevisionType(), property, entryValue, entryOldValue, entry.getId());
                }
            });
        }
        changeRequestChangeLog.getEntries().remove(entry);
    }
}
