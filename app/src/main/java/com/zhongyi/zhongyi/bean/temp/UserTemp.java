package com.zhongyi.zhongyi.bean.temp;

import com.zhongyi.zhongyi.bean.BaseNetBean;
import com.zhongyi.zhongyi.bean.User;

public class UserTemp extends BaseNetBean {
    private User data;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
