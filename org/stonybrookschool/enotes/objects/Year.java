package org.stonybrookschool.enotes.objects;

public class Year
{
    private int yearId;
    private String yearName;

    public Year( ) {

    }

    public Year( int yearId, String yearName  )
    {
        this.yearId = yearId;
        this.yearName = yearName;
    }

    public int getYearId( ) { return( yearId ); }
    public String getYearName( ) { return( yearName ); }

    public void setYearId( int yearId ) { this.yearId = yearId; }
    public void setYearName( String yearName ) { this.yearName = yearName; }
}
