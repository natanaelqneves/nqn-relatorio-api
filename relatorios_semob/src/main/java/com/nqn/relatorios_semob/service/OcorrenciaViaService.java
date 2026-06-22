package com.nqn.relatorios_semob.service;


import com.nqn.relatorios_semob.dto.OcorrenciaViaRequestDTO;
import com.nqn.relatorios_semob.model.OcorrenciaVia;
import com.nqn.relatorios_semob.model.Relatorio;
import com.nqn.relatorios_semob.repository.OcorrenciaViaRepository;
import com.nqn.relatorios_semob.repository.RelatorioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OcorrenciaViaService {

    private final OcorrenciaViaRepository ocorrenciaViaRepository;
    private final RelatorioRepository relatorioRepository;

    // Injeção via Construtor (Boa prática do Spring, dispensa o @Autowired)
    public OcorrenciaViaService(OcorrenciaViaRepository ocorrenciaViaRepository, RelatorioRepository relatorioRepository) {
        this.ocorrenciaViaRepository = ocorrenciaViaRepository;
        this.relatorioRepository = relatorioRepository;
    }

    @Transactional
    public void salvarOcorrencia(Long relatorioId, OcorrenciaViaRequestDTO dto) {
        // 1. Garante que o relatório do plantão existe
        Relatorio relatorio = relatorioRepository.findById(relatorioId)
                .orElseThrow(() -> new EntityNotFoundException("Relatório não encontrado com o ID: " + relatorioId));

        // 2. Instancia a entidade (Java puro, sem Lombok)
        OcorrenciaVia novaOcorrencia = new OcorrenciaVia();
        novaOcorrencia.setRelatorio(relatorio);
        novaOcorrencia.setUrlFoto(dto.urlFoto());
        novaOcorrencia.setDescricaoDefeito(dto.descricaoDefeito());
        novaOcorrencia.setLatitude(dto.latitude());
        novaOcorrencia.setLongitude(dto.longitude());

        // 3. Grava no banco
        ocorrenciaViaRepository.save(novaOcorrencia);
    }


    public void deletarPorIdRelatorio(Long id) {
        ocorrenciaViaRepository.deleteByRelatorioId(id);
    }
}
