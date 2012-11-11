/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.apache.commons.lang3.StringEscapeUtils;

class MyParser {
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;
    
    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    
    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }
    
    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }
    
    static String convertDateFormat(String inputDate)
    {
    	Date date = null;
    	try {
			date = new SimpleDateFormat("MMM-dd-yy HH:mm:ss", Locale.ENGLISH).parse(inputDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	String outputDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    	return outputDate;
    }
    
    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile, 
    		Set<DatabaseEbayUser> ebayUserSet, 
    		Map<String, DatabaseCategory> categoryMap, 
    		Set<DatabaseItem> itemSet, 
    		Set<DatabaseItemCategory> itemCategorySet, 
    		Set<DatabaseBid> bidSet) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        /* Fill in code here (you will probably need to write auxiliary
            methods). */
        
        Element root = doc.getDocumentElement();
        Element items[] = getElementsByTagNameNR(root, "Item");
        
        // Parse Items
        int itemCount = items.length;
        for (int itemIndex = 0; itemIndex < itemCount; itemIndex++)
        {
        	Element item = items[itemIndex];
        	String itemID = item.getAttribute("ItemID");
        	String name = StringEscapeUtils.escapeCsv(getElementTextByTagNameNR(item, "Name"));
        	String sellerLocation = StringEscapeUtils.escapeCsv(getElementTextByTagNameNR(item, "Location"));
        	String sellerCountry = StringEscapeUtils.escapeCsv(getElementTextByTagNameNR(item, "Country"));
        	String startTime = convertDateFormat(getElementTextByTagNameNR(item, "Started"));
        	String endTime = convertDateFormat(getElementTextByTagNameNR(item, "Ends"));
        	String description = getElementTextByTagNameNR(item, "Description");
        	if (description.length() > 4000)
        		description = description.substring(0, 4000);
        	description = StringEscapeUtils.escapeCsv(description);
        	String minimumBid = strip(getElementTextByTagNameNR(item, "First_Bid"));
        	String buyNowPrice = strip(getElementTextByTagNameNR(item, "Buy_Price"));
        	if (buyNowPrice.isEmpty())
        		buyNowPrice = "0.00";
        	
        	// Parse Seller
        	Element seller = getElementByTagNameNR(item, "Seller");
        	String sellerID = seller.getAttribute("UserID");
        	String sellerRating = seller.getAttribute("Rating");
        	
        	// Create EbayUser object and add to set
        	DatabaseEbayUser sellerObject = new DatabaseEbayUser(sellerID, sellerRating, sellerCountry, sellerLocation);
        	ebayUserSet.add(sellerObject);
        	
        	// Create Item object and add to set
        	DatabaseItem itemObject = new DatabaseItem(itemID, name, buyNowPrice, minimumBid, startTime, endTime, description, sellerID); 
        	itemSet.add(itemObject);
        	
        	// Parse categories
        	Element categories[] = getElementsByTagNameNR(item, "Category");
        	int categoryCount = categories.length;
        	for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++)
        	{
        		// Parse Category
        		String categoryName = StringEscapeUtils.escapeCsv(getElementText(categories[categoryIndex]));
        		int categoryID;
        		
        		// Create Category object and add to map
        		if (categoryMap.containsKey(categoryName))
        		{
        			DatabaseCategory categoryObject = categoryMap.get(categoryName);
        			categoryID = categoryObject.categoryID;
        		}
        		else
        		{
        			categoryID = categoryMap.size();
        			DatabaseCategory categoryObject = new DatabaseCategory(categoryID, categoryName);
        			categoryMap.put(categoryName, categoryObject);
        		}
        		
        		// Create ItemCategory object and add to set
        		DatabaseItemCategory itemCategoryObject = new DatabaseItemCategory(itemID, categoryID);
        		itemCategorySet.add(itemCategoryObject);
        	}
        	
        	// Parse bids
        	Element bidsParent = getElementByTagNameNR(item, "Bids");
        	Element bids[] = getElementsByTagNameNR(bidsParent, "Bid");
        	int bidCount = bids.length;
        	for (int bidIndex = 0; bidIndex < bidCount; bidIndex++)
        	{
        		// Parse bid
        		Element bid = bids[bidIndex];
            	String bidTime = convertDateFormat(getElementTextByTagNameNR(bid, "Time"));
        		String bidAmount = strip(getElementTextByTagNameNR(bid, "Amount"));
        		
        		// Parse bidder
        		Element bidder = getElementByTagNameNR(bid, "Bidder");
        		String bidderID = StringEscapeUtils.escapeCsv(bidder.getAttribute("UserID"));
        		String bidderRating = bidder.getAttribute("Rating");
        		String bidderLocation = StringEscapeUtils.escapeCsv(getElementTextByTagNameNR(bidder, "Location"));
            	String bidderCountry = StringEscapeUtils.escapeCsv(getElementTextByTagNameNR(bidder, "Country"));
        		
        		// Create EbayUser object and add to set
        		DatabaseEbayUser bidderObject = new DatabaseEbayUser(bidderID, bidderRating, bidderCountry, bidderLocation);
        		ebayUserSet.add(bidderObject);
        		
        		// Create Bid object and add to set
        		DatabaseBid bidObject = new DatabaseBid(bidSet.size(), itemID, bidderID, bidTime, bidAmount);
        		bidSet.add(bidObject);
        	}
        }
        
        /**************************************************************/
        
    }
    
    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        Set<DatabaseEbayUser> ebayUserSet = new HashSet<DatabaseEbayUser>();
        Map<String, DatabaseCategory> categoryMap = new HashMap<String, DatabaseCategory>();
        Set<DatabaseItem> itemSet = new HashSet<DatabaseItem>();
        Set<DatabaseItemCategory> itemCategorySet = new HashSet<DatabaseItemCategory>();
        Set<DatabaseBid> bidSet = new HashSet<DatabaseBid>();
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile, ebayUserSet, categoryMap, itemSet, itemCategorySet, bidSet);
        }
        
        /* Write sets out to files */
		try {
			FileWriter ebayUserStream = new FileWriter("EbayUser.csv");
	        BufferedWriter ebayUserWriter = new BufferedWriter(ebayUserStream);
	        Iterator<DatabaseEbayUser> ebayUserIterator = ebayUserSet.iterator();
	        while (ebayUserIterator.hasNext())
	        {
	        	DatabaseEbayUser user = ebayUserIterator.next();
	        	ebayUserWriter.write(user.toCSVString()); 
	        }
	        ebayUserWriter.close();
	        ebayUserStream.close();
	        
	        FileWriter categoryStream = new FileWriter("Category.csv");
	        BufferedWriter categoryWriter = new BufferedWriter(categoryStream);
	        Collection<DatabaseCategory> categoryCollection = categoryMap.values();
	        Iterator<DatabaseCategory> categoryIterator = categoryCollection.iterator();
	        while (categoryIterator.hasNext())
	        {
	        	DatabaseCategory category = categoryIterator.next();
	        	categoryWriter.write(category.toCSVString()); 	
	        }
	        categoryWriter.close();
	        categoryStream.close();
	        
	        FileWriter itemStream = new FileWriter("Item.csv");
	        BufferedWriter itemWriter = new BufferedWriter(itemStream);
	        Iterator<DatabaseItem> itemIterator = itemSet.iterator();
	        while (itemIterator.hasNext())
	        {
	        	DatabaseItem item = itemIterator.next();
	        	itemWriter.write(item.toCSVString()); 	
	        }
	        itemWriter.close();
	        itemStream.close();
	        
	        FileWriter itemCategoryStream = new FileWriter("ItemCategory.csv");
	        BufferedWriter itemCategoryWriter = new BufferedWriter(itemCategoryStream);
	        Iterator<DatabaseItemCategory> itemCategoryIterator = itemCategorySet.iterator();
	        while (itemCategoryIterator.hasNext())
	        {
	        	DatabaseItemCategory itemCategory = itemCategoryIterator.next();
	        	itemCategoryWriter.write(itemCategory.toCSVString()); 	
	        }
	        itemCategoryWriter.close();
	        itemCategoryStream.close();
	        
	        FileWriter bidStream = new FileWriter("Bid.csv");
	        BufferedWriter bidWriter = new BufferedWriter(bidStream);
	        Iterator<DatabaseBid> bidIterator = bidSet.iterator();
	        while (bidIterator.hasNext())
	        {
	        	DatabaseBid bid = bidIterator.next();
	        	bidWriter.write(bid.toCSVString()); 	
	        }
	        bidWriter.close();
	        bidStream.close();
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
