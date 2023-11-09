ALTER TABLE [user] ADD email varchar(50)
CREATE TABLE friendship (
    id BIGINT PRIMARY KEY IDENTITY(1, 1),
    friend_one BIGINT references [user](id),
    friend_two BIGINT references [user](id),
    status varchar(20)
)