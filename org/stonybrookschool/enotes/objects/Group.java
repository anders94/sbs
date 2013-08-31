package org.stonybrookschool.enotes.objects;

public class Group
{
    private int groupId;
    private String groupName;
    private String groupDescription;

    public Group( int groupId, String groupName, String groupDescription  )
    {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
    }

    public int getGroupId( ) { return( groupId ); }
    public String getGroupName( ) { return( groupName ); }
    public String getGroupDescription( ) { return( groupDescription ); }

    public void setGroupId( int groupId ) { this.groupId = groupId; }
    public void setGroupName( String groupName ) { this.groupName = groupName; }
    public void setGroupDescription( String groupDescription ) { this.groupDescription = groupDescription; }
}
