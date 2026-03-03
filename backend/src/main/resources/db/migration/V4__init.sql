-- ============================================================
-- Step 1: Drop FK from skills that references old goals
--         Values of cooldown_goal_id are preserved: they will
--         reference user_goal_status(id) after migration (1:1)
-- ============================================================
ALTER TABLE skills DROP FOREIGN KEY fk_skills_goal;

-- ============================================================
-- Step 2: Preserve old data in temp tables
-- ============================================================
RENAME TABLE goals TO _old_goals;
RENAME TABLE suggested_goals TO _old_suggested_goals;

-- ============================================================
-- Step 3: Create new goals (goal definitions, replaces suggested_goals)
-- ============================================================
CREATE TABLE goals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    action TEXT,
    target BIGINT NOT NULL,
    type ENUM('EXERCISE', 'SLEEP', 'MEDITATION', 'NUTRITION', 'PRODUCTIVITY') NOT NULL,
    category ENUM('DAILY_CHALLENGES', 'APP_USAGE', 'SKILL') NOT NULL,
    reward INT NOT NULL
);

-- ============================================================
-- Step 4: Migrate suggested_goals -> new goals
--         (name was empty in V1, so fall back to action)
-- ============================================================
INSERT INTO goals (name, action, target, type, category, reward)
SELECT
    COALESCE(NULLIF(name, ''), action, 'Unknown'),
    action,
    target,
    type,
    category,
    reward
FROM _old_suggested_goals;

-- ============================================================
-- Step 5: Create user_goal_status (replaces old goals)
--         IDs are preserved explicitly so skills.cooldown_goal_id
--         keeps pointing to the correct rows (1:1 mapping)
-- ============================================================
CREATE TABLE user_goal_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    goal_id BIGINT NOT NULL,
    status ENUM('COMPLETED', 'NOT_COMPLETED', 'IN_PROGRESS') NOT NULL,
    date DATE NOT NULL,
    value BIGINT NOT NULL,
    CONSTRAINT fk_user_goal_status_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_goal_status_goal
        FOREIGN KEY (goal_id) REFERENCES goals(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_user_goal_date (user_id, date)
);

-- ============================================================
-- Step 6: Migrate old goals -> user_goal_status
--         id is copied explicitly to preserve references from skills
-- ============================================================
INSERT INTO user_goal_status (id, user_id, goal_id, status, date, value)
SELECT
    og.id,
    og.user_id,
    g.id,
    og.status,
    og.date,
    og.value
FROM _old_goals og
JOIN goals g
  ON g.target   = og.target
 AND g.type     = og.type
 AND g.category = og.category
 AND g.reward   = og.reward;

-- ============================================================
-- Step 7: Re-add FK on skills now pointing to user_goal_status
--         (cooldown is based on per-user goal progress, not definitions)
-- ============================================================
ALTER TABLE skills
    ADD CONSTRAINT fk_skills_goal
        FOREIGN KEY (cooldown_goal_id) REFERENCES user_goal_status(id)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- ============================================================
-- Step 8: Drop temp tables
-- ============================================================
DROP TABLE _old_goals;
DROP TABLE _old_suggested_goals;