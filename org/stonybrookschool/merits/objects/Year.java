package org.stonybrookschool.merits.objects;

public class Year
{
    private int    id, year;
    private String classText;

    public Year ( )
    {

    }

    public Year ( int id, int year, String classText )
    {
	this.id    = id;
	this.year  = year;
	this.classText = classText;
    }

    public int getId( ) { return( id ); }
    public int getYear( ) { return( year ); }
    public String getClassText( ) { return( classText ); }

    public void setId( int id ) { this.id = id; }
    public void setYear( int year ) { this.year = year; }
    public void setClassText( String classText ) { this.classText = classText; }

}
