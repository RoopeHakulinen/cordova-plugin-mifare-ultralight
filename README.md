# Cordova Mifare Ultralight plugin

This plugins lets you interact with Mifare Ultralight NFC tags on Android devices. You can use it to
- Detect Mifare Ultralight tag near-by the device.
- Connect/disconnet with Mifare Ultralight tags.
- Unlock Mifare Ultralight tags with PIN.
- Read Mifare Ultralight tags.
- Write Mifare Ultralight tags.
- Check if NFC is enabled on the device.

## Contents
* [Installation](#installation)
* [Usage](#usage)
* [FAQ](#faq)
* [Kudos](#kudos)

## Installation
### PhoneGap Build
Include 

```
<plugin name="cordova-plugin-mifare-ultralight" spec="~1.0.0"/>
```


in the `config.xml` of your application.

### Cordova CLI
```
cordova plugin add cordova-plugin-mifare-ultralight
```

## Usage
### Events
There is currently only one event emitted by the plugin and that is for found tags. To listen for the event, use this code:

```javascript
document.addEventListener('mifareTagDiscovered', (tag) => {
    alert(`Found tag: ${tag}`);
});
```

The tag passed as parameter contains tag id as an array of bytes as returned by `Tag` class method [`getId()`](https://developer.android.com/reference/android/nfc/Tag.html#getId()). 

### Methods
All methods provided can be found via `window.mifare`. If you are using TypeScript, you'll need to add this line on top of each file where you intend to access the plugin:

```typescript
declare const window;
```

Available methods:
* [mifare.enabled](#mifareenabled)
* [mifare.connect](#mifareconnect)
* [mifare.disconnect](#mifaredisconnect)
* [mifare.isConnected](#mifareisconnected)
* [mifare.read](#mifareread)
* [mifare.write](#mifarewrite)
* [mifare.unlock](#mifareunlock)

#### mifare.enabled
`mifare.enabled(success, failure)`

Checks for availability of NFC on device. If success callback is called, NFC is available and enabled. If failure callback is called instead, there is string parameter available telling whether device does have no NFC at all (`NO_NFC`) or if it just disabled (`NFC_DISABLED`).

##### Example
```javascript
window.mifare.enabled(() => alert('NFC enabled'), status => alert(status));
```

#### mifare.connect
`mifare.connect(success, failure)`

Connects to tag that was found. This is usually called inside the event handler for `mifareTagDiscovered`. 
 
##### Example
```javascript
document.addEventListener('mifareTagDiscovered', () => {
    window.mifare.connect(() => alert('Connected'), err => alert(`Couldn't connect because: ${err}`));
});
```

#### mifare.disconnect
`mifare.disconnect(success, failure)`

Disconnects from the tag that was connected.
 
##### Example
```javascript
window.mifare.disconnect(() => alert('Disonnected'), err => alert(`Couldn't disconnect because: ${err}`));
```

#### mifare.isConnected
`mifare.isConnected(success, failure)`
Checks if there is a connection to a tag.
 
##### Example
```javascript
window.mifare.isConnected(() => alert('Connected to tag'), () => alert('Not connected'));
```

#### mifare.read
`mifare.read(page, success, failure)`

Tries to read the page specified. Parameter `page` should be a number. If reading succeeds, the success callback will be called with data object as parameter. To access the actual data as string use `.data` for this object.

##### Example
```javascript
window.mifare.read(4, (response) => alert(response.data), err => alert(`Reading failed because ${err}`));
```

#### mifare.write
`mifare.write(page, data, success, failure)`

Tries to write the data to the page specified. Parameter `page` should be a number. Parameter `data` should be an array of 4 strings of 2 characters (4 bytes). 

##### Example
```javascript
window.mifare.write(4, ['AA', 'BB', 'CC', 'DD'], (response) => alert('Write ok'), err => alert(`Writing failed because ${err}`));
```

#### mifare.unlock
`mifare.unlock(pin, success, failure)`

Tries to unlock the tag. Parameter `pin` should be a number. 

##### Example
```javascript
window.mifare.unlock(0x1234, (response) => alert('Unlocked successfully'), err => alert(`Couldn't unlock because ${err}`));
```

## FAQ

*Q: Why is there no version to iOS or Windows Phone?*

A: This plugin was written for my own needs to interact with Mifare Ultralight in Cordova-based application on Android. 

iOS couldn't even be supported as Apple only permits its own wallet application to use the NFC. 

Windows Phone could be supported if someone wrote the necessary native code. I might be able to help a little with this but won't be implementing this all by myself.

*Q: Are there [Ionic native](https://github.com/driftyco/ionic-native) bindings for easier usage?*

A: Not yet unfortunately. It is something on the todo list for sure, though.

*Q: Why is there no event for tag lost?*

A: I would be happy to take in a Pull Request for this. It seemed somewhat cumbersome to implement.. Maybe at some point I will find the motivation to add it.

## Kudos
This plugin is heavily influenced by the excellent work done on [PhoneGap NFC Plugin](https://github.com/chariotsolutions/phonegap-nfc). Thank you for the effort you have put into it over the years.
