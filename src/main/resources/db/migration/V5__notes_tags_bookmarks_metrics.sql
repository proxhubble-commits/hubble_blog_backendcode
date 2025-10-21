-- ============================================
-- V5: Notes, Tags, Bookmarks, and Metrics
-- 블로그 포스트, 태그, 북마크 및 메트릭 시스템
-- ============================================

-- 시리즈 (폴더/카테고리/연재 통합)
CREATE TABLE series (
  id               BIGSERIAL PRIMARY KEY,
  user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  title            VARCHAR(255) NOT NULL,
  slug             VARCHAR(255) NOT NULL,
  
  -- 타입
  type             VARCHAR(20) NOT NULL DEFAULT 'FOLDER', -- FOLDER, SERIES
  
  -- 통계
  posts_count      INTEGER NOT NULL DEFAULT 0,
  
  -- 정렬
  -- sort_order       INTEGER NOT NULL DEFAULT 0, -- TODO: 나중에 추가하고 싶다
  
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(user_id, slug)
);

-- 노트/포스트 (블로그 게시물)
CREATE TABLE notes (
  id               BIGSERIAL PRIMARY KEY,
  user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  series_id        BIGINT REFERENCES series(id) ON DELETE SET NULL,
  series_order     INTEGER, -- 시리즈 내 순서
  
  -- 콘텐츠
  title            VARCHAR(255) NOT NULL,
  slug             VARCHAR(255) NOT NULL,
  summary          TEXT, -- 요약/미리보기
  content          TEXT NOT NULL,
  content_type     VARCHAR(20) NOT NULL DEFAULT 'MARKDOWN', -- MARKDOWN, HTML, PLAIN
  thumbnail_url    TEXT,
  
  -- 상태 및 공개 설정
  status           VARCHAR(20) NOT NULL DEFAULT 'DRAFT', -- DRAFT, PUBLISHED, SCHEDULED, ARCHIVED
  visibility       VARCHAR(20) NOT NULL DEFAULT 'PUBLIC', -- PUBLIC, UNLISTED, PRIVATE
  
  -- 카운터 (메인 테이블에 포함 - 자주 조회)
  views_count      INTEGER NOT NULL DEFAULT 0,
  likes_count      INTEGER NOT NULL DEFAULT 0,
  comments_count   INTEGER NOT NULL DEFAULT 0,
  bookmarks_count  INTEGER NOT NULL DEFAULT 0,
  
  -- SEO (메인 테이블에 포함 - 자주 사용)
  meta_title       VARCHAR(255),
  meta_description TEXT,
  meta_keywords    TEXT,
  
  -- 타임스탬프
  published_at     TIMESTAMPTZ,
  scheduled_at     TIMESTAMPTZ, -- 예약 발행
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  
  -- 읽기 시간 (분)
  reading_time     INTEGER,
  
  -- 확장 메타데이터 (검색 안 하는 부가 정보)
  metadata         JSONB DEFAULT '{}'::jsonb,
  
  UNIQUE(user_id, slug)
);

-- 노트 댓글
CREATE TABLE note_comments (
  id               BIGSERIAL PRIMARY KEY,
  note_id          BIGINT NOT NULL REFERENCES notes(id) ON DELETE CASCADE,
  user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  parent_id        BIGINT REFERENCES note_comments(id) ON DELETE CASCADE,
  content          TEXT NOT NULL,
  likes_count      INTEGER NOT NULL DEFAULT 0,
  is_deleted       BOOLEAN NOT NULL DEFAULT false,
  is_author_reply  BOOLEAN NOT NULL DEFAULT false, -- 작성자 답글 표시
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 노트 좋아요
CREATE TABLE note_likes (
  id               BIGSERIAL PRIMARY KEY,
  note_id          BIGINT NOT NULL REFERENCES notes(id) ON DELETE CASCADE,
  user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(note_id, user_id)
);

-- 댓글 좋아요
CREATE TABLE comment_likes (
  id               BIGSERIAL PRIMARY KEY,
  comment_id       BIGINT NOT NULL REFERENCES note_comments(id) ON DELETE CASCADE,
  user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(comment_id, user_id)
);

-- 태그 (해시태그)
CREATE TABLE tags (
  id               BIGSERIAL PRIMARY KEY,
  name             VARCHAR(100) NOT NULL UNIQUE,
  slug             VARCHAR(100) NOT NULL UNIQUE,
  description      TEXT,
  usage_count      INTEGER NOT NULL DEFAULT 0,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 노트-태그 연결
CREATE TABLE note_tags (
  id               BIGSERIAL PRIMARY KEY,
  note_id          BIGINT NOT NULL REFERENCES notes(id) ON DELETE CASCADE,
  tag_id           BIGINT NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(note_id, tag_id)
);

-- 북마크
CREATE TABLE bookmarks (
  id               BIGSERIAL PRIMARY KEY,
  user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  note_id          BIGINT NOT NULL REFERENCES notes(id) ON DELETE CASCADE,
  folder           VARCHAR(100) DEFAULT 'default',
  notes_text       TEXT, -- 사용자 메모
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(user_id, note_id)
);

-- 북마크 폴더
CREATE TABLE bookmark_folders (
  id               BIGSERIAL PRIMARY KEY,
  user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  name             VARCHAR(100) NOT NULL,
  description      TEXT,
  icon             VARCHAR(50),
  sort_order       INTEGER NOT NULL DEFAULT 0,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(user_id, name)
);

-- 노트 조회 기록
CREATE TABLE note_views (
  id               BIGSERIAL PRIMARY KEY,
  note_id          BIGINT NOT NULL REFERENCES notes(id) ON DELETE CASCADE,
  user_id          BIGINT REFERENCES users(id) ON DELETE SET NULL,
  ip_address       VARCHAR(45),
  referrer         TEXT, -- 유입 경로
  user_agent       TEXT,
  viewed_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 일간 노트 메트릭 (분리 필수 - 집계 데이터)
CREATE TABLE note_daily_metrics (
  id               BIGSERIAL PRIMARY KEY,
  note_id          BIGINT NOT NULL REFERENCES notes(id) ON DELETE CASCADE,
  date             DATE NOT NULL,
  views_count      INTEGER NOT NULL DEFAULT 0,
  unique_views     INTEGER NOT NULL DEFAULT 0,
  likes_count      INTEGER NOT NULL DEFAULT 0,
  comments_count   INTEGER NOT NULL DEFAULT 0,
  bookmarks_count  INTEGER NOT NULL DEFAULT 0,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(note_id, date)
);

-- 사용자 일간 활동 메트릭 (분리 필수 - 집계 데이터)
CREATE TABLE user_daily_metrics (
  id               BIGSERIAL PRIMARY KEY,
  user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  date             DATE NOT NULL,
  posts_published  INTEGER NOT NULL DEFAULT 0,
  comments_created INTEGER NOT NULL DEFAULT 0,
  likes_received   INTEGER NOT NULL DEFAULT 0,
  total_views      INTEGER NOT NULL DEFAULT 0,
  new_subscribers  INTEGER NOT NULL DEFAULT 0,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(user_id, date)
);

-- ============================================
-- 인덱스 생성
-- ============================================

-- 시리즈 인덱스
CREATE INDEX idx_series_user_id ON series(user_id);
CREATE INDEX idx_series_slug ON series(slug);
CREATE INDEX idx_series_type ON series(type);
-- CREATE INDEX idx_series_sort_order ON series(sort_order); -- TODO: 나중에 추가

-- 노트 인덱스
CREATE INDEX idx_notes_user_id ON notes(user_id);
CREATE INDEX idx_notes_series_id ON notes(series_id);
CREATE INDEX idx_notes_status ON notes(status);
CREATE INDEX idx_notes_visibility ON notes(visibility);
CREATE INDEX idx_notes_slug ON notes(slug);
CREATE INDEX idx_notes_published_at ON notes(published_at DESC NULLS LAST);
CREATE INDEX idx_notes_created_at ON notes(created_at DESC);
CREATE INDEX idx_notes_likes_count ON notes(likes_count DESC);
CREATE INDEX idx_notes_views_count ON notes(views_count DESC);
CREATE INDEX idx_notes_scheduled_at ON notes(scheduled_at) WHERE scheduled_at IS NOT NULL;

-- 노트 전문 검색 인덱스
CREATE INDEX idx_notes_title_search ON notes USING gin(to_tsvector('english', title));
CREATE INDEX idx_notes_content_search ON notes USING gin(to_tsvector('english', content));

-- 댓글 인덱스
CREATE INDEX idx_note_comments_note_id ON note_comments(note_id);
CREATE INDEX idx_note_comments_user_id ON note_comments(user_id);
CREATE INDEX idx_note_comments_parent_id ON note_comments(parent_id);
CREATE INDEX idx_note_comments_created_at ON note_comments(created_at DESC);

-- 좋아요 인덱스
CREATE INDEX idx_note_likes_note_id ON note_likes(note_id);
CREATE INDEX idx_note_likes_user_id ON note_likes(user_id);
CREATE INDEX idx_comment_likes_comment_id ON comment_likes(comment_id);

-- 태그 인덱스
CREATE INDEX idx_tags_slug ON tags(slug);
CREATE INDEX idx_tags_usage_count ON tags(usage_count DESC);
CREATE INDEX idx_note_tags_note_id ON note_tags(note_id);
CREATE INDEX idx_note_tags_tag_id ON note_tags(tag_id);

-- 북마크 인덱스
CREATE INDEX idx_bookmarks_user_id ON bookmarks(user_id);
CREATE INDEX idx_bookmarks_note_id ON bookmarks(note_id);
CREATE INDEX idx_bookmarks_folder ON bookmarks(folder);
CREATE INDEX idx_bookmarks_created_at ON bookmarks(created_at DESC);
CREATE INDEX idx_bookmark_folders_user_id ON bookmark_folders(user_id);

-- 조회 기록 인덱스
CREATE INDEX idx_note_views_note_id ON note_views(note_id);
CREATE INDEX idx_note_views_user_id ON note_views(user_id);
CREATE INDEX idx_note_views_viewed_at ON note_views(viewed_at DESC);

-- 메트릭 인덱스
CREATE INDEX idx_note_daily_metrics_note_id ON note_daily_metrics(note_id);
CREATE INDEX idx_note_daily_metrics_date ON note_daily_metrics(date DESC);
CREATE INDEX idx_user_daily_metrics_user_id ON user_daily_metrics(user_id);
CREATE INDEX idx_user_daily_metrics_date ON user_daily_metrics(date DESC);

-- ============================================
-- 트리거 함수
-- ============================================

-- 노트 좋아요 카운터
CREATE OR REPLACE FUNCTION update_note_likes_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE notes SET likes_count = likes_count + 1 WHERE id = NEW.note_id;
        UPDATE user_metrics SET total_likes = total_likes + 1 
        WHERE user_id = (SELECT user_id FROM notes WHERE id = NEW.note_id);
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE notes SET likes_count = GREATEST(0, likes_count - 1) WHERE id = OLD.note_id;
        UPDATE user_metrics SET total_likes = GREATEST(0, total_likes - 1)
        WHERE user_id = (SELECT user_id FROM notes WHERE id = OLD.note_id);
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER note_likes_counter 
  AFTER INSERT OR DELETE ON note_likes
  FOR EACH ROW EXECUTE FUNCTION update_note_likes_count();

-- 댓글 카운터 + 작성자 답글 표시
CREATE OR REPLACE FUNCTION update_note_comments_count()
RETURNS TRIGGER AS $$
DECLARE
    note_author_id BIGINT;
BEGIN
    IF TG_OP = 'INSERT' THEN
        -- 노트 작성자 ID 가져오기
        SELECT user_id INTO note_author_id FROM notes WHERE id = NEW.note_id;
        
        -- 작성자가 댓글을 단 경우 is_author_reply = true
        IF NEW.user_id = note_author_id THEN
            NEW.is_author_reply = true;
        END IF;
        
        UPDATE notes SET comments_count = comments_count + 1 WHERE id = NEW.note_id;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE notes SET comments_count = GREATEST(0, comments_count - 1) WHERE id = OLD.note_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER note_comments_counter 
  BEFORE INSERT OR AFTER DELETE ON note_comments
  FOR EACH ROW EXECUTE FUNCTION update_note_comments_count();

-- 댓글 좋아요 카운터
CREATE OR REPLACE FUNCTION update_comment_likes_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE note_comments SET likes_count = likes_count + 1 WHERE id = NEW.comment_id;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE note_comments SET likes_count = GREATEST(0, likes_count - 1) WHERE id = OLD.comment_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER comment_likes_counter 
  AFTER INSERT OR DELETE ON comment_likes
  FOR EACH ROW EXECUTE FUNCTION update_comment_likes_count();

-- 북마크 카운터
CREATE OR REPLACE FUNCTION update_note_bookmarks_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE notes SET bookmarks_count = bookmarks_count + 1 WHERE id = NEW.note_id;
        UPDATE user_metrics SET bookmarks_count = bookmarks_count + 1 WHERE user_id = NEW.user_id;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE notes SET bookmarks_count = GREATEST(0, bookmarks_count - 1) WHERE id = OLD.note_id;
        UPDATE user_metrics SET bookmarks_count = GREATEST(0, bookmarks_count - 1) WHERE user_id = OLD.user_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER bookmarks_counter 
  AFTER INSERT OR DELETE ON bookmarks
  FOR EACH ROW EXECUTE FUNCTION update_note_bookmarks_count();

-- 태그 사용 카운터
CREATE OR REPLACE FUNCTION update_tag_usage_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE tags SET usage_count = usage_count + 1 WHERE id = NEW.tag_id;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE tags SET usage_count = GREATEST(0, usage_count - 1) WHERE id = OLD.tag_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tag_usage_counter 
  AFTER INSERT OR DELETE ON note_tags
  FOR EACH ROW EXECUTE FUNCTION update_tag_usage_count();

-- 시리즈 포스트 카운터
CREATE OR REPLACE FUNCTION update_series_posts_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' AND NEW.series_id IS NOT NULL AND NEW.status = 'PUBLISHED' THEN
        UPDATE series SET posts_count = posts_count + 1 WHERE id = NEW.series_id;
    ELSIF TG_OP = 'DELETE' AND OLD.series_id IS NOT NULL AND OLD.status = 'PUBLISHED' THEN
        UPDATE series SET posts_count = GREATEST(0, posts_count - 1) WHERE id = OLD.series_id;
    ELSIF TG_OP = 'UPDATE' THEN
        -- 시리즈 변경
        IF OLD.series_id != NEW.series_id THEN
            IF OLD.series_id IS NOT NULL AND OLD.status = 'PUBLISHED' THEN
                UPDATE series SET posts_count = GREATEST(0, posts_count - 1) WHERE id = OLD.series_id;
            END IF;
            IF NEW.series_id IS NOT NULL AND NEW.status = 'PUBLISHED' THEN
                UPDATE series SET posts_count = posts_count + 1 WHERE id = NEW.series_id;
            END IF;
        -- 상태 변경
        ELSIF OLD.status != NEW.status AND NEW.series_id IS NOT NULL THEN
            IF OLD.status != 'PUBLISHED' AND NEW.status = 'PUBLISHED' THEN
                UPDATE series SET posts_count = posts_count + 1 WHERE id = NEW.series_id;
            ELSIF OLD.status = 'PUBLISHED' AND NEW.status != 'PUBLISHED' THEN
                UPDATE series SET posts_count = GREATEST(0, posts_count - 1) WHERE id = NEW.series_id;
            END IF;
        END IF;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER series_posts_counter 
  AFTER INSERT OR DELETE OR UPDATE ON notes
  FOR EACH ROW EXECUTE FUNCTION update_series_posts_count();

-- 사용자 포스트 카운터
CREATE OR REPLACE FUNCTION update_user_posts_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE user_metrics SET posts_count = posts_count + 1 WHERE user_id = NEW.user_id;
        IF NEW.status = 'PUBLISHED' THEN
            UPDATE user_metrics 
            SET published_count = published_count + 1,
                last_published_at = NEW.published_at
            WHERE user_id = NEW.user_id;
        END IF;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE user_metrics SET posts_count = GREATEST(0, posts_count - 1) WHERE user_id = OLD.user_id;
        IF OLD.status = 'PUBLISHED' THEN
            UPDATE user_metrics SET published_count = GREATEST(0, published_count - 1)
            WHERE user_id = OLD.user_id;
        END IF;
    ELSIF TG_OP = 'UPDATE' THEN
        IF OLD.status != 'PUBLISHED' AND NEW.status = 'PUBLISHED' THEN
            UPDATE user_metrics 
            SET published_count = published_count + 1,
                last_published_at = NEW.published_at
            WHERE user_id = NEW.user_id;
        ELSIF OLD.status = 'PUBLISHED' AND NEW.status != 'PUBLISHED' THEN
            UPDATE user_metrics SET published_count = GREATEST(0, published_count - 1)
            WHERE user_id = NEW.user_id;
        END IF;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER user_posts_counter 
  AFTER INSERT OR DELETE OR UPDATE ON notes
  FOR EACH ROW EXECUTE FUNCTION update_user_posts_count();

-- updated_at 트리거
CREATE TRIGGER update_series_updated_at BEFORE UPDATE ON series
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_notes_updated_at BEFORE UPDATE ON notes
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_note_comments_updated_at BEFORE UPDATE ON note_comments
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_bookmark_folders_updated_at BEFORE UPDATE ON bookmark_folders
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- 초기 데이터
-- ============================================

-- 새 사용자 생성 시 기본 북마크 폴더 자동 생성
CREATE OR REPLACE FUNCTION create_default_bookmark_folder()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO bookmark_folders (user_id, name, description, icon, sort_order)
    VALUES (NEW.id, 'default', 'Default bookmark folder', '📌', 0);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER create_user_default_bookmark_folder 
  AFTER INSERT ON users
  FOR EACH ROW EXECUTE FUNCTION create_default_bookmark_folder();

-- 기존 사용자에 대한 기본 폴더 생성
INSERT INTO bookmark_folders (user_id, name, description, icon, sort_order)
SELECT id, 'default', 'Default bookmark folder', '📌', 0
FROM users
ON CONFLICT (user_id, name) DO NOTHING;
