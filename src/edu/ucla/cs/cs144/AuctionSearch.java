package edu.ucla.cs.cs144;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.text.SimpleDateFormat;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchConstraint;
import edu.ucla.cs.cs144.SearchResult;

public class AuctionSearch implements IAuctionSearch {

	/* 
         * You will probably have to use JDBC to access MySQL data
         * Lucene IndexSearcher class to lookup Lucene index.
         * Read the corresponding tutorial to learn about how to use these.
         *
         * Your code will need to reference the directory which contains your
	 * Lucene index files.  Make sure to read the environment variable 
         * $LUCENE_INDEX with System.getenv() to build the appropriate path.
	 *
	 * You may create helper functions or classes to simplify writing these
	 * methods. Make sure that your helper functions are not public,
         * so that they are not exposed to outside of this class.
         *
         * Any new classes that you create should be part of
         * edu.ucla.cs.cs144 package and their source files should be
         * placed at src/edu/ucla/cs/cs144.
         *
         */
	
	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) {
		SearchResult[] results = new SearchResult[0];
		
		try {
			String[] fields = {"name", "description", "category"};
			MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
			Query luceneQuery = parser.parse(query);
		    results = getSearchResultsForQuery(luceneQuery, numResultsToSkip, numResultsToReturn);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return results;
	}

	public SearchResult[] advancedSearch(SearchConstraint[] constraints, 
			int numResultsToSkip, int numResultsToReturn) {
		SearchResult[] results = new SearchResult[0];
		
		// TODO: Your code here!
		// Get itemIDs from SQL search first
		Connection connection = null;
		try {
			connection = DbManager.getConnection(true);

			// Go through constraints to build Lucene and SQL queries
			String query = "";
			
			// TODO: assumes only one bidder constraint if any
			String bidderID = null;
			BigDecimal buyNowPrice = null;
			Date endTime = null;
			String sellerID = null;
			for (int constraintIndex = 0, constraintCount = constraints.length; constraintIndex < constraintCount; constraintIndex++)
			{
				SearchConstraint constraint = constraints[constraintIndex];
				String field = constraint.getFieldName();
				if (field.equals(FieldName.BidderId))
					bidderID = constraint.getValue();
				else if (field.equals(FieldName.BuyPrice))
					buyNowPrice = new BigDecimal(constraint.getValue());
				else if (field.equals(FieldName.EndTime))
					endTime = new SimpleDateFormat("MMM-dd-yy HH:mm:ss", Locale.ENGLISH).parse(constraint.getValue());
				else if (field.equals(FieldName.SellerId))
					sellerID = constraint.getValue();
				else
				{
					String luceneField = "";
					if (field.equals(FieldName.ItemName))
						luceneField = "name";
					else if (field.equals(FieldName.Description))
						luceneField = "description";
					else if (field.equals(FieldName.Category))
						luceneField = "category";
					
					query = query + " " + luceneField + ":(" + constraint.getValue() + ")";
				}
			}
			
			String itemSelect = "SELECT item_id FROM Item";
			boolean whereClause = false;
			if (buyNowPrice != null)
			{
				itemSelect = itemSelect + " WHERE buy_now_price = ?";
				whereClause = true;
			}
			if (endTime != null)
			{
				if (whereClause)
					itemSelect = itemSelect + " AND time_end = ?";
				else
				{
					itemSelect = itemSelect + " WHERE time_end = ?";
					whereClause = true;
				}
			}
			if (sellerID != null)
			{
				if (whereClause)
					itemSelect = itemSelect + " AND seller_id = ?";
				else
				{
					itemSelect = itemSelect + " WHERE seller_id = ?";
					whereClause = true;
				}
			}
			
			String SQLQuery;
			if (bidderID != null)
			{
				if (whereClause)
					SQLQuery = "SELECT item_id FROM (" + itemSelect + ") AS FilteredItem JOIN Bid ON FilteredItem.item_id = Bid.item_id WHERE bidder_id = ?";
				else
					SQLQuery = "SELECT item_id FROM Item AS FilteredItem JOIN Bid ON FilteredItem.item_id = Bid.item_id WHERE bidder_id = ?";
			}
			else
			{
				if (whereClause)
					SQLQuery = itemSelect;
				else
					SQLQuery = null;
			}
			
			if (SQLQuery != null)
			{
				PreparedStatement statement = connection.prepareStatement(SQLQuery);
				
				// Set statement parameters
				int parameterIndex = 1;
				if (buyNowPrice != null)
				{
					statement.setBigDecimal(parameterIndex, buyNowPrice);
					parameterIndex++;
				}
				if (endTime != null)
				{
					statement.setTimestamp(parameterIndex, (Timestamp) endTime);
					parameterIndex++;
				}
				if (sellerID != null)
				{
					statement.setString(parameterIndex, sellerID);
					parameterIndex++;
				}
				if (bidderID != null)
				{
					statement.setString(parameterIndex, bidderID);
				}
				
				ResultSet itemsRS = statement.executeQuery();
				
				// Make one long string of valid itemIDs
				String itemIDs = "";
				while (itemsRS.next())
				{
					String itemID = Long.toString(itemsRS.getLong("item_id"));
					itemIDs = itemIDs + " " + itemID;
				}
				
				if (!itemIDs.equals(""))
					query = "+id:(" + itemIDs + ")" + query;
			}
			
			QueryParser parser = new QueryParser("description", new StandardAnalyzer());
			Query luceneQuery = parser.parse(query);
			
			results = getSearchResultsForQuery(luceneQuery, numResultsToSkip, numResultsToReturn);
		} catch (SQLException | java.text.ParseException | ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return results;
	}

	public String getXMLDataForItemId(String itemId) {
		// TODO: Your code here!
		return null;
	}
	
	public String echo(String message) {
		return message;
	}
	
	private SearchResult[] getSearchResultsForQuery(Query luceneQuery, int numResultsToSkip, int numResultsToReturn) throws CorruptIndexException, IOException
	{
		SearchResult[] results = new SearchResult[0];
		
		String indexDirectory = System.getenv("LUCENE_INDEX");
		IndexSearcher searcher = new IndexSearcher(indexDirectory);
		Hits hits = searcher.search(luceneQuery);
		
		// Translate Hits into SearchResults
		int hitCount;
		if (numResultsToReturn == 0)
			hitCount = hits.length();
		else
			hitCount = numResultsToSkip + numResultsToReturn;
		results = new SearchResult[hitCount];
		for (int hitIndex = numResultsToSkip, searchResultIndex = 0; hitIndex < hitCount; hitIndex++, searchResultIndex++)
		{
			Document document = hits.doc(hitIndex);
			String itemID = document.get("id");
			String name = document.get("name");
		    	
			SearchResult result = new SearchResult(itemID, name);
			results[searchResultIndex] = result;
		}
		
		return results;
	}

}
