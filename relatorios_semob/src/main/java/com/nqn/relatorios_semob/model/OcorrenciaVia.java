package com.nqn.relatorios_semob.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_ocorrencia")
public class OcorrenciaVia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "relatorio_id", nullable = false)
    @JsonIgnore // 🚨 Essencial! Evita que a ocorrência tente renderizar o relatório todo de novo
    private Relatorio relatorio;

    @Column(name = "url_foto", nullable = false, length = 500)
    private String urlFoto;

    @Column(name = "descricao_defeito", nullable = false)
    private String descricaoDefeito;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "data_registro")
    private LocalDateTime dataRegistro;

    // Construtor Padrão (Obrigatório para o Hibernate)
    public OcorrenciaVia() {
        this.dataRegistro = LocalDateTime.now();
    }

    // Construtor Completo
    public OcorrenciaVia(Long id, Relatorio relatorio, String urlFoto, String descricaoDefeito, Double latitude, Double longitude, LocalDateTime dataRegistro) {
        this.id = id;
        this.relatorio = relatorio;
        this.urlFoto = urlFoto;
        this.descricaoDefeito = descricaoDefeito;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dataRegistro = dataRegistro != null ? dataRegistro : LocalDateTime.now();
    }

    // Getters e Setters Tradicionais
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Relatorio getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(Relatorio relatorio) {
        this.relatorio = relatorio;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getDescricaoDefeito() {
        return descricaoDefeito;
    }

    public void setDescricaoDefeito(String descricaoDefeito) {
        this.descricaoDefeito = descricaoDefeito;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }
}
