-- ============================================
-- V3: Users, Profiles, Settings and Indexes
-- 사용자 프로필 확장 및 설정 (블로그 플랫폼)
-- ============================================

-- 사용자 프로필 상세 테이블
CREATE TABLE user_profiles (
  user_id          BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
  bio              TEXT,
  website          VARCHAR(255),
  location         VARCHAR(100),
  birth_date       DATE,
  phone            VARCHAR(20),
  avatar_url       TEXT,
  cover_url        TEXT,
  social_links     JSONB DEFAULT '{}'::jsonb, -- {twitter: "...", github: "...", linkedin: "..."}
  
  -- 블로그 작성자 정보
  author_name      VARCHAR(100), -- 필명/작가명
  expertise        TEXT, -- 전문 분야
  
  custom_fields    JSONB DEFAULT '{}'::jsonb,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 사용자 설정 테이블
CREATE TABLE user_settings (
  user_id          BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
  
  -- 프라이버시 설정
  profile_visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC', -- PUBLIC, PRIVATE
  show_email       BOOLEAN NOT NULL DEFAULT false,
  show_stats       BOOLEAN NOT NULL DEFAULT true, -- 통계 공개 여부
  allow_comments   BOOLEAN NOT NULL DEFAULT true,
  
  -- 알림 설정
  email_notifications JSONB DEFAULT '{"comments": true, "likes": true, "subscribers": true}'::jsonb,
  push_notifications  JSONB DEFAULT '{"comments": true, "likes": true, "subscribers": true}'::jsonb,
  
  -- 편집기 설정
  default_editor   VARCHAR(20) NOT NULL DEFAULT 'MARKDOWN', -- MARKDOWN, WYSIWYG
  auto_save        BOOLEAN NOT NULL DEFAULT true,
  
  -- 기타 설정
  language         VARCHAR(10) NOT NULL DEFAULT 'ko',
  timezone         VARCHAR(50) NOT NULL DEFAULT 'Asia/Seoul',
  theme            VARCHAR(20) NOT NULL DEFAULT 'light',
  
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 사용자 통계 테이블
CREATE TABLE user_metrics (
  user_id          BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
  posts_count      INTEGER NOT NULL DEFAULT 0,
  published_count  INTEGER NOT NULL DEFAULT 0, -- 공개된 포스트 수
  subscribers_count INTEGER NOT NULL DEFAULT 0, -- 구독자 수
  total_views      BIGINT NOT NULL DEFAULT 0,
  total_likes      INTEGER NOT NULL DEFAULT 0,
  bookmarks_count  INTEGER NOT NULL DEFAULT 0,
  last_published_at TIMESTAMPTZ,
  last_active_at   TIMESTAMPTZ,
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 구독 관계 테이블 (블로그 작성자 구독)
CREATE TABLE subscriptions (
  id               BIGSERIAL PRIMARY KEY,
  subscriber_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  author_id        BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  notification_enabled BOOLEAN NOT NULL DEFAULT true, -- 새 글 알림 수신 여부
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(subscriber_id, author_id),
  CHECK (subscriber_id != author_id)
);

-- ============================================
-- 인덱스 생성
-- ============================================

-- User 테이블 기존 인덱스 강화
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX idx_users_nickname ON users(nickname);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_created_at ON users(created_at DESC);

-- OAuth 계정 인덱스 강화
CREATE INDEX IF NOT EXISTS idx_oauth_accounts_provider_user ON oauth_accounts(provider, provider_user_id);
CREATE INDEX idx_oauth_accounts_user_id ON oauth_accounts(user_id);

-- Refresh Token 인덱스 강화
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

-- 프로필 인덱스
CREATE INDEX idx_user_profiles_location ON user_profiles(location);
CREATE INDEX idx_user_profiles_author_name ON user_profiles(author_name);
CREATE INDEX idx_user_profiles_updated_at ON user_profiles(updated_at DESC);

-- 구독 인덱스
CREATE INDEX idx_subscriptions_subscriber_id ON subscriptions(subscriber_id);
CREATE INDEX idx_subscriptions_author_id ON subscriptions(author_id);
CREATE INDEX idx_subscriptions_created_at ON subscriptions(created_at DESC);

-- ============================================
-- 트리거 함수
-- ============================================

-- updated_at 자동 업데이트 함수
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 트리거 적용
CREATE TRIGGER update_user_profiles_updated_at 
  BEFORE UPDATE ON user_profiles
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_settings_updated_at 
  BEFORE UPDATE ON user_settings
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_metrics_updated_at 
  BEFORE UPDATE ON user_metrics
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- 구독자 카운터 트리거
-- ============================================

CREATE OR REPLACE FUNCTION update_subscription_counts()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        -- 작성자의 구독자 수 증가
        UPDATE user_metrics SET subscribers_count = subscribers_count + 1 
        WHERE user_id = NEW.author_id;
    ELSIF TG_OP = 'DELETE' THEN
        -- 작성자의 구독자 수 감소
        UPDATE user_metrics SET subscribers_count = GREATEST(0, subscribers_count - 1)
        WHERE user_id = OLD.author_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER subscription_counter 
  AFTER INSERT OR DELETE ON subscriptions
  FOR EACH ROW EXECUTE FUNCTION update_subscription_counts();

-- ============================================
-- 기존 사용자에 대한 초기 데이터 생성
-- ============================================

-- 모든 기존 사용자에 대해 프로필, 설정, 메트릭 생성
INSERT INTO user_profiles (user_id)
SELECT id FROM users
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_settings (user_id)
SELECT id FROM users
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_metrics (user_id)
SELECT id FROM users
ON CONFLICT (user_id) DO NOTHING;
