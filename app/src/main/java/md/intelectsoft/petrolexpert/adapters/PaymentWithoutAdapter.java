package md.intelectsoft.petrolexpert.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import md.intelectsoft.petrolexpert.R;
import md.intelectsoft.petrolexpert.network.pe.result.stationSettings.PaymentTypeStation;


/**
 * Created by Igor on 10.02.2020
 */

public class PaymentWithoutAdapter extends ArrayAdapter<PaymentTypeStation> {
    int layoutId;

    public PaymentWithoutAdapter(@NonNull Context context, int resource, @NonNull List<PaymentTypeStation> objects) {
        super(context, resource, objects);
        this.layoutId = resource;
    }

    private static class ViewHolder {
        ImageView payImage;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        convertView = LayoutInflater.from(parent.getContext()).inflate(layoutId ,parent,false);

        viewHolder.payImage = convertView.findViewById(R.id.imagePaymentType);

        PaymentTypeStation item = getItem(position);
        convertView.setTag(item);

        if(item.getImage() != null && item.getImage().length > 0){
            Bitmap productImg = BitmapFactory.decodeByteArray(item.getImage(), 0, item.getImage().length);

            if(productImg != null) viewHolder.payImage.setImageBitmap(productImg);
        }
        if(!item.isEnabled())
            convertView.setEnabled(false);
        else
            convertView.setEnabled(true);

        return convertView;
    }
}
