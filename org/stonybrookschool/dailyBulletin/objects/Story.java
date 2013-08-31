package org.stonybrookschool.dailyBulletin.objects;

public class Story
{
    private int    id, userId, priority;
    private String date, publishDate, unpublishDate, text, state, head;
    private String student, staff, parent, userName;

    public Story ( )
    {

    }

    public Story ( int id, String date, String publishDate, String unpublishDate,
		   String head, String text, int userId, String state,
		   String student, String staff, String parent, int priority, String userName )

    {
	this.id            = id;
	this.date          = date;
	this.publishDate   = publishDate;
	this.unpublishDate = unpublishDate;
	this.head          = head;
	this.text          = text;
	this.userId        = userId;
	this.state         = state;
        this.student       = student;
        this.staff         = staff;
	this.parent        = parent;
        this.priority      = priority;
	this.userName      = userName;
    }

    public int getId( ) { return( id ); }
    public String getDate( ) { return( date ); }
    public String getPublishDate( ) { return( publishDate ); }
    public String getUnpublishDate( ) { return( unpublishDate ); }
    public String getHead( ) { return( head ); }
    public String getText( ) { return( text ); }
    public int getUserId( ) { return( userId ); }
    public String getState( ) { return( state ); }
    public String getStudent( ) { return( student ); }
    public String getStaff( ) { return( staff ); }
    public String getParent( ) { return( parent ); }
    public int getPriority( ) { return( priority ); }
    public String getUserName( ) { return( userName ); }

    public void setId( int id ) { this.id = id; }
    public void setDate( String date ) { this.date = date; }
    public void setPublishDate( String publishDate ) { this.publishDate = publishDate; }
    public void setUnpublishDate( String unpublishDate ) { this.unpublishDate = unpublishDate; }
    public void setHead( String head ) { this.head = head; }
    public void setText( String text ) { this.text = text; }
    public void setUserId( int userId ) { this.userId = userId; }
    public void setState( String state ) { this.state = state; }
    public void setStudent( String student ) { this.student = student; }
    public void setStaff( String staff ) { this.staff = staff; }
    public void setParent( String parent ) { this.parent = parent; }
    public void setPriority( int priority ) { this.priority = priority; }
    public void setUserName( String userName ) { this.userName = userName; }

}
