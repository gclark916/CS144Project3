package edu.ucla.cs.cs144;

public class DatabaseBid {
	long bidID;
	String itemID;
	String bidderID;
	String time;
	String amount;
	
	/**
	 * @param bidID
	 * @param itemID
	 * @param bidderID
	 * @param time
	 * @param amount
	 */
	public DatabaseBid(long bidID, String itemID, String bidderID, String time,
			String amount) {
		super();
		this.bidID = bidID;
		this.itemID = itemID;
		this.bidderID = bidderID;
		this.time = time;
		this.amount = amount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + (int) (bidID ^ (bidID >>> 32));
		result = prime * result
				+ ((bidderID == null) ? 0 : bidderID.hashCode());
		result = prime * result + ((itemID == null) ? 0 : itemID.hashCode());
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
		DatabaseBid other = (DatabaseBid) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (bidID != other.bidID)
			return false;
		if (bidderID == null) {
			if (other.bidderID != null)
				return false;
		} else if (!bidderID.equals(other.bidderID))
			return false;
		if (itemID == null) {
			if (other.itemID != null)
				return false;
		} else if (!itemID.equals(other.itemID))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}
	
	public String toCSVString()
	{
		return String.format("%d,%s,\"%s\",\"%s\",%s\n", bidID, itemID, bidderID, time, amount);
	}
}
