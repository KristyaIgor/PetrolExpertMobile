package md.intelectsoft.petrolmpos.Utils;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;

import md.intelectsoft.petrolmpos.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class TabOther extends Fragment {
    SharedPreferences sPref;
    EditText inputCode;
    TextView showKeyLic;
    Button btn_verific;
    ProgressDialog pgH;

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            byte[] encode = Base64.encode(messageDigest,0);
            String respencode = new String(encode).toUpperCase();
            // Create String
            String digits="";
            for (int i = 0; i < respencode.length(); i++) {
                char chrs = respencode.charAt(i);
                if (!Character.isDigit(chrs))
                    digits = digits+chrs;
            }
            String keyLic = "";
            for (int k=0;k<digits.length();k++){
                if (Character.isLetter(digits.charAt(k))){
                    keyLic=keyLic + digits.charAt(k);
                }
            }
            keyLic=keyLic.substring(0,8);

            return keyLic;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_other, container, false);

        inputCode=rootView.findViewById(R.id.et_key);
        showKeyLic=rootView.findViewById(R.id.code_lic);
        btn_verific=rootView.findViewById(R.id.btn_verify);
        pgH =new ProgressDialog(getContext());
        sPref = getActivity().getSharedPreferences("Save setting", MODE_PRIVATE);
        final SharedPreferences.Editor inpSet = sPref.edit();

        final TelephonyManager tm = (TelephonyManager) getActivity().getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, androidId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            }
        }

        tmDevice = "KitKat" + tm.getDeviceId() + "Mars";
        androidId = "Igor" + Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID) + " Cristea";

        UUID deviceUuid = new UUID( md5(androidId).hashCode(), ((long)tmDevice.hashCode() << 32)| "IgorDevelop".hashCode());
        String deviceId = deviceUuid.toString();
        deviceId = deviceId.replace("-","");
        deviceId = deviceId.substring(8);

        String code = sPref.getString("CodeLicense","");
        if (code.equals("")) {
            for (int k = 0; k < deviceId.length(); k++) {
                if (Character.isLetter(deviceId.charAt(k))) {
                    code = code + deviceId.charAt(k);
                }
            }
            if (code.length() < 8) {
                while (code.length() < 8) {
                    Random rnd = new Random();
                    String randomLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                    code = code + randomLetters.charAt(rnd.nextInt(randomLetters.length()));
                }
            } else {
                code = code.substring(0, 8);
            }
            showKeyLic.setText(code.toUpperCase());
            SharedPreferences.Editor keyEdit = sPref.edit();
            keyEdit.putString("CodeLicense",code.toUpperCase());
            keyEdit.apply();
        }else
        {
            showKeyLic.setText(code.toUpperCase());
        }
        final String internKey = md5(code.toUpperCase() + "ENCEFALOMIELOPOLIRADICULONEVRITA");

        inputCode.setText(sPref.getString("KeyText",""));
        btn_verific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = inputCode.getText().toString().toUpperCase();
                SharedPreferences.Editor ed = sPref.edit();
                if (Test(key,internKey)){
                    inputCode.setBackgroundResource(R.drawable.ping_true);
                    ed.putBoolean("Key",true);
                    ed.putString("KeyText",key);
                    ed.apply();
                }else{
                    inputCode.setBackgroundResource(R.drawable.ping_false);
                    ed.putBoolean("Key",false);
                    ed.putString("KeyText",inputCode.getText().toString().toUpperCase());
                    ed.apply();
                }
            }
        });
        return rootView;
    }

    public boolean Test (String key,String entern_key){
            if (key.equals(entern_key)){
                return true;
            }
        return false;
    }
}
