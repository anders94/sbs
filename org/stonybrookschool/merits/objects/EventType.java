package org.stonybrookschool.merits.objects;

public class EventType
{
    private int    id, threshold;
    private String name, description, transportation, meal;
    private float  lowPrice, highPrice;

    public EventType ( )
    {

    }

    public EventType ( int id, String name )
    {
	this.id   = id;
	this.name = name;
    }

    public EventType ( int id, String name, String meal )
    {
	this.id   = id;
	this.name = name;
	this.meal = meal;
    }

    public EventType ( int id, String name, 
		       String description,
		       float lowPrice,
		       float highPrice,
		       int threshold,
		       String transportation )
    {
        this.id             = id;
        this.name           = name;
	this.description    = description;
	this.lowPrice       = lowPrice;
	this.highPrice      = highPrice;
	this.threshold      = threshold;
	this.transportation = transportation;
    }

    public int getId( ) { return( id ); }
    public String getName( ) { return( name ); }
    public String getDescription( ) { return( description ); }
    public float getLowPrice( ) { return( lowPrice ); }
    public float getHighPrice( ) { return( highPrice ); }
    public int getThreshold( ) { return( threshold ); }
    public float getPrice( int purchases )
    {
	if ( purchases >= threshold )
	    return( highPrice );
	else
	    return( lowPrice );
    }
    public String getTransportation( ) { return( transportation ); }
    public String getMeal( ) { return( meal ); }

    public void setId( int id ) { this.id = id; }
    public void setName( String name ) { this.name = name; }
    public void setDescription( String description ) { this.description = description; }
    public void setLowPrice( float lowPrice ) { this.lowPrice = lowPrice; }
    public void setHighPrice( float highPrice ) { this.highPrice = highPrice; }
    public void setThreshold( int threshold ) { this.threshold = threshold; }
    public void setTransportation( String transportation ) { this.transportation = transportation; }
    public void setMeal( String meal ) { this.meal = meal; }

}
