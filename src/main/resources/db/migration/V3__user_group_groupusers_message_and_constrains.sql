CREATE TABLE [user] (
    id BIGINT PRIMARY KEY IDENTITY(1, 1),
    nickname VARCHAR(255) NOT NULL,
    position_lat DOUBLE PRECISION,
    position_lon DOUBLE PRECISION,
    is_shaking BIT NOT NULL,
    stealth_choice TINYINT NOT NULL,
    profile_picture VARCHAR(255)
    );

CREATE TABLE [group] (
    id BIGINT PRIMARY KEY IDENTITY(1, 1),
    name VARCHAR(50) NOT NULL,
    is_friendship BIT NOT NULL,
    description VARCHAR(1000) NULL,
    group_picture VARCHAR(255) NULL
    );

CREATE TABLE messages (
    id BIGINT PRIMARY KEY IDENTITY(1, 1),
    sender_id BIGINT NOT NULL ,
    group_id BIGINT NOT NULL,
    text VARCHAR(255) NOT NULL,
    time_sent DATETIME NOT NULL
);

CREATE TABLE group_users (
    id BIGINT PRIMARY KEY IDENTITY(1, 1),
    user_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    stealth_choice TINYINT NOT NULL
);

ALTER TABLE messages
    ADD CONSTRAINT FK_Messages_SenderId FOREIGN KEY (sender_id) REFERENCES [user](id);

ALTER TABLE messages
    ADD CONSTRAINT FK_Messages_GroupId FOREIGN KEY (group_id) REFERENCES [group](id);

ALTER TABLE group_users
    ADD CONSTRAINT FK_GroupUsers_UId FOREIGN KEY (user_id) REFERENCES [user](id);

ALTER TABLE group_users
    ADD CONSTRAINT FK_GroupUsers_GroupId FOREIGN KEY (group_id) REFERENCES [group](id);


