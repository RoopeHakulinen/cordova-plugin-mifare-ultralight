<a name="1.0.4"></a>
# [1.0.4](https://github.com/RoopeHakulinen/cordova-plugin-mifare-ultralight/blob/master/CHANGELOG.md#1.0.4) (2019-02-01)

Allow unlocking with a number that is in Java in the value range of `Long`. Earlier only `Int` values were allowed. This should be a non-breaking change as the numbers are only passed from JavaScript where there is only a `number` type covering for both Java side data types.

<a name="1.0.3"></a>
# [1.0.3](https://github.com/RoopeHakulinen/cordova-plugin-mifare-ultralight/blob/master/CHANGELOG.md#1.0.3) (2018-04-12)

Call the error callback in case the unlocking of tag fails. So far it was only working correctly for the success use case.

<a name="1.0.2"></a>
# [1.0.2](https://github.com/RoopeHakulinen/cordova-plugin-mifare-ultralight/blob/master/CHANGELOG.md#1.0.2) (2018-02-19)

Fix the format of tag on _mifareTagDiscovered_ event.

<a name="1.0.1"></a>
# [1.0.1](https://github.com/RoopeHakulinen/cordova-plugin-mifare-ultralight/blob/master/CHANGELOG.md#1.0.1) (2017-07-10)

Actually return the tag identifier on each _mifareTagDiscovered_ event.

<a name="1.0.0"></a>
# [1.0.0](https://github.com/RoopeHakulinen/cordova-plugin-mifare-ultralight/blob/master/CHANGELOG.md#1.0.0) (2017-05-14)

Initial release with functionality and README instructions.