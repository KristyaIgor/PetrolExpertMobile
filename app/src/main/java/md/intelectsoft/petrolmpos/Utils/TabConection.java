package md.intelectsoft.petrolmpos.Utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import md.intelectsoft.petrolmpos.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;
import static md.intelectsoft.petrolmpos.Utils.NetworkUtils.generateURL_Ping;
import static md.intelectsoft.petrolmpos.Utils.NetworkUtils.generateURL_RegDev;
import static md.intelectsoft.petrolmpos.Utils.NetworkUtils.getResponse_from_Ping;
import static md.intelectsoft.petrolmpos.Utils.NetworkUtils.getResponse_from_RegDev;

public class TabConection extends Fragment {
    EditText IPField;
    TextView ID_ET;
    TextView ASL_cont;
    TextView Folder_count, Bill_close_count;
    EditText portField;
    final String IP_save = "IP";
    final String Port_save = "Port";
    final String ID_resp = "ID_Mob";

    private String port;
    private String ip_;
    private String id_tel;
    private String id_in_base;
    SharedPreferences sPref;
    ProgressBar progress_Bar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_conect, container, false);

        IPField = rootView.findViewById(R.id.et_search_ip);
        portField =rootView.findViewById(R.id.et_search_port);
        TextView test = rootView.findViewById(R.id.btn_test);
        ID_ET =rootView.findViewById(R.id.dev_id);
        TextView status_id = rootView.findViewById(R.id.id_status);
        progress_Bar = rootView.findViewById(R.id.progressBar_setting);
        sPref = getActivity().getSharedPreferences("Save setting", MODE_PRIVATE);
        loadText();
        final TelephonyManager tm = (TelephonyManager) getActivity().getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }


        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getActivity().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        final String deviceId = deviceUuid.toString();
        status_id.setText(deviceId);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("deviceId", deviceId);
        ed.apply();

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_Bar.setVisibility(ProgressBar.VISIBLE);
                port = portField.getText().toString();
                ip_ = IPField.getText().toString();
                id_tel = deviceId;
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString(IP_save, IPField.getText().toString());
                ed.putString(Port_save, portField.getText().toString());
                ed.apply();
                URL generatedURL = generateURL_Ping(ip_,port);
                new querryPing().execute(generatedURL);
            }
        });
        return rootView;
    }
    private void loadText () {
        IPField.setText(sPref.getString(IP_save, ""));
        portField.setText(sPref.getString(Port_save, ""));
        ID_ET.setText(sPref.getString(ID_resp,""));
    }
    class querryPing extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response="";
            try {
                response = getResponse_from_Ping(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (!response.equals("")) {
                try {
                    JSONObject Result = new JSONObject(response);
                    boolean ping_result = Result.getBoolean("PingResult");
                    if(ping_result){
                        IPField.setBackgroundResource(R.drawable.ping_true);
                        URL generateURLRegDev = generateURL_RegDev(ip_, port, id_tel,"Android","0","0");
                        new querryReg().execute(generateURLRegDev);
                    }else{
                        progress_Bar.setVisibility(ProgressBar.INVISIBLE);
                        IPField.setBackgroundResource(R.drawable.ping_false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                progress_Bar.setVisibility(ProgressBar.INVISIBLE);
                IPField.setBackgroundResource(R.drawable.ping_false);
            }

        }
    }
    class querryReg extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = "null";
            try {
                response = getResponse_from_RegDev(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (!response.equals("null")) {
                try {
                    JSONObject Result = new JSONObject(response);
                    JSONObject RegisterResult = Result.getJSONObject("RegisterDeviceResult");
                    boolean no_error = RegisterResult.getBoolean("NoError");
                    boolean Registred = RegisterResult.getBoolean("Registred");
                    if(no_error && Registred){
                        String number=  RegisterResult.getString("RegistredNumber");
                        progress_Bar.setVisibility(ProgressBar.INVISIBLE);
                        ID_ET.setText(number);
                        id_in_base = number;
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString(ID_resp, ID_ET.getText().toString());
                        ed.apply();
                    }else{
                        String ErrorMesage=  RegisterResult.getString("ErrorMessage");
                        progress_Bar.setVisibility(ProgressBar.INVISIBLE);
                        AlertDialog.Builder exit = new AlertDialog.Builder(getContext());
                        exit.setTitle("Dispozitivul nu este inregistrat!!");
                        exit.setMessage(ErrorMesage);
                        exit.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        exit.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString(ID_resp, ID_ET.getText().toString());
                ed.apply();
                progress_Bar.setVisibility(ProgressBar.INVISIBLE);
                AlertDialog.Builder exit = new AlertDialog.Builder(getContext());
                exit.setTitle("Dispozitivul nu este inregistrat!!");
                exit.setMessage("Verificati setarile!");
                exit.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                exit.show();

            }

        }
    }

}
