package md.intelectsoft.petrolmpos;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.vfi.smartpos.deviceservice.aidl.IDeviceService;
import com.vfi.smartpos.deviceservice.aidl.IPrinter;
import com.vfi.smartpos.deviceservice.aidl.PrinterConfig;
import com.vfi.smartpos.deviceservice.aidl.PrinterListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import md.intelectsoft.petrolmpos.Utils.SPFHelp;
import md.intelectsoft.petrolmpos.bottomsheet.PaymentMethodSheetDialog;
import md.intelectsoft.petrolmpos.models.ToggleButton;
import md.intelectsoft.petrolmpos.network.pe.PERetrofitClient;
import md.intelectsoft.petrolmpos.network.pe.PEServiceAPI;
import md.intelectsoft.petrolmpos.network.pe.body.registerBill.BillRegistered;
import md.intelectsoft.petrolmpos.network.pe.body.registerBill.LineBill;
import md.intelectsoft.petrolmpos.network.pe.body.registerBill.PaymentBill;
import md.intelectsoft.petrolmpos.network.pe.result.AssortmentCardSerializable;
import md.intelectsoft.petrolmpos.network.pe.result.AssortmentSerializable;
import md.intelectsoft.petrolmpos.network.pe.result.RegisterBillResponse;
import md.intelectsoft.petrolmpos.printeractivity.PrinterFonts;
import md.intelectsoft.petrolmpos.realm.FiscalKey;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class CountProductActivity extends AppCompatActivity {
    @BindView(R.id.toggleQuantityOrSum) ToggleButton toggleButton;
    @BindView(R.id.textCountProductName) TextView productName;
    @BindView(R.id.textCountProductPrice) TextView productPrice;
    @BindView(R.id.textTitleCountSum) TextView titleCountOrSum;
    @BindView(R.id.textCountOrSum) TextView countOrSum;
    @BindView(R.id.textTotalBillSum) TextView totalBill;

    @BindView(R.id.buttonPayWithoutIdentify) Button buttonPay;

    boolean isLeftButtonSelected = true;

    String nameProduct, cardId;
    double priceProduct = 0;
    private boolean isAuth = false;
    ProgressDialog progressDialog;
    PEServiceAPI peServiceAPI;
    Context context;

    public static DisplayMetrics displayMetrics;

    AssortmentCardSerializable productWithAuth;
    AssortmentSerializable productWithoutAuth;

    ToggleButton.OnToggleClickListener onToggleClickListener;

    IDeviceService idevice;
    IPrinter printer;


    @OnClick(R.id.imageCancelCount) void onCloseCount(){
        finish();
    }

    @OnClick(R.id.textButtonCount1) void onButton1(){
        if (countOrSum.getText().toString().equals("0")) countOrSum.setText("1");
        else if(countOrSum.getText().toString().contains(".")){
            String test = countOrSum.getText().toString().substring(countOrSum.getText().toString().indexOf("."), countOrSum.getText().toString().length());
            if (test.length() < 3){
                countOrSum.append("1");
            }
        }
        else countOrSum.append("1");
    }
    @OnClick(R.id.textButtonCount2) void onButton2(){
        if (countOrSum.getText().toString().equals("0")) countOrSum.setText("2");
        else if(countOrSum.getText().toString().contains(".")){
            String test = countOrSum.getText().toString().substring(countOrSum.getText().toString().indexOf("."), countOrSum.getText().toString().length());
            if (test.length() < 3){
                countOrSum.append("2");
            }
        }
        else countOrSum.append("2");
    }
    @OnClick(R.id.textButtonCount3) void onButton3(){
        if (countOrSum.getText().toString().equals("0")) countOrSum.setText("3");
        else if(countOrSum.getText().toString().contains(".")){
            String test = countOrSum.getText().toString().substring(countOrSum.getText().toString().indexOf("."), countOrSum.getText().toString().length());
            if (test.length() < 3){
                countOrSum.append("3");
            }
        }
        else countOrSum.append("3");
    }
    @OnClick(R.id.textButtonCount4) void onButton4(){
        if (countOrSum.getText().toString().equals("0")) countOrSum.setText("4");
        else if(countOrSum.getText().toString().contains(".")){
            String test = countOrSum.getText().toString().substring(countOrSum.getText().toString().indexOf("."), countOrSum.getText().toString().length());
            if (test.length() < 3){
                countOrSum.append("4");
            }
        }
        else countOrSum.append("4");
    }
    @OnClick(R.id.textButtonCount5) void onButton5(){
        if (countOrSum.getText().toString().equals("0")) countOrSum.setText("5");
        else if(countOrSum.getText().toString().contains(".")){
            String test = countOrSum.getText().toString().substring(countOrSum.getText().toString().indexOf("."), countOrSum.getText().toString().length());
            if (test.length() < 3){
                countOrSum.append("5");
            }
        }
        else countOrSum.append("5");
    }
    @OnClick(R.id.textButtonCount6) void onButton6() {
        if (countOrSum.getText().toString().equals("0")) countOrSum.setText("6");
        else if(countOrSum.getText().toString().contains(".")){
            String test = countOrSum.getText().toString().substring(countOrSum.getText().toString().indexOf("."), countOrSum.getText().toString().length());
            if (test.length() < 3){
                countOrSum.append("6");
            }
        }
        else countOrSum.append("6");
    }
    @OnClick(R.id.textButtonCount7) void onButton7(){
        if (countOrSum.getText().toString().equals("0")) countOrSum.setText("7");
        else if(countOrSum.getText().toString().contains(".")){
            String test = countOrSum.getText().toString().substring(countOrSum.getText().toString().indexOf("."), countOrSum.getText().toString().length());
            if (test.length() < 3){
                countOrSum.append("7");
            }
        }
        else countOrSum.append("7");
    }
    @OnClick(R.id.textButtonCount8) void onButton8(){
        if (countOrSum.getText().toString().equals("0"))
            countOrSum.setText("8");
        else if(countOrSum.getText().toString().contains(".")){
            String test = countOrSum.getText().toString().substring(countOrSum.getText().toString().indexOf("."), countOrSum.getText().toString().length());
            if (test.length() < 3){
                countOrSum.append("8");
            }
        }
        else
            countOrSum.append("8");
    }
    @OnClick(R.id.textButtonCount9) void onButton9(){
        if (countOrSum.getText().toString().equals("0")) countOrSum.setText("9");
        else if(countOrSum.getText().toString().contains(".")){
            String test = countOrSum.getText().toString().substring(countOrSum.getText().toString().indexOf("."), countOrSum.getText().toString().length());
            if (test.length() < 3){
                countOrSum.append("9");
            }
        }
        else countOrSum.append("9");
    }
    @OnClick(R.id.textButtonCount0) void onButton0(){
        if (!countOrSum.getText().toString().equals("0"))
            if (countOrSum.getText().toString().contains(".")) {
                String test = countOrSum.getText().toString().substring(countOrSum.getText().toString().indexOf("."), countOrSum.getText().toString().length());
                if (test.length() < 3) {
                    countOrSum.append("0");
                }
            }
            else{
                countOrSum.append("0");
            }
    }
    @OnClick(R.id.textButtonClearCountSum) void onButtonClear(){
        if(!countOrSum.getText().toString().contains("."))
            countOrSum.append(".");
    }
    @OnClick(R.id.textButtonDeleteCountSum) void onButtonDelete(){
        String text = countOrSum.getText().toString();
        if(text.length() - 1 != 0){
            countOrSum.setText(text.substring(0, text.length() - 1));
        }
        else
            countOrSum.setText("0");
    }

    @OnClick(R.id.buttonPayWithoutIdentify) void onPay(){
        double sum = Double.parseDouble(countOrSum.getText().toString());
        if(sum > 0 && totalBill.getError() == null){
            if(isAuth){
                buttonPay.setText("Salveaza");

                BillRegistered bill = new BillRegistered();
                bill.setClientCardCode(cardId);
                bill.setOfficeCode(SPFHelp.getInstance().getString("deviceId",""));
                bill.setShiftId(SPFHelp.getInstance().getString("ShiftId",""));

                LineBill lineBill = new LineBill();
                PaymentBill paymentBill = new PaymentBill();

                lineBill.setNomenclatureCode(productWithAuth.getAssortmentCode());
                lineBill.setPrice(productWithAuth.getPrice());
                lineBill.setDiscountedPrice(productWithAuth.getPrice());
                lineBill.setName(productWithAuth.getName());

                if(isLeftButtonSelected){
                    lineBill.setSum(sum);
                    lineBill.setCount(sum / productWithAuth.getPrice());
                    lineBill.setDiscountedSum(sum);
                    paymentBill.setSum(sum);
                }
                else{
                    lineBill.setSum(sum * productWithAuth.getPrice());
                    lineBill.setCount(sum);
                    lineBill.setDiscountedSum(sum * productWithAuth.getPrice());
                    paymentBill.setSum(sum * productWithAuth.getPrice());
                }
                paymentBill.setPaymentCode(0);
                List<PaymentBill> paymentBillList = new ArrayList<>();
                List<LineBill> listLines = new ArrayList<>();
                listLines.add(lineBill);
                paymentBillList.add(paymentBill);

                bill.setLines(listLines);
                bill.setPaymentBills(paymentBillList);

                registerBillToBack(bill);
            }
            else{

                PaymentMethodSheetDialog loginForm = PaymentMethodSheetDialog.newInstance();
                loginForm.show(getSupportFragmentManager(), PaymentMethodSheetDialog.TAG);
            }
        }
        else{
            Toast error = Toast.makeText(this, "Check field!", Toast.LENGTH_SHORT);
            error.setGravity(Gravity.CENTER, 0, -100);
            error.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_product_without);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        context = this;
        progressDialog = new ProgressDialog(context);

        String uri = SPFHelp.getInstance().getString("URI", null);
        peServiceAPI = PERetrofitClient.getPEService(uri);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        Intent intent = getIntent();
        isAuth = intent.getBooleanExtra("Identify",false);
        cardId = intent.getStringExtra("ClientCardCode");

        if(isAuth){
            //with identify
            productWithAuth = (AssortmentCardSerializable) getIntent().getSerializableExtra("Product");

            nameProduct = productWithAuth.getName();
            priceProduct = productWithAuth.getPrice();


        }
        else{
            //without identify
            productWithoutAuth = (AssortmentSerializable) getIntent().getSerializableExtra("Product");

            nameProduct = productWithoutAuth.getName();
            priceProduct = productWithoutAuth.getPrice();
        }

        productName.setText(nameProduct);
        productPrice.setText(String.valueOf(priceProduct) + " MDL");

        onToggleClickListener = new ToggleButton.OnToggleClickListener() {
            @Override
            public void onLefToggleEnabled(boolean enabled) {
                if(enabled) isLeftButtonSelected = true;
                titleCountOrSum.setText("Introduceti suma:");
                double sum = Double.parseDouble(countOrSum.getText().toString());
                totalBill.setText(String.format("%.2f",sum / priceProduct).replace(",",".") + " L");

            }

            @Override
            public void onRightToggleEnabled(boolean enabled) {
                if (enabled) isLeftButtonSelected = false;
                titleCountOrSum.setText("Introduceti cantitatea:");
                double count = Double.parseDouble(countOrSum.getText().toString());
                totalBill.setText(String.format("%.2f",count * priceProduct).replace(",",".") + " MDL");
            }
        };

        onToggleClickListener.onLefToggleEnabled(true);

        toggleButton.setOnToggleClickListener(onToggleClickListener);

        countOrSum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.equals("0")){
                    if (isLeftButtonSelected)
                        totalBill.setText("0.00 L");
                    else
                        totalBill.setText("0.00 MDL");
                }
                else{
                    if(isLeftButtonSelected){
                        double sum = Double.parseDouble(countOrSum.getText().toString());
                        totalBill.setText(String.format("%.2f", sum / priceProduct).replace(",", ".") + " L");
                        if(isAuth){
                            if(sum / productWithAuth.getPrice() < productWithAuth.getDailyLimit() - productWithAuth.getDailyLimitConsumed())
                                totalBill.setError(null);
                            else
                                totalBill.setError("Depasita limita!");
                        }
                        else{

                        }

                    }
                    else{
                        double countText = Double.parseDouble(countOrSum.getText().toString());
                        totalBill.setText(String.format("%.2f", countText * priceProduct).replace(",", ".") + " MDL");
                        if(isAuth) {
                            if (countText < productWithAuth.getDailyLimit() - productWithAuth.getDailyLimitConsumed())
                                totalBill.setError(null);
                            else
                                totalBill.setError("Depasita limita!");
                        }
                        else{

                        }

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void registerBillToBack(BillRegistered bill) {
        Call<RegisterBillResponse> call = peServiceAPI.registerBill(bill);

        progressDialog.setMessage("Save bill...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setButton(-1, "Cancel", (dialog, which) -> {
            call.cancel();
            if(call.isCanceled())
                dialog.dismiss();
        });
        progressDialog.show();

        call.enqueue(new Callback<RegisterBillResponse>() {
            @Override
            public void onResponse(Call<RegisterBillResponse> call, Response<RegisterBillResponse> response) {
                RegisterBillResponse billResponse = response.body();
                progressDialog.dismiss();

                if(billResponse != null){
                    Toast.makeText(CountProductActivity.this, "Bill register: " + billResponse.getErrorCode(), Toast.LENGTH_SHORT).show();
                    if(billResponse.getErrorCode() == 0){
                        //TODO send bill to fiscal service and save local and print this bill
                        if(BaseApp.isVFServiceConnected()){
                            idevice = BaseApp.getApplication().getDeviceService();
                            try {
                                printer = idevice.getPrinter();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                            doPrintString(bill);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterBillResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CountProductActivity.this, "Failure register bill to back office! Message: " + t.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showErrorDialogRegisterBill (String text) {
        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Attention!")
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {

                })
                .show();
    }

    public void doPrintString(BillRegistered bill) {
        try {
            // bundle format for addText
            Bundle format = new Bundle();

            // bundle formate for AddTextInLine
            Bundle fmtAddTextInLine = new Bundle();
            //
//            format.putInt(PrinterConfig.addText.FontSize.BundleName, PrinterConfig.addText.FontSize.NORMAL_DH_24_48_IN_BOLD);
//            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.CENTER);
////            printer.addText(format, "Hello!");
//
//            format.putInt(PrinterConfig.addText.FontSize.BundleName, PrinterConfig.addText.FontSize.LARGE_DH_32_64_IN_BOLD);
//            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.CENTER);
////            printer.addText(format, "Hello!");
//
//            format.putInt(PrinterConfig.addText.FontSize.BundleName, PrinterConfig.addText.FontSize.HUGE_48);
//            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.CENTER);
//            printer.addText(format, "Hello!");
//
//            // image
//
//            byte[] buffer = null;
//            try {
//                //
//                InputStream is = getContext().getAssets().open("verifone_logo.jpg");
//                // get the size
//                int size = is.available();
//                // crete the array of byte
//                buffer = new byte[size];
//                is.read(buffer);
//                // close the stream
//                is.close();
//
//            } catch (IOException e) {
//                // Should never happen!
//                throw new RuntimeException(e);
//            }
//            if( null != buffer) {
//                Bundle fmtImage = new Bundle();
//                fmtImage.putInt("offset", (384-200)/2);
//                fmtImage.putInt("width", 250);  // bigger then actual, will print the actual
//                fmtImage.putInt("height", 128); // bigger then actual, will print the actual
//                printer.addImage( fmtImage, buffer );
//
//                fmtImage.putInt("offset", 50 );
//                fmtImage.putInt("width", 100 ); // smaller then actual, will print the setting
//                fmtImage.putInt("height", 24); // smaller then actual, will print the setting
//                printer.addImage( fmtImage, buffer );
//            }


            //
//            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.LARGE_32_32 );
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterFonts.path + PrinterFonts.FONT_FORTE );
//            printer.addTextInLine(fmtAddTextInLine, "Verifone X9-Series", "", "", 0);
//            //
//            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.NORMAL_24_24 );
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterFonts.path + PrinterFonts.FONT_segoesc );
//            printer.addTextInLine(fmtAddTextInLine, "", "", "This is the Print Demo", 0);


            format.putInt(PrinterConfig.addText.FontSize.BundleName, PrinterConfig.addText.FontSize.NORMAL_24_24);
            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.CENTER);
            printer.addText(format, "\"" + SPFHelp.getInstance().getString("CompanyName", "") + "\"");

            printer.addText(format, "IDNO: " + SPFHelp.getInstance().getString("CompanyIDNO", ""));
            printer.addText(format, "Inr.Nr: " + SPFHelp.getInstance().getString("FiscalCode", ""));
            printer.addText(format, "");

            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.NORMAL_24_24);
            printer.addTextInLine( fmtAddTextInLine, "00001" , "", "01 #", 0);

            printer.addTextInLine( fmtAddTextInLine, "#-" + SPFHelp.getInstance().getString("Cash", "Casa nui"), "", "#", 0);
            printer.addTextInLine( fmtAddTextInLine, "#-" + SPFHelp.getInstance().getString("Owner", "Autor nui"), "", "#", 0);
            printer.addTextInLine( fmtAddTextInLine, "#-Id: 00000" , "", "#", 0);
            printer.addText(format, "");

//            // left
//            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.LEFT );
//            printer.addText(format, "Left Alignment long string here: PrinterConfig.addText.Alignment.LEFT ");
//
//            // right
//            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.RIGHT );
//            printer.addText(format, "Right Alignment  long  string with wrapper here");

            printer.addText(format, "--------------------------------");



            List<LineBill> listProducts = bill.getLines();

            for(LineBill line : listProducts){
                printer.addTextInLine( fmtAddTextInLine, line.getName() , "", String.format("%.2f",line.getCount()) + " L", 0);
            }

            printer.addText(format, "--------------------------------");

            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.NORMAL_DH_24_48_IN_BOLD);

            printer.addTextInLine( fmtAddTextInLine, "TOTAL" , "", String.format("%.2f", bill.getPaymentBills().get(0).getSum()), 0);

            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.NORMAL_24_24 );
            printer.addTextInLine( fmtAddTextInLine, "IntelectSoft S.R.L." , "", "", 0);

//            Bundle fmtAddBarCode = new Bundle();
//            fmtAddBarCode.putInt( PrinterConfig.addBarCode.Alignment.BundleName, PrinterConfig.addBarCode.Alignment.RIGHT );
//            fmtAddBarCode.putInt( PrinterConfig.addBarCode.Height.BundleName, 64 );
//            printer.addBarCode( fmtAddBarCode, "123456 Verifone" );
//
//            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.LARGE_32_32 );
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterFonts.FONT_AGENCYB);
//            printer.addTextInLine(fmtAddTextInLine, "", "123456 Verifone", "", 0);
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterConfig.addTextInLine.GlobalFont.English );    // set to the default
//
//            printer.addText(format, "--------------------------------");


//            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.LARGE_32_32 );
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterFonts.path + PrinterFonts.FONT_ALGER );
//            printer.addTextInLine( fmtAddTextInLine, "Left", "Center", "right", 0);
//            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.LARGE_32_32 );
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterFonts.path + PrinterFonts.FONT_BROADW );
//            printer.addTextInLine( fmtAddTextInLine, "L & R", "", "Divide Equally", 0);
//            printer.addTextInLine( fmtAddTextInLine, "L & R", "", "Divide flexible", PrinterConfig.addTextInLine.mode.Devide_flexible);
//            // left
//            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.LEFT );
//            printer.addText(format, "--------------------------------");
//
//            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.LARGE_32_32 );
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterConfig.addTextInLine.GlobalFont.English);
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterFonts.path + PrinterFonts.FONT_segoesc );
//            printer.addTextInLine( fmtAddTextInLine,
//                    "",
//                    "",
//                    "Right long string here call addTextInLine ONLY give the right string",
//                    0);

            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.LEFT );
            format.putInt(PrinterConfig.addText.FontSize.BundleName, PrinterConfig.addText.FontSize.NORMAL_24_24 );
            printer.addText(format, "--------------------------------");


            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterConfig.addTextInLine.GlobalFont.English);  // this the default
            printer.addTextInLine( fmtAddTextInLine, "", "#",
                    "Right long string with the center string",
                    0);
            printer.addText(format, "--------------------------------");
            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.SMALL_16_16);
            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterFonts.FONT_AGENCYB);
            printer.addTextInLine( fmtAddTextInLine, "Print the QR code far from the barcode to avoid scanner found both of them", "", "",
                    PrinterConfig.addTextInLine.mode.Devide_flexible);


            Realm mRealm = Realm.getDefaultInstance();
            FiscalKey key = mRealm.where(FiscalKey.class).findFirst();
            format.putInt(PrinterConfig.addText.FontSize.BundleName, PrinterConfig.addText.FontSize.HUGE_48);
            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.CENTER);
            if(key == null) printer.addText(format, "BON NEFISCAL!");
            else printer.addText(format, "BON FISCAL!");

            Bundle fmtAddQRCode = new Bundle();
            fmtAddQRCode.putInt(PrinterConfig.addQrCode.Offset.BundleName, 128);
            fmtAddQRCode.putInt(PrinterConfig.addQrCode.Height.BundleName, 128);
            printer.addQrCode( fmtAddQRCode, "www.verifone.cn");

            printer.addTextInLine( fmtAddTextInLine, "", "try to scan it",
                    "",
                    0);

            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.LEFT );
            format.putInt(PrinterConfig.addText.FontSize.BundleName, PrinterConfig.addText.FontSize.NORMAL_24_24 );
            printer.addText(format, "---------X-----------X----------");

            printer.addText(format, "\n");
            printer.feedLine(3);
            // start print here
            printer.startPrint(new MyListener());

//            Message msg = new Message();
//            msg.getData().putString("msg", "start printing");
//            handler.sendMessage(msg);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    class MyListener extends PrinterListener.Stub {
        @Override
        public void onError(int error) throws RemoteException {
            Message msg = new Message();
            msg.getData().putString("msg", "print error,errno:" + error);
            handler.sendMessage(msg);
        }

        @Override
        public void onFinish() throws RemoteException {
            setResult(RESULT_OK);
            finish();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("TAG", msg.getData().getString("msg"));
            Toast.makeText(context, msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();


        }
    };
}