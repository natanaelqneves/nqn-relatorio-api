package com.nqn.relatorios_semob.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CriptografiaUtil {

    private static final String ALGORITMO = "AES";

    @Value("${KEY.SECRET}")
    private String chaveSecretaString;

    public String criptografar(String textoPuro) {
        try {
            SecretKeySpec key = new SecretKeySpec(chaveSecretaString.getBytes(), ALGORITMO);

            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] criptografado = cipher.doFinal(textoPuro.getBytes());
            return Base64.getEncoder().encodeToString(criptografado);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar dados", e);
        }
    }

    public String descriptografar(String textoCriptografado) {
        try {
            SecretKeySpec key = new SecretKeySpec(chaveSecretaString.getBytes(), ALGORITMO);

            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodificado = Base64.getDecoder().decode(textoCriptografado);
            byte[] descriptografado = cipher.doFinal(decodificado);
            return new String(descriptografado);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar dados", e);
        }
    }
}