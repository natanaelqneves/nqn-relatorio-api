package com.nqn.relatorios_semob.service;

import com.nqn.relatorios_semob.dto.ViaturaResponseDTO;
import com.nqn.relatorios_semob.model.Viatura;
import com.nqn.relatorios_semob.repository.ViaturaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ViaturaService {

    private ViaturaRepository viaturaRepository;

    public ViaturaService(ViaturaRepository viaturaRepository) {
        this.viaturaRepository = viaturaRepository;
    }

    @Transactional
    public Viatura buscarViaturaPorId(Long id) {
        Viatura viatura = viaturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viatura não encontrada com o ID: " + id));

        return viatura;
    }

    @Transactional
    public Viatura buscarViaturaPorPlaca(String placa) {
        Viatura viatura =  viaturaRepository.findByPlaca(placa)
                .orElseThrow(() -> new RuntimeException("Viatura não encontrada com a placa: " + placa));
        return viatura;
    }

    public List<ViaturaResponseDTO> buscarViaturas(){
        List<ViaturaResponseDTO> listaViaturasResponse =new ArrayList<>();

        viaturaRepository.findAll().
                stream()
                .map(this::novaViatura)
                .toList();

        return listaViaturasResponse;
    }

    private ViaturaResponseDTO novaViatura(Viatura viatura) {
        return new ViaturaResponseDTO(
                viatura.getId(),
                viatura.getPlaca(),
                viatura.getModelo(),
                viatura.getAno(),
                viatura.getKmAtual()
        );
    }
}
