-- Sessions table: linear schedule with one speaker per session, no overlaps
-- Each session belongs to a single event and has exactly one speaker
CREATE TABLE sessions (
  id          uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  event_id    uuid NOT NULL REFERENCES events(id) ON DELETE CASCADE,
  speaker_id  uuid NOT NULL REFERENCES speakers(id) ON DELETE RESTRICT,
  title       text NOT NULL,                -- session title
  abstract    text,                         -- session description/summary
  day         date,                         -- redundant but useful for day-based filters
  starts_at   timestamptz NOT NULL,         -- start time in event timezone
  ends_at     timestamptz NOT NULL,         -- end time in event timezone
  seq         int  NOT NULL,                -- absolute ordering within the event
  track       text,                         -- optional track/stream identifier
  created_at  timestamptz NOT NULL DEFAULT now(),
  updated_at  timestamptz NOT NULL DEFAULT now(),
  UNIQUE(event_id, speaker_id),             -- one session per speaker per event
  UNIQUE(event_id, seq)                     -- linear sequence: one session after another
);

-- Index for efficient time-based queries
CREATE INDEX sessions_time_idx ON sessions(event_id, starts_at, ends_at);

