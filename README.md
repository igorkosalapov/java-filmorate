# Filmorate

Filmorate — сервис, позволяющий пользователям оценивать фильмы, ставить лайки, добавлять их в базу, просматривать популярные фильмы и дружить с другими пользователями.
#
##  Диаграмма базы данных Filmorate


 Ниже представлена ER-диаграмма базы данных, отражающая структуру сервиса Filmorate:

# 

# ![Диаграмма Filmorate](er-diagram.png)



 Диаграмма показывает связи между сущностями: фильмы, пользователи, жанры, рейтинги, лайки и дружба.  

 Эта структура обеспечивает корректное хранение данных и выполнение основных операций приложения.

#

 ## Примеры запросов к базе данных


 ### 📌 1. Добавить нового пользователя

 ```sql
INSERT INTO users (email, login, name, birthday)
VALUES ('user@example.com', 'superlogin', 'Иван Иванов', '1990-05-10');
```
 ### 📌 2. Добавить добавить фильм
 ```sql
INSERT INTO films (name, description, release_date, duration, rating_id)
VALUES ('Матрица', 'Фильм о выборе реальности', '1999-03-31', 136, 1);
```
### 📌 3. Получить топ-фильмы по количеству лайков
```sql
SELECT f.*, COUNT(fl.user_id) AS like_count
FROM films f
LEFT JOIN film_likes fl ON f.film_id = fl.film_id
GROUP BY f.film_id
ORDER BY like_count DESC
LIMIT 10;
```

