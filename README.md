# ABreak IoT - Sistema de Monitoramento de Pausas Saudáveis

## 📋 Sobre o Projeto

O **ABreak** é uma solução IoT que promove a saúde no trabalho através do monitoramento inteligente de pausas. O sistema alerta os trabalhadores quando é hora de fazer uma pausa, ajudando a prevenir fadiga, estresse e problemas de saúde relacionados ao trabalho contínuo.

## 🎯 Objetivo

Melhorar o bem-estar dos trabalhadores incentivando pausas regulares através de:
- Monitoramento do tempo de trabalho contínuo
- Alertas visuais e sonoros
- Registro e estatísticas de pausas realizadas
- Acompanhamento de metas diárias

## 🔧 Componentes Utilizados

### Hardware (Simulação Wokwi)
- **ESP32** - Microcontrolador principal
- **Display LCD 16x2 (I2C)** - Informações em tempo real
- **LED Verde** - Indica trabalho normal
- **LED Vermelho** - Alerta de pausa necessária
- **Buzzer** - Alertas sonoros
- **Botão** - Iniciar/finalizar pausas

### Software e Protocolos
- **Arduino IDE/Wokwi** - Desenvolvimento do firmware
- **MQTT** - Comunicação em tempo real
- **JSON** - Formato de dados
- **WiFi** - Conectividade

## 📡 Arquitetura da Solução

```
[ESP32 + Sensores] → [WiFi] → [MQTT Broker] → [Dashboard/App]
        ↓
   [Display LCD]
   [LEDs/Buzzer]
```

### Tópicos MQTT Utilizados
- `abreak/pausas` - Eventos de início/fim de pausas
- `abreak/status` - Status geral do sistema
- `abreak/alertas` - Alertas preventivos e urgentes
- `abreak/config` - Configurações remotas

## 🚀 Como Executar

### 1. Simulação no Wokwi

**Acesse**: https://wokwi.com/projects/447352664930765825

**Passos**:
1. Clique em "Start Simulation"
2. Observe o display LCD mostrando o tempo até a próxima pausa
3. Pressione o botão quando o alerta aparecer para iniciar a pausa
4. Aguarde pelo menos 15 segundos em pausa
5. Pressione o botão novamente para voltar ao trabalho

### 2. Configuração Local

Se quiser rodar localmente:

```bash
# 1. Clone o repositório
git clone https://github.com/ehojonv/abreak-iot.git

# 2. Abra o arquivo .ino no Arduino IDE
# 3. Instale as bibliotecas necessárias:
#    - PubSubClient
#    - ArduinoJson
#    - LiquidCrystal_I2C

# 4. Selecione a placa ESP32
# 5. Faça o upload do código
```

## 📊 Funcionalidades

### ⏱️ Controle de Tempo
- **Intervalo de trabalho**: 60 segundos (configurável)
- **Aviso prévio**: 10 segundos antes do alerta
- **Pausa mínima**: 15 segundos

### 🚦 Sistema de Alertas
1. **LED Verde**: Trabalhando normalmente
2. **LED Vermelho + Buzzer**: Hora da pausa obrigatória
3. **Aviso preventivo**: Notificação 10s antes

### 📈 Estatísticas
- Total de pausas realizadas
- Tempo em pausa
- Meta diária (padrão: 8 pausas)
- Celebração ao atingir a meta

### 📱 Display LCD
Mostra em tempo real:
- Tempo para próxima pausa
- Pausas realizadas vs meta
- Status de pausa ativa
- Mensagens motivacionais

## 📦 Dados Enviados via MQTT

### Evento de Pausa (JSON)
```json
{
  "evento": "pausa",
  "tipo": "iniciada",
  "timestamp": 123456,
  "pausa_numero": 3,
  "pausas_hoje": 3,
  "meta_diaria": 8,
  "usuario_id": "user_demo",
  "dispositivo": "ESP32_ABreak"
}
```

### Status do Sistema
```json
{
  "status": "ativo",
  "evento": "iniciado",
  "total_pausas": 5,
  "pausas_hoje": 5,
  "em_pausa": false,
  "sistema": {
    "wifi_rssi": -45,
    "memoria_livre": 234567
  }
}
```

**Conteúdo** (até 3 minutos):
- Apresentação da proposta
- Funcionamento na simulação Wokwi
- Demonstração dos alertas
- Envio de dados via MQTT
- Benefícios para o usuário

## 👥 Equipe

- **Felipe Anselmo** - RM: 560661
- **João Vinicius Alves** - RM: 559369
- **Matheus Mariotto** - RM: 560276

## 🎯 Aplicação no Futuro do Trabalho

O ABreak atende diretamente aos desafios propostos:

✅ **Saúde e Bem-Estar**: Previne fadiga e problemas ergonômicos  
✅ **Trabalho Híbrido**: Funciona em qualquer ambiente  
✅ **Dados e Análise**: Métricas para RH e gestão  
✅ **Tecnologia + Humanidade**: IA como parceira do trabalhador  
✅ **ODS 8**: Trabalho decente e ambiente saudável  

## 🔄 Possíveis Integrações

- **Backend**: API em Java/C# para armazenamento de dados
- **Mobile**: App para acompanhamento pessoal
- **Dashboard**: Visualização gerencial em tempo real
- **IA**: Recomendações personalizadas de pausas

## 💡 Melhorias Futuras

- [ ] Sensor de batimentos cardíacos
- [ ] Detecção de postura (acelerômetro)
- [ ] Sugestões de exercícios durante pausas
- [ ] Gamificação com pontos e rankings
- [ ] Machine Learning para pausas personalizadas
- [ ] Integração com smartwatch

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: C++ (Arduino)
- **Protocolo**: MQTT (JSON)
- **Conectividade**: WiFi
- **Hardware**: ESP32
- **Bibliotecas**: PubSubClient, ArduinoJson, LiquidCrystal_I2C

## 📄 Licença

Projeto acadêmico - Global Solution 2025/2 - FIAP  
**Tema**: O Futuro do Trabalho  
**Disciplina**: Disruptive Architectures: IoT, IoB & Generative AI

---

**⭐ Se este projeto ajudou você, deixe uma estrela no repositório!**