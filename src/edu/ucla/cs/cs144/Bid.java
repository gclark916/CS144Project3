package edu.ucla.cs.cs144;

import java.math.BigDecimal;
import java.util.Date;

public class Bid {
	long id;
	long itemID;
	EbayUser bidder;
	Date time;
	BigDecimal amount;
	
	/**
	 * @param id
	 * @param itemID
	 * @param bidder
	 * @param amount
	 * @param time
	 */
	public Bid(long id, long itemID, EbayUser bidder, BigDecimal amount,
			Date time) {
		super();
		this.id = id;
		this.itemID = itemID;
		this.bidder = bidder;
		this.amount = amount;
		this.time = time;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((bidder == null) ? 0 : bidder.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (itemID ^ (itemID >>> 32));
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bid other = (Bid) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (bidder == null) {
			if (other.bidder != null)
				return false;
		} else if (!bidder.equals(other.bidder))
			return false;
		if (id != other.id)
			return false;
		if (itemID != other.itemID)
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}
	
}
