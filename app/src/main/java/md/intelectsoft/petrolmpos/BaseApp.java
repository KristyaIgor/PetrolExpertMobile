package md.intelectsoft.petrolmpos;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.vfi.smartpos.deviceservice.aidl.IDeviceService;

import md.intelectsoft.petrolmpos.verifone.transaction.AppParams;
import md.intelectsoft.petrolmpos.verifone.transaction.TransBasic;

public class BaseApp extends Application {
    private static final String TAG = "PetrolMPOS_BaseApp";
    private static BaseApp application;
    private static boolean isVFServiceConnected = false;

    //service connection for verifone service
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected, DeviceHelper, TransBasic,AppParams init");

            TransBasic.getInstance().initTransBasic(handler , BaseApp.this);
            AppParams.getInstance().initAppParam();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, name.getPackageName() + " is disconnected");
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        bindDeviceService();
        application = this;
    }

    public static BaseApp getApplication() {
        return application;
    }

    //bind to device service verifone
    private void bindDeviceService(){
        Intent intent = new Intent();
        intent.setAction("com.vfi.smartpos.device_service");
        intent.setPackage("com.vfi.smartpos.deviceservice");
        // or
//        ComponentName componentName = new ComponentName("com.vfi.smartpos.deviceservice", "com.verifone.smartpos.service.VerifoneDeviceService");
//        intent.setComponent(componentName);

        isVFServiceConnected = bindService(intent, connection, Context.BIND_AUTO_CREATE);
        if (!isVFServiceConnected) {
            Log.i(TAG, "deviceService bind failed");
        } else {
            Log.i(TAG, "deviceService bind success");
        }
    }

    public static boolean isVFServiceConnected() {
        return isVFServiceConnected;
    }

    public static void setVFServiceConnected(boolean isVFServiceConnected) {
        BaseApp.isVFServiceConnected = isVFServiceConnected;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, msg.getData().getString("msg"));
            Toast.makeText(BaseApp.this, msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
        }
    };
}
