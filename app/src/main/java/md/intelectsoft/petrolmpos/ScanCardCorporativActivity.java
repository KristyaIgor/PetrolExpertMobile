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
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolmpos.Utils.SPFHelp;
import md.intelectsoft.petrolmpos.network.pe.PERetrofitClient;
import md.intelectsoft.petrolmpos.network.pe.PEServiceAPI;
import md.intelectsoft.petrolmpos.network.pe.result.AssortmentCard;
import md.intelectsoft.petrolmpos.network.pe.result.AssortmentCardSerializable;
import md.intelectsoft.petrolmpos.network.pe.result.GetCardInfo;
import md.intelectsoft.petrolmpos.network.pe.result.GetCardInfoSerializable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class ScanCardCorporativActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    PEServiceAPI peServiceAPI;
    private boolean isVerifone;
    private Context context;
    private String deviceId;

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];

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

        }
        else{
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter == null){
                Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            }

            if(!nfcAdapter.isEnabled()){
                new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                    .setTitle("Attention!")
                    .setMessage("Turn on the NFC")
                    .setCancelable(false)
                    .setPositiveButton("Turn On", (dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel",((dialogInterface, i) -> {

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

            if (nfcAdapter != null) {
                nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
            }
        }
    }

    private void getCardInfoPEC (String cardId){
        Call<GetCardInfo> call = peServiceAPI.getCardInfo(deviceId, cardId);

        progressDialog.setMessage("Load assortment...");
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
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();
                        }

                    }else{
                        progressDialog.dismiss();
                        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setTitle("Attention!")
                                .setMessage("Error check code! Message: " + getCardInfo.getErrorMessage() + ". Error code: " + getCardInfo.getErrorCode())
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialogInterface, i) -> {
                                  finish();
                                })
                                .setNegativeButton("Retry",((dialogInterface, i) -> {
                                    getCardInfoPEC(cardId);
                                }))
                                .show();
                    }
                }
                else{
                    progressDialog.dismiss();
                    new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                            .setTitle("Attention!")
                            .setMessage("Error check MyDiscount code! Response is empty!")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialogInterface, i) -> {
                                finish();
                            })
                            .setNegativeButton("Retry",((dialogInterface, i) -> {
                                getCardInfoPEC(cardId);
                            }))
                            .show();
                }

            }


            @Override
            public void onFailure(Call<GetCardInfo> call, Throwable t) {
                progressDialog.dismiss();
                new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setTitle("Attention!")
                        .setMessage("Failure check code! Message: " + t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            finish();
                        })
                        .setNegativeButton("Retry",((dialogInterface, i) -> {
                            getCardInfoPEC(cardId);
                        }))
                        .show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 201){
            if(permissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startActivityForResult(new Intent(context, ScannedBarcodeActivity.class), 121);
        }
    }
}