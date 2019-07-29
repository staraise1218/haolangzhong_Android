package com.zhongyi.zhongyi.bean.temp;

import com.zhongyi.zhongyi.bean.BaseNetBean;
import com.zhongyi.zhongyi.bean.UploadFile;

import java.util.List;

public class UploadFileTemp extends BaseNetBean {
    private List<UploadFile> data;

    public List<UploadFile> getData() {
        return data;
    }

    public void setData(List<UploadFile> data) {
        this.data = data;
    }
}
