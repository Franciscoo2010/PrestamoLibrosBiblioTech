-- =============================================================================
-- SCRIPT DML (SEEDERS): Datos de Prueba - BiblioTech (Seguro para reejecutar)
-- =============================================================================

USE bibliotech_in4cm;

-- Limpiar tablas de forma segura antes de insertar los seeders
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE prestamos;
TRUNCATE TABLE libros;
TRUNCATE TABLE usuarios;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Inserción de Usuarios (5 registros con roles variados)
INSERT INTO usuarios (nombre_completo, email, password, rol) VALUES
('Carlos Administrador', 'admin@bibliotech.com', '123456', 'ADMIN'),
('Maria Bibliotecaria', 'maria@bibliotech.com', '123456', 'BIBLIOTECARIO'),
('Juan Perez', 'juan.perez@gmail.com', '123456', 'LECTOR'),
('Ana Gomez', 'ana.gomez@gmail.com', '123456', 'LECTOR'),
('Luis Martinez', 'luis.martinez@gmail.com', '123456', 'LECTOR');

-- 2. Inserción de Libros (5 registros)
INSERT INTO libros (titulo, autor, isbn, stock, anio_publicacion) VALUES
('Clean Code', 'Robert C. Martin', '978-0132350884', 5, 2008),
('Design Patterns', 'Erich Gamma', '978-0201633610', 3, 1994),
('Java: How to Program', 'Paul Deitel', '978-0134743356', 4, 2017),
('Database System Concepts', 'Abraham Silberschatz', '978-0078022158', 2, 2019),
('Introduction to Algorithms', 'Thomas H. Cormen', '978-0262033848', 3, 2009);

-- 3. Inserción de Préstamos iniciales (5 registros para asegurar la rúbrica de volumen)
INSERT INTO prestamos (id_usuario, id_libro, fecha_prestamo, fecha_devolucion_esperada, estado) VALUES
(3, 1, '2026-10-01', '2026-10-15', 'ACTIVO'),
(4, 2, '2026-10-03', '2026-10-17', 'ACTIVO'),
(5, 3, '2026-09-10', '2026-09-24', 'DEVUELTO'),
(3, 4, '2026-09-15', '2026-09-29', 'ATRASADO'),
(4, 5, '2026-10-02', '2026-10-16', 'ACTIVO');

-- Reflejar en el inventario los préstamos que todavía no han sido devueltos
UPDATE libros l
SET l.stock = l.stock - (
    SELECT COUNT(*)
    FROM prestamos p
    WHERE p.id_libro = l.id_libro AND p.estado <> 'DEVUELTO'
);

-- 4. Consultas de validación mediante Vistas
SELECT * FROM vw_lista_usuarios;
SELECT * FROM vw_lista_libros;
SELECT * FROM vw_lista_prestamos;
