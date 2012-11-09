-- Find the number of users in the database.
SELECT COUNT(user_id) FROM EbayUser;

-- Find the number of sellers from "New York", (i.e., sellers whose location 
-- is exactly the string "New York"). Pay special attention to case 
-- sensitivity. You should match the sellers from "New York" but not from "new york".
SELECT COUNT(DISTINCT(user_id))
FROM Item JOIN 
	(SELECT user_id
	FROM EbayUser
	WHERE location LIKE BINARY 'New York') AS NewYorkUser
	ON Item.seller_id = NewYorkUser.user_id;

-- Find the number of auctions belonging to exactly four categories.
SELECT COUNT(*)
FROM (SELECT item_id
FROM ItemCategory
GROUP BY item_id
HAVING COUNT(category_id) = 4)
AS ItemWith4Categories;

-- Find the ID(s) of current (unsold) auction(s) with the highest bid. Remember 
-- that the data was captured at the point in time December 20th, 2001, one 
-- second after midnight, so you can use this time point to decide which 
-- auction(s) are current. Pay special attention to the current auctions without 
-- any bid.

SELECT item_id
FROM Bid
WHERE amount = 
	(SELECT current_price
	FROM
		(SELECT Bid.item_id, buy_now_price, MAX(Bid.amount) AS current_price
		FROM Bid JOIN 
			(SELECT item_id, buy_now_price
			FROM Item
			WHERE time_end > '2001-12-20 00:00:01'
			AND time_start < '2001-12-20 00:00:01') AS TimeActiveAuction
			ON TimeActiveAuction.item_id = Bid.item_id
		GROUP BY Bid.item_id
		HAVING current_price < buy_now_price 
			OR buy_now_price = 0.00) -- the item has not been bought yet
		AS CurrentPrices
	ORDER BY current_price DESC
	LIMIT 1);

-- Find the number of sellers whose rating is higher than 1000.
SELECT COUNT(DISTINCT(seller_id))
FROM Item JOIN
	(SELECT user_id
	FROM EbayUser
	WHERE rating > 1000) AS Over1000RatingUser
	ON Item.seller_id = Over1000RatingUser.user_id;

-- Find the number of users who are both sellers and bidders.
SELECT COUNT(DISTINCT(bidder_id))
FROM Bid JOIN Item
	ON Bid.bidder_id = Item.seller_id;
	
-- Find the number of categories that include at least one item with a bid of 
-- more than $100.
SELECT COUNT(DISTINCT(ItemCategory.category_id))
FROM ItemCategory JOIN
	(SELECT DISTINCT(item_id)
	FROM Bid
	WHERE amount > 100) AS Above100Item
	ON ItemCategory.item_id = Above100Item.item_id;