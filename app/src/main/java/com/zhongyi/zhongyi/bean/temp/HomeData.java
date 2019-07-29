package com.zhongyi.zhongyi.bean.temp;

import com.zhongyi.zhongyi.bean.Banner;
import com.zhongyi.zhongyi.bean.Doctor;
import com.zhongyi.zhongyi.bean.Drug;

import java.util.List;

public class HomeData {
    private List<Banner> banner;
    private List<Doctor> doctor;
    private List<Drug> airDrug;

    public List<Banner> getBanner() {
        return banner;
    }

    public void setBanner(List<Banner> banner) {
        this.banner = banner;
    }

    public List<Doctor> getDoctor() {
        return doctor;
    }

    public void setDoctor(List<Doctor> doctor) {
        this.doctor = doctor;
    }

    public List<Drug> getAirDrug() {
        return airDrug;
    }

    public void setAirDrug(List<Drug> airDrug) {
        this.airDrug = airDrug;
    }
}
