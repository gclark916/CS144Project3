package edu.ucla.cs.cs144;

import java.math.BigDecimal;
import java.util.Date;

public class Item {
	long id;
	String name;
	EbayUser seller;
	String description;
	Bid[] bids;
	String[] categories;
	BigDecimal minimumFirstBid;
	BigDecimal buyNowPrice;
	Date startTime;
	Date endTime;
	
	/**
	 * @param id
	 * @param name
	 * @param seller
	 * @param description
	 * @param minimumFirstBid
	 * @param buyNowPrice
	 * @param startTime
	 * @param endTime
	 * @param bids
	 * @param categories
	 */
	public Item(long id, String name, EbayUser seller, String description,
			BigDecimal minimumFirstBid, BigDecimal buyNowPrice, Date startTime,
			Date endTime, Bid[] bids, String[] categories) {
		super();
		this.id = id;
		this.name = name;
		this.seller = seller;
		this.description = description;
		this.minimumFirstBid = minimumFirstBid;
		this.buyNowPrice = buyNowPrice;
		this.startTime = startTime;
		this.endTime = endTime;
		this.bids = bids;
		this.categories = categories;
	}
	
}
