package fi.roopehakulinen.CordovaPluginMifareUltralight;

import android.nfc.Tag;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MifareUltralight {
    private android.nfc.tech.MifareUltralight mifare = null;

    public void connect(Tag tag) throws IOException {
        mifare = android.nfc.tech.MifareUltralight.get(tag);
        mifare.connect();
    }

    public boolean isConnected() {
        return mifare != null && mifare.isConnected();
    }

    public void disconnect() throws Exception {
        mifare.close();
        mifare = null;
    }

    public boolean unlockWithPin(int pin) throws Exception {
        final byte[] pinAsByteArray = intToByteArray(pin);
        byte[] response = mifare.transceive(new byte[]{
                (byte) 0x1B, // PWD_AUTH
                pinAsByteArray[0], pinAsByteArray[1], pinAsByteArray[2], pinAsByteArray[3]
        });
        if ((response != null) && (response.length >= 2)) {
            // byte[] pack = Arrays.copyOf(response, 2);
            return true;
        }
        return false;
    }

    public boolean lockWithPin(
            int pinPage,
            int pinAckPage,
            int protectionPage,
            int firstPageToBeProtectedPage,
            int firstPageToBeProtected,
            int pin,
            boolean protectAlsoReads,
            int authenticationTryLimit
    ) throws Exception {
        byte[] pack;
        // 1. Write the PIN
        final byte[] pinAsByteArray = intToByteArray(pin);
        byte[] response = mifare.writePage(page, pinAsByteArray);
        if ((response != null) && (response.length >= 2)) {
            pack = Arrays.copyOf(response, 2);
        } else {
            return false;
        }
        // 2. Write the acknowledgement
        byte[] data = {pack[0], pack[1], (byte) 0, (byte) 0};
        mifare.writePage(pinAckPage, data);

        // 3. Read the protection page to be able to only modify certain bits and then write it again
        response = mifare.readPages(protectionPage);
        if ((response != null) && (response.length >= 16)) {
            data = {
                    (byte) ((response[0] & 0x078) | (protectAlsoReads ? 0x080 : 0x000) | (authenticationTryLimit & 0x007)),
                    response[1], // Keep old value for byte 1
                    response[2], // Keep old value for byte 2
                    response[3] // Keep old value for byte 3
            };
            mifare.writePage(protectionPage, data);
        }

        // 4. Read the page to be able to only modify certain bits and then Write first page to be protected
        response = mifare.readPages(firstPageToBeProtectedPage);
        if ((response != null) && (response.length >= 16)) {
            data = {
                    response[0], // Keep old value for byte 0
                    response[1], // Keep old value for byte 1
                    response[2], // Keep old value for byte 2
                    (byte) (firstPageToBeProtected & 0x0ff)
            };
            response = mifare.writePage(firstPageToBeProtectedPage, data);
            if ((response != null) && (response.length >= 2)) {
                return true;
            }
        }

        return false;
    }

    public byte[] read(int pageOffset) throws Exception {
        if (mifare == null || !mifare.isConnected()) {
            throw new Exception();
        }
        return mifare.readPages(pageOffset);
    }

    public void write(int pageOffset, byte[] data) throws Exception {
        if (mifare == null || !mifare.isConnected()) {
            throw new Exception();
        }
        mifare.writePage(pageOffset, data);
    }

    private byte[] intToByteArray(int number) {
        return ByteBuffer.allocate(4).putInt(number).array();
    }
}
