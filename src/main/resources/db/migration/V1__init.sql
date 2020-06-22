drop table if exists GLOBAL_PROPERTY CASCADE;
drop table if exists MESSAGE CASCADE;
drop table if exists PROPERTY CASCADE;
drop sequence if exists hibernate_sequence;

create sequence hibernate_sequence start with 1 increment by 1;

create table GLOBAL_PROPERTY
(
    id      bigint       not null,
    key     varchar(255) not null,
    value   varchar(512) not null,
    name    varchar(255),
    desc    varchar(1024),
    created timestamp,
    updated timestamp,
    primary key (id)
);
create table MESSAGE
(
    id      bigint        not null,
    key     varchar(255)  not null,
    title   varchar(255)  not null,
    message varchar(1024) not null,
    created timestamp,
    updated timestamp,
    primary key (id)
);
create table PROPERTY
(
    id         bigint        not null,
    key        varchar(255)  not null,
    name       varchar(255),
    type       varchar(255),
    value      varchar(1024) not null,
    message_id bigint,
    desc       varchar(255),
    created    timestamp,
    updated    timestamp,
    primary key (id)
);

create table SEND_HISTORY
(
    id              bigint        not null,
    condition_type  varchar(255)  not null,
    condition_value varchar(255)  not null,
    created         timestamp,
    from_user_id    varchar(255)  not null,
    message_content varchar(1024) not null,
    message_title   varchar(255),
    to_user_id      varchar(255)  not null,
    updated         timestamp,
    primary key (id)
);

alter table PROPERTY
    add constraint FK2unehkx69go0sj92j1fs22r1w foreign key (message_id) references message;


INSERT INTO GLOBAL_PROPERTY (ID, CREATED, DESC, KEY, NAME, UPDATED, VALUE)
VALUES (1, '2020-06-10 22:00:53.143000', null, 'interval', 'interval', '2020-06-11 02:03:13.306000', '30');
INSERT INTO GLOBAL_PROPERTY (ID, CREATED, DESC, KEY, NAME, UPDATED, VALUE)
VALUES (2, '2020-06-11 23:53:48.967000', null, 'onOff', 'On Off', '2020-06-11 23:53:48.967000', 'OFF');

INSERT INTO GLOBAL_PROPERTY (ID, CREATED, DESC, KEY, NAME, UPDATED, VALUE)
VALUES (3, '2020-06-11 23:53:48.967000', null, 'senderId', 'Sender Id', '2020-06-11 23:53:48.967000', 'f5baedec-e6d9-4ae2-80db-c33ef7714e04');
INSERT INTO GLOBAL_PROPERTY (ID, CREATED, DESC, KEY, NAME, UPDATED, VALUE)
VALUES (4, '2020-06-11 23:53:48.967000', null, 'accessToken', 'Sender Id', '2020-06-11 23:53:48.967000', 'C75283491EE20CD61164C98D8D096A29C3698804010A5F417750BD6CA5B7B760');
INSERT INTO GLOBAL_PROPERTY (ID, CREATED, DESC, KEY, NAME, UPDATED, VALUE)
VALUES (5, '2020-06-11 23:53:48.967000', null, 'dayBefore', 'Day Before', '2020-06-11 23:53:48.967000', '10');

INSERT INTO MESSAGE (ID, CREATED, KEY, MESSAGE, TITLE, UPDATED)
VALUES (4, '2020-06-10 22:00:53.165000', 'COUNT', 'COUNT Message
이것은 카운트 메세지
입니다.', '메세지 제목', '2020-06-11 00:08:21.144000');
INSERT INTO MESSAGE (ID, CREATED, KEY, MESSAGE, TITLE, UPDATED)
VALUES (6, '2020-06-10 22:00:53.169000', 'MONEY', 'MONEY message 1-2-3-4 하나둘셋.', '메세지 제목', '2020-06-12 00:08:52.006000');

INSERT INTO PROPERTY (ID, CREATED, DESC, KEY, NAME, TYPE, UPDATED, VALUE, MESSAGE_ID)
VALUES (5, '2020-06-10 22:00:53.166000', null, 'COUNT', 'COUNT', 'COUNT', '2020-06-11 23:59:38.773000', '1,5,10', 4);
INSERT INTO PROPERTY (ID, CREATED, DESC, KEY, NAME, TYPE, UPDATED, VALUE, MESSAGE_ID)
VALUES (7, '2020-06-10 22:00:53.169000', null, 'MONEY', 'MONEY', 'MONEY', '2020-06-10 22:00:53.169000', '60000,200000', 6);
