package com.example.buzz;

import android.app.Activity;

public class Product extends Activity {
String productname;
String id;
Float price;
Integer rowid,quantity;
DatabaseConnector dbc=new DatabaseConnector(this);

public Product()
{
}


public Integer getRowId() {
return rowid;
}

public void setRowId(Integer rowid) {
this.rowid = rowid;
}

public Integer getQty() {
return quantity;
}

public void setQty(Integer Qty) {
this.quantity = Qty;
}


public String getProductName() {
return productname;
}

public void setProductName(String name) {
this.productname = name;
}

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

public Float getPrice() {
return price;
}

public void setPrice(String id) {
this.price = price;
}


public Product(Integer rowid,String productname, String id,Float price,Integer quantity) {
super();
this.productname = productname;
this.price = price;
this.id = id;
this.rowid=rowid;
this.quantity=quantity;

}

}
