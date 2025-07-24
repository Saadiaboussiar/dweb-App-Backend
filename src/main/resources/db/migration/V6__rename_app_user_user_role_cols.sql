ALTER TABLE app_user_user_roles
RENAME COLUMN user_id TO app_user_id;

ALTER TABLE app_user_user_roles
RENAME COLUMN role_id TO app_role_id;