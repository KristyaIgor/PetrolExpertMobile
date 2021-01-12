package md.intelectsoft.petrolmpos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import md.intelectsoft.petrolmpos.R;
import md.intelectsoft.petrolmpos.network.pe.result.Assortment;


/**
 * Created by Igor on 10.02.2020
 */

public class AssortmentAdapter extends ArrayAdapter<Assortment> {
    int layoutId;

    public AssortmentAdapter(@NonNull Context context, int resource, @NonNull List<Assortment> objects) {
        super(context, resource, objects);
        this.layoutId = resource;
    }

    private static class ViewHolder {
        TextView productName, productPrice;
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

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Assortment item = getItem(position);

        viewHolder.productName.setText(item.getName());
        viewHolder.productPrice.setText(item.getPrice() + " MDL");

        return convertView;
    }
}
