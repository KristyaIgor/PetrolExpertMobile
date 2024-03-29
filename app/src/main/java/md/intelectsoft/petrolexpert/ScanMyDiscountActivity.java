package md.intelectsoft.petrolexpert;

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
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.vfi.smartpos.deviceservice.aidl.IScanner;
import com.vfi.smartpos.deviceservice.aidl.ScannerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import md.intelectsoft.petrolexpert.Utils.SPFHelp;
import md.intelectsoft.petrolexpert.network.pe.PECErrorMessage;
import md.intelectsoft.petrolexpert.network.pe.PERetrofitClient;
import md.intelectsoft.petrolexpert.network.pe.PEServiceAPI;
import md.intelectsoft.petrolexpert.network.pe.result.AssortmentCard;
import md.intelectsoft.petrolexpert.network.pe.result.AssortmentCardSerializable;
import md.intelectsoft.petrolexpert.network.pe.result.GetCardInfo;
import md.intelectsoft.petrolexpert.network.pe.result.GetCardInfoSerializable;
import md.intelectsoft.petrolexpert.network.pe.result.stationSettings.EmployeesCard;
import md.intelectsoft.petrolexpert.verifone.Utilities.DeviceHelper;
import md.intelectsoft.petrolexpert.verifone.transaction.AppParams;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static md.intelectsoft.petrolexpert.ClientMyDiscountCardCorporativActivity.round;

@SuppressLint("NonConstantResourceId")
public class ScanMyDiscountActivity extends AppCompatActivity {

    @BindView(R.id.surfaceViewMyDiscount) SurfaceView surfaceView;
    @BindView(R.id.layoutScanCamera) ConstraintLayout constraintLayoutScanCamera;

    @BindView(R.id.textTitleActivity) TextView titleApp;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private Context context;
    Realm mRealm;

    private boolean isVerifone;
    private String deviceId;
    private int counter = 0;

    ProgressDialog progressDialog;
    PEServiceAPI peServiceAPI;

    String title;
    String titlePhone;

    IScanner iScanner;
    ScanMyDiscountActivity activity;

    @OnClick(R.id.layoutCloseScanMyDiscountActivity) void onExit(){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_my_discount);
        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        activity = ScanMyDiscountActivity.this;
        context = this;
        progressDialog = new ProgressDialog(context);

        title = getString(R.string.msg_put_my_discount_title);
        titlePhone = getString(R.string.msg_position_qr_code);

        deviceId = SPFHelp.getInstance().getString("deviceId", "");
        String uri = SPFHelp.getInstance().getString("URI", null);
        peServiceAPI = PERetrofitClient.getPEService(uri);
        mRealm = Realm.getDefaultInstance();

        boolean isDiscount = getIntent().getBooleanExtra("isDisc", false);

        if(!isDiscount){
            title = getString(R.string.please_show_qr);
            titlePhone = getString(R.string.position_code_barcode);
        }

        isVerifone = BaseApp.isVFServiceConnected();

        if(isVerifone) {
            constraintLayoutScanCamera.setVisibility(View.GONE);

            iScanner = DeviceHelper.getInstance().getScanner();

            Bundle bundle = new Bundle();
            bundle.putString("topTitleString", getString(R.string.scanning_verifone_title));
            bundle.putString("upPromptString", title);
//            bundle.putString("downPromptString", "Please show Bar code");
            bundle.putBoolean("showScannerBorder", AppParams.getInstance().isShowScanBorder());

            try {
                iScanner.startScan(bundle, 40, new ScannerListener.Stub() {
                    @Override
                    public void onSuccess(String barcode) throws RemoteException {

//                        ToastUtil.toastOnUiThread(activity, barcode);


//                        TransactionParams.getInstance().setQRData(barcode);
//                        TransBasic.getInstance().printTest(1);
                        Message msg = new Message();
                        msg.getData().putString("msg", barcode);
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onError(int error, String message) throws RemoteException {

                    }

                    @Override
                    public void onTimeout() throws RemoteException {
                        finish();
                    }

                    @Override
                    public void onCancel() throws RemoteException {
                        finish();
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        else{
            titleApp.setText(titlePhone);
        }
    }

    private void getCardInfoPEC (String cardId){
        Call<GetCardInfo> call = peServiceAPI.getCardInfoByBarcode(deviceId, cardId);

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
                        EmployeesCard card = mRealm.where(EmployeesCard.class).equalTo("cardBarcode", getCardInfo.getCardBarcode()).findFirst();
                        if(card != null){
                            // Cardul dat este cardul de serviciu si arunc la pagina cu assortiment simplu si achitare diferita de cea de contul clientului
                            progressDialog.dismiss();
                            new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                    .setTitle(getString(R.string.attention_dialog_title))
                                    .setCancelable(false)
                                    .setMessage("De pe acest card temporar nu pot fi efectuate vinzari!" + (card.getUserName().length() > 0 ? "El apartine " + card.getUserName() : "" + " Codul cardului: ") + card.getCardNumber())
                                    .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
                                        finish();
                                    })
                                    .show();
                        }
                        else {
                            if (getCardInfo.getAssortiment() != null && getCardInfo.getAssortiment().size() > 0) {
                                List<AssortmentCardSerializable> assortmentSerializables = new ArrayList<>();
                                for (AssortmentCard item : getCardInfo.getAssortiment()) {
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

                                Intent intent = new Intent(ScanMyDiscountActivity.this, ClientMyDiscountCardCorporativActivity.class);
                                intent.putExtra("ResponseClient", cardInfoSerializable);
                                intent.putExtra("ClientCardCode", getCardInfo.getCardBarcode());
                                intent.putExtra("ClientCardName", getCardInfo.getCardNumber() + "/" + getCardInfo.getCardName());
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            }
                        }
                    }
                    else{
                        progressDialog.dismiss();
                        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setTitle(getString(R.string.attention_dialog_title))
                                .setMessage(getString(R.string.error_check_code_msg) + PECErrorMessage.getErrorMessage(getCardInfo.getErrorCode()))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
                                    counter = 0;
                                    if(isVerifone){
                                        Bundle bundle = new Bundle();
                                        bundle.putString("topTitleString", getString(R.string.scanning_verifone_title));
                                        bundle.putString("upPromptString", title);
                                        bundle.putBoolean("showScannerBorder", AppParams.getInstance().isShowScanBorder());

                                        try {
                                            iScanner.startScan(bundle, 60, new ScannerListener.Stub() {
                                                @Override
                                                public void onSuccess(String barcode) throws RemoteException {
                                                    Message msg = new Message();
                                                    msg.getData().putString("msg", barcode);
                                                    handler.sendMessage(msg);
                                                }

                                                @Override
                                                public void onError(int error, String message) throws RemoteException {

                                                }

                                                @Override
                                                public void onTimeout() throws RemoteException {
                                                    finish();
                                                }

                                                @Override
                                                public void onCancel() throws RemoteException {
                                                    finish();
                                                }
                                            });
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                    }
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
                            .setMessage(getString(R.string.error_check_code_empty))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
                                counter = 0;
                                if(isVerifone){
                                    Bundle bundle = new Bundle();
                                    bundle.putString("topTitleString", getString(R.string.scanning_verifone_title));
                                    bundle.putString("upPromptString", title);
                                    bundle.putBoolean("showScannerBorder", AppParams.getInstance().isShowScanBorder());

                                    try {
                                        iScanner.startScan(bundle, 60, new ScannerListener.Stub() {
                                            @Override
                                            public void onSuccess(String barcode) throws RemoteException {
                                                Message msg = new Message();
                                                msg.getData().putString("msg", barcode);
                                                handler.sendMessage(msg);
                                            }

                                            @Override
                                            public void onError(int error, String message) throws RemoteException {

                                            }

                                            @Override
                                            public void onTimeout() throws RemoteException {
                                                finish();
                                            }

                                            @Override
                                            public void onCancel() throws RemoteException {
                                                finish();
                                            }
                                        });
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
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
                            counter = 0;
                            if(isVerifone){
                                Bundle bundle = new Bundle();
                                bundle.putString("topTitleString", getString(R.string.scanning_verifone_title));
                                bundle.putString("upPromptString", title);
                                bundle.putBoolean("showScannerBorder", AppParams.getInstance().isShowScanBorder());

                                try {
                                    iScanner.startScan(bundle, 60, new ScannerListener.Stub() {
                                        @Override
                                        public void onSuccess(String barcode) throws RemoteException {
                                            Message msg = new Message();
                                            msg.getData().putString("msg", barcode);
                                            handler.sendMessage(msg);
                                        }

                                        @Override
                                        public void onError(int error, String message) throws RemoteException {

                                        }

                                        @Override
                                        public void onTimeout() throws RemoteException {
                                            finish();
                                        }

                                        @Override
                                        public void onCancel() throws RemoteException {
                                            finish();
                                        }
                                    });
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.retry_button),((dialogInterface, i) -> {
                            getCardInfoPEC(cardId);
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