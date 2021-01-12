package md.intelectsoft.petrolmpos.verifone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import md.intelectsoft.petrolmpos.R;
import md.intelectsoft.petrolmpos.verifone.Utilities.Utility;
import md.intelectsoft.petrolmpos.verifone.basic.BaseActivity;
import md.intelectsoft.petrolmpos.verifone.transaction.TransBasic;
import md.intelectsoft.petrolmpos.verifone.transaction.TransactionParams;

public class InputPinActivity extends BaseActivity {
    TransBasic transBasic;
    TextView tvCardNo;
    TextView tvAmount;
    ImageView btnBack;
    TextView tvTransName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_pin);

        transBasic = TransBasic.getInstance();
        transBasic.doPinPad();
        transBasic.setOnInputPinConfirm(new TransBasic.OnInputPinConfirm() {
            @Override
            public void onConfirm() {
                startActivity(new Intent(InputPinActivity.this, ESignActivity.class));
                finish();
            }
        });
    }

    private void initView() {
        tvCardNo = findViewById(R.id.tv_cardno);
        tvAmount = findViewById(R.id.tv_amount);
        btnBack = findViewById(R.id.back_home);
        tvTransName = findViewById(R.id.trans_name);
        tvCardNo.setText(Utility.fixCardNoWithMask(TransactionParams.getInstance().getPan()));
        tvAmount.setText(Utility.getReadableAmount(TransactionParams.getInstance().getTransactionAmount()) + " MDL");
        btnBack.setVisibility(View.INVISIBLE);
        tvTransName.setText("Input PIN");
    }
}