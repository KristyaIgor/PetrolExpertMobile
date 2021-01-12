package md.intelectsoft.petrolmpos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import md.intelectsoft.petrolmpos.Utils.SPFHelp;
import md.intelectsoft.petrolmpos.adapters.AssortmentAdapter;
import md.intelectsoft.petrolmpos.network.pe.PERetrofitClient;
import md.intelectsoft.petrolmpos.network.pe.PEServiceAPI;
import md.intelectsoft.petrolmpos.network.pe.result.Assortment;
import md.intelectsoft.petrolmpos.network.pe.result.GetAssortment;
import md.intelectsoft.petrolmpos.network.pe.result.RegisterDevice;
import md.intelectsoft.petrolmpos.network.pe.result.SimpleResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static md.intelectsoft.petrolmpos.Utils.NetworkUtils.generateURL_CreateBill;
import static md.intelectsoft.petrolmpos.Utils.NetworkUtils.generateURL_GetAssortiment;
import static md.intelectsoft.petrolmpos.Utils.NetworkUtils.generateURL_PrintX;
import static md.intelectsoft.petrolmpos.Utils.NetworkUtils.getResponse_from_GetAssortiment;
import static md.intelectsoft.petrolmpos.Utils.NetworkUtils.getResponse_from_PrintX;

public class MainActivity extends AppCompatActivity {
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    Context context;
    SimpleAdapter simpleAdapterASL;
    TextView txtStatusCard,txtName,txtBalance,txtLimit;
    ProgressBar progressBar, wait_progressbar;
    int anInt;
    ProgressDialog pgH;
    ListView list_assortiment;
    ArrayList<HashMap<String, Object>> asl_list = new ArrayList<>();
    String IP_text,PORT_text,ID_text;
    SharedPreferences sPref;
    final String IP_save = "IP";
    final String Port_save = "Port";
    final String ID_save = "deviceId";
    AlertDialog setCount ;
    CountDownTimer time_out;

    PEServiceAPI peServiceAPI;
    String deviceId, cardId, typeLimit;
    AssortmentAdapter adapter;


    public void requestMultiplePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CAMERA

                },
                12);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 12 && grantResults.length == 3) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
            } else if(grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            } else if (grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 201);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        context = this;
        wait_progressbar =findViewById(R.id.progressBar_o_get_asl);
        txtStatusCard=findViewById(R.id.txt_status_card);
        progressBar=findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        list_assortiment=findViewById(R.id.ListAsortiment);
        txtBalance= findViewById(R.id.txtBalance_card);
        txtName=findViewById(R.id.txtName_card);
        txtName=findViewById(R.id.txtName_card);
        txtLimit = findViewById(R.id.txtLimits_card);
        wait_progressbar.setVisibility(View.INVISIBLE);

        pgH=new ProgressDialog(context);
        sPref = getSharedPreferences("Save setting", MODE_PRIVATE);

        String uri = SPFHelp.getInstance().getString("URI", "http://0.0.0.0:1111");
        deviceId = SPFHelp.getInstance().getString("deviceId", null);

        peServiceAPI = PERetrofitClient.getPEService(uri);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
        requestMultiplePermissions();

        list_assortiment.setOnItemClickListener((parent, view, position, id) -> {
            Assortment itemSelected = adapter.getItem(position);

            double countAvailable = itemSelected.getCount();
            if (countAvailable >= 1.0) {
                    time_out.cancel();
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.INVISIBLE);
                    LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.select_count_petrol, null);

                    setCount = new AlertDialog.Builder(context).create();
                    setCount.setCancelable(false);
                    setCount.setView(dialogView);

                    Button btnOK = dialogView.findViewById(R.id.btn_ok);
                    Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
                    final TextView txtTotalCount = dialogView.findViewById(R.id.txtTotalCount);
                    final TextView txtNames = dialogView.findViewById(R.id.txtName_toplivo);
                    final TextView txtCountMaxim = dialogView.findViewById(R.id.txtcount_maxim);

                    TextView txt1 = dialogView.findViewById(R.id.txt_btn_1);
                    TextView txt2 = dialogView.findViewById(R.id.txt_btn_2);
                    TextView txt3 = dialogView.findViewById(R.id.txt_btn_3);

                    TextView txt4 = dialogView.findViewById(R.id.txt_btn_4);
                    TextView txt5 = dialogView.findViewById(R.id.txt_btn_5);
                    TextView txt6 = dialogView.findViewById(R.id.txt_btn_6);

                    TextView txt7 = dialogView.findViewById(R.id.txt_btn_7);
                    TextView txt8 = dialogView.findViewById(R.id.txt_btn_8);
                    TextView txt9 = dialogView.findViewById(R.id.txt_btn_9);

                    TextView txt0 = dialogView.findViewById(R.id.txt_btn_0);
                    TextView txt_point = dialogView.findViewById(R.id.txt_btn_point);
                    TextView txt_delete = dialogView.findViewById(R.id.txt_btn_back);

                    txtNames.setText(itemSelected.getName());
                    txtCountMaxim.setText("max:" + countAvailable + " " +typeLimit);
                    final double finalCount_number = countAvailable;

                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String sumTotalcount = txtTotalCount.getText().toString();

                            if (!sumTotalcount.equals("") && !sumTotalcount.equals(".")) {
                                double double_countTotal = Double.valueOf(sumTotalcount);
                                if (double_countTotal >= 1) {
                                    setCount.dismiss();


                                    saveBill(cardId,itemSelected.getPriceLineID(), itemSelected.getPrice(), txtTotalCount.getText().toString(), "0", "0");
                                } else {
                                    Toast.makeText(MainActivity.this, "Introduceti cantitatea mai mare de 1!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Introduceti cantitatea!", Toast.LENGTH_SHORT).show();
                                txtTotalCount.setText("");
                            }
                        }
                    });
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setCount.dismiss();
                            txtStatusCard.setText("Card indepartat.");
                            adapter.clear();
                            list_assortiment.setAdapter(adapter);
                            txtName.setText("");
                            txtBalance.setText("");
                            txtLimit.setText("");
                        }
                    });


                    txt0.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String senf = txtTotalCount.getText().toString() + "0";
                            double nolevoi = Double.valueOf(senf);

                            if (nolevoi <= finalCount_number)
                                txtTotalCount.append("0");
                        }
                    });
                    txt1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String senf = txtTotalCount.getText().toString() + "1";
                            double nolevoi = Double.valueOf(senf);

                            if (nolevoi <= finalCount_number)
                                txtTotalCount.append("1");
                        }
                    });

                    txt2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String senf = txtTotalCount.getText().toString() + "2";
                            double nolevoi = Double.valueOf(senf);

                            if (nolevoi <= finalCount_number)
                                txtTotalCount.append("2");
                        }
                    });
                    txt3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String senf = txtTotalCount.getText().toString() + "3";
                            double nolevoi = Double.valueOf(senf);

                            if (nolevoi <= finalCount_number)
                                txtTotalCount.append("3");
                        }
                    });
                    txt4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String senf = txtTotalCount.getText().toString() + "4";
                            double nolevoi = Double.valueOf(senf);

                            if (nolevoi <= finalCount_number)
                                txtTotalCount.append("4");
                        }
                    });
                    txt5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String senf = txtTotalCount.getText().toString() + "5";
                            double nolevoi = Double.valueOf(senf);

                            if (nolevoi <= finalCount_number)
                                txtTotalCount.append("5");
                        }
                    });
                    txt6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String senf = txtTotalCount.getText().toString() + "6";
                            double nolevoi = Double.valueOf(senf);

                            if (nolevoi <= finalCount_number)
                                txtTotalCount.append("6");
                        }
                    });
                    txt7.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String senf = txtTotalCount.getText().toString() + "7";
                            double nolevoi = Double.valueOf(senf);

                            if (nolevoi <= finalCount_number)
                                txtTotalCount.append("7");
                        }
                    });
                    txt8.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String senf = txtTotalCount.getText().toString() + "8";
                            double nolevoi = Double.valueOf(senf);

                            if (nolevoi <= finalCount_number)
                                txtTotalCount.append("8");
                        }
                    });
                    txt9.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String senf = txtTotalCount.getText().toString() + "9";
                            double nolevoi = Double.valueOf(senf);

                            if (nolevoi <= finalCount_number)
                                txtTotalCount.append("9");
                        }
                    });
                    txt_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String test = txtTotalCount.getText().toString();
                            if (!txtTotalCount.getText().toString().equals("")) {
                                txtTotalCount.setText(test.substring(0, test.length() - 1));
                            }
                        }
                    });
                    txt_point.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String test = txtTotalCount.getText().toString();
                            boolean contains = false;
                            for (int i = 0; i < test.length(); i++) {
                                String chars = String.valueOf(test.charAt(i));
                                if (chars.equals(".")) {
                                    contains = true;
                                }
                            }
                            if (!contains) {
                                txtTotalCount.append(".");
                            }

                        }
                    });

                    setCount.show();
                }
        });

        registerDevice();

        readFromIntent(getIntent());

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
    }

    private void registerDevice() {
        Call<RegisterDevice> call = peServiceAPI.registerDevice("7df311e5-e0f7-4878-b9b6-b8678b753537", "testTerminal", "0" , "0");
        call.enqueue(new Callback<RegisterDevice>() {
            @Override
            public void onResponse(Call<RegisterDevice> call, Response<RegisterDevice> response) {
                RegisterDevice device = response.body();
                Toast.makeText(MainActivity.this, "Registered" + device.getNoError() + " number: " + device.getRegistred() + device.getRegistredNumber(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<RegisterDevice> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAssortment(String id){
        cardId = id;
        Call<GetAssortment> call = peServiceAPI.getAssortment("7df311e5-e0f7-4878-b9b6-b8678b753537", id,"0", "0");
        call.enqueue(new Callback<GetAssortment>() {
            @Override
            public void onResponse(Call<GetAssortment> call, Response<GetAssortment> response) {
                GetAssortment getAssortment = response.body();
                wait_progressbar.setVisibility(View.INVISIBLE);
                if(getAssortment != null){
                    if(getAssortment.getNoError()){
                        typeLimit = getAssortment.getLimitType();
                        txtName.setText(getAssortment.getClientName());
                        txtBalance.setText(getAssortment.getBalanta() + " MDL");
                        txtLimit.setText("Z: " + getAssortment.getLimitDay()
                                + " , S: " + getAssortment.getWeeklyLimit()
                                + " , L: " + getAssortment.getLimitMount() + " " + typeLimit);

                        List<Assortment> assortmentList = getAssortment.getAssortmentList();
                        if(assortmentList != null && assortmentList.size() > 0){
                            adapter = new AssortmentAdapter(context, R.layout.list_assortiment_view, assortmentList);
                            list_assortiment.setAdapter(adapter);
                        }

                        anInt = 10;
                        progressBar.setProgress(anInt);
                        progressBar.setVisibility(View.VISIBLE);
                        time_out = new CountDownTimer(10000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                anInt--;
                                progressBar.setProgress((int) anInt * 10);
                            }

                            public void onFinish() {
                                txtStatusCard.setText("Card indepartat.");
                                adapter.clear();
                                list_assortiment.setAdapter(adapter);
                                progressBar.setProgress(0);
                                progressBar.setVisibility(View.INVISIBLE);
                                txtName.setText("");
                                txtBalance.setText("");
                                txtLimit.setText("");
                            }
                        }.start();

                    }else error("Eroare de conexiune!Mesaj: " + getAssortment.getErrorMessage());
                }
                else error("Nu a fost primit nici un raspuns!" );
            }

            @Override
            public void onFailure(Call<GetAssortment> call, Throwable t) {
                wait_progressbar.setVisibility(View.INVISIBLE);
                error("Eroare de conexiune!" + t.getMessage());
            }
        });
    }

    private void saveBill(String cardId, String priceLineID, double price, String count, String lat, String lon){
        Call<SimpleResponse> call = peServiceAPI.createBill("7df311e5-e0f7-4878-b9b6-b8678b753537", cardId, priceLineID, price, count, lat, lon);
        pgH.setMessage("Salvarea bonului...");
        pgH.setIndeterminate(true);
        pgH.setCancelable(false);
        pgH.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> {
            call.cancel();
            if(call.isCanceled())
                pgH.dismiss();
        });
        pgH.show();

        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                SimpleResponse simpleResponse = response.body();
                pgH.dismiss();
                if(simpleResponse.getNoError()){
                    Toast.makeText(MainActivity.this, "Tranzactia efectuata cu succes!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "Eroare! Mesaj: " + simpleResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
                txtStatusCard.setText("Card indepartat.");
                asl_list.clear();
                list_assortiment.setAdapter(simpleAdapterASL);
                txtName.setText("");
                txtBalance.setText("");
                txtLimit.setText("");
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                pgH.dismiss();
                Toast.makeText(MainActivity.this, "Eroare! Mesaj: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                txtStatusCard.setText("Card indepartat.");
                asl_list.clear();
                list_assortiment.setAdapter(simpleAdapterASL);
                txtName.setText("");
                txtBalance.setText("");
                txtLimit.setText("");
            }
        });
    }

    private void error(String text){
        anInt = 5;
        txtStatusCard.setText(text);
        txtStatusCard.setTextColor(getResources().getColor(R.color.colorAccent));
        new CountDownTimer(5000, 100) {

            public void onTick(long millisUntilFinished) {
                anInt--;
                progressBar.setProgress((int) anInt * 10 / (2000 / 1000));
            }

            public void onFinish() {
                txtStatusCard.setText("Card indepartat.");
                txtStatusCard.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }.start();
    }

    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if (tagFromIntent!=null) {
                MifareUltralight mUltra = MifareUltralight.get(tagFromIntent);
                if (mUltra != null) {
                    asl_list.clear();
                    list_assortiment.setAdapter(simpleAdapterASL);
                    try {
                        mUltra.connect();
                        StringBuilder sb = new StringBuilder();
                        byte[] pages = mUltra.readPages(0);
                        for (byte page : pages) {
                            int b = page & 0xff;
                            if (b < 0x10)
                                sb.append("");
                            sb.append(b);
                        }
                        Log.d("NFC", "MifareUltralight " + sb.toString());

                        wait_progressbar.setVisibility(View.VISIBLE);

                        byte[] id = tagFromIntent.getId();
                        txtStatusCard.setText("Cardul: " + toReversedHex(id));

                        getAssortment(sb.toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            mUltra.close();
                            Log.d("NFC", "MifareUltralight disconected");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                boolean auth = false;
                MifareClassic mfc = MifareClassic.get(tagFromIntent);
                if (mfc!=null) {
                    asl_list.clear();
                    list_assortiment.setAdapter(simpleAdapterASL);
                    try {
                        String metaInfo = "";
                        //Enable I/O operations to the tag from this TagTechnology object.
                        mfc.connect();
//                    int type = mfc.getType();
//                    int sectorCount = mfc.getSectorCount();
//                    String typeS = "";
//                    switch (type) {
//                        case MifareClassic.TYPE_CLASSIC:
//                            typeS = "TYPE_CLASSIC";
//                            break;
//                        case MifareClassic.TYPE_PLUS:
//                            typeS = "TYPE_PLUS";
//                            break;
//                        case MifareClassic.TYPE_PRO:
//                            typeS = "TYPE_PRO";
//                            break;
//                        case MifareClassic.TYPE_UNKNOWN:
//                            typeS = "TYPE_UNKNOWN";
//                            break;
//                    }
//                    metaInfo += "Card type：" + typeS + "\n with" + sectorCount + " Sectors\n, "
//                            + mfc.getBlockCount() + " Blocks\nStorage Space: " + mfc.getSize() + "B\n";
                        StringBuilder sb = new StringBuilder();
                        auth = mfc.authenticateSectorWithKeyA(0, MifareClassic.KEY_DEFAULT);
                        int bCount;
                        int bIndex;

                        if (auth) {
                            //metaInfo += "Sector " + j + ": Verified successfullyn";
                            //bCount = mfc.getBlockCountInSector(0);
                            //bIndex = mfc.sectorToBlock(0);
//                            for (int i = 0; i < 1; i++) {
                            byte[] data = mfc.readBlock(0);

                            for (byte page : data) {
                                int b = page & 0xff;
                                if (b < 0x10)
                                    sb.append("");
                                sb.append(b);
                            }
//                                bIndex++;
//                            }
                        }
                        else {
                            metaInfo += "Sector " + 0 + ": Verified failure\n";
                            Log.d("Error NFC", metaInfo);
                        }
//                    }
                        Log.d("NFC", "MifareClassic " + sb.toString());

                        wait_progressbar.setVisibility(View.VISIBLE);

                        byte[] id = tagFromIntent.getId();
                        txtStatusCard.setText("Cardul: " + toReversedHex(id));

                        getAssortment(sb.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            mfc.close();
                            Log.d("NFC", "MifareClassic disconected");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }else {
            Log.e("Error NFC", "Unknown intent " + intent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        readFromIntent(intent);
    }
    @Override
    public void onPause(){
        super.onPause();
        if (NfcAdapter.getDefaultAdapter(this) != null) nfcAdapter.disableForegroundDispatch(this);
    }
    @Override
    public void onResume(){
        super.onResume();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
        }

    }
    private String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append("-");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString().toUpperCase();
    }

    class AsyncTask_CreateBill extends AsyncTask<URL, String, String> {
        @Override
        protected String doInBackground(URL... urls) {
            String response="false";
            try {
                response = getResponse_from_GetAssortiment(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if(!response.equals("false")) {
                try {
                    JSONObject responseAsl = new JSONObject(response);
                    JSONObject GetAssrtmentResult = responseAsl.getJSONObject("CreateBillResult");
                    boolean ErrorCode = GetAssrtmentResult.getBoolean("NoError");
                    if (ErrorCode) {
                        pgH.dismiss();
                        String ErrorMesage = GetAssrtmentResult.getString("ErrorMessage");
                        AlertDialog.Builder exit = new AlertDialog.Builder(MainActivity.this);
                        exit.setTitle("Atentie!");
                        exit.setMessage("Tranzactia efectuata cu succes!");
                        exit.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                txtStatusCard.setText("Card indepartat.");
                                asl_list.clear();
                                list_assortiment.setAdapter(simpleAdapterASL);
                                txtName.setText("");
                                txtBalance.setText("");
                                txtLimit.setText("");
                                dialog.dismiss();
                            }
                        });
                        exit.show();
                    } else {
                        pgH.dismiss();
                        String ErrorMesage = GetAssrtmentResult.getString("ErrorMessage");
                        AlertDialog.Builder exit = new AlertDialog.Builder(MainActivity.this);
                        exit.setTitle("Atentie!");
                        exit.setMessage(ErrorMesage);
                        exit.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                txtStatusCard.setText("Card indepartat.");
                                asl_list.clear();
                                list_assortiment.setAdapter(simpleAdapterASL);
                                txtName.setText("");
                                txtBalance.setText("");
                                txtLimit.setText("");
                                dialog.dismiss();
                            }
                        });
                        exit.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                pgH.dismiss();

                AlertDialog.Builder exit = new AlertDialog.Builder(MainActivity.this);
                exit.setTitle("Atentie!");
                exit.setMessage("Nu este raspuns de la serviciu.");
                exit.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        txtStatusCard.setText("Card indepartat.");
                        asl_list.clear();
                        list_assortiment.setAdapter(simpleAdapterASL);
                        txtName.setText("");
                        txtBalance.setText("");
                        txtLimit.setText("");
                        dialog.dismiss();
                    }
                });
                exit.show();
            }


        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_started, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_setting_start :{
                Intent setting_activity = new Intent(".SettingActivityPetrol");
                startActivityForResult(setting_activity,10);
            }break;
            case R.id.action_print_x : {
                pgH.setMessage("Asteptati...");
                pgH.setIndeterminate(true);
                pgH.setCancelable(false);
                pgH.show();
                IP_text=sPref.getString(IP_save, "");
                PORT_text=sPref.getString(Port_save, "");
                ID_text=sPref.getString(ID_save,"");
                URL URLPrintX  = generateURL_PrintX(IP_text,PORT_text,ID_text);
                new querryPrintX().execute(URLPrintX);
            }break;
            case R.id.action_exit : {
                finish();
            }break;
            case R.id.action_scan : {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(".ScannedBarcodeActivity"),303);
                }else {
                    ActivityCompat.requestPermissions(MainActivity.this, new
                            String[]{Manifest.permission.CAMERA}, 201);
                }

            }break;

        }
        return super.onOptionsItemSelected(item);
    }
    class querryPrintX extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = "";
            try {
                response = getResponse_from_PrintX(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if(!response.equals("")){
                pgH.dismiss();
                try {
                    JSONObject responsJson = new JSONObject(response);
                    JSONObject PrintXReportResult = responsJson.getJSONObject("PrintXReportResult");
                    boolean noerror = PrintXReportResult.getBoolean("NoError");
                    String ErrorMessage = PrintXReportResult.getString("ErrorMessage");
                    if(noerror){
                        Toast.makeText(MainActivity.this,"Imprimare X raport...",Toast.LENGTH_SHORT).show();
                    }else{
                        AlertDialog.Builder exit = new AlertDialog.Builder(context);
                        exit.setTitle("Erroare la imprimare!");
                        exit.setMessage(ErrorMessage);
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
            }else{
                pgH.dismiss();
                AlertDialog.Builder exit = new AlertDialog.Builder(context);
                exit.setTitle("Erroare la imprimare!");
                exit.setMessage("Nu este raspuns de le server!");
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
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View mDecorView = getWindow().getDecorView();
            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==303){
            if (resultCode==RESULT_OK){
                if (data != null) {
                    String AllowedBalance = data.getStringExtra("AllowedBalance");
                    String CardNumber = data.getStringExtra("CardNumber");
                    String CustomerName = data.getStringExtra("CustomerName");
                    String DailyLimit = data.getStringExtra("DailyLimit");
                    String MonthlyLimit = data.getStringExtra("MonthlyLimit");
                    String TankCapacity = data.getStringExtra("TankCapacity");
                    String WeeklyLimit = data.getStringExtra("WeeklyLimit");
                    String AslName = data.getStringExtra("AslName");
                    String AslPrice = data.getStringExtra("AslPrice");


                    AlertDialog.Builder exit = new AlertDialog.Builder(context);
                    exit.setTitle("Informatie despre card:");
                    exit.setMessage(
                            "Clientul : "+ CustomerName + "\n" +
                                    "Cardul : "+ CardNumber + "\n" +
                                    "Balanta: "+ AllowedBalance + "\n" +
                                    "Capacitatea rezervorului: "+ TankCapacity + "\n" +
                                    "Limite: " + "\n" +
                                    "Luna: "+ MonthlyLimit + "\n" +
                                    "Saptamina: "+ WeeklyLimit + "\n" +
                                    "Zi: "+ DailyLimit + "\n" +
                                    "Asortiment: " + "\n" +
                                    "Denumirea: "+ AslName + "\n" +
                                    "Pretul: "+ AslPrice );
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
}