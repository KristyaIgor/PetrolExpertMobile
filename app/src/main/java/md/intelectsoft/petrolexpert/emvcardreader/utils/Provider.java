package md.intelectsoft.petrolexpert.emvcardreader.utils;

import android.nfc.tech.IsoDep;
import android.util.Log;

import java.io.IOException;

import md.intelectsoft.petrolexpert.emvcardreader.enums.SwEnum;
import md.intelectsoft.petrolexpert.emvcardreader.exception.CommunicationException;
import md.intelectsoft.petrolexpert.emvcardreader.parser.IProvider;


public class Provider implements IProvider {


    private static final String TAG = Provider.class.getName();

    private StringBuffer log = new StringBuffer();

    private IsoDep mTagCom;

    public void setmTagCom(final IsoDep mTagCom) {
        this.mTagCom = mTagCom;
    }


    public StringBuffer getLog() {
        return log;
    }

    @Override
    public byte[] transceive(byte[] pCommand) throws CommunicationException {
        Log.d(TAG, "Send command: " + BytesUtils.bytesToString(pCommand));

        byte[] response = null;
        try {
            // send command to emv card
            response = mTagCom.transceive(pCommand);
        } catch (IOException e) {
            throw new CommunicationException(e.getMessage());
        }

        Log.d(TAG, "resp from command: " + BytesUtils.bytesToString(pCommand) + " is ---> " + BytesUtils.bytesToString(response));
        try {
            Log.d(TAG, "Pretty Print Response APDU Command: " + TlvUtil.prettyPrintAPDUResponse(response));

            SwEnum val = SwEnum.getSW(response);
            if (val != null) {
                Log.d(TAG, "response detail value: " + val.getDetail());
            }

            Log.d(TAG, "Pretty Print Response replacement space to nbsp: " + TlvUtil.prettyPrintAPDUResponse(response).replace("\n", ""));

        } catch (Exception e) {
        }

        return response;
    }
}
