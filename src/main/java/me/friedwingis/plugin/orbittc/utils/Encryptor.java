package me.friedwingis.plugin.orbittc.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

/**
 * Copyright FriedWingis - 2023
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 **/
public class Encryptor {

    private static final char[] SECRET = "".toCharArray();
    private static final byte[] SALT = new byte[] { };
    private static final String ALGORITHM = "";

    public static String encrypt(final String plainText) throws GeneralSecurityException {
        if (plainText == null)
            return null;

        final Cipher cipher = get();
        final byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private static Cipher get() throws GeneralSecurityException {
        final SecretKey key = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(new PBEKeySpec(SECRET));
        final Cipher cipher = Cipher.getInstance(ALGORITHM);

        cipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return cipher;
    }
}
