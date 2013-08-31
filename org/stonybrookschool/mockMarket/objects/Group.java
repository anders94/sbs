package org.stonybrookschool.mockMarket.objects;

public class Group
{
    private int    id, ordinal;
    private String name;

    public Group ( )
    {

    }

    public Group ( int id, String name, int ordinal )
    {
	this.id      = id;
	this.name    = name;
	this.ordinal = ordinal;
    }

    public int getId( )      { return( id ); }
    public String getName( ) { return( name ); }
    public int getOrdinal( ) { return( ordinal ); }

    public void setId( int id ) { this.id = id; }
    public void setName( String name ) { this.name = name; }
    public void setOrdinal( int ordinal ) { this.ordinal = ordinal; }
}
