package br.com.fiap.abreak_iot.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.abreak_iot.model.Pausa;
import br.com.fiap.abreak_iot.repository.PausaRepository;
import br.com.fiap.abreak_iot.service.MqttClientService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pausas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para desenvolvimento
public class PausaController {

    private final PausaRepository pausaRepository;
    private final MqttClientService mqttService;

    /**
     * GET /api/pausas - Lista todas as pausas
     */
    @GetMapping
    public ResponseEntity<List<Pausa>> listarTodas() {
        return ResponseEntity.ok(pausaRepository.findAll());
    }

    /**
     * GET /api/pausas/usuario/{usuarioId} - Pausas de um usuário
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pausa>> listarPorUsuario(@PathVariable String usuarioId) {
        List<Pausa> pausas = pausaRepository.findByUsuarioIdOrderByTimestampDesc(usuarioId);
        return ResponseEntity.ok(pausas);
    }

    /**
     * GET /api/pausas/usuario/{usuarioId}/hoje - Pausas de hoje
     */
    @GetMapping("/usuario/{usuarioId}/hoje")
    public ResponseEntity<List<Pausa>> listarHoje(@PathVariable String usuarioId) {
        LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        List<Pausa> pausas = pausaRepository.findByUsuarioIdAndTimestampAfter(usuarioId, inicioDia);
        return ResponseEntity.ok(pausas);
    }

    /**
     * GET /api/pausas/usuario/{usuarioId}/resumo - Resumo do dia
     */
    @GetMapping("/usuario/{usuarioId}/resumo")
    public ResponseEntity<Map<String, Object>> obterResumo(@PathVariable String usuarioId) {
        LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);

        Long totalPausas = pausaRepository.contarPausasHoje(usuarioId, inicioDia);
        List<Pausa> pausasHoje = pausaRepository.findByUsuarioIdAndTimestampAfter(usuarioId, inicioDia);

        long tempoTotalPausas = pausasHoje.stream()
                .filter(p -> p.getDuracaoMs() != null)
                .mapToLong(Pausa::getDuracaoMs)
                .sum();

        Map<String, Object> resumo = new HashMap<>();
        resumo.put("totalPausasHoje", totalPausas);
        resumo.put("tempoTotalMinutos", tempoTotalPausas / 60000);
        resumo.put("metaDiaria", 8);
        resumo.put("metaCumprida", totalPausas >= 8);
        resumo.put("pausas", pausasHoje);

        return ResponseEntity.ok(resumo);
    }

    /**
     * GET /api/pausas/ultimas - Últimas 10 pausas
     */
    @GetMapping("/ultimas")
    public ResponseEntity<List<Pausa>> listarUltimas() {
        List<Pausa> pausas = pausaRepository.findTop10ByUsuarioIdOrderByTimestampDesc("user_demo");
        return ResponseEntity.ok(pausas);
    }

    /**
     * POST /api/pausas/config - Alterar configuração do ESP32
     */
    @PostMapping("/config")
    public ResponseEntity<Map<String, String>> configurar(@RequestBody Map<String, Integer> config) {
        Integer metaDiaria = config.get("meta_diaria");

        if (metaDiaria == null || metaDiaria < 1 || metaDiaria > 20) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Meta diária deve estar entre 1 e 20"));
        }

        mqttService.enviarConfiguracao(metaDiaria);

        return ResponseEntity.ok(Map.of(
                "mensagem", "Configuração enviada com sucesso",
                "meta_diaria", String.valueOf(metaDiaria)));
    }

    /**
     * GET /api/pausas/saude - Health check
     */
    @GetMapping("/saude")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "OK");
        status.put("timestamp", LocalDateTime.now());
        status.put("totalPausas", pausaRepository.count());
        return ResponseEntity.ok(status);
    }
}