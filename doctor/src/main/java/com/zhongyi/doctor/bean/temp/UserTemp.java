package com.zhongyi.doctor.bean.temp;


import com.zhongyi.doctor.bean.BaseNetBean;
import com.zhongyi.doctor.bean.User;

public class UserTemp extends BaseNetBean {
    private User data;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
