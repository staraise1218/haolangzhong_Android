package com.zhongyi.doctor.bean.ask;

public class RegisterBean {
    private String mobile;
    private String  password;
    private String confirm;
    private String code;
    private String type;

    public RegisterBean(String mobile, String password, String confirm, String code, String type) {
        this.mobile = mobile;
        this.password = password;
        this.confirm = confirm;
        this.code = code;
        this.type = type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
