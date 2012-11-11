package edu.ucla.cs.cs144;

public class DatabaseItem {
	String itemID;
	String name;
	String buyNowPrice;
	String minimumBid;
	String startTime;
	String endTime;
	String description;
	String sellerID;
	
	/**
	 * @param itemID
	 * @param name
	 * @param buyNowPrice
	 * @param minimumBid
	 * @param startTime
	 * @param endTime
	 * @param description
	 * @param sellerID
	 */
	public DatabaseItem(String itemID, String name, String buyNowPrice,
			String minimumBid, String startTime, String endTime,
			String description, String sellerID) {
		super();
		this.itemID = itemID;
		this.name = name;
		this.buyNowPrice = buyNowPrice;
		this.minimumBid = minimumBid;
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
		this.sellerID = sellerID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((buyNowPrice == null) ? 0 : buyNowPrice.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((itemID == null) ? 0 : itemID.hashCode());
		result = prime * result
				+ ((minimumBid == null) ? 0 : minimumBid.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((sellerID == null) ? 0 : sellerID.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
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
		DatabaseItem other = (DatabaseItem) obj;
		if (buyNowPrice == null) {
			if (other.buyNowPrice != null)
				return false;
		} else if (!buyNowPrice.equals(other.buyNowPrice))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (itemID == null) {
			if (other.itemID != null)
				return false;
		} else if (!itemID.equals(other.itemID))
			return false;
		if (minimumBid == null) {
			if (other.minimumBid != null)
				return false;
		} else if (!minimumBid.equals(other.minimumBid))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sellerID == null) {
			if (other.sellerID != null)
				return false;
		} else if (!sellerID.equals(other.sellerID))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		return true;
	}
	
	public String toCSVString()
	{
		return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", 
				itemID, 
				name, 
				buyNowPrice, 
				minimumBid,
				startTime,
				endTime,
				description,
				sellerID);
	}
}
