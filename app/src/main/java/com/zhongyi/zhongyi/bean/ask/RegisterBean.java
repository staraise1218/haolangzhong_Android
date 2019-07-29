package com.zhongyi.zhongyi.bean.ask;

public class RegisterBean {
    private String mobile;
    private String  password;
    private String confirm;
    private String code;
    private String type;
    private String shareCode;

    public RegisterBean(String mobile, String password, String confirm, String code, String type,String shareCode) {
        this.mobile = mobile;
        this.password = password;
        this.confirm = confirm;
        this.code = code;
        this.type = type;
        this.shareCode = shareCode;
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

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }
}
