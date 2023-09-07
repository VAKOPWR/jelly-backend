CREATE TABLE location (
    id int primary key IDENTITY(1, 1),
    userId varchar(50),
    longitude double precision,
    latitude double precision
)