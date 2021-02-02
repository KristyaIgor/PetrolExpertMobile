package md.intelectsoft.petrolexpert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

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
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import md.intelectsoft.petrolexpert.Utils.LocaleHelper;
import md.intelectsoft.petrolexpert.Utils.SPFHelp;
import md.intelectsoft.petrolexpert.network.broker.Body.InformationData;
import md.intelectsoft.petrolexpert.network.broker.BrokerRetrofitClient;
import md.intelectsoft.petrolexpert.network.broker.BrokerServiceAPI;
import md.intelectsoft.petrolexpert.network.broker.Results.ErrorMessage;
import md.intelectsoft.petrolexpert.network.pe.body.SetFiscalBody;
import md.intelectsoft.petrolexpert.network.pe.body.registerBill.BillRegistered;
import md.intelectsoft.petrolexpert.network.pe.result.SetFiscal;
import md.intelectsoft.petrolexpert.realm.FiscalKey;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class InfoActivity extends AppCompatActivity {
    @BindView(R.id.textCompanyName) TextView companyName;
    @BindView(R.id.textLicenseCode) TextView licenseCode;
    @BindView(R.id.textTerminalNumber) TextView terminalNr;
    @BindView(R.id.textUserName) TextView userName;
    @BindView(R.id.textCashWorkPlace) TextView cashWorkPlace;
    @BindView(R.id.textIDNO) TextView companyIdno;
    @BindView(R.id.imgButtonsSetFiscal) ImageButton setAsFiscal;
    @BindView(R.id.textFicsalCode) TextView fiscalCode;

    @BindView(R.id.langEnButton) RadioButton langEN;
    @BindView(R.id.langRoButton) RadioButton langRO;
    @BindView(R.id.langRuButton) RadioButton langRU;

    @BindView(R.id.textAppVersion) TextView appVersion;
    @BindView(R.id.textLastSendDiagnosticInfo) TextView lastDiagnosticData;
    @BindView(R.id.textBillCounter) TextView billCounter;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    TimeZone timeZone = TimeZone.getTimeZone("Europe/Chisinau");

    ProgressDialog progressDialog;
    BrokerServiceAPI brokerServiceAPI;
    Context context;
    String updateUrl;


    @OnClick(R.id.layoutCountOfBill) void showBills(){
        startActivity(new Intent(this, BillListActivity.class));
    }

    @OnClick(R.id.imageBackToMain) void onBack(){
        finish();
    }

    @OnClick(R.id.layoutCheckVersionApp) void onCheckNewVersion(){
        if(!BaseApp.isVFServiceConnected()){
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
                        else
                            Toast.makeText(context, getString(R.string.this_latest_version), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @OnClick(R.id.layoutSendDiagnosticData) void onSendDiagnosticData(){
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
                    Toast.makeText(context, getString(R.string.diagnostic_report_send), Toast.LENGTH_SHORT).show();
                    SPFHelp.getInstance().putString("LastDiagnosticData", simpleDateFormat.format(new Date().getTime()));
                    lastDiagnosticData.setText(getString(R.string.last_send) + simpleDateFormat.format(new Date().getTime()));
                }
                else
                    Toast.makeText(context, getString(R.string.report_not_send) + message.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ErrorMessage> call, Throwable t) {
                Toast.makeText(context, getString(R.string.report_not_send) + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        context = this;
        progressDialog = new ProgressDialog(context);
        brokerServiceAPI = BrokerRetrofitClient.getApiBrokerService();
        simpleDateFormat.setTimeZone(timeZone);

        String company = SPFHelp.getInstance().getString("CompanyName", "");
        String idno = SPFHelp.getInstance().getString("CompanyIDNO", "");
        String license = SPFHelp.getInstance().getString("LicenseCode", "");
        String cash = SPFHelp.getInstance().getString("Cash", "");
        String station = SPFHelp.getInstance().getString("StationName", "");
        String owner = SPFHelp.getInstance().getString("Owner", "");
        int numberTerminal = SPFHelp.getInstance().getInt("RegisteredNumber", 0);

        companyName.setText(company);
        companyIdno.setText("IDNO: " + idno);
        licenseCode.setText(license);
        terminalNr.setText(String.valueOf(numberTerminal));
        userName.setText(owner);
        cashWorkPlace.setText(cash + "/" + station);

        String fiscalNumber = SPFHelp.getInstance().getString("FiscalCode", null);
        if(fiscalNumber == null)
            fiscalCode.setText("Device is not taxed");
        else {
            fiscalCode.setText(fiscalNumber);
            setAsFiscal.setVisibility(View.GONE);
        }

        String lang = LocaleHelper.getLanguage(this);

        if(lang.equals("en")){
            langEN.setChecked(true);
            langRO.setChecked(false);
            langRU.setChecked(false);
        }
        else if(lang.equals("ro")){
            langRO.setChecked(true);
            langEN.setChecked(false);
            langRU.setChecked(false);
        }
        else if(lang.equals("ru")){
            langRU.setChecked(true);
            langEN.setChecked(false);
            langRO.setChecked(false);
        }

        langEN.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                langRO.setChecked(false);
                langRU.setChecked(false);
                LocaleHelper.setLocale(this, "en");
                setResult(RESULT_FIRST_USER);
                finish();
            }
        });
        langRO.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                langEN.setChecked(false);
                langRU.setChecked(false);
                LocaleHelper.setLocale(this, "ro");
                setResult(RESULT_FIRST_USER);
                finish();
            }
        });
        langRU.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                langEN.setChecked(false);
                langRO.setChecked(false);
                LocaleHelper.setLocale(this, "ru");
                setResult(RESULT_FIRST_USER);
                finish();
            }
        });

        setAsFiscal.setOnClickListener(v -> {
            setAsFiscalDevice();
        });

        RealmResults<BillRegistered> bils = Realm.getDefaultInstance().where(BillRegistered.class).findAll();
        if(!bils.isEmpty())
            billCounter.setText(String.valueOf(bils.size()));

        appVersion.setText(getString(R.string.version) + getAppVersion(this));

        SPFHelp.getInstance().putString("LastDiagnosticData", simpleDateFormat.format(new Date().getTime()));
        lastDiagnosticData.setText(getString(R.string.last_send) + SPFHelp.getInstance().getString("LastDiagnosticData", getString(R.string.nevery)));
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

    private void setAsFiscalDevice(){
        SetFiscalBody fiscalBody = new SetFiscalBody();

        String licenseId = SPFHelp.getInstance().getString("LicenseID", null);
        String activationCode = SPFHelp.getInstance().getString("LicenseActivationCode", null);

        fiscalBody.setLicenseActivationCode(activationCode);
        fiscalBody.setLicenseID(licenseId);

        if(licenseId != null && activationCode != null){
            Call<SetFiscal> call = brokerServiceAPI.setAsFiscal(fiscalBody);
            progressDialog.setMessage("Set device as fiscal..");
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

            call.enqueue(new Callback<SetFiscal>() {
                @Override
                public void onResponse(Call<SetFiscal> call, Response<SetFiscal> response) {
                    SetFiscal fiscalResult = response.body();
                    progressDialog.dismiss();
                    if(fiscalResult != null){
                        if(fiscalResult.getErrorCode() == 0){
                            fiscalCode.setText(fiscalResult.getFiscalNumber());
                            setAsFiscal.setVisibility(View.GONE);

                            SPFHelp.getInstance().putString("FiscalCode", fiscalResult.getFiscalNumber());
                            FiscalKey fs = new FiscalKey();
                            fs.setKey(encrypt(fiscalResult.getKey().getBytes(), BaseApp.getApplication().getWordTime()));

                            Realm mRealm = Realm.getDefaultInstance();
                            mRealm.executeTransaction(realm -> {
                                realm.insert(fs);
                            });
                        }
                        else Toast.makeText(InfoActivity.this, "Error set as fiscal! Code: " + fiscalResult.getErrorCode() + " Message: " + fiscalResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    } else Toast.makeText(InfoActivity.this, "Empty result!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<SetFiscal> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(InfoActivity.this, "Error set as fiscal! Message: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static byte[] encrypt(byte[] plaintext, byte[] wordTime){
        byte[] IV = new byte[16];
        Cipher cipher = null;
        byte[] cipherText = null;
        try {
            cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(wordTime, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            cipherText = cipher.doFinal(plaintext);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return cipherText;
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
                    Uri uri = FileProvider.getUriForFile(InfoActivity.this, "md.intelectsoft.petrolexpert.provider",file);
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
}