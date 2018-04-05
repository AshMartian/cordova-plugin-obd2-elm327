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
import android.provider.Settings;
import android.util.Log;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;

import co.lujun.lmbluetoothsdk.BluetoothController;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;
import co.lujun.lmbluetoothsdk.base.State;
import jdk.nashorn.internal.codegen.CompilerConstants.Call;
import co.lujun.lmbluetoothsdk.BluetoothLEController;
import co.lujun.lmbluetoothsdk.base.BluetoothLEListener;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

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
    private static final String ISCONNECTED = "isConnected";
    private static final String SUBSCRIBECONNECT = "subscribeConnected";
    private static final String WRITE = "write";
    private static final String LIST = "listDevices";
    private static final String SUBSCRIBE = "subscribe";
    private static final String UNSUBSCRIBE = "unsubscribe";

    private static BluetoothController mBTController;
    private static BluetoothLEController mBLEController;

    private CallbackContext connectCallback;

    private Set<String> deviceList = new HashSet<String>();
    private JSONArray deviceJSON = new JSONArray();

    private String connectionType = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {


        if (action.equals(SETCONFIG)) {
            this.setConfig(args, callbackContext);
            return true;
        } else if (action.equals(SCAN)) {
            this.scanDevices(callbackContext);
            return true;
        } else if (action.equals(CONNECT)) {
            JSONObject message = args.getJSONObject(0);
            this.connectDevice(message, callbackContext);
            return true;
        } else if (action.equals(ISCONNECTED)) {
            this.isConnected(callbackContext);
            return true;
        } else if (action.equals(SUBSCRIBECONNECT)) {
            connectCallback = callbackContext;
            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);

            return true;
        } else if (action.equals(WRITE)) {
            String message = args.getString(0);
            this.write(message, callbackContext);
            return true;
        } else if (action.equals(LIST)) {
            this.listDevices(callbackContext);
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

    private JSONObject deviceToJSON(BluetoothDevice device, short rssi, String type) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            json.put("name", device.getName());
            json.put("address", device.getAddress());
            json.put("id", device.getAddress());
            json.put("type", type);
            json.put("rssi", rssi);
            if (device.getBluetoothClass() != null) {
                json.put("class", device.getBluetoothClass().getDeviceClass());
            }
            return json;
        } catch (JSONException e) {
            throw e;
        }
    }
    private JSONObject deviceToJSON(BluetoothDevice device) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            json.put("name", device.getName());
            json.put("address", device.getAddress());
            json.put("id", device.getAddress());
            if (device.getBluetoothClass() != null) {
                json.put("class", device.getBluetoothClass().getDeviceClass());
            }
            return json;
        } catch (JSONException e) {
            throw e;
        }
    }

    private void setConfig(JSONArray args, CallbackContext callbackContext) throws JSONException {
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
                if (connectCallback != null) {
                    PluginResult result = new PluginResult(PluginResult.Status.OK, state);
                    result.setKeepCallback(true);
                    connectCallback.sendPluginResult(result);
                }
            }
    
            @Override
            public void onActionDeviceFound(BluetoothDevice device, short rssi) {
                // Callback when found device.
                try {
                    int numDeviecs = deviceList.size();
                    
                    deviceList.add(device.getAddress());
                    if(deviceList.size() != numDeviecs) {
                        deviceJSON.put(deviceToJSON(device, rssi, "BT"));
                    }
                } catch(JSONException e) {

                }
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
            public void onDiscoveringCharacteristics(List<BluetoothGattCharacteristic> characteristics) {

            }


            @Override
            public void onDiscoveringServices(List<BluetoothGattService> services) {

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
                if (connectCallback != null) {
                    PluginResult result = new PluginResult(PluginResult.Status.OK, state);
                    result.setKeepCallback(true);
                    connectCallback.sendPluginResult(result);
                }
            }

            @Override
            public void onActionDeviceFound(final BluetoothDevice device, short rssi) {
                // Callback when found device.
                try {
                    int numDeviecs = deviceList.size();
                    
                    deviceList.add(device.getAddress());
                    if(deviceList.size() != numDeviecs) {
                        deviceJSON.put(deviceToJSON(device, rssi, "BLE"));
                    }
                } catch(JSONException e) {

                }
            }
        });
        if (!mBTController.isEnabled()) {
            mBTController.openBluetooth();
        }

        callbackContext.success();
    }

    private void scanDevices( CallbackContext callbackContext) {
        deviceList = new HashSet<String>();
        deviceJSON = new JSONArray();
        if (mBLEController.isSupportBLE()){
            mBLEController.startScan();
        }
        mBTController.startScan();
        callbackContext.success();
    }

    private void listDevices(CallbackContext callbackContext) {
        callbackContext.success(deviceJSON);
    }

    private void connectDevice(JSONObject device, CallbackContext callbackContext) throws JSONException {
        if (device != null) {
            connectionType = device.getString("type");
            if(connectionType == "BLE") {
                mBLEController.connect(device.getString("address"));
            } else if(connectionType == "BT") {
                mBTController.connect(device.getString("address"));
            } else if(connectionType == "WIFI") {

            }
            
            callbackContext.success(device);
        } else {
            callbackContext.error("Expected one non-empty device object argument.");
        }
    }

    private void isConnected(CallbackContext callbackContext) throws JSONException {
        if(connectionType != null) {
            if(connectionType == "BLE") {
                callbackContext.success(deviceToJSON(mBLEController.getConnectedDevice()));
            } else if(connectionType == "BT") {
                callbackContext.success(deviceToJSON(mBTController.getConnectedDevice()));
            } else if(connectionType == "WIFI") {

            }
        } else {
            callbackContext.error("disconnected");
        }
    }

    private void subscribeConnected(CallbackContext callbackContext) {
        //callbackContext.success();
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
