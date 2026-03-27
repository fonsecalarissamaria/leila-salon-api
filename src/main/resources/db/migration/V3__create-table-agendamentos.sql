CREATE TABLE agendamentos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    data DATETIME NOT NULL,
    servicos VARCHAR(255),
    ativo TINYINT NOT NULL DEFAULT 1,
    cliente_id BIGINT NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_agendamento_cliente
    FOREIGN KEY (cliente_id)
    REFERENCES clientes(id)
);