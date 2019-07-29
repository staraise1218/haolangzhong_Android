package com.zhongyi.zhongyi.bean;
public class User {
    private String regtime;
    private String islock;
    private String del_flag;
    private String password;
    private String mobile;
    private String id;
    private String type;
    private long create_date;
    private String isauthentication;
    private String token;
    private String is_invite;
    private String lastlogin;
    private String icon;
    private String nike_name;
    private String third_id;
    private String third_type;
    private String create_by;
    private String member_lv;
    private Share share;


    public String getRegtime() {
        return regtime;
    }

    public void setRegtime(String regtime) {
        this.regtime = regtime;
    }

    public String getIslock() {
        return islock;
    }

    public void setIslock(String islock) {
        this.islock = islock;
    }

    public String getDel_flag() {
        return del_flag;
    }

    public void setDel_flag(String del_flag) {
        this.del_flag = del_flag;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCreate_date() {
        return create_date;
    }

    public void setCreate_date(long create_date) {
        this.create_date = create_date;
    }

    public String getIsauthentication() {
        return isauthentication;
    }

    public void setIsauthentication(String isauthentication) {
        this.isauthentication = isauthentication;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIs_invite() {
        return is_invite;
    }

    public void setIs_invite(String is_invite) {
        this.is_invite = is_invite;
    }

    public String getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(String lastlogin) {
        this.lastlogin = lastlogin;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNike_name() {
        return nike_name;
    }

    public void setNike_name(String nike_name) {
        this.nike_name = nike_name;
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

    public String getCreate_by() {
        return create_by;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public String getMember_lv() {
        return member_lv;
    }

    public void setMember_lv(String member_lv) {
        this.member_lv = member_lv;
    }

    public Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        this.share = share;
    }

    @Override
    public String toString() {
        return "User{" +
                "regtime='" + regtime + '\'' +
                ", islock='" + islock + '\'' +
                ", del_flag='" + del_flag + '\'' +
                ", password='" + password + '\'' +
                ", mobile='" + mobile + '\'' +
                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", create_date=" + create_date +
                ", isauthentication='" + isauthentication + '\'' +
                ", token='" + token + '\'' +
                ", is_invite='" + is_invite + '\'' +
                ", lastlogin='" + lastlogin + '\'' +
                ", icon='" + icon + '\'' +
                ", nike_name='" + nike_name + '\'' +
                ", third_id='" + third_id + '\'' +
                ", third_type='" + third_type + '\'' +
                ", create_by='" + create_by + '\'' +
                ", member_lv='" + member_lv + '\'' +
                ", share=" + share +
                '}';
    }
}
