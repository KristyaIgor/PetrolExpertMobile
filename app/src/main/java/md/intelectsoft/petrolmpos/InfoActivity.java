package md.intelectsoft.petrolmpos;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolmpos.Utils.LocaleHelper;
import md.intelectsoft.petrolmpos.Utils.SPFHelp;

@SuppressLint("NonConstantResourceId")
public class InfoActivity extends AppCompatActivity {
    @BindView(R.id.textCompanyName) TextView companyName;
    @BindView(R.id.textLicenseCode) TextView licenseCode;
    @BindView(R.id.textTerminalNumber) TextView terminalNr;
    @BindView(R.id.textUserName) TextView userName;
    @BindView(R.id.textCashWorkPlace) TextView cashWorkPlace;
    @BindView(R.id.textIDNO) TextView companyIdno;

    @BindView(R.id.langEnButton) RadioButton langEN;
    @BindView(R.id.langRoButton) RadioButton langRO;
    @BindView(R.id.langRuButton) RadioButton langRU;

    @OnClick(R.id.imageBackToMain) void onBack(){
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        String company = SPFHelp.getInstance().getString("CompanyName", "");
        String idno = SPFHelp.getInstance().getString("CompanyIDNO", "");
        String license = SPFHelp.getInstance().getString("LicenseCode", "");
        String cash = SPFHelp.getInstance().getString("Cash", "");
        String owner = SPFHelp.getInstance().getString("Owner", "");
        int numberTerminal = SPFHelp.getInstance().getInt("RegisteredNumber", 0);

        companyName.setText(company);
        companyIdno.setText("IDNO: " + idno);
        licenseCode.setText(license);
        terminalNr.setText(String.valueOf(numberTerminal));
        userName.setText(owner);
        cashWorkPlace.setText(cash);

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
            }
        });
        langRO.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                langEN.setChecked(false);
                langRU.setChecked(false);
                LocaleHelper.setLocale(this, "ro");
            }
        });
        langRU.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                langEN.setChecked(false);
                langRO.setChecked(false);
                LocaleHelper.setLocale(this, "ru");
            }
        });
    }

}