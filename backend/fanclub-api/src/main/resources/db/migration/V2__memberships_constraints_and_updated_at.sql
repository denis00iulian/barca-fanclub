-- V2: tighten memberships validity rules + keep updated_at correct

-- 1) If membership is ACTIVE, starts_at and ends_at must be present.
-- 2) If both dates are present, ends_at must be >= starts_at.

ALTER TABLE memberships
    ADD CONSTRAINT chk_memberships_active_has_dates
        CHECK (
            status <> 'ACTIVE'
                OR (starts_at IS NOT NULL AND ends_at IS NOT NULL)
            );

ALTER TABLE memberships
    ADD CONSTRAINT chk_memberships_date_range
        CHECK (
            (starts_at IS NULL AND ends_at IS NULL)
                OR (starts_at IS NOT NULL AND ends_at IS NOT NULL AND ends_at >= starts_at)
            );

-- Optional but recommended: index for active lookup
CREATE INDEX IF NOT EXISTS idx_memberships_user_status
    ON memberships(user_id, status);

-- updated_at maintenance via trigger
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger AS $$
BEGIN
  NEW.updated_at = now();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_memberships_set_updated_at ON memberships;

CREATE TRIGGER trg_memberships_set_updated_at
    BEFORE UPDATE ON memberships
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();
