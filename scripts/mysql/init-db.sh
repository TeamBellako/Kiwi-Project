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

CREATE USER '${BACKEND_DB_USERNAME}'@'%' IDENTIFIED BY '${BACKEND_DB_PASSWORD}';
GRANT ALL PRIVILEGES ON ${MYSQL_DATABASE}.* TO '${BACKEND_DB_USERNAME}'@'%';

FLUSH PRIVILEGES;

EOF

mysql --defaults-extra-file=${TMP_MY_CNF} < /tmp/init-db.sql

rm -f /tmp/init-db.sql
rm -f ${TMP_MY_CNF}