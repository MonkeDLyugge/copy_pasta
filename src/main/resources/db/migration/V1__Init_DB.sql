create table app_paste (
                           id  bigserial not null,
                           access varchar(255),
                           cancel_date timestamp,
                           text varchar(2048),
                           primary key (id)
)