package md.intelectsoft.petrolexpert.bottomsheet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import md.intelectsoft.petrolexpert.BaseApp;
import md.intelectsoft.petrolexpert.CountProductActivity;
import md.intelectsoft.petrolexpert.R;
import md.intelectsoft.petrolexpert.Utils.PayTypeEnum;
import md.intelectsoft.petrolexpert.adapters.PaymentWithoutAdapter;
import md.intelectsoft.petrolexpert.network.pe.result.AssortmentSerializable;
import md.intelectsoft.petrolexpert.network.pe.result.stationSettings.PaymentTypeStation;
import md.intelectsoft.petrolexpert.paymentactivity.DKVPaymentActivity;


/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     PaymentMethodSheetDialog.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class PaymentMethodSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener{

    static Dialog dialog;
    static View bottomSheet;
    PaymentWithoutAdapter adapter;

    public static final String TAG = "PaymentMethod";

    AssortmentSerializable productSelected;
    double sum;
    private ItemClickListener mListener;

    /**
     * PPSE directory "2PAY.SYS.DDF01"
     */
    private static final byte[] PPSE = "2PAY.SYS.DDF01".getBytes();

    /**
     * PSE directory "1PAY.SYS.DDF01"
     */
    private static final byte[] PSE = "1PAY.SYS.DDF01".getBytes();

    public PaymentMethodSheetDialog(AssortmentSerializable productSelected, double sumProduct) {
        this.productSelected = productSelected;
        this.sum = sumProduct;
    }

    public static PaymentMethodSheetDialog newInstance(AssortmentSerializable productWithoutAuth, double sumProduct) {
        return new PaymentMethodSheetDialog(productWithoutAuth, sumProduct);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListener) {
            mListener = (ItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ItemClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.bottom_sheet_pay_without, container, false);

        GridView grid = root.findViewById(R.id.listPaymentMethod);
        ConstraintLayout close = root.findViewById(R.id.layoutCloseDialogPayWithout);


        List<PaymentTypeStation> list = BaseApp.getApplication().getListPayment();

        adapter = new PaymentWithoutAdapter(getContext(), R.layout.list_item_payment_type, list);
        grid.setAdapter(adapter);

        grid.setOnItemClickListener((parent, view, position, id) ->{
            PaymentTypeStation selectedPay = adapter.getItem(position);
//            if(Pa){
//                //BPay cred ca
//                startActivity(new Intent(getContext(), BPayPaymentActivity.class));
//            }
//            else
            if(selectedPay.getType() == PayTypeEnum.Cash){
//                sendBillToBackAndSaveLocal();
            }

            else if (selectedPay.getType() == 15){ // dkv payment
                Intent count = new Intent(getContext(), DKVPaymentActivity.class);
                count.putExtra("Product", productSelected);
                count.putExtra("Sum", sum);
                startActivity(count);
                dialog.dismiss();
            }
        });

        close.setOnClickListener(v -> dialog.dismiss());

        return root;
    }

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

    @Override
    public void onClick(View view) {
        PaymentTypeStation itemClick = (PaymentTypeStation) view.getTag();
        mListener.onItemClick(itemClick);
//        dismiss();
    }

    public interface ItemClickListener {
        void onItemClick(PaymentTypeStation item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}