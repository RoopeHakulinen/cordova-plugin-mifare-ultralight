var exec = require('cordova/exec');


exports.enabled = function (success, error) {
    exec(success, error, "cordova-plugin-mifare-ultralight", "enabled", []);
};

exports.connect = function (success, error) {
    exec(success, error, "cordova-plugin-mifare-ultralight", "connect", []);
};

exports.disconnect = function (success, error) {
    exec(success, error, "cordova-plugin-mifare-ultralight", "disconnect", []);
};

exports.isConnected = function (success, error) {
    exec(success, error, "cordova-plugin-mifare-ultralight", "isConnected", []);
};

exports.read = function (page, success, error) {
    exec(success, error, "cordova-plugin-mifare-ultralight", "read", [page]);
};

exports.write = function (page, data, success, error) {
    exec(success, error, "cordova-plugin-mifare-ultralight", "write", [page, data]);
};

exports.unlock = function (pin, success, error) {
    exec(success, error, "cordova-plugin-mifare-ultralight", "unlock", [pin]);
};

exports.lock = function (
    pinPage,
    pinAckPage,
    protectionPage,
    firstPageToBeProtectedPage,
    firstPageToBeProtected,
    pin,
    protectAlsoReads,
    authenticationTryLimit,
    success,
    error
) {
    exec(success, error, "cordova-plugin-mifare-ultralight", "lock", [
        pinPage,
        pinAckPage,
        protectionPage,
        firstPageToBeProtectedPage,
        firstPageToBeProtected,
        pin,
        protectAlsoReads,
        authenticationTryLimit
    ]);
};

exports.lockNTAG212 = function (firstPageToBeProtected, pin, protectAlsoReads, authenticationTryLimit, success, error) {
    exports.lock(39, 40, 38, 37, firstPageToBeProtected, pin, protectAlsoReads, authenticationTryLimit, success, error);
};

exports.lockMF0UL11 = function (firstPageToBeProtected, pin, protectAlsoReads, authenticationTryLimit, success, error) {
    exports.lock(12, 13, 11, 10, firstPageToBeProtected, pin, protectAlsoReads, authenticationTryLimit, success, error);
};

exports.lockMF0UL21 = function (firstPageToBeProtected, pin, protectAlsoReads, authenticationTryLimit, success, error) {
    exports.lock(27, 28, 26, 25, firstPageToBeProtected, pin, protectAlsoReads, authenticationTryLimit, success, error);
};