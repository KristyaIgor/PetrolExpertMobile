package md.intelectsoft.petrolmpos;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolmpos.Utils.SPFHelp;
import md.intelectsoft.petrolmpos.network.broker.Body.SendRegisterApplication;
import md.intelectsoft.petrolmpos.network.broker.BrokerRetrofitClient;
import md.intelectsoft.petrolmpos.network.broker.BrokerServiceAPI;
import md.intelectsoft.petrolmpos.network.broker.Enum.BrokerServiceEnum;
import md.intelectsoft.petrolmpos.network.broker.Results.AppDataRegisterApplication;
import md.intelectsoft.petrolmpos.network.broker.Results.RegisterApplication;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class AuthorizeActivity extends AppCompatActivity {
    @BindView(R.id.layoutCode) TextInputLayout inputLayoutCode;
    @BindView(R.id.inputCode) TextInputEditText inputEditTextCode;

    String androidID, deviceName, publicIp, privateIp, deviceSN, osVersion, deviceModel;

    ProgressDialog progressDialog;
    BrokerServiceAPI brokerServiceAPI;
    Context context;

    @OnClick(R.id.registerApp) void onRegister(){
        String activationCode = inputEditTextCode.getText().toString();
        preparedActivateApp(activationCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        context = this;
        progressDialog = new ProgressDialog(context);
        brokerServiceAPI = BrokerRetrofitClient.getApiBrokerService();

        deviceModel = Build.MODEL;
        deviceSN = Build.SERIAL;
        deviceName = Build.DEVICE;
        osVersion = Build.VERSION.RELEASE;
        androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = new UUID(androidID.hashCode(), deviceName.hashCode()).toString();
        publicIp = getPublicIPAddress(this);
        privateIp = getIPAddress(true);

        SPFHelp.getInstance().putString("deviceId", deviceId);

        inputEditTextCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.equals(""))
                    inputLayoutCode.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void preparedActivateApp(String activationCode) {
        if(activationCode.equals(""))
            inputLayoutCode.setError("Input the field!");
        else{
            //data send to register app in broker server
            SendRegisterApplication registerApplication = new SendRegisterApplication();

            String ids = new UUID(androidID.hashCode(),androidID.hashCode()).toString();
            registerApplication.setDeviceID(ids);
            registerApplication.setDeviceModel(deviceModel);
            registerApplication.setDeviceName(deviceName);
            registerApplication.setSerialNumber(deviceSN);
            registerApplication.setPrivateIP(privateIp);
            registerApplication.setPublicIP(publicIp);
            registerApplication.setOSType(BrokerServiceEnum.Android);
            registerApplication.setApplicationVersion(getAppVersion(this));
            registerApplication.setProductType(131);
            registerApplication.setOSVersion(osVersion);
            registerApplication.setLicenseActivationCode(activationCode);

            Log.e("TAG", "registerDeviceToBrokerService: "
                    + "setDeviceID: " + ids
                    + "\n setDeviceModel: " + deviceModel
                    + "\n setDeviceName: " + deviceName
                    + "\n setSerialNumber: " + deviceSN
                    + "\n setPrivateIP: " + privateIp
                    + "\n setPublicIP: " + publicIp
                    + "\n setOSType: " + BrokerServiceEnum.Android
                    + "\n setApplicationVersion: " + getAppVersion(this)
                    + "\n setProductType: " + BrokerServiceEnum.SalesAgent
                    + "\n setOSVersion: " + osVersion
                    + "\n setLicenseActivationCode: " + activationCode);

            registerApplicationToBroker(registerApplication, activationCode);
        }
    }

    private void registerApplicationToBroker(SendRegisterApplication registerApplication, String activationCode) {
        Call<RegisterApplication> registerApplicationCall = brokerServiceAPI.registerApplicationCall(registerApplication);
        progressDialog.setMessage("Register device...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setButton(-1, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registerApplicationCall.cancel();
                if(registerApplicationCall.isCanceled())
                    dialog.dismiss();
            }
        });
        progressDialog.show();

        registerApplicationCall.enqueue(new Callback<RegisterApplication>() {
            @Override
            public void onResponse(Call<RegisterApplication> call, Response<RegisterApplication> response) {
                RegisterApplication result = response.body();

                if (result == null){
                    progressDialog.dismiss();
                    Toast.makeText(context, "Response from broker server is null!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(result.getErrorCode() == 0) {
                        AppDataRegisterApplication appDataRegisterApplication = result.getAppData();
                        //if app registered successful , save installation id and company name
                        Map<String,String> licenseData = new HashMap<>();
                        licenseData.put("LicenseID", appDataRegisterApplication.getLicenseID());
                        licenseData.put("LicenseCode", appDataRegisterApplication.getLicenseCode());
                        licenseData.put("CompanyName", appDataRegisterApplication.getCompany());
                        licenseData.put("CompanyIDNO", appDataRegisterApplication.getIDNO());
                        licenseData.put("LicenseActivationCode", activationCode);

                        SPFHelp.getInstance().putStrings(licenseData);

                        //after register app ,get URI for accounting system on broker server
                        progressDialog.dismiss();

                        if(appDataRegisterApplication.getURI() != null && !appDataRegisterApplication.getURI().equals("") && appDataRegisterApplication.getURI().length() > 5){
                            long nowDate = new Date().getTime();
                            String serverStringDate = appDataRegisterApplication.getServerDateTime();
                            serverStringDate = serverStringDate.replace("/Date(","");
                            serverStringDate = serverStringDate.replace("+0200)/","");
                            serverStringDate = serverStringDate.replace("+0300)/","");

                            long serverDate = Long.parseLong(serverStringDate);

                            SPFHelp.getInstance().putString("URI", appDataRegisterApplication.getURI());
                            SPFHelp.getInstance().putLong("DateReceiveURI", nowDate);
                            SPFHelp.getInstance().putLong("ServerDateTime", serverDate);

                            startActivity(new Intent(context, MainActivity.class));
                            finish();
                        }
                        else{
                            new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                    .setTitle("URL not set!")
                                    .setMessage("The application is not fully configured.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialogInterface, i) -> {
                                        finish();
                                    })
                                    .setNegativeButton("Retry",((dialogInterface, i) -> {
                                        registerApplicationToBroker(registerApplication, activationCode);
                                    }))
                                    .show();

                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(context, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterApplication> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context, "Failure get URI: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }

    private String getPublicIPAddress(Context context) {
        //final NetworkInfo info = NetworkUtils.getNetworkInfo(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = cm.getActiveNetworkInfo();

        RunnableFuture<String> futureRun = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                if ((info != null && info.isAvailable()) && (info.isConnected())) {
                    StringBuilder response = new StringBuilder();

                    try {
                        HttpURLConnection urlConnection = (HttpURLConnection) (
                                new URL("http://checkip.amazonaws.com/").openConnection());
                        urlConnection.setRequestProperty("User-Agent", "Android-device");
                        //urlConnection.setRequestProperty("Connection", "close");
                        urlConnection.setReadTimeout(1000);
                        urlConnection.setConnectTimeout(1000);
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setRequestProperty("Content-type", "application/json");
                        urlConnection.connect();

                        int responseCode = urlConnection.getResponseCode();

                        if (responseCode == HttpURLConnection.HTTP_OK) {

                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }

                        }
                        urlConnection.disconnect();
                        return response.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //Log.w(TAG, "No network available INTERNET OFF!");
                    return null;
                }
                return null;
            }
        });

        new Thread(futureRun).start();

        try {
            return futureRun.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getAppVersion(Context context){
        String result = "";

        try{
            result = context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
            result = result.replaceAll("[a-zA-Z] |-","");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}