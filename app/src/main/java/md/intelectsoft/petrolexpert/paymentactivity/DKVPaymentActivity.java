package md.intelectsoft.petrolexpert.paymentactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vfi.smartpos.deviceservice.aidl.IDeviceService;
import com.vfi.smartpos.deviceservice.aidl.IMagCardReader;
import com.vfi.smartpos.deviceservice.aidl.IPrinter;
import com.vfi.smartpos.deviceservice.aidl.IRFCardReader;
import com.vfi.smartpos.deviceservice.aidl.ISmartCardReader;
import com.vfi.smartpos.deviceservice.aidl.MagCardListener;
import com.vfi.smartpos.deviceservice.aidl.PrinterConfig;
import com.vfi.smartpos.deviceservice.aidl.PrinterListener;
import com.vfi.smartpos.deviceservice.aidl.RFSearchListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import md.intelectsoft.petrolexpert.emvcardreader.exception.CommunicationException;
import md.intelectsoft.petrolexpert.emvcardreader.model.EmvCard;
import md.intelectsoft.petrolexpert.emvcardreader.parser.EmvParser;
import md.intelectsoft.petrolexpert.BaseApp;
import md.intelectsoft.petrolexpert.R;
import md.intelectsoft.petrolexpert.Utils.SPFHelp;
import md.intelectsoft.petrolexpert.bottomsheet.PaymentMethodSheetDialog;
import md.intelectsoft.petrolexpert.network.pe.result.AssortmentSerializable;
import md.intelectsoft.petrolexpert.printeractivity.PrinterFonts;
import md.intelectsoft.petrolexpert.realm.FiscalKey;

import static md.intelectsoft.petrolexpert.ScanCardCorporativActivity.CPU_CARD;
import static md.intelectsoft.petrolexpert.ScanCardCorporativActivity.S50_CARD;
import static md.intelectsoft.petrolexpert.ScanCardCorporativActivity.S70_CARD;

@SuppressLint("NonConstantResourceId")
public class DKVPaymentActivity extends AppCompatActivity {

    @BindView(R.id.textDKVPayCardNo) TextView textCardNo;
    @BindView(R.id.progressBarWaitCardApply) ProgressBar progressWait;
    @BindView(R.id.textDKVPayProductName) TextView textProductName;
    @BindView(R.id.textDKVPayProductSum) TextView textProductSum;
    @BindView(R.id.textwaitcardlistener) TextView textCardWait;
    @BindView(R.id.buttonConfirmDKVPayment) Button buttonConfirmCard;

    IDeviceService idevice;
    IPrinter printer;
    IRFCardReader irfCardReader;
    ISmartCardReader iSmartCardReader;
    IMagCardReader msr;

    AssortmentSerializable productWithoutAuth;

    private String TAG = "PetrolExpert_BaseApp";

    @OnClick(R.id.imageBackFromDKVPayment) void onBack() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d_k_v_payment);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);


        //without identify
        productWithoutAuth = (AssortmentSerializable) getIntent().getSerializableExtra("Product");
        textProductName.setText(productWithoutAuth.getName());
        textProductSum.setText(getIntent().getDoubleExtra("Sum", 0) + " MDL");
        buttonConfirmCard.setEnabled(false);

        if(BaseApp.isVFServiceConnected()){
            //Initialize elements
            idevice = BaseApp.getApplication().getDeviceService();
            try {
                irfCardReader = idevice.getRFCardReader();
                iSmartCardReader = idevice.getSmartCardReader(0);
                msr = idevice.getMagCardReader();
            } catch (RemoteException e) {
                e.printStackTrace();
            }


            //try read cards
            try {
                msr.searchCard(30, new MyMsrListener());
                irfCardReader.searchCard(rfSearchListener, 30);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
//            doSmartCard();
        }
    }


    class MyListener extends PrinterListener.Stub {
        @Override
        public void onError(int error) throws RemoteException {
            Log.e(TAG, "onError: print error,errno:" + error );
        }

        @Override
        public void onFinish() throws RemoteException {
            Log.e(TAG, "onFinish: " );
        }
    }


    RFSearchListener rfSearchListener = new RFSearchListener.Stub() {
        @Override
        public void onCardPass(int cardType) throws RemoteException {
            if (S50_CARD == cardType || S70_CARD == cardType) {
                Log.e("PetrolExpert_BaseApp",  "M1 card @ " + cardType);
                BaseApp.getApplication().getDeviceService().getBeeper().startBeep(200);
            }
            else if (CPU_CARD == cardType) {
                idevice.getBeeper().startBeep(150);

                doCTLSCard();
            }
        }

        @Override
        public void onFail(int error, String message) throws RemoteException {
            Log.i("PetrolExpert_BaseApp", "Check card fail+ error code:" + error + "error message :" + message);

            if(error == 167){

            }
        }
    };

    private void doCTLSCard(){
        EmvCard card = null;

        EmvParser parser = new EmvParser(irfCardReader);

        try {
            card = parser.readEmvCard();
            Log.e(TAG, "doCTLSCard: " + card.getCardNumber() );
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if(card != null){
            Message msg = new Message();
            msg.getData().putBoolean("rf", true);
            handler.sendMessage(msg);
        }
    }

    class MyMsrListener extends MagCardListener.Stub {
        @Override
        public void onError(int error, String message) throws RemoteException {
            Log.e("PetrolExpert_BaseApp", "onError: " +  "Magnetic card error, code:" + error + '(' + message + ')' );
        }

        @Override
        public void onSuccess(Bundle track) throws RemoteException {
            String pan = track.getString("PAN", "Get Pan fails");
            String track1 = track.getString("TRACK1", "Get Track1 fails");
            String track2 = track.getString("TRACK2", "Get Track2 fails");
            String track3 = track.getString("TRACK3", "Get Track3 fails");
            String serviceCode = track.getString("SERVICE_CODE", "fails");
            String expiredDate = track.getString("EXPIRED_DATE", "fails");

            Log.e("PetrolExpert_BaseApp", "onSuccess MagCard: " + "SUCCESS" + "\n" +
                    "PAN:" + pan + "\n" +
                    "TRACK1:" + track1 + "\n" +
                    "TRACK2:" + track2 + "\n" +
                    "TRACK3:" + track3 + "\n" +
                    "SERVICE_CODE:" + serviceCode + "\n" +
                    "EXPIRED_DATE:" + expiredDate + "\n" );

            idevice.getBeeper().startBeep(100);

            Message msg = new Message();
            msg.getData().putString("pan", pan);
            msg.getData().putBoolean("msr", true);
            handler.sendMessage(msg);
        }

        @Override
        public void onTimeout() throws RemoteException {
            Log.e("PetrolExpert_BaseApp", "onTimeout: " );
        }
    }


    public void doSmartCard() {
        byte apdu_cmd_test[] = { (byte) 0x00, (byte) 0xA4, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x3F, (byte) 0x00 };

        byte[] apdu_ret = null;

//        CommandApdu apduSelect2 = new CommandApdu(CommandEnum.SELECT, BytesUtils.fromString("A000000668000000000276"), 0);

        try{
            iSmartCardReader.powerUp();

            if( iSmartCardReader.isCardIn() ){
                idevice.getBeeper().startBeep(150);
//                apdu_ret = iSmartCardReader.exchangeApdu(apduSelect2.toBytes());
                Log.d(TAG, "doSmartCard exchangeApdu ret:" + byte2HexStr(apdu_ret) );
//
//                if(ResponseUtils.isSucceed(apdu_ret)){
////                    Log.e(TAG, "onCardPass: \n" + prettyPrintAPDUResponse(apdu_ret));
//                }
//                CommandApdu apduSelect = new CommandApdu(CommandEnum.SELECT, true ? PPSE : PSE, 0);
//
//                Log.d(TAG, "doSmartCard apduSelected:" + byte2HexStr(apduSelect.toBytes()));
//                byte[] apdu_selected_ret = iSmartCardReader.exchangeApdu(apduSelect.toBytes());
//
//                Log.d(TAG, "doSmartCard apdu_selected_ret:" + byte2HexStr(apdu_selected_ret));
//                if(ResponseUtils.isSucceed(apdu_selected_ret)){
//                    Log.e(TAG, "doSmartCard: \n" + prettyPrintAPDUResponse(apdu_selected_ret));
//                }

            }
            iSmartCardReader.powerDown();
        } catch(RemoteException e)
        {
            e.printStackTrace();
            try{
                iSmartCardReader.powerDown();
            } catch(RemoteException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static String byte2HexStr(byte[] var0) {
        if (var0 == null) {
            return "";
        } else {
            String var1 = "";
            StringBuilder var2 = new StringBuilder("");

            for (int var3 = 0; var3 < var0.length; ++var3) {
                var1 = Integer.toHexString(var0[var3] & 255);
                var2.append(var1.length() == 1 ? "0" + var1 : var1);
            }

            return var2.toString().toUpperCase().trim();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);


            if(msg.getData().getBoolean("msr")){
                String pan = msg.getData().getString("pan");
                Log.d(TAG, pan);
                progressWait.setVisibility(View.GONE);
                textCardWait.setVisibility(View.GONE);
                textCardNo.setText(getPANWithMask(pan));

                buttonConfirmCard.setEnabled(true);
            }
            if(msg.getData().getBoolean("rf")){
                progressWait.setVisibility(View.GONE);
                textCardWait.setVisibility(View.GONE);
                textCardNo.setText("ulala");

                buttonConfirmCard.setEnabled(true);
            }
        }
    };

    private String getPANWithMask(String pan){
        if(pan.length() > 9){
            String panFirst = pan.substring(0, 4);
            String panLast = pan.substring(pan.length() - 4);
            String panMedium = pan.substring(4, pan.length() - 4);
            panMedium = panMedium.replaceAll("[0-9]", "*");
            return panFirst + " " + panMedium + " " + panLast;
        }
        else
            return pan;
    }


    public void doPrintString() {
        try {
            // bundle format for addText
            Bundle format = new Bundle();

            // bundle formate for AddTextInLine
            Bundle fmtAddTextInLine = new Bundle();
            //
//            format.putInt(PrinterConfig.addText.FontSize.BundleName, PrinterConfig.addText.FontSize.NORMAL_DH_24_48_IN_BOLD);
//            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.CENTER);
////            printer.addText(format, "Hello!");
//
//            format.putInt(PrinterConfig.addText.FontSize.BundleName, PrinterConfig.addText.FontSize.LARGE_DH_32_64_IN_BOLD);
//            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.CENTER);
////            printer.addText(format, "Hello!");
//
//            format.putInt(PrinterConfig.addText.FontSize.BundleName, PrinterConfig.addText.FontSize.HUGE_48);
//            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.CENTER);
//            printer.addText(format, "Hello!");
//
//            // image
//
//            byte[] buffer = null;
//            try {
//                //
//                InputStream is = getContext().getAssets().open("verifone_logo.jpg");
//                // get the size
//                int size = is.available();
//                // crete the array of byte
//                buffer = new byte[size];
//                is.read(buffer);
//                // close the stream
//                is.close();
//
//            } catch (IOException e) {
//                // Should never happen!
//                throw new RuntimeException(e);
//            }
//            if( null != buffer) {
//                Bundle fmtImage = new Bundle();
//                fmtImage.putInt("offset", (384-200)/2);
//                fmtImage.putInt("width", 250);  // bigger then actual, will print the actual
//                fmtImage.putInt("height", 128); // bigger then actual, will print the actual
//                printer.addImage( fmtImage, buffer );
//
//                fmtImage.putInt("offset", 50 );
//                fmtImage.putInt("width", 100 ); // smaller then actual, will print the setting
//                fmtImage.putInt("height", 24); // smaller then actual, will print the setting
//                printer.addImage( fmtImage, buffer );
//            }


            //
//            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.LARGE_32_32 );
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterFonts.path + PrinterFonts.FONT_FORTE );
//            printer.addTextInLine(fmtAddTextInLine, "Verifone X9-Series", "", "", 0);
//            //
//            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.NORMAL_24_24 );
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterFonts.path + PrinterFonts.FONT_segoesc );
//            printer.addTextInLine(fmtAddTextInLine, "", "", "This is the Print Demo", 0);


            format.putInt(PrinterConfig.addText.FontSize.BundleName, PrinterConfig.addText.FontSize.NORMAL_24_24);
            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.CENTER);
            printer.addText(format, "\"" + SPFHelp.getInstance().getString("CompanyName", "") + "\"");

            printer.addText(format, "IDNO: " + SPFHelp.getInstance().getString("CompanyIDNO", ""));
            printer.addText(format, "Inr.Nr: " + SPFHelp.getInstance().getString("FiscalCode", ""));
            printer.addText(format, "");

            printer.addTextInLine( fmtAddTextInLine, "00001" , "", "01 #", 0);

            printer.addTextInLine( fmtAddTextInLine, "#-" + SPFHelp.getInstance().getString("Cash", "Casa nui"), "", "#", 0);
            printer.addTextInLine( fmtAddTextInLine, "#-" + SPFHelp.getInstance().getString("Owner", "Autor nui"), "", "#", 0);
            printer.addTextInLine( fmtAddTextInLine, "#-Id: 00000" , "", "#", 0);
            printer.addText(format, "");

//            // left
//            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.LEFT );
//            printer.addText(format, "Left Alignment long string here: PrinterConfig.addText.Alignment.LEFT ");
//
//            // right
//            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.RIGHT );
//            printer.addText(format, "Right Alignment  long  string with wrapper here");

            printer.addText(format, "--------------------------------");

            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.NORMAL_DH_24_48_IN_BOLD);

            printer.addTextInLine( fmtAddTextInLine, "TOTAL" , "", "54.00", 0);

            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.NORMAL_24_24 );
            printer.addTextInLine( fmtAddTextInLine, "IntelectSoft S.R.L." , "", "", 0);

//            Bundle fmtAddBarCode = new Bundle();
//            fmtAddBarCode.putInt( PrinterConfig.addBarCode.Alignment.BundleName, PrinterConfig.addBarCode.Alignment.RIGHT );
//            fmtAddBarCode.putInt( PrinterConfig.addBarCode.Height.BundleName, 64 );
//            printer.addBarCode( fmtAddBarCode, "123456 Verifone" );
//
//            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.LARGE_32_32 );
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterFonts.FONT_AGENCYB);
//            printer.addTextInLine(fmtAddTextInLine, "", "123456 Verifone", "", 0);
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterConfig.addTextInLine.GlobalFont.English );    // set to the default
//
//            printer.addText(format, "--------------------------------");


//            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.LARGE_32_32 );
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterFonts.path + PrinterFonts.FONT_ALGER );
//            printer.addTextInLine( fmtAddTextInLine, "Left", "Center", "right", 0);
//            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.LARGE_32_32 );
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterFonts.path + PrinterFonts.FONT_BROADW );
//            printer.addTextInLine( fmtAddTextInLine, "L & R", "", "Divide Equally", 0);
//            printer.addTextInLine( fmtAddTextInLine, "L & R", "", "Divide flexible", PrinterConfig.addTextInLine.mode.Devide_flexible);
//            // left
//            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.LEFT );
//            printer.addText(format, "--------------------------------");
//
//            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.LARGE_32_32 );
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterConfig.addTextInLine.GlobalFont.English);
//            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterFonts.path + PrinterFonts.FONT_segoesc );
//            printer.addTextInLine( fmtAddTextInLine,
//                    "",
//                    "",
//                    "Right long string here call addTextInLine ONLY give the right string",
//                    0);

            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.LEFT );
            format.putInt(PrinterConfig.addText.FontSize.BundleName, PrinterConfig.addText.FontSize.NORMAL_24_24 );
            printer.addText(format, "--------------------------------");

            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterConfig.addTextInLine.GlobalFont.English);  // this the default
            printer.addTextInLine( fmtAddTextInLine, "", "#",
                    "Right long string with the center string",
                    0);
            printer.addText(format, "--------------------------------");
            fmtAddTextInLine.putInt(PrinterConfig.addTextInLine.FontSize.BundleName, PrinterConfig.addTextInLine.FontSize.SMALL_16_16);
            fmtAddTextInLine.putString(PrinterConfig.addTextInLine.GlobalFont.BundleName, PrinterFonts.FONT_AGENCYB);
            printer.addTextInLine( fmtAddTextInLine, "Print the QR code far from the barcode to avoid scanner found both of them", "", "",
                    PrinterConfig.addTextInLine.mode.Devide_flexible);


            Realm mRealm = Realm.getDefaultInstance();
            FiscalKey key = mRealm.where(FiscalKey.class).findFirst();
            if(key == null){
                format.putInt(PrinterConfig.addText.FontSize.BundleName, PrinterConfig.addText.FontSize.HUGE_48);
                format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.CENTER);
                printer.addText(format, "BON NEFISCAL!");
            }
            else{
                format.putInt(PrinterConfig.addText.FontSize.BundleName, PrinterConfig.addText.FontSize.HUGE_48);
                format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.CENTER);
                printer.addText(format, "BON FISCAL!");
            }


            Bundle fmtAddQRCode = new Bundle();
            fmtAddQRCode.putInt(PrinterConfig.addQrCode.Offset.BundleName, 128);
            fmtAddQRCode.putInt(PrinterConfig.addQrCode.Height.BundleName, 128);
            printer.addQrCode( fmtAddQRCode, "www.verifone.cn");

            printer.addTextInLine( fmtAddTextInLine, "", "try to scan it",
                    "",
                    0);

            format.putInt(PrinterConfig.addText.Alignment.BundleName, PrinterConfig.addText.Alignment.LEFT );
            format.putInt(PrinterConfig.addText.FontSize.BundleName, PrinterConfig.addText.FontSize.NORMAL_24_24 );
            printer.addText(format, "---------X-----------X----------");

            printer.addText(format, "\n");
            printer.feedLine(3);
            // start print here
            printer.startPrint(new MyListener());

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}