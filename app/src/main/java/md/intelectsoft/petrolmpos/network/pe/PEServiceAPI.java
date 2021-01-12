package md.intelectsoft.petrolmpos.network.pe;

import md.intelectsoft.petrolmpos.network.pe.result.GetAssortment;
import md.intelectsoft.petrolmpos.network.pe.result.RegisterDevice;
import md.intelectsoft.petrolmpos.network.pe.result.SimpleResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PEServiceAPI {
    @GET("json/Ping")
    Call<Boolean> ping ();

    @GET("json/RegisterDevice")
    Call<RegisterDevice> registerDevice (@Query("deviceId") String deviceId, @Query("Name") String name,
                                         @Query("Latitude") String lat, @Query("Longitude") String lon);

    @GET("json/GetAssortment")
    Call<GetAssortment> getAssortment (@Query("deviceId") String deviceId, @Query("CardID") String cardId,
                                       @Query("Latitude") String lat, @Query("Longitude") String lon);

    @GET("json/CreateBill")
    Call<SimpleResponse> createBill (@Query("deviceId") String deviceId, @Query("CardID") String cardId,
                              @Query("PriceLine") String priceLineId, @Query("Price") double price,
                              @Query("Count") String count,
                              @Query("Latitude") String lat, @Query("Longitude") String lon);

    @GET("json/PrintXReport")
    Call<SimpleResponse> printX (@Query("deviceId") String deviceId);
}
