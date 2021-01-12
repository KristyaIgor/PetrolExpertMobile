package md.intelectsoft.petrolmpos.network.broker;


import md.intelectsoft.petrolmpos.network.broker.Body.InformationData;
import md.intelectsoft.petrolmpos.network.broker.Body.SendGetURI;
import md.intelectsoft.petrolmpos.network.broker.Body.SendRegisterApplication;
import md.intelectsoft.petrolmpos.network.broker.Results.ErrorMessage;
import md.intelectsoft.petrolmpos.network.broker.Results.RegisterApplication;
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
}
