CREATE TABLE users (
  id               BIGSERIAL PRIMARY KEY,
  email            VARCHAR(320) UNIQUE NOT NULL,
  nickname         VARCHAR(60) NOT NULL,
  role             VARCHAR(30) NOT NULL DEFAULT 'USER',
  profile          JSONB NOT NULL DEFAULT '{}'::jsonb,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE oauth_accounts (
  id               BIGSERIAL PRIMARY KEY,
  user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  provider         VARCHAR(30) NOT NULL,
  provider_user_id VARCHAR(128) NOT NULL,
  raw_attributes   JSONB NOT NULL DEFAULT '{}'::jsonb,
  linked_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(provider, provider_user_id)
);

CREATE TABLE refresh_tokens (
  id               BIGSERIAL PRIMARY KEY,
  user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token_hash       VARCHAR(128) NOT NULL UNIQUE,
  expires_at       TIMESTAMPTZ NOT NULL,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Posts table is optional; create later when needed.
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_oauth_accounts_provider_user ON oauth_accounts(provider, provider_user_id);
