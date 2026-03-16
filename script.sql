CREATE DATABASE IF NOT EXISTS sharicklogitrack_db;
USE sharicklogitrack_db;

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

USE sharicklogitrack_db;

-- =========================================
-- INSERTS PARA LOGITRACK
-- ORDEN CORRECTO POR LLAVES FORÁNEAS
-- usuarios -> bodegas -> productos -> movimientos -> movimiento_detalles -> auditorias
-- =========================================

-- -----------------------------------------
-- 1. USUARIOS
-- -----------------------------------------
INSERT INTO usuarios (id, nombre, documento, username, password, rol) VALUES
(1, 'Administrador General', '1001001001', 'admin', 'admin123', 'ADMIN'),
(2, 'Laura Gomez', '1098001002', 'lgomez', 'laura123', 'EMPLEADO'),
(3, 'Carlos Peña', '1098001003', 'cpena', 'carlos123', 'EMPLEADO'),
(4, 'Andrea Rojas', '1098001004', 'arojas', 'andrea123', 'EMPLEADO'),
(5, 'Miguel Torres', '1098001005', 'mtorres', 'miguel123', 'EMPLEADO');

-- -----------------------------------------
-- 2. BODEGAS
-- -----------------------------------------
INSERT INTO bodegas (id, nombre, ubicacion, capacidad, encargado_id) VALUES
(1, 'Bodega Central', 'Bucaramanga', 500, 2),
(2, 'Bodega Norte', 'Floridablanca', 350, 3),
(3, 'Bodega Sur', 'Giron', 280, 4);

-- -----------------------------------------
-- 3. PRODUCTOS
-- -----------------------------------------
INSERT INTO productos (id, nombre, categoria, precio, stock, bodega_id) VALUES
(1, 'Laptop Lenovo ThinkPad E14', 'Portatiles', 3200000.00, 15, 1),
(2, 'Mouse Logitech M185', 'Accesorios', 85000.00, 40, 1),
(3, 'Teclado Redragon Kumara', 'Perifericos', 210000.00, 18, 1),
(4, 'Monitor Samsung 24 Pulgadas', 'Monitores', 780000.00, 10, 1),
(5, 'Impresora HP Laser 107a', 'Impresoras', 950000.00, 6, 2),
(6, 'Disco SSD Kingston 480GB', 'Almacenamiento', 180000.00, 25, 2),
(7, 'Router TP-Link Archer C6', 'Redes', 230000.00, 12, 2),
(8, 'UPS Forza 1000VA', 'Energia', 420000.00, 8, 3),
(9, 'Camara Hikvision 1080p', 'Seguridad', 260000.00, 12, 3),
(10, 'Lector Codigo Zebra DS2208', 'Lectores', 690000.00, 5, 3);

-- -----------------------------------------
-- 4. MOVIMIENTOS
-- -----------------------------------------
INSERT INTO movimientos (id, fecha, tipo_movimiento, usuario_id, bodega_origen_id, bodega_destino_id) VALUES
(1, '2026-03-01 08:30:00', 'ENTRADA', 1, NULL, 1),
(2, '2026-03-02 10:15:00', 'ENTRADA', 1, NULL, 2),
(3, '2026-03-03 14:00:00', 'SALIDA', 2, 1, NULL),
(4, '2026-03-04 09:20:00', 'TRANSFERENCIA', 1, 1, 2),
(5, '2026-03-05 16:45:00', 'ENTRADA', 1, NULL, 3),
(6, '2026-03-06 11:10:00', 'SALIDA', 4, 3, NULL),
(7, '2026-03-07 13:30:00', 'SALIDA', 3, 2, NULL);

-- -----------------------------------------
-- 5. DETALLES DE MOVIMIENTO
-- -----------------------------------------
INSERT INTO movimiento_detalles (id, movimiento_id, producto_id, cantidad) VALUES
(1, 1, 1, 15),
(2, 1, 2, 50),
(3, 1, 3, 18),
(4, 1, 4, 10),
(5, 1, 7, 12),
(6, 2, 5, 6),
(7, 2, 6, 30),
(8, 3, 2, 10),
(9, 4, 7, 12),
(10, 5, 8, 8),
(11, 5, 9, 14),
(12, 5, 10, 5),
(13, 6, 9, 2),
(14, 7, 6, 5);

-- -----------------------------------------
-- 6. AUDITORIAS
-- -----------------------------------------
INSERT INTO auditorias (id, entidad, operacion, fecha, usuario_id, valor_anterior, valor_nuevo) VALUES
(1, 'usuarios', 'INSERT', '2026-03-01 08:00:00', 1, NULL,
'{"id":1,"nombre":"Administrador General","username":"admin","rol":"ADMIN"}'),

(2, 'bodegas', 'INSERT', '2026-03-01 08:10:00', 1, NULL,
'{"id":1,"nombre":"Bodega Central","ubicacion":"Bucaramanga","capacidad":500,"encargado_id":2}'),

(3, 'productos', 'INSERT', '2026-03-01 08:40:00', 1, NULL,
'{"id":1,"nombre":"Laptop Lenovo ThinkPad E14","stock":15,"bodega_id":1}'),

(4, 'movimientos', 'INSERT', '2026-03-03 14:00:00', 2, NULL,
'{"id":3,"tipo_movimiento":"SALIDA","usuario_id":2,"bodega_origen_id":1}'),

(5, 'productos', 'UPDATE', '2026-03-03 14:05:00', 2,
'{"id":2,"nombre":"Mouse Logitech M185","stock":50}',
'{"id":2,"nombre":"Mouse Logitech M185","stock":40}'),

(6, 'movimientos', 'INSERT', '2026-03-04 09:20:00', 1, NULL,
'{"id":4,"tipo_movimiento":"TRANSFERENCIA","bodega_origen_id":1,"bodega_destino_id":2}'),

(7, 'productos', 'UPDATE', '2026-03-06 11:15:00', 4,
'{"id":9,"nombre":"Camara Hikvision 1080p","stock":14}',
'{"id":9,"nombre":"Camara Hikvision 1080p","stock":12}');

-- -----------------------------------------
-- OPCIONAL: AJUSTAR AUTO_INCREMENT
-- -----------------------------------------
ALTER TABLE usuarios AUTO_INCREMENT = 6;
ALTER TABLE bodegas AUTO_INCREMENT = 4;
ALTER TABLE productos AUTO_INCREMENT = 11;
ALTER TABLE movimientos AUTO_INCREMENT = 8;
ALTER TABLE movimiento_detalles AUTO_INCREMENT = 15;
ALTER TABLE auditorias AUTO_INCREMENT = 8;