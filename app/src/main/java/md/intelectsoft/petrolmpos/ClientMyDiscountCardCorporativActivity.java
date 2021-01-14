package md.intelectsoft.petrolmpos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolmpos.adapters.AssortmentCardAdapter;
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


        String sumOrQuant = "";
        String typeLimit = "";
        if(cardInfoSerializable.getLimitType() == null){
            clientLimitDay.setText("0");
            clientLimitWeek.setText("0");
            clientLimitMonth.setText("0");
            sumOrQuant = "Suma disponibila:";
            typeLimit = " MDL";
        }
        else{
            if(cardInfoSerializable.getLimitType().equals("Bani"))
                typeLimit = " MDL";
            else {
                typeLimit = " L";
                sumOrQuant = "Cantitatea disponibila:";
            }

            clientLimitDay.setText(cardInfoSerializable.getDailyLimit() + typeLimit);
            clientLimitWeek.setText(cardInfoSerializable.getWeeklyLimit() + typeLimit);
            clientLimitMonth.setText(cardInfoSerializable.getMonthlyLimit() + typeLimit);
        }
        clientAmountType.setText(sumOrQuant);
        clientCardName.setText(cardInfoSerializable.getCustomerName());
        clientBalance.setText(cardInfoSerializable.getBalance() + " MDL");
        clientAmount.setText(cardInfoSerializable.getAllowedBalance() + typeLimit);

        adapter = new AssortmentCardAdapter(context, R.layout.list_assortiment_view, cardInfoSerializable.getAssortiment());
        clientProducts.setAdapter(adapter);
    }
}