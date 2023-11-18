ALTER TABLE group_users
    ADD is_pinned BIT NOT NULL DEFAULT 0,
        is_muted BIT NOT NULL DEFAULT 0;
ALTER TABLE group_users DROP COLUMN stealth_choice;

ALTER TABLE friendship
    ADD stealth_choice_user_one varchar(20) NOT NULL DEFAULT 'PRECISE',
        stealth_choice_user_two varchar(20) NOT NULL DEFAULT 'PRECISE';

EXEC sp_rename 'friendship', 'relationship';
EXEC sp_rename 'relationship.friend_one', 'user_one', 'COLUMN';
EXEC sp_rename 'relationship.friend_two', 'user_two', 'COLUMN';

ALTER TABLE [user] ADD registration_token VARCHAR(255);
ALTER TABLE [user] ALTER COLUMN stealth_choice varchar(20) NOT NULL;
ALTER TABLE [user] ADD CONSTRAINT DF_user_StealthChoice DEFAULT 'PRECISE' FOR stealth_choice;

