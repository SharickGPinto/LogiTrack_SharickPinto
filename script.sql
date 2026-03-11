CREATE DATABASE IF NOT EXISTS logitrack_db;
USE logitrack_db;

-- 1. Tabla de Usuarios (Con Enums de Rol y campos UNIQUE)
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    documento VARCHAR(20) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ADMIN', 'EMPLEADO') NOT NULL
);

-- 2. Tabla de Bodegas (El edificio)
CREATE TABLE bodegas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    ubicacion VARCHAR(50) NOT NULL,
    capacidad INT NOT NULL,
    encargado_id BIGINT NOT NULL,
    FOREIGN KEY (encargado_id) REFERENCES usuarios(id)
);

-- 3. Tabla de Productos (El carnet que está en el edificio)
CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    categoria VARCHAR(100),
    precio DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0 NOT NULL,
    bodega_id BIGINT NOT NULL,
    FOREIGN KEY (bodega_id) REFERENCES bodegas(id)
);

-- 4. Tabla de Movimientos (Cabecera de la transacción)
CREATE TABLE movimientos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME NOT NULL,
    tipo_movimiento ENUM('ENTRADA', 'SALIDA', 'TRANSFERENCIA') NOT NULL,
    usuario_id BIGINT NOT NULL,
    bodega_origen_id BIGINT NULL,  -- NULL si es ENTRADA
    bodega_destino_id BIGINT NULL, -- NULL si es SALIDA
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (bodega_origen_id) REFERENCES bodegas(id),
    FOREIGN KEY (bodega_destino_id) REFERENCES bodegas(id)
);

-- 5. Tabla de Detalles de Movimiento (Los productos que se mueven)
CREATE TABLE movimiento_detalles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movimiento_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    FOREIGN KEY (movimiento_id) REFERENCES movimientos(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- 6. Tabla de Auditorías (Registro automático de cambios)
CREATE TABLE auditorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entidad VARCHAR(50) NOT NULL,
    operacion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha DATETIME NOT NULL,
    usuario_id BIGINT NULL,
    valor_anterior TEXT, -- TEXT para soportar JSONs largos
    valor_nuevo TEXT,    -- TEXT para soportar JSONs largos
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);