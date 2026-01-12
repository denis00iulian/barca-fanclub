-- V3: remove DB trigger/function for memberships updated_at (switch to app-first)

DROP TRIGGER IF EXISTS trg_memberships_set_updated_at ON memberships;
DROP FUNCTION IF EXISTS set_updated_at();
