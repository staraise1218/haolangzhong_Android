package com.zhongyi.doctor.bean.temp;


import com.zhongyi.doctor.bean.BaseNetBean;
import com.zhongyi.doctor.bean.Mark;

import java.util.List;

public class MarkTemp extends BaseNetBean {
    private List<Mark> data;

    public List<Mark> getData() {
        return data;
    }

    public void setData(List<Mark> data) {
        this.data = data;
    }
}
