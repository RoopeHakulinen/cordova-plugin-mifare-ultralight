var exec = require('cordova/exec');


exports.enabled = function(success, error) {
    exec(success, error, "cordova-plugin-mifare-ultralight", "enabled", []);
};

exports.connect = function(success, error) {
    exec(success, error, "cordova-plugin-mifare-ultralight", "connect", []);
};

exports.disconnect = function(success, error) {
    exec(success, error, "cordova-plugin-mifare-ultralight", "disconnect", []);
};

exports.isConnected = function(success, error) {
    exec(success, error, "cordova-plugin-mifare-ultralight", "isConnected", []);
};

exports.read = function(page, success, error) {
    exec(success, error, "cordova-plugin-mifare-ultralight", "read", [page]);
};

exports.write = function(page, data, success, error) {
    exec(success, error, "cordova-plugin-mifare-ultralight", "write", [page, data]);
};

exports.unlock = function(pin, success, error) {
    exec(success, error, "cordova-plugin-mifare-ultralight", "unlock", [pin]);
};