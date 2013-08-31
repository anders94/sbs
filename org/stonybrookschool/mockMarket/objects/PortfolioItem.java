package org.stonybrookschool.mockMarket.objects;

public class PortfolioItem
{
    private int    symbolId;
    private String symbol, company, time;
    private float  price, pointChange, quantity;

    public PortfolioItem ( )
    {

    }

    public PortfolioItem ( int symbolId, String symbol,
			   String company, String time,
			   float price, float pointChange,
			   float quantity )
    {
	this.symbolId    = symbolId;
	this.symbol      = symbol;
	this.company     = company;
	this.time        = time;
	this.price       = price;
	this.pointChange = pointChange;
	this.quantity    = quantity;
    }

    public int getSymbolId( )       { return( symbolId ); }
    public String getSymbol( )      { return( symbol ); }
    public String getCompany( )     { return( company ); }
    public String getTime( )        { return( time ); }
    public float getPrice( )        { return( price ); }
    public float getPointChange( )  { return( pointChange ); }
    public float getQuantity( )     { return( quantity ); }

    public void setSymbolId( int symbolId ) { this.symbolId = symbolId; }
    public void setSymbol( String symbol ) { this.symbol = symbol; }
    public void setComapny( String company ) { this.company = company; }
    public void setTime( String time ) { this.time = time; }
    public void setPrice( float price ) { this.price = price; }
    public void setPointChange( float pointChange ) { this.pointChange = pointChange; }
    public void setQuantity( float quantity ) { this.quantity = quantity; }

}
