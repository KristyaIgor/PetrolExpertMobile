package md.intelectsoft.petrolmpos;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import md.intelectsoft.petrolmpos.Utils.LocaleHelper;
import md.intelectsoft.petrolmpos.Utils.SPFHelp;
import md.intelectsoft.petrolmpos.bottomsheet.PaymentMethodSheetDialog;
import md.intelectsoft.petrolmpos.enums.LimitCardEnum;
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
    double priceProduct = 0 , maxClientAvailable = 0;
    private boolean isAuth = false;
    int limitType;
    ProgressDialog progressDialog;
    PEServiceAPI peServiceAPI;
    Context context;

    public static DisplayMetrics displayMetrics;

    AssortmentCardSerializable productWithAuth;
    AssortmentSerializable productWithoutAuth;

    ToggleButton.OnToggleClickListener onToggleClickListener;

    IDeviceService idevice;
    IPrinter printer;

    BillRegistered bill;


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
        double sumOrQuantity = round(Double.parseDouble(countOrSum.getText().toString()), 2);
        if(sumOrQuantity > 0){
            if(isAuth){ // Если продажа клиенту который авторизацию прошел , либо QR кодом либо корпоративной картой
                if(isLeftButtonSelected){ //Если продажа идет по сумме
                    if(productWithAuth.getDailyLimit() == 0){ // Если лимит у товара 0 проверяю по доступной сумме
                        if(sumOrQuantity < maxClientAvailable) // если сумма чека меньше чем доступная сумма , продажа разрешается
                            sendBillToBackAndSaveLocal(sumOrQuantity , round(sumOrQuantity / productWithAuth.getPrice(), 2));
                        else // Если сумма чека больше чем доступная сумма, продажа блокируется , выводится сообщение на экран в лееях
                            showErrorDialogRegisterBill("Сумма чека больше чем доступная сумма клиента! Вы превысели на: " + round(sumOrQuantity - maxClientAvailable, 2) + " MDL");
                    }
                    else{ //иначе проверяю по лимиту у товара
                        if(limitType == LimitCardEnum.MDL){  // если лимит стоит в лееях
                            double sumOfLimit = round(productWithAuth.getDailyLimit() - productWithAuth.getDailyLimitConsumed(), 2);
                            double quantity = round(sumOrQuantity / productWithAuth.getPrice(), 2);
                            if(sumOrQuantity < sumOfLimit) //если сумма товара разделить на цену ,получая литры, меньше чем дневной лимит товара минус сегодняшнее потребление то продажа разрешается
                                sendBillToBackAndSaveLocal(sumOrQuantity , quantity);
                            else // иначе продажа блокируется , выводится сообщение на экран в лееях
                                showErrorDialogRegisterBill("Первышен дневной лимит! Вы превысели на: " + round(sumOrQuantity - sumOfLimit, 2) + " MDL");
                        }
                        else{ //иначе проверяю по литрам
                            double literOfInput = round(sumOrQuantity / productWithAuth.getPrice(), 2);
                            double literOfLimit = round(productWithAuth.getDailyLimit() - productWithAuth.getDailyLimitConsumed(), 2);
                            if(literOfInput < literOfLimit) //если сумма товара разделить на цену ,получая литры, меньше чем дневной лимит товара минус сегодняшнее потребление то продажа разрешается
                                sendBillToBackAndSaveLocal(sumOrQuantity , literOfInput);
                            else // иначе продажа блокируется , выводится сообщение на экран в лееях
                                showErrorDialogRegisterBill("Первышен дневной лимит! Вы превысели на: " + round(literOfInput - literOfLimit, 2) + " L.");
                        }
                    }
                }
                else{  // если продажа идет по литрам
                    if(productWithAuth.getDailyLimit() == 0){ // Если лимит у товара 0 проверяю по доступной сумме
                        double sumOfInput = round(sumOrQuantity * productWithAuth.getPrice(), 2);
                        if(sumOfInput < maxClientAvailable)  //если кол-во товара умножить на цену товара, меньше чем доступная сумма, продажа разрешается
                            sendBillToBackAndSaveLocal(sumOfInput , sumOrQuantity);
                        else // иначе продажа блокируется , выводится сообщение на экран в литрах
                            showErrorDialogRegisterBill("Вы ввели больше топливо чем у клиента доступно! Вы превысели на: " + (sumOfInput - maxClientAvailable) + " MDL");
                    }
                    else{ //иначе проверяю по лимиту у товара
                        if(limitType == LimitCardEnum.MDL){  // если лимит стоит в лееях
                            double sumOfLimit = round(productWithAuth.getDailyLimit() - productWithAuth.getDailyLimitConsumed(), 2);
                            double sumOfInput = round(sumOrQuantity * productWithAuth.getPrice(), 2);
                            if(sumOfInput < sumOfLimit) //если сумма товара разделить на цену ,получая литры, меньше чем дневной лимит товара минус сегодняшнее потребление то продажа разрешается
                                sendBillToBackAndSaveLocal(sumOfInput , sumOrQuantity);
                            else // иначе продажа блокируется , выводится сообщение на экран в лееях
                                showErrorDialogRegisterBill("Первышен дневной лимит! Вы превысели на: " + round(sumOfInput - sumOfLimit, 2) + " MDL");
                        }
                        else{ //иначе проверяю по литрам
                            double sumOfInput = round(sumOrQuantity * productWithAuth.getPrice(), 2);
                            double literOfLimit = round(productWithAuth.getDailyLimit() - productWithAuth.getDailyLimitConsumed(), 2);
                            if(sumOrQuantity < literOfLimit) //если сумма товара разделить на цену ,получая литры, меньше чем дневной лимит товара минус сегодняшнее потребление то продажа разрешается
                                sendBillToBackAndSaveLocal(sumOfInput , sumOrQuantity);
                            else // иначе продажа блокируется , выводится сообщение на экран в лееях
                                showErrorDialogRegisterBill("Первышен дневной лимит! Вы превысели на: " + round(sumOrQuantity - literOfLimit, 2) + " L.");
                        }
                    }
                }
            }
            else{

                PaymentMethodSheetDialog loginForm = PaymentMethodSheetDialog.newInstance();
                loginForm.show(getSupportFragmentManager(), PaymentMethodSheetDialog.TAG);
            }
        }
        else{
            Toast error = Toast.makeText(this, getString(R.string.please_input_the_field), Toast.LENGTH_SHORT);
            error.setGravity(Gravity.CENTER, 0, -100);
            error.show();
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private void sendBillToBackAndSaveLocal(double sum , double quantity) {
        bill = new BillRegistered();
        bill.setClientCardCode(cardId);
        bill.setOfficeCode(SPFHelp.getInstance().getString("deviceId",""));
        bill.setDate(new Date().getTime());
        bill.setAuthorId(SPFHelp.getInstance().getString("OwnerId",""));
        bill.setAuthorName(SPFHelp.getInstance().getString("Owner",""));
        bill.setCashId(SPFHelp.getInstance().getString("CashId",""));
        bill.setCashName(SPFHelp.getInstance().getString("Cash",""));
        bill.setStationName(SPFHelp.getInstance().getString("StationName", ""));

        LineBill lineBill = new LineBill();
        PaymentBill paymentBill = new PaymentBill();

        lineBill.setNomenclatureCode(productWithAuth.getAssortmentCode());
        lineBill.setPrice(productWithAuth.getPrice());
        lineBill.setDiscountedPrice(productWithAuth.getPrice());
        lineBill.setName(productWithAuth.getName());

        lineBill.setSum(sum);
        lineBill.setCount(quantity);
        lineBill.setDiscountedSum(sum);

        paymentBill.setSum(sum);
        paymentBill.setPaymentCode(0);

        RealmList<PaymentBill> paymentBillList = new RealmList<>();
        RealmList<LineBill> listLines = new RealmList<>();

        listLines.add(lineBill);
        paymentBillList.add(paymentBill);

        bill.setLines(listLines);
        bill.setPaymentBills(paymentBillList);

        registerBillToBack(bill);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String lang = LocaleHelper.getLanguage(this);
        setAppLocale(lang);
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
        maxClientAvailable = intent.getDoubleExtra("ClientMaxAvailable", 0);
        limitType = intent.getIntExtra("LimitType", 0);

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
                titleCountOrSum.setText(getString(R.string.input_summ));
                double sum = Double.parseDouble(countOrSum.getText().toString());
                totalBill.setText(String.format("%.2f",sum / priceProduct).replace(",",".") + " L");

            }

            @Override
            public void onRightToggleEnabled(boolean enabled) {
                if (enabled) isLeftButtonSelected = false;
                titleCountOrSum.setText(getString(R.string.input_count));
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
                    }
                    else{
                        double countText = Double.parseDouble(countOrSum.getText().toString());
                        totalBill.setText(String.format("%.2f", countText * priceProduct).replace(",", ".") + " MDL");
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

        progressDialog.setMessage(getString(R.string.save_bill_pg));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setButton(-1, getString(R.string.cancel_button), (dialog, which) -> {
            call.cancel();
            if(call.isCanceled())
                dialog.dismiss();
        });
        progressDialog.show();

        call.enqueue(new Callback<RegisterBillResponse>() {
            @Override
            public void onResponse(Call<RegisterBillResponse> call, Response<RegisterBillResponse> response) {
                RegisterBillResponse billResponse = response.body();
                if(billResponse != null){
                    if(billResponse.getErrorCode() == 0){
                        if(BaseApp.isVFServiceConnected()){
                            idevice = BaseApp.getApplication().getDeviceService();
                            try {
                                printer = idevice.getPrinter();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                            if(getPrinterStatus())
                                doPrintString();
                        }

                        Realm.getDefaultInstance().executeTransaction(realm -> {
                            realm.insert(bill);
                        });
                    }
                    else
                        showErrorDialogRegisterBill("Error register bill! Code: " + billResponse.getErrorCode());
                }
                else
                    showErrorDialogRegisterBill("Error register bill! Response is empty.");
            }

            @Override
            public void onFailure(Call<RegisterBillResponse> call, Throwable t) {
                progressDialog.dismiss();
                showErrorDialogRegisterBill("Failure register bill to back office! Message: " + t.getMessage());
            }
        });
    }


    private void showErrorDialogRegisterBill (String text) {
        progressDialog.dismiss();
        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle(getString(R.string.attention_dialog_title))
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok_button),null)
                .setPositiveButton(getString(R.string.retry_button), (dialogInterface, i) -> {
                    registerBillToBack(bill);
                })
                .show();
    }

    public void doPrintString() {
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

//            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.LEFT );
//            format.putInt(PrinterConfig.addText.FontSize.BundleName, PrinterConfig.addText.FontSize.NORMAL_24_24 );
//            printer.addText(format, "--------------------------------");

//
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterConfig.addTextInLine.GlobalFont.English);  // this the default
//            printer.addTextInLine( fmtAddTextInLine, "", "#",
//                    "Right long string with the center string",
//                    0);
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


    /**
     * 获取打印机状态 | get printer status
     * <ul>
     * <li>ERROR_NONE(0x00) - 状态正常</li>
     * <li>ERROR_PAPERENDED(0xF0) - 缺纸，不能打印</li>
     * <li>ERROR_NOCONTENT(0xF1) - 打印内存无内容</li>
     * <li>ERROR_HARDERR(0xF2) - 硬件错误</li>
     * <li>ERROR_OVERHEAT(0xF3) - 打印头过热</li>
     * <li>ERROR_BUFOVERFLOW(0xF5) - 缓冲模式下所操作的位置超出范围 </li>
     * <li>ERROR_LOWVOL(0xE1) - 低压保护 </li>
     * <li>ERROR_PAPERENDING(0xF4) - 纸张将要用尽，还允许打印(单步进针打特有返回值)</li>
     * <li>ERROR_MOTORERR(0xFB) - 打印机芯故障(过快或者过慢)</li>
     * <li>ERROR_PENOFOUND(0xFC) - 自动定位没有找到对齐位置,纸张回到原来位置   </li>
     * <li>ERROR_PAPERJAM(0xEE) - 卡纸</li>
     * <li>ERROR_NOBM(0xF6) - 没有找到黑标</li>
     * <li>ERROR_BUSY(0xF7) - 打印机处于忙状态</li>
     * <li>ERROR_BMBLACK(0xF8) - 黑标探测器检测到黑色信号</li>
     * <li>ERROR_WORKON(0xE6) - 打印机电源处于打开状态</li>
     * <li>ERROR_LIFTHEAD(0xE0) - 打印头抬起(自助热敏打印机特有返回值)</li>
     * <li>ERROR_CUTPOSITIONERR(0xE2) - 切纸刀不在原位(自助热敏打印机特有返回值)</li>
     * <li>ERROR_LOWTEMP(0xE3) - 低温保护或AD出错(自助热敏打印机特有返回值)</li>
     * </ul>
     * \_en_
     *  get printer status
     *
     *  the status:
     * <ul>
     * <li>ERROR_NONE(0x00) - normal</li>
     * <li>ERROR_PAPERENDED(0xF0) - Paper out</li>
     * <li>ERROR_NOCONTENT(0xF1) - no content</li>
     * <li>ERROR_HARDERR(0xF2) - printer error</li>
     * <li>ERROR_OVERHEAT(0xF3) - over heat</li>
     * <li>ERROR_BUFOVERFLOW(0xF5) - buffer overflow</li>
     * <li>ERROR_LOWVOL(0xE1) - battery low</li>
     * <li>ERROR_PAPERENDING(0xF4) - Paper low for sprocket printer</li>
     * <li>ERROR_MOTORERR(0xFB) - moto error</li>
     * <li>ERROR_PAPERJAM(0xEE) - paper jam</li>
     * <li>ERROR_BUSY(0xF7) - printer is busy</li>
     * <li>ERROR_WORKON(0xE6) - printer is awake</li>
     * </ul>
     * \en_e
     */
    class MyListener extends PrinterListener.Stub {
        @Override
        public void onError(int error) throws RemoteException {
            progressDialog.dismiss();
            if (error == 240) { // finish paper
                showDialogPrinterError("Paper out, please put new paper in printer and retry print bill!");
            }
            else{
                Message msg = new Message();
                msg.getData().putString("msg", "print error,errno:" + error);
                handler.sendMessage(msg);
            }
        }

        @Override
        public void onFinish() throws RemoteException {
            progressDialog.dismiss();
            setResult(RESULT_OK);
            finish();
        }
    }

    private void showDialogPrinterError(String text){
        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Printer error!")
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {

                })
                .setNeutralButton("Retry", (dialog, which) -> {
                    if(getPrinterStatus())
                        doPrintString();
                })
                .show();
    }

    private boolean getPrinterStatus(){
        boolean printerReady = false;
        try {
            int status = printer.getStatus();
            switch (status){
                case (int) 0x00 : {  // no error
                   printerReady = true;
                }break;
                case (int) 0xF0 : {
                    showDialogPrinterError("Paper ended, please put new paper in printer and retry print bill!");
                    printerReady = false;
                }break;
                case (int) 0xF3 : {
                    showDialogPrinterError("Printer overheat!");
                    printerReady = false;
                }break;
                case (int) 0xE1 : {
                    showDialogPrinterError("Battery is low nivel!");
                    printerReady = false;
                }break;
                case (int) 0xF4 : {
                    showDialogPrinterError("Paper endendig. Please put new paper!");
                    printerReady = false;
                }break;
                case (int) 0xFB : {
                    showDialogPrinterError("Moto error in printer!");
                    printerReady = false;
                }break;
                case (int) 0xEE : {
                    showDialogPrinterError("Paper jam. Please check paper!");
                    printerReady = false;
                }break;
                case (int) 0xF7 : {
                    showDialogPrinterError("Paper is busy. Please wait!");
                    printerReady = false;
                }break;
                default: printerReady = false;
            }
        } catch (RemoteException e) {
            showDialogPrinterError("Remote exception: " + e.getMessage());
        }

        return printerReady;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("TAG", msg.getData().getString("msg"));
            Toast.makeText(context, msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();


        }
    };

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