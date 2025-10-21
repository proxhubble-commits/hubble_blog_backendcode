-- ============================================
-- V4: Skills and Interests
-- 스킬 및 관심사 시스템
-- ============================================

-- 스킬 마스터 테이블
CREATE TABLE skills (
  id               BIGSERIAL PRIMARY KEY,
  name             VARCHAR(100) NOT NULL,
  slug             VARCHAR(100) NOT NULL UNIQUE,
  category         VARCHAR(50) NOT NULL,
  description      TEXT,
  icon             VARCHAR(50),
  usage_count      INTEGER NOT NULL DEFAULT 0,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 스킬 별칭 (검색 개선용)
CREATE TABLE skill_aliases (
  id               BIGSERIAL PRIMARY KEY,
  skill_id         BIGINT NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
  alias            VARCHAR(100) NOT NULL,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(alias)
);

-- 사용자 스킬
CREATE TABLE user_skills (
  id               BIGSERIAL PRIMARY KEY,
  user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  skill_id         BIGINT NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
  proficiency      VARCHAR(20) NOT NULL DEFAULT 'BEGINNER', -- BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
  years_experience INTEGER,
  is_primary       BOOLEAN NOT NULL DEFAULT false,
  sort_order       INTEGER NOT NULL DEFAULT 0,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(user_id, skill_id)
);

-- 관심사 마스터 테이블
CREATE TABLE interests (
  id               BIGSERIAL PRIMARY KEY,
  name             VARCHAR(100) NOT NULL,
  slug             VARCHAR(100) NOT NULL UNIQUE,
  category         VARCHAR(50) NOT NULL,
  description      TEXT,
  icon             VARCHAR(50),
  usage_count      INTEGER NOT NULL DEFAULT 0,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 사용자 관심사
CREATE TABLE user_interests (
  id               BIGSERIAL PRIMARY KEY,
  user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  interest_id      BIGINT NOT NULL REFERENCES interests(id) ON DELETE CASCADE,
  intensity        VARCHAR(20) NOT NULL DEFAULT 'MODERATE', -- LOW, MODERATE, HIGH
  sort_order       INTEGER NOT NULL DEFAULT 0,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(user_id, interest_id)
);

-- ============================================
-- 인덱스 생성
-- ============================================

-- 스킬 인덱스
CREATE INDEX idx_skills_category ON skills(category);
CREATE INDEX idx_skills_slug ON skills(slug);
CREATE INDEX idx_skills_usage_count ON skills(usage_count DESC);
CREATE INDEX idx_skill_aliases_alias ON skill_aliases(alias);

-- 사용자 스킬 인덱스
CREATE INDEX idx_user_skills_user_id ON user_skills(user_id);
CREATE INDEX idx_user_skills_skill_id ON user_skills(skill_id);
CREATE INDEX idx_user_skills_proficiency ON user_skills(proficiency);
CREATE INDEX idx_user_skills_is_primary ON user_skills(is_primary);

-- 관심사 인덱스
CREATE INDEX idx_interests_category ON interests(category);
CREATE INDEX idx_interests_slug ON interests(slug);
CREATE INDEX idx_interests_usage_count ON interests(usage_count DESC);

-- 사용자 관심사 인덱스
CREATE INDEX idx_user_interests_user_id ON user_interests(user_id);
CREATE INDEX idx_user_interests_interest_id ON user_interests(interest_id);

-- ============================================
-- 트리거 함수
-- ============================================

-- 스킬 사용 카운터 업데이트
CREATE OR REPLACE FUNCTION update_skill_usage_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE skills SET usage_count = usage_count + 1 WHERE id = NEW.skill_id;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE skills SET usage_count = GREATEST(0, usage_count - 1) WHERE id = OLD.skill_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER user_skills_usage_counter 
  AFTER INSERT OR DELETE ON user_skills
  FOR EACH ROW EXECUTE FUNCTION update_skill_usage_count();

-- 관심사 사용 카운터 업데이트
CREATE OR REPLACE FUNCTION update_interest_usage_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE interests SET usage_count = usage_count + 1 WHERE id = NEW.interest_id;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE interests SET usage_count = GREATEST(0, usage_count - 1) WHERE id = OLD.interest_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER user_interests_usage_counter 
  AFTER INSERT OR DELETE ON user_interests
  FOR EACH ROW EXECUTE FUNCTION update_interest_usage_count();

-- updated_at 트리거
CREATE TRIGGER update_skills_updated_at 
  BEFORE UPDATE ON skills
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_skills_updated_at 
  BEFORE UPDATE ON user_skills
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_interests_updated_at 
  BEFORE UPDATE ON interests
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- 초기 데이터: 스킬
-- ============================================

INSERT INTO skills (name, slug, category, description) VALUES
  -- 프로그래밍 언어
  ('Java', 'java', 'Programming Language', 'Enterprise and Android development'),
  ('Python', 'python', 'Programming Language', 'General purpose, AI/ML, Data Science'),
  ('JavaScript', 'javascript', 'Programming Language', 'Web development, Full-stack'),
  ('TypeScript', 'typescript', 'Programming Language', 'Typed JavaScript'),
  ('Kotlin', 'kotlin', 'Programming Language', 'Modern JVM and Android'),
  ('Go', 'go', 'Programming Language', 'System programming, Cloud native'),
  ('Rust', 'rust', 'Programming Language', 'Systems programming, Performance'),
  ('C++', 'cpp', 'Programming Language', 'System programming, Games'),
  ('C#', 'csharp', 'Programming Language', '.NET development, Unity'),
  ('Swift', 'swift', 'Programming Language', 'iOS and macOS development'),
  
  -- 프론트엔드
  ('React', 'react', 'Frontend Framework', 'Popular JavaScript library'),
  ('Vue.js', 'vuejs', 'Frontend Framework', 'Progressive JavaScript framework'),
  ('Angular', 'angular', 'Frontend Framework', 'Enterprise web framework'),
  ('Next.js', 'nextjs', 'Frontend Framework', 'React framework for production'),
  ('Svelte', 'svelte', 'Frontend Framework', 'Cybernetically enhanced web apps'),
  ('HTML/CSS', 'html-css', 'Frontend', 'Web markup and styling'),
  ('Tailwind CSS', 'tailwind', 'Frontend', 'Utility-first CSS framework'),
  
  -- 백엔드
  ('Spring Boot', 'spring-boot', 'Backend Framework', 'Java enterprise framework'),
  ('Node.js', 'nodejs', 'Backend Framework', 'JavaScript runtime'),
  ('Django', 'django', 'Backend Framework', 'Python web framework'),
  ('FastAPI', 'fastapi', 'Backend Framework', 'Modern Python API framework'),
  ('Express.js', 'expressjs', 'Backend Framework', 'Node.js web framework'),
  ('NestJS', 'nestjs', 'Backend Framework', 'Progressive Node.js framework'),
  
  -- 데이터베이스
  ('PostgreSQL', 'postgresql', 'Database', 'Advanced relational database'),
  ('MySQL', 'mysql', 'Database', 'Popular relational database'),
  ('MongoDB', 'mongodb', 'Database', 'NoSQL document database'),
  ('Redis', 'redis', 'Database', 'In-memory data store'),
  ('Elasticsearch', 'elasticsearch', 'Database', 'Search and analytics engine'),
  
  -- DevOps & 클라우드
  ('Docker', 'docker', 'DevOps', 'Container platform'),
  ('Kubernetes', 'kubernetes', 'DevOps', 'Container orchestration'),
  ('AWS', 'aws', 'Cloud', 'Amazon Web Services'),
  ('Google Cloud', 'gcp', 'Cloud', 'Google Cloud Platform'),
  ('Azure', 'azure', 'Cloud', 'Microsoft Azure'),
  ('CI/CD', 'cicd', 'DevOps', 'Continuous Integration/Deployment'),
  ('GitHub Actions', 'github-actions', 'DevOps', 'Workflow automation'),
  
  -- 데이터 & AI
  ('Machine Learning', 'machine-learning', 'AI/ML', 'ML algorithms and models'),
  ('Deep Learning', 'deep-learning', 'AI/ML', 'Neural networks'),
  ('TensorFlow', 'tensorflow', 'AI/ML', 'ML framework'),
  ('PyTorch', 'pytorch', 'AI/ML', 'ML framework'),
  ('Data Science', 'data-science', 'Data', 'Data analysis and insights'),
  ('Big Data', 'big-data', 'Data', 'Large-scale data processing'),
  
  -- 모바일
  ('Android', 'android', 'Mobile', 'Android app development'),
  ('iOS', 'ios', 'Mobile', 'iOS app development'),
  ('React Native', 'react-native', 'Mobile', 'Cross-platform mobile'),
  ('Flutter', 'flutter', 'Mobile', 'Cross-platform UI toolkit'),
  
  -- 기타
  ('Git', 'git', 'Tools', 'Version control system'),
  ('Linux', 'linux', 'Operating System', 'Unix-like OS'),
  ('Agile/Scrum', 'agile-scrum', 'Methodology', 'Agile development'),
  ('GraphQL', 'graphql', 'API', 'Query language for APIs'),
  ('REST API', 'rest-api', 'API', 'RESTful web services'),
  ('Microservices', 'microservices', 'Architecture', 'Service-oriented architecture');

-- 스킬 별칭 추가
INSERT INTO skill_aliases (skill_id, alias) VALUES
  ((SELECT id FROM skills WHERE slug = 'javascript'), 'js'),
  ((SELECT id FROM skills WHERE slug = 'typescript'), 'ts'),
  ((SELECT id FROM skills WHERE slug = 'postgresql'), 'postgres'),
  ((SELECT id FROM skills WHERE slug = 'mongodb'), 'mongo'),
  ((SELECT id FROM skills WHERE slug = 'kubernetes'), 'k8s'),
  ((SELECT id FROM skills WHERE slug = 'machine-learning'), 'ml'),
  ((SELECT id FROM skills WHERE slug = 'deep-learning'), 'dl');

-- ============================================
-- 초기 데이터: 관심사 (기획/디자인/코딩 3가지만)
-- ============================================

INSERT INTO interests (name, slug, category, description) VALUES
  ('기획', 'planning', 'Role', '서비스 기획, 프로덕트 매니징'),
  ('디자인', 'design', 'Role', 'UI/UX 디자인, 그래픽 디자인'),
  ('코딩', 'coding', 'Role', '프로그래밍, 개발');
