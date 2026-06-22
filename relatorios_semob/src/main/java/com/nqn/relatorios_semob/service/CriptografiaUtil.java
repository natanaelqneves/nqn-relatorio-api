package com.nqn.relatorios_semob.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component // 🚨 1. Transforma a classe em um Componente gerenciado pelo Spring
public class CriptografiaUtil {

    private static final String ALGORITMO = "AES";

    // 🚨 2. Injeta o texto puro da chave que está no application.properties ou no Render
    @Value("${KEY.SECRET}")
    private String chaveSecretaString;

    // 🚨 3. REMOVIDO o "static": Agora é um método de instância comum
    public String criptografar(String textoPuro) {
        try {
            // Cria a chave spec dinamicamente usando os bytes da string injetada
            SecretKeySpec key = new SecretKeySpec(chaveSecretaString.getBytes(), ALGORITMO);

            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] criptografado = cipher.doFinal(textoPuro.getBytes());
            return Base64.getEncoder().encodeToString(criptografado);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar dados", e);
        }
    }

    // 🚨 4. REMOVIDO o "static"
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