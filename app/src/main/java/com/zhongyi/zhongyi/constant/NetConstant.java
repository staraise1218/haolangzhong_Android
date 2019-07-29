package com.zhongyi.zhongyi.constant;

/**
 * Created by 15342 on 2018/4/15.
 */

public class NetConstant {
    //public static final String BASE_URL = "http://192.168.1.28:8980/js/f/sys/";
    //public static final String BASE_URL2 = "http://192.168.1.28:8980/js/f/app/";
    //public static final String BASE_IMGE_URL = BASE_URL+"fileInfo/showPic/";
    //测试地址
    public static final String BASE_URL = "http://120.92.10.2:81/hlz/f/sys/";
    public static final String BASE_URL2 = "http://120.92.10.2:81/hlz/f/app/";
    //图片前缀
    public static final String BASE_IMGE_URL = "http://120.92.10.2/hlz/f/sys/fileInfo/showPic/";
    //登录接口
    public static final String LOGIN = "user/login";
    //第三方登录接口
    public static final String THIRD_LOGIN = "user/thirdLogin";
    //注册接口
    public static final String REGISTER = "user/register";
    //注册接口
    public static final String BIND = "user//bindThirdUserInfo";
    //忘记密码接口
    public static final String  RESET_PWD= "user/forgetPassword";
    //验证码接口
    public static final String CODE = "user/getCode";
    //首页
    public static final String  INDEX = "userIndex/getData";
    //专家列表
    public static final String DOCTOR_INFO = "doctorInfo/queryList";
    //按照标签查找
    public static final String DOCTOR_INFO_QUERY = "doctorInfo/queryListByLabel";
    //好郎中本院专家列表
    public static final String DOCTOR_INFO_OWN = "doctorInfo/queryListOwn";
    //获取综合服务首页数据
    public static final String ARTICLE_INFO = "articleInfo/queryList";
    //医生标签
    public static final String DOCTER_MARK = "doctorLabel/queryList/";
    //病症标签
    public static final String DOCTER_LABLE = "doctorLabel/queryDiseaseList";
    //在线问诊获取医生列表
    public static final String DOCTER_List = "doctorInfo/queryListByLabel";
    //提交订单
    public static final String SUBMIT_DATA = "consultationOrder/saveOrder";
    //求助通道
    public static final String  HELP_ORDER= "public/saveOrder";
    //上传文件
    public static final String UPLOAD = "fileInfo/upload";
    //保存用户信息
    public static final String SAVE_USER_INFO = "userInfo/saveUserInfo";
    //关注专家
    public static final String FELLOW = "userCollection/saveUserCollection";
    //取消关注专家
    public static final String CANCEL_FELLOW = "userCollection/cancelCollection/";
    //获取关注的专家
    public static final String USER_COLLECTION = "userCollection/queryDoctorList";
    //splish页面
    public static final String HOME_PAGE = "homePage/getHomePage";
    //获取城市
    public static final String CITY = "area/getGradeArea";
    //获取版本
    public static final String VERSION = "appversion/getNewVersionByType";
    //获取版本
    public static final String HOSPITAL_INFO = "hospital/showHospitalInfoMain";
    //预约账号
    public static final String HOSPITAL_REGISTER = "hospital/submitHospitalRegister";
    //测试
    public static final String  TEST = BASE_URL+"payController/getprepay";
    //测试
    public static final String  TEST_AL = BASE_URL+"payController/aliAppPay";
    //会员接口
    public static final String  MEMBER = "member/isMemberByUserId";
    //挂号支付知否完成
    public static final String  REGISTERB_ORDER = "hospital/getDoctorRegisterOrderSuccess";
    //商城支付是否完成
    public static final  String GET_ORDER_SUCCESS = "order/getOrderSuccess";
    //调理支付知否完成
    public static final String GET_TIAO_LI_ORDER_SUCCESS = "order/getTiaoLiOrderSuccess";
    //会员支付是否完成
    public static final String GET_MEMBER_ORDER_SUCCESS = "order/getMemberOrderSuccess";
    //锦旗支付是否完成
    public static final String GET_FLAG_ORDER_SUCCESS = "order/getDashangOrderSuccess";

    //H5页面
    //在线问诊
    public static final String  ZXWZ = "http://120.92.10.2:81/hlz/static/catchMedicine.html";
    //空中药房
    public static final String  KZYF = "http://120.92.10.2:81/hlz/static/airPharmacy.html";
    //专家详情
    public static final String EXPERT = "http://120.92.10.2:81/hlz/static/Introduction.html";
    //调理页面
    public static final String INTRODUCTION_ZHAO = "http://120.92.10.2:81/hlz/static/Introductionzhao.html";
    //商品详情
    public static final String GOODS_DETAIL = "http://120.92.10.2:81/hlz/static/medicinedetail.html";
    //商品列表
    public static final String GOODS_LIST = "http://120.92.10.2:81/hlz/static/medicineSearch.html";
    //文章详情
    public static final String ARTICLE_DETAILS = "http://120.92.10.2:81/hlz/static/articledetails.html";
    //地址
    public static final String ADDRESS = "http://120.92.10.2:81/hlz/static/address.html";
    //我的订单
    public static final String MY_ORDER = "http://120.92.10.2:81/hlz/static/myorder.html";
    //我的药方
    public static final String MY_DRUG = "http://120.92.10.2:81/hlz/static/myPrescription.html";
    //我的红包
    public static final String MY_MONEY = "http://120.92.10.2:81/hlz/static/redBag.html";
    //我的问诊
    public static final String MY_WENZHEN = "http://120.92.10.2:81/hlz/static/visitsrecord.html";
    //我的收藏
    public static final String MY_SHOUCANG = "http://120.92.10.2:81/hlz/static/mycollection.html";
    //咨询详情
    public static final String CONSULTATION_PAY = "http://120.92.10.2:81/hlz/static/consultationPay.html";
    //客服反馈
    public static final String FEEDBACK = "http://120.92.10.2:81/hlz/static/userFeedback.html";
    //关于我们
    public static final String ABOUT_US = "http://120.92.10.2:81/hlz/static/aboutMe.html";
    //用户消息
    public static final String MESSAGE = "http://120.92.10.2:81/hlz/static/message.html";
    //好郎中用户协议
    public static final String XIE_YI = "http://120.92.10.2:81/hlz/static/yonghuxieyi.html";
    //好郎中医院详情
    public static final String HOSPITAL_DETAIL = "http://120.92.10.2:81/hlz/static/hospitalInfo.html";
    //查看会员页面
    public static final String MEMBER_BUY = " http://120.92.10.2:81/hlz/static/huiyuan.html";
    //挂号支付
    public static final String GUAHAO = "http://120.92.10.2:81/hlz/static/payRegister.html?docid=";
    //我的调理tiaoliUserPaySucessOrder.html
    public static final String  TIAOLI= "http://120.92.10.2:81/hlz/static/tiaoliUserPaySucessOrder.html";
    //我的挂号
    public static final String  GUA_HAO_MY= "http://120.92.10.2:81/hlz/static/guahaomy.html";

    //网站地址2
    public static final String BASE_URL_WEB_2 = "http://yangzhiapp.staraise.com.cn";

}
