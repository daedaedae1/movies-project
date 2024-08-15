drop table [테이블 명];

create table user(
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   userid   varchar(40)    not null,
   pwd      varchar(100)   not null,
   name     varchar(40)    not null
);

create table movie(
   id BIGINT    AUTO_INCREMENT  PRIMARY KEY,
   title        varchar(100)    not null,
   overview     MEDIUMTEXT      not null,
   poster_path  varchar(255)    not null,
   tmdb_id      VARCHAR(255)    not null
);

* 속성 변경
ALTER TABLE movie MODIFY overview MEDIUMTEXT;

* 모든 열 삭제
delete from [테이블 명];

CREATE TABLE genre(
   id INT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(100) NOT NULL
);

CREATE TABLE movie_genre(
   movie_id BIGINT NOT NULL,
   genre_id INT NOT NULL,
   PRIMARY KEY(movie_id, genre_id),
   FOREIGN KEY(movie_id) REFERENCES movie(id),
   FOREIGN KEY(genre_id) REFERENCES genre(id)
);

CREATE TABLE viewing_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (movie_id) REFERENCES movie(id)
);
