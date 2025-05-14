#!/usr/bin/bash
set -e

TMP_MY_CNF="/tmp/.my.cnf"

cat <<EOF > ${TMP_MY_CNF}
[client]
user=root
password=${MYSQL_ROOT_PASSWORD}
EOF

chmod 600 ${TMP_MY_CNF}

cat <<EOF > /tmp/init-db.sql
CREATE DATABASE IF NOT EXISTS ${MYSQL_DATABASE};

USE ${MYSQL_DATABASE};

CREATE TABLE IF NOT EXISTS user_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    are_notifications_enabled BOOLEAN NOT NULL,
    theme VARCHAR(255) NOT NULL,

    CONSTRAINT user_settings_check_email_format CHECK (
        email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'
    )
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    CONSTRAINT users_check_email_format CHECK (
        email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'
    ),

    CONSTRAINT fk_user_email_to_settings FOREIGN KEY (email) REFERENCES user_settings(email)
);

CREATE USER '${BACKEND_DB_USERNAME}'@'%' IDENTIFIED BY '${BACKEND_DB_PASSWORD}';
GRANT ALL PRIVILEGES ON ${MYSQL_DATABASE}.* TO '${BACKEND_DB_USERNAME}'@'%';

CREATE ROLE '${TEAM_ROLE}';
GRANT SELECT, INSERT ON ${MYSQL_DATABASE}.* TO '${TEAM_ROLE}';

CREATE USER '${TEAM_01_USERNAME}'@'%' IDENTIFIED BY '${TEAM_01_PASSWORD}';
CREATE USER '${TEAM_02_USERNAME}'@'%' IDENTIFIED BY '${TEAM_02_PASSWORD}';
CREATE USER '${TEAM_03_USERNAME}'@'%' IDENTIFIED BY '${TEAM_03_PASSWORD}';

GRANT ${TEAM_ROLE} TO '${TEAM_01_USERNAME}'@'%';
GRANT ${TEAM_ROLE} TO '${TEAM_02_USERNAME}'@'%';
GRANT ${TEAM_ROLE} TO '${TEAM_03_USERNAME}'@'%';

FLUSH PRIVILEGES;

EOF

mysql --defaults-extra-file=${TMP_MY_CNF} < /tmp/init-db.sql

rm -f /tmp/init-db.sql
rm -f ${TMP_MY_CNF}