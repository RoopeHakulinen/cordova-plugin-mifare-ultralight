package fi.roopehakulinen.CordovaPluginMifareUltralight;

import org.apache.cordova.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

/**
 * This class echoes a string called from JavaScript.
 */
public class CordovaPluginMifareUltralight extends CordovaPlugin {
    private static final String TAG = "CordovaPluginMifareUltralight";
    private final List<IntentFilter> intentFilters = new ArrayList<IntentFilter>();
    private final ArrayList<String[]> techLists = new ArrayList<String[]>();
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private NfcAdapter mAdapter;
    private PendingIntent pendingIntent;
    private Tag tag = null;
    private MifareUltralight mifareUltralight = new MifareUltralight();
    private Intent savedIntent = null;

    private String javaScriptEventTemplate =
            "var e = document.createEvent(''Events'');\n" +
                    "e.initEvent(''{0}'');\n" +
                    "e.tag = {1};\n" +
                    "document.dispatchEvent(e);";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        startNfc();

        mAdapter = NfcAdapter.getDefaultAdapter(this.cordova.getActivity().getApplicationContext());

        intentFilters.add(new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED));
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        stopNfc();
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        startNfc();
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (mAdapter == null) {
            callbackContext.error("NO_NFC");
            return true;
        } else if (!mAdapter.isEnabled()) {
            callbackContext.error("NFC_DISABLED");
            return true;
        } else if (action.equals("enabled")) {
            callbackContext.success("NFC_OK");
            return true;
        }

        if (action.equals("connect")) {
            this.connect(callbackContext);
            return true;
        } else if (action.equals("disconnect")) {
            this.disconnect(callbackContext);
            return true;
        } else if (action.equals("isConnected")) {
            this.isConnected(callbackContext);
            return true;
        } else if (action.equals("read")) {
            final String arg0 = args.getString(0);
            final int pageOffset = Integer.parseInt(arg0);
            this.read(callbackContext, pageOffset);
            return true;
        } else if (action.equals("write")) {
            final String arg0 = args.getString(0);
            final int pageOffset = Integer.parseInt(arg0);
            final byte[] data = jsonToByteArray(args.getJSONArray(1));
            this.write(callbackContext, pageOffset, data);
            return true;
        } else if (action.equals("unlock")) {
            final String arg0 = args.getString(0);
            final long pin = Long.parseLong(arg0);
            this.unlock(callbackContext, pin);
            return true;
        }
        return false;
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent " + intent);
        super.onNewIntent(intent);
        setIntent(intent);
        savedIntent = intent;
        parseMessage();
    }

    private void connect(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (getIntent() == null) { // Lost Tag
                    clean(callbackContext, "No tag available.");
                    return;
                }

                final Tag tag = savedIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if (tag == null) {
                    clean(callbackContext, "No tag available.");
                    return;
                }
                try {
                    Log.i(TAG, "Tag is: " + tag);
                    mifareUltralight.connect(tag);
                    callbackContext.success();
                } catch (final Exception e) {
                    clean(callbackContext, e);
                }
            }
        });
    }

    private void disconnect(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (getIntent() == null) { // Lost Tag
                    clean(callbackContext, "No tag available.");
                    return;
                }
                try {
                    mifareUltralight.disconnect();
                    callbackContext.success();
                } catch (final Exception e) {
                    clean(callbackContext, e);
                }
            }
        });
    }

    private void isConnected(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (getIntent() == null) { // Lost Tag
                    clean(callbackContext, "No tag available.");
                    return;
                }
                try {
                    final boolean isConnected = mifareUltralight.isConnected();
                    final JSONObject result = new JSONObject();
                    result.put("connected", isConnected);
                    callbackContext.success(result);
                } catch (final Exception e) {
                    clean(callbackContext, e);
                }
            }
        });
    }

    private void read(final CallbackContext callbackContext, final int pageOffset) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (getIntent() == null) { // Lost Tag
                    clean(callbackContext, "No tag available.");
                    return;
                }
                try {
                    final byte[] data = mifareUltralight.read(pageOffset);
                    final JSONObject result = new JSONObject();
                    result.put("data", bytesToHex(data));
                    callbackContext.success(result);
                } catch (final Exception e) {
                    clean(callbackContext, e);
                }
            }
        });
    }

    private void write(final CallbackContext callbackContext, final int pageOffset, final byte[] data) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (getIntent() == null) { // Lost Tag
                    clean(callbackContext, "No tag available.");
                    return;
                }
                try {
                    mifareUltralight.write(pageOffset, data);
                    callbackContext.success();
                } catch (final Exception e) {
                    clean(callbackContext, e);
                }
            }
        });
    }

    private void unlock(final CallbackContext callbackContext, final long pin) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (getIntent() == null) { // Lost Tag
                    clean(callbackContext, "No tag available.");
                    return;
                }

                final Tag tag = savedIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if (tag == null) {
                    clean(callbackContext, "No tag available.");
                    return;
                }
                try {
                    final boolean success = mifareUltralight.unlockWithPin(pin);
                    if (success) {
                        callbackContext.success();
                    } else {
                        callbackContext.error("Incorrect PIN");
                    }
                } catch (final Exception e) {
                    clean(callbackContext, e);
                }
            }
        });
    }

    private void fireTagEvent(Tag tag, String name) {
        String command = MessageFormat.format(javaScriptEventTemplate, name, byteArrayToJSON(tag.getId()));
        this.webView.sendJavascript(command);
    }

    private void clean(final CallbackContext callbackContext, Exception e) {
        clean(callbackContext, "Error: " + e);
    }

    private void clean(final CallbackContext callbackContext, String error) {
        tag = null;
        callbackContext.error("Error: " + error);
    }

    static JSONArray byteArrayToJSON(byte[] bytes) {
        JSONArray json = new JSONArray();
        for (byte aByte : bytes) {
            json.put(aByte & 0xFF);
        }
        return json;
    }

    static byte[] jsonToByteArray(JSONArray json) throws JSONException {
        byte[] b = new byte[json.length()];
        for (int i = 0; i < json.length(); i++) {
            b[i] = (byte) Integer.parseInt(json.getString(i), 16);
        }
        return b;
    }

    private IntentFilter[] getIntentFilters() {
        return intentFilters.toArray(new IntentFilter[intentFilters.size()]);
    }

    private void startNfc() {
        createPendingIntent(); // onResume can call startNfc before execute

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());

                if (nfcAdapter != null && !getActivity().isFinishing()) {
                    try {
                        // nfcAdapter.enableForegroundDispatch(getActivity(), pendingIntent, getIntentFilters(), getTechLists());
                        nfcAdapter.enableForegroundDispatch(getActivity(), pendingIntent, null, null);
                    } catch (IllegalStateException e) {
                        // issue 110 - user exits app with home button while nfc is initializing
                        Log.w(TAG, "Illegal State Exception starting NFC. Assuming application is terminating.");
                    }

                }
            }
        });
    }

    private void stopNfc() {
        Log.d(TAG, "stopNfc");
        getActivity().runOnUiThread(new Runnable() {
            public void run() {

                NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());

                if (nfcAdapter != null) {
                    try {
                        nfcAdapter.disableForegroundDispatch(getActivity());
                    } catch (IllegalStateException e) {
                        // issue 125 - user exits app with back button while nfc
                        Log.w(TAG, "Illegal State Exception stopping NFC. Assuming application is terminating.");
                    }
                }
            }
        });
    }

    private void createPendingIntent() {
        if (pendingIntent == null) {
            Activity activity = getActivity();
            Intent intent = new Intent(activity, activity.getClass());
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        }
    }

    private Activity getActivity() {
        return this.cordova.getActivity();
    }

    private void parseMessage() {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "parseMessage " + getIntent());
                Intent intent = getIntent();
                String action = intent.getAction();
                Log.d(TAG, "action " + action);
                if (action == null) {
                    return;
                }

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
                    fireTagEvent(tag, "mifareTagDiscovered");
                }
                setIntent(new Intent());
            }
        });
    }

    private Intent getIntent() {
        return getActivity().getIntent();
    }

    private void setIntent(Intent intent) {
        getActivity().setIntent(intent);
    }

    private String bytesToHex(final byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
