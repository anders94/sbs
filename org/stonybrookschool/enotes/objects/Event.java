package org.stonybrookschool.enotes.objects;

public class Event
{
    private int eventId;
    private String eventName;
    private int eventYear;
    private boolean published;

    public Event( int eventId, String eventName, int eventYear, boolean published  )
    {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventYear = eventYear;
        this.published = published;
    }

    public static Event getDefault() {
	return new Event( 1, "1st Semester Deficiency", 4, false );
    }

    public int getEventId( ) { return( eventId ); }
    public String getEventName( ) { return( eventName ); }
    public int getEventYear( ) { return( eventYear ); }
    public boolean getPublished( ) { return( published ); }

}
