package org.stonybrookschool.enotes.objects;

public class Department
{
    private int departmentId;
    private String departmentName;

    public Department( int departmentId, String departmentName  )
    {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    public int getDepartmentId( ) { return( departmentId ); }
    public String getDepartmentName( ) { return( departmentName ); }
}
