CREATE TABLE user_status (
    id BIGINT PRIMARY KEY IDENTITY(1, 1),
    uid BIGINT REFERENCES [user](id),
    position_lat DOUBLE PRECISION,
    position_lon DOUBLE PRECISION,
    speed DOUBLE PRECISION,
    is_shaking BIT NOT NULL DEFAULT (0),
    battery_level INT,
    is_online BIT NOT NULL DEFAULT (0),
    timestamp SMALLDATETIME,
    version BIGINT NOT NULL DEFAULT 1
);

ALTER TABLE [user] DROP COLUMN position_lat, position_lon, is_shaking;
