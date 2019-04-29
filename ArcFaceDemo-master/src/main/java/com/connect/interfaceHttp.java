package com.connect;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface interfaceHttp {
    @GET("save.php")
    Call<Translation> getOn(@Query("flag")String flag,@Query("name")String name,@Query("getoo")int getoo,@Query("gettime")String gettime);

    @GET("save.php")
    Call<Translation> getOff(@Query("flag")String flag,@Query("name")String name,@Query("getoo")int getoo,@Query("gettime")String gettime);

    @GET("savelog.php")
    Call<Translation> savelog(@Query("flag")String flag,@Query("name")String name,@Query("getoo")int getoff,@Query("gettime")String gettime);


    @GET("buslogin.php")
    Call<busInfo> getInfo(@Query("busnumber")String busnumber);

    @GET("phonelogin.php")
    Call<busInfo> phoneLogin(@Query("phonenumber")String phonenumber);

    @GET("setbusgps.php")
    Call<Translation> setGPS(@Query("id")int id,@Query("busnumber")String busnumber,@Query("lon")String lon,@Query("lat")String lat,@Query("speed")String speed,@Query("time")String time,@Query("postxt")String postxt, @Query("onoff")int onoff);

    @GET("closegps.php")
    Call<Translation> closeGPS(@Query("id")int id);

    @GET("uploadStuData2.php")
    Call<Translation> uploadStuData(@Query("studata")String stuData);

//阿里云

    @GET("login")
    Call<busInfo> Login(@Query("mobile")String mobile);

    @GET("updategps")
    Call<StatusJson> updategps(@Query("line_id")int line_id,@Query("bus_id")int bus_id,@Query("lon")String lon,@Query("lat")String lat,@Query("speed")String speed,@Query("utime")String utime,@Query("pos_text")String pos_text);

    @GET("endgps")
    Call<StatusJson> endgps(@Query("id")int id);

    @GET("getversion")
    Call<Translation> getversion();


    @GET("savestunotice")
    Call<StatusJson> saveStuNotice(@Query("line_id") int line_id, @Query("teacher_tel") String teacher_tel, @Query("content") String content);

    @GET("getStuHols")
    Call<StuHolsJson>  getLeaveInfo(@Query("line_id") int line_id, @Query("teacher_tel") String teacher_tel,@Query("hols_type") int hols_type);

    @GET("uploadbuslog")
    Call<StatusJson> uploadBusLog(@Query("buslog")String buslog);

//获取历史记录
    @GET("getstulog")
    Call<BusLogHisInfo>  getBuslogHis(@Query("line_id") int line_id, @Query("log_date") String log_date,@Query("type") int type);



    @GET("uptstulog")
    Call<StatusJson> uploadBusLogHis(@Query("buslog")String buslog);

    @GET("uploadcrashinfo")
    Call<StatusJson> uploadCrashInfo(@Query("errorinfo")String errorinfo,@Query("businfo")String businfo,@Query("stuinfo")String stulog,@Query("busloginfo")String busloginfo,@Query("time")String time);
}

