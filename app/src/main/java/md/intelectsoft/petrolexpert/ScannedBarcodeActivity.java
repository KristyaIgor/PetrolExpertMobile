package md.intelectsoft.petrolexpert;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import md.intelectsoft.petrolexpert.Utils.SPFHelp;
import md.intelectsoft.petrolexpert.network.pe.PERetrofitClient;
import md.intelectsoft.petrolexpert.network.pe.PEServiceAPI;
import md.intelectsoft.petrolexpert.network.pe.result.AssortmentCard;
import md.intelectsoft.petrolexpert.network.pe.result.AssortmentCardSerializable;
import md.intelectsoft.petrolexpert.network.pe.result.GetCardInfo;
import md.intelectsoft.petrolexpert.network.pe.result.GetCardInfoSerializable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static md.intelectsoft.petrolexpert.ClientMyDiscountCardCorporativActivity.round;

public class ScannedBarcodeActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private ConstraintLayout layoutExit;
    private int counter = 0;
    private String deviceId;

    ProgressDialog progressDialog;
    PEServiceAPI peServiceAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        surfaceView = findViewById(R.id.surfaceView);
        layoutExit = findViewById(R.id.layoutCloseScanActivity);

        progressDialog = new ProgressDialog(this);

        deviceId = SPFHelp.getInstance().getString("deviceId", "");
        String uri = SPFHelp.getInstance().getString("URI", null);
        peServiceAPI = PERetrofitClient.getPEService(uri);


        layoutExit.setOnClickListener(v -> {
            finish();
        });
    }


    private void initialiseDetectorsAndSources() {
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(460, 460)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    cameraSource.start(surfaceView.getHolder());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
               // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    if(counter == 0) {
                        String barcode = barcodes.valueAt(0).displayValue;
                        Message msg = new Message();
                        msg.getData().putString("msg", barcode);
                        counter = 1;
                        handler.sendMessage(msg);
                    }
                }
            }
        });
    }

    Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("PetrolExpert_BaseApp", msg.getData().getString("msg"));
            getCardInfoPEC(msg.getData().getString("msg"));
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    private void getCardInfoPEC(String cardId){
        Call<GetCardInfo> call = peServiceAPI.getCardInfoByBarcode(deviceId, cardId);

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
                                        item.getPriceDiscounted() == 0 ? item.getPrice() : item.getPriceDiscounted(),
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
                                        item.getWeeklyLimitConsumed(),
                                        item.getVatPercent());
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

                            Intent intent = new Intent(ScannedBarcodeActivity.this, ClientMyDiscountCardCorporativActivity.class);
                            intent.putExtra("ResponseClient", cardInfoSerializable);
                            intent.putExtra("ClientCardCode", getCardInfo.getCardBarcode());
                            intent.putExtra("ClientCardName", getCardInfo.getCardNumber() + "/" + getCardInfo.getCardName());
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();
                        }

                    }else{
                        progressDialog.dismiss();
                        new MaterialAlertDialogBuilder(ScannedBarcodeActivity.this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setTitle("Attention!")
                                .setMessage("Error check MyDiscount code! Message: " + getCardInfo.getErrorMessage())
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialogInterface, i) -> {
                                    counter = 0;
                                })
                                .setNegativeButton("Retry",((dialogInterface, i) -> {
                                    getCardInfoPEC(getCardInfo.getCardBarcode());
                                }))
                                .show();
                    }
                }
                else{
                    progressDialog.dismiss();
                    new MaterialAlertDialogBuilder(ScannedBarcodeActivity.this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                            .setTitle("Attention!")
                            .setMessage("Error check MyDiscount code! Response is empty!")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialogInterface, i) -> {
                                counter = 0;
                            })
                            .setNegativeButton("Retry",((dialogInterface, i) -> {
                                getCardInfoPEC(getCardInfo.getCardBarcode());
                            }))
                            .show();
                }

            }


            @Override
            public void onFailure(Call<GetCardInfo> call, Throwable t) {
                progressDialog.dismiss();
                new MaterialAlertDialogBuilder(ScannedBarcodeActivity.this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setTitle("Attention!")
                        .setMessage("Failure check MyDiscount code! Message: " + t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            counter = 0;
                        })
                        .setNegativeButton("Retry",((dialogInterface, i) -> {
                            getCardInfoPEC(getMD5HashCardCode(cardId));
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

}


