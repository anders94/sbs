package org.stonybrookschool.mockMarket.objects;

public class Order
{
    private int    id, userId;
    private String time, action, symbol, status;
    private String transactionTime, comments;
    private float  quantity, price;

    public Order ( )
    {

    }

    public Order ( int id, String time, int userId, 
                   String action, String symbol,
		   String status, float quantity, 
		   String transactionTime,
		   float price, String comments )
    {
	this.id              = id;
	this.time            = time;
        this.userId          = userId;
	this.action          = action;
	this.symbol          = symbol;
	this.status          = status;
	this.quantity        = quantity;
	this.transactionTime = transactionTime;
	this.price           = price;
        this.comments        = comments;
    }

    public int getId( )         { return( id ); }
    public String getTime( )    { return( time ); }
    public int getUserId( )     { return( userId ); }
    public String getAction( )  { return( action ); }
    public String getSymbol( )  { return( symbol ); }
    public String getStatus( )  { return( status ); }
    public float getQuantity( ) { return( quantity ); }
    public String getTransactionTime( ) { return ( transactionTime ); }
    public float getPrice( ) { return( price ); }
    public String getComments( )  { return( comments ); }

    public void setId( int id ) { this.id = id; }
    public void setTime( String time ) { this.time = time; }
    public void setUserId( int userId ) { this.userId = userId; }
    public void setAction( String action ) { this.action = action; }
    public void setSymbol( String symbol ) { this.symbol = symbol; }
    public void setStatus( String status ) { this.status = status; }
    public void setQuantity( float quantity ) { this.quantity = quantity; }
    public void setTransactionTime( String transactionTime ) { this.transactionTime = transactionTime; }
    public void setPrice( float price ) { this.price = price; }
    public void setComments( String comments ) { this.comments = comments; }
}
