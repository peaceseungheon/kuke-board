create table article (
  article_id BIGINT not null primary key,
  title VARCHAR(255) not null,
  content varchar(3000) not null,
  board_id BIGINT not null,
  writer_id BIGINT not null,
  created_at datetime not null,
  modified_at datetime not null
);