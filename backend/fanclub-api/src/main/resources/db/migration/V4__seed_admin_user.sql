-- User: admin@fanclub.local
-- Password: Admin123!

INSERT INTO users (email, password_hash, name, role)
VALUES (
           'admin@fanclub.local',
           '$2b$10$m2QvYTVbGgKPwy7l2.3DIu0ALPywh10ick2ilVGVq0fdlAooKxjS6',
           'Admin',
           'ADMIN'
       )
    ON CONFLICT (email) DO NOTHING;

