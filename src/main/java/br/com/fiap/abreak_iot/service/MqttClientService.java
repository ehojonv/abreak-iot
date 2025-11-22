package br.com.fiap.abreak_iot.service;

import java.time.LocalDateTime;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.abreak_iot.model.Pausa;
import br.com.fiap.abreak_iot.repository.PausaRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MqttClientService {
    
    @Value("${mqtt.broker.url}")
    private String brokerUrl;
    
    @Value("${mqtt.client.id}")
    private String clientId;
    
    @Value("${mqtt.topic.pausas}")
    private String topicPausas;
    
    @Value("${mqtt.topic.status}")
    private String topicStatus;
    
    @Value("${mqtt.topic.alertas}")
    private String topicAlertas;
    
    @Value("${mqtt.topic.config}")
    private String topicConfig;
    
    private final PausaRepository pausaRepository;
    private final ObjectMapper objectMapper;
    private MqttClient mqttClient;
    
    public MqttClientService(PausaRepository pausaRepository) {
        this.pausaRepository = pausaRepository;
        this.objectMapper = new ObjectMapper();
    }
    
    @PostConstruct
    public void conectar() {
        try {
            log.info("🔌 Conectando ao broker MQTT: {}", brokerUrl);
            
            mqttClient = new MqttClient(brokerUrl, clientId);
            
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    log.error("❌ Conexão MQTT perdida!", cause);
                }
                
                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    processarMensagem(topic, new String(message.getPayload()));
                }
                
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Não usado neste caso
                }
            });
            
            mqttClient.connect(options);
            
            // Subscrever aos tópicos
            mqttClient.subscribe(topicPausas, 1);
            mqttClient.subscribe(topicStatus, 1);
            mqttClient.subscribe(topicAlertas, 1);
            
            log.info("✅ Conectado ao MQTT e inscrito nos tópicos!");
            
        } catch (MqttException e) {
            log.error("❌ Erro ao conectar MQTT", e);
        }
    }
    
    private void processarMensagem(String topic, String payload) {
        try {
            log.info("📩 Mensagem recebida [{}]: {}", topic, payload);
            
            JsonNode json = objectMapper.readTree(payload);
            
            // Processar pausas
            if (topic.equals(topicPausas)) {
                salvarPausa(json, payload);
            }
            
            // Processar status
            if (topic.equals(topicStatus)) {
                processarStatus(json);
            }
            
            // Processar alertas
            if (topic.equals(topicAlertas)) {
                processarAlerta(json);
            }
            
        } catch (Exception e) {
            log.error("❌ Erro ao processar mensagem", e);
        }
    }
    
    private void salvarPausa(JsonNode json, String payload) {
        try {
            Pausa pausa = new Pausa();
            pausa.setTipo(json.get("tipo").asText());
            pausa.setTimestamp(LocalDateTime.now());
            pausa.setPausaNumero(json.get("pausa_numero").asInt());
            pausa.setPausasHoje(json.get("pausas_hoje").asInt());
            pausa.setMetaDiaria(json.get("meta_diaria").asInt());
            pausa.setUsuarioId(json.get("usuario_id").asText());
            pausa.setDispositivo(json.get("dispositivo").asText());
            pausa.setDadosOriginais(payload);
            
            if (json.has("duracao_ms")) {
                pausa.setDuracaoMs(json.get("duracao_ms").asLong());
                pausa.setDuracaoSeg(json.get("duracao_seg").asLong());
            }
            
            pausaRepository.save(pausa);
            log.info("✅ Pausa salva: {} - #{}", pausa.getTipo(), pausa.getPausaNumero());
            
        } catch (Exception e) {
            log.error("❌ Erro ao salvar pausa", e);
        }
    }
    
    private void processarStatus(JsonNode json) {
        log.info("📊 Status recebido: evento={}, pausas_hoje={}", 
                json.get("evento").asText(),
                json.get("pausas_hoje").asInt());
    }
    
    private void processarAlerta(JsonNode json) {
        log.warn("⚠️ Alerta recebido: tipo={}, mensagem={}", 
                json.get("tipo_alerta").asText(),
                json.get("mensagem").asText());
    }
    
    // Método para enviar configurações para o ESP32
    public void enviarConfiguracao(int metaDiaria) {
        try {
            String config = String.format("{\"meta_diaria\": %d}", metaDiaria);
            mqttClient.publish(topicConfig, config.getBytes(), 1, false);
            log.info("✅ Configuração enviada: meta_diaria={}", metaDiaria);
        } catch (MqttException e) {
            log.error("❌ Erro ao enviar configuração", e);
        }
    }
    
    @PreDestroy
    public void desconectar() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                mqttClient.close();
                log.info("🔌 Desconectado do MQTT");
            }
        } catch (MqttException e) {
            log.error("❌ Erro ao desconectar", e);
        }
    }
}