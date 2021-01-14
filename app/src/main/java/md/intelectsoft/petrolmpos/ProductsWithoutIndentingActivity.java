package md.intelectsoft.petrolmpos;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolmpos.adapters.AssortmentAdapter;
import md.intelectsoft.petrolmpos.network.pe.result.AssortmentSerializable;

@SuppressLint("NonConstantResourceId")
public class ProductsWithoutIndentingActivity extends AppCompatActivity {

    @BindView(R.id.continueSales) Button continueSales;
    @BindView(R.id.listProductsWithoutIndenting) ListView listProducts;

    Context context;
    List<AssortmentSerializable> products;
    AssortmentAdapter adapter;

    @OnClick(R.id.imageBackToMainFromProducts) void onCloseActivity (){
        finish();
    }
    @OnClick(R.id.continueSales) void onContinue (){

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_whitout_indentify);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        context = this;

        Intent intent = getIntent();
        products = (List<AssortmentSerializable>) intent.getSerializableExtra("ResponseAssortment");

        adapter = new AssortmentAdapter(context, R.layout.list_assortiment_view_without, products);
        listProducts.setAdapter(adapter);

        listProducts.setOnItemClickListener((parent, view, position, id) -> {
            AssortmentSerializable item = adapter.getItem(position);
            listProducts.setItemChecked(position, true);
            continueSales.setVisibility(View.VISIBLE);
        });
    }
}