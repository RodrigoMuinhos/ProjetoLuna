-- Insert test users for LunaCore
INSERT INTO users (email, password, role, status, full_name, created_at, updated_at)
VALUES 
  ('adm@lunavita.com', '\\\.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', 'ACTIVE', 'Administrador', NOW(), NOW()),
  ('recepcao@lunavita.com', '\\\.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'RECEPTION', 'ACTIVE', 'Recepção', NOW(), NOW()),
  ('medico@lunavita.com', '\\\.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'DOCTOR', 'ACTIVE', 'Médico', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;
