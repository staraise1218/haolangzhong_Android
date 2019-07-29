package com.zhongyi.zhongyi.bean.ask;

public class BindBean {
    private String third_id;
    private String third_type;
    private String user_type;
    private String mobile;
    private String code;

    public BindBean(String third_id, String third_type, String user_type, String mobile, String code) {
        this.third_id = third_id;
        this.third_type = third_type;
        this.user_type = user_type;
        this.mobile = mobile;
        this.code = code;
    }

    public String getThird_id() {
        return third_id;
    }

    public void setThird_id(String third_id) {
        this.third_id = third_id;
    }

    public String getThird_type() {
        return third_type;
    }

    public void setThird_type(String third_type) {
        this.third_type = third_type;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
