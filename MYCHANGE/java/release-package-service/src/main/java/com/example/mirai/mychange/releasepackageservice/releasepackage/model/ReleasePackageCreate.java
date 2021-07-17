package com.example.mirai.projectname.releasepackageservice.releasepackage.model;


import java.util.List;


public class ReleasePackageCreate {
    List<ReleasePackageContext> contexts;

    public List<ReleasePackageContext> getContexts() {
        return contexts;
    }

    public void setContexts(List<ReleasePackageContext> contexts) {
        this.contexts = contexts;
    }
}
