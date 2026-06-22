package com.nqn.relatorios_semob.service;


import com.nqn.relatorios_semob.dto.OcorrenciaViaResponseDTO;
import com.nqn.relatorios_semob.dto.RelatorioRequestDTO;
import com.nqn.relatorios_semob.dto.RelatorioResponseDTO;
import com.nqn.relatorios_semob.model.Relatorio;
import com.nqn.relatorios_semob.model.Usuario;
import com.nqn.relatorios_semob.model.Viatura;
import com.nqn.relatorios_semob.repository.RelatorioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RelatorioService {

    private final RelatorioRepository relatorioRepository;
    private final ViaturaService viaturaService;
    private final OcorrenciaViaService ocorrenciaViaService;

    public RelatorioService(RelatorioRepository relatorioRepository, ViaturaService viaturaService, OcorrenciaViaService ocorrenciaViaService) {
        this.relatorioRepository = relatorioRepository;
        this.viaturaService = viaturaService;
        this.ocorrenciaViaService = ocorrenciaViaService;
    }

    @Transactional
    public RelatorioResponseDTO cadastrar(RelatorioRequestDTO dto, Usuario usuarioLogado) {
        Viatura viatura = viaturaService.buscarViaturaPorPlaca(dto.placaViatura());
        viatura.setKmAtual(dto.kmFinal());



        Relatorio relatorio = new Relatorio();
        relatorio.setUsuario(usuarioLogado);
        relatorio.setDataDoServico(dto.dataDoServico());
        relatorio.setHorasPlantao(dto.horasPlantao());
        relatorio.setHoraIncio(dto.horaInicio());
        System.out.println(dto.horasPlantao());
        System.out.println(dto.horaInicio());
        Integer horaFim = (dto.horaInicio() + dto.horasPlantao()) % 24;
        System.out.println(horaFim);
        relatorio.setHoraFinal(horaFim);
        relatorio.setViatura(viatura);
        relatorio.setKmInicial(dto.kmInicial());
        relatorio.setKmFinal(dto.kmFinal());
        relatorio.setDescricaoServico(dto.descricaoServico());
        relatorio.setAvarias(dto.avarias());
        relatorio.setSinistro(dto.sinistro());

        Relatorio relatorioSalvo = relatorioRepository.save(relatorio);

        return novoRelatorioResponse(relatorioSalvo);
    }

    @Transactional
    public RelatorioResponseDTO atualizar(Long id, RelatorioRequestDTO dto){
        Viatura viatura = viaturaService.buscarViaturaPorPlaca(dto.placaViatura());
        viatura.setKmAtual(dto.kmFinal());

        Relatorio relatorio = relatorioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado com o ID: " + id));

        relatorio.setDataDoServico(dto.dataDoServico());
        relatorio.setHorasPlantao(dto.horasPlantao());
        relatorio.setHoraIncio(dto.horaInicio());
        System.out.println(dto.horasPlantao());
        System.out.println(dto.horaInicio());
        Integer horaFim = (dto.horaInicio() + dto.horasPlantao()) % 24;
        System.out.println(horaFim);
        relatorio.setHoraFinal(horaFim);
        relatorio.setViatura(viatura);
        relatorio.setKmInicial(dto.kmInicial());
        relatorio.setKmFinal(dto.kmFinal());
        relatorio.setDescricaoServico(dto.descricaoServico());
        relatorio.setAvarias(dto.avarias());
        relatorio.setSinistro(dto.sinistro());

        relatorioRepository.save(relatorio);

        RelatorioResponseDTO response = novoRelatorioResponse(relatorio);

        return response;
    }

    @Transactional
    public List<RelatorioResponseDTO> listarRelatorios(){
        List<RelatorioResponseDTO> listaDeRelatorios = new ArrayList<>();

        relatorioRepository.findAll().
                stream()
                .map(this::novoRelatorioResponse)
                .toList();

        return listaDeRelatorios;
    }

    public List<RelatorioResponseDTO> listarRelatoriosPorDataDoServico(){
        List<RelatorioResponseDTO> listaDeRelatorios = new ArrayList<>();

        relatorioRepository.findAllByOrderByDataDoServicoDesc().forEach(relatorio ->
                listaDeRelatorios.add(novoRelatorioResponse(relatorio)));

        return listaDeRelatorios;
    }

    public List<RelatorioResponseDTO> listarRelatoriosPorUsuarioEPeriodo(
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
            // Busca padrão (Trás todos os meses do usuário)
            relatorios = relatorioRepository.findByUsuarioIdOrderByDataDoServicoDesc(usuarioLogado.getId());
        }

        relatorios.forEach(relatorio ->
                listaDeRelatorios.add(novoRelatorioResponse(relatorio)));

        return listaDeRelatorios;
    }

    public void apagarRelatorio(Long id) {
        Relatorio relatorio = relatorioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado com o ID: " + id));

//        ocorrenciaViaService.deletarPorIdRelatorio(relatorio.getId());
        relatorioRepository.deleteById(id);
    }

    public Relatorio buscarPorIdEUsuario(Long id, Usuario usuarioLogado) {
        Relatorio relatorio = relatorioRepository.findByIdAndUsuarioId(id, usuarioLogado.getId())
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado com o ID: " + id));

        return relatorio;
    }


    private RelatorioResponseDTO novoRelatorioResponse(Relatorio relatorio) {
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

    public RelatorioResponseDTO converteRelatorioResponse(Relatorio relatorio) {
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
}

