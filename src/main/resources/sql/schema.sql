create table if not exists PUBLIC.ratings
(
    rating_id   INTEGER auto_increment
        primary key,
    RATING_NAME CHARACTER VARYING(10)
);

create table if not exists PUBLIC.GENRES
(
    GENRE_ID   INTEGER auto_increment
        primary key,
    GENRE_NAME CHARACTER VARYING(20)
);

create table if not exists PUBLIC.FILMS
(
    FILM_ID          BIGINT auto_increment
        primary key,
    FILM_NAME        CHARACTER VARYING(150),
    FILM_DESCRIPTION CHARACTER VARYING(200),
    RATING_ID        INTEGER,
    RELEASE_DATE     TIMESTAMP,
    DURATION         INTEGER,
    constraint FK_FILM_RATING
        foreign key (RATING_ID) references PUBLIC.RATINGS
);

create table if not exists PUBLIC.GENRES_FILMS
(
    FILM_ID   BIGINT not null,
    GENRE_ID  INTEGER not null,
    constraint PK_GENRE_FILMS
        primary key (FILM_ID, GENRE_ID),
    constraint FK_GENRE_FILMS_FILM
        foreign key (FILM_ID) references PUBLIC.FILMS,
    constraint FK_GENRE_FILMS_GENRE
        foreign key (GENRE_ID) references PUBLIC.GENRES
);

create table if not exists PUBLIC.USERS
(
    USER_ID   BIGINT auto_increment
        primary key,
    EMAIL     CHARACTER VARYING(100),
    LOGIN     CHARACTER VARYING(100),
    USER_NAME CHARACTER VARYING(100),
    BIRTHDAY  TIMESTAMP
);

create table if not exists PUBLIC.RELATIONSHIP_USERS
(
    USER_ID   BIGINT not null,
    FRIEND_ID BIGINT not null,
    constraint PK_RELATIONSHIP_USERS
        primary key (USER_ID, FRIEND_ID),
    constraint FK_RELATIONSHIP_USERS_USER_ID
        foreign key (USER_ID) references PUBLIC.USERS,
    constraint FK_RELATIONSHIP_USERS_FRIEND_ID
        foreign key (FRIEND_ID) references PUBLIC.USERS
);

create table if not exists PUBLIC.LIKES_FILMS
(
    FILM_ID BIGINT not null,
    USER_ID BIGINT not null,
    constraint PK_LIKES_FILMS
        primary key (FILM_ID, USER_ID),
    constraint FK_LIKES_FILMS_FILM_ID
        foreign key (FILM_ID) references PUBLIC.FILMS,
    constraint FK_LIKES_FILMS_USER_ID
        foreign key (USER_ID) references PUBLIC.USERS
);

create table if not exists PUBLIC.DIRECTORS
(
    DIRECTOR_ID BIGINT auto_increment
        primary key,
    DIRECTOR_NAME CHARACTER VARYING(100)
)
