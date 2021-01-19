package md.intelectsoft.petrolmpos;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolmpos.bottomsheet.PaymentMethodSheetDialog;
import md.intelectsoft.petrolmpos.models.ToggleButton;
import md.intelectsoft.petrolmpos.network.pe.result.AssortmentCardSerializable;
import md.intelectsoft.petrolmpos.network.pe.result.AssortmentSerializable;

@SuppressLint("NonConstantResourceId")
public class CountProductWithoutActivity extends AppCompatActivity {
    @BindView(R.id.toggleQuantityOrSum) ToggleButton toggleButton;
    @BindView(R.id.textCountProductName) TextView productName;
    @BindView(R.id.textCountProductPrice) TextView productPrice;
    @BindView(R.id.textTitleCountSum) TextView titleCountOrSum;
    @BindView(R.id.textCountOrSum) TextView countOrSum;
    @BindView(R.id.textTotalBillSum) TextView totalBill;

    boolean isLeftButtonSelected = true;

    String nameProduct;
    double priceProduct = 0;

    public static DisplayMetrics displayMetrics;

    ToggleButton.OnToggleClickListener onToggleClickListener;


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
        if(sum > 0){
            PaymentMethodSheetDialog loginForm = PaymentMethodSheetDialog.newInstance();
            loginForm.show(getSupportFragmentManager(), PaymentMethodSheetDialog.TAG);
        }
        else{
            Toast error = Toast.makeText(this, "Input field!", Toast.LENGTH_SHORT);
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

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        Intent intent = getIntent();

        if(intent.getBooleanExtra("Identify",false)){
            //with identify
            AssortmentCardSerializable product = (AssortmentCardSerializable) getIntent().getSerializableExtra("Product");

            nameProduct = product.getName();
            priceProduct = product.getPrice();
        }
        else{
            //without identify
            AssortmentSerializable product = (AssortmentSerializable) getIntent().getSerializableExtra("Product");

            nameProduct = product.getName();
            priceProduct = product.getPrice();
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
                        totalBill.setText(String.format("%.2f",sum / priceProduct).replace(",",".") + " L");
                    }
                    else{
                        double countText = Double.parseDouble(countOrSum.getText().toString());
                        totalBill.setText(String.format("%.2f",countText * priceProduct).replace(",",".") + " MDL");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


}