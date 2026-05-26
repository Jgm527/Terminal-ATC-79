-- init.sql — ATC-79 Database Schema + Airport Data

CREATE DATABASE atc79;

\c atc79;

-- ============================================================
-- 1. JUGADORES
-- ============================================================
CREATE TABLE IF NOT EXISTS players (
    player_id      SERIAL PRIMARY KEY,
    alias          VARCHAR(30) UNIQUE NOT NULL,
    password_hash  VARCHAR(64) NOT NULL
);

-- ============================================================
-- 2. AEROPUERTOS (cabecera + perfil de spawn)
-- ============================================================
CREATE TABLE IF NOT EXISTS airports (
    airport_code         VARCHAR(4) PRIMARY KEY,
    name                 VARCHAR(100) NOT NULL,
    min_vectoring_alt    INT NOT NULL,
    difficulty_multiplier DOUBLE PRECISION DEFAULT 1.0,
    base_spawn_ticks     INT NOT NULL,
    spawn_variation      DOUBLE PRECISION NOT NULL,
    max_concurrent       INT NOT NULL,
    burst_min            INT NOT NULL,
    burst_max            INT NOT NULL,
    min_spawn_altitude   INT NOT NULL,
    max_spawn_altitude   INT NOT NULL
);

-- ============================================================
-- 3. PISTAS
-- ============================================================
CREATE TABLE IF NOT EXISTS runways (
    runway_id    SERIAL PRIMARY KEY,
    airport_code VARCHAR(4) NOT NULL REFERENCES airports(airport_code),
    runway_code  VARCHAR(5) NOT NULL,
    start_x      DOUBLE PRECISION NOT NULL,
    start_y      DOUBLE PRECISION NOT NULL,
    end_x        DOUBLE PRECISION NOT NULL,
    end_y        DOUBLE PRECISION NOT NULL,
    has_ils      BOOLEAN DEFAULT FALSE
);

-- ============================================================
-- 4. PUNTOS DE ESPERA
-- ============================================================
CREATE TABLE IF NOT EXISTS holding_points (
    point_id     SERIAL PRIMARY KEY,
    airport_code VARCHAR(4) NOT NULL REFERENCES airports(airport_code),
    point_code   VARCHAR(5) NOT NULL,
    pos_x        DOUBLE PRECISION NOT NULL,
    pos_y        DOUBLE PRECISION NOT NULL,
    radius_nm    DOUBLE PRECISION DEFAULT 1.0
);

-- ============================================================
-- 5. RUTAS DE ENTRADA (STARs)
-- ============================================================
CREATE TABLE IF NOT EXISTS entry_routes (
    route_id     SERIAL PRIMARY KEY,
    airport_code VARCHAR(4) NOT NULL REFERENCES airports(airport_code),
    route_name   VARCHAR(20) NOT NULL,
    heading      INT NOT NULL
);

-- ============================================================
-- 6. PUNTOS DE SPAWN DENTRO DE CADA RUTA
-- ============================================================
CREATE TABLE IF NOT EXISTS entry_route_spawns (
    spawn_id SERIAL PRIMARY KEY,
    route_id INT NOT NULL REFERENCES entry_routes(route_id),
    pos_x    DOUBLE PRECISION NOT NULL,
    pos_y    DOUBLE PRECISION NOT NULL
);

-- ============================================================
-- 7. PESOS DE MODELOS POR AEROPUERTO
-- ============================================================
CREATE TABLE IF NOT EXISTS airport_model_weights (
    airport_code VARCHAR(4) NOT NULL REFERENCES airports(airport_code),
    model_id     VARCHAR(10) NOT NULL,
    weight       INT NOT NULL,
    PRIMARY KEY (airport_code, model_id)
);

-- ============================================================
-- 8. PARTIDAS COMPLETADAS
-- ============================================================
CREATE TABLE IF NOT EXISTS game_sessions (
    session_id          SERIAL PRIMARY KEY,
    player_id           INT NOT NULL REFERENCES players(player_id),
    airport_code        VARCHAR(4) NOT NULL REFERENCES airports(airport_code),
    score_total         INT DEFAULT 0,
    successful_landings INT DEFAULT 0,
    streak_max          INT DEFAULT 0,
    duration_seconds    INT DEFAULT 0,
    game_over_cause     VARCHAR(30),
    completed_at        TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_leaderboard ON game_sessions(airport_code, score_total DESC);
CREATE INDEX IF NOT EXISTS idx_sessions_player ON game_sessions(player_id, completed_at DESC);
CREATE INDEX IF NOT EXISTS idx_runways_airport ON runways(airport_code);
CREATE INDEX IF NOT EXISTS idx_holdings_airport ON holding_points(airport_code);
CREATE INDEX IF NOT EXISTS idx_routes_airport ON entry_routes(airport_code);
CREATE INDEX IF NOT EXISTS idx_spawns_route ON entry_route_spawns(route_id);

-- ============================================================
-- DATOS: AEROPUERTOS + PERFILES DE SPAWN
-- ============================================================
INSERT INTO airports (airport_code, name, min_vectoring_alt, difficulty_multiplier,
    base_spawn_ticks, spawn_variation, max_concurrent, burst_min, burst_max,
    min_spawn_altitude, max_spawn_altitude) VALUES

('LEAL', 'Alicante-Elche',            2000, 1.0, 90, 0.20, 4,  1, 2, 3000,  6000),
('LEBL', 'Barcelona-El Prat',         2500, 1.3, 80, 0.20, 5,  1, 2, 3500,  8000),
('KLAX', 'Los Angeles International', 3000, 1.6, 70, 0.25, 6,  2, 3, 4000, 10000),
('EGLL', 'London Heathrow',           3000, 2.0, 62, 0.25, 7,  2, 3, 4000, 11000),
('GCXO', 'Tenerife Norte',            3500, 2.5, 56, 0.20, 8,  2, 3, 2500,  6000),
('BIKF', 'Keflavik',                  2500, 3.0, 52, 0.20, 9,  2, 3, 3000,  8000),
('KJFK', 'John F. Kennedy International', 3000, 3.5, 50, 0.25, 10, 3, 4, 4000, 12000)
ON CONFLICT (airport_code) DO NOTHING;

-- ============================================================
-- DATOS: PISTAS
-- ============================================================
INSERT INTO runways (airport_code, runway_code, start_x, start_y, end_x, end_y, has_ils) VALUES

-- LEAL (2 pistas: 10/28)
('LEAL', '10',  0.0,  0.0,  1.6,  0.5, true),
('LEAL', '28',  1.6,  0.5,  0.0,  0.0, false),

-- LEBL (4 pistas: 07L/25R, 02/20)
('LEBL', '07L', -0.8, -1.2,  2.2,  1.0, true),
('LEBL', '25R',  2.2,  1.0, -0.8, -1.2, true),
('LEBL', '02',   0.5, -0.8,  2.0,  0.6, false),
('LEBL', '20',   2.0,  0.6,  0.5, -0.8, false),

-- KLAX (6 pistas: 24L/06R, 24R/06L, 25L/07R)
('KLAX', '24L', -1.5, -0.6,  1.5,  0.6, true),
('KLAX', '06R',  1.5,  0.6, -1.5, -0.6, true),
('KLAX', '24R', -1.5, -0.2,  1.5,  1.0, true),
('KLAX', '06L',  1.5,  1.0, -1.5, -0.2, true),
('KLAX', '25L', -1.5,  0.2,  1.5,  1.4, true),
('KLAX', '07R',  1.5,  1.4, -1.5,  0.2, true),

-- EGLL (6 pistas: 09L/27R, 09R/27L, 23/05)
('EGLL', '09L', -1.2, -0.4,  1.8,  0.6, true),
('EGLL', '27R',  1.8,  0.6, -1.2, -0.4, true),
('EGLL', '09R', -1.2,  0.1,  1.8,  1.1, true),
('EGLL', '27L',  1.8,  1.1, -1.2,  0.1, true),
('EGLL', '23',   0.8, -1.0,  1.8,  0.5, false),
('EGLL', '05',   1.8,  0.5,  0.8, -1.0, false),

-- GCXO (2 pistas: 12/30)
('GCXO', '12',  0.0,  0.0,  1.2,  2.0, true),
('GCXO', '30',  1.2,  2.0,  0.0,  0.0, false),

-- BIKF (4 pistas: 11/29, 01/19)
('BIKF', '11', -0.8, -0.8,  1.2,  0.8, true),
('BIKF', '29',  1.2,  0.8, -0.8, -0.8, true),
('BIKF', '01',  0.0, -1.0,  0.0,  1.0, false),
('BIKF', '19',  0.0,  1.0,  0.0, -1.0, false),

-- KJFK (8 pistas: 04L/22R, 04R/22L, 13L/31R, 13R/31L)
('KJFK', '04L', -1.0, -0.8,  1.5,  0.7, true),
('KJFK', '22R',  1.5,  0.7, -1.0, -0.8, true),
('KJFK', '04R', -1.0, -0.4,  1.5,  1.1, true),
('KJFK', '22L',  1.5,  1.1, -1.0, -0.4, true),
('KJFK', '13L',  0.2, -1.0,  1.2,  1.5, true),
('KJFK', '31R',  1.2,  1.5,  0.2, -1.0, true),
('KJFK', '13R',  0.6, -1.0,  1.6,  1.5, true),
('KJFK', '31L',  1.6,  1.5,  0.6, -1.0, true);

-- ============================================================
-- DATOS: PUNTOS DE ESPERA
-- ============================================================
INSERT INTO holding_points (airport_code, point_code, pos_x, pos_y, radius_nm) VALUES
('LEAL', 'H1',  -5.0,  3.2, 1.0),
('LEBL', 'H1',  -6.0,  4.0, 1.0),
('LEBL', 'H2',   4.7, -5.4, 1.0),
('LEBL', 'H3',   1.7,  5.7, 1.0),
('KLAX', 'H1',  -4.0,  3.0, 1.0),
('KLAX', 'H2',   4.0, -2.0, 1.0),
('KLAX', 'H3',   0.0,  5.0, 1.0),
('EGLL', 'H1',  -4.5,  4.5, 1.0),
('EGLL', 'H2',   5.0, -6.0, 1.0),
('EGLL', 'H3',   0.0,  6.5, 1.0),
('EGLL', 'H4',  -2.9, -5.0, 1.0),
('GCXO', 'H1',   6.5,  8.0, 1.0),
('BIKF', 'H1',  -3.0,  4.7, 1.0),
('BIKF', 'H2',   3.6, -4.2, 1.0),
('KJFK', 'H1',  -3.7,  5.0, 1.0),
('KJFK', 'H2',   4.0, -4.5, 1.0),
('KJFK', 'H3',   0.6, 10.5, 1.0),
('KJFK', 'H4',  -4.7, -6.0, 1.0),
('KJFK', 'H5',   8.0,  8.5, 1.0);

-- ============================================================
-- DATOS: RUTAS DE ENTRADA + PUNTOS DE SPAWN
-- ============================================================

-- Las 4 rutas estandar (N, E, S, W) existen en TODOS los aeropuertos
-- con los mismos spawn points: 3 puntos a ~15 NM en cada direccion

-- LEAL
INSERT INTO entry_routes (airport_code, route_name, heading) VALUES
    ('LEAL', 'NORTH', 170), ('LEAL', 'EAST', 260), ('LEAL', 'SOUTH', 350), ('LEAL', 'WEST', 80);

INSERT INTO entry_route_spawns (route_id, pos_x, pos_y)
SELECT r.route_id, p.pos_x, p.pos_y FROM entry_routes r
JOIN (VALUES ('NORTH', -3, 15), ('NORTH', 0, 15), ('NORTH', 3, 15),
             ('EAST',  15, -3), ('EAST',  15, 0), ('EAST',  15, 3),
             ('SOUTH',  3, -15), ('SOUTH', 0, -15), ('SOUTH', -3, -15),
             ('WEST',  -15, 3), ('WEST',  -15, 0), ('WEST',  -15, -3))
    AS p(rname, pos_x, pos_y) ON r.route_name = p.rname
WHERE r.airport_code = 'LEAL';

-- LEBL
INSERT INTO entry_routes (airport_code, route_name, heading) VALUES
    ('LEBL', 'NORTH', 170), ('LEBL', 'EAST', 260), ('LEBL', 'SOUTH', 350), ('LEBL', 'WEST', 80);

INSERT INTO entry_route_spawns (route_id, pos_x, pos_y)
SELECT r.route_id, p.pos_x, p.pos_y FROM entry_routes r
JOIN (VALUES ('NORTH', -3, 15), ('NORTH', 0, 15), ('NORTH', 3, 15),
             ('EAST',  15, -3), ('EAST',  15, 0), ('EAST',  15, 3),
             ('SOUTH',  3, -15), ('SOUTH', 0, -15), ('SOUTH', -3, -15),
             ('WEST',  -15, 3), ('WEST',  -15, 0), ('WEST',  -15, -3))
    AS p(rname, pos_x, pos_y) ON r.route_name = p.rname
WHERE r.airport_code = 'LEBL';

-- KLAX
INSERT INTO entry_routes (airport_code, route_name, heading) VALUES
    ('KLAX', 'NORTH', 170), ('KLAX', 'EAST', 260), ('KLAX', 'SOUTH', 350), ('KLAX', 'WEST', 80);

INSERT INTO entry_route_spawns (route_id, pos_x, pos_y)
SELECT r.route_id, p.pos_x, p.pos_y FROM entry_routes r
JOIN (VALUES ('NORTH', -3, 15), ('NORTH', 0, 15), ('NORTH', 3, 15),
             ('EAST',  15, -3), ('EAST',  15, 0), ('EAST',  15, 3),
             ('SOUTH',  3, -15), ('SOUTH', 0, -15), ('SOUTH', -3, -15),
             ('WEST',  -15, 3), ('WEST',  -15, 0), ('WEST',  -15, -3))
    AS p(rname, pos_x, pos_y) ON r.route_name = p.rname
WHERE r.airport_code = 'KLAX';

-- EGLL
INSERT INTO entry_routes (airport_code, route_name, heading) VALUES
    ('EGLL', 'NORTH', 170), ('EGLL', 'EAST', 260), ('EGLL', 'SOUTH', 350), ('EGLL', 'WEST', 80);

INSERT INTO entry_route_spawns (route_id, pos_x, pos_y)
SELECT r.route_id, p.pos_x, p.pos_y FROM entry_routes r
JOIN (VALUES ('NORTH', -3, 15), ('NORTH', 0, 15), ('NORTH', 3, 15),
             ('EAST',  15, -3), ('EAST',  15, 0), ('EAST',  15, 3),
             ('SOUTH',  3, -15), ('SOUTH', 0, -15), ('SOUTH', -3, -15),
             ('WEST',  -15, 3), ('WEST',  -15, 0), ('WEST',  -15, -3))
    AS p(rname, pos_x, pos_y) ON r.route_name = p.rname
WHERE r.airport_code = 'EGLL';

-- GCXO
INSERT INTO entry_routes (airport_code, route_name, heading) VALUES
    ('GCXO', 'NORTH', 170), ('GCXO', 'EAST', 260), ('GCXO', 'SOUTH', 350), ('GCXO', 'WEST', 80);

INSERT INTO entry_route_spawns (route_id, pos_x, pos_y)
SELECT r.route_id, p.pos_x, p.pos_y FROM entry_routes r
JOIN (VALUES ('NORTH', -3, 15), ('NORTH', 0, 15), ('NORTH', 3, 15),
             ('EAST',  15, -3), ('EAST',  15, 0), ('EAST',  15, 3),
             ('SOUTH',  3, -15), ('SOUTH', 0, -15), ('SOUTH', -3, -15),
             ('WEST',  -15, 3), ('WEST',  -15, 0), ('WEST',  -15, -3))
    AS p(rname, pos_x, pos_y) ON r.route_name = p.rname
WHERE r.airport_code = 'GCXO';

-- BIKF
INSERT INTO entry_routes (airport_code, route_name, heading) VALUES
    ('BIKF', 'NORTH', 170), ('BIKF', 'EAST', 260), ('BIKF', 'SOUTH', 350), ('BIKF', 'WEST', 80);

INSERT INTO entry_route_spawns (route_id, pos_x, pos_y)
SELECT r.route_id, p.pos_x, p.pos_y FROM entry_routes r
JOIN (VALUES ('NORTH', -3, 15), ('NORTH', 0, 15), ('NORTH', 3, 15),
             ('EAST',  15, -3), ('EAST',  15, 0), ('EAST',  15, 3),
             ('SOUTH',  3, -15), ('SOUTH', 0, -15), ('SOUTH', -3, -15),
             ('WEST',  -15, 3), ('WEST',  -15, 0), ('WEST',  -15, -3))
    AS p(rname, pos_x, pos_y) ON r.route_name = p.rname
WHERE r.airport_code = 'BIKF';

-- KJFK
INSERT INTO entry_routes (airport_code, route_name, heading) VALUES
    ('KJFK', 'NORTH', 170), ('KJFK', 'EAST', 260), ('KJFK', 'SOUTH', 350), ('KJFK', 'WEST', 80);

INSERT INTO entry_route_spawns (route_id, pos_x, pos_y)
SELECT r.route_id, p.pos_x, p.pos_y FROM entry_routes r
JOIN (VALUES ('NORTH', -3, 15), ('NORTH', 0, 15), ('NORTH', 3, 15),
             ('EAST',  15, -3), ('EAST',  15, 0), ('EAST',  15, 3),
             ('SOUTH',  3, -15), ('SOUTH', 0, -15), ('SOUTH', -3, -15),
             ('WEST',  -15, 3), ('WEST',  -15, 0), ('WEST',  -15, -3))
    AS p(rname, pos_x, pos_y) ON r.route_name = p.rname
WHERE r.airport_code = 'KJFK';

-- Extra routes (solo algunos aeropuertos)
INSERT INTO entry_routes (airport_code, route_name, heading) VALUES
    ('LEBL', 'NORTHEAST', 225),
    ('KLAX', 'NORTHWEST', 135),
    ('EGLL', 'SOUTHEAST', 315),
    ('KJFK', 'NORTHEAST', 225),
    ('KJFK', 'SOUTHWEST',  45);

-- Spawns for extra routes
INSERT INTO entry_route_spawns (route_id, pos_x, pos_y)
SELECT r.route_id, p.pos_x, p.pos_y FROM entry_routes r
JOIN (VALUES
    ('NORTHEAST', 10, 10), ('NORTHEAST', 12, 12), ('NORTHEAST', 14, 14),
    ('NORTHWEST', -10, 10), ('NORTHWEST', -12, 12), ('NORTHWEST', -14, 14),
    ('SOUTHEAST', 10, -10), ('SOUTHEAST', 12, -12), ('SOUTHEAST', 14, -14),
    ('SOUTHWEST', -10, -10), ('SOUTHWEST', -12, -12), ('SOUTHWEST', -14, -14)
) AS p(rname, pos_x, pos_y) ON r.route_name = p.rname;

-- ============================================================
-- DATOS: PESOS DE MODELOS POR AEROPUERTO
-- ============================================================
INSERT INTO airport_model_weights (airport_code, model_id, weight) VALUES
-- LEAL
('LEAL', 'C172',  5), ('LEAL', 'PA28',  5), ('LEAL', 'F27', 20),
('LEAL', 'B727', 15), ('LEAL', 'B737', 20), ('LEAL', 'DC9', 15),
('LEAL', 'B747',  5), ('LEAL', 'CONC',  5), ('LEAL', 'DC10', 5),
('LEAL', 'CRJ',   5),
-- LEBL
('LEBL', 'C172',  3), ('LEBL', 'PA28',  3), ('LEBL', 'F27', 15),
('LEBL', 'B727', 15), ('LEBL', 'B737', 22), ('LEBL', 'DC9', 15),
('LEBL', 'B747', 12), ('LEBL', 'CONC',  5), ('LEBL', 'DC10', 5),
('LEBL', 'CRJ',   5),
-- KLAX
('KLAX', 'C172',  2), ('KLAX', 'PA28',  2), ('KLAX', 'F27',  8),
('KLAX', 'B727', 15), ('KLAX', 'B737', 25), ('KLAX', 'DC9', 12),
('KLAX', 'B747', 18), ('KLAX', 'CONC',  5), ('KLAX', 'DC10', 8),
('KLAX', 'CRJ',   5),
-- EGLL
('EGLL', 'C172',  1), ('EGLL', 'PA28',  1), ('EGLL', 'F27',  7),
('EGLL', 'B727', 15), ('EGLL', 'B737', 25), ('EGLL', 'DC9', 12),
('EGLL', 'B747', 20), ('EGLL', 'CONC',  8), ('EGLL', 'DC10', 8),
('EGLL', 'CRJ',   3),
-- GCXO
('GCXO', 'C172', 10), ('GCXO', 'PA28', 10), ('GCXO', 'F27', 25),
('GCXO', 'B727', 10), ('GCXO', 'B737', 15), ('GCXO', 'DC9', 10),
('GCXO', 'B747',  5), ('GCXO', 'CONC',  5), ('GCXO', 'DC10', 5),
('GCXO', 'CRJ',   5),
-- BIKF
('BIKF', 'C172',  5), ('BIKF', 'PA28',  5), ('BIKF', 'F27', 15),
('BIKF', 'B727', 15), ('BIKF', 'B737', 20), ('BIKF', 'DC9', 12),
('BIKF', 'B747', 12), ('BIKF', 'CONC',  8), ('BIKF', 'DC10', 5),
('BIKF', 'CRJ',   3),
-- KJFK
('KJFK', 'C172',  1), ('KJFK', 'PA28',  1), ('KJFK', 'F27',  3),
('KJFK', 'B727', 12), ('KJFK', 'B737', 25), ('KJFK', 'DC9', 10),
('KJFK', 'B747', 22), ('KJFK', 'CONC', 10), ('KJFK', 'DC10', 10),
('KJFK', 'CRJ',   6);
