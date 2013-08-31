package org.stonybrookschool.merits.objects;

public class User
{
    private int    id, yearId;
    private String first, last, title, date, username, password;
    private String email, faculty, superuser, year;
    private String studentCarPermission, adultCarPermission;
    private float  merits, demerits;

    public User ( )
    {

    }

    public User ( int id, String first, String last, String title,
		  String date, String username, 
		  String email, String faculty, String superuser, 
		  float merits, float demerits, String year, 
                  String studentCarPermission, String adultCarPermission )
    {
	this.id        = id;
	this.first     = first;
	this.last      = last;
	this.title     = title;
	this.date      = date;
	this.username  = username;
	this.email     = email;
	this.faculty   = faculty;
	this.superuser = superuser;
	this.merits    = merits;
	this.demerits  = demerits;
	this.year      = year;
	this.studentCarPermission = studentCarPermission;
	this.adultCarPermission   = adultCarPermission;
    }

    public int getId( )           { return( id ); }
    public String getFirst( )     { return( first ); }
    public String getLast( )      { return( last ); }
    public String getTitle( )     { return( title ); }
    public String getDate( )      { return( date ); }
    public String getUsername( )  { return( username ); }
    public String getPassword( )  { return( password ); }
    public String getEmail( )     { return( email ); }
    public String getFaculty( )   { return( faculty ); }
    public String getSuperuser( ) { return( superuser ); }
    public float getMerits( )     { return( merits ); }
    public float getDemerits( )   { return( demerits ); }
    public String getYear( )      { return( year ); }
    public int getYearId( )       { return( yearId ); }
    public String getStudentCarPermission( ) { return( studentCarPermission ); }
    public String getAdultCarPermission( )   { return( adultCarPermission ); }

    public void setId( int id ) { this.id = id; }
    public void setFirst( String first ) { this.first = first; }
    public void setLast( String last ) { this.last = last; }
    public void setTitle( String title ) { this.title = title; }
    public void setDate( String date ) { this.date = date; }
    public void setUsername( String username ) { this.username = username; }
    public void setPassword( String password ) { this.password = password; }
    public void setEmail( String email ) { this.email = email; }
    public void setFaculty( String faculty ) { this.faculty = faculty; }
    public void setSuperuser( String superuser ) { this.superuser = superuser; }
    public void setMerits( float merits ) { this.merits = merits; }
    public void setDemerits( float demerits ) { this.demerits = demerits; }
    public void setYear( String year ) { this.year = year; }
    public void setYearId( int yearId ) { this.yearId = yearId; }
    public void setStudentCarPermission( String studentCarPermission ) { this.studentCarPermission = studentCarPermission; }
    public void setAdultCarPermission( String adultCarPermission ) { this.adultCarPermission = adultCarPermission; }

}
