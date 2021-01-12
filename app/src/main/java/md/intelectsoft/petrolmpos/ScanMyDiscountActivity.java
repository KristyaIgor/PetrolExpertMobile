package md.intelectsoft.petrolmpos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolmpos.Utils.SPFHelp;
import md.intelectsoft.petrolmpos.network.broker.BrokerServiceAPI;
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
public class ScanMyDiscountActivity extends AppCompatActivity {

    @BindView(R.id.surfaceViewMyDiscount) SurfaceView surfaceView;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private Context context;

    private boolean isVerifone;
    private String deviceId;
    private int counter = 0;

    ProgressDialog progressDialog;
    PEServiceAPI peServiceAPI;

    @OnClick(R.id.layoutCloseScanMyDiscountActivity) void onExit(){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_my_discount);
        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        context = this;
        progressDialog = new ProgressDialog(context);

        deviceId = SPFHelp.getInstance().getString("deviceId", "");
        String uri = SPFHelp.getInstance().getString("URI", null);
        peServiceAPI = PERetrofitClient.getPEService(uri);

        isVerifone = BaseApp.isVFServiceConnected();
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

                            Intent intent = new Intent(ScanMyDiscountActivity.this, ClientMyDiscountCardCorporativActivity.class);
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
                                    counter = 0;
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
                                counter = 0;
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
                            counter = 0;
                        })
                        .setNegativeButton("Retry",((dialogInterface, i) -> {
                            getAssortment(cardId);
                        }))
                        .show();
            }
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
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
//                 Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
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
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("TAG", msg.getData().getString("msg"));
            Toast.makeText(context, msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
            getAssortment(msg.getData().getString("msg"));
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(!isVerifone)
            cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isVerifone)
            initialiseDetectorsAndSources();
    }
}