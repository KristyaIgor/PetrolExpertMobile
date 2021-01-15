package md.intelectsoft.petrolmpos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import md.intelectsoft.petrolmpos.R;
import md.intelectsoft.petrolmpos.network.pe.result.AssortmentSerializable;


/**
 * Created by Igor on 10.02.2020
 */

public class PaymentWithoutAdapter extends ArrayAdapter<String> {
    int layoutId;

    public PaymentWithoutAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.layoutId = resource;
    }

    private static class ViewHolder {
        TextView productName;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if(convertView == null){
            convertView = inflater.inflate(layoutId ,parent,false);

            viewHolder.productName = convertView.findViewById(R.id.textView7);



            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String item = getItem(position);

        viewHolder.productName.setText(item);


        return convertView;
    }
}
