-- Script de criação da tabela PAUSAS para Oracle Database
-- Baseado na entidade JPA Pausa

-- Remove a tabela se já existir (opcional - comentar se não quiser dropar)
-- DROP TABLE pausas CASCADE CONSTRAINTS;

-- Remove a sequence se já existir (opcional)
-- DROP SEQUENCE pausas_seq;

-- Cria a sequence para o ID (usado pelo IDENTITY)
CREATE SEQUENCE pausas_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Cria a tabela PAUSAS
CREATE TABLE pausas (
    id NUMBER(19) NOT NULL,
    tipo VARCHAR2(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    duracao_ms NUMBER(19),
    duracao_seg NUMBER(19),
    pausa_numero NUMBER(10),
    pausas_hoje NUMBER(10),
    meta_diaria NUMBER(10),
    usuario_id VARCHAR2(255) NOT NULL,
    dispositivo VARCHAR2(255) NOT NULL,
    dados_originais CLOB,
    
    -- Constraint de chave primária
    CONSTRAINT pk_pausas PRIMARY KEY (id)
);

-- Cria índices para melhorar performance de consultas
CREATE INDEX idx_pausas_usuario_id ON pausas(usuario_id);
CREATE INDEX idx_pausas_timestamp ON pausas(timestamp);
CREATE INDEX idx_pausas_tipo ON pausas(tipo);
CREATE INDEX idx_pausas_dispositivo ON pausas(dispositivo);

-- Cria um índice composto para consultas por usuário e data
CREATE INDEX idx_pausas_usuario_timestamp ON pausas(usuario_id, timestamp);

-- Adiciona comentários à tabela e colunas (documentação)
COMMENT ON TABLE pausas IS 'Tabela de registro de pausas do sistema IoT';
COMMENT ON COLUMN pausas.id IS 'Identificador único da pausa';
COMMENT ON COLUMN pausas.tipo IS 'Tipo da pausa: iniciada ou finalizada';
COMMENT ON COLUMN pausas.timestamp IS 'Data e hora do registro da pausa';
COMMENT ON COLUMN pausas.duracao_ms IS 'Duração da pausa em milissegundos';
COMMENT ON COLUMN pausas.duracao_seg IS 'Duração da pausa em segundos';
COMMENT ON COLUMN pausas.pausa_numero IS 'Número sequencial da pausa';
COMMENT ON COLUMN pausas.pausas_hoje IS 'Quantidade de pausas realizadas no dia';
COMMENT ON COLUMN pausas.meta_diaria IS 'Meta diária de pausas configurada';
COMMENT ON COLUMN pausas.usuario_id IS 'Identificador do usuário';
COMMENT ON COLUMN pausas.dispositivo IS 'Identificador do dispositivo IoT';
COMMENT ON COLUMN pausas.dados_originais IS 'Dados originais recebidos via MQTT em formato JSON';

-- Trigger para auto-incremento do ID (simula IDENTITY do Hibernate)
CREATE OR REPLACE TRIGGER trg_pausas_id
BEFORE INSERT ON pausas
FOR EACH ROW
WHEN (new.id IS NULL)
BEGIN
    SELECT pausas_seq.NEXTVAL INTO :new.id FROM dual;
END;
/

-- Verifica a criação
SELECT 'Tabela PAUSAS criada com sucesso!' AS status FROM dual;

-- Query para verificar a estrutura da tabela
-- DESC pausas;

-- Query para verificar os índices criados
-- SELECT index_name, column_name FROM user_ind_columns WHERE table_name = 'PAUSAS' ORDER BY index_name, column_position;