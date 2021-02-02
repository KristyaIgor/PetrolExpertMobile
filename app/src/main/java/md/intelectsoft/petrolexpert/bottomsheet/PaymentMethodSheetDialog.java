package md.intelectsoft.petrolexpert.bottomsheet;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vfi.smartpos.deviceservice.aidl.IDeviceService;
import com.vfi.smartpos.deviceservice.aidl.IPrinter;
import com.vfi.smartpos.deviceservice.aidl.PrinterConfig;
import com.vfi.smartpos.deviceservice.aidl.PrinterListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import md.intelectsoft.petrolexpert.BaseApp;
import md.intelectsoft.petrolexpert.CountProductActivity;
import md.intelectsoft.petrolexpert.R;
import md.intelectsoft.petrolexpert.Utils.SPFHelp;
import md.intelectsoft.petrolexpert.adapters.PaymentWithoutAdapter;
import md.intelectsoft.petrolexpert.paymentactivity.BPayPaymentActivity;
import md.intelectsoft.petrolexpert.printeractivity.PrinterFonts;
import md.intelectsoft.petrolexpert.realm.FiscalKey;


/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     PaymentMethodSheetDialog.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class PaymentMethodSheetDialog extends BottomSheetDialogFragment {

    static Dialog dialog;
    static View bottomSheet;
    PaymentWithoutAdapter adapter;

    public static final String TAG = "ActionBottomDialog";

    IDeviceService idevice;
    IPrinter printer;

    public static PaymentMethodSheetDialog newInstance() {
        return new PaymentMethodSheetDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.bottom_sheet_pay_without, container, false);

        GridView grid = root.findViewById(R.id.listPaymentMethod);
        ConstraintLayout close = root.findViewById(R.id.layoutCloseDialogPayWithout);


        List<String> list = new ArrayList<>();
        for(int i = 0; i < 25; i++){
            list.add("Item " + i);
        }

        adapter = new PaymentWithoutAdapter(getContext(), R.layout.list_item_payment_type, list);
        grid.setAdapter(adapter);

        grid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch(scrollState) {
                    case 2: // SCROLL_STATE_FLING
                        close.setVisibility(View.GONE);
                        break;

                    case 1: // SCROLL_STATE_TOUCH_SCROLL
                        close.setVisibility(View.GONE);
                        break;

                    case 0: // SCROLL_STATE_IDLE
                        close.setVisibility(View.VISIBLE);
                        break;

                    default:
                        close.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        grid.setOnItemClickListener((parent, view, position, id) ->{
            String test = adapter.getItem(position);
            if(test.equals("Item 2")){
                //BPay cred ca
                startActivity(new Intent(getContext(), BPayPaymentActivity.class));
            }
            else if(test.equals("Item 1")){
                //Cash payment
                if(BaseApp.isVFServiceConnected()){
                    idevice = BaseApp.getApplication().getDeviceService();
                    try {
                        printer = idevice.getPrinter();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    doPrintString();
                }
            }
        });

        close.setOnClickListener(v -> dialog.dismiss());

        return root;
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

            Message msg = new Message();
            msg.getData().putString("msg", "start printing");
            handler.sendMessage(msg);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    class MyListener extends PrinterListener.Stub {
        @Override
        public void onError(int error) throws RemoteException {
            Message msg = new Message();
            msg.getData().putString("msg", "print error,errno:" + error);
            handler.sendMessage(msg);
        }

        @Override
        public void onFinish() throws RemoteException {
            Message msg = new Message();
            msg.getData().putString("msg", "print finished");
            handler.sendMessage(msg);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String string = msg.getData().getString("string");
            super.handleMessage(msg);
            Log.d(TAG, msg.getData().getString("msg"));
            Toast.makeText(getContext(), msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();

        }
    };


    @Override
    public void onStart() {
        super.onStart();
        dialog = getDialog();
        dialog.setCancelable(false);

        if (dialog != null) {
            bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            int displayHeight = CountProductActivity.displayMetrics.heightPixels;
            int dialogWindowHeight = (int) (displayHeight * 0.85f);
            bottomSheet.getLayoutParams().height = dialogWindowHeight;
        }
        View view = getView();
        view.post(() -> {
            View parent = (View) view.getParent();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();
            BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setPeekHeight(view.getMeasuredHeight());
//            ((View) .getParent()).setBackgroundColor(Color.TRANSPARENT);

        });
    }
}