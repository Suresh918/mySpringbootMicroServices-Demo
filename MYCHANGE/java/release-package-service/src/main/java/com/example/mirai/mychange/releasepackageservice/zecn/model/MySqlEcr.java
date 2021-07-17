package com.example.mirai.projectname.releasepackageservice.zecn.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "MySQL_ecr")
@XmlAccessorType(XmlAccessType.FIELD)
public class MySqlEcr {
    String id;
    String summary="";
    String nc="";
    String ncdescr="";
    String manhrsde="";
    String otherde="";
    String decincog="";
    String nonreccost="";
    String decinct="";
    String decmatus="";
    String toolinvmanf="";
    String indirhrsmanf="";
    String downtimeimpl="";
    String downtimephit="";
    String hitspmonth="";
    String decsparus="";
    String toolinvcs="";
    String indirhrscs="";
    String fcocost="";
    String currprodplan="";
    String relinstbase="";
    String ipresult="";
    String ecrresult="";
    String rfc1="";
    String ecrstate="";
    String implrangegf="";
    String implrangecs="";
    String source="";
    String history="";
    String problem="";
    String concl="";
    String producttype="";
    String productfamily="";
    String subsystem="";
    String pimstate="";
    String remarks="";
    String fc="";
    String fco="";
    String ca="";
}
