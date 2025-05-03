package me.friedwingis.plugin.orbittc.utils;

import me.friedwingis.plugin.orbittc.struct.TebexPackage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

/**
 * Copyright FriedWingis - 2023
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 **/
public class Serialization {

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(final String base64String) {
        try (final ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(base64String)); ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String serialize(final Object object) {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
