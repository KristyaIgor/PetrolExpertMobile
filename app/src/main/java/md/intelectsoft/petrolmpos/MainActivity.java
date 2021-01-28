package md.intelectsoft.petrolmpos;

import android.Manifest;
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
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
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
import md.intelectsoft.petrolmpos.enums.ShiftStateEnum;
import md.intelectsoft.petrolmpos.network.broker.Body.SendGetURI;
import md.intelectsoft.petrolmpos.network.broker.BrokerRetrofitClient;
import md.intelectsoft.petrolmpos.network.broker.BrokerServiceAPI;
import md.intelectsoft.petrolmpos.network.broker.Enum.BrokerServiceEnum;
import md.intelectsoft.petrolmpos.network.broker.Results.AppDataRegisterApplication;
import md.intelectsoft.petrolmpos.network.broker.Results.RegisterApplication;
import md.intelectsoft.petrolmpos.network.pe.PERetrofitClient;
import md.intelectsoft.petrolmpos.network.pe.PEServiceAPI;
import md.intelectsoft.petrolmpos.network.pe.result.AssortmentSerializable;
import md.intelectsoft.petrolmpos.network.pe.result.GetCurrentShift;
import md.intelectsoft.petrolmpos.network.pe.result.RegisterDevice;
import md.intelectsoft.petrolmpos.network.pe.result.SimpleResponse;
import md.intelectsoft.petrolmpos.network.pe.result.authorizeUser.GetAuthorizeUser;
import md.intelectsoft.petrolmpos.network.pe.result.authorizeUser.UserAuth;
import md.intelectsoft.petrolmpos.network.pe.result.stationSettings.AssortmentStation;
import md.intelectsoft.petrolmpos.network.pe.result.stationSettings.GetStationSettings;
import md.intelectsoft.petrolmpos.printeractivity.PrinterFonts;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.layoutApplyCard) ConstraintLayout layoutApplyCard;
    @BindView(R.id.textTerminalNumber) TextView terminalNumber;
    @BindView(R.id.textOperatorName) TextView terminalUser;


    String androidID, deviceName, publicIp, privateIp, deviceSN, osVersion, deviceModel, deviceId;

    ProgressDialog progressDialog;
    BrokerServiceAPI brokerServiceAPI;
    PEServiceAPI peServiceAPI;
    Context context;

    @OnClick(R.id.buttonScanCardCorporativ) void onScanCardCorp(){
        startActivity(new Intent(context, ScanCardCorporativActivity.class));
    }

    @OnClick(R.id.buttonScanMyDiscount) void onScanMyDiscount(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            startActivityForResult(new Intent(context, ScanMyDiscountActivity.class), 122);
        else
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 221);
    }

    @OnClick(R.id.buttonScanWithoutIdentify) void onScanWithoutIdentify(){
        Call<GetStationSettings> getAssortmentCall = peServiceAPI.getStationSettings(deviceId);
        progressDialog.setMessage("Load available products...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setButton(-1, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getAssortmentCall.cancel();
                if (getAssortmentCall.isCanceled())
                    dialog.dismiss();
            }
        });
        progressDialog.show();

        getAssortmentCall.enqueue(new Callback<GetStationSettings>() {
            @Override
            public void onResponse(Call<GetStationSettings> call, Response<GetStationSettings> response) {
                GetStationSettings getAssortment = response.body();

                if(getAssortment != null){
                    if(getAssortment.getErrorCode() == 0){
                        if(getAssortment.getAssortment() != null && getAssortment.getAssortment().size() > 0){

                            List<AssortmentSerializable> listOfProducts = new ArrayList<>();

                            for(AssortmentStation item: getAssortment.getAssortment()){
                                AssortmentSerializable product = new AssortmentSerializable(
                                        item.getAssortimentID(),
                                        item.getAssortmentCode(),
                                        item.getDiscount(),
                                        item.getName(),
                                        item.getPrice(),
                                        item.getPriceLineID());

                                listOfProducts.add(product);
                            }

                            progressDialog.dismiss();

                            Intent intent = new Intent(context, ProductsWithoutIndentingActivity.class);
                            intent.putExtra("ResponseAssortment", (Serializable) listOfProducts);
                            startActivity(intent);
                        }
                        else{
                            progressDialog.dismiss();
                            new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                    .setTitle("Attention!")
                                    .setMessage("List of products is empty!")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    }
                    else{
                        progressDialog.dismiss();
                        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setTitle("Attention!")
                                .setMessage("Error load products! Message: " + getAssortment.getErrorMessage() + ". Error code: " + getAssortment.getErrorCode())
                                .setCancelable(false)
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }
                else{
                    progressDialog.dismiss();
                    new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                            .setTitle("Attention!")
                            .setMessage("Error load products! Response is empty!")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialogInterface, i) -> {
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<GetStationSettings> call, Throwable t) {
                progressDialog.dismiss();
                new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setTitle("Attention!")
                        .setMessage("Failure load products! Message: " + t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> {

                        })
                        .show();
            }
        });
    }

    @OnClick(R.id.buttonPrintX) void onPrintX(){
        printX();
    }

    @OnClick(R.id.buttonInfo) void onInfo() {
        startActivity(new Intent(context, InfoActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_v0);

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
        deviceId = SPFHelp.getInstance().getString("deviceId", "");
        publicIp = getPublicIPAddress(this);
        privateIp = getIPAddress(true);

        String uri = SPFHelp.getInstance().getString("URI", null);
        String licenseId = SPFHelp.getInstance().getString("LicenseID", null);

        peServiceAPI = PERetrofitClient.getPEService(uri);

        getURI(licenseId);
        long tokenValidate = SPFHelp.getInstance().getLong("TokenValid", 0);
        long dateNow = new Date().getTime();
        if(tokenValidate < dateNow) authorizeUser(SPFHelp.getInstance().getString("UserCodeAuth", ""));
        else registerDevice();

        getShiftInfo();

        terminalUser.setText(SPFHelp.getInstance().getString("Owner",""));
        terminalNumber.setText("Nr: " + SPFHelp.getInstance().getInt("RegisteredNumber",0));

        if(BaseApp.isVFServiceConnected())
            PrinterFonts.initialize(this.getAssets());

    }

    private void authorizeUser(String code) {
        Call<GetAuthorizeUser> call = peServiceAPI.authorizeUser(code);

        progressDialog.setMessage("Refresh token...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setButton(-1, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                call.cancel();
                if(call.isCanceled())
                    finish();
            }
        });
        progressDialog.show();

        call.enqueue(new Callback<GetAuthorizeUser>() {
            @Override
            public void onResponse(Call<GetAuthorizeUser> call, Response<GetAuthorizeUser> response) {
                GetAuthorizeUser getAuthorizeUser = response.body();
                progressDialog.dismiss();
                if(getAuthorizeUser != null){
                    if(getAuthorizeUser.getErrorCode() == 0){
                        String tokenUser = getAuthorizeUser.getToken().getUid();
                        String tokenValid = getAuthorizeUser.getToken().getValidTo();
                        if (tokenValid != null) {
                            if (tokenValid != null)
                                tokenValid = tokenValid.replace("/Date(", "");
                            if (tokenValid != null)
                                tokenValid = tokenValid.substring(0, tokenValid.length() - 7);
                        }

                        long timeValid = Long.parseLong(tokenValid);
                        SPFHelp.getInstance().putLong("TokenValid", timeValid);
                        SPFHelp.getInstance().putString("TokenId", tokenUser);

                        registerDevice();
                    }
                    else
                        showErrorDialogAuthUser("Error auth user!Message: " + getAuthorizeUser.getErrorMessage());
                }
                else showErrorDialogAuthUser("Error auth user! Response is empty!");
            }

            @Override
            public void onFailure(Call<GetAuthorizeUser> call, Throwable t) {
                progressDialog.dismiss();
                showErrorDialogAuthUser("Error auth user!Message: " + t.getMessage());
            }
        });
    }

    private void showErrorDialogAuthUser (String text) {
        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Attention!")
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {

                })
                .show();
    }

    private void getShiftInfo() {
        Call<GetCurrentShift> call = peServiceAPI.getCurrentShift(deviceId);

        call.enqueue(new Callback<GetCurrentShift>() {
            @Override
            public void onResponse(Call<GetCurrentShift> call, Response<GetCurrentShift> response) {
                GetCurrentShift getCurrentShift = response.body();
                if(getCurrentShift != null){
                    if(getCurrentShift.getErrorCode() == 0){
                        if(getCurrentShift.getShiftState() != ShiftStateEnum.Valid){
                            if(getCurrentShift.getShiftState() == ShiftStateEnum.Closed){
                                showErrorDialogShiftState("Shift is closed! You can't work! Open shift first.");
                            }
                            else if(getCurrentShift.getShiftState() == ShiftStateEnum.Elapsed){
                                showErrorDialogShiftState("Shift elapsed! You can't work!");
                            }
                            else{
                                showErrorDialogShiftState("Can't not work! Shift state: " + getCurrentShift.getShiftState());
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GetCurrentShift> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 121){
            if(resultCode == RESULT_OK){
                String barcode = data != null ? data.getStringExtra("Barcode") : "";
                Log.e("TAG", "onActivityResult: " + barcode );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 201){
            if(permissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startActivityForResult(new Intent(context, ScannedBarcodeActivity.class), 121);
        }
        if(requestCode == 221){
            if(permissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startActivityForResult(new Intent(context, ScanMyDiscountActivity.class), 122);
        }
    }

//    private void getAssortment(String cardId){
//        Call<GetAssortment> call = peServiceAPI.getAssortment(deviceId, cardId, "0" , "0");
//
//        progressDialog.setMessage("Get assortment...");
//        progressDialog.setCancelable(false);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setButton(-1, "Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                call.cancel();
//                if (call.isCanceled())
//                    dialog.dismiss();
//            }
//        });
//        progressDialog.show();
//
//        call.enqueue(new Callback<GetAssortment>() {
//            @Override
//            public void onResponse(Call<GetAssortment> call, Response<GetAssortment> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<GetAssortment> call, Throwable t) {
//
//            }
//        });
//    }

    private void printX() {
        Call<SimpleResponse> call = peServiceAPI.printX(deviceId);

        progressDialog.setMessage("Print X report...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setButton(-1, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                call.cancel();
                if (call.isCanceled())
                    dialog.dismiss();
            }
        });
        progressDialog.show();

        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                progressDialog.dismiss();
                SimpleResponse device = response.body();
                if(device != null)
                    if(device.getNoError()) Toast.makeText(MainActivity.this, "X report printed!", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(MainActivity.this, "X report not printed! Message: " + device.getErrorMessage(), Toast.LENGTH_SHORT).show();
                else Toast.makeText(MainActivity.this, "X report not printed! Not response!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "X report not printed! Message: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerDevice() {
        Call<RegisterDevice> call = peServiceAPI.registerDevice(deviceId, "Android " + deviceModel, SPFHelp.getInstance().getString("CashId",""), SPFHelp.getInstance().getString("TokenId",""));

        progressDialog.setMessage("Check device...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setButton(-1, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                call.cancel();
                if (call.isCanceled())
                    dialog.dismiss();
            }
        });
        progressDialog.show();

        call.enqueue(new Callback<RegisterDevice>() {
            @Override
            public void onResponse(Call<RegisterDevice> call, Response<RegisterDevice> response) {
                progressDialog.dismiss();
                RegisterDevice device = response.body();
                if(device != null)
                    if(device.getNoError() == 0 && device.getRegistred()){
                        terminalNumber.setText("Nr: " + device.getRegistredNumber());
                        SPFHelp.getInstance().putInt("RegisteredNumber", device.getRegistredNumber());
                    }
                    else showErrorDialogRegisterDevice("Device not registered! Message: " + device.getErrorMessage());
                else showErrorDialogRegisterDevice("Device not registered! Not response!");
            }

            @Override
            public void onFailure(Call<RegisterDevice> call, Throwable t) {
                progressDialog.dismiss();
                showErrorDialogRegisterDevice("Device not registered! Message: " + t.getMessage());
            }
        });
    }

    private void showErrorDialogRegisterDevice(String text) {
        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Attention!")
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    finish();
                })
                .setNegativeButton("Retry",((dialogInterface, i) -> {
                    registerDevice();
                }))
                .show();
    }

    private void showErrorDialogShiftState(String text) {
        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Attention!")
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    finish();
                })
//                .setNegativeButton("Retry",((dialogInterface, i) -> {
//
//                }))
                .show();
    }

    private void getURI(String licenseID) {
        //data send to register app in broker server
        SendGetURI registerApplication = new SendGetURI();

        String ids = new UUID(androidID.hashCode(),androidID.hashCode()).toString();
        registerApplication.setDeviceID(ids);
        registerApplication.setDeviceModel(deviceModel);
        registerApplication.setDeviceName(deviceName);
        registerApplication.setSerialNumber(deviceSN);
        registerApplication.setPrivateIP(privateIp);
        registerApplication.setPublicIP(publicIp);
        registerApplication.setLicenseID(licenseID);
        registerApplication.setOSType(BrokerServiceEnum.Android);
        registerApplication.setApplicationVersion(getAppVersion(this));
        registerApplication.setProductType(131);
        registerApplication.setWorkPlace(SPFHelp.getInstance().getString("Cash",""));
        registerApplication.setLastAuthorizedUser(SPFHelp.getInstance().getString("Owner", ""));
        registerApplication.setOSVersion(osVersion);

        Call<RegisterApplication> getURICall = brokerServiceAPI.getURICall(registerApplication);

        getURICall.enqueue(new Callback<RegisterApplication>() {
            @Override
            public void onResponse(Call<RegisterApplication> call, Response<RegisterApplication> response) {
                RegisterApplication result = response.body();
                if (result == null){
                    Toast.makeText(context, "Response from broker server is null!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(result.getErrorCode() == 0) {
                        AppDataRegisterApplication appDataRegisterApplication = result.getAppData();
                        //if app registered successful , save installation id and company name

                        Map<String,String> licenseData = new HashMap<>();
                        licenseData.put("LicenseID",appDataRegisterApplication.getLicenseID());
                        licenseData.put("LicenseCode",appDataRegisterApplication.getLicenseCode());
                        licenseData.put("CompanyName",appDataRegisterApplication.getCompany());
                        licenseData.put("CompanyIDNO",appDataRegisterApplication.getIDNO());

                        SPFHelp.getInstance().putStrings(licenseData);

                        if(appDataRegisterApplication.getURI() != null && !appDataRegisterApplication.getURI().equals("") && appDataRegisterApplication.getURI().length() > 5) {
                            long nowDate = new Date().getTime();

                            String serverStringDate = appDataRegisterApplication.getServerDateTime();
                            serverStringDate = serverStringDate.replace("/Date(","");
                            serverStringDate = serverStringDate.replace("+0200)/","");
                            serverStringDate = serverStringDate.replace("+0300)/","");

                            long serverDate = Long.parseLong(serverStringDate);

                            SPFHelp.getInstance().putString("URI", appDataRegisterApplication.getURI());
                            SPFHelp.getInstance().putLong("DateReceiveURI", nowDate);
                            SPFHelp.getInstance().putLong("ServerDateTime", serverDate);

                        }else{
                            new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                    .setTitle("URL not set!")
                                    .setMessage("The application is not fully configured.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialogInterface, i) -> {
                                        finish();
                                    })
                                    .setNegativeButton("Retry",((dialogInterface, i) -> {
                                        getURI(licenseID);
                                    }))
                                    .show();
                        }
                    }
                    else if(result.getErrorCode() == 133){

                        Map<String,String> licenseData = new HashMap<>();
                        licenseData.put("LicenseID", null);
                        licenseData.put("LicenseCode", null);
                        licenseData.put("CompanyName", null);
                        licenseData.put("CompanyIDNO", null);

                        SPFHelp.getInstance().putStrings(licenseData);
                        SPFHelp.getInstance().putBoolean("KeepMeSigned", false);

                        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setTitle("Application not activated!")
                                .setMessage("The application is not activated! Please activate it can you continue.")
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialogInterface, i) -> {
                                    finish();
                                })
                                .show();
                    }
                    else if(result.getErrorCode() == 134){
                        Map<String,String> licenseData = new HashMap<>();
                        licenseData.put("LicenseID", null);
                        licenseData.put("LicenseCode", null);
                        licenseData.put("CompanyName", null);
                        licenseData.put("CompanyIDNO", null);

                        SPFHelp.getInstance().putStrings(licenseData);

                        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setTitle("License not activated!")
                                .setMessage("The license for this application not activated! Please activate it can you continue.")
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialogInterface, i) -> {
                                    startActivity(new Intent(context, AuthorizeActivity.class));
                                    finish();
                                })
                                .show();
                    }
                    else if(result.getErrorCode() == 124){
                        Map<String,String> licenseData = new HashMap<>();
                        licenseData.put("LicenseID", null);
                        licenseData.put("LicenseCode", null);
                        licenseData.put("CompanyName", null);
                        licenseData.put("CompanyIDNO", null);

                        SPFHelp.getInstance().putStrings(licenseData);

                        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setTitle("License not exist!")
                                .setMessage("The license for this application not exist! Please enter valid license can continue.")
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialogInterface, i) -> {
                                    startActivity(new Intent(context, AuthorizeActivity.class));
                                    finish();
                                })
                                .show();
                    }

                    else {
                        Toast.makeText(context, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterApplication> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
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