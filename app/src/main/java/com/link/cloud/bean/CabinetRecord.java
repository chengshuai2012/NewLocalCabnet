package com.link.cloud.bean;


import io.realm.RealmObject;


public class CabinetRecord extends RealmObject {
    String memberName;
    String phoneNum;
    String cabinetStating;
    String opentime;
    String cabinetNumber;

    public long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(long creatTime) {
        this.creatTime = creatTime;
    }

    long creatTime;

    public CabinetRecord() {
    }
    public void setCabinetNumber(String cabinetNumber) {
        this.cabinetNumber = cabinetNumber;
    }

    public void setCabinetStating(String cabinetStating) {
        this.cabinetStating = cabinetStating;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setOpentime(String opentime) {
        this.opentime = opentime;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getCabinetNumber() {
        return cabinetNumber;
    }

    public String getCabinetStating() {
        return cabinetStating;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getOpentime() {
        return opentime;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

}
