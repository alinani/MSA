package com.example.buzz;

public class MSAActions {
	
	public int icon;
	public String title;

	public MSAActions() {
		super();
	}

	public MSAActions(int icon, String title) {
		super();
		this.icon = icon;
		this.title = title;
	}
	
	public String getAction()
	{
		return title;
	}

}
