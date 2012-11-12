package edu.ucla.cs.cs144;

import java.io.IOException;
import java.sql.Connection;
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

        try 
        {
        	// create a connection to the database to retrieve Items from MySQL
        	connection = DbManager.getConnection(true);
        	
        	// Map itemIDs to category sets
        	Map<Long, Set<String>> categories = new HashMap<Long, Set<String>>();
        	Statement categoryStatement = connection.createStatement();
	        ResultSet categoriesRS = categoryStatement.executeQuery("SELECT item_id, name FROM ItemCategory JOIN Category ON ItemCategory.category_id = Category.category_id");
	        while (categoriesRS.next())
	        {
	        	long itemID = categoriesRS.getLong("item_id");
	        	String category = categoriesRS.getString("name");
	        	Set<String> catSet = categories.get(itemID);
				if (catSet != null)
					catSet.add(category);
				else
				{
					catSet = new HashSet<String>();
					catSet.add(category);
					categories.put(itemID, catSet);
				}
	        }
	        
	        // Construct and index all items
	        String indexDirectory = System.getenv("LUCENE_INDEX");
	        IndexWriter indexWriter;
	        indexWriter = new IndexWriter(indexDirectory, new StandardAnalyzer(), true);
	        
	        Statement itemStatement = connection.createStatement();
	        ResultSet itemsRS = itemStatement.executeQuery("SELECT * FROM Item");
	        while (itemsRS.next())
	        {
	        	long itemID = itemsRS.getLong("item_id");
	        	String itemName = itemsRS.getString("name");
	        	String itemDescription = itemsRS.getString("description");
	        	
	        	// Get all categories for this item
	        	Set<String> itemCategories = categories.get(itemID);
	        	String[] categoryArray = new String[itemCategories.size()];
	        	categoryArray = (String[]) itemCategories.toArray(categoryArray);
	        	
	        	Item item = new Item(itemID, itemName, null, itemDescription, null, null, null, null, null, categoryArray);
	        	indexItem(item, indexWriter);
	        } 
	        
	        indexWriter.close();
	        connection.close();
        }
        catch (SQLException e) 
        {
        	e.printStackTrace();
        	System.out.println(e);
        } catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
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
