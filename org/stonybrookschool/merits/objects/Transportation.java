package org.stonybrookschool.merits.objects;

public class Transportation
{
    private int    id;
    private String name;

    public Transportation( )
    {

    }

    public Transportation( int id, String name )
    {
	this.id   = id;
	this.name = name;
    }

    public int getId( ) { return( id ); }
    public String getName( ) { return( name ); }

    public void setId( int id ) { this.id = id; }
    public void setName( String name ) { this.name = name; }

}
