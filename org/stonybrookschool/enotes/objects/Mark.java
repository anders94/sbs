package org.stonybrookschool.enotes.objects;

public class Mark
{
    private int markId;
    private String markName;

    public Mark( int markId, String markName  )
    {
        this.markId = markId;
        this.markName = markName;
    }

    public int getMarkId( ) { return( markId ); }
    public String getMarkName( ) { return( markName ); }

    public void setMarkId( int markId ) { this.markId = markId; }
    public void setMarkName( String markName ) { this.markName = markName; }
}
