package com.example.buzz;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class DatabaseConnector {

	private static final String DB_NAME = "BUZZ";
	private SQLiteDatabase database;
	private DatabaseOpenHelper dbOpenHelper;
	private String ProductTable;
	String TbData;

	// Select appropriate product database
	private Context context;

	// Shared Preferences
	static final String PREFS = "shopdata";

	// retrieve from extras

	public void TableSelection() {
		if (TbData.equals("SHOPRITE")) {
			ProductTable = "ShopriteProducts";
		} else if (TbData.equals("SPAR")) {
			ProductTable = "SparProducts";
		} else if (TbData.equals("PICK n PAY")) {
			ProductTable = "PnPProducts";
		} else if (TbData.equals("MELISA")) {
			ProductTable = "MelisaProducts";
		}
	}

	// TempProduct tables columns
	public static final String KEY_TID = "_id";
	public static final String KEY_TPRODNAME = "ProdName";
	public static final String KEY_TPRODPRICE = "Price";
	public static final String KEY_TPRODQTY = "Quantity";

	// Product tables columns
	public static final String KEY_PID = "_id";
	public static final String KEY_PRODID = "ProductId";
	public static final String KEY_PRODNAME = "Name";
	public static final String KEY_PRODPRICE = "Price";

	// Declare columnNames TO BE USED IN LIST(Shopping Cart)
	public static final String KEY_ID = "_id";
	public static final String KEY_PRODUCTNAME = "Product";
	public static final String KEY_PRODUCTPRICE = "Price";
	public static final String KEY_PRODUCTID = "ProductId";
	public static final String KEY_QTY = "Qty";

	// Shopping List items
	public static final String KEY_SLID = "_id";
	public static final String KEY_SLNAME = "Name";
	public static final String KEY_SLDATE = "Date";
	public static final String KEY_SLSHOP = "ShopName";
	public static final String KEY_SLSUBTOTAL = "SubTotal";

	// HistoryList items
	public static final String KEY_HLID = "_id";
	public static final String KEY_HLPRODUCTNAME = "Name";
	public static final String KEY_HLPRICE = "Price";
	public static final String KEY_HLQTY = "Qty";
	
	// HistoryList items
		public static final String KEY_BID = "_id";
		public static final String KEY_BSTORE = "StoreName";
		public static final String KEY_BPRICE = "Price";
	
//Promo fields
		public static final String KEY_PrID="_id";
		public static final String KEY_PMSG="Message";
	// DoCleanup Counters
	int UserTbl = 0;

	public DatabaseConnector(Context context) {
		dbOpenHelper = new DatabaseOpenHelper(context);
		this.context = context;

	}

	public void open() throws SQLException {
		// open database in reading/writing mode
		database = dbOpenHelper.getWritableDatabase();
	}

	public void close() {
		if (database != null)
			database.close();
	}

	/************* SELECT QUERIES **************/
	//get registration status
		public Cursor GetRegStatus() {
			
			return database.query("Register", null, null,
					null, null, null, null);
		}
		
		
	// Get all products from Cart table
	public Cursor Products() {
		return database.query("Cart", new String[] { KEY_ID, KEY_PRODUCTNAME,
				KEY_PRODUCTPRICE, KEY_PRODUCTID, KEY_QTY }, null, null, null,
				null, "Product ASC");
	}

	// Select all items from various products tables to populate list view for
	// Search....
	public Cursor GetAllProducts(String p_sTablname) {
		return database.query(p_sTablname, new String[] { KEY_PID, KEY_PRODID,
				KEY_PRODNAME, KEY_TPRODPRICE }, null, null, null, null,
				"Name ASC");
	}

	// Select all items from temp product table used to make shopping list
	public Cursor GetAllTempProducts() {
		return database.query("TempProdList", new String[] { KEY_PID,KEY_TPRODQTY, KEY_TPRODNAME,
				 KEY_PRODPRICE }, null, null, null, null,
				"ProdName ASC");
	}
	
	//Share List
	public Cursor GetAllTempProdShare() {

		return database.query("TempProdList", null,null, null, null, null, null);
	}

	
	// Get product from TempProducts table
		public Cursor GetTempProdItem(String p_ProdName) {

			return database.query("TempProdList", null, "ProdName='" + p_ProdName
					+ "'", null, null, null, null);
		}
		
	// Get particular product from any dynamic table select
	public Cursor GetItem(String productid, String Tablname) {
		return database.query(Tablname, null, "ProductId='" + productid + "'",
				null, null, null, null);
	}

	// Get select item from PnPProduct by productID
	public Cursor GetPnPItem(String productid) {

		return database.query("PnPProducts", null, "ProductId='" + productid
				+ "'", null, null, null, null);
	}

	// Get select item from Products by Name
	public Cursor GetProdItembyName(String ProdName, String Tablname) {

		return database.query(Tablname, null, "Name='" + ProdName + "'", null,
				null, null, null);
	}

	// Get select item from SparProduct
	public Cursor GetSparItem(String productid) {

		return database.query("SparProducts", null, "ProductId='" + productid
				+ "'", null, null, null, null);
	}

	// Get select item from MelisaProduct
	public Cursor GetMelisaItem(String productid) {

		return database.query("MelisaProducts", null, "ProductId='" + productid
				+ "'", null, null, null, null);
	}

	// Get select item from ShopriteProduct
	public Cursor GetShopriteItem(String productid) {

		return database.query("ShopriteProducts", null, "ProductId='"
				+ productid + "'", null, null, null, null);
	}

	// Get select item from Cart
	public Cursor GetCartItem(String productid) {
		return database.query("Cart", null, "ProductId='" + productid + "'",
				null, null, null, null);
	}

	// Get select item from Product
	public Cursor GetSingleListDetails(long listid) {
		open();
		return database.query("ShoppingList", null, "_id=" + listid, null,
				null, null, null);

	}

	// Get select item from Cart
	public Cursor GetCartItems() {
		return database.query("Cart", null, null, null, null, null, null);
	}

	// Get select item from Cart
	public Cursor GetShoppingLists() {
		return database.query("ShoppingList", new String[] { KEY_SLID,KEY_SLSHOP,
				KEY_SLNAME, KEY_SLDATE, KEY_SLSUBTOTAL }, null, null, null,
				null, "Name ASC");
	}

	// Get HistoryTable items
	// Get select item from Product
	public Cursor GetHistoryItems(String listname) {
		return database.query("HistoryProducts", null, "ListName='" + listname
				+ "'", null, null, null, "Name ASC");
	}
	
	//Price comparison
	public Cursor GetMinimumPrice()
	{
	
		return  database.query("TempBestPrice", new String[] {"MIN(Price) AS Price"}, null, null, null, null, null);
		
	}
	
	public Cursor GetMinimumPriceStore(float p_fPrice)
	{
	
		return  database.query("TempBestPrice", new String[] {"StoreName","Price"}, "Price="+p_fPrice, null, null, null, null);
		
	}
	
	//get other prices after we chosen lowest
	public Cursor GetOtherPrices(float pPrice) {
		return database.query("TempBestPrice", new String[] { KEY_BID,
				KEY_BSTORE, KEY_BPRICE }, "Price != "+pPrice, null, null,
				null, "StoreName ASC");
	}
	
	//get other prices after we chosen lowest
		public Cursor GetPromotionalmsg() {
			return database.query("Promotions", new String[] { KEY_PrID,
					KEY_PMSG},null, null, null,
					null, "_id DESC");
		}
	

	/************* UPDATE QUERIES **************/

	// UpdateCartProductQty
	public void UpdateCartProductQty(String pProductId, Integer pQty) {
		ContentValues args = new ContentValues();
		args.put("Qty", pQty);

		database.update("Cart", args, "ProductId='" + pProductId + "'", null);
	}

	//update quantity in the temp table when making list
	public void UpdateTempProductQtyByName(String pProductName, Integer pQty) {
		ContentValues args = new ContentValues();
		args.put("Quantity", pQty);

		database.update("TempProdList", args, "ProdName='" + pProductName + "'", null);
	}
	
	//update quantity in the temp table when making list
		public void UpdateTempProductQtyById(long pId, Integer pQty) {
			ContentValues args = new ContentValues();
			args.put("Quantity", pQty);

			database.update("TempProdList", args, "_id='" + pId + "'", null);
		}

	// UpdateProductPrices with those online
	public void UpdateProductPrices(String pProductId, String Price,String TableName) {
			float fPrice = Float.parseFloat(Price);
		ContentValues args = new ContentValues();
		args.put("Price", fPrice);

		try {
			database.update(TableName, args, "ProductId='" + pProductId
					+ "'", null);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	//Update the register table so splash screen knows which activity to go to
	public void UpdateRegStatus(String p_sEmail) {
		ContentValues args = new ContentValues();
		args.put("Status", "1");
		args.put("Email",p_sEmail);

		database.update("Register", args,null, null);
	}
	
	//Update the register table when prices updates
		public void UpdateDbUpdate() {
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	           //get current date time with Date()
	           Date date = new Date();
	           String DTS=dateFormat.format(date);
			
			ContentValues args = new ContentValues();
			args.put("DbUpdateTime", DTS.toString());
			
			database.update("Register", args,null, null);
		}

	/************* INSERT QUERIES **************/

	//Insert Products into empty product table
	public void PopulateProducts(String pProductId, String pProduct,String pDescription, Float pPrice) {

			int qty = 1;
			ContentValues args = new ContentValues();

			args.put("ProductId", pProductId);
			args.put("Name", pProduct);
			args.put("Description", pDescription);
			args.put("Price", pPrice);

			try {
				database.insert("PnPProducts", null, args);
			} catch (SQLException e) {
				String msg = e.getMessage();
			}

		}
	
	// Insert into shopping cart
	public void AddtoCart(String pProductId, String pProduct, Float pPrice) {

		int qty = 1;
		ContentValues args = new ContentValues();

		args.put("Product", pProduct);
		args.put("Price", pPrice);
		args.put("ProductId", pProductId);
		args.put("Qty", qty);

		try {
			database.insert("Cart", null, args);
		} catch (SQLException e) {
			String msg = e.getMessage();
		}

	}

	// Insert into shopping cart used list
	public void AddtoCart(String pProductId, String pProduct, Float pPrice,
			Integer Qty) {

		ContentValues args = new ContentValues();

		args.put("Product", pProduct);
		args.put("Price", pPrice);
		args.put("ProductId", pProductId);
		args.put("Qty", Qty);

		try {
			database.insert("Cart", null, args);
		} catch (SQLException e) {
			String msg = e.getMessage();
		}

	}

	// Insert into History table
	public void InsertHistoryProducts(String pProductId, String pProductName,
			String pListName, Float pPrice, Integer Qty) {

		float l_fDefaultNewPrice = 0;
		ContentValues args = new ContentValues();

		args.put("Name", pProductName);
		args.put("Price", pPrice);
		args.put("PriceNew", l_fDefaultNewPrice);
		args.put("ProductId", pProductId);
		args.put("ListName", pListName);
		args.put("Qty", Qty);

		try {
			database.insert("HistoryProducts", null, args);
		} catch (SQLException e) {
			String msg = e.getMessage();
		}

	}

	// Insert into ShoppingList
	public void InsertShoppingList(String pListName, String Total,String pShopName) {

		// get current date
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		String CurrentDate = dateFormat.format(date);

		ContentValues args = new ContentValues();

		args.put("Name", pListName);
		args.put("Date", CurrentDate);
		args.put("SubTotal", Total);
		args.put("ShopName", pShopName);

		try {
			database.insert("ShoppingList", null, args);
		} catch (SQLException e) {
			String msg = e.getMessage();
		}

	}

	// Insert into TempProductList used when making shopping list from listview
	public void InsertTempProdList(String pProdId,String pProdName, String Price) {

		ContentValues args = new ContentValues();

		args.put("ProdId", pProdId);
		args.put("ProdName", pProdName);
		args.put("Price", Price);
	

		try {
			database.insert("TempProdList", null, args);
		} catch (SQLException e) {
			String msg = e.getMessage();
		}

	}
	
	// Insert into shopping cart
	public void AddtoTempBestProd(String pProductId, String pStore, Float pPrice) {

		ContentValues args = new ContentValues();

		args.put("ProductId", pProductId);
		args.put("StoreName", pStore);
		args.put("Price", pPrice);
			
		try {
			database.insert("TempBestPrice", null, args);
		} catch (SQLException e) {
			String msg = e.getMessage();
		}

	}
	
	// Insert into Promotions table
		public void DoAddPromotionalMsg(String pMsg) {

			ContentValues args = new ContentValues();

			args.put("Message", pMsg);
		
			try {
				database.insert("Promotions", null, args);
			} catch (SQLException e) {
				String msg = e.getMessage();
			}

		}

	/**************************** LOGIC QUERIES *****************************************/
	// Count items in cart
	public String CountItems() {

		final SQLiteStatement stmt = database
				.compileStatement("Select Sum(Qty) from Cart");

		// Cast as appropriate
		return stmt.simpleQueryForString();

	}

	/************* TABLE Clear QUERIES **************/
	// Clear Tables

	public void DoClearList() {
		database.delete("Cart", null, null);
	}
	
	public void DoClearTempList() {
		database.delete("TempProdList", null, null);
	}
	
	public void DoClearShoppingLists() {
		database.delete("ShoppingList", null, null);
	}


	public void DoDeleteItem(Integer delitem) {

		database.delete("Cart", "_id=" + delitem, null);

	}

	public void DoDeleteCartItem(String prodId) {

		database.delete("Cart", "ProductId=" + prodId, null);

	}
	
	public void DoDeleteItemFromTempProdList(long Id) {

		database.delete("TempProdList", "_id=" + Id, null);

	}
	
	public void DoDeleteCaparisonItems() {

		database.delete("TempBestPrice", null, null);
	}
	
	public void DoDeletePromotions() {

		database.delete("Promotions", null, null);
	}

}
