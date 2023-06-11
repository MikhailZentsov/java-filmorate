create table if not exists PUBLIC.ratings
(
    rating_id   INTEGER GENERATED BY DEFAULT AS IDENTITY
        primary key,
    RATING_NAME CHARACTER VARYING(10)
);

create table if not exists PUBLIC.GENRES
(
    GENRE_ID   INTEGER GENERATED BY DEFAULT AS IDENTITY
        primary key,
    GENRE_NAME CHARACTER VARYING(20)
);

create table if not exists PUBLIC.FILMS
(
    FILM_ID          BIGINT GENERATED BY DEFAULT AS IDENTITY
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

create table if not exists PUBLIC.DIRECTORS
(
    DIRECTOR_ID BIGINT GENERATED BY DEFAULT AS IDENTITY
        primary key,
    DIRECTOR_NAME CHARACTER VARYING(100)
);

create table if not exists PUBLIC.DIRECTORS_FILMS
(
    DIRECTOR_ID BIGINT not null,
    FILM_ID BIGINT not null,
    constraint PK_DIRECTORS_FILM
        primary key (DIRECTOR_ID, FILM_ID),
    constraint FK_DIRECTORS_FILMS_DIRECTOR_ID
        foreign key (DIRECTOR_ID) references PUBLIC.DIRECTORS ON
            DELETE CASCADE ON UPDATE CASCADE,
    constraint FK_DIRECTORS_FILMS_FILM_ID
        foreign key (FILM_ID) references PUBLIC.FILMS ON
            DELETE CASCADE ON UPDATE CASCADE
);

create table if not exists PUBLIC.USERS
(
    USER_ID   BIGINT GENERATED BY DEFAULT AS IDENTITY
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

create table if not exists PUBLIC.REVIEWS
(
    REVIEW_ID BIGINT GENERATED BY DEFAULT AS IDENTITY
        primary key,
    CONTENT CHARACTER VARYING not null,
    IS_POSITIVE BOOLEAN,
    FILM_ID BIGINT not null,
    USER_ID BIGINT not null,
    constraint FK_REVIEWS_FILM_ID
        foreign key (FILM_ID) references PUBLIC.FILMS on delete cascade,
    constraint FK_REVIEWS_USER_ID
        foreign key (USER_ID) references PUBLIC.USERS on delete cascade
);

create table if not exists PUBLIC.REVIEW_REACTION
(
    REVIEW_ID BIGINT not null,
    USER_ID BIGINT not null,
    REACTION BIGINT not null,
    constraint PK_REVIEW_REACTION primary key (REVIEW_ID, USER_ID),
    constraint FK_REVIEW_REACTION_REVIEW_ID
        foreign key (REVIEW_ID) references PUBLIC.REVIEWS on delete cascade,
    constraint FK_REVIEW_REACTION_USER_ID
        foreign key (USER_ID) references PUBLIC.USERS on delete cascade
);