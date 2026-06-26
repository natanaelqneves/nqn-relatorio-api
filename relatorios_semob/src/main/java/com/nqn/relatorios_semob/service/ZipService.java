package com.nqn.relatorios_semob.service;


import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService {

    public byte[] compactarArquivos(Map<String, byte[]> arquivos) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (Map.Entry<String, byte[]> arquivo : arquivos.entrySet()) {
                ZipEntry entry = new ZipEntry(arquivo.getKey());
                zos.putNextEntry(entry);

                zos.write(arquivo.getValue() != null ? arquivo.getValue() : new byte[0]);
                zos.closeEntry();
            }

            zos.finish();
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar o arquivo ZIP de relatórios", e);
        }
    }
}