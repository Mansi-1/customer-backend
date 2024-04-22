FROM flyway/flyway:latest

COPY src/main/resources/db/changelog /flyway/sql
COPY ./flyway.conf /flyway/conf/flyway.conf
