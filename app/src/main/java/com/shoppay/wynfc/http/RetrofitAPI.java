package com.shoppay.wynfc.http;

import com.shoppay.wynfc.bean.Login;
import com.shoppay.wynfc.bean.Yuming;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2016/12/2.
 */

public interface RetrofitAPI {
    /**
     * 获取域名
     */
    @FormUrlEncoded
    @POST("PosJi/API/AppPub.ashx?Method=AppGetDomain")
    Call<Yuming> obtainYuming(@FieldMap Map<String, Object> map);

    /**
     * 错误日志
     */
    @FormUrlEncoded
    @POST("mobile/app/api/appAPI.ashx?Method=LogError")
    Call<ResponseBody> errorMsg(@FieldMap Map<String, Object> map);

    /**
     * 商家登录
     */
    @FormUrlEncoded
    @POST("/mobile/app/api/appAPI.ashx?Method=AppShopLogin")
    Call<Login> login(@FieldMap Map<String, Object> map);
    /**
     * 商家退出
     */
    @FormUrlEncoded
    @POST("/mobile/app/api/appAPI.ashx?Method=AppUserLoginOut")
    Call<Login> outLogin(@FieldMap Map<String, Object> map);

    /**
     * 商家办卡
     */
    @FormUrlEncoded
    @POST("/mobile/app/api/appAPI.ashx?Method=AppMemAdd")
    Call<ResponseBody> vipCard(@FieldMap Map<String, Object> map);
    /**
     * 会员等级列表
     */
    @FormUrlEncoded
    @POST("/mobile/app/api/appAPI.ashx?Method=APPGetMemLevelList")
    Call<ResponseBody> vipDengji(@FieldMap Map<String, Object> map);
    /**
     * 商品分类
     */
    @FormUrlEncoded
    @POST("/mobile/app/api/appAPI.ashx?Method=APPGetGoodsClass")
    Call<ResponseBody> shopClass(@FieldMap Map<String, Object> map);





    @FormUrlEncoded
    @POST("UpdateUserInfo")
    Call<ResponseBody> updateUserInfo(@FieldMap Map<String, Object> map);

    /**
     * 查询列表数据
     */
    @FormUrlEncoded
    @POST("GetLabModelCategoryList")
    Call<ResponseBody> getLabModelCategoryList(@FieldMap Map<String, Object> map);
    /**
     * 查询列表数据
     */
    @FormUrlEncoded
    @POST("userLogin")
    Call<ResponseBody> retrofittest(@FieldMap Map<String, Object> map);

    /**
     * 5.查询列表数据
     */
    @FormUrlEncoded
    @POST("GetLabModelList")
    Call<ResponseBody> getLabModelList(@FieldMap Map<String, Object> map);


    /**
     * 6.查询模型详情
     */
    @FormUrlEncoded
    @POST("GetLabModelItem")
    Call<ResponseBody> getLabModelItem(@FieldMap Map<String, Object> map);

    /**
     * 7.查询实验列表
     */
    @FormUrlEncoded
    @POST("GetLabList")
    Call<ResponseBody> getLabList(@FieldMap Map<String, Object> map);


    /**
     * 8.查询实验详情
     */
    @FormUrlEncoded
    @POST("GetLabInfo")
    Call<ResponseBody> getLabInfo(@FieldMap Map<String, Object> map);


    /**
     *9.查询内容分类列表
     */
    @FormUrlEncoded
    @POST("GetContentCategoryList")
    Call<ResponseBody> getContentCategoryList(@FieldMap Map<String, Object> map);



    /**
     *10.查询分类内容列表
     */
    @FormUrlEncoded
    @POST("GetContentList")
    Call<ResponseBody> getContentList(@FieldMap Map<String, Object> map);


    /**
     *11.查询分类内容详情
     */
    @FormUrlEncoded
    @POST("GetContentItem")
    Call<ResponseBody> getContentItem(@FieldMap Map<String, Object> map);



    /**
     *12.查询未分类内容列表
     */
    @FormUrlEncoded
    @POST("GetArticleList")
    Call<ResponseBody> getArticleList(@FieldMap Map<String, Object> map);


    /**
     *13.查询未分类内容详情
     */
    @FormUrlEncoded
    @POST("GetArticleItem")
    Call<ResponseBody> getArticleItem(@FieldMap Map<String, Object> map);

    /**
     * 发送消息
     */
    @FormUrlEncoded
    @POST("MobileSendMessage")
    Call<ResponseBody> mobileSendMessage(@FieldMap Map<String, Object> map);


    @Streaming
    @GET
    Call<ResponseBody> downloadApkAsync(@Url String fileUrl);

    @POST("FrameworkRight/GetPayNeedIntIdByUserOwnerId")
    Call<ResponseBody> getUserid(@QueryMap Map<String, Object> map);
}
