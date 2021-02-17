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
import md.intelectsoft.petrolexpert.network.pe.result.AssortmentSerializable;


/**
 * Created by Igor on 10.02.2020
 */

public class AssortmentAdapter extends ArrayAdapter<AssortmentSerializable> {
    int layoutId;

    public AssortmentAdapter(@NonNull Context context, int resource, @NonNull List<AssortmentSerializable> objects) {
        super(context, resource, objects);
        this.layoutId = resource;
    }

    private static class ViewHolder {
        TextView productName, productPrice, productDiscount, titleDiscount;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if(convertView == null){
            convertView = inflater.inflate(layoutId ,parent,false);

            viewHolder.productName = convertView.findViewById(R.id.textProductName);
            viewHolder.productPrice = convertView.findViewById(R.id.txtPriceProduct);
            viewHolder.productDiscount = convertView.findViewById(R.id.textDiscountProduct);
            viewHolder.titleDiscount = convertView.findViewById(R.id.textTitleDiscount);


            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AssortmentSerializable item = getItem(position);

        viewHolder.productName.setText(item.getName());
        viewHolder.productPrice.setText(item.getPrice() + " MDL");

        if(item.getDiscount() == 0){
            viewHolder.titleDiscount.setVisibility(View.GONE);
            viewHolder.productDiscount.setVisibility(View.GONE);
        }
        else{
            viewHolder.titleDiscount.setVisibility(View.VISIBLE);
            viewHolder.productDiscount.setVisibility(View.VISIBLE);

            viewHolder.productDiscount.setText(String.valueOf(item.getDiscount()));
        }

        return convertView;
    }
}
