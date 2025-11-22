package br.com.fiap.abreak_iot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pausas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pausa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String tipo; // "iniciada" ou "finalizada"
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    private Long duracaoMs;
    
    private Long duracaoSeg;
    
    private Integer pausaNumero;
    
    private Integer pausasHoje;
    
    private Integer metaDiaria;
    
    @Column(nullable = false)
    private String usuarioId;
    
    @Column(nullable = false)
    private String dispositivo;
    
    // Dados originais do MQTT em JSON
    @Column(columnDefinition = "TEXT")
    private String dadosOriginais;
}