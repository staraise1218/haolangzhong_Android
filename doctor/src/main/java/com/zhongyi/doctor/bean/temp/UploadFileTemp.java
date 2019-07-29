package com.zhongyi.doctor.bean.temp;



import com.zhongyi.doctor.bean.BaseNetBean;
import com.zhongyi.doctor.bean.UploadFile;

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
