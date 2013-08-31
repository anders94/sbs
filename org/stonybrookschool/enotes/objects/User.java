package org.stonybrookschool.enotes.objects;

import java.util.*;
import java.sql.*;

public class User
{
    final static long ONEYEAR = 31536000000L;

    private int userId;
    private boolean obsolete;
    private Timestamp datetime;
    private String first;
    private String last;
    private String title;
    private String username;
    private String password;
    private String email;
    private int groupId;
    private String groupName;
    private int academicId;
    private int yearId;
    private int schoolId; // student ID
    private int counselorId;
    private String counselorName;
    private int homeId;
    private String homeName;
    private String superuser;

    public User(
        int userId,
        boolean obsolete,
        Timestamp datetime,
        String first,
        String last,
        String title,
        String username,
        String password,
        String email,
        int    groupId,
        String groupName,
        int    academicId,
        int    yearId,
        int    schoolId,
        int    counselorId,
        String counselorName,
        int    homeId,
        String homeName,
        String superuser
    ) {

        this.userId = userId;
        this.obsolete = obsolete;
        this.datetime = datetime;
        this.first = first;
        this.last = last;
        this.title = title;
        this.username = username;
        this.password = password;
        this.email = email;
        this.groupId = groupId;
        this.groupName = groupName;
        this.academicId = academicId;
        this.yearId = yearId;
        this.schoolId = schoolId;
        this.counselorName = counselorName;
        this.counselorId = counselorId;
        this.homeId = homeId;
        this.homeName = homeName;
        this.superuser = superuser;
    }

    public void      setCounselorName( String name ) { counselorName = name; }
    public int       getUserId() { return userId; }
    public boolean   getObsolete() { return obsolete; }
    public Timestamp getDatetime() { return datetime; }
    public String    getFirst() { return first; }
    public String    getLast() { return last; }
    public String    getTitle() { return title; }
    public String    getUsername() { return username; }
    public String    getPassword() { return password; }
    public String    getEmail() { return email; }
    public int       getGroupId() { return groupId; }
    public String    getGroupName() { return groupName; }
    public int       getAcademicId() { return academicId; }
    public int       getYearId() { return yearId; }
    public int       getSchoolId() { return schoolId; }
    public int       getCounselorId() { return counselorId; }
    public String    getCounselorName() { return counselorName; }
    public int       getHomeId() { return homeId; }
    public String    getHomeName() { return homeName; }
    public String    getSuperuser() { return superuser; }

    public static User getDefaultUser() {
        User ret = new User(
                       0,      // userId (doAdminUserSubmit depends on 0 here.)
                       false,  // obsolete
                       new Timestamp(0),// datetime
                       "",     // first
                       "",     // last
                       "",     // title
                       "",     // username
                       "",     // password
                       "",     // email
                       0,      // groupId
                       "",     // groupName
                       0,      // academicId
                       0,      // yearId
                       0,      // schoolId
                       0,      // counselorId
                       "",     // counselorName
                       0,      // homeId
                       "",     // homeName
                       "f"     // superuser
                   );
        return ( ret );
    }

    // Returns the current grade given the year they graduate,
    // 3 for 2003, 4 for 2004, etc.
    //
    public static int getGrade(int yearId) {
        GregorianCalendar now = new GregorianCalendar();
        //     now.setTime(new java.util.Date());

        // This might be a problem when running this right around
        // graduation day.  It's not always on 27 May.
        GregorianCalendar grad = new GregorianCalendar(yearId + 2000, 5, 27);

        if (now.before(grad)) {
            return 12 - (int)((grad.getTime().getTime() - now.getTime().getTime()) / ONEYEAR);
        } else {
            return 13 + (int)((now.getTime().getTime() - grad.getTime().getTime()) / ONEYEAR);
        }
    }

    // Returns the current grade of this user.
    //
    public int getGrade() {
        return getGrade(yearId);
    }

    // For example, if this is the 2003-2004 school year, returns 4;
    // If this is the 2004-2005 school year, returns 5;
    // The School year changes on July 1.
    public static int getCurrentYear() {
	GregorianCalendar now = new GregorianCalendar();

	if ( now.get(Calendar.MONTH) < 7 )
	    return( now.get(Calendar.YEAR) );
	else
	    return( now.get(Calendar.YEAR) - 1 );
    }

}
