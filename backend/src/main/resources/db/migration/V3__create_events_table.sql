-- Annual event table (supports multiple editions)
-- Each event represents a specific edition of a recurring event
CREATE TABLE events (
  id           uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  name         text        NOT NULL,         -- e.g., "Computer Science Week"
  year         int         NOT NULL,         -- edition year
  description  text,                         -- event overview
  venue        text,                         -- physical location
  tz           text        NOT NULL DEFAULT 'America/Lima', -- timezone for schedule
  starts_on    date,                         -- event start date
  ends_on      date,                         -- event end date
  website_url  text,                         -- official website
  contacts     jsonb       NOT NULL DEFAULT '{}'::jsonb, -- contact information (emails, phones, etc.)
  created_at   timestamptz NOT NULL DEFAULT now(),
  updated_at   timestamptz NOT NULL DEFAULT now(),
  UNIQUE (name, year)                        -- one edition per event per year
);

