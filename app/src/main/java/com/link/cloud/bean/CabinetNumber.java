package com.link.cloud.bean;


import io.realm.RealmObject;

/**
 * Created by 30541 on 2018/3/29.
 */

public class CabinetNumber extends RealmObject {

    String cabinetLockPlate;
    String circuitNumber;
    String cabinetNumber;
    String isUser;
    String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    String passWord;

    public CabinetNumber() {
    }
    public String getCabinetLockPlate() {
        return this.cabinetLockPlate;
    }
    public void setCabinetLockPlate(String cabinetLockPlate) {
        this.cabinetLockPlate = cabinetLockPlate;
    }
    public String getCircuitNumber() {
        return this.circuitNumber;
    }
    public void setCircuitNumber(String circuitNumber) {
        this.circuitNumber = circuitNumber;
    }
    public String getCabinetNumber() {
        return this.cabinetNumber;
    }
    public void setCabinetNumber(String cabinetNumber) {
        this.cabinetNumber = cabinetNumber;
    }
    public String getIsUser() {
        return this.isUser;
    }
    public void setIsUser(String isUser) {
        this.isUser = isUser;
    }
}
