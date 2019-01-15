package com.link.cloud.bean;

import java.io.Serializable;

import io.realm.RealmObject;


public class CabinetInfo extends RealmObject implements Serializable {
    /**
     * id : 152
     * lockId : 53
     * deviceId : 5
     * lineNo : 2
     * cabinetNo : 2
     * locked : false
     * uuid :
     * nickname :
     * phone :
     * startTime : null
     * endTime : null
     * vip : false
     * lockNo : 1
     * passwd : null
     */

    private int id;
    private int lockId;
    private int deviceId;
    private int lineNo;
    private String cabinetNo;
    private boolean locked;
    private String uuid;
    private String nickname;
    private String phone;
    private String startTime;
    private String endTime;
    private boolean vip;
    private int lockNo;
    private String passwd;
    private String openWay;


    public String getOpenWay() {
        return openWay;
    }

    public void setOpenWay(String openWay) {
        this.openWay = openWay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLockId() {
        return lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public String getCabinetNo() {
        return cabinetNo;
    }

    public void setCabinetNo(String cabinetNo) {
        this.cabinetNo = cabinetNo;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public int getLockNo() {
        return lockNo;
    }

    public void setLockNo(int lockNo) {
        this.lockNo = lockNo;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }


}
