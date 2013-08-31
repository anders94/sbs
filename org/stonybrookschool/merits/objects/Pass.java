package org.stonybrookschool.merits.objects;

public class Pass
{
    private int    id, userId, eventId, transportationId;
    private String date, user, event, eventDate, whereGoing;
    private String whenLeaving, whenReturning, transportation;
    private float  purchasePrice;

    public Pass ( )
    {

    }

    public Pass ( int id, String date, String user, String event,
		  String eventDate, float purchasePrice, int transportationId,
		  String whereGoing, String whenLeaving, String whenReturning )

    {
	this.id               = id;
	this.date             = date;
	this.user             = user;
	this.event            = event;
	this.eventDate        = eventDate;
	this.purchasePrice    = purchasePrice;
	this.transportationId = transportationId;
	this.whereGoing       = whereGoing;
	this.whenLeaving      = whenLeaving;
        this.whenReturning    = whenReturning;
    }

    public Pass ( int id, String date, String user, String event,
		  String eventDate, float purchasePrice, int eventId,
		  int transportationId, String whereGoing, String whenLeaving,
		  String whenReturning )

    {
	this.id               = id;
	this.date             = date;
	this.user             = user;
	this.event            = event;
	this.eventDate        = eventDate;
	this.purchasePrice    = purchasePrice;
	this.eventId          = eventId;
	this.transportationId = transportationId;
	this.whereGoing       = whereGoing;
	this.whenLeaving      = whenLeaving;
        this.whenReturning    = whenReturning;
    }

    public int getId( ) { return( id ); }
    public String getDate( ) { return( date ); }
    public String getUser( ) { return( user ); }
    public int getUserId( ) { return( userId ); }
    public String getEvent( ) { return( event ); }
    public int getEventId( ) { return( eventId ); }
    public String getEventDate( ) { return( eventDate ); }
    public float getPurchasePrice( ) { return( purchasePrice ); }
    public int getTransportationId( ) { return( transportationId ); }
    public String getTransportation( ) { return( transportation ); }
    public String getWhereGoing( ) { return( whereGoing ); }
    public String getWhenLeaving( ) { return( whenLeaving ); }
    public String getWhenReturning( ) { return( whenReturning ); }

    public void setId( int id ) { this.id = id; }
    public void setDate( String date ) { this.date = date; }
    public void setUser( String user ) { this.user = user; }
    public void setUserId( int userId ) { this.userId = userId; }
    public void setEvent( String event ) { this.event = event; }
    public void setEventId( int eventId ) { this.eventId = eventId; }
    public void setEventDate( String eventDate ) { this.eventDate = eventDate; }
    public void setPurchasePrice( float purchasePrice ) { this.purchasePrice = purchasePrice; }
    public void setTransportationId( int transportationId ) { this.transportationId = transportationId; }
    public void setTransportation( String transportation ) { this.transportation = transportation; }
    public void setWhereGoing( String whereGoing ) { this.whereGoing = whereGoing; }
    public void setWhenLeaving( String whenLeaving ) { this.whenLeaving = whenLeaving; }
    public void setWhenReturning( String whenReturning ) { this.whenReturning = whenReturning; }

}
