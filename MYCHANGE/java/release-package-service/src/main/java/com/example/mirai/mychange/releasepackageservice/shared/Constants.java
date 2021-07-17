package com.example.mirai.projectname.releasepackageservice.shared;

import com.google.common.collect.ImmutableList;

public final class Constants {

    public static final int NEW_SEQUENCE = 1;
    public static final String ECN_STATUS_Open="Open";
    public static final String ECN_STATUS_Cancelled="Cancelled";
    public static final String ECN_STATUS_OPEN="OPEN";
    public static final String ECN_STATUS_CANCELLED="CANCELLED";
    public static final ImmutableList<String> EMAIL_ID_LIST =
            ImmutableList.of("q05test@example.qas");///TODO add H,Sagar and M ids for final commit to Prod
    public static final String ER_STATUS="60";
    public static final String EC_STATUS="CLSD";
    public static final String PUBLISH_CASEACTION="PUBLISH";
    public static final String START_CASEACTION="START";
    public static final String CHANGE_OBJECT="CHANGEOBJECT";
    public static final String CHANGEOBJECT_STATUS_CLOSED="CLOSED";
    public static final String CHANGEOBJECT_STATUS_CREATED="CREATED";
    public static final String CHANGEOBJECT_STATUS_NEW="NEW";
    public static final String CREATE ="CREATE";
    public static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    //ChangeObject Statuses
    public static final String CHANGE_OBJECT_STATUS_NEW = "1";
    public static final String CHANGE_OBJECT_STATUS_CREATED = "2";
    public static final String CHANGE_OBJECT_STATUS_VALIDATED = "3";
    public static final String CHANGE_OBJECT_STATUS_RELEASED = "4";
    public static final String CHANGE_OBJECT_STATUS_PUBLICATION_PENDING = "5";
    public static final String CHANGE_OBJECT_STATUS_CLOSED = "6";

}
