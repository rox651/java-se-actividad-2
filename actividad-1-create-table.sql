-- Sabor Paisa

DROP TABLE IF EXISTS purchase_item CASCADE;
DROP TABLE IF EXISTS purchase CASCADE;
DROP TABLE IF EXISTS order_item CASCADE;
DROP TABLE IF EXISTS "order" CASCADE;
DROP TABLE IF EXISTS customer CASCADE;
DROP TABLE IF EXISTS menu_item_ingredient CASCADE;
DROP TABLE IF EXISTS provider_ingredient CASCADE;
DROP TABLE IF EXISTS provider CASCADE;
DROP TABLE IF EXISTS ingredient CASCADE;
DROP TABLE IF EXISTS menu_item CASCADE;
DROP TABLE IF EXISTS categoria_menu CASCADE;
DROP TABLE IF EXISTS local CASCADE;

CREATE TABLE local (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  direccion TEXT NOT NULL,
  telefono VARCHAR(50),
  gerente VARCHAR(150),
  hora_apertura TIME NOT NULL,
  hora_cierre TIME NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Entidad: Categoría de menú (entrada, postre, bebida, etc.)
CREATE TABLE categoria_menu (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE
);

-- Entidad: Item de menú (platos y bebidas)
CREATE TABLE menu_item (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(200) NOT NULL,
  categoria_id INTEGER REFERENCES categoria_menu(id),
  precio_base NUMERIC(10,2) NOT NULL,
  descripcion TEXT,
  disponible BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Entidad: Ingrediente (inventario)
CREATE TABLE ingredient (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL UNIQUE,
  unidad_medida VARCHAR(30) NOT NULL,
  cantidad_inventario NUMERIC(12,3) DEFAULT 0,
  cantidad_minima NUMERIC(12,3) DEFAULT 0,
  cantidad_maxima NUMERIC(12,3),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Entidad: Proveedor
CREATE TABLE provider (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(200) NOT NULL,
  nit VARCHAR(50),
  telefono VARCHAR(50),
  direccion TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Relación N:M proveedor-ingrediente
CREATE TABLE provider_ingredient (
  provider_id INTEGER REFERENCES provider(id) ON DELETE CASCADE,
  ingredient_id INTEGER REFERENCES ingredient(id) ON DELETE CASCADE,
  precio_unitario NUMERIC(12,4),
  unidad_medida VARCHAR(30),
  PRIMARY KEY (provider_id, ingredient_id)
);

-- Relación ingredientes por item del menú
CREATE TABLE menu_item_ingredient (
  menu_item_id INTEGER REFERENCES menu_item(id) ON DELETE CASCADE,
  ingredient_id INTEGER REFERENCES ingredient(id) ON DELETE RESTRICT,
  cantidad_requerida NUMERIC(12,4) NOT NULL,
  unidad_medida VARCHAR(30),
  PRIMARY KEY (menu_item_id, ingredient_id)
);

-- Entidad: Cliente
CREATE TABLE customer (
  id SERIAL PRIMARY KEY,
  nombre_completo VARCHAR(200) NOT NULL,
  documento_identidad VARCHAR(50),
  correo VARCHAR(200),
  telefono VARCHAR(50),
  es_frecuente BOOLEAN DEFAULT FALSE,
  fecha_inscripcion DATE,
  puntos_acumulados INTEGER DEFAULT 0,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Entidad: Pedido
CREATE TABLE "order" (
  id SERIAL PRIMARY KEY,
  customer_id INTEGER REFERENCES customer(id),
  fecha_hora TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  local_id INTEGER REFERENCES local(id) NOT NULL,
  valor_total NUMERIC(12,2) NOT NULL,
  tipo_pago VARCHAR(50) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Entidad: Detalle del pedido
CREATE TABLE order_item (
  id SERIAL PRIMARY KEY,
  order_id INTEGER REFERENCES "order"(id) ON DELETE CASCADE,
  menu_item_id INTEGER REFERENCES menu_item(id) ON DELETE RESTRICT,
  cantidad NUMERIC(10,3) NOT NULL,
  precio_unitario NUMERIC(12,2) NOT NULL,
  subtotal NUMERIC(12,2) NOT NULL
);

-- Entidad: Compra a proveedor
CREATE TABLE purchase (
  id SERIAL PRIMARY KEY,
  provider_id INTEGER REFERENCES provider(id) NOT NULL,
  local_id INTEGER REFERENCES local(id) NOT NULL,
  fecha_compra TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  valor_total NUMERIC(12,2) NOT NULL,
  tipo_pago VARCHAR(50) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Entidad: Detalle de compra
CREATE TABLE purchase_item (
  id SERIAL PRIMARY KEY,
  purchase_id INTEGER REFERENCES purchase(id) ON DELETE CASCADE,
  ingredient_id INTEGER REFERENCES ingredient(id) ON DELETE RESTRICT,
  cantidad NUMERIC(12,4) NOT NULL,
  precio_unitario NUMERIC(12,4) NOT NULL,
  subtotal NUMERIC(14,4) GENERATED ALWAYS AS (cantidad * precio_unitario) STORED
);


-- Local
INSERT INTO local (nombre, direccion, telefono, gerente, hora_apertura, hora_cierre)
VALUES
('Sabor Paisa Centro', 'Calle 10 #45-22', '3001112233', 'Carlos Gómez', '08:00', '22:00'),
('Sabor Paisa Norte', 'Av 7 #120-15', '3002223344', 'Ana Restrepo', '08:00', '22:00'),
('Sabor Paisa Sur', 'Cra 50 #5-18', '3003334455', 'Jorge Ruiz', '09:00', '21:00'),
('Sabor Paisa Envigado', 'Cra 32 #26-12', '3004445566', 'Luisa Pérez', '08:30', '22:00'),
('Sabor Paisa Laureles', 'Circular 75 #38-21', '3005556677', 'Marta López', '08:00', '23:00');

-- Categoría menú
INSERT INTO categoria_menu (nombre) VALUES
('Entrada'), ('Plato Fuerte'), ('Postre'), ('Bebida'), ('Acompañamiento');

-- Menu items
INSERT INTO menu_item (nombre, categoria_id, precio_base, descripcion)
VALUES
('Bandeja Paisa', 2, 35000, 'Plato tradicional paisa'),
('Arepa con queso', 1, 6000, 'Arepa paisa con queso campesino'),
('Jugo de Mora', 4, 8000, 'Jugo natural'),
('Chicharrón', 5, 12000, 'Chicharrón crocante'),
('Natilla', 3, 7000, 'Postre tradicional navideño');

-- Ingredientes
INSERT INTO ingredient (nombre, unidad_medida, cantidad_inventario, cantidad_minima, cantidad_maxima)
VALUES
('Fríjoles', 'kg', 30, 5, 100),
('Arroz', 'kg', 50, 10, 200),
('Carne Molida', 'kg', 20, 5, 100),
('Queso', 'kg', 15, 2, 50),
('Aguacate', 'unidad', 40, 10, 200);

-- 🛒 Proveedores
INSERT INTO provider (nombre, nit, telefono, direccion)
VALUES
('Proveedor Antioqueño', '90012345-1', '3101112222', 'Medellín'),
('Campo Fresco', '90154321-2', '3102223333', 'Envigado'),
('La Vaquita', '90198765-3', '3103334444', 'Sabaneta'),
('Frutas del Valle', '90111223-4', '3104445555', 'Cali'),
('Don Granos', '90199887-5', '3105556666', 'Bogotá');

-- 📦 Proveedor - Ingrediente
INSERT INTO provider_ingredient VALUES
(1, 1, 5000, 'kg'),
(1, 2, 3000, 'kg'),
(2, 3, 12000, 'kg'),
(3, 4, 15000, 'kg'),
(4, 5, 2000, 'unidad');

-- 🍳 Menu item ingredientes
INSERT INTO menu_item_ingredient VALUES
(1, 1, 0.2, 'kg'),
(1, 2, 0.15, 'kg'),
(1, 3, 0.25, 'kg'),
(2, 4, 0.1, 'kg'),
(1, 5, 1, 'unidad');

-- 👤 Clientes
INSERT INTO customer (nombre_completo, documento_identidad, correo, telefono, es_frecuente, fecha_inscripcion)
VALUES
('Juan Pérez', '12345678', 'juan@gmail.com', '3006667777', TRUE, '2024-01-05'),
('Ana Torres', '98765432', 'ana@gmail.com', '3007778888', FALSE, '2024-02-10'),
('Carlos Díaz', '55566677', 'carlos@gmail.com', '3008889999', TRUE, '2024-03-01'),
('Laura Gómez', '44455566', 'laura@gmail.com', '3011112222', FALSE, '2024-03-14'),
('Pedro Ruiz', '22233344', 'pedro@gmail.com', '3022223333', TRUE, '2024-04-01');

-- 🧾 Pedidos
INSERT INTO "order" (customer_id, local_id, valor_total, tipo_pago)
VALUES
(1, 1, 35000, 'Efectivo'),
(2, 1, 14000, 'Tarjeta'),
(3, 2, 8000, 'Nequi'),
(4, 3, 47000, 'Daviplata'),
(5, 2, 6000, 'Tarjeta');

-- 🧾 Items del pedido
INSERT INTO order_item (order_id, menu_item_id, cantidad, precio_unitario, subtotal)
VALUES
(1, 1, 1, 35000, 35000),
(2, 2, 2, 6000, 12000),
(2, 4, 1, 2000, 2000),
(3, 3, 1, 8000, 8000),
(5, 2, 1, 6000, 6000);

-- 🛍️ Compras a proveedor
INSERT INTO purchase (provider_id, local_id, valor_total, tipo_pago)
VALUES
(1,1,500000,'Transferencia'),
(2,1,400000,'Efectivo'),
(3,2,350000,'Transferencia'),
(4,3,200000,'Nequi'),
(5,2,600000,'Efectivo');

-- 🛍️ Detalle compra
INSERT INTO purchase_item (purchase_id, ingredient_id, cantidad, precio_unitario)
VALUES
(1,1,20,5000),
(1,2,30,3000),
(2,3,10,12000),
(3,4,5,15000),
(4,5,50,2000);



-- ======== CONSULTAS =======



-- 1) Clientes frecuentes 
SELECT nombre_completo, telefono
FROM customer
WHERE es_frecuente = TRUE AND puntos_acumulados >= 0;

-- 2) Platos de categoría bebidas o entradas
SELECT nombre, precio_base
FROM menu_item
WHERE categoria_id = 4 OR categoria_id = 1;

-- 3) Ingredientes con inventario bajo 
SELECT nombre, cantidad_inventario
FROM ingredient
WHERE cantidad_inventario <= cantidad_minima;

-- 4) Platos con precio mayor a 10.000
SELECT nombre, precio_base
FROM menu_item
WHERE precio_base > 10000;

-- 5) Búsqueda (clientes que contienen 'a')
SELECT nombre_completo
FROM customer
WHERE nombre_completo LIKE '%a%';



-- SUBCONSULTA: Cliente que más dinero ha gastado
SELECT nombre_completo
FROM customer
WHERE id = (
    SELECT customer_id
    FROM "order"
    GROUP BY customer_id
    ORDER BY SUM(valor_total) DESC
    LIMIT 1
);


-- VISTA
CREATE VIEW vw_ventas_por_local AS
SELECT l.nombre AS local,
       SUM(o.valor_total) AS total_ventas
FROM local l
JOIN "order" o ON o.local_id = l.id
GROUP BY l.nombre;



SELECT * FROM vw_ventas_por_local;

