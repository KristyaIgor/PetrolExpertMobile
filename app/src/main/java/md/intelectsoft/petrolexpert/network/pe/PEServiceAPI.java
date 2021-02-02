package md.intelectsoft.petrolexpert.network.pe;

import md.intelectsoft.petrolexpert.network.pe.body.registerBill.BillRegistered;
import md.intelectsoft.petrolexpert.network.pe.result.GetCardInfo;
import md.intelectsoft.petrolexpert.network.pe.result.GetCashList;
import md.intelectsoft.petrolexpert.network.pe.result.GetCurrentShift;
import md.intelectsoft.petrolexpert.network.pe.result.RegisterBillResponse;
import md.intelectsoft.petrolexpert.network.pe.result.RegisterDevice;
import md.intelectsoft.petrolexpert.network.pe.result.SimpleResponse;
import md.intelectsoft.petrolexpert.network.pe.result.authorizeUser.GetAuthorizeUser;
import md.intelectsoft.petrolexpert.network.pe.result.stationSettings.GetStationSettings;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PEServiceAPI {
    @GET("json/Ping")
    Call<Boolean> ping ();

    @GET("json/RegisterDevice")
    Call<RegisterDevice> registerDevice (@Query("StationID") String deviceId, @Query("Name") String name, @Query("CashID") String cashId, @Query("tokenID") String tokenUid);

    @GET("json/GetCardInfoByBarcode")
    Call<GetCardInfo> getCardInfoByBarcode (@Query("StationID") String deviceId, @Query("Barcode") String barcode);

    @POST("json/RegisterBill")
    Call<RegisterBillResponse> registerBill (@Body BillRegistered bill);

    @GET("json/PrintXReport")
    Call<SimpleResponse> printX (@Query("deviceId") String deviceId);

    @GET("json/AuthorizeUser")
    Call<GetAuthorizeUser> authorizeUser (@Query("userName") String userName);

    @GET("json/GetCashList")
    Call<GetCashList> getCashList (@Query("tokenUid") String userCode);

    @GET("json/GetStationSettings")
    Call<GetStationSettings> getStationSettings (@Query("StationID") String deviceId);

    @GET("json/GetCurrentShift")
    Call<GetCurrentShift> getCurrentShift (@Query("StationID") String deviceId);

}
