package com.example.camera_app.Service;

import android.content.Intent;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ListViewItem extends AppCompatActivity {

    private String serial_number ;
    private Bitmap image ;
    private String item_name ;
    private String  company;
    private String classification;
    private String ee;
    private String ud;
    private String nb;

    //------------------------------------Getter---------------------------

    public String getSerial_Number() {
        return serial_number;
    }
    public Bitmap getImage() {
        return image;
    }
    public String getItem_Name() {
        return item_name;
    }
    public String getCompany() {
        return company;
    }
    public String getClassification() {
        return classification;
    }
    public String getEE() {
        return ee;
    }

    public String getUD() {
        return ud;
    }

    public String getNB() {
        return nb;
    }

    //------------------------------------Setter---------------------------

    public void setSerial_Number(String serial_number) {
        this.serial_number = serial_number;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
    public void setItem_Name(String item_name) {
        this.item_name = item_name;
    }
    public void setCompany(String company) {
        this.company = company;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }
    public void setEE(String ee) {
        this.ee = ee;
    }
    public void setUD(String ud) {
        this.ud = ud;
    }
    public void setNB(String nb) {
        this.nb = nb;
    }


}
