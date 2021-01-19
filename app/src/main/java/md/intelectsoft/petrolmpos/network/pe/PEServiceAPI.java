package md.intelectsoft.petrolmpos.network.pe;

import md.intelectsoft.petrolmpos.network.pe.body.SetFiscalBody;
import md.intelectsoft.petrolmpos.network.pe.result.GetAssortment;
import md.intelectsoft.petrolmpos.network.pe.result.GetCardInfo;
import md.intelectsoft.petrolmpos.network.pe.result.RegisterDevice;
import md.intelectsoft.petrolmpos.network.pe.result.SetFiscal;
import md.intelectsoft.petrolmpos.network.pe.result.SimpleResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PEServiceAPI {
    @GET("json/Ping")
    Call<Boolean> ping ();

    @GET("json/RegisterDevice")
    Call<RegisterDevice> registerDevice (@Query("StationID") String deviceId, @Query("Name") String name);

    @GET("json/GetAsortment")
    Call<GetAssortment> getAssortment (@Query("StationID") String deviceId);

    @GET("json/GetCardInfoByBarcode")
    Call<GetCardInfo> getCardInfoByBarcode (@Query("StationID") String deviceId, @Query("Barcode") String barcode);

    @GET("json/CreateBill")
    Call<SimpleResponse> createBill (@Query("deviceId") String deviceId, @Query("CardID") String cardId,
                              @Query("PriceLine") String priceLineId, @Query("Price") double price,
                              @Query("Count") String count,
                              @Query("Latitude") String lat, @Query("Longitude") String lon);

    @GET("json/PrintXReport")
    Call<SimpleResponse> printX (@Query("deviceId") String deviceId);

    @GET("json/SetAsFiscal")
    Call<SetFiscal> setAsFiscal (@Body SetFiscalBody licenseData);


}
