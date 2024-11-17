package com.example.camera_app.Service;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.InputStream;
import java.util.List;

public class ServiceAPIResponse {

    //user이름
    @SerializedName("name")
    private String name;

    //성공 여부
    @SerializedName("success")
    private boolean success;

    //파일 이름
    @SerializedName("filename")
    private String filename;

    //파일 경로
    @SerializedName("destination")
    private String destination;

    //이미지
    @SerializedName("image64")
    private String image;

    @SerializedName("company")
    private String company;

    @SerializedName("classification_name")
    private String classification_name;

    //효능효과
    @SerializedName("EE")
    private String EE;

    //용법용량
    @SerializedName("UD")
    private String UD;

    //주의사항
    @SerializedName("NB")
    private String NB;



//-------------------------------------------



    public String getName() {
        return name;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getFileName() {
        return filename;
    }

    public String getDestination() {
        return destination;
    }

    public String getImage() {
        return image;
    }

    public String getCompany() {
        return company;
    }

    public String getClassification_name() {
        return classification_name;
    }

    public String getEE() {
        return EE;
    }

    public String getUD() {
        return UD;
    }

    public String getNB() {
        return NB;
    }

}
