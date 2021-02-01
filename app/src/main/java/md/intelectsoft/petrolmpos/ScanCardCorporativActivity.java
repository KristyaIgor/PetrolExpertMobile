package md.intelectsoft.petrolmpos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.vfi.smartpos.deviceservice.aidl.IRFCardReader;
import com.vfi.smartpos.deviceservice.aidl.RFSearchListener;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolmpos.Utils.LocaleHelper;
import md.intelectsoft.petrolmpos.Utils.SPFHelp;
import md.intelectsoft.petrolmpos.network.pe.PERetrofitClient;
import md.intelectsoft.petrolmpos.network.pe.PEServiceAPI;
import md.intelectsoft.petrolmpos.network.pe.result.AssortmentCard;
import md.intelectsoft.petrolmpos.network.pe.result.AssortmentCardSerializable;
import md.intelectsoft.petrolmpos.network.pe.result.GetCardInfo;
import md.intelectsoft.petrolmpos.network.pe.result.GetCardInfoSerializable;
import md.intelectsoft.petrolmpos.verifone.Utilities.DeviceHelper;
import md.intelectsoft.petrolmpos.verifone.transaction.TransBasic;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class ScanCardCorporativActivity extends AppCompatActivity {
    @BindView(R.id.progressBarTimeScanCard) ProgressBar progressBarScanCard;

    ProgressDialog progressDialog;
    PEServiceAPI peServiceAPI;
    private boolean isVerifone;
    private Context context;
    private String deviceId;

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];

    IRFCardReader irfCardReader;

    //S50卡
    public final static int S50_CARD = 0x00;
    //S70卡
    public final static int S70_CARD = 0x01;
    //CPU卡
    public final static int CPU_CARD = 0x05;

    int interval = 30;

    @OnClick(R.id.imageScanCameraCardCorp) void onScanCamera(){
        Intent scanIntent = new Intent(context, ScanMyDiscountActivity.class);
        scanIntent.putExtra("isDisc", false);
        startActivity(scanIntent);
    }

    @OnClick(R.id.layoutCloseScanCardCorpActivity) void onCloseActivity() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String lang = LocaleHelper.getLanguage(this);
        setAppLocale(lang);
        setContentView(R.layout.activity_scan_card_corporativ);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        context = this;
        progressDialog = new ProgressDialog(context);

        deviceId = SPFHelp.getInstance().getString("deviceId", "");
        String uri = SPFHelp.getInstance().getString("URI", null);
        peServiceAPI = PERetrofitClient.getPEService(uri);

        isVerifone = BaseApp.isVFServiceConnected();
        if(isVerifone){

            try {
                irfCardReader = BaseApp.getApplication().getDeviceService().getRFCardReader();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            try {

                irfCardReader.searchCard(rfSearchListener, 30);
                startProgressBar();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        else{
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter == null){
                Toast.makeText(this, getString(R.string.device_not_supported_nfc), Toast.LENGTH_LONG).show();
            }

            if(!nfcAdapter.isEnabled()){
                new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                    .setTitle(getString(R.string.attention_dialog_title))
                    .setMessage(R.string.turn_on_nfc_msg)
                    .setCancelable(false)
                    .setPositiveButton(R.string.turn_on_button, (dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        startActivity(intent);
                    })
                    .setNegativeButton(getString(R.string.cancel_button),((dialogInterface, i) -> {

                    }))
                    .show();
            }

            readFromIntent(getIntent());

            pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
            writeTagFilters = new IntentFilter[] { tagDetected };

        }
    }

    private void startProgressBar() {
        progressBarScanCard.setMax(30000);
        new CountDownTimer(30000, 1) {

            public void onTick(long millisUntilFinished) {
                progressBarScanCard.setProgress((int)millisUntilFinished);
            }

            public void onFinish() {
                if(!isVerifone)
                    finish();
            }
        }.start();
    }

    RFSearchListener rfSearchListener = new RFSearchListener.Stub() {
        @Override
        public void onCardPass(int cardType) throws RemoteException {
            if (S50_CARD == cardType || S70_CARD == cardType) {
                Log.e("TAG",  "M1 card @ " + cardType);
                BaseApp.getApplication().getDeviceService().getBeeper().startBeep(200);
                readRFData();
            } else if (CPU_CARD == cardType) {
                Log.e("TAG",  "CPU card");
            }
        }

        @Override
        public void onFail(int error, String message) throws RemoteException {

            Log.i("TAG", "Check card fail+ error code:" + error + "error message :" + message);

            if(error == 167){
                finish();
            }
        }
    };

    public void readRFData() {
        byte[] buffer = new byte[16];
        int i = 0;

        byte[] key = new byte[6];
        for (i = 0; i < 6; i++)
            key[i] = (byte) 0xFF;

        try {
            for (i = 0; i < 1; i++) {
                int ret = irfCardReader.authBlock(i, 0, key);
                if (ret < 0) {
                    Log.d("TAG", "authBlock FAILS:" + ret);
                } else {
                    Log.d("TAG", "authBlock OK:" + ret);
                }

                ret = irfCardReader.readBlock(i, buffer);
                if (0 == ret) {
                    StringBuilder sb = new StringBuilder();
                    for (byte page : buffer) {
                        int b = page & 0xff;
                        if (b < 0x10)
                            sb.append("");
                        sb.append(b);
                    }
                    Log.d("NFC", "Mifare Classic " + sb.toString());
                    Log.d("TAG", "readData: success:" + toHexString(buffer) + " @ " + i);

                    Message msg = new Message();
                    msg.getData().putString("msg", sb.toString());
                    handler.sendMessage(msg);

                } else {
                    Log.d("TAG", "readData: fail:" + ret + " @ " + i);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if (tagFromIntent!=null) {
                MifareUltralight mUltra = MifareUltralight.get(tagFromIntent);
                if (mUltra != null) {
                    try {
                        mUltra.connect();
                        StringBuilder sb = new StringBuilder();
                        byte[] pages = mUltra.readPages(0);
                        for (byte page : pages) {
                            int b = page & 0xff;
                            if (b < 0x10)
                                sb.append("");
                            sb.append(b);
                        }
                        Log.d("NFC", "MifareUltralight " + sb.toString());

                        byte[] id = tagFromIntent.getId();
                        getCardInfoPEC(sb.toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            mUltra.close();
                            Log.d("NFC", "MifareUltralight disconected");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                boolean auth = false;
                MifareClassic mfc = MifareClassic.get(tagFromIntent);
                if (mfc!=null) {
                    try {
                        String metaInfo = "";
                        //Enable I/O operations to the tag from this TagTechnology object.
                        mfc.connect();
//                    int type = mfc.getType();
//                    int sectorCount = mfc.getSectorCount();
//                    String typeS = "";
//                    switch (type) {
//                        case MifareClassic.TYPE_CLASSIC:
//                            typeS = "TYPE_CLASSIC";
//                            break;
//                        case MifareClassic.TYPE_PLUS:
//                            typeS = "TYPE_PLUS";
//                            break;
//                        case MifareClassic.TYPE_PRO:
//                            typeS = "TYPE_PRO";
//                            break;
//                        case MifareClassic.TYPE_UNKNOWN:
//                            typeS = "TYPE_UNKNOWN";
//                            break;
//                    }
//                    metaInfo += "Card type：" + typeS + "\n with" + sectorCount + " Sectors\n, "
//                            + mfc.getBlockCount() + " Blocks\nStorage Space: " + mfc.getSize() + "B\n";
                        StringBuilder sb = new StringBuilder();
                        auth = mfc.authenticateSectorWithKeyA(0, MifareClassic.KEY_DEFAULT);
                        int bCount;
                        int bIndex;

                        if (auth) {
                            //metaInfo += "Sector " + j + ": Verified successfullyn";
                            //bCount = mfc.getBlockCountInSector(0);
                            //bIndex = mfc.sectorToBlock(0);
//                            for (int i = 0; i < 1; i++) {
                            byte[] data = mfc.readBlock(0);

                            for (byte page : data) {
                                int b = page & 0xff;
                                if (b < 0x10)
                                    sb.append("");
                                sb.append(b);
                            }
//                                bIndex++;
//                            }
                        }
                        else {
                            metaInfo += "Sector " + 0 + ": Verified failure\n";
                            Log.d("Error NFC", metaInfo);
                        }
//                    }
                        Log.d("NFC", "MifareClassic " + sb.toString());

                        Log.d("NFC", "MifareClassic Reverse hex " + toHexString(mfc.readBlock(0)));

                        byte[] id = tagFromIntent.getId();

                        getCardInfoPEC(sb.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            mfc.close();
                            Log.d("NFC", "MifareClassic disconected");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }else {
            Log.e("Error NFC", "Unknown intent " + intent);
        }
    }

    private String toHexString(byte[] buffer) {

        String bufferString = "";

        for (int i = 0; i < buffer.length; i++) {

            String hexChar = Integer.toHexString(buffer[i] & 0xFF);
            if (hexChar.length() == 1) {
                hexChar = "0" + hexChar;
            }

            bufferString += hexChar.toUpperCase() + " ";
        }

        return bufferString;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        readFromIntent(intent);
    }
    @Override
    public void onPause(){
        super.onPause();
        if(!isVerifone){
            if (NfcAdapter.getDefaultAdapter(this) != null) nfcAdapter.disableForegroundDispatch(this);
        }

    }
    @Override
    public void onResume(){
        super.onResume();
        if(!isVerifone){
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);

            if (nfcAdapter != null && nfcAdapter.isEnabled()) {
                nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
                startProgressBar();
            }
        }
    }

    private void getCardInfoPEC (String cardId){


        Call<GetCardInfo> call = peServiceAPI.getCardInfoByBarcode(deviceId, getMD5HashCardCode(cardId));

        progressDialog.setMessage(getString(R.string.load_assortment_pg));
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

        call.enqueue(new Callback<GetCardInfo>() {
            @Override
            public void onResponse(Call<GetCardInfo> call, Response<GetCardInfo> response) {
                GetCardInfo getCardInfo = response.body();

                if(getCardInfo != null){
                    if(getCardInfo.getErrorCode() == 0){
                        if(getCardInfo.getAssortiment() != null && getCardInfo.getAssortiment().size() > 0){
                            List<AssortmentCardSerializable> assortmentSerializables = new ArrayList<>();
                            for (AssortmentCard item : getCardInfo.getAssortiment()){
                                AssortmentCardSerializable assortmentSerializable = new AssortmentCardSerializable(
                                        item.getAssortimentID(),
                                        item.getAssortmentCode(),
                                        item.getDiscount(),
                                        item.getName(),
                                        item.getPrice(),
                                        item.getPriceLineID(),
                                        item.getAdditionalLimit(),
                                        item.getCardBalance(),
                                        item.getDailyLimit(),
                                        item.getDailyLimitConsumed(),
                                        item.getLimit(),
                                        item.getMonthlyLimit(),
                                        item.getMonthlyLimitConsumed(),
                                        item.getWeeklyLimit(),
                                        item.getWeeklyLimitConsumed()
                                );
                                assortmentSerializables.add(assortmentSerializable);
                            }

                            GetCardInfoSerializable cardInfoSerializable = new GetCardInfoSerializable(
                                    getCardInfo.getAllowedBalance(),
                                    assortmentSerializables,
                                    getCardInfo.getBalance(),
                                    getCardInfo.getBlockedAmount(),
                                    getCardInfo.getCardEnabled(),
                                    getCardInfo.getCardName(),
                                    getCardInfo.getCardNumber(),
                                    getCardInfo.getCustomerEnabled(),
                                    getCardInfo.getCustomerId(),
                                    getCardInfo.getCustomerName(),
                                    getCardInfo.getDailyLimit(),
                                    getCardInfo.getDailyLimitConsumed(),
                                    getCardInfo.getLimitType(),
                                    getCardInfo.getMonthlyLimit(),
                                    getCardInfo.getMonthlyLimitConsumed(),
                                    getCardInfo.getPhone(),
                                    getCardInfo.getRefusedRefillClientAccount(),
                                    getCardInfo.getTankCapacity(),
                                    getCardInfo.getWeeklyLimit(),
                                    getCardInfo.getWeeklyLimitConsumed()
                            );

                            Intent intent = new Intent(context, ClientMyDiscountCardCorporativActivity.class);
                            intent.putExtra("ResponseClient", cardInfoSerializable);
                            intent.putExtra("ClientCardCode", getMD5HashCardCode(cardId));
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();
                        }

                    }else{
                        progressDialog.dismiss();
                        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setTitle(getString(R.string.attention_dialog_title))
                                .setMessage(getString(R.string.error_check_code_msg) + getCardInfo.getErrorMessage() + getString(R.string.err_code) + getCardInfo.getErrorCode())
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
                                  finish();
                                })
                                .setNegativeButton(getString(R.string.retry_button),((dialogInterface, i) -> {
                                    getCardInfoPEC(cardId);
                                }))
                                .show();
                    }
                }
                else{
                    progressDialog.dismiss();
                    new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                            .setTitle(getString(R.string.attention_dialog_title))
                            .setMessage(getString(R.string.error_check_my_discount))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
                                finish();
                            })
                            .setNegativeButton(getString(R.string.retry_button),((dialogInterface, i) -> {
                                getCardInfoPEC(cardId);
                            }))
                            .show();
                }

            }


            @Override
            public void onFailure(Call<GetCardInfo> call, Throwable t) {
                progressDialog.dismiss();
                new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setTitle(getString(R.string.attention_dialog_title))
                        .setMessage(getString(R.string.fail_check_code) + t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
                            finish();
                        })
                        .setNegativeButton(getString(R.string.retry_button),((dialogInterface, i) -> {
                            getCardInfoPEC(cardId);
                        }))
                        .show();
            }
        });
    }

    public static String getMD5HashCardCode(String message) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.reset();
        m.update(message.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        String hashtext = bigInt.toString(16);
// Now we need to zero pad it if you actually want the full 32 chars.
        while(hashtext.length() < 32 ){
            hashtext = "0"+hashtext;
        }
        return hashtext;
    }

    Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("TAG", msg.getData().getString("msg"));
            getCardInfoPEC(msg.getData().getString("msg"));
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 201){
            if(permissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startActivityForResult(new Intent(context, ScannedBarcodeActivity.class), 121);
        }
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
}