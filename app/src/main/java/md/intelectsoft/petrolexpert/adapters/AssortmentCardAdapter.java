package md.intelectsoft.petrolexpert.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import md.intelectsoft.petrolexpert.R;
import md.intelectsoft.petrolexpert.network.pe.result.AssortmentCardSerializable;

import static md.intelectsoft.petrolexpert.ClientMyDiscountCardCorporativActivity.round;


/**
 * Created by Igor on 10.02.2020
 */

public class AssortmentCardAdapter extends ArrayAdapter<AssortmentCardSerializable> {
    int layoutId;

    public AssortmentCardAdapter(@NonNull Context context, int resource, @NonNull List<AssortmentCardSerializable> objects) {
        super(context, resource, objects);
        this.layoutId = resource;
    }

    private static class ViewHolder {
        TextView productName, productPrice, productDiscount;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_assortiment_view ,parent,false);

            viewHolder.productName = convertView.findViewById(R.id.text_view_asl_name);
            viewHolder.productPrice = convertView.findViewById(R.id.txt_asl_price);
            viewHolder.productDiscount = convertView.findViewById(R.id.textDiscount);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AssortmentCardSerializable item = getItem(position);

        viewHolder.productName.setText(item.getName());

        if(item.getPriceDiscount() > 0 && item.getPriceDiscount() < item.getPrice()){
            viewHolder.productDiscount.setVisibility(View.VISIBLE);
            viewHolder.productPrice.setText(String.format("%.2f",item.getPrice()).replace(",",".") + " MDL");
            viewHolder.productDiscount.setText(String.format("%.2f", item.getPriceDiscount()).replace(",",".") + " MDL");
            viewHolder.productPrice.setTextSize(14);
//            viewHolder.productPrice.setTextColor(getContext().getColor(R.color.red));
            viewHolder.productPrice.setPaintFlags(viewHolder.productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            viewHolder.productPrice.setText(String.format("%.2f", item.getPrice()).replace(",", ".") + " MDL");
            viewHolder.productDiscount.setVisibility(View.GONE);
        }
//        viewHolder.productCount.setText(String.format("%.2f", item.getDailyLimit() - item.getDailyLimitConsumed()).replace(",","."));

        return convertView;
    }
}
