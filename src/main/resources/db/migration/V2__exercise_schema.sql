CREATE TABLE EXERCISES (
    exercise_id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    is_custom BOOLEAN NOT NULL,
    owner_id UUID,

    CONSTRAINT fk_owner FOREIGN KEY(owner_id) REFERENCES Users(user_id) ON DELETE CASCADE
)