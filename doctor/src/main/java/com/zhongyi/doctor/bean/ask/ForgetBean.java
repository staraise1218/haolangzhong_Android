package com.zhongyi.doctor.bean.ask;

public class ForgetBean {
    private String mobile;
    private String  newpass;
    private String confirm;
    private String code;

    public ForgetBean(String mobile, String newpass, String confirm, String code) {
        this.mobile = mobile;
        this.newpass = newpass;
        this.confirm = confirm;
        this.code = code;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNewpass() {
        return newpass;
    }

    public void setNewpass(String newpass) {
        this.newpass = newpass;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
