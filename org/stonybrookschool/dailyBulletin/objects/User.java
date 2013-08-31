package org.stonybrookschool.dailyBulletin.objects;

public class User
{
    private int    id;
    private String email, first, last, publisher, staff;

    public User ( )
    {

    }

    public User ( int id, String email, String first, String last, 
		  String publisher, String staff )

    {
	this.id            = id;
	this.email         = email;
	this.first         = first;
	this.last          = last;
	this.publisher     = publisher;
	this.staff         = staff;
    }

    public int getId( ) { return( id ); }
    public String getEmail( ) { return( email ); }
    public String getFirst( ) { return( first ); }
    public String getLast( ) { return( last ); }
    public String getPublisher( ) { return( publisher ); }
    public String getStaff( ) { return( staff ); }

    public void setId( int id ) { this.id = id; }
    public void setEmail( String email ) { this.email = email; }
    public void setFirst( String first ) { this.first = first; }
    public void setLast( String last ) { this.last = last; }
    public void setPublisher( String publisher ) { this.publisher = publisher; }
    public void setStaff( String staff ) { this.staff = staff; }

}












