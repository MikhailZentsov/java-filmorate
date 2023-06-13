# java-filmorate

Технологии: Java + Spring Boot + Maven + JUnit + RESTful API + JDBC


Данный проект представляет собой бэкенд для сервиса, который работает с фильмами и оценками пользователей и рекомендует фильмы к просмотру.

Основная задача приложения - решить проблему поиска фильмов на вечер. С его помощью вы можете легко найти фильм, который вам понравится.

### Реализованы следующие эндпоинты:

#### 1. Фильмы
+ POST /films - создание фильма

+ PUT /films - редактирование фильма

+ GET /films - получение списка всех фильмов

+ GET /films/{id} - получение информации о фильме по его id

+ PUT /films/{id}/like/{userId} — поставить лайк фильму

+ DELETE /films/{id}/like/{userId} — удалить лайк фильма

+ DELETE /films/{id} - удаление фильма по id

+ GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, возвращает первые 10.

+ GET /films/search?query={query}?by={by} - поиск фильмов по заголовку и режиссеру

+ GET /films/director/directorId={directorId}?sortBy={sortBy} - получение всех фильмов режиссера с сортировкой по лайкам или годам

+ GET /films/common?userId={userId}?friendId={friendId} - получение общих фильмов пользователя и его друга

#### 2. Пользователи

+ POST /users - создание пользователя

+ PUT /users - редактирование пользователя

+ GET /users - получение списка всех пользователей

+ DELETE /users/{userId} - удаление пользователя по id

+ GET /users/{id} - получение данных о пользователе по id

+ PUT /users/{id}/friends/{friendId} — добавление в друзья

+ DELETE /users/{id}/friends/{friendId} — удаление из друзей

+ GET /users/{id}/friends — возвращает список друзей

+ GET /users/{id}/friends/common/{otherId} — возвращает список друзей, общих с другим пользователем

+ GET /users/{id}/recommendations - получение рекомендаций по фильмам

+ GET /users/{id}/feed - возвращает ленту событий пользователя

#### 3. Режиссеры

+ POST /directors - создание режиссера

+ GET /directors - получение списка всех режиссеров

+ GET /directors/{id} - получение режиссера по id

+ PUT /directors - обновление режиссера

+ DELETE /directors/{id} - удаление режиссера по id

#### 4. Жанры

+ GET /genres - получение списка всех жанров

+ GET /genres/{id} - получение жанра по id

#### 5. MPA рейтинг

+ GET /mpa - получение списка всех рейтингов

+ GET /mpa/{id} - получение рейтинга по id

#### 6. Отзывы

+ POST /reviews - создание отзыва

+ PUT /reviews - обновление отзыва

+ DELETE /reviews/{id} - удаление отзыва по id

+ GET /reviews/{id} - получение отзыва по id

+ GET /reviews?filmId={filmId}?count={count} - получение count отзывов по id фильма. Если значение не задано, возвращает первые 10

#### 7. Лайки

+ PUT /reviews/{id}/like/{userId} - добавление лайка

+ PUT /reviews/{id}/dislike/{userId} - добавление дизлайка

+ DELETE /reviews/{id}/like{userId} - удаление лайка

+ DELETE /reviews{id}/dislike{userId} - удаление дизлайка




Схема базы данных:
![filmorate.png](/src/main/resources/images/filmorate.png)
