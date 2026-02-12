-- Ejecutar en la base de datos inventario_roles
-- Crea la tabla usuarios y un usuario por defecto para pruebas.

CREATE TABLE IF NOT EXISTS usuarios (
    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
    usuario   VARCHAR(50) NOT NULL UNIQUE,
    clave     VARCHAR(100) NOT NULL
);

-- Usuario de prueba: admin / admin (cámbialo en producción)
INSERT INTO usuarios (usuario, clave) VALUES ('admin', 'admin')
ON DUPLICATE KEY UPDATE usuario = usuario;
