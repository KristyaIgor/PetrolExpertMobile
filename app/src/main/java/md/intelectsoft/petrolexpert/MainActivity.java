package md.intelectsoft.petrolexpert;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.remoteconfig.BuildConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolexpert.Utils.LocaleHelper;
import md.intelectsoft.petrolexpert.Utils.SPFHelp;
import md.intelectsoft.petrolexpert.enums.ShiftStateEnum;
import md.intelectsoft.petrolexpert.network.broker.Body.InformationData;
import md.intelectsoft.petrolexpert.network.broker.Body.SendGetURI;
import md.intelectsoft.petrolexpert.network.broker.BrokerRetrofitClient;
import md.intelectsoft.petrolexpert.network.broker.BrokerServiceAPI;
import md.intelectsoft.petrolexpert.network.broker.Enum.BrokerServiceEnum;
import md.intelectsoft.petrolexpert.network.broker.Results.AppDataRegisterApplication;
import md.intelectsoft.petrolexpert.network.broker.Results.ErrorMessage;
import md.intelectsoft.petrolexpert.network.broker.Results.RegisterApplication;
import md.intelectsoft.petrolexpert.network.pe.PERetrofitClient;
import md.intelectsoft.petrolexpert.network.pe.PEServiceAPI;
import md.intelectsoft.petrolexpert.network.pe.result.AssortmentSerializable;
import md.intelectsoft.petrolexpert.network.pe.result.GetCurrentShift;
import md.intelectsoft.petrolexpert.network.pe.result.RegisterDevice;
import md.intelectsoft.petrolexpert.network.pe.result.authorizeUser.GetAuthorizeUser;
import md.intelectsoft.petrolexpert.network.pe.result.stationSettings.AssortmentStation;
import md.intelectsoft.petrolexpert.network.pe.result.stationSettings.GetStationSettings;
import md.intelectsoft.petrolexpert.printeractivity.PrinterFonts;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.layoutApplyCard) ConstraintLayout layoutApplyCard;
    @BindView(R.id.textTerminalNumber) TextView terminalNumber;
    @BindView(R.id.textOperatorName) TextView terminalUser;
    @BindView(R.id.textCashWorkPlaceMain) TextView terminalCashName;


    String androidID, deviceName, publicIp, privateIp, deviceSN, osVersion, deviceModel, deviceId, updateUrl;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    TimeZone timeZone = TimeZone.getTimeZone("Europe/Chisinau");
    ProgressDialog progressDialog;
    BrokerServiceAPI brokerServiceAPI;
    PEServiceAPI peServiceAPI;
    Context context;

    @OnClick(R.id.buttonScanCardCorporativ) void onScanCardCorp(){
        startActivity(new Intent(context, ScanCardCorporativActivity.class));
    }

    @OnClick(R.id.buttonScanMyDiscount) void onScanMyDiscount(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            Intent myDisc = new Intent(context,ScanMyDiscountActivity.class);
            myDisc.putExtra("isDisc", true);
            startActivityForResult(myDisc, 122);
        }
        else
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 221);
    }

    @OnClick(R.id.buttonScanWithoutIdentify) void onScanWithoutIdentify(){
        if(4 + 4 == 5){
            Call<GetStationSettings> getAssortmentCall = peServiceAPI.getStationSettings(deviceId);
            progressDialog.setMessage(getString(R.string.load_products_dialot_msg));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setButton(-1, getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
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
                                        .setTitle(getString(R.string.attention_dialog_title))
                                        .setMessage("List of products is empty!")
                                        .setCancelable(false)
                                        .setPositiveButton(getString(R.string.ok_button), null)
                                        .show();
                            }
                        }
                        else{
                            progressDialog.dismiss();
                            new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                    .setTitle(getString(R.string.attention_dialog_title))
                                    .setMessage("Error load products! Message: " + getAssortment.getErrorMessage() + ". Error code: " + getAssortment.getErrorCode())
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.ok_button), null)
                                    .show();
                        }
                    }
                    else{
                        progressDialog.dismiss();
                        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setTitle(getString(R.string.attention_dialog_title))
                                .setMessage("Error load products! Response is empty!")
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
                                })
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<GetStationSettings> call, Throwable t) {
                    progressDialog.dismiss();
                    new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                            .setTitle(getString(R.string.attention_dialog_title))
                            .setMessage("Failure load products! Message: " + t.getMessage())
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {

                            })
                            .show();
                }
            });
        }
    }

    @OnClick(R.id.imageButtonLogout) void onPrintX(){
        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle(getString(R.string.attention_dialog_title))
                .setMessage(getString(R.string.change_user_msg))
                .setPositiveButton(R.string.yes_btn, (dialogInterface, i) -> {
                    SPFHelp.getInstance().putLong("TokenValid", 0);
                    SPFHelp.getInstance().putString("TokenId", null);
                    SPFHelp.getInstance().putString("Owner", null);
                    SPFHelp.getInstance().putString("UserCodeAuth", null);
                    SPFHelp.getInstance().putInt("RegisteredNumber", 0);
                    SPFHelp.getInstance().putBoolean("FirstStart", true);

                    startActivity(new Intent(context, AuthorizeActivity.class));
                    finish();
                })
                .setNegativeButton(getString(R.string.cancel_button), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    @OnClick(R.id.imageButtonInfo) void onInfo() {
        startActivityForResult(new Intent(context, InfoActivity.class), 245);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String lang = LocaleHelper.getLanguage(this);
        setAppLocale(lang);
        setContentView(R.layout.activity_main_v1);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        context = this;
        progressDialog = new ProgressDialog(context);
        brokerServiceAPI = BrokerRetrofitClient.getApiBrokerService();
        simpleDateFormat.setTimeZone(timeZone);

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
        terminalCashName.setText(SPFHelp.getInstance().getString("StationName","") + "/" + SPFHelp.getInstance().getString("Cash", ""));
        terminalNumber.setText(getString(R.string.nr_terminal) + SPFHelp.getInstance().getInt("RegisteredNumber",0));

        if(BaseApp.isVFServiceConnected())
            PrinterFonts.initialize(this.getAssets());
        else{
            FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(1)
                    .build();
            remoteConfig.setConfigSettingsAsync(configSettings);

            Map<String,Object> defaultValue = new HashMap<>();
            defaultValue.put("update_url", "");
            defaultValue.put("is_update",false);
            defaultValue.put("version", "1.0");

            remoteConfig.setDefaultsAsync(defaultValue);

            remoteConfig.fetchAndActivate().addOnCompleteListener( new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {
                    if(task.isSuccessful()){
                        Log.d("TAG", "remote config is fetched.");

                        boolean isUpdate = remoteConfig.getBoolean("is_update");
                        updateUrl = remoteConfig.getString("update_url");
                        String version = remoteConfig.getString("version");
                        String currentVersion = getAppVersion(context);

                        if(isUpdate && !version.equals(currentVersion)){
                            showDialogNewVersion(currentVersion, version, updateUrl);
                        }
                    }
                }
            });
        }

        if(SPFHelp.getInstance().getBoolean("DiagnosticStartSend", true))
            sendDiagnosticData();

    }

    private void showDialogNewVersion(String oldVersion, String newVersion, String url) {
        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle(getString(R.string.attention_dialog_title))
                .setCancelable(false)
                .setMessage(getString(R.string.existnewversion) + newVersion + getString(R.string.curentversion) + oldVersion + getString(R.string.youwantupdate))
                .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 4585);
                    }else{
                        downloadAndInstallApk(url);
                    }
                })
                .setNegativeButton(getString(R.string.cancel_button),((dialogInterface, i) -> {

                }))
                .show();
    }

    private void downloadAndInstallApk(String url){
        progressDialog.setMessage(getString(R.string.download_new_version));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setButton(-1, getString(R.string.cancel_button), (dialog, which) -> {
            dialog.dismiss();
        });
        progressDialog.show();
        //get destination to update file and set Uri
        //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
        //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better

//        boolean isWritabel = isExternalStorageWritable();
//
//        String destination = Environment.getExternalStorageDirectory() + "/IntelectSoft";
//
//        String fileName = "/eso1907a.jpg";
//        destination += fileName;
//        final Uri uri = Uri.parse("file://" + destination);

        File externalCacheFile = new File(context.getExternalCacheDir(), "petrolexpert.apk");
//
//        //Delete update file if exists
//        File file = new File(destination);
        if (externalCacheFile.exists())
            //file.delete() - test this, I think sometimes it doesnt work
            externalCacheFile.delete();

        //set download manager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(getString(R.string.download_new_version));
        request.setTitle(getString(R.string.petrol_update));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        //set destination
        request.setDestinationUri(Uri.fromFile(externalCacheFile));

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long ids = manager.enqueue(request);


        //set BroadcastReceiver to install app when .apk is downloaded
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                progressDialog.dismiss();
                File file = new File(context.getExternalCacheDir(),"petrolexpert.apk");// mention apk file path here
                long spaceKb = file.length()/1024;
                if(file.exists() && spaceKb > 1000){
                    Uri uri = FileProvider.getUriForFile(MainActivity.this, "md.intelectsoft.petrolexpert.provider",file);
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(uri, "application/vnd.android.package-archive");
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(install);

                    finish();
                }
                unregisterReceiver(this);
            }
        };
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void sendDiagnosticData() {
        JSONObject informationArray = new JSONObject();
        JSONObject battery = getBatteryInformation(this);
        JSONObject memory = getMemoryInformation();
        JSONObject cpu = getCPUInformation();
        JSONObject wifi = getWIFIInformation();

        try {

            informationArray.put("Battery", battery);
            informationArray.put("Memory", memory);
            informationArray.put("CPU", cpu);
            informationArray.put("WiFi", wifi);

            Log.e("TAG", "onNavigationItemSelected JSON array: " + informationArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String licenseID = SPFHelp.getInstance().getString("LicenseID", null);

        InformationData data = new InformationData();
        data.setLicenseID(licenseID);
        data.setInformation(informationArray.toString());

        Call<ErrorMessage> call = brokerServiceAPI.updateDiagnosticInfo(data);
        call.enqueue(new Callback<ErrorMessage>() {
            @Override
            public void onResponse(Call<ErrorMessage> call, Response<ErrorMessage> response) {
                ErrorMessage message = response.body();
                if(message.getErrorCode() == 0) {
                    SPFHelp.getInstance().putBoolean("DiagnosticStartSend", true);
                    SPFHelp.getInstance().putString("LastDiagnosticData", simpleDateFormat.format(new Date().getTime()));
                }
            }

            @Override
            public void onFailure(Call<ErrorMessage> call, Throwable t) {
            }
        });
    }

    private void authorizeUser(String code) {
        Call<GetAuthorizeUser> call = peServiceAPI.authorizeUser(code);

        progressDialog.setMessage(getString(R.string.refresh_token_msg));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setButton(-1, getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
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
                        showErrorDialogAuthUser(getString(R.string.error_auth_user_msg) + getAuthorizeUser.getErrorMessage());
                }
                else showErrorDialogAuthUser(getString(R.string.error_auth_user_empty));
            }

            @Override
            public void onFailure(Call<GetAuthorizeUser> call, Throwable t) {
                progressDialog.dismiss();
                showErrorDialogAuthUser(getString(R.string.error_auth_user_failure) + t.getMessage());
            }
        });
    }

    private void showErrorDialogAuthUser (String text) {
        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle(getString(R.string.attention_dialog_title))
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {

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
                                showErrorDialogShiftState(getString(R.string.shift_closed_msg));
                            }
                            else if(getCurrentShift.getShiftState() == ShiftStateEnum.Elapsed){
                                showErrorDialogShiftState(getString(R.string.shift_elapsed_msg));
                            }
                            else{
                                showErrorDialogShiftState(getString(R.string.shift_state_msg) + getCurrentShift.getShiftState());
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
        else if( requestCode == 245){
            if(resultCode == RESULT_FIRST_USER){
                startActivity(new Intent(this, SplashActivity.class));
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 201){
            if(permissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent myDisc = new Intent(context,ScanMyDiscountActivity.class);
                myDisc.putExtra("isDisc", false);
                startActivityForResult(myDisc, 121);
            }
        }
        if(requestCode == 221){
            if(permissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent myDisc = new Intent(context,ScanMyDiscountActivity.class);
                myDisc.putExtra("isDisc", true);
                startActivityForResult(myDisc, 122);
            }
        }
        if(requestCode == 4585){
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                downloadAndInstallApk(updateUrl);
            }
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

    private void registerDevice() {
        Call<RegisterDevice> call = peServiceAPI.registerDevice(deviceId, "Android " + deviceModel, SPFHelp.getInstance().getString("CashId",""), SPFHelp.getInstance().getString("TokenId",""));

        progressDialog.setMessage(getString(R.string.check_device_msg));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setButton(-1, getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
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
                        terminalNumber.setText(getString(R.string.nr_terminal) + device.getRegistredNumber());
                        SPFHelp.getInstance().putInt("RegisteredNumber", device.getRegistredNumber());
                    }
                    else showErrorDialogRegisterDevice(getString(R.string.device_not_registered) + device.getErrorMessage());
                else showErrorDialogRegisterDevice(getString(R.string.device_not_registered_not_response));
            }

            @Override
            public void onFailure(Call<RegisterDevice> call, Throwable t) {
                progressDialog.dismiss();
                showErrorDialogRegisterDevice(getString(R.string.device_not_registered_failure) + t.getMessage());
            }
        });
    }

    private void showErrorDialogRegisterDevice(String text) {
        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle(getString(R.string.attention_dialog_title))
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
                    finish();
                })
                .setNegativeButton(getString(R.string.retry_button),((dialogInterface, i) -> {
                    registerDevice();
                }))
                .show();
    }

    private void showErrorDialogShiftState(String text) {
        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle(getString(R.string.attention_dialog_title))
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
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
                    checkApplicationUserAvailable();
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
                            checkApplicationUserAvailable();

                        }else{
                            new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                    .setTitle(getString(R.string.url_not_set_title))
                                    .setMessage(getString(R.string.app_not_configured))
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
                                        finish();
                                    })
                                    .setNegativeButton(getString(R.string.retry_button),((dialogInterface, i) -> {
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
                        SPFHelp.getInstance().putString("CashId", null);

                        SPFHelp.getInstance().putStrings(licenseData);
                        SPFHelp.getInstance().putBoolean("KeepMeSigned", false);

                        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setTitle(getString(R.string.app_not_activated))
                                .setMessage(getString(R.string.aplication_not_activated_msg))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
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
                        SPFHelp.getInstance().putString("CashId", null);

                        SPFHelp.getInstance().putStrings(licenseData);

                        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setTitle(getString(R.string.license_not_activated))
                                .setMessage(getString(R.string.license_not_active_msg))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
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
                        SPFHelp.getInstance().putString("CashId", null);

                        SPFHelp.getInstance().putStrings(licenseData);

                        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setTitle(getString(R.string.license_not_exist))
                                .setMessage(getString(R.string.license_not_exist_msg))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
                                    startActivity(new Intent(context, AuthorizeActivity.class));
                                    finish();
                                })
                                .show();
                    }
                    else {
                        Toast.makeText(context, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        checkApplicationUserAvailable();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterApplication> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                checkApplicationUserAvailable();
            }
        });
    }

    private void checkApplicationUserAvailable() {
        //TODO check app id can work if not response from broker service

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

    private void setAppLocale(String localeCode){
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
    }

    private JSONObject getBatteryInformation(Context context) {
        JSONObject battery = new JSONObject();

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        double voltage = (double) batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        int plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        double temperature = (double) batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        String technology = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);


        try {
            battery.put("Level", level);
            battery.put("Voltage", round(voltage/1000, 2));
            battery.put("Plugged", plugged);
            battery.put("Status", status);
            battery.put("Health", health);
            battery.put("Temperature", round(temperature/10, 2));
            battery.put("Technology", technology);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return battery;
    }
    private JSONObject getMemoryInformation(){
        JSONObject memory = new JSONObject();

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        double installed = mi.totalMem / 0x100000L;
        double available = mi.availMem / 0x100000L;
        double used = (installed  - available);

        try {
            memory.put("Installed", installed + " MB");
            memory.put("Free", available + " MB");
            memory.put("Used", used + " MB");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return memory;
    }
    private JSONObject getCPUInformation(){
        JSONObject cpu = new JSONObject();


        return cpu;
    }
    private JSONObject getWIFIInformation(){
        JSONObject wifi = new JSONObject();

        WifiManager wifiManager = (WifiManager) context.getSystemService (Context.WIFI_SERVICE);
        int state = wifiManager.getWifiState();

        switch (state){
            case WifiManager.WIFI_STATE_ENABLED :{

            }
        }

        WifiInfo info = wifiManager.getConnectionInfo();

        String ssid = info.getSSID();

        Log.e("TAG", "getWIFIInformation: " + ssid );

        return wifi;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}