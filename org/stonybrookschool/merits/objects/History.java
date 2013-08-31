package org.stonybrookschool.merits.objects;

public class History
{
    private String date, giver, reciever, event, reason;
    private int giverUserId, recieverUserId;
    private float  deltaMerits, deltaDemerits;

    public History ( )
    {

    }

    public History ( String date, String giver, String reciever,
		     float deltaMerits, float deltaDemerits, String event, String reason )

    {
	this.date          = date;
	this.giver         = giver;
	this.reciever      = reciever;
	this.deltaMerits   = deltaMerits;
	this.deltaDemerits = deltaDemerits;
	this.event         = event;
	this.reason        = reason;
    }

    public History ( String date, String giver, int recieverUserId,
		     float deltaMerits, float deltaDemerits, String event, String reason )

    {
	this.date           = date;
	this.giver          = giver;
	this.recieverUserId = recieverUserId;
	this.deltaMerits    = deltaMerits;
	this.deltaDemerits  = deltaDemerits;
	this.event          = event;
	this.reason         = reason;
    }

    public String getDate( ) { return( date ); }
    public String getGiver( ) { return( giver ); }
    public int getGiverUserId( ) { return( giverUserId ); }
    public String getReciever( ) { return( reciever ); }
    public int getRecieverUserId( ) { return( recieverUserId ); }
    public float getDeltaMerits( ) { return( deltaMerits ); }
    public float getDeltaDemerits( ) { return( deltaDemerits ); }
    public String getEvent( ) { return( event ); }
    public String getReason( ) { return( reason ); }

    public void setDate( String date ) { this.date = date; }
    public void setGiver( String giver ) { this.giver = giver; }
    public void setGiverUserId( int giverUserId ) { this.giverUserId = giverUserId; }
    public void setReciever( String reciever ) { this.reciever = reciever; }
    public void setRecieverUserId( int recieverUserId ) { this.recieverUserId = recieverUserId; }
    public void setDeltaMerits( float deltaMerits ) { this.deltaMerits = deltaMerits; }
    public void setDeltaDemerits( float deltaDemerits ) { this.deltaDemerits = deltaDemerits; }
    public void setEvent( String event ) { this.event = event; }
    public void setReason( String reason ) { this.reason = reason; }

}
