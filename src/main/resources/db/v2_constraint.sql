
ALTER TABLE "categories" ADD FOREIGN KEY ("parent_cate_id") REFERENCES "categories" ("id");

ALTER TABLE "blindbox_series" ADD FOREIGN KEY ("category_id") REFERENCES "categories" ("id");

ALTER TABLE "blindbox_assets" ADD FOREIGN KEY ("blindbox_id") REFERENCES "blindbox_series" ("id");

ALTER TABLE "orders" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "orders" ADD FOREIGN KEY ("campaign_id") REFERENCES "campaigns" ("id");

ALTER TABLE "order_item" ADD FOREIGN KEY ("order_id") REFERENCES "orders" ("id");

ALTER TABLE "order_item" ADD FOREIGN KEY ("blindbox_unit_id") REFERENCES "blindbox_unit" ("id");

ALTER TABLE "transactions" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "transactions" ADD FOREIGN KEY ("order_id") REFERENCES "orders" ("id");

ALTER TABLE "transactions" ADD FOREIGN KEY ("related_transaction_id") REFERENCES "transactions" ("id");

ALTER TABLE "campaigns" ADD FOREIGN KEY ("blindbox_series_id") REFERENCES "blindbox_series" ("id");

ALTER TABLE "campaign_tiers" ADD FOREIGN KEY ("campaign_id") REFERENCES "campaigns" ("id");

ALTER TABLE "blindbox_unit" ADD FOREIGN KEY ("blindbox_series_id") REFERENCES "blindbox_series" ("id");

ALTER TABLE "orders" ADD FOREIGN KEY ("status") REFERENCES "orders" ("total_price");
