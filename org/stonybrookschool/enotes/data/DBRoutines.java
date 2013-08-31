package org.stonybrookschool.enotes.data;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.text.*;

import com.caucho.sql.DBPool;

import org.stonybrookschool.enotes.objects.*;

/* DBRoutines is the only class used for database access. */
/* That is standard across all SBS Web servlets.          */

public class DBRoutines
{
    protected static String poolString = "jdbc/eNotes";

    private static final SimpleDateFormat logsdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );

    public static final int ALL = -1;
    public static final int DISCIPLINE = 1;
    public static final int COMMENT    = 2;
    public static final int QUICKNOTE  = 4;

    public static final int PENDING    = 1;
    public static final int PUBLISHED  = 2;
    public static final int PROOFED    = 4;
    public static final int DELETED    = 8;

    public static final int COUNSELOR   = 1;
    public static final int GRADE_CHAIR = 2;

    public static final SimpleDateFormat mySQLsdf = new SimpleDateFormat( "yyyy-MM-dd hh:mm:00" );

    // You might ask why I don't use join clauses to reduce the number of SQL statements.  This is
    // my reason.  All records have related fields and non-related fields.  If I use a join to
    // grab a record that is not completely filled out, I don't get the non-related fields.  For
    // example, consider the SQL statement 
    //
    //     select username,groupName from users,groups 
    //     where users.groupId=groups.groupId and userId=761;
    //
    // If groupId is zero for this user, the result set will be empty even though a user
    // exists whose userId is 761.  
 

    // The Strings below must be final because there will be multiple instances of DBRoutines whenever
    // more than one person is logged on at a time.  Nothing global to DBRoutines can be changed at
    // runtime.
    //
    // Each select string here is used with a PreparedStatement object of a specific name.  There are
    // ad hoc SQL statements inside various routines, but these are the common statements.
    //
    // PreparedStatement quicknoteStmt;
    private final static String quicknoteSelect =
        "select obsolete,timestamp,studentUserId,sectionId,quicknoteTypeId,commentText,published,status " +
        "from quicknotes where (status<>'deleted') and (quicknoteId=?);";

    // PreparedStatement quicknoteNameStmt;
    private final static String quicknoteNameSelect =
        "select quicknoteTypeName from quicknoteTypes where quicknoteTypeId=?;";

    // PreparedStatement disciplineStmt;
    private final static String disciplineSelect =
        "select obsolete,timestamp,studentUserId,sectionId,capId,commentText,status " +
        "from disciplines where (status<>'deleted') and (disciplineId=?);";

    // PreparedStatement capStmt;
    private final static String capSelect =
        "select offenseName,punishmentName,obsolete " +
        "from crimesAndPunishments where capId=?;";

    // PreparedStatement commentStmt;
    private final static String commentSelect =
        "select obsolete,timestamp,studentUserId,sectionId,eventId,markId,commentText,status " +
        "from comments where (status<>'deleted') and (commentId=?);";

    // PreparedStatement sectionStmt;
    private final static String sectionSelect =
        "select name from sections where id=?;";

    // PreparedStatement eventStmt;
    private final static String eventSelect =
        "select eventName from events where eventId=?;";

    // PreparedStatement markStmt;
    private final static String markSelect =
        "select markName from marks where markId=?;";

    // PreparedStatement groupStmt;
    private final static String groupSelect =
        "select groupName from groups where groupId=?;";

    // PreparedStatement homeStmt;
    private final static String homeSelect =
        "select homeName from homes where homeId=?";

    // PreparedStatement userStmt;
    private final static String userSelect =
        "select userId,obsolete,timestamp,first,last,title,username,password,email,groupId," +
        "academicId,yearId,schoolId,counselorUserId,homeId,superuser " +
        "from users where userId=?;";

    // PreparedStatement departmentStmt;
    private final static String departmentSelect =
	"select departmentId,departmentName from departments where departmentId=?;";

    // PreparedStatement periodStmt;
    private final static String periodSelect = 
	"select periodId,periodName from periods where periodId=?;";

    private final static String propertiesSelect =
        "select value from properties where name=?;";

    public static String escapeTics( String s )
    {
        s = replace( s, '\'', "\\'" );

        return( s );
    }

    public static String replace( String oldString, char a, String b )
    {
        String newString = new String( );

        StringCharacterIterator sci = new StringCharacterIterator( oldString );

        while ( sci.getIndex( ) < sci.getEndIndex( ) ) {
            if ( sci.current( ) == a )
                newString = newString + b;
            else
                newString = newString + sci.current( );
            sci.next( );
        }

        return ( newString );
    }

    public static String replace( String string, String from, String to ) {
        if ( from.equals( "" ) )
            return ( string );

        StringBuffer buf = new StringBuffer( 2 * string.length( ) );
        int previndex = 0;
        int index = 0;
        int flen = from.length();

        while ( true ) {
            index = string.indexOf( from, previndex );
            if (index == -1) {
                buf.append( string.substring( previndex ) );
                break;
            }
            buf.append( string.substring( previndex, index ) + to );
            previndex = index + flen;

        }

        return( buf.toString( ) );

    }

    public static boolean checkPassword( String username, String password ) {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        String truePassword = null;
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );

            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " eNotes logon attempt: " );
            if ( username != null) log.append( username );
	    System.out.println( log.toString() );

            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            username = username.toLowerCase();

            // username is not case-sensitive because BINARY is not used
            // in the SQL statement or set as a column attribute.
            rs = stmt.executeQuery( "select password from users " +
                                    "where username='" + username + "';" );

            if ( rs.next( ) ) truePassword = rs.getString( 1 );

	    rs.close();
	    stmt.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " eNotes/DBRoutines.java: checkPassword(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        // password compare is case-sensitive because String.equals is
        // case-sensitive
        if ( password.equals( truePassword ) )
            return ( true );
        else
            return ( false );

    } // checkPassword()

    private static String getHomeName( int homeId, PreparedStatement homeStmt ) {
        ResultSet rs;
        String retStr = "undefined";
	StringBuffer log;

        try {
            homeStmt.setInt( 1, homeId );
            rs = homeStmt.executeQuery();
            if (rs.next()) retStr = rs.getString(1);
	    rs.close();
	    
        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getHomeName(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        }

        return retStr;
    } // getHomeName;

    private static String getGroupName( int groupId, PreparedStatement groupStmt ) {
        ResultSet rs;
        String retStr = "undefined";
	StringBuffer log;

        try {
            groupStmt.setInt( 1, groupId );
            rs = groupStmt.executeQuery();
            if (rs.next()) retStr = rs.getString(1);
	    rs.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getGroupName(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        }

        return retStr;
    } // getGroupName;

    
    public static User getUser( int iUserId ) {
	DBPool pool;
        Connection conn = null;
        User u = User.getDefaultUser();
	PreparedStatement homeStmt;
	PreparedStatement groupStmt;
	PreparedStatement userStmt;
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );

            homeStmt = conn.prepareStatement( homeSelect );
            groupStmt = conn.prepareStatement( groupSelect );
            userStmt = conn.prepareStatement( userSelect );

            u = _getUser( iUserId, homeStmt, groupStmt, userStmt );

	    homeStmt.close();
	    groupStmt.close();
	    userStmt.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getUser( iUserId ): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return u;
    } // getUser( iUserId );

    // Returns a User object containing all fields
    // given the userId.
    private static User _getUser( 
	    int iUserId, 
	    PreparedStatement homeStmt,
	    PreparedStatement groupStmt,
	    PreparedStatement userStmt)
    {
        ResultSet rs;
        ResultSet rss;
        User u = User.getDefaultUser();
        StringBuffer counselor = new StringBuffer("undefined");
        int groupId;
        int homeId;
        int counselorId;
	StringBuffer log;

        try {
            userStmt.setInt( 1, iUserId );
            rs = userStmt.executeQuery();
            if ( rs.next( ) ) {
                groupId = rs.getInt( 10 );
                counselorId = rs.getInt( 14 );
                homeId = rs.getInt( 15 );
                u = new User(
                        rs.getInt(1),      // userId
                        rs.getBoolean(2),  // obsolete
                        rs.getTimestamp(3),// datetime
                        rs.getString(4),   // first
                        rs.getString(5),   // last
                        rs.getString(6),   // title
                        rs.getString(7),   // username
                        rs.getString(8),   // password
                        rs.getString(9),   // email
                        groupId,           // groupId
                        getGroupName( groupId, groupStmt ), // groupName
                        rs.getInt(11),     // academicId
                        rs.getInt(12),     // yearId
                        rs.getInt(13),     // schoolId
                        counselorId,       // counselorId
                        "",                // counselorName
                        homeId,            // homeId
                        getHomeName( homeId, homeStmt ), // homeName
                        rs.getString(16)   // superuser
                    );

                userStmt.setInt( 1, counselorId );
                rss = userStmt.executeQuery();
                if ( rss.next() ) {
                    counselor = new StringBuffer();
                    counselor.append( rss.getString(6) ); // title
                    counselor.append( " " );
                    counselor.append( rss.getString(4) ); // first
                    counselor.append( " " );
                    counselor.append( rss.getString(5) ); // last
                }
                u.setCounselorName( counselor.toString() );

		rss.close();

            } // if ( rs.next() )

	    rs.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: _getUser( iUserId, statements ): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        }

        return ( u );
    } // _getUser( iUserId, statements )


    public static Vector getUsersNotSuperUsers() {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        String retStr = "undefined";
        Vector us = new Vector();
	PreparedStatement homeStmt;
	PreparedStatement groupStmt;
	PreparedStatement userStmt;
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs = stmt.executeQuery(
                      "select userId from users where superuser='f' "
                      + "order by last,first;");

            homeStmt = conn.prepareStatement( homeSelect );
            groupStmt = conn.prepareStatement( groupSelect );
            userStmt = conn.prepareStatement( userSelect );

            while (rs.next()) us.add( _getUser( rs.getInt(1), homeStmt, groupStmt, userStmt ));

	    rs.close();
	    homeStmt.close();
	    groupStmt.close();
	    userStmt.close();
	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getUsersNotSuperUsers(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return (us);
    } // getUsersNotSuperUsers()

    // Looks overly complicated, but I want to use
    // _get User( iUserId ), and that gets called hundereds or thousands
    // of times, so it has to be fast.
    public static User getUser( String username ) {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        User u = User.getDefaultUser();
	PreparedStatement homeStmt;
	PreparedStatement groupStmt;
	PreparedStatement userStmt;
	StringBuffer log;

        try {
            // Use the username to get a userId.
            // Then call _get User( userId ) to get the entire user
            // record--this is set up to all work together efficiently.
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );

            homeStmt = conn.prepareStatement( homeSelect );
            groupStmt = conn.prepareStatement( groupSelect );
            userStmt = conn.prepareStatement( userSelect );

            stmt = conn.createStatement( );
            rs = stmt.executeQuery(
                      "select u.userId " +
                      "from users u " +
                      "where u.username='" + username + "';" );

            if (rs.next()) {
                u = _getUser( rs.getInt(1), homeStmt, groupStmt, userStmt );
            }

	    rs.close();
	    stmt.close();
	    homeStmt.close();
	    groupStmt.close();
	    userStmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getUser( username ): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return u;
    } // getUser( username )

    public static Vector getAssociations( int iUserId ) {
	DBPool pool;
        Connection conn = null;
        PreparedStatement stmt;
        ResultSet rs;
	PreparedStatement homeStmt;
	PreparedStatement groupStmt;
	PreparedStatement userStmt;

        User u = getUser( iUserId );
        Vector v = new Vector();
        String statement;
	StringBuffer log;

        if ( "parent".equals( u.getGroupName() ) ) {
            // this is a parent.  so get all his students.

            statement = "select studentUserId from parentsToStudents "
                        + "where parentUserId=?;";

        } else if ( "student".equals( u.getGroupName() )) {
            // this is a student.  so get all his parents.

            statement = "select parentUserId from parentsToStudents "
                        + "where studentUserId=?;";
        } else {
            return ( v );
        }

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );

            homeStmt = conn.prepareStatement( homeSelect );
            groupStmt = conn.prepareStatement( groupSelect );
            userStmt = conn.prepareStatement( userSelect );

            stmt = conn.prepareStatement( statement );
            stmt.setInt( 1, iUserId );
            rs = stmt.executeQuery( );

            while (rs.next()) v.add( _getUser( rs.getInt(1), homeStmt, groupStmt, userStmt ) );

	    rs.close();
	    homeStmt.close();
	    groupStmt.close();
	    userStmt.close();
	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getAssociations(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return ( v );
    } // getAssociations()

    public static User insertUser( User u, StringBuffer message ) {
        // is the username being used?
        // can I get groupId?
        // can I get homeId?
        // If this is all ok, try to insert the record.
        // If anything fails, return a default User.
        // If it works, return the user.
        // store returned result, whatever it is, in u2.
	DBPool pool;
        Connection conn = null;
        ResultSet rs;  // to see what I updated.
        Statement stmt;
	PreparedStatement homeStmt;
	PreparedStatement groupStmt;
	PreparedStatement userStmt;
        User u2 = User.getDefaultUser();
        String groupId;
        String homeId;
        String obsolete;
        String academicId;
        String yearId;
        String schoolId;
        String counselorId;
        String userId;
	StringBuffer log;

        if ( 0 == u.getUserId() ) {
            // if this is a new user, he must have a new
            // username.
            u2 = getUser( u.getUsername() );
            if ( 0 != u2.getUserId() ) {
                message.delete(0,message.length());
                message.append("that username is being used");
                return u;
            }
        }
        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            obsolete = (u.getObsolete() ? "1" : "0");
            groupId = new Integer( u.getGroupId() ).toString();
            homeId = new Integer( u.getHomeId() ).toString();
            counselorId = new Integer( u.getCounselorId() ).toString();
            academicId = new Integer( u.getAcademicId() ).toString();
            yearId = new Integer( u.getYearId() ).toString();
            schoolId = new Integer( u.getSchoolId() ).toString();

            if ( 0 == u.getUserId() ) {
                // this is a new user
                stmt.executeUpdate(
                    "insert into users (" +
                    "obsolete," +
                    "first," +
                    "last," +
                    "title," +
                    "username," +
                    "password," +
                    "email," +
                    "groupId," +
                    "academicId," +
                    "yearId," +
                    "schoolId," +
                    "counselorUserId," +
                    "homeId) " +
                    " " +
                    "values(" +
                    obsolete+"," +
                    "'" + replace( u.getFirst(), "'", "\\'" ) + "'," +
                    "'" + replace( u.getLast(), "'", "\\'" ) + "'," +
                    "'" + replace( u.getTitle(), "'", "\\'" ) + "'," +
                    "'" + replace( u.getUsername(), "'", "\\'" ) + "'," +
                    "'" + replace( u.getPassword(), "'", "\\'" ) + "'," +
                    "'" + replace( u.getEmail(), "'", "\\'" ) + "'," +
                    groupId+"," +
                    academicId+"," +
                    yearId+"," +
                    schoolId+"," +
                    counselorId+"," +
                    homeId +
                    ");"
                ); 
            } else {
                // this is an update to an existing user
                userId = new Integer( u.getUserId() ).toString();
                stmt.executeUpdate(
                    "update users set " +
                    "obsolete=" + obsolete + "," +
                    "first='" + replace( u.getFirst(), "'", "\\'" ) + "'," +
                    "last='" + replace( u.getLast(), "'", "\\'" ) + "'," +
                    "title='" + replace( u.getTitle(), "'", "\\'" ) + "'," +
                    "username='" + replace( u.getUsername(), "'", "\\'" ) + "'," +
                    "password='" + replace( u.getPassword(), "'", "\\'" ) + "'," +
                    "email='" + replace( u.getEmail(), "'", "\\'" ) + "'," +
                    "groupId=" + groupId + "," +
                    "academicId=" + academicId + "," +
                    "yearId=" + yearId + "," +
                    "schoolId=" + schoolId + "," +
                    "counselorUserId=" + counselorId + "," +
                    "homeId=" + homeId + " " +
                    "where userId=" + userId + ";"
                );
            } // if()

            // Execution got here, so we didn't get an exception
            // on the update.  Now get the new user using userId
            // to see a copy of the fields as they were added.

            rs = stmt.executeQuery(
                     "select userId from users "
                     + "where username='" + replace( u.getUsername(), "'", "\\'" ) + "';"
                 );

	    homeStmt = conn.prepareStatement( homeSelect );
	    groupStmt = conn.prepareStatement( groupSelect );
	    userStmt = conn.prepareStatement( userSelect );

            // set u2 to the record we just added.
            if (rs.next()) {
                u2 = _getUser( rs.getInt( 1 ), homeStmt, groupStmt, userStmt );
                if (0 == u2.getUserId()) {
                    message.delete(0, message.length());
                    message.append( "could not retrieve new or updated user");
                    // on my system I can look in
                    //     /usr/local/resin-2.1.4/log/stderr.log
                    // for error messages.
                } else {
                    message.delete(0, message.length());
                    message.append( "OK" );
                }
            } else {
                u2 = u;
                message.delete(0, message.length());
                message.append( "could not retrieve new user id" );
            }

	    rs.close();
	    stmt.close();
	    homeStmt.close();
	    groupStmt.close();
	    userStmt.close();
	    
        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: insertUser(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );

            message.delete(0, message.length());
            message.append( e.getMessage() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return u2;
    } // insertUser()


    public static String insertSection( Section s ) {
	// validate periodId
	// validate teacherUserId
	// validate departmentId
	String message = "";
	DBPool pool;
        Connection conn = null;
	StringBuffer stmtStmt = new StringBuffer();
        ResultSet rs;
	Statement stmt;
	PreparedStatement userStmt;
	PreparedStatement departmentStmt;
	PreparedStatement periodStmt;
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

	    periodStmt = conn.prepareStatement( periodSelect );
	    departmentStmt = conn.prepareStatement( departmentSelect );
	    userStmt = conn.prepareStatement( userSelect );

            // There are versions of MySql that do not enforce referential integrity.
	    // Make sure we work with all versions.
	    periodStmt.setInt( 1, s.getPeriodId() );
	    rs = periodStmt.executeQuery();
	    if (!rs.next()) message = "periodId not found";
	    else {
		departmentStmt.setInt( 1, s.getDepartmentId() );
		rs.close();
		rs = departmentStmt.executeQuery();
		if (!rs.next()) message = "departmentId not found";
		else {
		    userStmt.setInt( 1, s.getTeacherUserId());
		    rs.close();
		    rs = userStmt.executeQuery();
		    if (!rs.next()) message = "teacherId not found";
		    else {
			if ( 0 == s.getSectionId() ) {
			    stmtStmt.append("insert into sections (");
			    stmtStmt.append("name,");
			    stmtStmt.append("yearId,");
			    stmtStmt.append("periodId,");
			    stmtStmt.append("teacherUserId,");
			    stmtStmt.append("sectionNumber,");
			    stmtStmt.append("departmentId,");
			    stmtStmt.append("honors");

			    stmtStmt.append(") values (");
			    stmtStmt.append(s.getSectionName()); stmtStmt.append(",");
			    stmtStmt.append(s.getYearId()); stmtStmt.append(",");
			    stmtStmt.append(s.getPeriodId()); stmtStmt.append(",");
			    stmtStmt.append(s.getTeacherUserId()); stmtStmt.append(",");
			    stmtStmt.append(s.getSectionNumber()); stmtStmt.append(",");
			    stmtStmt.append(s.getDepartmentId());  stmtStmt.append(",");
			    stmtStmt.append((s.getHonors() ? "1" : "0"));

			    stmtStmt.append(");");
			} else {
			    stmtStmt.append("update sections set ");
			    stmtStmt.append("name=" + s.getSectionName());
			    stmtStmt.append(",yearId=" + s.getYearId());
			    stmtStmt.append(",periodId=" + s.getPeriodId());
			    stmtStmt.append(",teacherUserId=" + s.getTeacherUserId());
			    stmtStmt.append(",sectionNumber=" + s.getSectionNumber());
			    stmtStmt.append(",departmentId=" + s.getDepartmentId());
			    stmtStmt.append(",honors=" + (s.getHonors() ? "1" : "0"));

			    stmtStmt.append(" where id=" + s.getSectionId() + ";" );
			}
			stmt.executeUpdate( stmtStmt.toString() );

			// That's it.  We're done.
			message = "OK";

		    } // check teacherId
		} // check departmentId
	    } // check periodId

	    rs.close();
	    stmt.close();
	    userStmt.close();
	    departmentStmt.close();
	    periodStmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: insertSection(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
	    
            message = e.getMessage();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

	return( message );
    } // insertSection;


    public static void delSection( int sectionId ) {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
	StringBuffer log;
	
        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

	    /* This is a question to Thom:
	     * When deleting sections do I delete from the following tables
	     * where sectionId = sectionId?
	     *
	     *   comments
	     *   disciplines
	     *   quicknotes
	     *   studentsToSections
	     */

	     stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: delSection(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

    } // delSection()

 
    public static Vector getByGroupName( String groupName ) {
	DBPool pool;
        Connection conn = null;
        Vector v = new Vector();
        String groupId;
        ResultSet rs;
        Statement stmt;
	PreparedStatement homeStmt;
	PreparedStatement groupStmt;
	PreparedStatement userStmt;
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            rs = stmt.executeQuery(
                     "select groupId from groups where groupName='"+ groupName +"';"
                 );

	    // none of this applies to teachers, but its nice to have
	    // one method that does everything.
	    homeStmt = conn.prepareStatement( homeSelect );
	    groupStmt = conn.prepareStatement( groupSelect );
	    userStmt = conn.prepareStatement( userSelect );

            if (rs.next()) {
                groupId = rs.getString(1);

		rs.close();
                rs = stmt.executeQuery(
                         "select userId from users where groupId=" + groupId + " order by last,first;"
                     );

                // add the users to the vector
                while (rs.next()) v.add( _getUser( rs.getInt( 1 ), homeStmt, groupStmt, userStmt ) );
            } // if

	    rs.close();
	    stmt.close();
	    userStmt.close();
	    groupStmt.close();
	    homeStmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getByGroupName(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return v;
    } // getByGroupName

    public static Vector getHomes() {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs = stmt.executeQuery(
                     "select homeId,homeName from homes order by homeName;"
                 );

            while(rs.next()) v.add( new Home( rs.getInt(1), rs.getString(2) ) );

	    rs.close();
	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getHomes(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return v;
    } // getHomes

    public static Vector getGroups() {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            rs = stmt.executeQuery(
                     "select groupId,groupName,groupDescription " +
                     "from groups;"
                 );

            while (rs.next())
                v.add(new Group(rs.getInt(1), rs.getString(2), rs.getString(3)));

	    rs.close();
	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getGroups(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return v;
    } // getGroups


    public static Vector getOthers( int userId ) {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        String strStmt;
        ResultSet rs;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            rs = stmt.executeQuery(
                     "select groupName from users,groups "+
                     "where "+
                     "users.groupId=groups.groupId and "+
                     "userId="+ userId +";"
                 );
            if (rs.next()) {
                if ("parent".equals( rs.getString( 1 ) ) ) {
                    // userId refers to a parent, so get a list of students
                    v = getByGroupName( "student" );
                } else {
                    // userId refers to a student, so get a list of parents
                    v = getByGroupName( "parent" );
                }
            } // if that didn't work, just return an empty vector

	    rs.close();
	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getOthers(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return v;
    } // getOthers

    public static void delAddAssoc(
        int oldParentId, int oldStudentId,
        int newParentId, int newStudentId )
    {

        delAssoc( oldParentId, oldStudentId );
        insertAssoc( newParentId, newStudentId );

    } // delAddAssoc()

    public static void delAssoc( int oldParentId, int oldStudentId ) {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        int count = 0;
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            rs = stmt.executeQuery(
                     "select count(parentUserId) from parentsToStudents p "+
                     "where "+
                     "p.parentUserId=" + oldParentId + " and "+
                     "p.studentUserId=" + oldStudentId + ";"
                 );

            if (rs.next()) count = rs.getInt(1);

            // Usually, I should set an "obsolete flag" or something
	    // instead of actually deleting the record.  But here I
	    // have no choice.
            stmt.executeUpdate(
                "delete from parentsToStudents " +
                "where "+
                "parentUserId=" + oldParentId + " and "+
                "studentUserId =" + oldStudentId + ";"
            );

            if ( count > 1 ) {
                // Oops.  The student was added more than once.
                // Put one copy back in.

                insertAssoc( oldParentId, oldStudentId );

            } // if there was more than one entry

	    rs.close();
	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: delAssoc(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

    } // delAssoc()

    public static void insertAssoc( int parentId, int studentId ) {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            stmt.executeUpdate(
                "insert into parentsToStudents (parentUserId,studentUserId) " +
                "values ("+
                parentId + "," +
                studentId + ");"
            );

	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: insertAssoc(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

    } // insertAssoc()


    public static Vector getStudentsInThisSection( int sectionId ) {
	DBPool pool;
        Connection conn = null;
        ResultSet rs;
        Statement stmt;
	PreparedStatement homeStmt;
	PreparedStatement groupStmt;
	PreparedStatement userStmt;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            rs = stmt.executeQuery(
                     "select userId,last,first,sectionId " +
                     "from users u,studentsToSections s " +
                     "where u.userId=s.studentUserId and s.sectionId=" + sectionId +
                     " order by u.last,u.first;");

            homeStmt = conn.prepareStatement( homeSelect );
            groupStmt = conn.prepareStatement( groupSelect );
            userStmt = conn.prepareStatement( userSelect );

            while (rs.next()) v.add( _getUser( rs.getInt(1), homeStmt, groupStmt, userStmt ) );

	    rs.close();
	    stmt.close();
	    homeStmt.close();
	    groupStmt.close();
	    userStmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getStudentsInThisSection(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return v;
    } // getStudentsInThisSection()

    // returns a list of students who match the criteria.
    // values of 0 in homeId or yearId mean get all
    public static Vector getStudentsHomeYear( int homeId, int yearId ) {
	DBPool pool;
        Connection conn = null;
        ResultSet rs;
        Statement stmt;
	PreparedStatement homeStmt;
	PreparedStatement groupStmt;
	PreparedStatement userStmt;
        StringBuffer b = new StringBuffer();
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            b.append( "select userId from users,groups ");
            b.append( "where (users.groupId=groups.groupId) and ");
            b.append(       "(groups.groupName='student') " );
            if ( homeId != 0 ) {
                b.append( "and (homeId=" + homeId );
                if (yearId == 0)
                    b.append(") ");
                else
                    b.append(") ");
            }
            if ( yearId != 0 ) {
                b.append( "and (yearId=" + yearId + ") ");
            }
            b.append("order by users.last,users.first;");

            rs = stmt.executeQuery( b.toString() );

            homeStmt  = conn.prepareStatement( homeSelect );
            groupStmt = conn.prepareStatement( groupSelect );
            userStmt  = conn.prepareStatement( userSelect );

            while (rs.next()) v.add( _getUser( rs.getInt(1), homeStmt, groupStmt, userStmt ) );

	    rs.close();
	    stmt.close();
	    homeStmt.close();
	    groupStmt.close();
	    userStmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getStudentsHomeYear(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return v;
    } // getStudentsHomeYear()


    // get the section given the sectionId.
    //
    // this gets called a lot.  I'm going to HAVE to speed this up.
    // I don't believe in wasting MIPs.  My plan is to make a
    // _getSection() method that doesn't make a connection
    // and uses PreparedStatements.
    public static Section getSection( int sectionId ) {
	DBPool pool;
        Connection conn = null;
        Statement stmt;  // main statement
        Statement sstmt; // subsequent statement
        ResultSet rs;
        ResultSet rss;
	// These don't need to be initialized because getDefault() takes care of it.
        int periodId;
        int teacherUserId;
        int departmentId;
        String periodName;
        StringBuffer teacherName;
	String teacherFirst;
	String teacherLast;
	String teacherTitle;
        String departmentName;
	PreparedStatement userStmt;
        Section section = Section.getDefault();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            sstmt = conn.createStatement( );

            rs = stmt.executeQuery(
                     "select "+
                     "s.name," +
                     "s.yearId," +
                     "s.periodId," +
                     "s.teacherUserId," +
                     "s.sectionNumber," +
                     "s.departmentId," +
                     "s.honors " +
                     "from sections s " +
                     "where id=" + sectionId + ";"
                 );

            if (rs.next()) {

                // valid sectionId, so fill in all the name fields.

                periodId = rs.getInt( 3 );
                teacherUserId = rs.getInt( 4 );
                departmentId = rs.getInt( 6 );
                periodName = "";
                teacherName = new StringBuffer();
                departmentName = "";
		teacherFirst = "";
		teacherLast = "";
		teacherTitle = "";

                // Normally I would call _getUser(), but in this case I get everything I need
                // using userStmt, so I'll just do that.
                userStmt = conn.prepareStatement( userSelect );
                userStmt.setInt( 1, teacherUserId );
                rss = userStmt.executeQuery();
                if ( rss.next() ) {
		    teacherFirst = rss.getString( 4 );
		    teacherLast = rss.getString( 5 );
		    teacherTitle = rss.getString( 6 );
                    teacherName.append( teacherLast );
                    teacherName.append( ", " );
                    teacherName.append( teacherFirst );
                    teacherName.append( " " );
                    teacherName.append( teacherTitle );
                }

		rss.close();
		userStmt.close();

                rss = sstmt.executeQuery(
                          "select periodName from periods "+
                          "where periodId="+ periodId +";" );

                if (rss.next()) periodName = rss.getString( 1 );

		rss.close();

                rss = sstmt.executeQuery(
                          "select departmentName from departments "+
                          "where departmentId="+ departmentId +";" );

                if (rss.next()) departmentName = rss.getString( 1 );

		rss.close();

                // Now I've translated periodId to periodName,
                // departmentId to departmentName, etc.
                // Now construct the section object.
                section = new Section(
                              sectionId,
                              rs.getString( 1 ),
                              rs.getInt( 2 ),
                              periodId,
                              periodName,
                              teacherUserId,
                              teacherName.toString(),
			      teacherFirst,
			      teacherLast,
			      teacherTitle,
                              rs.getString( 5 ),
                              departmentId,
                              departmentName,
                              rs.getBoolean( 7 )
                          );

            } // if found the sectionId

	    rs.close();
	    stmt.close();
	    sstmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getSection(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return section;

    } // getSection

    // delete a student from a section
    public static void delSectionStudent( int sectionId, int studentId ) {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        int count = 0;
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            rs = stmt.executeQuery(
                     "select count(sectionId) from studentsToSections s "+
                     "where "+
                     "s.studentUserId=" + studentId + " and "+
                     "s.sectionId=" + sectionId + ";"
                 );

            if (rs.next()) count = rs.getInt(1);

	    // As a rule, never issue DELETE FROM.  Set the "obsolete flag"
	    // instead.  But here I have no choice.
            stmt.executeUpdate(
                "delete from studentsToSections " +
                "where "+
                "studentUserId=" + studentId + " and "+
                "sectionId=" + sectionId + ";"
            );

            if ( count > 1 ) {
                // Oops.  The student was added more than once.
                // Put one copy back in.

                insertStudentSection( studentId, sectionId );

            } // if there was more than one entry

	    rs.close();
	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: delSectionStudent(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

    } // delSectionStudent()

    // insert a student into a section
    public static void insertStudentSection( int studentId, int sectionId ) {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            stmt.executeUpdate(
                "insert into studentsToSections (" +
                "studentUserId,"+
                "sectionId"+
                ") " +
                "values ("+
                studentId + "," +
                sectionId +
                ");"
            );

	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: insertSectionStudent(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

    } // insertStudentSection

    // get all the sections taught by this teacher.
    //
    // It looks ugly to have a loop calling getSection()
    // which creates another Connection, and makes queries
    // using non-prepared statements.
    // I plan to change this to use something like _getUser
    // which uses PreparedStatements to translate ids to
    // names, and uses the caller's connection.
    public static Vector getSections( int teacherId ) {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            rs = stmt.executeQuery(
                     "select id from sections " +
                     "where teacherUserId=" + teacherId + ";"
                 );

            while (rs.next()) v.add( getSection( rs.getInt( 1 ) ) );

	    rs.close();
	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getSections(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return v;

    } // getSections

    // As I said before, it's inefficient to call getSection() inside a loop,
    // but we don't have hundreds of people calling this.  Only administrators
    // call this.
    public static Vector getAllSections() {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs = stmt.executeQuery(
                     "select id from sections s,periods p " +
		     "where s.periodId=p.periodId order by name,periodName,id;"
                 );

            while (rs.next()) v.add( getSection( rs.getInt( 1 ) ) );

	    rs.close();
	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getAllSections(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

	return( v );
    } // getAllSections()

    public static Vector getAllPeriods() {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs = stmt.executeQuery(
                     "select periodId,periodName from periods " +
                     "order by periodName;"
                 );

            while (rs.next()) v.add( new Period( rs.getInt(1), rs.getString(2) ) );

	    rs.close();
	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getAllPeriods(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return( v );
    } // getAllPeriods()


    public static Vector getAllDepartments() {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs = stmt.executeQuery(
                     "select departmentId,departmentName from departments " +
                     "order by departmentName;"
                 );

            while (rs.next()) v.add( new Department( rs.getInt(1), rs.getString(2) ) );

	    rs.close();
	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getAllDepartments(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return( v );
    } // getAllDepartments()

    // Return enotes of any status for this student and section.
    public static Vector getEnotesForStudentSection( int studentId, int sectionId ) {
        Vector v = new Vector();
        Vector enotesOneKind;
        Vector allEnotes = new Vector();
        Vector buffer = new Vector();
        Enote[] enotesByDate;

        enotesOneKind = getCommentsForStudentSection( studentId, sectionId );
        for (int i = 0, j = enotesOneKind.size(); i < j; i++ )
            buffer.add( enotesOneKind.elementAt(i) );

        enotesOneKind = getDisciplineForStudentSection( studentId, sectionId );
        for (int i = 0, j = enotesOneKind.size(); i < j; i++ )
            buffer.add( enotesOneKind.elementAt(i) );

        enotesOneKind = getQuicknoteForStudentSection( studentId, sectionId );
        for (int i = 0, j = enotesOneKind.size(); i < j; i++ )
            buffer.add( enotesOneKind.elementAt(i) );

        enotesByDate = (Enote[])(buffer.toArray(new Enote[0]));
        java.util.Arrays.sort( enotesByDate );

        for (int i = 0, j = enotesByDate.length; i < j; i++)
            allEnotes.add( enotesByDate[i] );

        return allEnotes;
    } // getEnotesForStudentSection()

    private static Comment _getComment( 
	    int id, 
	    PreparedStatement userStmt,
	    PreparedStatement commentStmt,
	    PreparedStatement sectionStmt,
	    PreparedStatement eventStmt,
	    PreparedStatement markStmt
    ) {
        ResultSet rs;
        ResultSet rss;
        Comment c = Comment.getDefault();

        // ints that get translated to name using queries.
        int studentId;  // use userStmt
        int sectionId;  // use sectionStmt
        int eventId;    // use eventStmt
        int markId;     // use markStmt

        String studentFirst = "";
        String studentLast = "";
        String sectionName = "";
        String eventName = "";
        String markName = "";
	StringBuffer log;

        try {
            commentStmt.setInt( 1, id );
            rs = commentStmt.executeQuery( );
            if ( rs.next( ) ) {
                studentId = rs.getInt( 3 );
                sectionId = rs.getInt( 4 );
                eventId = rs.getInt( 5 );
                markId = rs.getInt( 6 );

                userStmt.setInt( 1, studentId );
                rss = userStmt.executeQuery();
                if (rss.next()) {
                    studentFirst = rss.getString( 4 );
                    studentLast = rss.getString( 5 );
                }

		rss.close();

                sectionStmt.setInt( 1, sectionId );
                rss = sectionStmt.executeQuery();
                if (rss.next()) sectionName = rss.getString( 1 );

		rss.close();

                eventStmt.setInt( 1, eventId );
                rss = eventStmt.executeQuery();
                if (rss.next()) eventName = rss.getString( 1 );

		rss.close();

                markStmt.setInt( 1, markId );
                rss = markStmt.executeQuery();
                if (rss.next()) markName = rss.getString( 1 );

		rss.close();

                c = new Comment(
                        id, // commentId
                        0 == rs.getInt( 1 ), // obsolete
                        rs.getTimestamp( 2 ), // datetime
                        studentId,        // studentUserId
                        studentFirst,
                        studentLast,
                        sectionId,
                        sectionName,
                        "", // period name is looked up later.
                        eventId,
                        eventName,
                        markId,
                        markName,
                        rs.getString( 7 ), // commentText
                        rs.getString( 8 ) // status
                    );
            } // if (rs.next())

	    rs.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: _getComment(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        }

        return ( c );
    } // _getComment()

    
    private static Quicknote _getQuicknote( 
	    int id, 
	    PreparedStatement userStmt,
	    PreparedStatement quicknoteStmt,
	    PreparedStatement quicknoteNameStmt,
	    PreparedStatement sectionStmt
    ) {
        ResultSet rs;
        ResultSet rss;
        Quicknote q = Quicknote.getDefault();
        // ints that get translated to name using queries.
        int studentId;  // use userStmt
        int sectionId;  // use sectionStmt
        int quicknoteTypeId; // use quicknoteNameStmt

        String studentFirst = "";
        String studentLast = "";
        String sectionName = "";
        String quicknoteName = "";

	StringBuffer log;

        try {
            quicknoteStmt.setInt( 1, id );
            rs = quicknoteStmt.executeQuery();
            if (rs.next()) {
                studentId = rs.getInt( 3 );
                sectionId = rs.getInt( 4 );
                quicknoteTypeId = rs.getInt( 5 );

                userStmt.setInt( 1, studentId );
                rss = userStmt.executeQuery();
                if (rss.next()) {
                    studentFirst = rss.getString( 4 );
                    studentLast = rss.getString( 5 );
                }

		rss.close();

                sectionStmt.setInt( 1, sectionId );
                rss = sectionStmt.executeQuery();
                if (rss.next()) sectionName = rss.getString( 1 );

		rss.close();

                quicknoteNameStmt.setInt( 1, quicknoteTypeId );
                rss = quicknoteNameStmt.executeQuery();
                if (rss.next()) quicknoteName = rss.getString( 1 );

		rss.close();

                q = new Quicknote(
                        id,//   int quicknoteId,
                        0 == rs.getInt( 1 ),//   boolean obsolete,
                        rs.getTimestamp( 2 ),//   Timestamp datetime,
                        studentId,//   int studentUserId,
                        studentFirst,//   String studentFirst,
                        studentLast,//   String studentLast,
                        sectionId,//   int sectionId,
                        sectionName,
                        "", // periodName is looked up later.
                        quicknoteTypeId,//   int quicknoteTypeId,
                        quicknoteName,//   String quicknoteTypeName,
                        rs.getString( 6 ),//   String commentText,
                        0 == rs.getInt( 7 ),//   boolean published,
                        rs.getString( 8 )//   String status
                    );

            } // if (rs.next()) 

	    rs.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: _getQuicknote(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        }

        return q;

    } // _getQuicknote()

    private static Discipline _getDiscipline( 
	    int id, 
	    PreparedStatement userStmt, 
	    PreparedStatement disciplineStmt,
	    PreparedStatement capStmt,
	    PreparedStatement sectionStmt
    ) {
        ResultSet rs;
        ResultSet rss;
        Discipline d = Discipline.getDefault();

        // ints that get translated to name using queries.
        int studentId;  // use userStmt
        int sectionId;  // use sectionStmt
        int capId; // use capStmt

        String studentFirst = "";
        String studentLast = "";
        String sectionName = "";
        String offenseName = "";
        String punishmentName = "";
	StringBuffer log;

        try {
            disciplineStmt.setInt( 1, id );
            rs = disciplineStmt.executeQuery();
            if ( rs.next( ) ) {
                studentId = rs.getInt( 3 );
                sectionId = rs.getInt( 4 );
                capId = rs.getInt( 5 );

                userStmt.setInt( 1, studentId );
                rss = userStmt.executeQuery();
                if (rss.next()) {
                    studentFirst = rss.getString( 4 );
                    studentLast = rss.getString( 5 );
                }

		rss.close();

                sectionStmt.setInt( 1, sectionId );
                rss = sectionStmt.executeQuery();
                if (rss.next()) sectionName = rss.getString( 1 );

		rss.close();

                capStmt.setInt( 1, capId );
                rss = capStmt.executeQuery();
                if (rss.next()) {
                    offenseName = rss.getString( 1 );
                    punishmentName = rss.getString( 2 );
                }

		rss.close();

                d = new Discipline(
                        id,//    int disciplineId,
                        0 == rs.getInt( 1 ),//    boolean obsolete,
                        rs.getTimestamp( 2 ),//    Timestamp datetime,
                        studentId,//    int studentUserId,
                        studentFirst,//    String studentFirst,
                        studentLast,//   String studentLast,
                        sectionId,//   int sectionId,
                        sectionName,//   String sectionName,
                        "", // periodName (looked up later)
                        capId,//   int capId,
                        offenseName,//   String offenseName,
                        punishmentName,//   String punishmentName,
                        rs.getString( 6 ),//   String commentText,
                        rs.getString( 7 ) //   String status
                    );
            } // found id

	    rs.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: _getDiscipline(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        }

        return d;
    } // _getDiscipline()

    public static Vector getCommentsForStudentSection(int studentId, int sectionId ) {
	DBPool pool;
        Connection conn = null;
        ResultSet rs;
        Statement stmt;
	PreparedStatement userStmt;
	PreparedStatement commentStmt;
	PreparedStatement sectionStmt;
	PreparedStatement eventStmt;
	PreparedStatement markStmt;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            commentStmt = conn.prepareStatement( commentSelect );
            userStmt = conn.prepareStatement( userSelect );
            sectionStmt = conn.prepareStatement( sectionSelect );
            eventStmt = conn.prepareStatement( eventSelect );
            markStmt = conn.prepareStatement( markSelect );

            rs = stmt.executeQuery(
                       "select commentId from comments " +
                       "where studentUserId="+ studentId +" and sectionId="+ sectionId +
			   " and status<>'deleted';" );

            while ( rs.next( ) ) v.add( 
		_getComment( 
		    rs.getInt( 1 ),
		    userStmt,
		    commentStmt,
		    sectionStmt,
		    eventStmt,
		    markStmt
		) );

	    rs.close();
	    stmt.close();
	    commentStmt.close();
	    userStmt.close();
	    sectionStmt.close();
	    eventStmt.close();
	    markStmt.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getCommentsForStudentSection(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return (v);
    } // getCommentsForStudentSection()

    public static Vector getDisciplineForStudentSection(int studentId, int sectionId ) {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	PreparedStatement userStmt;
	PreparedStatement disciplineStmt;
	PreparedStatement capStmt;
	PreparedStatement sectionStmt;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            disciplineStmt = conn.prepareStatement( disciplineSelect );
            userStmt = conn.prepareStatement( userSelect );
            sectionStmt = conn.prepareStatement( sectionSelect );
            capStmt = conn.prepareStatement( capSelect );

            rs = stmt.executeQuery(
                     "select disciplineId from disciplines " +
                     "where status<>'deleted' and "+
		     "studentUserId="+ studentId +" and sectionId="+ sectionId +";" );

            while ( rs.next( ) ) v.add( 
		_getDiscipline( 
		    rs.getInt( 1 ),
		    userStmt,
		    disciplineStmt,
		    capStmt,
		    sectionStmt
		) );

	    rs.close();
	    stmt.close();
	    disciplineStmt.close();
	    userStmt.close();
	    sectionStmt.close();
	    capStmt.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getDisciplineForStudentSection(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return (v);
    } // getDisciplineForStudentSection()

    public static Vector getQuicknoteForStudentSection(int studentId, int sectionId) {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	PreparedStatement userStmt;
	PreparedStatement quicknoteStmt;
	PreparedStatement quicknoteNameStmt;
	PreparedStatement sectionStmt;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            quicknoteStmt = conn.prepareStatement( quicknoteSelect );
            userStmt = conn.prepareStatement( userSelect );
            sectionStmt = conn.prepareStatement( sectionSelect );
            quicknoteNameStmt = conn.prepareStatement( quicknoteNameSelect );

            rs = stmt.executeQuery(
                     "select quicknoteId from quicknotes " +
                     "where status<>'deleted' and "+
		     "studentUserId="+ studentId +" and sectionId="+ sectionId +";" );

            while ( rs.next( ) ) v.add( 
		_getQuicknote( 
		    rs.getInt( 1 ),
		    userStmt,
		    quicknoteStmt,
		    quicknoteNameStmt,
		    sectionStmt
		) );

	    rs.close();
	    stmt.close();
	    quicknoteStmt.close();
	    userStmt.close();
	    sectionStmt.close();
	    quicknoteNameStmt.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getQuicknoteForStudentSection(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return (v);

    } // getQuicknoteForStudentSection()

    public static Vector getCap() {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            rs = stmt.executeQuery(
                     "select "+
                     "capId,offenseName,punishmentName "+
                     "from crimesAndPunishments "+
                     "where obsolete=0;" );

            while ( rs.next( ) )
                v.add(
                    new Cap(
                        rs.getInt( 1 ), // capId
                        rs.getString( 2 ), // offenseName
                        rs.getString( 3 ), // punishmentName
                        false // obsolete
                    )
                );

	    rs.close();
	    stmt.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getCap(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return (v);
    } // getCap()

    public static Vector getEvents() {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            rs = stmt.executeQuery(
                     "select "+
                     "eventId,eventName,eventYear,published "+
                     "from events;");

            while ( rs.next( ) )
                v.add(
                    new Event(
                        rs.getInt( 1 ), // eventd
                        rs.getString( 2 ), // eventName
                        rs.getInt( 3 ), // eventYear
                        1 == rs.getInt( 4 ) // published
                    )
                );

	    rs.close();
	    stmt.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getEvents(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return (v);
    } // getEvents()

    public static Vector getMarks() {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            rs = stmt.executeQuery(
                     "select markId,markName from marks;");

            while ( rs.next( ) )
                v.add(
                    new Mark(
                        rs.getInt( 1 ), // markId
                        rs.getString( 2 )  // markName
                    )
                );

	    rs.close();
	    stmt.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getMarks(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return (v);

    } // getMarks()

    public static Vector getQuicknoteTypes() {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            rs = stmt.executeQuery(
                     "select quicknoteTypeId,quicknoteTypeName from quicknoteTypes;");

            while ( rs.next( ) )
                v.add(
                    new QuicknoteType(
                        rs.getInt( 1 ), // quicknoteTypeId
                        rs.getString( 2 )  // quicknoteTypeName
                    )
                );

	    rs.close();
	    stmt.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getQuicknoteTypes(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return (v);

    } // getQuicknoteTypes()

    public static Discipline getDiscipline( int dId ) {
	DBPool pool;
        Connection conn = null;
	PreparedStatement userStmt;
	PreparedStatement disciplineStmt;
	PreparedStatement capStmt;
	PreparedStatement sectionStmt;
        Discipline d = Discipline.getDefault();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );

            disciplineStmt = conn.prepareStatement( disciplineSelect );
            userStmt = conn.prepareStatement( userSelect );
            sectionStmt = conn.prepareStatement( sectionSelect );
            capStmt = conn.prepareStatement( capSelect );

            d = _getDiscipline( dId, userStmt, disciplineStmt, capStmt, sectionStmt );

	    userStmt.close();
	    disciplineStmt.close();
	    capStmt.close();
	    sectionStmt.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getDiscipline(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return (d);

    } // getDiscipline()

    public static Comment getComment( int cId ) {
	DBPool pool;
        Connection conn = null;
	PreparedStatement userStmt;
	PreparedStatement commentStmt;
	PreparedStatement sectionStmt;
	PreparedStatement eventStmt;
	PreparedStatement markStmt;
        Comment c = Comment.getDefault();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );

            commentStmt = conn.prepareStatement( commentSelect );
            userStmt = conn.prepareStatement( userSelect );
            sectionStmt = conn.prepareStatement( sectionSelect );
            eventStmt = conn.prepareStatement( eventSelect );
            markStmt = conn.prepareStatement( markSelect );

            c = _getComment( cId, userStmt, commentStmt, sectionStmt, eventStmt, markStmt );

	    commentStmt.close();
	    userStmt.close();
	    sectionStmt.close();
	    eventStmt.close();
	    markStmt.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getComment(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return (c);

    } // getComment()

    public static Quicknote getQuicknote( int qId ) {
	DBPool pool;
        Connection conn = null;
	PreparedStatement userStmt;
	PreparedStatement quicknoteStmt; 
	PreparedStatement quicknoteNameStmt; 
	PreparedStatement sectionStmt;
        Quicknote q = Quicknote.getDefault();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );

            quicknoteStmt = conn.prepareStatement( quicknoteSelect );
            userStmt = conn.prepareStatement( userSelect );
            sectionStmt = conn.prepareStatement( sectionSelect );
            quicknoteNameStmt = conn.prepareStatement( quicknoteNameSelect );

            q = _getQuicknote( qId, userStmt, quicknoteStmt, quicknoteNameStmt, sectionStmt );

	    quicknoteStmt.close();
	    userStmt.close();
	    sectionStmt.close();
	    quicknoteNameStmt.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getQuicknote(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return (q);

    } // getQuicknote()


    public static int insertDiscipline( Discipline d, StringBuffer message ) {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        StringBuffer b = new StringBuffer();
	int ret = 0;
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            if ( d.getDisciplineId() == 0 ) {
                b.append("insert into disciplines ");
                b.append("(obsolete,timestamp,studentUserId,sectionId,capId,commentText,status) ");
                b.append("values (");
                b.append(( d.getObsolete() ? "1" : "0" ));
                b.append(",'");
                b.append( mySQLsdf.format( d.getDatetime() ));
                b.append("',");
                b.append( d.getStudentUserId() );
                b.append(",");
                b.append( d.getSectionId() );
                b.append(",");
                b.append( d.getCapId() );
                b.append(",'");
                b.append( d.getCommentText());
                b.append("','");
                b.append( d.getStatus() );
                b.append("');");
                stmt.executeUpdate( b.toString() );
            } else {
                b.append("update disciplines set ");
                b.append("obsolete=");
                b.append(( d.getObsolete() ? "1" : "0" ));
                b.append(",timestamp='");
                b.append( mySQLsdf.format( d.getDatetime() ));
                b.append("',studentUserId=");
                b.append( d.getStudentUserId() );
                b.append(",sectionId=");
                b.append( d.getSectionId() );
                b.append(",capId=");
                b.append( d.getCapId() );
                b.append(",commentText='");
                b.append( d.getCommentText() );
                b.append("',status='");
                b.append( d.getStatus() );
                b.append("' where disciplineId=");
                b.append( d.getDisciplineId() );
                b.append(";");
                stmt.executeUpdate( b.toString() );
            }
            message.delete(0, message.length());
            message.append( "OK" );

	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: insertDiscipline(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
            
            message.delete(0, message.length());
            message.append( e.getMessage() );

	    ret = -1;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

	return ret;

    } // insertDiscipline()


    public static int insertComment( Comment c, StringBuffer message ) {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        StringBuffer b = new StringBuffer();
	int ret = 0;
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );

            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            if ( c.getCommentId() == 0 ) {
                b.append("insert into comments ");
                b.append("(obsolete,timestamp,studentUserId,sectionId,eventId,markId,commentText,status) ");
                b.append("values (");
                b.append(( c.getObsolete() ? "1" : "0" ));
                b.append(",'");
                b.append( mySQLsdf.format( c.getDatetime() ));
                b.append("',");
                b.append( c.getStudentUserId() );
                b.append(",");
                b.append( c.getSectionId() );
                b.append(",");
                b.append( c.getEventId() );
                b.append(",");
                b.append( c.getMarkId() );
                b.append(",'");
                b.append( c.getCommentText());
                b.append("','");
                b.append( c.getStatus() );
                b.append("');");

                stmt.executeUpdate( b.toString() );
            } else {
                b.append("update comments set ");
                b.append("obsolete=");
                b.append(( c.getObsolete() ? "1" : "0" ));
                b.append(",timestamp='");
                b.append( mySQLsdf.format( c.getDatetime() ));
                b.append("',studentUserId=");
                b.append( c.getStudentUserId() );
                b.append(",sectionId=");
                b.append( c.getSectionId() );
                b.append(",eventId=");
                b.append( c.getEventId() );
                b.append(",markId=");
                b.append( c.getMarkId() );
                b.append(",commentText='");
                b.append( c.getCommentText() );
                b.append("',status='");
                b.append( c.getStatus() );
                b.append("' where commentId=");
                b.append( c.getCommentId() );
                b.append(";");
                stmt.executeUpdate( b.toString() );
            }
            message.delete(0, message.length());
            message.append( "OK" );

	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: insertComment(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );

            message.delete(0, message.length());
            message.append( e.getMessage() );

	    ret = -1;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

	return ret;
    } // insertComment()


    public static int insertQuicknote( Quicknote q, StringBuffer message ) {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        StringBuffer b = new StringBuffer();
	int ret = 0;
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            if ( q.getQuicknoteId() == 0 ) {
                b.append("insert into quicknotes ");
                b.append("(obsolete,timestamp,studentUserId,sectionId,quicknoteTypeId,commentText,status) ");
                b.append("values (");
                b.append(( q.getObsolete() ? "1" : "0" ));
                b.append(",'");
                b.append( mySQLsdf.format( q.getDatetime() ));
                b.append("',");
                b.append( q.getStudentUserId() );
                b.append(",");
                b.append( q.getSectionId() );
                b.append(",");
                b.append( q.getQuicknoteTypeId() );
                b.append(",'");
                b.append( q.getCommentText());
                b.append("','");
                b.append( q.getStatus() );
                b.append("');");
                stmt.executeUpdate( b.toString() );
            } else {
                b.append("update quicknotes set ");
                b.append("obsolete=");
                b.append(( q.getObsolete() ? "1" : "0" ));
                b.append(",timestamp='");
                b.append( mySQLsdf.format( q.getDatetime() ));
                b.append("',studentUserId=");
                b.append( q.getStudentUserId() );
                b.append(",sectionId=");
                b.append( q.getSectionId() );
                b.append(",quicknoteTypeId=");
                b.append( q.getQuicknoteTypeId() );
                b.append(",commentText='");
                b.append( q.getCommentText() );
                b.append("',status='");
                b.append( q.getStatus() );
                b.append("' where quicknoteId=");
                b.append( q.getQuicknoteId() );
                b.append(";");
                stmt.executeUpdate( b.toString() );
            }
            message.delete(0, message.length());
            message.append( "OK" );

	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: insertQuicknote(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
            message.delete(0, message.length());
            message.append( e.getMessage() );
	    ret = -1;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

	return ret;
    } // insertQuicknote()


    // Returns a list of published enotes for this student.
    public static Vector getPubEnotesStudent( int studentId, int mask )  {
	DBPool pool;
        Connection conn;
        ResultSet rs;
        Statement stmt;
	PreparedStatement userStmt;
	PreparedStatement quicknoteStmt;
	PreparedStatement quicknoteNameStmt;
	PreparedStatement disciplineStmt;
	PreparedStatement capStmt;
	PreparedStatement commentStmt;
	PreparedStatement sectionStmt;
	PreparedStatement eventStmt;
	PreparedStatement markStmt;
        Vector enotesAccum;
        Enote[] enotesByDate;
        Vector v = new Vector();
        StringBuffer b;
	StringBuffer log;

        if (0 == mask) return ( v );

        conn = null;
        enotesAccum = new Vector();

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            userStmt = conn.prepareStatement( userSelect );
            sectionStmt = conn.prepareStatement( sectionSelect );

            if ( 0 != (mask & COMMENT) ) {
                commentStmt = conn.prepareStatement( commentSelect );
                eventStmt = conn.prepareStatement( eventSelect );
                markStmt = conn.prepareStatement( markSelect );

                b = new StringBuffer();
                b.append("select commentId from comments ");
                b.append("where studentUserId="+ studentId);
                b.append(" and status='published' and obsolete=0;" );

                rs = stmt.executeQuery( b.toString() );

                while ( rs.next( ) ) {
		    enotesAccum.add( 
			_getComment( 
			    rs.getInt( 1 ),
			    userStmt,
			    commentStmt,
			    sectionStmt,
			    eventStmt,
			    markStmt
			) );
		}

		// Just closing the result set and statments prepared
		// in this block
		rs.close();
		commentStmt.close();
		eventStmt.close();
		markStmt.close();

            }
            if ( 0 != (mask & DISCIPLINE)) {
                disciplineStmt = conn.prepareStatement( disciplineSelect );
                capStmt = conn.prepareStatement( capSelect );

                b = new StringBuffer();
                b.append("select disciplineId from disciplines ");
                b.append("where studentUserId="+ studentId);
                b.append(" and status='published' and obsolete=0;" );

                rs = stmt.executeQuery( b.toString() );

                while ( rs.next( ) ) {
		    enotesAccum.add( 
			_getDiscipline( 
			    rs.getInt( 1 ),
			    userStmt,
			    disciplineStmt,
			    capStmt,
			    sectionStmt
			) );
		}

		rs.close();
		disciplineStmt.close();
		capStmt.close();

            }
            if ( 0 != (mask & QUICKNOTE)) {
                quicknoteStmt = conn.prepareStatement( quicknoteSelect );
                quicknoteNameStmt = conn.prepareStatement( quicknoteNameSelect );

                b = new StringBuffer();
                b.append("select quicknoteId from quicknotes ");
                b.append("where studentUserId="+ studentId);
                b.append(" and status='published' and obsolete=0;" );

                rs = stmt.executeQuery( b.toString());

                while ( rs.next( ) ) {
		    enotesAccum.add( 
			_getQuicknote( 
			    rs.getInt( 1 ),
			    userStmt,
			    quicknoteStmt,
			    quicknoteNameStmt,
			    sectionStmt
			) );
		}

		rs.close();
		quicknoteNameStmt.close();
		quicknoteStmt.close();
            }

            enotesByDate = (Enote[])(enotesAccum.toArray(new Enote[0]));
            java.util.Arrays.sort( enotesByDate );

            for (int i = 0, j = enotesByDate.length; i < j; i++) {
                v.add( enotesByDate[i] );
	    }

	    stmt.close();
	    userStmt.close();
	    sectionStmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getEnoteStudent(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return( v );
    } // getPubEnotesStudent()


    public static Vector getProperties() {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            rs = stmt.executeQuery(
                     "select name,value from properties order by name;");

            while ( rs.next( ) )
                v.add(
                    new Property(
                        rs.getString( 1 ), // name
                        rs.getString( 2 )  // value
                    )
                );

	    rs.close();
	    stmt.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getProperties(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return( v );
    } // getProperties


    public static void setProperty( String name, String value ) {
	DBPool pool;
        Connection conn = null;
	Statement stmt;
	StringBuffer log;


        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
	    stmt = conn.createStatement( );

	    stmt.executeUpdate(
                "update properties set value='" + value + "' where name='" + name + "';");

	    stmt.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: setProperty(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
	} finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    } // setProperty()


    // set status of all comments to published.
    public static void publishComments( ) {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	StringBuffer log;

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );

	    stmt.executeUpdate( "update comments set status='published' where status='proofed';" );

	    stmt.close();

	} catch (SQLException e) {
	    log = new StringBuffer( logsdf.format( new java.util.Date() ) );
	    log.append( " DBRoutines.java: publishComments(): " );
	    log.append( e.getMessage() );
	    System.err.println( log.toString() );
	} finally {
	    try { if (conn != null) conn.close(); } catch (SQLException e) {}
	}

    } // publishComments


    private static String _getProperty( String name, PreparedStatement pstmt ) {
        String value = null;
        ResultSet rs;
	StringBuffer log;

        try {
            pstmt.setString( 1, name );
            rs = pstmt.executeQuery();
            if ( rs.next() ) value = rs.getString( 1 );
	    rs.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: _getProperty(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        }
        return value;
    } // _getProperty()


    public static String getProperty( String name ) {
	DBPool pool;
        Connection conn = null;
        PreparedStatement pstmt;
	String value = null;
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            pstmt = conn.prepareStatement( propertiesSelect );

	    value = _getProperty( name, pstmt );

	    pstmt.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getProperty(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
	    try { if (conn != null) conn.close(); } catch (SQLException e) {}
	}

        return value;

    } // getProperty()


    // MailParameters should be a subclass of Properties
    // forget that business of having multiple feedback recipients.
    public static MailParameters getMailParameters() {
	DBPool pool;
        Connection conn = null;
        ResultSet rs;
        PreparedStatement pstmt;
        Vector feedbackRecipients = new Vector();
        MailParameters mp = null;
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );

            pstmt = conn.prepareStatement( propertiesSelect );

            mp = new MailParameters(
                     _getProperty( "emailMessageFrom", pstmt),
                     _getProperty( "emailServer", pstmt),
                     _getProperty( "disciplineStandardText", pstmt),
                     _getProperty( "quicknoteStandardText", pstmt),
                     _getProperty( "commentStandardText", pstmt),
                     _getProperty( "disciplineSubject", pstmt),
                     _getProperty( "quicknoteSubject", pstmt),
                     _getProperty( "commentSubject", pstmt)
                 );

            pstmt.setString( 1, "feedbackAdr" );
            rs = pstmt.executeQuery();
            while (rs.next()) mp.addFeedbackRecipient( rs.getString( 1 ) );

	    rs.close();
	    pstmt.close();

        } catch (SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getMailParameters(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return mp;
    } // getMailParameters


    // get all disciplines for adminSendDisciplines
    public static Vector getSendDisciplines() {
	DBPool pool;
        Connection conn = null;
        ResultSet rs;
        Statement stmt;
	PreparedStatement userStmt;
	PreparedStatement disciplineStmt;
	PreparedStatement capStmt;
	PreparedStatement sectionStmt;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs = stmt.executeQuery(
                      "select disciplineId " +
                      "from disciplines " +
		      "where status='pending' " +
		      "order by timestamp desc;");

            disciplineStmt = conn.prepareStatement( disciplineSelect );
            userStmt = conn.prepareStatement( userSelect );
            sectionStmt = conn.prepareStatement( sectionSelect );
            capStmt = conn.prepareStatement( capSelect );

            while (rs.next()) {
		v.add( 
		    _getDiscipline( 
			rs.getInt(1),
			userStmt,
			disciplineStmt,
			capStmt,
			sectionStmt
		    ));
	    }

	    rs.close();
	    stmt.close();
	    userStmt.close();
	    disciplineStmt.close();
	    capStmt.close();
	    sectionStmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getSendDisciplines(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return (v);

    } // getSendDisciplines()


    // get all quicknotes for adminSendDisciplines
    public static Vector getSendQuicknotes() {
	DBPool pool;
        Connection conn = null;
        ResultSet rs;
        Statement stmt;
	PreparedStatement userStmt;
	PreparedStatement quicknoteStmt;
	PreparedStatement quicknoteNameStmt;
	PreparedStatement sectionStmt;
        Vector v = new Vector();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs = stmt.executeQuery(
                      "select quicknoteId "+
                      "from quicknotes "+
		      "where status='pending' "+
		      "order by timestamp desc;");

            quicknoteStmt = conn.prepareStatement( quicknoteSelect );
            userStmt = conn.prepareStatement( userSelect );
            sectionStmt = conn.prepareStatement( sectionSelect );
            quicknoteNameStmt = conn.prepareStatement( quicknoteNameSelect );

            while (rs.next()) {
		v.add( 
		    _getQuicknote( 
			rs.getInt(1),
			userStmt,
			quicknoteStmt,
			quicknoteNameStmt,
			sectionStmt
		    ));
	    }

	    rs.close();
	    stmt.close();
	    quicknoteStmt.close();
	    userStmt.close();
	    sectionStmt.close();
	    quicknoteNameStmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getSendQuicknotes(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return (v);

    } // getSendQuicknotes()


    // There is a properties name/value pair in the mySQL 'properties' table named "commentEvent"
    // return the Event associated with it.
    public static Event getConfiguredEvent() {
	String currentEvent = getProperty( "commentEvent" );
	Event result = Event.getDefault();
	Vector events = getEvents();

	if ( null == currentEvent ) return result;

	for ( int i = 0, j = events.size(); i < j; i++ ) {
	    if ( currentEvent.equals( ((Event)events.elementAt(i)).getEventName() ) )
		return ((Event)events.elementAt(i));
	} // for

	return result;
    }

    // get all comments for doAdminCommentPerGrade and doAdminMarkThisComment
    public static Vector getSendComments( int gradYear, String status ) {
	DBPool pool;
        Connection conn = null;
        ResultSet rs;
        Statement stmt;
	PreparedStatement userStmt;
	PreparedStatement commentStmt;
	PreparedStatement sectionStmt;
	PreparedStatement eventStmt;
	PreparedStatement markStmt;
        Vector v = new Vector();
	Event event = getConfiguredEvent();
	StringBuffer b = new StringBuffer();
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

            commentStmt = conn.prepareStatement( commentSelect );
            userStmt = conn.prepareStatement( userSelect );
            sectionStmt = conn.prepareStatement( sectionSelect );
            eventStmt = conn.prepareStatement( eventSelect );
            markStmt = conn.prepareStatement( markSelect );

	    b.append("select commentId ");
	    b.append("from comments,users,events ");
	    b.append("where ");
	    if ( "all".equals(status) ) {
		b.append("(status<>'deleted') ");
	    } else {
		b.append("(status='");
		b.append(status);
		b.append("') ");
	    }
	    b.append("and (comments.studentUserId=users.userId) ");
	    b.append("and (comments.eventId=events.eventId) ");
	    b.append("and (events.eventName='");
	    b.append(event.getEventName());
	    b.append("') ");
	    if ( 0 != gradYear)
		b.append( " and (users.yearId=" + gradYear + ") ");
	    b.append("order by users.last,users.first asc;");

            rs = stmt.executeQuery( b.toString() );

            while (rs.next())
		v.add( _getComment( rs.getInt(1), userStmt, commentStmt, sectionStmt, eventStmt, markStmt ));

	    rs.close();
	    stmt.close();
	    userStmt.close();
	    commentStmt.close();
	    sectionStmt.close();
	    eventStmt.close();
	    markStmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getSendComments(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return (v);

    } // getSendComments()


    public static String getDisciplinarian() {
        // return ("Discipline.Office@stonybrookschool.org");
	 DBPool pool;
         Connection conn = null;
         Statement stmt;
         PreparedStatement pstmt;
         ResultSet rs;
         String adr = "";
         String actualAdr;
	 StringBuffer log;
	 
         try {
	     pool = DBPool.getPool( poolString );
             conn = pool.getConnection( );
             stmt = conn.createStatement( );
             pstmt = conn.prepareStatement( propertiesSelect );
             actualAdr = _getProperty( "errorAdr", pstmt );
             adr = "No Disciplinarian found" + actualAdr;
	 
               rs = stmt.executeQuery(
                      "select first,last,email "+
                       "from users,homes "+
                       "where (users.homeId=homes.homeId) and "+
                       "(homes.homeName='Discipline');");
	 
            if (rs.next())
                 adr = MailDistribution.formatEmailAddress(
                           "",
                           rs.getString(1),
                           rs.getString(2),
                          rs.getString(3)
                      );
	 
	     rs.close();
	     stmt.close();
	     pstmt.close();
	 
         }  catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
             log.append( " DBRoutines.java: getDisciplinarian(): " );
             log.append( e.getMessage() );
             System.err.println( log.toString() );
         } finally {
             try { if (conn != null) conn.close(); } catch (SQLException e) {}
         }

         return (adr);

    } // getDisciplinarian()


    public static Vector getFacultyHome( int homeId ) {
	DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        Vector v = new Vector();
	PreparedStatement homeStmt;
	PreparedStatement groupStmt;
	PreparedStatement userStmt;
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs = stmt.executeQuery(
                      "select userId "+
                      "from users,groups "+
                      "where (users.groupId=groups.groupID) and "+
                      "(groups.groupName='faculty') and "+
                      "(users.homeId="+ homeId +");");

            homeStmt = conn.prepareStatement( homeSelect );
            groupStmt = conn.prepareStatement( groupSelect );
            userStmt = conn.prepareStatement( userSelect );

            while (rs.next()) v.add( _getUser( rs.getInt(1), homeStmt, groupStmt, userStmt ) );

	    rs.close();
	    stmt.close();
	    homeStmt.close();
	    groupStmt.close();
	    userStmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getFacultyHome(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return( v );
    } // getFacultyHome()


    // Intended usage is, for example:
    //   setEnoteStatus( "comments", "deleted", "commentId", 23 );
    public static int setEnoteStatus( String table, String status, String enoteId, int value ) {
	DBPool pool;
        Connection conn = null;
	Statement stmt;
	StringBuffer b = new StringBuffer();
	int ret = 0;
	StringBuffer log;

        try {
	    pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );

            stmt = conn.createStatement( );
	    b.append( "update " );
	    b.append( table );
	    b.append( " set status='" );
	    b.append( status );
	    b.append( "' where " );
	    b.append( enoteId );
	    b.append( "=" );
	    b.append( value );
	    b.append( ";" );

	    stmt.executeUpdate( b.toString() );

	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: setEnoteStatus(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
	    ret = -1;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

	return ret;
    } // setEnotStatus()


    public static Vector getPubEnotesFaculty( User faculty, int hat, int mask ) {
	DBPool pool;
	Connection conn = null;
	ResultSet rs;
	Statement stmt;
	Vector result = new Vector();
	Vector oneStudentEnotes;
	StringBuffer b = new StringBuffer();
	int i;
	int j;
	Enote enote;
	Event cce = getConfiguredEvent();  // currently configured event
	StringBuffer log;

	if (!(hat == COUNSELOR || hat == GRADE_CHAIR)) return result;
	if (hat == GRADE_CHAIR && faculty.getYearId() == 0) return result;

	try {
	    if ( hat == COUNSELOR ) {
		b.append( "select userId from users,groups ");
		b.append( "where users.groupId=groups.groupId ");
		b.append(   "and groupName='student' ");
		b.append(   "and counselorUserId=" );
		b.append(      faculty.getUserId() );
		b.append( " order by last,first;" );
	    } else {
		b.append( "select userId from users,groups ");
		b.append( "where users.groupId=groups.groupId ");
		b.append(   "and groupName='student' ");
		b.append(   "and users.yearId=" );
		b.append(      faculty.getYearId() );
		b.append( " order by last,first;" );
	    }
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );

	    stmt = conn.createStatement( );

	    rs = stmt.executeQuery( b.toString() );

	    while (rs.next()) {
		oneStudentEnotes = getPubEnotesStudent( rs.getInt(1), mask );
		for (i = 0, j = oneStudentEnotes.size(); i < j; i++) {
		    enote = (Enote)oneStudentEnotes.elementAt(i);

		    if (enote instanceof Comment) {
			if (((Comment)enote).getEventId() == cce.getEventId())
			    result.add( enote );
		    } else {
			result.add( enote );
		    }

		} // for
	    } // while

	    rs.close();
	    stmt.close();

        } catch(SQLException e) {
            log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " DBRoutines.java: getPubEnotesFaculty(): " );
            log.append( e.getMessage() );
            System.err.println( log.toString() );
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

	return result;

    } // getPubEnotesFaculty()

    // Returns a Vector of User
    public static Vector getStudentsWhoRcvdComment( int eventId ) {
	DBPool pool;
	Connection conn = null;
	ResultSet rs;
	Statement stmt;
	PreparedStatement homeStmt;
	PreparedStatement groupStmt;
	PreparedStatement userStmt;
	Vector result = new Vector();
	StringBuffer log;

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );

	    homeStmt = conn.prepareStatement( homeSelect );
	    groupStmt = conn.prepareStatement( groupSelect );
	    userStmt = conn.prepareStatement( userSelect );

            stmt = conn.createStatement( );
	    rs = stmt.executeQuery(
		"select userId from users,groups,comments "+
		"where (users.groupId=groups.groupId) "+
		    "and (groupName='student') " +
		    "and (users.userId=comments.studentUserId) "+
		    "and (comments.obsolete=0) " +
		    "and (comments.status='published') " +
		    "and (comments.eventId=" + eventId + ");" );

	    while (rs.next()) 
		result.add( _getUser( rs.getInt(1), homeStmt, groupStmt, userStmt ));

	    rs.close();
	    stmt.close();
	    homeStmt.close();
	    groupStmt.close();
	    userStmt.close();

	} catch(SQLException e) {
	    log = new StringBuffer( logsdf.format( new java.util.Date() ) );
	    log.append( " DBRoutines.java: getStudentsWhoRcvdComment(): " );
	    log.append( e.getMessage() );
	    System.err.println( log.toString() );
	} finally {
	    try { if (conn != null) conn.close(); } catch (SQLException e) {}
	}

	return result;
    } // getStudentsWhoRcvdComment()

} // class DBRoutines
