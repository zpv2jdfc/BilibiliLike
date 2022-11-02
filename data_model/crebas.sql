/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2022/11/2 16:31:24                           */
/*==============================================================*/


drop table if exists oauth;

drop table if exists tb_video;

drop table if exists tb_video_comment;

drop table if exists tb_video_detail;

drop table if exists user;

/*==============================================================*/
/* Table: oauth                                                 */
/*==============================================================*/
create table oauth
(
   id                   bigint not null auto_increment,
   user_id              bigint not null,
   identity_type        varchar(256) not null,
   identifier           varchar(256) not null,
   credential           varchar(256) not null,
   create_time          datetime not null default current_timestamp,
   lm_time              datetime not null default current_timestamp,
   primary key (id)
);

alter table oauth comment '验证登陆用户信息';

/*==============================================================*/
/* Table: tb_video                                              */
/*==============================================================*/
create table tb_video
(
   id                   bigint not null auto_increment,
   title                varchar(256) not null,
   tags                 varchar(256),
   user_id              bigint not null,
   duration             int not null,
   like_num             int(12) not null default 0,
   comment_num          int(12) not null default 0,
   preview              varchar(256) not null default "",
   release_time         datetime not null default current_timestamp,
   status               int not null default 0,
   create_time          datetime not null default current_timestamp,
   lm_time              datetime not null default current_timestamp,
   primary key (id)
);

alter table tb_video comment '视频资源基本信息';

/*==============================================================*/
/* Table: tb_video_comment                                      */
/*==============================================================*/
create table tb_video_comment
(
   id                   bigint not null auto_increment,
   video_id             bigint not null,
   user_id              bigint not null,
   parent_id            bigint not null,
   comment              text not null,
   like_num             int(12) not null default 0,
   comment_time         datetime not null default current_timestamp,
   create_time          datetime not null default current_timestamp,
   lm_time              datetime not null default current_timestamp,
   primary key (id)
);

/*==============================================================*/
/* Table: tb_video_detail                                       */
/*==============================================================*/
create table tb_video_detail
(
   id                   bigint not null auto_increment,
   video_id             bigint,
   location             varchar(256) not null,
   details              text,
   create_time          datetime not null default current_timestamp,
   lm_time              datetime not null default current_timestamp,
   primary key (id)
);

alter table tb_video_detail comment '视频资源的详细信息';

/*==============================================================*/
/* Table: user                                                  */
/*==============================================================*/
create table user
(
   id                   bigint not null auto_increment,
   user_nickname        varchar(255) not null,
   user_avatar          varchar(2048) not null default "default",
   user_singature       varchar(128) not null default "",
   user_level           int not null default 0,
   user_privilege       int not null default 0,
   user_status          int not null default 0,
   user_setting         varchar(64) not null default "",
   create_time          datetime not null default current_timestamp,
   lm_time              datetime not null default current_timestamp,
   primary key (id)
);

alter table user comment '用户最基本的信息，不包含登录信息';

alter table oauth add constraint fk_oauth_user foreign key (user_id)
      references user (id) on delete restrict on update restrict;

alter table tb_video_comment add constraint fk_comment_user foreign key (user_id)
      references user (id) on delete restrict on update restrict;

alter table tb_video_comment add constraint fk_comment_video foreign key (video_id)
      references tb_video (id) on delete restrict on update restrict;

alter table tb_video_detail add constraint fk_detail_video foreign key (video_id)
      references tb_video (id) on delete restrict on update restrict;

