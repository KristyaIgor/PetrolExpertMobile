package md.intelectsoft.petrolmpos;

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
import java.util.ArrayList;
import java.util.List;

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
            Log.d("TAG", msg.getData().getString("msg"));
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
                GetCardInfo getAssortment = response.body();
                if(getAssortment != null){
                    if(getAssortment.getErrorCode() == 0){
                        if(getAssortment.getAssortiment() != null && getAssortment.getAssortiment().size() > 0){
                            List<AssortmentCardSerializable> assortmentSerializables = new ArrayList<>();
                            for (AssortmentCard item : getAssortment.getAssortiment()){
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
                                    getAssortment.getAllowedBalance(),
                                    assortmentSerializables,
                                    getAssortment.getBalance(),
                                    getAssortment.getBlockedAmount(),
                                    getAssortment.getCardEnabled(),
                                    getAssortment.getCardName(),
                                    getAssortment.getCardNumber(),
                                    getAssortment.getCustomerEnabled(),
                                    getAssortment.getCustomerId(),
                                    getAssortment.getCustomerName(),
                                    getAssortment.getDailyLimit(),
                                    getAssortment.getDailyLimitConsumed(),
                                    getAssortment.getLimitType(),
                                    getAssortment.getMonthlyLimit(),
                                    getAssortment.getMonthlyLimitConsumed(),
                                    getAssortment.getPhone(),
                                    getAssortment.getRefusedRefillClientAccount(),
                                    getAssortment.getTankCapacity(),
                                    getAssortment.getWeeklyLimit(),
                                    getAssortment.getWeeklyLimitConsumed()
                            );

                            Intent intent = new Intent(ScannedBarcodeActivity.this, ClientMyDiscountCardCorporativActivity.class);
                            intent.putExtra("ResponseClient", cardInfoSerializable);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();
                        }

                    }else{
                        progressDialog.dismiss();
                        new MaterialAlertDialogBuilder(ScannedBarcodeActivity.this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setTitle("Attention!")
                                .setMessage("Error check MyDiscount code! Message: " + getAssortment.getErrorMessage())
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialogInterface, i) -> {
                                    counter = 0;
                                })
                                .setNegativeButton("Retry",((dialogInterface, i) -> {
                                    getCardInfoPEC(cardId);
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
                                getCardInfoPEC(cardId);
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
                            getCardInfoPEC(cardId);
                        }))
                        .show();
            }
        });
    }
}


