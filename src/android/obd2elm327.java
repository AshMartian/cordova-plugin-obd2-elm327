package cordovapluginobd2elm327;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.pm.PackageManager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;

import co.lujun.lmbluetoothsdk;

import java.util.Set;

/**
 * This class echoes a string called from JavaScript.
 */
public class obd2elm327 extends CordovaPlugin {

    /**
     * Actions
     */
    private static final String SETCONFIG = "setConfig";
    private static final String SCAN = "scanDevices";
    private static final String CONNECT = "connectDevice";
    private static final String SUBSCRIBECONNECT = "subscribeConnected";
    private static final String WRITE = "write";
    private static final String LIST = "listDevices";
    private static final String SUBSCRIBE = "subscribe";
    private static final String UNSUBSCRIBE = "unsubscribe";

    private static BluetoothController mBTController;
    private static BluetoothLEController mBLEController;

    private JSONArray deviceList;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {


        if (action.equals(SETCONFIG)) {
            String message = args.getString(0);
            this.setConfig(message, callbackContext);
            return true;
        } else if (action.equals(SCAN)) {
            String message = args.getString(0);
            this.scanDevices(message, callbackContext);
            return true;
        } else if (action.equals(CONNECT)) {
            String message = args.getString(0);
            this.connectDevice(message, callbackContext);
            return true;
        } else if (action.equals(SUBSCRIBECONNECT)) {
            String message = args.getString(0);
            this.subscribeConnected(message, callbackContext);
            return true;
        } else if (action.equals(WRITE)) {
            String message = args.getString(0);
            this.write(message, callbackContext);
            return true;
        } else if (action.equals(LIST)) {
            String message = args.getString(0);
            this.listDevices(message, callbackContext);
            return true;
        } else if (action.equals(SUBSCRIBE)) {
            String message = args.getString(0);
            this.subscribe(message, callbackContext);
            return true;
        } else if (action.equals(UNSUBSCRIBE)) {
            String message = args.getString(0);
            this.unsubscribe(message, callbackContext);
            return true;
        }
        return false;
    }

    private JSONObject deviceToJSON(BluetoothDevice device, short rssi) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", device.getName());
        json.put("address", device.getAddress());
        json.put("id", device.getAddress());
        json.put("rssi", rssi);
        if (device.getBluetoothClass() != null) {
            json.put("class", device.getBluetoothClass().getDeviceClass());
        }
        return json;
    }
    private JSONObject deviceToJSON(BluetoothDevice device) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", device.getName());
        json.put("address", device.getAddress());
        json.put("id", device.getAddress());
        if (device.getBluetoothClass() != null) {
            json.put("class", device.getBluetoothClass().getDeviceClass());
        }
        return json;
    }

    private void setConfig(String message, CallbackContext callbackContext) {
        Context context=this.cordova.getActivity().getApplicationContext(); 
        mBTController = BluetoothController.getInstance().build(context);
    
        mBLEController = BluetoothLEController.getInstance().build(context);

        mBTController.setBluetoothListener(new BluetoothListener() {
            @Override
            public void onActionStateChanged(int preState, int state) {
                // Callback when bluetooth power state changed.
            }
    
            @Override
            public void onActionDiscoveryStateChanged(String discoveryState) {
                // Callback when local Bluetooth adapter discovery process state changed.
            }
    
            @Override
            public void onActionScanModeChanged(int preScanMode, int scanMode) {
                // Callback when the current scan mode changed.
            }
    
            @Override
            public void onBluetoothServiceStateChanged(int state) {
                // Callback when the connection state changed.
            }
    
            @Override
            public void onActionDeviceFound(BluetoothDevice device) {
                // Callback when found device.
                deviceList.put(deviceToJSON(device));
            }
    
            @Override
            public void onReadData(final BluetoothDevice device, final byte[] data) {
                // Callback when remote device send data to current device.
            }
        });
        
        mBLEController.setBluetoothListener(new BluetoothLEListener() {
        
            @Override
            public void onReadData(final BluetoothGattCharacteristic characteristic) {
                // Read data from BLE device.
            }
    
            @Override
            public void onWriteData(final BluetoothGattCharacteristic characteristic) {
                // When write data to remote BLE device, the notification will send to here.
            }
    
            @Override
            public void onDataChanged(final BluetoothGattCharacteristic characteristic) {
                // When data changed, the notification will send to here.
            }
    
            @Override
            public void onActionStateChanged(int preState, int state) {
                // Callback when bluetooth power state changed.
            }
    
            @Override
            public void onActionDiscoveryStateChanged(String discoveryState) {
                // Callback when local Bluetooth adapter discovery process state changed.
            }
    
            @Override
            public void onActionScanModeChanged(int preScanMode, int scanMode) {
                // Callback when the current scan mode changed.
            }
    
            @Override
            public void onBluetoothServiceStateChanged(final int state) {
                // Callback when the connection state changed.
            }
    
            @Override
            public void onActionDeviceFound(final BluetoothDevice device, short rssi) {
                // Callback when found device.
                deviceList.put(deviceToJSON(device, rssi));
            }
        });

        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void scanDevices(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            mBLEController.startScan();
            mBTController.startScan();
            callbackContext.success(deviceList);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void listDevices(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(deviceList);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void connectDevice(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void subscribeConnected(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void write(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void subscribe(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void unsubscribe(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
