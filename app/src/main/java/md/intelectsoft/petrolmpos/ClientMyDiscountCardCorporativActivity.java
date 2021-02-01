package md.intelectsoft.petrolmpos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolmpos.adapters.AssortmentCardAdapter;
import md.intelectsoft.petrolmpos.enums.LimitCardEnum;
import md.intelectsoft.petrolmpos.network.pe.result.AssortmentCardSerializable;
import md.intelectsoft.petrolmpos.network.pe.result.GetCardInfoSerializable;

@SuppressLint("NonConstantResourceId")
public class ClientMyDiscountCardCorporativActivity extends AppCompatActivity {

    @BindView(R.id.textClientCardName) TextView clientCardName;
    @BindView(R.id.textClientAmount) TextView clientAmount;
    @BindView(R.id.textNameOfAmount) TextView clientAmountType;
    @BindView(R.id.textClientLimitDay) TextView clientLimitDay;
    @BindView(R.id.textClientLimitWeek) TextView clientLimitWeek;
    @BindView(R.id.textClientLimitMonth) TextView clientLimitMonth;

    @BindView(R.id.textRemainLimitDaily) TextView clientRemainDay;
    @BindView(R.id.textRemainLimitWeekly) TextView clientRemainWeek;
    @BindView(R.id.textRemainLimitMonthly) TextView clientRemainMonth;

    @BindView(R.id.divider5) View divider;


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
        if(cardInfoSerializable.getLimitType() == LimitCardEnum.MDL)
            typeLimit = " MDL";
        else {
            typeLimit = " L";
        }

        clientLimitDay.setText(cardInfoSerializable.getDailyLimit() == 0 ? "0 " + typeLimit : String.format("%.2f",cardInfoSerializable.getDailyLimit()).replace(",",".") + typeLimit);
        clientLimitWeek.setText(cardInfoSerializable.getWeeklyLimit() == 0 ? "0 " + typeLimit : String.format("%.2f",cardInfoSerializable.getWeeklyLimit()).replace(",",".") + typeLimit);
        clientLimitMonth.setText(cardInfoSerializable.getMonthlyLimit() == 0 ? "0 "+ typeLimit : String.format("%.2f",cardInfoSerializable.getMonthlyLimit()).replace(",",".") + typeLimit);

        if(cardInfoSerializable.getDailyLimit() == 0 && cardInfoSerializable.getWeeklyLimit() == 0 && cardInfoSerializable.getMonthlyLimit() == 0 ){
            clientRemainDay.setVisibility(View.GONE);
            clientRemainWeek.setVisibility(View.GONE);
            clientRemainMonth.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        else{
            clientRemainDay.setText(String.format("%.2f", cardInfoSerializable.getDailyLimit() - cardInfoSerializable.getDailyLimitConsumed()).replace(",",".") + typeLimit);
            clientRemainWeek.setText(String.format("%.2f", cardInfoSerializable.getWeeklyLimit() - cardInfoSerializable.getWeeklyLimitConsumed()).replace(",",".") + typeLimit);
            clientRemainMonth.setText(String.format("%.2f", cardInfoSerializable.getMonthlyLimit() - cardInfoSerializable.getMonthlyLimitConsumed()).replace(",",".") + typeLimit);

            if(cardInfoSerializable.getDailyLimit() - cardInfoSerializable.getDailyLimitConsumed() == 0)
                clientRemainDay.setTextColor(Color.RED);
            if(cardInfoSerializable.getWeeklyLimit() - cardInfoSerializable.getWeeklyLimitConsumed() == 0)
                clientRemainWeek.setTextColor(Color.RED);
            if(cardInfoSerializable.getMonthlyLimit() - cardInfoSerializable.getMonthlyLimitConsumed() == 0)
                clientRemainMonth.setTextColor(Color.RED);
        }

        clientAmountType.setText(getString(R.string.suma_disponibila));
        clientCardName.setText(cardInfoSerializable.getCustomerName() + " - " + cardInfoSerializable.getCardNumber() + "/" + cardInfoSerializable.getCardName());
        clientAmount.setText(cardInfoSerializable.getAllowedBalance() + " MDL");

        adapter = new AssortmentCardAdapter(context, R.layout.list_assortiment_view, cardInfoSerializable.getAssortiment());
        clientProducts.setAdapter(adapter);

        clientProducts.setOnItemClickListener((parent, view, position, id) -> {
            AssortmentCardSerializable item = adapter.getItem(position);
            Intent count = new Intent(context, CountProductActivity.class);
            count.putExtra("Identify", true);
            count.putExtra("Product", item);
            count.putExtra("ClientCardCode", cardId);
            count.putExtra("ClientMaxAvailable", cardInfoSerializable.getAllowedBalance());
            count.putExtra("LimitType", cardInfoSerializable.getLimitType() == LimitCardEnum.MDL ? LimitCardEnum.MDL : LimitCardEnum.Liter);
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

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}