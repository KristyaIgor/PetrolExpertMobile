package md.intelectsoft.petrolexpert.adapters;

import android.content.Context;
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
        TextView productName, productPrice, productCount;
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
            viewHolder.productCount = convertView.findViewById(R.id.textCountProduct);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AssortmentCardSerializable item = getItem(position);

        viewHolder.productName.setText(item.getName());
        viewHolder.productPrice.setText(String.format("%.2f",item.getPrice()).replace(",",".") + " MDL");
//        viewHolder.productCount.setText(String.format("%.2f", item.getDailyLimit() - item.getDailyLimitConsumed()).replace(",","."));

        return convertView;
    }
}