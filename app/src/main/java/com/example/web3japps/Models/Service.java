package com.example.web3japps.Models;

import java.util.ArrayList;
import java.util.HashMap;

public class Service {
    private String serviceId;
    private String serviceTitle;
    private String serviceIntro;
    private String serviceBody;
    private String photo;
    private String dateOfPost;
    private Long date;
    private String price;
    private HashMap<String, String> cidNameMap = new HashMap<>();


    public Service(){};
    public Service(String serviceTitle, String serviceIntro, String photo, String dateOfPost, Long date, String price){
        this.serviceTitle = serviceTitle;
        this.serviceIntro = serviceIntro;
        this.photo = photo;
        this.dateOfPost = dateOfPost;
        this.date = date;
        this.price = price;
    }
    public void setServiceId(String serviceId){this.serviceId = serviceId;}
    public void setServiceTitle(String serviceTitle){this.serviceTitle = serviceTitle;}
    public void setServiceIntro(String serviceIntro){this.serviceIntro = serviceIntro;}
    //public void setServiceBody(String serviceBody){this.serviceBody = serviceBody;}
    public void setPhoto(String photo){this.photo = photo;}
    public void setDateOfPost(String dateOfPost){this.dateOfPost = dateOfPost;}
    public void setDate(Long date){this.date = date;}
    public void setCidNameMap(HashMap<String, String> cidNameMap){this.cidNameMap = cidNameMap;}
    public void setPrice(String price){this.price = price;}
    public String getServiceId(){return serviceId;}
    public String getServiceTitle(){return serviceTitle;}
    public String getServiceIntro(){return serviceIntro;}
    //public String getServiceBody(){return serviceBody;}
    public String getPhoto(){return photo;}
    public String getDateOfPost(){return dateOfPost;}
    public Long getDate(){return date;}
    public HashMap<String, String> getCidNameMap(){return cidNameMap;}
    public String getPrice(){return price;}
}
