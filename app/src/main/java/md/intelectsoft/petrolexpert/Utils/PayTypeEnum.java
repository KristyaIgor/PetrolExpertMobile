package md.intelectsoft.petrolexpert.Utils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Tony on 2017/12/3.
 */

public class PayTypeEnum {
    public static final int Unknow = 0,Cash = 1, CreditCard = 2, Coupon = 3, BankTransfer = 4, ClientAccount = 5, MutualOffset = 6, TMH = 7, TME = 8, OnlinePay = 9;

    @IntDef({Unknow, Cash, CreditCard, Coupon, BankTransfer, ClientAccount, MutualOffset, TMH, TME, OnlinePay})
    @Retention(RetentionPolicy.SOURCE)
    public @interface payType {

    }
}
