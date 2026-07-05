package com.nqn.relatorios_semob.service;


import com.nqn.relatorios_semob.dto.OcorrenciaRequestDTO;
import com.nqn.relatorios_semob.dto.OcorrenciaResponseDTO;
import com.nqn.relatorios_semob.model.Ocorrencia;
import com.nqn.relatorios_semob.model.Relatorio;
import com.nqn.relatorios_semob.repository.OcorrenciaRepository;
import com.nqn.relatorios_semob.repository.RelatorioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final RelatorioRepository relatorioRepository;

    public OcorrenciaService(OcorrenciaRepository ocorrenciaRepository, RelatorioRepository relatorioRepository) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.relatorioRepository = relatorioRepository;
    }

    @Transactional
    public OcorrenciaResponseDTO cadastrar(Long relatorioId, OcorrenciaRequestDTO dto) {
        Relatorio relatorio = relatorioRepository.findById(relatorioId)
                .orElseThrow(() -> new EntityNotFoundException("Relatório não encontrado com o ID: " + relatorioId));

        Ocorrencia novaOcorrencia = new Ocorrencia();

        novaOcorrencia.setRelatorio(relatorio);
        novaOcorrencia.setUrlFoto(dto.urlFoto());
        novaOcorrencia.setDescricaoDefeito(dto.descricaoDefeito());
        novaOcorrencia.setLatitude(dto.latitude());
        novaOcorrencia.setLongitude(dto.longitude());

        Ocorrencia ocorrenciaSalva = ocorrenciaRepository.save(novaOcorrencia);

        return gerarOcorrenciaResponse(ocorrenciaSalva);
    }

    public void apagarPorId(Long ocorrenciaId) {
        Ocorrencia ocorrencia = ocorrenciaRepository.findById(ocorrenciaId)
                .orElseThrow(() -> new EntityNotFoundException("Ocorrência não encontrada com o ID: " + ocorrenciaId));

        ocorrenciaRepository.deleteById(ocorrenciaId);
    }

    public void apagarPorIdRelatorio(Long id) {
        ocorrenciaRepository.deleteByRelatorioId(id);
    }

    private OcorrenciaResponseDTO gerarOcorrenciaResponse(Ocorrencia ocorrencia){
        return new OcorrenciaResponseDTO(
                ocorrencia.getId(),
                ocorrencia.getDescricaoDefeito(),
                ocorrencia.getUrlFoto(),
                ocorrencia.getLatitude(),
                ocorrencia.getLongitude()
        );
    }


}
