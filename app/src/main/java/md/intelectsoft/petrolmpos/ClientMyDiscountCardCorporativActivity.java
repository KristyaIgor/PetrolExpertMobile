package md.intelectsoft.petrolmpos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolmpos.adapters.AssortmentCardAdapter;
import md.intelectsoft.petrolmpos.network.pe.result.AssortmentCardSerializable;
import md.intelectsoft.petrolmpos.network.pe.result.GetCardInfoSerializable;

@SuppressLint("NonConstantResourceId")
public class ClientMyDiscountCardCorporativActivity extends AppCompatActivity {

    @BindView(R.id.textClientCardName) TextView clientCardName;
    @BindView(R.id.textCardBalance) TextView clientBalance;
    @BindView(R.id.textClientAmount) TextView clientAmount;
    @BindView(R.id.textNameOfAmount) TextView clientAmountType;
    @BindView(R.id.textClientLimitDay) TextView clientLimitDay;
    @BindView(R.id.textClientLimitWeek) TextView clientLimitWeek;
    @BindView(R.id.textClientLimitMonth) TextView clientLimitMonth;

    @BindView(R.id.listOfAvailableProducts) ListView clientProducts;

    AssortmentCardAdapter adapter;
    Context context;
    String cardId;

    @OnClick(R.id.layoutCloseClientInfoActivity) void onCloseActivity(){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_my_discount_card_corporativ);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        context = this;

        Intent intent = getIntent();
        GetCardInfoSerializable cardInfoSerializable = (GetCardInfoSerializable) intent.getSerializableExtra("ResponseClient");
        cardId = intent.getStringExtra("ClientCardCode");


        String typeLimit = "";
        if(cardInfoSerializable.getLimitType() == null){
            clientLimitDay.setText("0");
            clientLimitWeek.setText("0");
            clientLimitMonth.setText("0");
            typeLimit = " MDL";
        }
        else{
            if(cardInfoSerializable.getLimitType() == 0)
                typeLimit = " MDL";
            else {
                typeLimit = " L";
            }

            clientLimitDay.setText(cardInfoSerializable.getDailyLimit() + typeLimit);
            clientLimitWeek.setText(cardInfoSerializable.getWeeklyLimit() + typeLimit);
            clientLimitMonth.setText(cardInfoSerializable.getMonthlyLimit() + typeLimit);
        }
        clientAmountType.setText("Suma disponibila:");
        clientCardName.setText(cardInfoSerializable.getCustomerName());
        clientBalance.setText(cardInfoSerializable.getBalance() + " MDL");
        clientAmount.setText(cardInfoSerializable.getAllowedBalance() + " MDL");

        adapter = new AssortmentCardAdapter(context, R.layout.list_assortiment_view, cardInfoSerializable.getAssortiment());
        clientProducts.setAdapter(adapter);

        clientProducts.setOnItemClickListener((parent, view, position, id) -> {
            AssortmentCardSerializable item = adapter.getItem(position);
            Intent count = new Intent(context, CountProductActivity.class);
            count.putExtra("Identify", true);
            count.putExtra("Product", item);
            count.putExtra("ClientCardCode", cardId);
            startActivityForResult(count,164);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 164){
            if(resultCode == RESULT_OK){
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}