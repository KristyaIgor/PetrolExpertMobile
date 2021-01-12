package md.intelectsoft.petrolmpos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;

import md.intelectsoft.petrolmpos.network.pe.result.GetAssortment;
import md.intelectsoft.petrolmpos.network.pe.result.GetAssortmentSerializable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientMyDiscountCardCorporativActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_my_discount_card_corporativ);

        Intent intent = getIntent();
        GetAssortmentSerializable assortmentSerializable = (GetAssortmentSerializable) intent.getSerializableExtra("ResponseClient");
        String clientName = assortmentSerializable.getClientName();
        Log.e("Tag", "onCreate: " + clientName );
    }
}