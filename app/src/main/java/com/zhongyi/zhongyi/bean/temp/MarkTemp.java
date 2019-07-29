package com.zhongyi.zhongyi.bean.temp;

import com.zhongyi.zhongyi.bean.BaseNetBean;
import com.zhongyi.zhongyi.bean.Mark;

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
