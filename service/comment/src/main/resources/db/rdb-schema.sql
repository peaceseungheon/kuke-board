create table comment (
  comment_id bigint not null primary key,
  content varchar(3000) not null,
  article_id bigint not null,
  parent_comment_id bigint not null,
  writer_id bigint not null,
  deleted bool not null,
  created_at datetime not null
);

create table comment_v2 (
    comment_id bigint not null primary key,
    content varchar(3000) not null,
    article_id bigint not null,
    writer_id bigint not null,
    path varchar(25) character set utf8mb4 collate utf8mb4_bin not null,
    deleted bool not null,
    created_at datetime not null
);