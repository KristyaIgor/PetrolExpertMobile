package md.intelectsoft.petrolmpos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolmpos.Utils.SPFHelp;
import md.intelectsoft.petrolmpos.network.pe.PERetrofitClient;
import md.intelectsoft.petrolmpos.network.pe.PEServiceAPI;
import md.intelectsoft.petrolmpos.network.pe.result.Assortment;
import md.intelectsoft.petrolmpos.network.pe.result.AssortmentSerializable;
import md.intelectsoft.petrolmpos.network.pe.result.GetAssortment;
import md.intelectsoft.petrolmpos.network.pe.result.GetAssortmentSerializable;
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
        startActivity(new Intent(context, ScanMyDiscountActivity.class));
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
                finish();
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
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

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
                        getAssortment(sb.toString());

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

                        getAssortment(sb.toString());

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
        if (NfcAdapter.getDefaultAdapter(this) != null) nfcAdapter.disableForegroundDispatch(this);
    }
    @Override
    public void onResume(){
        super.onResume();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
        }

    }
    private String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append("-");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString().toUpperCase();
    }

    private void getAssortment(String cardId){
        Call<GetAssortment> call = peServiceAPI.getAssortment(deviceId, cardId, "0" , "0");

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

        call.enqueue(new Callback<GetAssortment>() {
            @Override
            public void onResponse(Call<GetAssortment> call, Response<GetAssortment> response) {
                GetAssortment getAssortment = response.body();

                if(getAssortment != null){
                    if(getAssortment.getNoError()){
                        if(getAssortment.getAssortmentList() != null && getAssortment.getAssortmentList().size() > 0){
                            List<AssortmentSerializable> assortmentSerializables = new ArrayList<>();
                            for (Assortment item : getAssortment.getAssortmentList()){
                                AssortmentSerializable assortmentSerializable = new AssortmentSerializable(
                                        item.getCount(),
                                        item.getName(),
                                        item.getPrice(),
                                        item.getPriceLineID()
                                );

                                assortmentSerializables.add(assortmentSerializable);
                            }

                            GetAssortmentSerializable assortmentSerializable = new GetAssortmentSerializable(
                                    assortmentSerializables,
                                    getAssortment.getBalanta(),
                                    getAssortment.getClientAmount(),
                                    getAssortment.getClientName(),
                                    getAssortment.getCredit(),
                                    getAssortment.getLimitDay(),
                                    getAssortment.getLimitMount(),
                                    getAssortment.getLimitType(),
                                    getAssortment.getWeeklyLimit()
                            );

                            Intent intent = new Intent(context, ClientMyDiscountCardCorporativActivity.class);
                            intent.putExtra("ResponseClient", assortmentSerializable);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();
                        }

                    }else{
                        progressDialog.dismiss();
                        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setTitle("Attention!")
                                .setMessage("Error check MyDiscount code! Message: " + getAssortment.getErrorMessage())
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialogInterface, i) -> {
                                  finish();
                                })
                                .setNegativeButton("Retry",((dialogInterface, i) -> {
                                    getAssortment(cardId);
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
                                getAssortment(cardId);
                            }))
                            .show();
                }

            }


            @Override
            public void onFailure(Call<GetAssortment> call, Throwable t) {
                progressDialog.dismiss();
                new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setTitle("Attention!")
                        .setMessage("Failure check MyDiscount code! Message: " + t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            finish();
                        })
                        .setNegativeButton("Retry",((dialogInterface, i) -> {
                            getAssortment(cardId);
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