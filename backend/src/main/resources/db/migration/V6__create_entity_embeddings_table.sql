-- Entity embeddings table for semantic search
-- Stores vector embeddings of speaker bios, session abstracts/titles
CREATE TABLE entity_embeddings (
  id          uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  entity_type text NOT NULL CHECK (entity_type IN ('speaker','session')),
  entity_id   uuid NOT NULL,                -- FK to speakers.id or sessions.id
  field       text NOT NULL CHECK (field IN ('bio','abstract','title','all')),
  embedding   vector(1536) NOT NULL,        -- OpenAI text-embedding-3-small dimension
  model       text NOT NULL CHECK (model = 'text-embedding-3-small'),
  dim         int  NOT NULL CHECK (dim = 1536),
  created_at  timestamptz NOT NULL DEFAULT now(),
  UNIQUE(entity_type, entity_id, field)     -- one embedding per entity-field combination
);

-- IVFFlat index for approximate nearest neighbor search using cosine similarity
CREATE INDEX entity_embeddings_ivf_cos ON entity_embeddings
  USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

