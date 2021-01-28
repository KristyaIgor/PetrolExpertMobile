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
import md.intelectsoft.petrolmpos.network.pe.result.CashList;


/**
 * Created by Igor on 10.02.2020
 */

public class AdapterCashListDialog extends ArrayAdapter<CashList> {
    Context context;
    int resource;

    public AdapterCashListDialog(@NonNull Context context, int resource, @NonNull List<CashList> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    private static class ViewHolder {
        TextView address;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            convertView = inflater.inflate(resource,parent,false);

            viewHolder.address = convertView.findViewById(R.id.textCashName);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CashList item = getItem(position);
        if (item != null) {
            viewHolder.address.setText(String.valueOf(item.getCashName()));

        }

        return convertView;
    }
}
