package com.vfi.smartpos.deviceservice.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class DRLData implements Parcelable {

    private byte[] drlID = null; //Dynamic Limit ID
    private byte[] clssFloorLimit = null;
    private byte[] clssTransLimit = null;

    public byte[] getDrlID() {
        return drlID;
    }

    public byte[] getClssFloorLimit() {
        return clssFloorLimit;
    }

    public byte[] getClssTransLimit() {
        return clssTransLimit;
    }

    public byte[] getCvmRequiredLimit() {
        return cvmRequiredLimit;
    }

    private byte[] cvmRequiredLimit = null;

    public DRLData(byte[] drlID, byte[] clssFloorLimit, byte[] clssTransLimit, byte[] cvmRequiredLimit) {
        this.drlID = drlID;
        this.clssFloorLimit = clssFloorLimit;
        this.clssTransLimit = clssTransLimit;
        this.cvmRequiredLimit = cvmRequiredLimit;
    }

    public static final Creator<DRLData> CREATOR = new Creator<DRLData>() {
        @Override
        public DRLData createFromParcel(Parcel in) {
            return new DRLData(in.createByteArray(), in.createByteArray(), in.createByteArray(), in.createByteArray());
        }

        @Override
        public DRLData[] newArray(int size) {
            return new DRLData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // 序列化
        dest.writeByteArray(drlID);
        dest.writeByteArray(clssFloorLimit);
        dest.writeByteArray(clssTransLimit);
        dest.writeByteArray(cvmRequiredLimit);
    }
}
