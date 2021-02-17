package md.intelectsoft.petrolexpert.enums;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ShiftStateEnum {
    public static final int Valid = 0, Elapsed = 1, ChangeStarted = 2, ChangeEnd = 3, None = 4, Closed = 5;

    @IntDef({Valid, Elapsed, ChangeStarted, ChangeEnd, None, Closed})
    @Retention(RetentionPolicy.SOURCE)
    public @interface stateShift {
    }
}
