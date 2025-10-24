-- Enable pg_trgm extension for trigram-based text search
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Speakers table (speakers can participate across multiple events/years)
CREATE TABLE speakers (
  id                uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  full_name         text        NOT NULL,    -- speaker's full name
  org_name          text,                     -- organization/company name
  job_title         text,                     -- current position
  bio               text,                     -- biography/profile
  profile_image_url text,                     -- profile picture URL
  contacts          jsonb       NOT NULL DEFAULT '{}'::jsonb, -- {email, linkedin, web, etc.}
  created_at        timestamptz NOT NULL DEFAULT now(),
  updated_at        timestamptz NOT NULL DEFAULT now()
);

-- Trigram index for fuzzy name search
CREATE INDEX speakers_name_trgm ON speakers USING gin (full_name gin_trgm_ops);

