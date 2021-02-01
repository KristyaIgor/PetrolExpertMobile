package md.intelectsoft.petrolmpos.enums;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class LimitCardEnum {
    public static final int MDL = 0, Liter = 1;

    @IntDef({MDL, Liter})
    @Retention(RetentionPolicy.SOURCE)
    public @interface stateShift {
    }
}
