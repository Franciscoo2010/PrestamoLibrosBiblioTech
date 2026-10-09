-- =============================================================================
-- SCRIPT DDL: Estructura de Base de Datos - BiblioTech
-- =============================================================================

DROP DATABASE IF EXISTS bibliotech_in4cm;
CREATE DATABASE IF NOT EXISTS bibliotech_in4cm;
USE bibliotech_in4cm;

-- 1. TABLAS PRINCIPALES
CREATE TABLE usuarios (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nombre_completo VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ADMIN', 'BIBLIOTECARIO', 'LECTOR') NOT NULL DEFAULT 'LECTOR',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_email_usuario UNIQUE (email)
);

CREATE TABLE libros (
    id_libro INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    anio_publicacion INT,
    CONSTRAINT uq_isbn_libro UNIQUE (isbn),
    CONSTRAINT ck_stock_libro CHECK (stock >= 0)
);

CREATE TABLE prestamos (
    id_prestamo INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    id_libro INT NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion_esperada DATE NOT NULL,
    fecha_devolucion_real DATE NULL,
    estado ENUM('ACTIVO', 'DEVUELTO', 'ATRASADO') NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT ck_fechas_prestamo CHECK (fecha_devolucion_esperada >= fecha_prestamo)
);

-- 2. LLAVES FORÁNEAS (RESTRICCIONES)
ALTER TABLE prestamos
ADD CONSTRAINT fk_prestamo_usuario FOREIGN KEY (id_usuario)
    REFERENCES usuarios(id_usuario)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
ADD CONSTRAINT fk_prestamo_libro FOREIGN KEY (id_libro)
    REFERENCES libros(id_libro)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- 3. ÍNDICES DE RENDIMIENTO
CREATE INDEX idx_usuario_email ON usuarios(email);
CREATE INDEX idx_libro_titulo ON libros(titulo);
CREATE INDEX idx_prestamo_usuario ON prestamos(id_usuario);
CREATE INDEX idx_prestamo_libro ON prestamos(id_libro);
CREATE INDEX idx_prestamo_estado ON prestamos(estado);

-- 4. STORED PROCEDURES (CRUD)
DELIMITER $$

-- --- CRUD: USUARIOS ---
CREATE PROCEDURE sp_insertarusuario(
    IN _nombre_completo VARCHAR(100),
    IN _email VARCHAR(100),
    IN _password VARCHAR(255),
    IN _rol VARCHAR(20)
)
BEGIN
    INSERT INTO usuarios(nombre_completo, email, password, rol)
    VALUES (_nombre_completo, _email, _password, _rol);
END $$

CREATE PROCEDURE sp_actualizarusuario(
    IN _id_usuario INT,
    IN _nombre_completo VARCHAR(100),
    IN _email VARCHAR(100),
    IN _rol VARCHAR(20)
)
BEGIN
    UPDATE usuarios
    SET nombre_completo = _nombre_completo,
        email = _email,
        rol = _rol
    WHERE id_usuario = _id_usuario;
END $$

CREATE PROCEDURE sp_eliminarusuario(
    IN _id_usuario INT
)
BEGIN
    DELETE FROM usuarios WHERE id_usuario = _id_usuario;
END $$

-- --- CRUD: LIBROS ---
CREATE PROCEDURE sp_insertarlibro(
    IN _titulo VARCHAR(150),
    IN _autor VARCHAR(100),
    IN _isbn VARCHAR(20),
    IN _stock INT,
    IN _anio_publicacion INT
)
BEGIN
    INSERT INTO libros(titulo, autor, isbn, stock, anio_publicacion)
    VALUES (_titulo, _autor, _isbn, _stock, _anio_publicacion);
END $$

CREATE PROCEDURE sp_actualizarlibro(
    IN _id_libro INT,
    IN _titulo VARCHAR(150),
    IN _autor VARCHAR(100),
    IN _isbn VARCHAR(20),
    IN _stock INT,
    IN _anio_publicacion INT
)
BEGIN
    UPDATE libros
    SET titulo = _titulo,
        autor = _autor,
        isbn = _isbn,
        stock = _stock,
        anio_publicacion = _anio_publicacion
    WHERE id_libro = _id_libro;
END $$

CREATE PROCEDURE sp_eliminarlibro(
    IN _id_libro INT
)
BEGIN
    DELETE FROM libros WHERE id_libro = _id_libro;
END $$

-- --- CRUD: PRÉSTAMOS ---
CREATE PROCEDURE sp_insertarprestamo(
    IN _id_usuario INT,
    IN _id_libro INT,
    IN _fecha_prestamo DATE,
    IN _fecha_devolucion_esperada DATE,
    IN _estado VARCHAR(20)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;
    UPDATE libros SET stock = stock - 1
    WHERE id_libro = _id_libro AND stock > 0;

    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El libro no existe o no tiene ejemplares disponibles.';
    END IF;

    INSERT INTO prestamos(id_usuario, id_libro, fecha_prestamo, fecha_devolucion_esperada, estado)
    VALUES (_id_usuario, _id_libro, _fecha_prestamo, _fecha_devolucion_esperada, _estado);
    COMMIT;
END $$

CREATE PROCEDURE sp_actualizarprestamo(
    IN _id_prestamo INT,
    IN _id_usuario INT,
    IN _id_libro INT,
    IN _fecha_prestamo DATE,
    IN _fecha_devolucion_esperada DATE,
    IN _fecha_devolucion_real DATE,
    IN _estado VARCHAR(20)
)
BEGIN
    DECLARE _estado_anterior VARCHAR(20);
    DECLARE _libro_anterior INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;
    SELECT estado, id_libro INTO _estado_anterior, _libro_anterior
    FROM prestamos WHERE id_prestamo = _id_prestamo FOR UPDATE;

    IF _estado_anterior IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El préstamo no existe.';
    END IF;

    UPDATE prestamos
    SET
        fecha_devolucion_real = _fecha_devolucion_real,
        estado = _estado
    WHERE id_prestamo = _id_prestamo;

    IF _estado_anterior <> 'DEVUELTO' AND _estado = 'DEVUELTO' THEN
        UPDATE libros SET stock = stock + 1 WHERE id_libro = _libro_anterior;
    END IF;
    COMMIT;
END $$

CREATE PROCEDURE sp_eliminarprestamo(
    IN _id_prestamo INT
)
BEGIN
    DECLARE _libro INT;
    DECLARE _estado VARCHAR(20);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;
    SELECT id_libro, estado INTO _libro, _estado
    FROM prestamos WHERE id_prestamo = _id_prestamo FOR UPDATE;

    IF _estado IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El préstamo no existe.';
    END IF;

    DELETE FROM prestamos WHERE id_prestamo = _id_prestamo;
    IF _estado <> 'DEVUELTO' THEN
        UPDATE libros SET stock = stock + 1 WHERE id_libro = _libro;
    END IF;
    COMMIT;
END $$

DELIMITER ;

-- 5. VISTAS DE CONSULTA
CREATE OR REPLACE VIEW vw_lista_usuarios AS
SELECT 
    id_usuario AS 'id usuario',
    nombre_completo AS 'nombre completo',
    email AS 'correo electrónico',
    rol AS 'rol',
    fecha_creacion AS 'fecha de creación'
FROM usuarios;

CREATE OR REPLACE VIEW vw_lista_libros AS
SELECT 
    id_libro AS 'id libro',
    titulo AS 'título',
    autor AS 'autor',
    isbn AS 'isbn',
    stock AS 'stock disponible',
    anio_publicacion AS 'año'
FROM libros;

CREATE OR REPLACE VIEW vw_lista_prestamos AS
SELECT 
    p.id_prestamo AS 'no. préstamo',
    u.nombre_completo AS 'usuario',
    l.titulo AS 'libro',
    p.fecha_prestamo AS 'fecha préstamo',
    p.fecha_devolucion_esperada AS 'devolución esperada',
    p.fecha_devolucion_real AS 'devolución real',
    p.estado AS 'estado'
FROM prestamos p
INNER JOIN usuarios u ON p.id_usuario = u.id_usuario
INNER JOIN libros l ON p.id_libro = l.id_libro;
