CREATE INDEX idx_flags_key ON flags(key);
CREATE INDEX idx_flags_enabled ON flags(enabled);
CREATE INDEX idx_audit_logs_flag_id ON audit_logs(flag_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
