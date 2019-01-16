package club.nsdn.nyasamatelecom.network.webservice;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class CryptManager {

    public static KeyPair createNewKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            NyaSamaTelecom.logger.error("Key pair generation failed!");
            return null;
        }
    }

    public static byte[] encryptData(Key key, byte[] data) {
        return cipherOperation(1, key, data);
    }

    public static byte[] decryptData(Key key, byte[] data) {
        return cipherOperation(2, key, data);
    }

    private static byte[] cipherOperation(int mode, Key key, byte[] data) {
        try {
            return createTheCipherInstance(mode, key.getAlgorithm(), key).doFinal(data);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        NyaSamaTelecom.logger.error("Cipher data failed!");
        return null;
    }

    private static Cipher createTheCipherInstance(int mode, String algorithm, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(mode, key);
            return cipher;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        NyaSamaTelecom.logger.error("Cipher creation failed!");
        return null;
    }


}
