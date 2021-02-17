package md.intelectsoft.petrolexpert.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;
import md.intelectsoft.petrolexpert.R;
import md.intelectsoft.petrolexpert.network.pe.body.registerBill.BillRegistered;
import md.intelectsoft.petrolexpert.network.pe.body.registerBill.LineBill;
import md.intelectsoft.petrolexpert.network.pe.body.registerBill.PaymentBill;


/**
 * Created by Igor on 23.12.2019
 */

public class BillListAdapter extends RealmBaseAdapter<BillRegistered> implements ListAdapter {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    TimeZone timeZone = TimeZone.getTimeZone("Europe/Chisinau");

    private static class ViewHolder {
        TextView nameProduct, countProduct, dateBill, authorBill, stationName, sumAndPay, payType;
    }


    public BillListAdapter(@Nullable OrderedRealmCollection<BillRegistered> data) {
        super(data);
        simpleDateFormat.setTimeZone(timeZone);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bill_view, parent, false);
            viewHolder = new ViewHolder();
           //find views for id
            viewHolder.nameProduct = convertView.findViewById(R.id.textProductNameInBill);
            viewHolder.countProduct = convertView.findViewById(R.id.textProductQuantityInBill);
            viewHolder.dateBill = convertView.findViewById(R.id.textBillDate);
            viewHolder.authorBill = convertView.findViewById(R.id.textAuthorBill);
            viewHolder.stationName = convertView.findViewById(R.id.textCashNameInBill);
            viewHolder.sumAndPay = convertView.findViewById(R.id.textSumAndPayType);
            viewHolder.payType = convertView.findViewById(R.id.textPaymentType);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            BillRegistered item = adapterData.get(position);
            List<LineBill> lines = item.getLines();
            List<PaymentBill> paymentBills = item.getPaymentBills();

            viewHolder.nameProduct.setText(lines.get(0).getName());
            viewHolder.countProduct.setText(String.format("%.2f",lines.get(0).getCount()) + " L");
            viewHolder.dateBill.setText(simpleDateFormat.format(item.getDate()));
            viewHolder.authorBill.setText(item.getAuthorName());
            viewHolder.stationName.setText(item.getCashName());
            String payMethod = "";
            if(paymentBills.get(0).getPaymentCode() == 0)
                payMethod = "Contul clientului";
            viewHolder.payType.setText(payMethod);
            viewHolder.sumAndPay.setText(String.format("%.2f",paymentBills.get(0).getSum()) + " MDL");

        }
        return convertView;
    }
}
