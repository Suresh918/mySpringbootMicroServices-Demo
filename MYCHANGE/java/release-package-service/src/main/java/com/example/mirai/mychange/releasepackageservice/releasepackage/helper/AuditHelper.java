package com.example.mirai.projectname.releasepackageservice.releasepackage.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageContext;

public class AuditHelper {

    public static ChangeLog handleAuditEntriesForContexts(ChangeLog releasePackageChangeLog) {
        List<ChangeLog.Entry> changeRequestChangeLogEntries = (List<ChangeLog.Entry>) ((ArrayList) releasePackageChangeLog.getEntries()).clone();
        changeRequestChangeLogEntries.forEach(entry -> {
            if ((entry.getOldValue() != null && Collection.class.isAssignableFrom(entry.getOldValue().getClass())) ||
                    (entry.getValue() != null && Collection.class.isAssignableFrom(entry.getValue().getClass()))) {
                handleAuditEntriesForCollection(entry, releasePackageChangeLog);
            }
        });
        return releasePackageChangeLog;
    }

    private static void handleAuditEntriesForCollection(ChangeLog.Entry entry, ChangeLog releasePackageChangeLog) {
        Collection value = (Collection) entry.getValue();
        Collection oldValue = (Collection) entry.getOldValue();
        if (Objects.nonNull(value)) {
            value.stream().forEach(item -> {
                if (item instanceof ReleasePackageContext) {
                    List<ReleasePackageContext> releasePackageOldContexts = (oldValue != null) ? (List<ReleasePackageContext>) oldValue.stream().filter((oldItem) -> oldItem.equals(item)).collect(Collectors.toList()) : null;
                    String entryValue = ((ReleasePackageContext) item).getContextId() + " - " + ((ReleasePackageContext) item).getName();
                    String entryOldValue = "";
                    if (releasePackageOldContexts != null && releasePackageOldContexts.size() > 0 && item.equals(releasePackageOldContexts.get(0))) {
                        entryOldValue = releasePackageOldContexts.get(0).getContextId() + " - " + releasePackageOldContexts.get(0).getName();
                    }
                    String property = entry.getProperty() + "." + ((ReleasePackageContext) item).getType().toLowerCase();
                    releasePackageChangeLog.addEntry(entry.getUpdater(), entry.getUpdatedOn(), entry.getRevision(), entry.getRevisionType(), property, entryValue, entryOldValue, entry.getId());
                }
            });
        }
        releasePackageChangeLog.getEntries().remove(entry);
    }
}
