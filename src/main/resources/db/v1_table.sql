CREATE TABLE "users"
(
    "id"            BIGSERIAL PRIMARY KEY NOT NULL,
    "email"         varchar(50) UNIQUE   NOT NULL,
    "password"      varchar(30)           NOT NULL,
    "name"          varchar(30) NOT NULL,
    "gender"        varchar                NOT NULL,
    "avatar"        varchar(255),
    "phone"         varchar(10) UNIQUE    NOT NULL,
    "date_of_birth" bigint,
    "role_name"     role                  NOT NULL,
    "is_active"     boolean               NOT NULL DEFAULT true,
    "created_at"    bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric),
    "updated_at"    bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric)
);

CREATE TABLE "categories"
(
    "id"             BIGSERIAL PRIMARY KEY NOT NULL,
    "category_name"  varchar(50) NOT NULL,
    "parent_cate_id" int,
    "is_active"      boolean               NOT NULL DEFAULT true,
    "created_at"     bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric),
    "updated_at"     bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric)
);

CREATE TABLE "blindbox_series"
(
    "id"          BIGSERIAL PRIMARY KEY NOT NULL,
    "series_name" varchar(100) NOT NULL,
    "description" text,
    "opened_at"   timestamp,
    "category_id" int,
    "is_active"   boolean               NOT NULL DEFAULT true,
    "created_at"  bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric),
    "updated_at"  bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric)
);

CREATE TABLE "blindbox_assets"
(
    "id"          BIGSERIAL PRIMARY KEY NOT NULL,
    "media_key"   varchar(100)          NOT NULL,
    "blindbox_id" int                   NOT NULL,
    "is_active"   boolean               NOT NULL DEFAULT true,
    "created_at"  bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric),
    "updated_at"  bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric)
);

CREATE TABLE "orders"
(
    "id"            BIGSERIAL PRIMARY KEY NOT NULL,
    "user_id"       int                   NOT NULL,
    "campaign_id"   int                   NOT NULL,
    "order_code"    varchar(20)           NOT NULL,
    "delivery_code" varchar(20),
    "user_address"  varchar(200),
    "status"        order_status,
    "total_price"   decimal(10, 2),
    "is_preorder"   boolean               NOT NULL DEFAULT false,
    "is_active"     boolean               NOT NULL DEFAULT true,
    "created_at"    bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric),
    "updated_at"    bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric)
);

CREATE TABLE "order_item"
(
    "id"               BIGSERIAL PRIMARY KEY NOT NULL,
    "order_id"         int                   NOT NULL,
    "blindbox_unit_id" int                   NOT NULL,
    "price"            decimal(10, 2)        NOT NULL,
    "quantity"         int                   NOT NULL,
    "is_active"        boolean               NOT NULL DEFAULT true,
    "created_at"       bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric),
    "updated_at"       bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric)
);

CREATE TABLE "transactions"
(
    "id"                     BIGSERIAL PRIMARY KEY NOT NULL,
    "user_id"                int                   NOT NULL,
    "order_id"               int                   NOT NULL ,
    "transaction_code"       varchar(20) UNIQUE    NOT NULL,
    "transaction_type"       varchar(20)      NOT NULL,
    "content"                varchar(200),
    "transaction_amount"     decimal(10, 2)        NOT NULL,
    "transaction_status"     varchar(20)    NOT NULL,
    "is_deposit"             boolean               NOT NULL DEFAULT false,
    "related_transaction_id" bigint,
    "is_active"              boolean               NOT NULL DEFAULT true,
    "created_at"             bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric),
    "updated_at"             bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric)
);

CREATE TABLE "campaigns"
(
    "id"                       BIGSERIAL PRIMARY KEY NOT NULL,
    "blindbox_series_id"       int                   NOT NULL,
    "campaign_type"            varchar         NOT NULL,
    "start_campaign_time"      bigint              NOT NULL,
    "end_campaign_time"        bigint              NOT NULL,
    "current_placed_blindbox"  int                   NOT NULL,
    "target_blindbox_quantity" int                   NOT NULL,
    "deposit_percent"          int,
    "base_price"               decimal(10, 2)        NOT NULL,
    "locked_price"             decimal(10, 2),
    "is_active"                boolean               NOT NULL DEFAULT true,
    "created_at"               bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric),
    "updated_at"               bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric)
);

CREATE TABLE "campaign_tiers"
(
    "id"               BIGSERIAL PRIMARY KEY NOT NULL,
    "campaign_id"      int                   NOT NULL,
    "tier_name"        (50),
    "min_quantity"     int                   NOT NULL,
    "max_quantity"     int,
    "discount_percent" int                   NOT NULL,
    "is_active"        boolean               NOT NULL DEFAULT true,
    "created_at"       bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric),
    "updated_at"       bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric)
);

CREATE TABLE "blindbox_unit"
(
    "id"                   BIGSERIAL PRIMARY KEY NOT NULL,
    "title"                varchar(50),
    "quantity_per_package" int                   NOT NULL,
    "stock_quantity"       int,
    "price"                decimal(10, 2),
    "discount_percent"     int,
    "blindbox_series_id"   int,
    "is_active"            boolean               NOT NULL DEFAULT true,
    "created_at"           bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric),
    "updated_at"           bigint                NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric)
);