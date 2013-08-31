package org.stonybrookschool.enotes.objects;

public class Period
{
    private int periodId;
    private String periodName;

    public Period( int periodId, String periodName  )
    {
        this.periodId = periodId;
        this.periodName = periodName;
    }

    public int getPeriodId( ) { return( periodId ); }
    public String getPeriodName( ) { return( periodName ); }
}
