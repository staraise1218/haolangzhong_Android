package com.zhongyi.zhongyi.bean.temp;

import com.zhongyi.zhongyi.bean.Doctor;

import java.util.List;

public class DoctorData {
      private int count;
      private List<Doctor> items;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Doctor> getItems() {
        return items;
    }

    public void setItems(List<Doctor> items) {
        this.items = items;
    }
}
