package com.zhongyi.zhongyi.utils;

import android.content.Context;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.opensdk.utils.Log;
import com.zhongyi.zhongyi.bean.temp.PayBean;

import java.util.Date;
import java.util.Random;

public class WX_Pay {
 
    public IWXAPI api;
    private PayReq req;
 
    public WX_Pay(Context context) {
        api = WXAPIFactory.createWXAPI(context, null);
        api.registerApp("wx4cac222a8fceba72");
    }
 
    /**
     * 向微信服务器发起的支付请求
     */
    public void pay(PayBean bean) {
 
        req = new PayReq();
 
        req.appId = bean.getAppid();//APPID
        req.partnerId = bean.getPartnerid();//    商户号
        req.prepayId = bean.getPrepayid();//  预付款ID
        req.nonceStr = bean.getNoncestr();//随机数
        req.timeStamp = bean.getTimestamp();//时间戳
        req.packageValue = "Sign=WXPay";//固定值Sign=WXPay
        req.sign = bean.getSign();//签名

        api.sendReq(req);
    }

}