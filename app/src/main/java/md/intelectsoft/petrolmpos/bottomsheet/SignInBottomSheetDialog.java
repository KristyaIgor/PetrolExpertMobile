package md.intelectsoft.petrolmpos.bottomsheet;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import md.intelectsoft.petrolmpos.CountProductWithoutActivity;
import md.intelectsoft.petrolmpos.R;
import md.intelectsoft.petrolmpos.adapters.PaymentWithoutAdapter;


/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     SignInBottomSheetDialog.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class SignInBottomSheetDialog extends BottomSheetDialogFragment {

    static Dialog dialog;
    static View bottomSheet;

    public static final String TAG = "ActionBottomDialog";

    public static SignInBottomSheetDialog newInstance() {
        return new SignInBottomSheetDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.bottom_sheet_pay_without, container, false);

        GridView grid = root.findViewById(R.id.listPaymentMethod);
        ConstraintLayout close = root.findViewById(R.id.layoutCloseDialogPayWithout);


        List<String> list = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            list.add("Item " + i);
        }

        PaymentWithoutAdapter adapter = new PaymentWithoutAdapter(getContext(), R.layout.list_item_payment_type, list);
        grid.setAdapter(adapter);

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
            int displayHeight = CountProductWithoutActivity.displayMetrics.heightPixels;
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