-- Ejecutar en la base de datos inventario_roles
-- Tabla de historial de movimientos (Entrada/Salida) de productos.

CREATE TABLE IF NOT EXISTS historico (
    idHistorico INT(6) AUTO_INCREMENT PRIMARY KEY,
    idUsuario   INT(6) NOT NULL,
    idProducto  INT(6) NOT NULL,
    movimiento  VARCHAR(10) NOT NULL,
    cantidad    INT(6) NOT NULL,
    fecha       DATETIME NOT NULL,
    CONSTRAINT fk_historico_usuario  FOREIGN KEY (idUsuario)  REFERENCES Usuarios(idUsuario),
    CONSTRAINT fk_historico_producto FOREIGN KEY (idProducto) REFERENCES productos(idProducto)
);
