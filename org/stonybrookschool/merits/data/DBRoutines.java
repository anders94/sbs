package org.stonybrookschool.merits.data;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.text.*;

import com.caucho.sql.DBPool;

import org.stonybrookschool.merits.objects.*;

/* database contains all the methods used for database access */

public class DBRoutines
{
    protected static String poolString = "jdbc/merits";

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

    public static boolean checkPassword( String username, String password )
    //        throws SQLException
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rs;
	String truePassword = null;

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );

	    String sql = "select password from users " +
		"where username='" + username + "'";

	    System.err.println( sql );
	    rs   = stmt.executeQuery( sql );

	    if ( rs.next( ) )
		truePassword = rs.getString( 1 );
	    else
		truePassword = null;     // no such user

	    rs.close();
	    stmt.close();

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: checkPassword( ): sql exception: " + e );
	}
	finally {
	    try {
		if (conn != null) {
		    conn.close();
		}
	    }
	    catch (SQLException e) {
	    }
	}

	if ( password.equals( truePassword ) )
	    return ( true );
	else
	    return ( false );

    }

    public static User getUser( String username )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	User u = new User( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
	    String sql = "select u.id, u.first, u.last, u.title, " +
		" date_format( u.date, '%W, %M %e, %Y %l:%i %p'), " +
		" u.email, u.faculty, u.superuser, " +
		" u.merits, u.demerits, y.class, u.studentCarPermission, " +
		" u.adultCarPermission " +
		"from users u, years y " +
		"where u.yearId = y.id " +
		" and u.username='" + username + "'";

	    System.err.println( sql );
            rs   = stmt.executeQuery( sql );

            if ( rs.next( ) ) {
		u.setId( rs.getInt( 1 ) );
		u.setFirst( rs.getString( 2 ) );
		u.setLast( rs.getString( 3 ) );
		u.setTitle( rs.getString( 4 ) );
		u.setDate( rs.getString( 5 ).toLowerCase( ) );
		u.setEmail( rs.getString( 6 ) );
		u.setFaculty( rs.getString( 7 ) );
		u.setSuperuser( rs.getString( 8 ) );
		u.setMerits( rs.getFloat( 9 ) );
		u.setDemerits( rs.getFloat( 10 ) );
		u.setYear( rs.getString( 11 ) );
		u.setStudentCarPermission( rs.getString( 12 ) );
		u.setAdultCarPermission( rs.getString( 13 ) );
		u.setUsername( username );
	    }

            rs.close( );
            stmt.close( );

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getUser( username ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
	return ( u );

    }

    public static User getUser( int userId )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	User u = new User( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select u.username, u.first, u.last, u.title, " +
				      " date_format( u.date, '%W, %M %e, %Y %l:%i %p'), " +
				      " u.email, u.faculty, u.superuser, " +
				      " u.merits, u.demerits, y.class, u.studentCarPermission, " +
				      " u.adultCarPermission " +
				      "from users u, years y " +
                                      "where u.yearId = y.id " +
				      " and u.id=" + userId );

            if ( rs.next( ) ) {
		u.setUsername( rs.getString( 1 ) );
		u.setFirst( rs.getString( 2 ) );
		u.setLast( rs.getString( 3 ) );
		u.setTitle( rs.getString( 4 ) );
		u.setDate( rs.getString( 5 ).toLowerCase( ) );
		u.setEmail( rs.getString( 6 ) );
		u.setFaculty( rs.getString( 7 ) );
		u.setSuperuser( rs.getString( 8 ) );
		u.setMerits( rs.getFloat( 9 ) );
		u.setDemerits( rs.getFloat( 10 ) );
		u.setYear( rs.getString( 11 ) );
		u.setStudentCarPermission( rs.getString( 12 ) );
		u.setAdultCarPermission( rs.getString( 13 ) );
	    }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getUser( userId ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
	return ( u );

    }

    public static Vector getHistory( int userId )
    //        throws SQLException
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	Vector hv = new Vector( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select date_format( h.date, '%W, %M %e, %Y %l:%i %p'), " +
				      " concat( u.first, ' ', u.last ), " +
				      " h.recieverUserId, h.deltaMerits, " +
				      " h.deltaDemerits, concat( et.name, ': ', e.date ), h.reason " +
				      "from history h, users u, events e, " +
				      " eventTypes et " +
				      "where h.giverUserId = u.id " +
				      " and ( h.recieverUserId = " + userId + 
				      " or h.giverUserId = " + userId + " ) " +
				      " and h.eventId = e.id " +
				      " and e.eventTypeId = et.id " +
				      "order by h.date desc" );

            while ( rs.next( ) ) {
		hv.addElement( new History ( rs.getString( 1 ).toLowerCase( ),
					     rs.getString( 2 ),
					     rs.getInt( 3 ),
					     rs.getFloat( 4 ),
					     rs.getFloat( 5 ),
					     rs.getString( 6 ),
					     rs.getString( 7 ) ) );
	    }

            rs.close( );
            stmt.close( );

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getHistory( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
	return ( hv );

    }

    public static Vector getPasses( int userId )
    //        throws SQLException
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	Vector pv = new Vector( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
	    rs   = stmt.executeQuery( "select p.id, date_format( p.date, '%W, %M %e, %Y %l:%i %p' ) as date, " +
				      " concat( u.first, ' ', u.last ) as user, et.name as event, " +
				      " date_format( e.date, '%W, %M %e, %Y %l:%i %p' ) as eventDate, " +
				      " p.purchasePrice, p.eventId, t.name as transportation, p.whereGoing, " +
				      " date_format( p.whenLeaving, '%W, %M %e, %Y %l:%i %p' ) as whenLeaving, " +
				      " date_format( p.whenReturning, '%W, %M %e, %Y %l:%i %p' ) as whenReturning " +
				      "from passes p " +
				      " left join users u on p.userId = u.id " +
				      " left join transportations t on p.transportationId = t.id " +
				      " left join events e on p.eventId = e.id " +
				      " left join eventTypes et on e.eventTypeId = et.id " +
				      "where p.userId = " + userId +
				      " and e.date > now( ) " +
				      " and et.name != 'misc' " +
				      "order by e.date" );

            while ( rs.next( ) ) {
		Pass p = new Pass( );

		p.setId( rs.getInt( 1 ) );
		p.setDate( rs.getString( 2 ).toLowerCase( ) );
		p.setUser( rs.getString( 3 ) );
		p.setEvent( rs.getString( 4 ) );
		p.setEventDate( rs.getString( 5 ).toLowerCase( ) );
		p.setPurchasePrice( rs.getFloat( 6 ) );
		p.setEventId( rs.getInt( 7 ) );
		if ( rs.getString( 8 ) != null ) {
		    p.setTransportation( rs.getString( 8 ) );
		    p.setWhereGoing( rs.getString( 9 ) );
		    p.setWhenLeaving( rs.getString( 10 ).toLowerCase( ) );
		    p.setWhenReturning( rs.getString( 11 ).toLowerCase( ) );

		}
		pv.addElement( p );

	    }
            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getPasses( userId ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
	return ( pv );

    }

    public static Vector getYears( )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	Vector yv = new Vector( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select id, year, class from years" );

            while ( rs.next( ) ) {
		yv.addElement( new Year ( rs.getInt( 1 ),
					  rs.getInt( 2 ),
					  rs.getString( 3 ) ) );
	    }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getYears( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
	return ( yv );

    }

    public static Vector getPassesByEventId( int eventId )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	Vector pv = new Vector( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
	    rs   = stmt.executeQuery( "select p.id, date_format( p.date, '%W, %M %e, %Y %l:%i %p' ) as date, " +
				      " concat( u.first, ' ', u.last ) as user, u.id as userId, et.name as event, " +
				      " date_format( e.date, '%W, %M %e, %Y %l:%i %p' ) as eventDate, " +
				      " p.purchasePrice, p.eventId, t.name as transportation, p.whereGoing, " +
				      " date_format( p.whenLeaving, '%W, %M %e, %Y %l:%i %p' ) as whenLeaving, " +
				      " date_format( p.whenReturning, '%W, %M %e, %Y %l:%i %p' ) as whenReturning " +
				      "from passes p " +
				      " left join users u on p.userId = u.id " +
				      " left join transportations t on p.transportationId = t.id " +
				      " left join events e on p.eventId = e.id " +
				      " left join eventTypes et on e.eventTypeId = et.id " +
				      "where e.id = " + eventId +
				      " and et.name != 'misc' " +
				      "order by e.date" );

            while ( rs.next( ) ) {
		Pass p = new Pass( );

		p.setId( rs.getInt( 1 ) );
		p.setDate( rs.getString( 2 ).toLowerCase( ) );
		p.setUser( rs.getString( 3 ) );
		p.setUserId( rs.getInt( 4 ) );
		p.setEvent( rs.getString( 5 ) );
		p.setEventDate( rs.getString( 6 ).toLowerCase( ) );
		p.setPurchasePrice( rs.getFloat( 7 ) );
		p.setEventId( rs.getInt( 8 ) );
		if ( rs.getString( 9 ) != null ) {
		    p.setTransportation( rs.getString( 9 ) );
		    p.setWhereGoing( rs.getString( 10 ) );
		    p.setWhenLeaving( rs.getString( 11 ).toLowerCase( ) );
		    p.setWhenReturning( rs.getString( 12 ).toLowerCase( ) );

		}
		pv.addElement( p );

	    }
            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getPassesByEventId( eventId ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
	return ( pv );

    }

    public static Vector getEventTypes( )
    //        throws SQLException
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	Vector etv = new Vector( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select id, name, meal from eventTypes where name != 'misc' order by id" );

            while ( rs.next( ) ) {
		etv.addElement( new EventType( rs.getInt( 1 ),
					       rs.getString( 2 ),
					       rs.getString( 3 ) ) );

	    }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getEventTypes( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
	return ( etv );

    }

    public static EventType getEventType( int eventTypeId )
    //        throws SQLException
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	EventType e = new EventType( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select name, description, lowPrice, " +
				      " highPrice, threshold, transportation, meal " +
				      "from eventTypes " +
				      "where id = " + eventTypeId );

            if ( rs.next( ) ) {
		e.setId( eventTypeId );
		e.setName( rs.getString( 1 ) );
		e.setDescription( rs.getString( 2 ) );
		e.setLowPrice( rs.getFloat( 3 ) );
		e.setHighPrice( rs.getFloat( 4 ) );
		e.setThreshold( rs.getInt( 5 ) );
		e.setTransportation( rs.getString( 6 ) );
		e.setMeal( rs.getString( 7 ) );

	    }

            rs.close( );
            stmt.close( );

        }
        catch (SQLException sqle) {
            System.err.println( "DBRoutines.java: getEventType( eventTypeId ): sql exception: " + sqle );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException sqle) {
            }
        }
         
	return ( e );

    }

    public static Vector getEvents( int eventTypeId )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        Vector ev = new Vector( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select e.id, date_format( e.date, '%W, %M %e, %Y %l:%i %p'), " +
                                      " et.name, e.purchases " +
                                      "from events e, eventTypes et " +
                                      "where e.date > now( ) " +
                                      " and e.eventTypeId = " + eventTypeId +
                                      " and e.eventTypeId = et.id " +
                                      "order by e.date" );

            while ( rs.next( ) ) {
                ev.addElement( new Event( rs.getInt( 1 ),
                                          rs.getString( 2 ).toLowerCase( ),
                                          rs.getString( 3 ),
                                          rs.getInt( 4 ) ) );

            }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getEvents( eventTypeId ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
        return ( ev );

    }

    public static Vector getEvents( )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        Vector ev = new Vector( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select e.id, date_format( e.date, '%W, %M %e, %Y %l:%i %p'), " +
                                      " et.name, e.purchases " +
                                      "from events e, eventTypes et " +
                                      "where et.name != 'misc' " +
				      " and e.eventTypeId = et.id " +
                                      "order by e.date desc" );

            while ( rs.next( ) ) {
                ev.addElement( new Event( rs.getInt( 1 ),
                                          rs.getString( 2 ).toLowerCase( ),
                                          rs.getString( 3 ),
                                          rs.getInt( 4 ) ) );

            }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getEvents( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
        return ( ev );

    }

    public static Event getEvent( int eventId )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	Event ev = new Event( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select date_format( e.date, '%W, %M %e, %Y %l:%i %p'), " +
				      " et.name, e.purchases, e.date - now(), e.eventTypeId, " +
				      " e.date " +
				      "from events e, eventTypes et " +
				      "where e.id = " + eventId +
				      " and e.eventTypeId = et.id" );

            if ( rs.next( ) ) {
		ev.setId( eventId );
		ev.setDate( rs.getString( 1 ).toLowerCase( ) );
		ev.setEventType( rs.getString( 2 ) );
		ev.setPurchases( rs.getInt( 3 ) );
		ev.setSecondsTill( rs.getInt( 4 ) );
		ev.setEventTypeId( rs.getInt( 5 ) );
		ev.setRawDate( rs.getString( 6 ) );

	    }
	    else
		ev = null;

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getEvent( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
	return ( ev );

    }

    public static int getEventIdByDate( String date )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	int eventId = 0;

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select id from events where date = '" + date + "'" );

            if ( rs.next( ) ) {
		eventId = rs.getInt( 1 );

	    }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getEventIdByDate( date ): exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
	return ( eventId );

    }

    public static boolean checkUserOwnsEvent( int userId, int eventId )
    //        throws SQLException
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rs;
	boolean b = false;

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rs   = stmt.executeQuery( "select id from passes " +
				      "where userId=" + userId + 
				      " and eventId=" + eventId );

	    if ( rs.next( ) )
		b = true;

	    rs.close();
	    stmt.close();

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: checkUserOwnsEvent( ): sql exception: " + e );
	}
	finally {
	    try {
		if (conn != null) {
		    conn.close();
		}
	    }
	    catch (SQLException e) {
	    }
	}

	return ( b );

    }

    public static void setUserMerits( int userId, float merits )
    //        throws SQLException
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            stmt.executeQuery( "update users " +
			       "set merits=" + merits + " " +
			       "where id=" + userId );

            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: setUserMerits( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
    }

    public static void setUserDemerits( int userId, float demerits )
    //        throws SQLException
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            stmt.executeQuery( "update users " +
			       "set demerits=" + demerits + " " +
			       "where id=" + userId );

            stmt.close( );

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: setUserDemerits( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
    }

    public static void newHistory( int giverUserId, int recieverUserId,
				   float deltaMerits, float deltaDemerits, int eventId,
				   String reason )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;

	System.err.println( "newHistory( giverUserId=" + giverUserId + ", recieverUserId=" + recieverUserId + 
			    ", deltaMerits=" + deltaMerits + ", deltaDemerits=" + deltaDemerits + ", eventId=" + eventId +
			    ", reason=" + reason );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            stmt.executeQuery( "insert into history values ( " +
			       " now(), " + giverUserId + ", " +
			       recieverUserId + ", " + deltaMerits +
			       ", " + deltaDemerits + ", " + eventId + 
			       ", '" + reason + "')" );

            stmt.close( );

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: newHistory( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
    }

    public static void newEvent( String date, int eventTypeId )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            stmt.executeQuery( "insert into events values ( " +
			       " null, '" + date + "', " +
			       eventTypeId + ", 0 )" );

            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: newEvent( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
    }

    public static void newUser( User u )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            stmt.executeQuery( "insert into users values ( " +
			       " null, '" + u.getFirst( ) + "', " +
			       " '" + u.getLast( ) + "', " +
			       " '" + u.getTitle( ) + "', " +
			       " now( ), '" + u.getUsername( ) + "', " +
			       " '" + u.getPassword( ) + "', " +
			       " '" + u.getEmail( ) + "', " +
			       " '" + u.getFaculty( ) + "', " +
			       " '" + u.getSuperuser( ) + "', " +
			       " '" + u.getMerits( ) + "', " +
			       " '" + u.getDemerits( ) + "', " +
			       " '" + u.getYearId( ) + "', " +
			       " '" + u.getStudentCarPermission( ) + "', " +
			       " '" + u.getAdultCarPermission( ) + "' )" );

            stmt.close( );

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: newUser( user ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
    }

    public static void newPass( int userId, int eventId, float purchasePrice,
				int transportationId, String whereGoing,
				String whenLeaving, String whenReturning )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
	whereGoing = escapeTics( whereGoing );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            stmt.executeQuery( "insert into passes values ( " +
			       " null, now( ), " + userId + ", " +
			       eventId + ", '" + whereGoing + "', '" +
			       whenLeaving + "', '" + whenReturning + "', " +
			       transportationId + ", " + purchasePrice + ")" );
	    stmt.close( );
            stmt = conn.createStatement( );
            stmt.executeQuery( "update events " +
			       "set purchases = purchases + 1 " +
			       "where id = " + eventId );
            stmt.close( );


        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: newPass( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
    }

    public static Pass getPass( int passId )
    //        throws SQLException
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	Pass p = new Pass( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select p.id, date_format( p.date, '%W, %M %e, %Y %l:%i %p'), " +
				      " p.userId, p.eventId, p.purchasePrice, p.transportationId, " +
				      " t.name, p.whereGoing, p.whenLeaving, p.whenReturning " +
				      "from passes p " +
				      " left join transportations t on p.transportationId = t.id " +
				      "where p.id = " + passId );

            if ( rs.next( ) ) {
		p.setId( rs.getInt( 1 ) );
		p.setDate( rs.getString( 2 ).toLowerCase( ) );
		p.setUserId( rs.getInt( 3 ) );
		p.setEventId( rs.getInt( 4 ) );
		p.setPurchasePrice( rs.getFloat( 5 ) );
		if ( "t".equals( p.getTransportation( ) ) ) {
		    p.setTransportationId( rs.getInt( 6 ) );
		    p.setTransportation( rs.getString( 7 ) );
		    p.setWhereGoing( rs.getString( 8 ) );
		    p.setWhenLeaving( rs.getString( 9 ).toLowerCase( ) );
		    p.setWhenReturning( rs.getString( 10 ).toLowerCase( ) );

		}

	    }
            rs.close( );
            stmt.close( );

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getPass( passId ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }

	return ( p );

    }

    public static void deletePass( int passId )
    //        throws SQLException
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
	ResultSet rs;
	String eventId;

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs = stmt.executeQuery( "select eventId " +
				    "from passes " +
				    "where id = " + passId );
	    if ( rs.next( ) ) {
		eventId = rs.getString( 1 );
		stmt.close( );
		stmt = conn.createStatement( );
		stmt.executeQuery( "delete from passes " +
				   "where id = " + passId );
		stmt.close( );
		stmt = conn.createStatement( );		
		stmt.executeQuery( "update events " +
				   "set purchases = purchases - 1 " + 
				   "where id = " + eventId );
	    }
	    else
		System.err.println( "DBRoutines.java: deletePass( ): no such passId: " + passId );

	    rs.close( );
	    stmt.close( );

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: deletePass( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
    }

    public static Vector getGroups( )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	Vector gv = new Vector( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select id, name from groups" );

            while ( rs.next( ) ) {
		gv.addElement( new Group( rs.getInt( 1 ),
					  rs.getString( 2 ) ) );

	    }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getGroups( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }

	return ( gv );

    }

    public static Vector getGroups( int userId )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	Vector gv = new Vector( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select g.id, g.name " +
				      "from users_groups ug, groups g " +
				      "where ug.userId = " + userId + 
				      " and ug.groupId=g.id" );

            while ( rs.next( ) ) {
		gv.addElement( new Group( rs.getInt( 1 ),
					  rs.getString( 2 ) ) );

	    }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getGroups( userId ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }

	return ( gv );

    }

    public static Vector getUsers( int groupId )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	Vector uv = new Vector( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
	    String sql = "select u.id, u.first, u.last, u.title, " +
		" date_format( u.date, '%W, %M %e, %Y %l:%i %p'), " +
		" u.username, u.email, u.faculty, u.superuser, " +
		" u.merits, u.demerits, y.class, u.studentCarPermission, " +
		" u.adultCarPermission " +
		"from users u, years y, users_groups ug " +
		"where ug.userId = u.id " +
		"  and ug.groupId = " + groupId +
		"  and u.yearId = y.id " +
		"order by u.faculty, u.last";

	    System.err.println( sql );

	    rs   = stmt.executeQuery( sql );

            while ( rs.next( ) ) {
		uv.addElement( new User( rs.getInt( 1 ),
					 rs.getString( 2 ),
					 rs.getString( 3 ),
					 rs.getString( 4 ),
					 rs.getString( 5 ).toLowerCase( ),
					 rs.getString( 6 ),
					 rs.getString( 7 ),
					 rs.getString( 8 ),
					 rs.getString( 9 ),
					 rs.getFloat( 10 ),
					 rs.getFloat( 11 ),
					 rs.getString( 12 ),
					 rs.getString( 13 ),
					 rs.getString( 14 ) ) );

	    }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getUsers( groupId=" + groupId + " ): sql exception: " + e );

        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }

	return ( uv );

    }

    public static Vector getUsers( )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	Vector uv = new Vector( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );

	    String sql = "select u.id, u.first, u.last, u.title, " +
		"  date_format( u.date, '%W, %M %e, %Y %l:%i %p'), " +
		"  u.username, u.email, u.faculty, u.superuser, " +
		"  u.merits, u.demerits, y.class, u.studentCarPermission, " +
		" u.adultCarPermission " +
		"from users u, years y " +
		"where u.yearId = y.id " +
		"order by u.faculty, u.last";

	    System.err.println( sql );

            rs   = stmt.executeQuery( sql );

            while ( rs.next( ) ) {
		uv.addElement( new User( rs.getInt( 1 ),
					 rs.getString( 2 ),
					 rs.getString( 3 ),
					 rs.getString( 4 ),
					 rs.getString( 5 ).toLowerCase( ),
					 rs.getString( 6 ),
					 rs.getString( 7 ),
					 rs.getString( 8 ),
					 rs.getString( 9 ),
					 rs.getFloat( 10 ),
					 rs.getFloat( 11 ),
					 rs.getString( 12 ),
					 rs.getString( 13 ),
					 rs.getString( 14 ) ) );

	    }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getUsers( ) - returns vector: sql exception: " + e );

        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }

	return ( uv );

    }

    public static Vector getPurchasers( int eventId )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	Vector pv = new Vector( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select u.id, u.first, u.last, u.title, " +
                                      " date_format( u.date, '%W, %M %e, %Y %l:%i %p'), " +
                                      " u.username, u.email, u.faculty, u.superuser, " +
                                      " u.merits, u.demerits, y.class, u.studentCarPermission, " +
                                      " u.adultCarPermission " +
				      "from users u, years y, passes p " +
				      "where p.eventId = " + eventId +
				      "  and p.userId = u.id " +
				      "  and u.yearId = y.id " +
				      "order by u.last" );

            while ( rs.next( ) ) {
		pv.addElement( new User( rs.getInt( 1 ),
					 rs.getString( 2 ),
					 rs.getString( 3 ),
					 rs.getString( 4 ),
					 rs.getString( 5 ).toLowerCase( ),
					 rs.getString( 6 ),
					 rs.getString( 7 ),
					 rs.getString( 8 ),
					 rs.getString( 9 ),
					 rs.getFloat( 10 ),
					 rs.getFloat( 11 ),
					 rs.getString( 12 ),
					 rs.getString( 13 ),
					 rs.getString( 14 ) ) );

	    }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getPurchasers( eventId ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }

	return ( pv );

    }

    public static Vector getLatestEvents( int eventTypeId )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        Vector ev = new Vector( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select e.id, date_format( e.date, '%W, %M %e, %Y %l:%i %p'), " +
                                      " et.name, e.purchases " +
                                      "from events e, eventTypes et " +
                                      "where e.date < now( ) " +
                                      " and e.eventTypeId = " + eventTypeId +
                                      " and e.eventTypeId = et.id " +
                                      "order by e.date desc" );

            while ( rs.next( ) ) {
                ev.addElement( new Event( rs.getInt( 1 ),
                                          rs.getString( 2 ).toLowerCase( ),
                                          rs.getString( 3 ),
                                          rs.getInt( 4 ) ) );

            }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getLatestEvents( eventTypeId ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
        return ( ev );

    }

    public static boolean isUserInGroup( int userId, int groupId )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rs;
	boolean b = false;

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rs   = stmt.executeQuery( "select userId from users_groups " +
				      "where userId=" + userId + 
				      " and groupId=" + groupId );

	    if ( rs.next( ) )
		b = true;

	    rs.close();
	    stmt.close();

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: isUserInGroup( userId, groupId ): sql exception: " + e );
	}
	finally {
	    try {
		if (conn != null) {
		    conn.close();
		}
	    }
	    catch (SQLException e) {
	    }
	}

	return ( b );

    }

    public static void deleteUsersGroups( int userId )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;

	System.err.println( "\n\ndelete from users_groups where userId = " + userId );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            stmt.executeQuery( "delete from users_groups " +
			       "where userId = " + userId );
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: deleteUsersGroups( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
    }

    public static void setUsersGroups( int userId, int groupId )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            stmt.executeQuery( "insert into users_groups values ( " +
			       userId + ", " + groupId + " )" );
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: setUsersGroups( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
    }

    public static void updateUser( User u )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;

	System.err.println( "\n\nupdate users\n\n" );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
	    if ( "".equals( u.getPassword( ) ) )
		stmt.executeQuery( "update users " +
				   "set first='" + u.getFirst( ) + "', " +
				   " last='" + u.getLast( ) + "', " +
				   " title='" + u.getTitle( ) + "', " +
				   " username='" + u.getUsername( ) + "', " +
				   " email='" + u.getEmail( ) + "', " +
				   " faculty='" + u.getFaculty( ) + "', " +
				   " superuser='" + u.getSuperuser( ) + "', " +
				   " merits=" + u.getMerits( ) + ", " +
				   " demerits=" + u.getDemerits( ) + ", " +
				   " yearId=" + u.getYearId( ) + ", " +
				   " studentCarPermission='" + u.getStudentCarPermission( ) + "', " +
				   " adultCarPermission='" + u.getAdultCarPermission( ) + "' " +
				   "where id=" + u.getId( ) );
	    else
		stmt.executeQuery( "update users " +
				   "set first='" + u.getFirst( ) + "', " +
				   " last='" + u.getLast( ) + "', " +
				   " title='" + u.getTitle( ) + "', " +
				   " username='" + u.getUsername( ) + "', " +
				   " password='" + u.getPassword( ) + "', " +
				   " email='" + u.getEmail( ) + "', " +
				   " faculty='" + u.getFaculty( ) + "', " +
				   " superuser='" + u.getSuperuser( ) + "', " +
				   " merits=" + u.getMerits( ) + ", " +
				   " demerits=" + u.getDemerits( ) + ", " +
				   " yearId=" + u.getYearId( ) + ", " +
				   " studentCarPermission='" + u.getStudentCarPermission( ) + "', " +
				   " adultCarPermission='" + u.getAdultCarPermission( ) + "' " +
				   "where id=" + u.getId( ) );

            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: updateUser( user ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
    }

    public static void updateEvent( int eventId, String date, int eventTypeId )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;

	System.err.println( "\n\nupdate events\n\n" );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
	    stmt.executeQuery( "update events " +
			       "set date='" + date + "', " +
			       " eventTypeId=" + eventTypeId + " " +
			       "where id=" + eventId );
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: updateEvent( eventId, date, eventTypeId ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
    }

    public static void deleteEvent( int eventId )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
	    stmt.executeQuery( "delete from events where id = " + eventId );
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: deleteEvent( eventId ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
    }

    public static String getDatabaseDate( )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	String dbDate = "";
        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select date_format( now( ), '%W, %M %e, %Y %l:%i:%s %p')" );
            rs.next( );
	    dbDate = rs.getString( 1 ).toLowerCase( );
            rs.close( );
            stmt.close( );

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getDatabaseDate( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
	return ( dbDate );

    }

    public static String getPreviousPurchaseDate( int userId, int eventId, int seconds )
    {
	// finds the last user purchase of a similar event within the 
        // previous number of seconds before the given event. if an 
        // event is found within the timeframe, the type and date of 
        // that purchase will be returned as a string. if not, an empty 
        // string is returned. this is usefull because students are 
        // sometimes limited to one meal purchase (dinner passes for 
        // example) within a given time frame. (2 weeks for example) 
        // should the user try to purchase a second pass of the same 
        // type within the blackout period, the user should be shown 
        // the type and date of the previous purchase holding this 
        // purchase up.

        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	String date = "";
	String eventTypeName = "";
	String textString = "";

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
	    rs   = stmt.executeQuery( "select e.date, et.name " +
				      "from events e " +
				      " left join eventTypes et on e.eventTypeId = et.id " +
				      "where e.id = " + eventId );
	    if ( rs.next( ) ) {
		date = rs.getString( 1 );
		eventTypeName = rs.getString( 2 );
		rs   = stmt.executeQuery( "select concat( et.name, ' on ', " +
					  " date_format( e.date, '%W, %M %e, %Y %l:%i:%s %p') ) " +
					  "from passes p " +
					  " left join events e on p.eventId = e.id " +
					  " left join eventTypes et on e.eventTypeId = et.id " +
					  "where et.name = '" + eventTypeName + "' " +
					  " and userId = " + userId +
					  " and e.date > ( '" + date + "' - interval " + seconds + " second ) " +
					  " and e.date < ( '" + date + "' + interval " + seconds + " second )" );
		if ( rs.next( ) )
		    textString = rs.getString( 1 ).toLowerCase( );

	    }
            rs.close( );
            stmt.close( );

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getPreviousPurchaseDate( userId, eventIt, seconds ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
	return ( textString );

    }

    public static Vector getTransportations( )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	Vector tv = new Vector( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select id, name from transportations" );

            while ( rs.next( ) ) {
		tv.addElement( new Transportation( rs.getInt( 1 ),
						   rs.getString( 2 ) ) );
	    }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getTransportations( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
	return ( tv );

    }

    public static Vector getTableSizeVector( )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	Vector tsv = new Vector( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select count(*) from eventTypes" );
	    rs.next( );
	    tsv.addElement( "eventTypes: " + rs.getInt( 1 ) );
            rs   = stmt.executeQuery( "select count(*) from events" );
	    rs.next( );
	    tsv.addElement( "events: " + rs.getInt( 1 ) );
            rs   = stmt.executeQuery( "select count(*) from groups" );
	    rs.next( );
	    tsv.addElement( "groups: " + rs.getInt( 1 ) );
            rs   = stmt.executeQuery( "select count(*) from history" );
	    rs.next( );
	    tsv.addElement( "history: " + rs.getInt( 1 ) );
            rs   = stmt.executeQuery( "select count(*) from passes" );
	    rs.next( );
	    tsv.addElement( "passes: " + rs.getInt( 1 ) );
            rs   = stmt.executeQuery( "select count(*) from transportations" );
	    rs.next( );
	    tsv.addElement( "transportations: " + rs.getInt( 1 ) );
            rs   = stmt.executeQuery( "select count(*) from users" );
	    rs.next( );
	    tsv.addElement( "users: " + rs.getInt( 1 ) );
            rs   = stmt.executeQuery( "select count(*) from users_groups" );
	    rs.next( );
	    tsv.addElement( "users_groups: " + rs.getInt( 1 ) );
            rs   = stmt.executeQuery( "select count(*) from years" );
	    rs.next( );
	    tsv.addElement( "years: " + rs.getInt( 1 ) );

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getTransportations( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }

	return ( tsv );

    }

    public static String getTotalMeritsAwarded( int userId )
    //        throws SQLException
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	String totalMerits = "0";

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select sum( deltaMerits ) " +
				      "from history " +
				      "where deltamerits > 0 " +
				      " && giverUserId != 1 " +
				      " && recieverUserId = " + userId );

            if ( rs.next( ) ) {
		totalMerits = rs.getString( 1 );

	    }

            rs.close( );
            stmt.close( );

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getHistory( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
	return ( totalMerits );

    }

    public static String getTotalDemeritsAwarded( int userId )
    //        throws SQLException
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	String totalDemerits = "0";

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select sum( deltaDemerits ) " +
				      "from history " +
				      "where deltaDemerits > 0 " +
				      " && giverUserId != 1 " +
				      " && recieverUserId = " + userId );

            if ( rs.next( ) ) {
		totalDemerits = rs.getString( 1 );

	    }

            rs.close( );
            stmt.close( );

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getHistory( ): sql exception: " + e );
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }    
            catch (SQLException e) {
            }
        }
         
	return ( totalDemerits );

    }

}
