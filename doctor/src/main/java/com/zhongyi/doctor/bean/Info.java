package com.zhongyi.doctor.bean;

import java.util.List;

public class Info {
    private List<DoctorLabel> doctorLabel;
    private List<DoctorPic> doctorPic;
    private DoctorInfo doctorInfo;

    public List<DoctorLabel> getDoctorLabel() {
        return doctorLabel;
    }

    public void setDoctorLabel(List<DoctorLabel> doctorLabel) {
        this.doctorLabel = doctorLabel;
    }

    public List<DoctorPic> getDoctorPic() {
        return doctorPic;
    }

    public void setDoctorPic(List<DoctorPic> doctorPic) {
        this.doctorPic = doctorPic;
    }

    public DoctorInfo getDoctorInfo() {
        return doctorInfo;
    }

    public void setDoctorInfo(DoctorInfo doctorInfo) {
        this.doctorInfo = doctorInfo;
    }
}
