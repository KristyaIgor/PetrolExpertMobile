package md.intelectsoft.petrolexpert;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import md.intelectsoft.petrolexpert.adapters.BillListAdapter;
import md.intelectsoft.petrolexpert.network.pe.body.registerBill.BillRegistered;

@SuppressLint("NonConstantResourceId")
public class BillListActivity extends AppCompatActivity {
    @BindView(R.id.listBills) ListView listBills;

    BillListAdapter adapter;

    @OnClick(R.id.imageBackFromBillList) void onBack(){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        RealmResults<BillRegistered> bills = Realm.getDefaultInstance().where(BillRegistered.class).findAll();
        if(!bills.isEmpty()){
            bills = bills.sort("date", Sort.DESCENDING);
            adapter = new BillListAdapter(bills);
            listBills.setAdapter(adapter);
        }
    }
}