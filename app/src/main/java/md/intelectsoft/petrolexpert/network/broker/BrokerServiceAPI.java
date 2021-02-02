package md.intelectsoft.petrolexpert.network.broker;


import md.intelectsoft.petrolexpert.network.broker.Body.InformationData;
import md.intelectsoft.petrolexpert.network.broker.Body.SendGetURI;
import md.intelectsoft.petrolexpert.network.broker.Body.SendRegisterApplication;
import md.intelectsoft.petrolexpert.network.broker.Results.ErrorMessage;
import md.intelectsoft.petrolexpert.network.broker.Results.RegisterApplication;
import md.intelectsoft.petrolexpert.network.pe.body.SetFiscalBody;
import md.intelectsoft.petrolexpert.network.pe.result.SetFiscal;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface BrokerServiceAPI {
    @GET("/ISConnectionBrokerService/json/Ping")
    Call<Boolean> ping();

    @POST("/ISConnectionBrokerService/json/RegisterApplication")
    Call<RegisterApplication> registerApplicationCall(@Body SendRegisterApplication bodyRegisterApp);

    @POST("/ISConnectionBrokerService/json/GetURI")
    Call<RegisterApplication> getURICall(@Body SendGetURI sendGetURI);

    @POST("/ISConnectionBrokerService/json/UpdateDiagnosticInformation")
    Call<ErrorMessage> updateDiagnosticInfo(@Body InformationData informationData);

    @POST ("/ISConnectionBrokerService/json/SetAsFiscal")
    Call<SetFiscal> setAsFiscal (@Body SetFiscalBody licenseData);
}
