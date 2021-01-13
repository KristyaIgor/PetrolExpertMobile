package md.intelectsoft.petrolmpos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolmpos.adapters.AssortmentAdapter;
import md.intelectsoft.petrolmpos.network.pe.result.AssortmentSerializable;
import md.intelectsoft.petrolmpos.network.pe.result.GetAssortment;
import md.intelectsoft.petrolmpos.network.pe.result.GetAssortmentSerializable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class ClientMyDiscountCardCorporativActivity extends AppCompatActivity {

    @BindView(R.id.textClientCardName) TextView clientCardName;
    @BindView(R.id.textCardBalance) TextView clientBalance;
    @BindView(R.id.textClientAmount) TextView clientAmount;

    @BindView(R.id.textClientLimitDay) TextView clientLimitDay;
    @BindView(R.id.textClientLimitWeek) TextView clientLimitWeek;
    @BindView(R.id.textClientLimitMonth) TextView clientLimitMonth;

    @BindView(R.id.listOfAvailableProducts) ListView clientProducts;

    AssortmentAdapter adapter;
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
        GetAssortmentSerializable assortmentSerializable = (GetAssortmentSerializable) intent.getSerializableExtra("ResponseClient");

        if(assortmentSerializable.getLimitType() == null){
            clientLimitDay.setText("0");
            clientLimitWeek.setText("0");
            clientLimitMonth.setText("0");
        }
        else{
            String typeLimit = "";
            if(assortmentSerializable.getLimitType().equals("Bani"))
                typeLimit = " MDL";
            else
                typeLimit = " L";

            clientLimitDay.setText(assortmentSerializable.getLimitDay() + typeLimit);
            clientLimitWeek.setText(assortmentSerializable.getWeeklyLimit() + typeLimit);
            clientLimitMonth.setText(assortmentSerializable.getLimitMount() + typeLimit);
        }

        clientCardName.setText(assortmentSerializable.getClientName());
        clientBalance.setText(assortmentSerializable.getBalanta() + " MDL");
        clientAmount.setText(assortmentSerializable.getClientAmount() + " MDL");

        adapter = new AssortmentAdapter(context, R.layout.list_assortiment_view, assortmentSerializable.getAssortmentList());
        clientProducts.setAdapter(adapter);
    }
}