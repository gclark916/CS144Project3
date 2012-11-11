package edu.ucla.cs.cs144;

public class DatabaseEbayUser {
	private String userID;
	private String rating;
	private String country;
	private String location;
	
	/**
	 * @param userID
	 * @param rating
	 * @param country
	 * @param location
	 */
	public DatabaseEbayUser(String userID, String rating, String country,
			String location) {
		super();
		this.userID = userID;
		this.rating = rating;
		this.country = country;
		this.location = location;
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
		DatabaseEbayUser other = (DatabaseEbayUser) obj;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (rating == null) {
			if (other.rating != null)
				return false;
		} else if (!rating.equals(other.rating))
			return false;
		if (userID == null) {
			if (other.userID != null)
				return false;
		} else if (!userID.equals(other.userID))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((rating == null) ? 0 : rating.hashCode());
		result = prime * result + ((userID == null) ? 0 : userID.hashCode());
		return result;
	}
	
	public String toCSVString()
	{
		return String.format("%s,%s,%s,%s\n", userID, rating, country, location);
	}
}
