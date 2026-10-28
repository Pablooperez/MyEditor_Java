package com.mycompany.myeditor;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;

public class GestorCifrado {

    private static final String SALT = "MyEditorSalt2026"; // Salt fijo para derivar clave
    private static final byte[] IV = new byte[16]; // Vector de inicialización de 16 bytes

    // ==========================================
    // 1. CIFRADO AES-256 (Con Contraseña)
    // ==========================================
    public static String cifrarAES(String texto, String password) throws Exception {
        SecretKey secretKey = generarClaveAES(password);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(IV));
        byte[] bytesCifrados = cipher.doFinal(texto.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(bytesCifrados);
    }

    public static String descifrarAES(String textoCifrado, String password) throws Exception {
        SecretKey secretKey = generarClaveAES(password);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV));
        byte[] bytesDecodificados = Base64.getDecoder().decode(textoCifrado.trim());
        byte[] bytesDescifrados = cipher.doFinal(bytesDecodificados);
        return new String(bytesDescifrados, StandardCharsets.UTF_8);
    }

    private static SecretKey generarClaveAES(String password) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), SALT.getBytes(StandardCharsets.UTF_8), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    // ==========================================
    // 2. CIFRADO CÉSAR (Con desplazamiento numérico)
    // ==========================================
    public static String cifrarCesar(String texto, int desplazamiento) {
        StringBuilder resultado = new StringBuilder();
        for (char caracter : texto.toCharArray()) {
            resultado.append((char) (caracter + desplazamiento));
        }
        return resultado.toString();
    }

    public static String descifrarCesar(String texto, int desplazamiento) {
        return cifrarCesar(texto, -desplazamiento);
    }

    // ==========================================
    // 3. BASE64 (Ofuscación simple)
    // ==========================================
    public static String cifrarBase64(String texto) {
        return Base64.getEncoder().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    public static String descifrarBase64(String textoBase64) throws IllegalArgumentException {
        byte[] bytes = Base64.getDecoder().decode(textoBase64.trim());
        return new String(bytes, StandardCharsets.UTF_8);
    }
}