package org.stonybrookschool.enotes.objects;

public class Cap
{
    private int capId;
    private String offenseName;
    private String punishmentName;
    private boolean obsolete;

    public Cap( int capId, String offenseName, String punishmentName, boolean obsolete  )
    {
        this.capId = capId;
        this.offenseName = offenseName;
        this.punishmentName = punishmentName;
        this.obsolete = obsolete;
    }

    public int getCapId( ) { return( capId ); }
    public String getOffenseName( ) { return( offenseName ); }
    public String getPunishmentName( ) { return( punishmentName ); }
    public boolean getObsolete( ) { return( obsolete ); }

}
