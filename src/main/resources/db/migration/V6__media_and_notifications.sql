-- ============================================
-- V6: Media and Notifications
-- 미디어 자산 및 알림 시스템 (블로그 플랫폼)
-- ============================================

-- 미디어 자산 테이블
CREATE TABLE media_assets (
  id               BIGSERIAL PRIMARY KEY,
  user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  type             VARCHAR(20) NOT NULL, -- IMAGE, VIDEO, AUDIO, DOCUMENT, OTHER
  url              TEXT NOT NULL,
  thumbnail_url    TEXT,
  
  -- 파일 정보
  filename         VARCHAR(255) NOT NULL,
  original_filename VARCHAR(255),
  mime_type        VARCHAR(100) NOT NULL,
  size_bytes       BIGINT NOT NULL,
  
  -- 미디어 메타데이터
  width            INTEGER,
  height           INTEGER,
  duration_seconds INTEGER,
  
  -- 상태 및 처리
  status           VARCHAR(20) NOT NULL DEFAULT 'PROCESSING', -- PROCESSING, READY, FAILED
  storage_provider VARCHAR(50) NOT NULL DEFAULT 'local', -- local, s3, gcs, cloudinary
  storage_path     TEXT,
  
  -- 추가 메타데이터
  metadata         JSONB DEFAULT '{}'::jsonb,
  alt_text         TEXT,
  caption          TEXT,
  
  -- 사용 추적
  usage_count      INTEGER NOT NULL DEFAULT 0,
  
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 노트-미디어 연결 테이블
CREATE TABLE note_media (
  id               BIGSERIAL PRIMARY KEY,
  note_id          BIGINT NOT NULL REFERENCES notes(id) ON DELETE CASCADE,
  media_id         BIGINT NOT NULL REFERENCES media_assets(id) ON DELETE CASCADE,
  sort_order       INTEGER NOT NULL DEFAULT 0,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(note_id, media_id)
);

-- 알림 테이블
CREATE TABLE notifications (
  id               BIGSERIAL PRIMARY KEY,
  user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  type             VARCHAR(50) NOT NULL, -- COMMENT, REPLY, LIKE, SUBSCRIBE, SYSTEM
  
  -- 액터 (알림을 발생시킨 사용자)
  actor_id         BIGINT REFERENCES users(id) ON DELETE SET NULL,
  
  -- 타겟 (알림의 대상)
  target_type      VARCHAR(50), -- NOTE, COMMENT
  target_id        BIGINT,
  
  -- 알림 내용
  title            VARCHAR(255),
  content          TEXT,
  action_url       TEXT,
  
  -- 상태
  is_read          BOOLEAN NOT NULL DEFAULT false,
  read_at          TIMESTAMPTZ,
  
  -- 그룹화 (같은 타입의 알림을 묶기 위한 키)
  group_key        VARCHAR(255),
  
  -- 메타데이터
  metadata         JSONB DEFAULT '{}'::jsonb,
  
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  expires_at       TIMESTAMPTZ -- 자동 삭제를 위한 만료 시간
);

-- 알림 설정 (사용자별 알림 선호도)
CREATE TABLE notification_preferences (
  id               BIGSERIAL PRIMARY KEY,
  user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  
  -- 이메일 알림
  email_comments   BOOLEAN NOT NULL DEFAULT true,
  email_replies    BOOLEAN NOT NULL DEFAULT true,
  email_likes      BOOLEAN NOT NULL DEFAULT true,
  email_subscribers BOOLEAN NOT NULL DEFAULT true,
  email_system     BOOLEAN NOT NULL DEFAULT true,
  email_digest     BOOLEAN NOT NULL DEFAULT false, -- 주간/월간 요약
  
  -- 푸시 알림
  push_comments    BOOLEAN NOT NULL DEFAULT true,
  push_replies     BOOLEAN NOT NULL DEFAULT true,
  push_likes       BOOLEAN NOT NULL DEFAULT false,
  push_subscribers BOOLEAN NOT NULL DEFAULT true,
  push_system      BOOLEAN NOT NULL DEFAULT true,
  
  -- 앱 내 알림
  app_comments     BOOLEAN NOT NULL DEFAULT true,
  app_replies      BOOLEAN NOT NULL DEFAULT true,
  app_likes        BOOLEAN NOT NULL DEFAULT true,
  app_subscribers  BOOLEAN NOT NULL DEFAULT true,
  app_system       BOOLEAN NOT NULL DEFAULT true,
  
  -- 일괄 설정
  do_not_disturb   BOOLEAN NOT NULL DEFAULT false,
  dnd_start_time   TIME, -- 방해 금지 시작 시간
  dnd_end_time     TIME, -- 방해 금지 종료 시간
  
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(user_id)
);

-- 푸시 토큰 (모바일 푸시 알림용)
CREATE TABLE push_tokens (
  id               BIGSERIAL PRIMARY KEY,
  user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token            TEXT NOT NULL UNIQUE,
  platform         VARCHAR(20) NOT NULL, -- IOS, ANDROID, WEB
  device_name      VARCHAR(100),
  is_active        BOOLEAN NOT NULL DEFAULT true,
  last_used_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 활동 로그 (선택적 - 분석용)
CREATE TABLE activity_logs (
  id               BIGSERIAL PRIMARY KEY,
  user_id          BIGINT REFERENCES users(id) ON DELETE SET NULL, -- NULL = 비로그인 사용자
  action           VARCHAR(50) NOT NULL, -- POST_VIEW, POST_CREATE, POST_UPDATE, LOGIN, etc.
  
  -- 타겟 리소스
  target_type      VARCHAR(50),
  target_id        BIGINT,
  
  -- 요청 정보
  ip_address       VARCHAR(45),
  user_agent       TEXT,
  device_type      VARCHAR(20), -- DESKTOP, MOBILE, TABLET
  referrer         TEXT,
  
  -- 메타데이터
  metadata         JSONB DEFAULT '{}'::jsonb,
  
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 이메일 구독 테이블 (뉴스레터용)
CREATE TABLE email_subscriptions (
  id               BIGSERIAL PRIMARY KEY,
  email            VARCHAR(320) NOT NULL UNIQUE,
  user_id          BIGINT REFERENCES users(id) ON DELETE SET NULL, -- 연결된 사용자 계정
  status           VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, UNSUBSCRIBED
  subscription_type VARCHAR(50) NOT NULL DEFAULT 'WEEKLY', -- DAILY, WEEKLY, MONTHLY, NEW_POST
  unsubscribe_token VARCHAR(255) UNIQUE,
  confirmed_at     TIMESTAMPTZ,
  unsubscribed_at  TIMESTAMPTZ,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================
-- 인덱스 생성
-- ============================================

-- 미디어 인덱스
CREATE INDEX idx_media_assets_user_id ON media_assets(user_id);
CREATE INDEX idx_media_assets_type ON media_assets(type);
CREATE INDEX idx_media_assets_status ON media_assets(status);
CREATE INDEX idx_media_assets_created_at ON media_assets(created_at DESC);
CREATE INDEX idx_note_media_note_id ON note_media(note_id);
CREATE INDEX idx_note_media_media_id ON note_media(media_id);

-- 알림 인덱스
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_actor_id ON notifications(actor_id);
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);
CREATE INDEX idx_notifications_group_key ON notifications(group_key);
CREATE INDEX idx_notifications_expires_at ON notifications(expires_at) WHERE expires_at IS NOT NULL;

-- 복합 인덱스 (읽지 않은 알림 조회 최적화)
CREATE INDEX idx_notifications_user_unread ON notifications(user_id, is_read, created_at DESC);

-- 푸시 토큰 인덱스
CREATE INDEX idx_push_tokens_user_id ON push_tokens(user_id);
CREATE INDEX idx_push_tokens_platform ON push_tokens(platform);
CREATE INDEX idx_push_tokens_is_active ON push_tokens(is_active);

-- 활동 로그 인덱스
CREATE INDEX idx_activity_logs_user_id ON activity_logs(user_id);
CREATE INDEX idx_activity_logs_action ON activity_logs(action);
CREATE INDEX idx_activity_logs_created_at ON activity_logs(created_at DESC);
CREATE INDEX idx_activity_logs_target ON activity_logs(target_type, target_id);

-- 이메일 구독 인덱스
CREATE INDEX idx_email_subscriptions_email ON email_subscriptions(email);
CREATE INDEX idx_email_subscriptions_user_id ON email_subscriptions(user_id);
CREATE INDEX idx_email_subscriptions_status ON email_subscriptions(status);
CREATE INDEX idx_email_subscriptions_unsubscribe_token ON email_subscriptions(unsubscribe_token);

-- ============================================
-- 트리거 함수
-- ============================================

-- 미디어 사용 카운터
CREATE OR REPLACE FUNCTION update_media_usage_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE media_assets SET usage_count = usage_count + 1 WHERE id = NEW.media_id;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE media_assets SET usage_count = GREATEST(0, usage_count - 1) WHERE id = OLD.media_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER media_usage_counter 
  AFTER INSERT OR DELETE ON note_media
  FOR EACH ROW EXECUTE FUNCTION update_media_usage_count();

-- 알림 읽음 처리 시 read_at 자동 설정
CREATE OR REPLACE FUNCTION update_notification_read_at()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_read = true AND OLD.is_read = false THEN
        NEW.read_at = now();
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER notification_read_at 
  BEFORE UPDATE ON notifications
  FOR EACH ROW EXECUTE FUNCTION update_notification_read_at();

-- updated_at 트리거
CREATE TRIGGER update_media_assets_updated_at 
  BEFORE UPDATE ON media_assets
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_notification_preferences_updated_at 
  BEFORE UPDATE ON notification_preferences
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- 알림 생성 헬퍼 함수
-- ============================================

-- 댓글 알림 생성
CREATE OR REPLACE FUNCTION create_comment_notification()
RETURNS TRIGGER AS $$
DECLARE
    note_author_id BIGINT;
    note_title VARCHAR(255);
BEGIN
    -- 노트 작성자 ID 가져오기
    SELECT user_id, title 
    INTO note_author_id, note_title
    FROM notes WHERE id = NEW.note_id;
    
    -- 자기 자신의 노트에 댓글 단 경우 알림 생성 안 함
    IF note_author_id != NEW.user_id THEN
        INSERT INTO notifications (
            user_id, type, actor_id, target_type, target_id,
            title, content, action_url, group_key
        ) VALUES (
            note_author_id, 
            'COMMENT', 
            NEW.user_id, 
            'NOTE', 
            NEW.note_id,
            note_title,
            LEFT(NEW.content, 100),
            '/notes/' || NEW.note_id || '#comment-' || NEW.id,
            'comment_note_' || NEW.note_id
        );
    END IF;
    
    -- 대댓글인 경우 원 댓글 작성자에게도 알림
    IF NEW.parent_id IS NOT NULL THEN
        DECLARE
            parent_author_id BIGINT;
        BEGIN
            SELECT user_id INTO parent_author_id 
            FROM note_comments WHERE id = NEW.parent_id;
            
            -- 자기 자신에게는 알림 안 보냄, 노트 작성자와 중복도 방지
            IF parent_author_id != NEW.user_id AND parent_author_id != note_author_id THEN
                INSERT INTO notifications (
                    user_id, type, actor_id, target_type, target_id,
                    title, content, action_url
                ) VALUES (
                    parent_author_id,
                    'REPLY',
                    NEW.user_id,
                    'COMMENT',
                    NEW.parent_id,
                    note_title,
                    LEFT(NEW.content, 100),
                    '/notes/' || NEW.note_id || '#comment-' || NEW.id
                );
            END IF;
        END;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER create_comment_notification_trigger
  AFTER INSERT ON note_comments
  FOR EACH ROW EXECUTE FUNCTION create_comment_notification();

-- 좋아요 알림 생성 (글 작성자에게만)
CREATE OR REPLACE FUNCTION create_like_notification()
RETURNS TRIGGER AS $$
DECLARE
    note_owner_id BIGINT;
    note_title VARCHAR(255);
BEGIN
    -- 노트 작성자 ID 가져오기
    SELECT user_id, title 
    INTO note_owner_id, note_title
    FROM notes WHERE id = NEW.note_id;
    
    -- 자기 자신의 노트에 좋아요한 경우 알림 생성 안 함
    IF note_owner_id != NEW.user_id THEN
        INSERT INTO notifications (
            user_id, type, actor_id, target_type, target_id,
            title, action_url, group_key
        ) VALUES (
            note_owner_id, 
            'LIKE', 
            NEW.user_id, 
            'NOTE', 
            NEW.note_id,
            note_title,
            '/notes/' || NEW.note_id,
            'like_note_' || NEW.note_id
        );
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER create_like_notification_trigger
  AFTER INSERT ON note_likes
  FOR EACH ROW EXECUTE FUNCTION create_like_notification();

-- 구독 알림 생성
CREATE OR REPLACE FUNCTION create_subscription_notification()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO notifications (
        user_id, type, actor_id, target_type, target_id,
        action_url
    ) VALUES (
        NEW.author_id, 
        'SUBSCRIBE', 
        NEW.subscriber_id, 
        'USER', 
        NEW.subscriber_id,
        '/users/' || NEW.subscriber_id
    );
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER create_subscription_notification_trigger
  AFTER INSERT ON subscriptions
  FOR EACH ROW EXECUTE FUNCTION create_subscription_notification();

-- ============================================
-- 만료된 알림 자동 삭제 함수
-- ============================================

CREATE OR REPLACE FUNCTION delete_expired_notifications()
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM notifications 
    WHERE expires_at IS NOT NULL AND expires_at < now();
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- 초기 데이터
-- ============================================

-- 새 사용자 생성 시 기본 알림 설정 자동 생성
CREATE OR REPLACE FUNCTION create_default_notification_preferences()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO notification_preferences (user_id)
    VALUES (NEW.id);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER create_user_notification_preferences 
  AFTER INSERT ON users
  FOR EACH ROW EXECUTE FUNCTION create_default_notification_preferences();

-- 기존 사용자에 대한 알림 설정 생성
INSERT INTO notification_preferences (user_id)
SELECT id FROM users
ON CONFLICT (user_id) DO NOTHING;

-- ============================================
-- 유틸리티 뷰
-- ============================================

-- 사용자별 읽지 않은 알림 개수 뷰
CREATE VIEW user_unread_notification_counts AS
SELECT 
    user_id,
    COUNT(*) as total_unread,
    COUNT(*) FILTER (WHERE type = 'LIKE') as unread_likes,
    COUNT(*) FILTER (WHERE type = 'COMMENT') as unread_comments,
    COUNT(*) FILTER (WHERE type = 'REPLY') as unread_replies,
    COUNT(*) FILTER (WHERE type = 'SUBSCRIBE') as unread_subscribes
FROM notifications
WHERE is_read = false
GROUP BY user_id;

-- 최근 활동 요약 뷰
CREATE VIEW recent_activity_summary AS
SELECT 
    user_id,
    action,
    COUNT(*) as count,
    MAX(created_at) as last_activity_at
FROM activity_logs
WHERE created_at > now() - INTERVAL '7 days'
  AND user_id IS NOT NULL
GROUP BY user_id, action;

-- 인기 포스트 뷰 (지난 30일)
CREATE VIEW trending_posts AS
SELECT 
    n.id,
    n.user_id,
    n.title,
    n.slug,
    n.views_count,
    n.likes_count,
    n.comments_count,
    (n.views_count * 0.5 + n.likes_count * 2 + n.comments_count * 3) as trend_score
FROM notes n
WHERE n.status = 'PUBLISHED'
  AND n.visibility = 'PUBLIC'
  AND n.published_at > now() - INTERVAL '30 days'
ORDER BY trend_score DESC;
