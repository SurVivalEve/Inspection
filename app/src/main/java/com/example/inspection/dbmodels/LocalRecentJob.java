package com.example.inspection.dbmodels;

import java.io.Serializable;

/**
 * Created by Sur.Vival on 12/5/2016.
 */
public class LocalRecentJob implements Serializable {
    private long id;
    private String title;
    private String remark;
    private String tstatus;
    private String phone;
    private String fullname;
    private String flatBlock;
    private String building;
    private String districtEN;
    private String island;
    private String which;
    private String appid;
    private String apptime;
    private String email;

    public LocalRecentJob() {
    }

    public LocalRecentJob(String title, String remark, String tstatus, String phone, String fullname, String flatBlock, String building, String districtEN, String island, String which, String appid, String apptime, String email) {
        this.title = title;
        this.remark = remark;
        this.tstatus = tstatus;
        this.phone = phone;
        this.fullname = fullname;
        this.flatBlock = flatBlock;
        this.building = building;
        this.districtEN = districtEN;
        this.island = island;
        this.which = which;
        this.appid = appid;
        this.apptime = apptime;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTstatus() {
        return tstatus;
    }

    public void setTstatus(String tstatus) {
        this.tstatus = tstatus;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getFlatBlock() {
        return flatBlock;
    }

    public void setFlatBlock(String flatBlock) {
        this.flatBlock = flatBlock;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getDistrictEN() {
        return districtEN;
    }

    public void setDistrictEN(String districtEN) {
        this.districtEN = districtEN;
    }

    public String getIsland() {
        return island;
    }

    public void setIsland(String island) {
        this.island = island;
    }

    public String getWhich() {
        return which;
    }

    public void setWhich(String which) {
        this.which = which;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getApptime() {
        return apptime;
    }

    public void setApptime(String apptime) {
        this.apptime = apptime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
