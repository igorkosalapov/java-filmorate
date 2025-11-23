# О проекте Filmorate

**Filmorate** — сервис, где пользователи могут оценивать фильмы, добавлять друзей и смотреть самые популярные фильмы.
Проект хранит пользователей, фильмы, лайки, жанры, рейтинги MPA и статусы дружбы.
---
## Схема базы данных
Ниже представлена ER-диаграмма базы данных Filmorate:

![Filmorate Database Diagram](er-diagram)
---
## Краткое пояснение к схеме
- **films** — информация о фильмах + рейтинг MPA.
- **genres** и **film_genres** — жанры фильма (многие-ко-многим).
- **mpa** — возрастные рейтинги.
- **users** — данные пользователей.
- **friendships** — дружба со статусами *UNCONFIRMED* / *CONFIRMED*.
- **likes** — лайки фильмов.
---
## Примеры SQL-запросов
### 1. Получить все фильмы
```sql

SELECT *

FROM films;
```
### 2. Топ-10 лучших фильмов
```sql

SELECT film_id,

  COUNT(user_id) AS likes

FROM likes

GROUP BY film_id

ORDER BY likes DESC

LIMIT 10;
```
