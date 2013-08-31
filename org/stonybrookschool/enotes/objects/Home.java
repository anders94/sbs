package org.stonybrookschool.enotes.objects;

public class Home
{
    private int homeId;
    private String homeName;

    public Home( int homeId, String homeName  )
    {
        this.homeId = homeId;
        this.homeName = homeName;
    }

    public int getHomeId( ) { return( homeId ); }
    public String getHomeName( ) { return( homeName ); }

    public void setHomeId( int homeId ) { this.homeId = homeId; }
    public void setHomeName( String homeName ) { this.homeName = homeName; }
} // class Home
