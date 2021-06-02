create table user_table
(
    id INTEGER not null
        constraint user_table_pk
            primary key autoincrement,
    email TEXT not null,
    login text,
    hashed_password text not null
);

create unique index user_table_email_uindex
    on user_table (email);

create unique index user_table_id_uindex
    on user_table (id);

create unique index user_table_login_uindex
    on user_table (login);

create table database_table
(
    id      integer not null
        constraint database_name_pk
            primary key autoincrement,
    user_id int     not null
        references user_table,
    type    text    not null,
    configs text    not null
);

create unique index database_name_id_uindex
    on database_table (id);

