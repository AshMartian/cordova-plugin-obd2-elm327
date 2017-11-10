var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'obd2-elm327', 'coolMethod', [arg0]);
};
