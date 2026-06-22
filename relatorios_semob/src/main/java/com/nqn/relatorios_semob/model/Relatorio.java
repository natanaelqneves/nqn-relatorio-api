package com.nqn.relatorios_semob.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tb_relatorio")
public class Relatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_do_servico", nullable = false)
    private LocalDate dataDoServico;

    @ManyToOne
    @JoinColumn(name = "viatura_id")
    private Viatura viatura;

    @Column(name = "horas_pantao")
    private Integer horasPlantao;

    @Column(name = "hora_inicio")
    private Integer horaIncio;

    @Column(name = "hora_final")
    private Integer horaFinal;

    @Column(name = "km_inicial", nullable = false)
    private Integer kmInicial;

    @Column(name = "km_final")
    private Integer kmFinal;

    @Column(name = "descricao_servico", columnDefinition = "TEXT", nullable = false)
    private String descricaoServico;

    @Column(columnDefinition = "TEXT")
    private String sinistro;

    @Column(columnDefinition = "TEXT")
    private String avarias;

    @OneToMany(mappedBy = "relatorio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OcorrenciaVia> ocorrencias;

    public Relatorio() {
    }

    public Relatorio(Long id, Usuario usuario, Viatura viatura, LocalDate dataDoServico, Integer horasPlantao, Integer horaIncio, Integer horaFinal, Integer kmInicial, Integer kmFinal, String descricaoServico, String sinistro, String avarias) {
        this.id = id;
        this.usuario = usuario;
        this.viatura = viatura;
        this.dataDoServico = dataDoServico;
        this.horasPlantao = horasPlantao;
        this.horaIncio = horaIncio;
        this.horaFinal = horaFinal;
        this.kmInicial = kmInicial;
        this.kmFinal = kmFinal;
        this.descricaoServico = descricaoServico;
        this.sinistro = sinistro;
        this.avarias = avarias;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getDataDoServico() {
        return dataDoServico;
    }

    public void setDataDoServico(LocalDate dataDoServico) {
        this.dataDoServico = dataDoServico;
    }

    public Viatura getViatura() {
        return viatura;
    }

    public void setViatura(Viatura viatura) {
        this.viatura = viatura;
    }

    public Integer getHorasPlantao() {
        return horasPlantao;
    }

    public void setHorasPlantao(Integer horasPlantao) {
        this.horasPlantao = horasPlantao;
    }

    public Integer getHoraIncio() {
        return horaIncio;
    }

    public void setHoraIncio(Integer horaIncio) {
        this.horaIncio = horaIncio;
    }

    public Integer getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(Integer horaFinal) {
        this.horaFinal = horaFinal;
    }

    public Integer getKmInicial() {
        return kmInicial;
    }

    public void setKmInicial(Integer kmInicial) {
        this.kmInicial = kmInicial;
    }

    public Integer getKmFinal() {
        return kmFinal;
    }

    public void setKmFinal(Integer kmFinal) {
        this.kmFinal = kmFinal;
    }

    public String getDescricaoServico() {
        return descricaoServico;
    }

    public void setDescricaoServico(String descricaoServico) {
        this.descricaoServico = descricaoServico;
    }

    public String getSinistro() {
        return sinistro;
    }

    public void setSinistro(String sinistro) {
        this.sinistro = sinistro;
    }

    public String getAvarias() {
        return avarias;
    }

    public void setAvarias(String avarias) {
        this.avarias = avarias;
    }

    public List<OcorrenciaVia> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(List<OcorrenciaVia> ocorrencias) {
        this.ocorrencias = ocorrencias;
    }


}