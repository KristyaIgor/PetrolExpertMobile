package md.intelectsoft.petrolmpos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import md.intelectsoft.petrolmpos.Utils.LocaleHelper;
import md.intelectsoft.petrolmpos.Utils.SPFHelp;
import md.intelectsoft.petrolmpos.network.broker.BrokerRetrofitClient;
import md.intelectsoft.petrolmpos.network.broker.BrokerServiceAPI;
import md.intelectsoft.petrolmpos.network.pe.body.SetFiscalBody;
import md.intelectsoft.petrolmpos.network.pe.body.registerBill.BillRegistered;
import md.intelectsoft.petrolmpos.network.pe.result.SetFiscal;
import md.intelectsoft.petrolmpos.realm.FiscalKey;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class InfoActivity extends AppCompatActivity {
    @BindView(R.id.textCompanyName) TextView companyName;
    @BindView(R.id.textLicenseCode) TextView licenseCode;
    @BindView(R.id.textTerminalNumber) TextView terminalNr;
    @BindView(R.id.textUserName) TextView userName;
    @BindView(R.id.textCashWorkPlace) TextView cashWorkPlace;
    @BindView(R.id.textIDNO) TextView companyIdno;
    @BindView(R.id.imgButtonsSetFiscal) ImageButton setAsFiscal;
    @BindView(R.id.textFicsalCode) TextView fiscalCode;

    @BindView(R.id.langEnButton) RadioButton langEN;
    @BindView(R.id.langRoButton) RadioButton langRO;
    @BindView(R.id.langRuButton) RadioButton langRU;

    @BindView(R.id.textBillCounter) TextView billCounter;


    @OnClick(R.id.layoutCountOfBill) void showBills(){
        startActivity(new Intent(this, BillListActivity.class));
    }

    @OnClick(R.id.imageBackToMain) void onBack(){
        finish();
    }

    ProgressDialog progressDialog;
    BrokerServiceAPI brokerServiceAPI;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        context = this;
        progressDialog = new ProgressDialog(context);
        brokerServiceAPI = BrokerRetrofitClient.getApiBrokerService();

        String company = SPFHelp.getInstance().getString("CompanyName", "");
        String idno = SPFHelp.getInstance().getString("CompanyIDNO", "");
        String license = SPFHelp.getInstance().getString("LicenseCode", "");
        String cash = SPFHelp.getInstance().getString("Cash", "");
        String station = SPFHelp.getInstance().getString("StationName", "");
        String owner = SPFHelp.getInstance().getString("Owner", "");
        int numberTerminal = SPFHelp.getInstance().getInt("RegisteredNumber", 0);

        companyName.setText(company);
        companyIdno.setText("IDNO: " + idno);
        licenseCode.setText(license);
        terminalNr.setText(String.valueOf(numberTerminal));
        userName.setText(owner);
        cashWorkPlace.setText(cash + "/" + station);

        String fiscalNumber = SPFHelp.getInstance().getString("FiscalCode", null);
        if(fiscalNumber == null)
            fiscalCode.setText("Device is not taxed");
        else {
            fiscalCode.setText(fiscalNumber);
            setAsFiscal.setVisibility(View.GONE);
        }

        String lang = LocaleHelper.getLanguage(this);

        if(lang.equals("en")){
            langEN.setChecked(true);
            langRO.setChecked(false);
            langRU.setChecked(false);
        }
        else if(lang.equals("ro")){
            langRO.setChecked(true);
            langEN.setChecked(false);
            langRU.setChecked(false);
        }
        else if(lang.equals("ru")){
            langRU.setChecked(true);
            langEN.setChecked(false);
            langRO.setChecked(false);
        }

        langEN.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                langRO.setChecked(false);
                langRU.setChecked(false);
                LocaleHelper.setLocale(this, "en");
                setResult(RESULT_FIRST_USER);
                finish();
            }
        });
        langRO.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                langEN.setChecked(false);
                langRU.setChecked(false);
                LocaleHelper.setLocale(this, "ro");
                setResult(RESULT_FIRST_USER);
                finish();
            }
        });
        langRU.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                langEN.setChecked(false);
                langRO.setChecked(false);
                LocaleHelper.setLocale(this, "ru");
                setResult(RESULT_FIRST_USER);
                finish();
            }
        });

        setAsFiscal.setOnClickListener(v -> {
            setAsFiscalDevice();
        });

        RealmResults<BillRegistered> bils = Realm.getDefaultInstance().where(BillRegistered.class).findAll();
        if(!bils.isEmpty())
            billCounter.setText(String.valueOf(bils.size()));

    }

    private void setAsFiscalDevice(){
        SetFiscalBody fiscalBody = new SetFiscalBody();

        String licenseId = SPFHelp.getInstance().getString("LicenseID", null);
        String activationCode = SPFHelp.getInstance().getString("LicenseActivationCode", null);

        fiscalBody.setLicenseActivationCode(activationCode);
        fiscalBody.setLicenseID(licenseId);

        if(licenseId != null && activationCode != null){
            Call<SetFiscal> call = brokerServiceAPI.setAsFiscal(fiscalBody);
            progressDialog.setMessage("Set device as fiscal..");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setButton(-1, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    call.cancel();
                    if (call.isCanceled())
                        dialog.dismiss();
                }
            });
            progressDialog.show();

            call.enqueue(new Callback<SetFiscal>() {
                @Override
                public void onResponse(Call<SetFiscal> call, Response<SetFiscal> response) {
                    SetFiscal fiscalResult = response.body();
                    progressDialog.dismiss();
                    if(fiscalResult != null){
                        if(fiscalResult.getErrorCode() == 0){
                            fiscalCode.setText(fiscalResult.getFiscalNumber());
                            setAsFiscal.setVisibility(View.GONE);

                            SPFHelp.getInstance().putString("FiscalCode", fiscalResult.getFiscalNumber());
                            FiscalKey fs = new FiscalKey();
                            fs.setKey(encrypt(fiscalResult.getKey().getBytes(), BaseApp.getApplication().getWordTime()));

                            Realm mRealm = Realm.getDefaultInstance();
                            mRealm.executeTransaction(realm -> {
                                realm.insert(fs);
                            });
                        }
                        else Toast.makeText(InfoActivity.this, "Error set as fiscal! Code: " + fiscalResult.getErrorCode() + " Message: " + fiscalResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    } else Toast.makeText(InfoActivity.this, "Empty result!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<SetFiscal> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(InfoActivity.this, "Error set as fiscal! Message: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static byte[] encrypt(byte[] plaintext, byte[] wordTime){
        byte[] IV = new byte[16];
        Cipher cipher = null;
        byte[] cipherText = null;
        try {
            cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(wordTime, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            cipherText = cipher.doFinal(plaintext);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return cipherText;
    }
}