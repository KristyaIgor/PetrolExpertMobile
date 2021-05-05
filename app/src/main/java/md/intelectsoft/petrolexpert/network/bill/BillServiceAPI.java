package md.intelectsoft.petrolexpert.network.bill;

import md.intelectsoft.petrolexpert.network.bill.body.RegisterFiscalBill;
import md.intelectsoft.petrolexpert.network.bill.response.RespRegisterFiscalBill;
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

public interface BillServiceAPI {

    @GET("json/GetBillInfo")
    Call<GetCardInfo> getBillInfo (@Query("ID") String billId);

    @POST("json/RegisterBill")
    Call<RespRegisterFiscalBill> registerBill (@Body RegisterFiscalBill bill);

}
