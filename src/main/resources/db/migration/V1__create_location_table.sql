CREATE TABLE location (
    id int primary key IDENTITY(1, 1),
    user_id varchar(50),
    longitude double precision,
    latitude double precision
)