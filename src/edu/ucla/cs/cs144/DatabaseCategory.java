package edu.ucla.cs.cs144;

public class DatabaseCategory {
	int categoryID;
	String name;
	
	/**
	 * @param categoryID
	 * @param name
	 */
	public DatabaseCategory(int categoryID, String name) {
		super();
		this.categoryID = categoryID;
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + categoryID;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		DatabaseCategory other = (DatabaseCategory) obj;
		if (categoryID != other.categoryID)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public String toCSVString()
	{
		return String.format("%d,%s\n", categoryID, name);
	}
}
