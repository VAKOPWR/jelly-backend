ALTER TABLE messages
    ADD status varchar(10) NOT NULL DEFAULT 'SENT',
        attached_photo varchar(255);


    EXEC sp_rename 'group_users', 'group_user';
