package com.example.mirai.projectname.releasepackageservice.releasepackage.model;

public enum ReleasePackageCaseActions {
    SUBMIT,
    SUBMIT_AGGREGATE,
    CREATE,
    READY,
    RELEASE,
    CLOSE,
    OBSOLETE,
    UPDATE,
    MERGE,
    CREATE_ACTION,

    ADD_PREREQUISITE,
    REMOVE_PREREQUISITE,

    READ,
    FETCH,

    REREADY,
    RECREATE,

    CREATE_COMMENT,
    CREATE_REVIEW,


    ///move to release package my team
    ADD_MY_TEAM_MEMBER,
    REMOVE_MY_TEAM_MEMBER,
    ADD_ROLE_TO_MEMBER,
    REMOVE_ROLE_FROM_MEMBER
}
