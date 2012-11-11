package edu.ucla.cs.cs144;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;

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
        }*/
        
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
        }
        
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
	        	
	        	// Get all categories for this item
	        	PreparedStatement itemCategoriesStatement = connection.prepareStatement("SELECT * FROM ItemCategory WHERE item_id = ?");
	        	itemCategoriesStatement.setLong(1, itemID);
	        	ResultSet itemCategoriesRS = itemCategoriesStatement.executeQuery();
	        	Set<String> itemCategories = new HashSet<String>();
	        	while (itemCategoriesRS.next())
	        	{
	        		int categoryID = itemCategoriesRS.getInt("category_id");
	        		String category = categories.get(categoryID);
	        		itemCategories.add(category);
	        	}
	        	String[] categoryArray = new String[itemCategories.size()];
	        	categoryArray = (String[]) itemCategories.toArray(categoryArray);
	        	
	        	Item item = new Item(itemID, itemName, null, itemDescription, null, null, null, null, null, categoryArray);
	        	indexItem(item, indexWriter);
	        }
        } catch (SQLException ex) {
        	System.out.println(ex);
        	return;
        }
        
        try {
			indexWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
    	document.add(new Field("id", Long.toString(item.id), Field.Store.YES, Field.Index.UN_TOKENIZED));
    	document.add(new Field("name", item.name, Field.Store.YES, Field.Index.TOKENIZED));
    	document.add(new Field("description", item.description, Field.Store.NO, Field.Index.TOKENIZED));
    	String concatenatedCategories = new String();
    	int categoryCount = item.categories.length;
    	for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++)
    	{
    		concatenatedCategories = concatenatedCategories + item.categories[categoryIndex] + " ";
    	}
    	document.add(new Field("category", concatenatedCategories, Field.Store.NO, Field.Index.TOKENIZED));
    	document.add(new Field("all", concatenatedCategories + " " + item.name + " " + item.description, Field.Store.NO, Field.Index.TOKENIZED));
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
