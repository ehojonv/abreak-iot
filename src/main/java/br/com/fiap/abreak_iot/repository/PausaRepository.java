package br.com.fiap.abreak_iot.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.fiap.abreak_iot.model.Pausa;

@Repository
public interface PausaRepository extends JpaRepository<Pausa, Long> {
    
    // Buscar pausas por usuário
    List<Pausa> findByUsuarioIdOrderByTimestampDesc(String usuarioId);
    
    // Buscar pausas de hoje
    List<Pausa> findByUsuarioIdAndTimestampAfter(String usuarioId, LocalDateTime inicio);
    
    // Contar pausas de hoje
    @Query("SELECT COUNT(p) FROM Pausa p WHERE p.usuarioId = ?1 AND p.timestamp >= ?2 AND p.tipo = 'finalizada'")
    Long contarPausasHoje(String usuarioId, LocalDateTime inicio);
    
    // Buscar últimas N pausas
    List<Pausa> findTop10ByUsuarioIdOrderByTimestampDesc(String usuarioId);
}