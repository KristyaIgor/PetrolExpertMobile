package md.intelectsoft.petrolmpos;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolmpos.adapters.AssortmentAdapter;
import md.intelectsoft.petrolmpos.models.ToggleButton;
import md.intelectsoft.petrolmpos.network.pe.result.AssortmentSerializable;

@SuppressLint("NonConstantResourceId")
public class ProductsWithoutIndentingActivity extends AppCompatActivity {

    @BindView(R.id.listProductsWithoutIndenting) ListView listProducts;

    Context context;
    List<AssortmentSerializable> products;
    AssortmentAdapter adapter;

    AssortmentSerializable productSelected;

    @OnClick(R.id.imageBackToMainFromProducts) void onCloseActivity (){
        finish();
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
            productSelected = adapter.getItem(position);
            Intent count = new Intent(context, CountProductWithoutActivity.class);
            count.putExtra("Identify", false);
            count.putExtra("Product", productSelected);
            startActivityForResult(count,154);
        });

    }
}