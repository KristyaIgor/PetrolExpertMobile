package md.intelectsoft.petrolmpos;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothClass;
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

import md.intelectsoft.petrolmpos.verifone.Utilities.DeviceHelper;
import md.intelectsoft.petrolmpos.verifone.Utilities.ToastUtil;
import md.intelectsoft.petrolmpos.verifone.transaction.AppParams;
import md.intelectsoft.petrolmpos.verifone.transaction.TransBasic;

public class BaseApp extends Application {
    private static final String TAG = "PetrolMPOS_BaseApp";
    private static BaseApp application;
    private static boolean isVFServiceConnected = false;

    private static IDeviceService deviceService;  //info about device

    //service connection for verifone service
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected, DeviceHelper, TransBasic,AppParams init");
            deviceService = IDeviceService.Stub.asInterface(service);
            DeviceHelper.getInstance().initDeviceHelper(BaseApp.this);
            TransBasic.getInstance().initTransBasic(handler , BaseApp.this);
            AppParams.getInstance().initAppParam();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, name.getPackageName() + " is disconnected");
            deviceService = null;
        }
    };



    @Override
    public void onCreate() {
        super.onCreate();
        bindDeviceService();
        application = this;
        ToastUtil.init(getApplicationContext());
    }

    public static BaseApp getApplication() {
        return application;
    }

    //bind to device service verifone
    private void bindDeviceService(){
        if (null != deviceService) {
            return;
        }

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

    // log & display
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, msg.getData().getString("msg"));
            Toast.makeText(BaseApp.this, msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
        }
    };

    public IDeviceService getDeviceService() {
        return deviceService;
    }
}
