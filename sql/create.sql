-- grep "<Country>" items-*.xml | sed 's/items-.*\.xml:    <Country>//' | sed 's/<\/Country>//' | wc -L
-- grep "<Location>" items-*.xml | sed 's/items-.*\.xml:[ ]*<Location>//' | sed 's/<\/Location>//' | wc -L
-- grep "Seller UserID" items-*.xml | sed 's/items-.*\.xml:    <Seller UserID="//' | sed 's/Rating=//' | sed 's/"// | wc -L

CREATE TABLE EbayUser(
	user_id VARCHAR(50),
	rating INT,
	country VARCHAR(60), -- max in data was 50
	location VARCHAR(100), -- max in data is 83
	PRIMARY KEY (user_id));
	
CREATE TABLE Category(
	category_id INT, 
	name VARCHAR(40), -- max in data was 33
	PRIMARY KEY (category_id));
	
CREATE TABLE Item(
	item_id BIGINT,
	name VARCHAR(60),	-- max in data was 57 
	buy_now_price DECIMAL(8,2),
	minimum_start_bid DECIMAL(8,2), 
	time_start TIMESTAMP,
	time_end TIMESTAMP,
	description VARCHAR(4000),
	seller_id VARCHAR(50),
	PRIMARY KEY (item_id),
	FOREIGN KEY (seller_id) REFERENCES EbayUser(user_id));
	
CREATE TABLE ItemCategory(
	item_id BIGINT, 
	category_id INT,
	PRIMARY KEY (item_id, category_id),
	FOREIGN KEY (item_id) REFERENCES Item(item_id),
	FOREIGN KEY (category_id) REFERENCES Category(category_id));

CREATE TABLE Bid(
	bid_id BIGINT,
	item_id BIGINT,
	bidder_id VARCHAR(50),
	time TIMESTAMP,
	amount DECIMAL(8,2), 
	PRIMARY KEY (bid_id),
	FOREIGN KEY (item_id) REFERENCES Item(item_id),
	FOREIGN KEY (bidder_id) REFERENCES EbayUser(user_id));