package com.nqn.relatorios_semob.service;


import com.nqn.relatorios_semob.dto.RelatorioRequestDTO;
import com.nqn.relatorios_semob.dto.RelatorioResponseDTO;
import com.nqn.relatorios_semob.model.Relatorio;
import com.nqn.relatorios_semob.model.Usuario;
import com.nqn.relatorios_semob.model.Viatura;
import com.nqn.relatorios_semob.repository.RelatorioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RelatorioService {

    private final RelatorioRepository relatorioRepository;
    private final ViaturaService viaturaService;

    public RelatorioService(RelatorioRepository relatorioRepository, ViaturaService viaturaService) {
        this.relatorioRepository = relatorioRepository;
        this.viaturaService = viaturaService;
    }

    @Transactional
    public RelatorioResponseDTO cadastrar(RelatorioRequestDTO dto, Usuario usuarioLogado) {

        Relatorio novoRelatorio = gerarRelatorio(new Relatorio(), dto, usuarioLogado);
        Relatorio relatorioSalvo = relatorioRepository.save(novoRelatorio);

        return gerarRelatorioResponse(relatorioSalvo);
    }

    @Transactional
    public RelatorioResponseDTO atualizar(Long id, RelatorioRequestDTO dto, Usuario usuarioLogado){
        Relatorio relatorioEncontrado = relatorioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Relatório não encontrado com o ID: " + id));

        boolean usuariosSaoIguais = relatorioEncontrado.getUsuario() != null && relatorioEncontrado.getUsuario().getId().equals(usuarioLogado.getId());

        if(!usuariosSaoIguais) {
            throw  new RuntimeException("Usuario não autorizado a alterar o relatório.");
        }

        Relatorio relatorioAtualizado = gerarRelatorio(relatorioEncontrado, dto, usuarioLogado);
        relatorioRepository.save(relatorioAtualizado);

        return gerarRelatorioResponse(relatorioAtualizado);
    }

    public List<RelatorioResponseDTO> buscarTodos(){

        return relatorioRepository.findAll().
                stream()
                .map(this::gerarRelatorioResponse)
                .toList();
    }

    public List<RelatorioResponseDTO> listarOrdenadoPorDataDoServico(){
        List<RelatorioResponseDTO> listaDeRelatorios = new ArrayList<>();

        return relatorioRepository.findAllByOrderByDataDoServicoDesc()
                .stream()
                .map(this::gerarRelatorioResponse)
                .toList();
    }

    public List<RelatorioResponseDTO> listarPorUsuarioEPeriodo(
            Usuario usuarioLogado,
            String dataInicioStr,
            String dataFimStr){

        List<RelatorioResponseDTO> listaDeRelatorios = new ArrayList<>();
        List<Relatorio> relatorios = new ArrayList<>();

        if (dataInicioStr != null && !dataInicioStr.trim().isEmpty()
                && dataFimStr != null && !dataFimStr.trim().isEmpty()) {

            LocalDate dataInicio = LocalDate.parse(dataInicioStr);
            LocalDate dataFim = LocalDate.parse(dataFimStr);

            relatorios = relatorioRepository.findByUsuarioIdAndPeriodo(usuarioLogado.getId(), dataInicio, dataFim);
        } else {
            relatorios = relatorioRepository.findByUsuarioIdAndPeriodo(usuarioLogado.getId(), LocalDate.now().withDayOfMonth(1), LocalDate.now());
        }

        relatorios.forEach(relatorio ->
                listaDeRelatorios.add(gerarRelatorioResponse(relatorio)));

        return listaDeRelatorios;
    }

    public void apagarRelatorio(Long id, Usuario usuarioLogado) {
        Relatorio relatorioEncontrado = relatorioRepository.findByIdAndUsuarioId(id, usuarioLogado.getId())
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado ou acesso não autorizado."));

        relatorioRepository.delete(relatorioEncontrado);
    }

    public Relatorio buscarPorIdEUsuario(Long id, Usuario usuarioLogado) {
        Relatorio relatorioEncontrado = relatorioRepository.findByIdAndUsuarioId(id, usuarioLogado.getId())
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado ou acesso não autorizado."));

        return relatorioEncontrado;
    }

    public RelatorioResponseDTO gerarRelatorioResponse(Relatorio relatorio) {
        return new RelatorioResponseDTO(
                relatorio.getId(),
                relatorio.getUsuario().getNomeCompleto(),
                relatorio.getUsuario().getMatricula(),
                relatorio.getUsuario().getEmail(),
                relatorio.getDataDoServico(),
                relatorio.getHorasPlantao(),
                relatorio.getHoraIncio(),
                relatorio.getHoraFinal(),
                relatorio.getViatura().getPlaca(),
                relatorio.getKmInicial(),
                relatorio.getKmFinal(),
                relatorio.getDescricaoServico(),
                relatorio.getSinistro(),
                relatorio.getAvarias(),
                relatorio.getOcorrencias()
        );
    }

    private Relatorio gerarRelatorio(Relatorio relatorio, RelatorioRequestDTO dto, Usuario usuarioLogado) {
        Viatura viatura = viaturaService.buscarViaturaPorPlaca(dto.placaViatura());
        viatura.setKmAtual(dto.kmFinal());

        Integer horaFim = (dto.horaInicio() + dto.horasPlantao()) % 24;
        relatorio.setUsuario(usuarioLogado);
        relatorio.setDataDoServico(dto.dataDoServico());
        relatorio.setHorasPlantao(dto.horasPlantao());
        relatorio.setHoraIncio(dto.horaInicio());
        relatorio.setHoraFinal(horaFim);
        relatorio.setViatura(viatura);
        relatorio.setKmInicial(dto.kmInicial());
        relatorio.setKmFinal(dto.kmFinal());
        relatorio.setDescricaoServico(dto.descricaoServico());
        relatorio.setAvarias(dto.avarias());
        relatorio.setSinistro(dto.sinistro());

        return relatorio;
    }
}