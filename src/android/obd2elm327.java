package cordova-plugin-obd2-elm327;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class obd2-elm327 extends CordovaPlugin {

    /**
     * Actions
     */
    private static final String SETCONFIG = "setConfig";
    private static final String SCAN = "scanDevices";
    private static final String CONNECT = "connectDevice";
    private static final String SUBSCRIBECONNECT = "subscribeConnected";
    private static final String WRITE = "write";
    private static final String SUBSCRIBE = "subscribe";
    private static final String UNSUBSCRIBE = "unsubscribe";

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

    private void setConfig(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void scanDevices(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
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
