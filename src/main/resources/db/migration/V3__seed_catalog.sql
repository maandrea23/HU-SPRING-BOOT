INSERT INTO venues (nombre, direccion, capacidad, ciudad) VALUES
    ('Auditorio Andino', 'Calle 72 #10-34', 450, 'Bogotá'),
    ('Teatro del Río', 'Carrera 5 #28-12', 700, 'Bogotá'),
    ('Centro Cultural Norte', 'Avenida 6N #24-60', 300, 'Cali'),
    ('Plaza de la Música', 'Calle 48 #45-80', 1200, 'Medellín'),
    ('Foro Caribe', 'Carrera 53 #76-210', 500, 'Barranquilla'),
    ('Casa del Mar', 'Bocagrande 8-31', 250, 'Cartagena'),
    ('Salón Cafetero', 'Carrera 23 #62-18', 180, 'Manizales'),
    ('Arena del Valle', 'Autopista Sur #12-40', 1500, 'Cali'),
    ('Biblioteca Central', 'Calle 11 #4-14', 150, 'Bogotá'),
    ('Parque Metropolitano', 'Avenida Regional #50-20', 2000, 'Medellín');

INSERT INTO categories (nombre, descripcion) VALUES
    ('Conciertos', 'Presentaciones musicales en vivo'),
    ('Talleres', 'Espacios prácticos de aprendizaje'),
    ('Conferencias', 'Charlas y encuentros profesionales'),
    ('Deportes', 'Actividades deportivas y recreativas'),
    ('Gastronomía', 'Experiencias culinarias'),
    ('Festivales', 'Celebraciones culturales de varios días'),
    ('Teatro', 'Obras y artes escénicas');

INSERT INTO events (nombre, fecha, descripcion, activo, venue_id)
SELECT 'Evento de catálogo ' || X,
       DATEADD('DAY', X, DATE '2026-01-01'),
       'Registro de demostración migrado por Flyway',
       TRUE,
       MOD(X - 1, 10) + 1
FROM SYSTEM_RANGE(1, 200);

INSERT INTO events_categories (event_id, category_id)
SELECT id, MOD(id - 1, 7) + 1 FROM events;
