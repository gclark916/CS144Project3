package edu.ucla.cs.cs144;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.LockObtainFailedException;

public class Indexer {
    
    /** Creates a new instance of Indexer */
    public Indexer() {
    }
 
    public void rebuildIndexes() 
    {

        Connection connection = null;

        // create a connection to the database to retrieve Items from MySQL
        try 
        {
        	connection = DbManager.getConnection(true);
        } 
        catch (SQLException ex) 
        {
        	System.out.println(ex);
        	return;
        }
        
        // Map usernames to EbayUSer objects
        /*Map<String, EbayUser> users = new HashMap<String, EbayUser>();
        try
        {
	        Statement statement = connection.createStatement();
	        ResultSet usersRS = statement.executeQuery("SELECT * FROM EbayUser");
	        while (usersRS.next())
	        {
	        	String userID = usersRS.getString("user_id");
	        	String country = usersRS.getString("country");
	        	String location = usersRS.getString("location");
	        	int rating = usersRS.getInt("rating");
	        	
	        	EbayUser user = new EbayUser(userID, rating, country, location);
	        	users.put(userID, user);
	        }
        }
        catch (SQLException ex)
        {
        	System.out.println(ex);
        	return;
        }
        
        // Map categoryIDs to names
        Map<Integer, String> categories = new HashMap<Integer, String>();
        try
        {
	        Statement statement = connection.createStatement();
	        ResultSet categoriesRS = statement.executeQuery("SELECT * FROM Category");
	        while (categoriesRS.next())
	        {
	        	int categoryID = categoriesRS.getInt("category_id");
	        	String category = categoriesRS.getString("name");
	        	categories.put(categoryID, category);
	        }
        }
        catch (SQLException ex)
        {
        	System.out.println(ex);
        	return;
        }*/
        
        // Construct and index all items
        String indexDirectory = System.getenv("LUCENE_INDEX");
        IndexWriter indexWriter;
		try {
			indexWriter = new IndexWriter(indexDirectory, new StandardAnalyzer(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
        try
        {
	        Statement statement = connection.createStatement();
	        ResultSet itemsRS = statement.executeQuery("SELECT * FROM Item");
	        while (itemsRS.next())
	        {
	        	long itemID = itemsRS.getLong("item_id");
	        	String itemName = itemsRS.getString("name");
	        	String itemDescription = itemsRS.getString("description");
	        	BigDecimal buyNowPrice = itemsRS.getBigDecimal("buy_now_price");
	        	BigDecimal minFirstBid = itemsRS.getBigDecimal("minimum_start_bid");
	        	Date startTime = itemsRS.getDate("time_start");
	        	Date endTime = itemsRS.getDate("time_end");
	        	//String sellerID = itemsRS.getString("seller_id");
	        	
	        	// Get all bids for item
	        	/*PreparedStatement bidsStatement = connection.prepareStatement("SELECT * FROM Bid WHERE item_id = ?");
	        	bidsStatement.setLong(1, itemID);
	        	ResultSet bidsRS = bidsStatement.executeQuery();*/
	        	Set<Bid> bids = new HashSet<Bid>();
	        	/*while (bidsRS.next())
	        	{
	        		long bidID = bidsRS.getLong("bid_id");
	        		BigDecimal amount = bidsRS.getBigDecimal("amount");
	        		Date bidTime = bidsRS.getDate("time");
	        		String bidderID = bidsRS.getString("bidder_id");
	        		EbayUser bidder = users.get(bidderID);
	        		
	        		Bid bid = new Bid(bidID, itemID, bidder, amount, bidTime);
	        		bids.add(bid);
	        	}*/
	        	Bid[] bidArray = (Bid[]) bids.toArray();
	        	
	        	// Get all categories for this item
	        	/*String categoriesQuery = String.format("SELECT * FROM ItemCategory WHERE item_id = %d", itemID);
	        	ResultSet categoriesRS = statement.executeQuery(categoriesQuery);*/
	        	Set<String> itemCategories = new HashSet<String>();
	        	/*while (categoriesRS.next())
	        	{
	        		int categoryID = categoriesRS.getInt("category_id");
	        		String category = categories.get(categoryID);
	        		itemCategories.add(category);
	        	}*/
	        	String[] categoryArray = (String[]) itemCategories.toArray();
	        	
	        	//EbayUser seller = users.get(sellerID);
	        	EbayUser seller = null;
	        	
	        	Item item = new Item(itemID, itemName, seller, itemDescription, minFirstBid, buyNowPrice, startTime, endTime, bidArray, categoryArray);
	        	indexItem(item, indexWriter);
	        }
        }
        catch (SQLException ex)
        {
        	System.out.println(ex);
        	return;
        }
        
        // close the database connection
	try {
	    connection.close();
	} catch (SQLException ex) {
	    System.out.println(ex);
	}
    }    

    public static void main(String args[]) {
        Indexer idx = new Indexer();
        idx.rebuildIndexes();
    }
    
    private void indexItem(Item item, IndexWriter writer)
    {
    	Document document = new Document();
    	document.add(new Field("id", ByteBuffer.allocate(8).putLong(item.id).array(), Field.Store.COMPRESS));
    	//long id = ByteBuffer.allocate(8).put(document.getBinaryValue("id")).getLong();
    	document.add(new Field("name", item.name, Field.Store.YES, Field.Index.TOKENIZED));
    	document.add(new Field("description", item.description, Field.Store.NO, Field.Index.TOKENIZED));
    	String concatenatedCategories = new String();
    	int categoryCount = item.categories.length;
    	for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++)
    	{
    		concatenatedCategories = concatenatedCategories.concat(item.categories[categoryIndex] + " ");
    	}
    	document.add(new Field("category", concatenatedCategories, Field.Store.NO, Field.Index.TOKENIZED));
    	try {
			writer.addDocument(document);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
