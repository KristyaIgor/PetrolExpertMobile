package md.intelectsoft.petrolexpert;

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
import md.intelectsoft.petrolexpert.adapters.AssortmentCardAdapter;
import md.intelectsoft.petrolexpert.enums.LimitCardEnum;
import md.intelectsoft.petrolexpert.network.pe.result.AssortmentCardSerializable;
import md.intelectsoft.petrolexpert.network.pe.result.GetCardInfoSerializable;

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

    @BindView(R.id.textDisponibilForCard) TextView clientDispobinilHeader;

    @BindView(R.id.textView34) TextView clientDayHeader;
    @BindView(R.id.textDisponibilForCard8) TextView clientWeekHeader;
    @BindView(R.id.textView35) TextView clientMonthHeader;

    @BindView(R.id.divider16) View divider1;
    @BindView(R.id.divider2) View divider2;

    @BindView(R.id.listOfAvailableProducts) ListView clientProducts;

    AssortmentCardAdapter adapter;
    Context context;
    String cardId, cardName;

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
        cardName = intent.getStringExtra("ClientCardName");


        String typeLimit = "";
        if(cardInfoSerializable.getLimitType() == LimitCardEnum.MDL)
            typeLimit = "MDL";
        else {
            typeLimit = "L";
        }

        clientLimitDay.setText(cardInfoSerializable.getDailyLimit() == 0 ? "0 ": String.format("%.2f",cardInfoSerializable.getDailyLimit()).replace(",","."));
        clientLimitWeek.setText(cardInfoSerializable.getWeeklyLimit() == 0 ? "0 ": String.format("%.2f",cardInfoSerializable.getWeeklyLimit()).replace(",","."));
        clientLimitMonth.setText(cardInfoSerializable.getMonthlyLimit() == 0 ? "0 ": String.format("%.2f",cardInfoSerializable.getMonthlyLimit()).replace(",","."));

        if(cardInfoSerializable.getDailyLimit() == 0 && cardInfoSerializable.getWeeklyLimit() == 0 && cardInfoSerializable.getMonthlyLimit() == 0 ){
            clientRemainDay.setVisibility(View.GONE);
            clientRemainWeek.setVisibility(View.GONE);
            clientRemainMonth.setVisibility(View.GONE);
            clientDispobinilHeader.setVisibility(View.GONE);
            clientLimitDay.setVisibility(View.GONE);
            clientLimitWeek.setVisibility(View.GONE);
            clientLimitMonth.setVisibility(View.GONE);
            clientDayHeader.setVisibility(View.GONE);
            clientWeekHeader.setVisibility(View.GONE);
            clientMonthHeader.setVisibility(View.GONE);
            divider1.setVisibility(View.GONE);
            divider2.setVisibility(View.GONE);
        }
        else{
            clientRemainDay.setText(String.format("%.2f", cardInfoSerializable.getDailyLimit() - cardInfoSerializable.getDailyLimitConsumed()).replace(",","."));
            clientRemainWeek.setText(String.format("%.2f", cardInfoSerializable.getWeeklyLimit() - cardInfoSerializable.getWeeklyLimitConsumed()).replace(",","."));
            clientRemainMonth.setText(String.format("%.2f", cardInfoSerializable.getMonthlyLimit() - cardInfoSerializable.getMonthlyLimitConsumed()).replace(",","."));

            if(cardInfoSerializable.getDailyLimit() - cardInfoSerializable.getDailyLimitConsumed() == 0)
                clientRemainDay.setTextColor(Color.RED);
            if(cardInfoSerializable.getWeeklyLimit() - cardInfoSerializable.getWeeklyLimitConsumed() == 0)
                clientRemainWeek.setTextColor(Color.RED);
            if(cardInfoSerializable.getMonthlyLimit() - cardInfoSerializable.getMonthlyLimitConsumed() == 0)
                clientRemainMonth.setTextColor(Color.RED);
        }

        clientDispobinilHeader.append(" (" + typeLimit + ")");

        clientAmountType.setText(getString(R.string.suma_disponibila));
        clientCardName.setText(cardInfoSerializable.getCustomerName() + " - " + cardInfoSerializable.getCardNumber() + "/" + cardInfoSerializable.getCardName());
        clientAmount.setText(String.format("%.2f",cardInfoSerializable.getAllowedBalance()).replace(",", "."));

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
            count.putExtra("ClientCardName", cardName);
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