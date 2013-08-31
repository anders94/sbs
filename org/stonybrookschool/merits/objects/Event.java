package org.stonybrookschool.merits.objects;

public class Event
{
    private int    id, purchases, secondsTill, eventTypeId;
    private String date, eventType, description, rawDate;


    public Event ( )
    {

    }

    public Event ( int id, String date, String eventType,
		   int purchases, int secondsTill )
    {
	this.id          = id;
	this.date        = date;
	this.eventType   = eventType;
	this.purchases   = purchases;
	this.secondsTill = secondsTill;
    }

    public Event ( int id, String date, String eventType,
		   int purchases )
    {
	this.id          = id;
	this.date        = date;
	this.eventType   = eventType;
	this.purchases   = purchases;
    }

    public int getId( ) { return( id ); }
    public String getDate( ) { return( date ); }
    public String getEventType( ) { return( eventType ); }
    public int getEventTypeId( ) { return( eventTypeId ); }
    public int getPurchases( ) { return( purchases ); }
    public int getSecondsTill( ) { return( secondsTill ); }
    public String getDescription( ) { return( description ); }
    public String getRawDate( ) { return( rawDate ); }

    public void setId( int id ) { this.id = id; }
    public void setDate( String date ) { this.date = date; }
    public void setEventType( String eventType ) { this.eventType = eventType; }
    public void setEventTypeId( int eventTypeId ) { this.eventTypeId = eventTypeId; }
    public void setPurchases( int purchases ) { this.purchases = purchases; }
    public void setSecondsTill( int secondsTill ) { this.secondsTill = secondsTill; }
    public void setDescription( String description ) { this.description = description; }
    public void setRawDate( String rawDate ) { this.rawDate = rawDate; }

}
