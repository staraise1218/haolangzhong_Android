package com.zhongyi.doctor.bean;
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
    private String adress;

    private Doctor doctorInfo;
    private DoctorPic doctorPic;
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

    public Doctor getDoctorInfo() {
        return doctorInfo;
    }

    public void setDoctorInfo(Doctor doctorInfo) {
        this.doctorInfo = doctorInfo;
    }

    public DoctorPic getDoctorPic() {
        return doctorPic;
    }

    public void setDoctorPic(DoctorPic doctorPic) {
        this.doctorPic = doctorPic;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        this.share = share;
    }
}
