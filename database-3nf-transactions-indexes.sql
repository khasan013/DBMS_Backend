-- Campus Crate: 3NF, marketplace transaction, indexes, and EXPLAIN verification
-- Each entity is stored once: USER/ADMIN, CATEGORY, LOCATION, ITEM, CLAIM,
-- MARKETPLACE_POST, MARKETPLACE_SALE, to_let_listing, and the food tables.
-- Repeating relationships use child tables (CLAIM_STATUS_HISTORY,
-- to_let_listing_photo, food_item, food_order_item). No non-key attribute in
-- these tables depends on another non-key attribute: the schema is in 3NF.

-- Marketplace sale invariant: one post may be sold only once.
ALTER TABLE MARKETPLACE_SALE
  ADD CONSTRAINT uk_marketplace_sale_post UNIQUE (post_id);

-- JDBC-equivalent purchase transaction. The application uses Spring @Transactional,
-- which calls commit when all statements succeed and rolls back on an exception.
START TRANSACTION;
SELECT post_id, seller_id, fixed_price, status
FROM MARKETPLACE_POST
WHERE post_id = ? FOR UPDATE;
-- Application verifies status = 'ACTIVE', selling_type = 'FIXED_PRICE', and buyer != seller.
INSERT INTO MARKETPLACE_SALE (post_id, buyer_id, sale_price, status)
VALUES (?, ?, ?, 'COMPLETED');
UPDATE MARKETPLACE_POST SET status = 'SOLD' WHERE post_id = ? AND status = 'ACTIVE';
COMMIT;
-- On any database/application error: ROLLBACK;

-- Frequently used lookup/filter indexes. Primary keys, unique keys, and existing
-- foreign-key relationship indexes are intentionally not repeated.
CREATE INDEX idx_marketplace_active_recent ON MARKETPLACE_POST (status, created_at DESC);
CREATE INDEX idx_marketplace_status_category ON MARKETPLACE_POST (status, category_id);
CREATE INDEX idx_item_status_created ON ITEM (status, created_at DESC);
CREATE INDEX idx_claim_status_updated ON CLAIM (status, updated_at DESC);
CREATE INDEX idx_claim_history_changed ON CLAIM_STATUS_HISTORY (claim_id, changed_at DESC);
CREATE INDEX idx_marketplace_sale_buyer_sold ON MARKETPLACE_SALE (buyer_id, sold_at DESC);
CREATE INDEX idx_to_let_status_rent ON to_let_listing (status, monthly_rent);

-- Index verification: the key column should show the named index rather than NULL.
EXPLAIN SELECT post_id, title FROM MARKETPLACE_POST
WHERE status = 'ACTIVE' AND category_id = ?;
EXPLAIN SELECT post_id, title FROM MARKETPLACE_POST
WHERE status = 'ACTIVE' ORDER BY created_at DESC;
EXPLAIN SELECT item_id, title FROM ITEM
WHERE status IN ('LOST', 'FOUND') ORDER BY created_at DESC;
EXPLAIN SELECT claim_id FROM CLAIM
WHERE status = 'PENDING' ORDER BY updated_at DESC;
EXPLAIN SELECT history_id FROM CLAIM_STATUS_HISTORY
WHERE claim_id = ? ORDER BY changed_at DESC;
EXPLAIN SELECT sale_id FROM MARKETPLACE_SALE
WHERE buyer_id = ? ORDER BY sold_at DESC;
EXPLAIN SELECT listing_id FROM to_let_listing
WHERE status = 'AVAILABLE' AND monthly_rent <= ?;
