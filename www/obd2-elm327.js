var exec = require('cordova/exec');

module.exports = { 

    /**
     * Overwrites the configuration for ELM327 codes
     * Example object: ```
     * {
     *   getAll: {
     *     codes: ["ATAR", "ATCM", "ATCM", "ATCRA", "ATMA", "ATBD"],
     *     priority: 10,
     *     speed: 30,
     *     lastSent: null,
     *     requestName: 'getAll'
     *   },
     * }
     * ```
     * 
     * @param {object} configuration 
     * @param {function} success 
     * @param {function} failure 
     */
    setConfig: (configuration, success, failure) => {
        cordova.exec(success, failure, "obd2-elm327", "setConfig", [configuration]);
    },

        
    /**
     * Scan for nearby or available devices
     * 
     * @param {function} success - callback for successful scan returns array of devices
     * @param {function} failure - callback for failure to scan wifi/bluetooth
     * @returns {object}
     */
    scanDevices: (success, failure) => {
        cordova.exec(success, failure, "obd2-elm327", "scanDevices", []);
    },


    /**
     * Connect to a device
     * 
     * @param {object} device - Device from scanDevices function 
     * @param {function} success - Callback for successful connection
     * @param {function} failure - Callback for failed connection
     */
    connectDevice: (device, success, failure) => {
        cordova.exec(success, failure, "obd2-elm327", "connectDevice", [device]);
    },


    /**
     * Subscribe to the connection status
     * 
     * @param {function} success - Called when connection is successful
     * @param {function} failure - Called when disconnected
     */
    subscribeConnected: (success, failure) => {
        cordova.exec(success, failure, "obd2-elm327", "subscribeConnected", []);
    },


    /**
     * Write data to the connected device
     * 
     * @param {any} data - Data to send to the device
     * @param {function} success - Callback for successful write
     * @param {function} failure - Callback for failed write (not connected)
     */
    write: (data, success, failure) => {

        // convert to ArrayBuffer
        if (typeof data === 'string') {
            data = stringToArrayBuffer(data);
        } else if (data instanceof Array) {
            // assuming array of interger
            data = new Uint8Array(data).buffer;
        } else if (data instanceof Uint8Array) {
            data = data.buffer;
        }
        cordova.exec(success, failure, "obd2-elm327", "write", [data]);
    },


    /**
     * Subscribe to incoming data stream
     * 
     * @param {string} delimiter - Call success when this string is found
     * @param {function} success - Called for succesful subscribe
     * @param {function} failure - Called for failed subscribe
     */
    subscribe: (delimiter, success, failure) => {
        cordova.exec(success, failure, "obd2-elm327", "subscribe", [delimiter]);
    },


    /**
     * Unsubscribe from the data stream
     * 
     * @param {any} success - Called for successful subscription
     * @param {any} failure - Called for failed unsubscription (if not currently subscribed)
     */
    unsubscribe: (success, failure) => {
        cordova.exec(success, failure, "obd2-elm327", "unsubscribe", []);
    },


};

var stringToArrayBuffer = function(str) {
    var ret = new Uint8Array(str.length);
    for (var i = 0; i < str.length; i++) {
        ret[i] = str.charCodeAt(i);
    }
    return ret.buffer;
};