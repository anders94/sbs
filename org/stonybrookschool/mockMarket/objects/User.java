package org.stonybrookschool.mockMarket.objects;

public class User
{
    private int    id;
    private String first, last, date, username, password;
    private String email, superuser, worthDate;
    private float  cash, worth;

    public User ( )
    {

    }

    public User ( int id, String first, String last,
		  String date, String username, 
		  String email, String superuser, 
		  float cash, float worth,
		  String worthDate )
    {
	this.id        = id;
	this.first     = first;
	this.last      = last;
	this.date      = date;
	this.username  = username;
	this.email     = email;
	this.superuser = superuser;
	this.cash      = cash;
	this.worth     = worth;
	this.worthDate = worthDate;
    }

    public int getId( )           { return( id ); }
    public String getFirst( )     { return( first ); }
    public String getLast( )      { return( last ); }
    public String getDate( )      { return( date ); }
    public String getUsername( )  { return( username ); }
    public String getPassword( )  { return( password ); }
    public String getEmail( )     { return( email ); }
    public String getSuperuser( ) { return( superuser ); }
    public float getCash( )       { return( cash ); }
    public float getWorth( )      { return( worth ); }
    public String getWorthDate( ) { return( worthDate ); }

    public void setId( int id ) { this.id = id; }
    public void setFirst( String first ) { this.first = first; }
    public void setLast( String last ) { this.last = last; }
    public void setDate( String date ) { this.date = date; }
    public void setUsername( String username ) { this.username = username; }
    public void setPassword( String password ) { this.password = password; }
    public void setEmail( String email ) { this.email = email; }
    public void setSuperuser( String superuser ) { this.superuser = superuser; }
    public void setCash( float cash ) { this.cash = cash; }
    public void setWorth( float worth ) { this.worth = worth; }
    public void setWorthDate( String worthDate ) { this.worthDate = worthDate; }

}
