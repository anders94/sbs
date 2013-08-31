package org.stonybrookschool.dailyBulletin.data;

import java.io.*;
import java.sql.*;
import java.util.*;

import com.caucho.sql.DBPool;

import org.stonybrookschool.dailyBulletin.*;
import org.stonybrookschool.dailyBulletin.objects.*;

/* database contains all the methods used for database access */

public class DBRoutines
{

    protected static String poolString = "jdbc/dailyBulletin";

    public static Story getStory( int storyId )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rset;
	Story s = new Story( );

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rset = stmt.executeQuery( "select s.id, s.date, s.publishDate, " + 
				      "  s.unpublishDate, s.head, s.text, s.userId, " + 
				      "  s.state, s.student, s.staff, s.parent, " + 
				      "  s.priority, " +
				      "  concat( u.first, ' ', u.last ) " +
				      "from stories s, users u " +
				      "where ( s.id=" + storyId + " ) " + 
				      "  and ( s.userId = u.id ) " );
	    if ( rset.next( ) ) {
		s.setId( rset.getInt( 1 ) );
		s.setDate( rset.getString( 2 ) );
		s.setPublishDate( rset.getString( 3 ) );
		s.setUnpublishDate( rset.getString( 4 ) );
		s.setHead( rset.getString( 5 ) );
		s.setText( rset.getString( 6 ) );
		s.setUserId( rset.getInt( 7 ) );
		s.setState( rset.getString( 8 ) );
		s.setStudent( rset.getString( 9 ) );
		s.setStaff( rset.getString( 10 ) );
		s.setParent( rset.getString( 11 ) );
		s.setPriority( rset.getInt( 12 ) );
		s.setUserName( rset.getString( 13 ) );

	    }

	    rset.close( );
	    stmt.close( );

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: getStory( ): sql exception: " + e );
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

	return( s );

    }

    public static void updateStory( Story s )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rset;

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );

	    if ( s.getId( ) > 0 )
	    {
		rset = stmt.executeQuery( "UPDATE stories  " +
					  "SET publishDate = '" + s.getPublishDate( ) +
					  "' , unpublishDate = '" + s.getUnpublishDate( ) +
					  "' , head = '" + CommonRoutines.escapeTics( s.getHead( ) ) +
					  "' , text = '" + CommonRoutines.escapeTics( s.getText( ) ) +
					  "' , state = '" + s.getState( ) +
					  "' , student = '" + s.getStudent( ) +
					  "' , staff = '" + s.getStaff( ) +
					  "' , parent = '" + s.getParent( ) +
					  "' , priority = '" + s.getPriority( ) + 
					  "' WHERE id = '" + s.getId( ) + "' ;" );
	    };
	    stmt.close( );

    
	    }

	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: getStory( ): sql exception: " + e );
	}
	finally {
	    try {
		if (conn != null) {
		    conn.close();
		}
	    }
	    catch (SQLException e) {
		System.err.println( "DBRoutines.java: getStory( ): sql exception: " + e );	    
	    }

	}

    }

    public static void insertStory( Story s )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );

	    stmt.executeQuery( "INSERT INTO stories ( " +
			       "date,  publishDate, unpublishDate, " +
			       "head, text, state, student, " +
			       "staff, parent, priority, userId ) " + 
			       "VALUES ( now( ), '" +
			       s.getPublishDate( ) + "' , '" +
			       s.getUnpublishDate( ) + "' , '" +
			       CommonRoutines.escapeTics( s.getHead( ) ) + "' , '" +
			       CommonRoutines.escapeTics( s.getText( ) ) + "' , '" +
			       s.getState( ) + "' , '" +
			       s.getStudent( ) + "' , '" +
			       s.getStaff( ) + "' , '" +
			       s.getParent( ) + "' , '" +
			       s.getPriority( ) + "' , '" +
			       s.getUserId( ) + "' );" );
	    stmt.close( );
	}

	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: insertStory( ): sql exception: " + e );
	}
	finally {
	    try {
		if (conn != null) {
		    conn.close();
		}
	    }
	    catch (SQLException e) {
		System.err.println( "DBRoutines.java: insertStory( ): sql exception: " + e );
	    }

	}

    }

    public static User getUser( String username )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rset;
	User u = new User( );

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rset = stmt.executeQuery( "select id, email, first, last, " + 
				      "  publisher, staff " + 
				      "from users " +  
				      "where username='" + username + "'" ); 

	    if ( rset.next( ) ) {
		u.setId( rset.getInt( 1 ) );
		u.setEmail( rset.getString( 2 ) );
		u.setFirst( rset.getString( 3 ) );
		u.setLast( rset.getString( 4 ) );
		u.setPublisher( rset.getString( 5 ) );
		u.setStaff( rset.getString( 6 ) );

	    }

	    rset.close( );
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

	return( u );

    }

    public static Vector getUserStoryIds( int user )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rset;
	Vector vect = new Vector( );

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rset = stmt.executeQuery( "select id " +
				      "from posts " +
				      "where ( userId = " + user + " ) " +
				      "order by publishDate desc " );

	    while ( rset.next( ) )
		vect.addElement( rset.getString( 1 ) );

	    rset.close( );
	    stmt.close( );

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: getPosts( ): sql exception: " + e );
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

	return( vect );

    }

    public static Vector getUserOndeckStories( int userId )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rset;
	Vector vect = new Vector( );

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rset = stmt.executeQuery( "select s.id, s.date, s.publishDate, " +
                                      "  s.unpublishDate, s.head, s.text, s.userId, " +
                                      "  s.state, s.student, s.staff, s.parent, " +
                                      "  s.priority, " +
                                      "  concat( u.first, ' ', u.last ) " +
                                      "from stories s, users u " +
				      "where s.userId = " + userId +
				      "  and s.userId = u.id " +
				      "  and ( s.state = 'pending' " +
				      "        or s.state = 'published' ) " + 
				      "order by s.state , s.publishDate desc " );

 	    while (rset.next ())
		vect.addElement(  new Story( rset.getInt( 1 ),
                                            rset.getString( 2 ),
                                            rset.getString( 3 ),
                                            rset.getString( 4 ),
                                            rset.getString( 5 ),
                                            rset.getString( 6 ),
                                            rset.getInt( 7 ),
                                            rset.getString( 8 ),
                                            rset.getString( 9 ),
                                            rset.getString( 10 ),
                                            rset.getString( 11 ),
                                            rset.getInt( 12 ),
                                            rset.getString( 13 ) ) );

	    rset.close( );
	    stmt.close( );

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: getUserCurrentStories( ): sql exception: " + e );
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

	return( vect );

    }

    public static Vector getOndeckStories(  )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rset;
	Vector vect = new Vector( );

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rset = stmt.executeQuery( "select  s.id, s.date, s.publishDate, " +
                                      "  s.unpublishDate, s.head, s.text, s.userId, " +
                                      "  s.state, s.student, s.staff, s.parent, " +
                                      "  s.priority, " +
                                      "  concat( u.first, ' ', u.last ) " +
                                      "from stories s, users u " +
				      "where s.userId = u.id and ( ( s.state='published' " +
				      "  and s.unpublishDate > now( ) ) " +
				      " or  " +
				      "  ( s.state = 'pending' ) ) " +
				      "order by s.state, s.publishDate desc" );

 	    while (rset.next( ))
		vect.addElement(  new Story( rset.getInt( 1 ),
                                            rset.getString( 2 ),
                                            rset.getString( 3 ),
                                            rset.getString( 4 ),
                                            rset.getString( 5 ),
                                            rset.getString( 6 ),
                                            rset.getInt( 7 ),
                                            rset.getString( 8 ),
                                            rset.getString( 9 ),
                                            rset.getString( 10 ),
                                            rset.getString( 11 ),
                                            rset.getInt( 12 ),
                                            rset.getString( 13 ) ) );

	    rset.close( );
	    stmt.close( );

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: getUserCurrentStories( ): sql exception: " + e );
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

	return( vect );

    }

    // getStories( publication ) soon to be depricated

    public static Vector getStories( String publication )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rset;
	Vector vect = new Vector( );

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rset = stmt.executeQuery( "select s.id, s.date, s.publishdate, " +
				      "  s.unpublishDate, s.head, s.text, " +
				      "  s.userId, s.state, " +
				      "  s.student, s.staff, s.parent, s.priority, " +
				      "  concat( u.first, ' ', u.last ) " +
				      "from stories s, users u " +
				      "where s.state='published' " +
				      "  and s.publishDate < now( ) " +
				      "  and s.unpublishDate > now( ) " +
				      "  and s." + publication + " = 't' " +
				      "  and s.userId = u.id " +
				      "order by s.priority, s.publishDate desc" );

	    while (rset.next( ))
		vect.addElement( new Story( rset.getInt( 1 ),
					    rset.getString( 2 ),
					    rset.getString( 3 ),
					    rset.getString( 4 ),
					    rset.getString( 5 ),
					    rset.getString( 6 ),
					    rset.getInt( 7 ), 
					    rset.getString( 8 ),
					    rset.getString( 9 ),
					    rset.getString( 10 ),
					    rset.getString( 11 ),
					    rset.getInt( 12 ),
					    rset.getString( 13 ) ) );

	    rset.close( );
	    stmt.close( );

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: getStories( ): sql exception: " + e );
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

	return( vect );

    }

    // new getStories( ) method to replace the one above

    public static Vector getStories( String publication, String date )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rset;
	Vector vect = new Vector( );

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rset = stmt.executeQuery( "select s.id, s.date, s.publishdate, " +
				      "  s.unpublishDate, s.head, s.text, " +
				      "  s.userId, s.state, " +
				      "  s.student, s.staff, s.parent, s.priority, " +
				      "  concat( u.first, ' ', u.last ) " +
				      "from stories s, users u " +
				      "where s.state='published' " +
				      "  and s.publishDate < '" + date + " 23:59:59' " +
				      "  and s.unpublishDate > '" + date + " 23:59:59' " +
				      "  and s." + publication + " = 't' " +
				      "  and s.userId = u.id " +
				      "order by s.priority, s.publishDate desc" );

	    while (rset.next( ))
		vect.addElement( new Story( rset.getInt( 1 ),
					    rset.getString( 2 ),
					    rset.getString( 3 ),
					    rset.getString( 4 ),
					    rset.getString( 5 ),
					    rset.getString( 6 ),
					    rset.getInt( 7 ), 
					    rset.getString( 8 ),
					    rset.getString( 9 ),
					    rset.getString( 10 ),
					    rset.getString( 11 ),
					    rset.getInt( 12 ),
					    rset.getString( 13 ) ) );

	    rset.close( );
	    stmt.close( );

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: getStories( ): sql exception: " + e );
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

	return( vect );

    }

    public static Vector getStoriesBySearchTerm( String publication, String unescapedQuery )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rset;
	Vector vect = new Vector( );
	String query = CommonRoutines.escapeTics( unescapedQuery );

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rset = stmt.executeQuery( "select s.id, s.date, s.publishdate, " +
				      "  s.unpublishDate, s.head, s.text, " +
				      "  s.userId, s.state, " +
				      "  s.student, s.staff, s.parent, s.priority, " +
				      "  concat( u.first, ' ', u.last ) " +
				      "from stories s " +
				      "  left join users u on s.userId = u.id " +
				      "where s.state='published' " +
				      "  and ( s.head like '%" + query + "%' or s.text like '%" + query + "%' ) " +
				      "  and s." + publication + " = 't' " +
				      "order by s.publishDate desc " +
				      "limit 50" );

	    while (rset.next( ))
		vect.addElement( new Story( rset.getInt( 1 ),
					    rset.getString( 2 ),
					    rset.getString( 3 ),
					    rset.getString( 4 ),
					    rset.getString( 5 ),
					    rset.getString( 6 ),
					    rset.getInt( 7 ), 
					    rset.getString( 8 ),
					    rset.getString( 9 ),
					    rset.getString( 10 ),
					    rset.getString( 11 ),
					    rset.getInt( 12 ),
					    rset.getString( 13 ) ) );

	    rset.close( );
	    stmt.close( );

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: getStoriesBySearchTerm( ): sql exception: " + e );
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
	LogDB.addLogLine( 1, "query:[" + unescapedQuery + "] results:[" + vect.size( ) + "]" );

	return( vect );

    }

    public static Vector getPendingStories( )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rset;
	Vector vect = new Vector( );

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rset = stmt.executeQuery( "select s.id, s.date, s.publishdate, " +
				      "  s.unpublishDate, s.head, s.text, " +
				      "  s.userId, s.state, " +
				      "  s.student, s.staff, s.parent, s.priority, " +
				      "  concat( u.first, ' ', u.last ) " +
				      "from stories s, users u " +
				      "where s.state='pending' " +
				      "  and s.userId = u.id " +
				      "order by s.publishDate desc" );

	    while (rset.next( ))
		vect.addElement( new Story( rset.getInt( 1 ),
					    rset.getString( 2 ),
					    rset.getString( 3 ),
					    rset.getString( 4 ),
					    rset.getString( 5 ),
					    rset.getString( 6 ),
					    rset.getInt( 7 ), 
					    rset.getString( 8 ),
					    rset.getString( 9 ),
					    rset.getString( 10 ),
					    rset.getString( 11 ),
					    rset.getInt( 12 ),
					    rset.getString( 13 ) ) );

	    rset.close( );
	    stmt.close( );

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: getPosts( ): sql exception: " + e );
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

	return( vect );

    }


    public static boolean checkPassword( String username, String password )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rs;
	boolean truePassword = false;

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rs   = stmt.executeQuery( "select id from users " +
				      "where username='" + username + "'" +
				      "  and password='" + password + "'" );

	    if ( rs.next( ) )
		truePassword = true;

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

	return ( truePassword );

    }

    public static Vector getPriorities( )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rset;
	Vector vect = new Vector( );

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rset = stmt.executeQuery( "select id, name " +
				      "from priorities " );

	    while ( rset.next( ) )
		vect.addElement( new Priority( rset.getInt( 1 ),
					       rset.getString( 2 ) ) );

	    rset.close( );
	    stmt.close( );

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: getPriorities( ): sql exception: " + e );
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

	return( vect );

    }

    // get me a poll already!

    public static Poll getPoll( int pollId )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rset;
	Poll p = new Poll( );
	Vector ov = new Vector( );
	int totalVotes = 0;

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rset = stmt.executeQuery( "select p.date, p.publishDate, p.unpublishDate, " +
				      "  p.question, p.userId, concat( u.first, ' ', u.last ), " + 
				      "  p.state, p.priorityId, resultsPublic " +
				      "from polls p, users u " +
				      "where p.id=" + pollId + " and " +
				      "  p.userId = u.id" );

	    if ( rset.next( ) ) {
		p.setId( pollId );
		p.setDate( rset.getString( 1 ) );
		p.setPublishDate( rset.getString( 2 ) );
		p.setUnpublishDate( rset.getString( 3 ) );
		p.setQuestion( rset.getString( 4 ) );
		p.setUserId( rset.getInt( 5 ) );
		p.setUsername( rset.getString( 6 ) );
		p.setState( rset.getString( 7 ) );
		p.setPriorityId( rset.getInt( 8 ) );
		p.setResultsPublic( rset.getString( 9 ) );

		rset.close( );
		rset = stmt.executeQuery( "select id, pollId, answer, ordinal, votes " +
					  "from pollOptions " +
					  "where pollId=" + pollId + " order by ordinal" );

		while ( rset.next( ) ) {
		    ov.addElement( new PollOption( rset.getInt( 1 ), 
						   rset.getInt( 2 ),
						   rset.getString( 3 ),
						   rset.getInt( 4 ),
						   rset.getInt( 5 ) ) );
		    totalVotes = totalVotes + rset.getInt( 5 );
		}

		p.setOptions( ov );
		p.setTotalVotes( totalVotes );

	    }

	    rset.close( );
	    stmt.close( );

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: getPoll( pollId ): sql exception: " + e );
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

	return( p );

    }

    public static int getVoteOptionId( int pollId, int userId )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rset;
	int pollOptionId = 0;

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rset = stmt.executeQuery( "select pollOptionId " +
				      "from votes " +
				      "where userId=" + userId + " and " +
				      "  pollId=" + pollId  );

	    if ( rset.next( ) ) {
		pollOptionId = rset.getInt( 1 );

	    }

	    rset.close( );
	    stmt.close( );

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: getVoteOptionId( pollId, userId ): sql exception: " + e );
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

	return( pollOptionId );

    }

    public static void setVoteOptionId( int pollId, int userId, int pollOptionId )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    stmt.executeQuery( "insert into votes " +
			       "values ( " + userId +
			       ", " + pollId + 
			       ", " + pollOptionId + ", now( ) )" );
	    stmt.executeQuery( "update pollOptions set votes = votes + 1 " +
			       "where id = " + pollOptionId );

	    stmt.close( );

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: setVoteOptionId( pollId, userId, optionId ): sql exception: " + e );
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

    public static Vector getLatestPollIds( String edition  )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rset;
	Vector vect = new Vector( );

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rset = stmt.executeQuery( "select id " +
				      "from polls " +
				      "where state = 'published' and " +
				      " now( ) > publishDate and " +
				      " now( ) < unpublishDate " +
				      "order by publishDate desc " );

	    while ( rset.next( ) )
		vect.addElement( rset.getString( 1 ) );

	    rset.close( );
	    stmt.close( );

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: getLatestPollIds( edition ): sql exception: " + e );
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

	return( vect );

    }

    public static Vector getPollIdsByUserId( int userId  )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rset;
	Vector vect = new Vector( );

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rset = stmt.executeQuery( "select id " +
				      "from polls " +
				      "where userId = " + userId + " " +
				      "order by date desc" );

	    while ( rset.next( ) )
		vect.addElement( rset.getString( 1 ) );

	    rset.close( );
	    stmt.close( );

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: getPollIdsByUserId( userId ): sql exception: " + e );
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

	return( vect );

    }

    public static Vector getPollAdminIds( )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	ResultSet rset;
	Vector vect = new Vector( );

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    rset = stmt.executeQuery( "select id " +
				      "from polls " +
				      "order by date desc" );

	    while ( rset.next( ) )
		vect.addElement( rset.getString( 1 ) );

	    rset.close( );
	    stmt.close( );

	}
	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: getPollAdminIds( ): sql exception: " + e );
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

	return( vect );

    }

    public static void insertPoll( Poll p )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	int id = 0;

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );

	    stmt.executeUpdate( "insert into polls " +
				" ( date, publishDate, unpublishDate, question, " + 
				"   userId, state, priorityId, resultsPublic ) " +
				"values " +
				" ( now( ), '" + p.getPublishDate( ) + "', '" +
				p.getUnpublishDate( ) + "', '" +
				CommonRoutines.escapeTics( p.getQuestion( ) ) + "', " +
				p.getUserId( ) + ", '" +
				p.getState( ) + "', " +
				p.getPriorityId( ) + ", '" +
				p.getResultsPublic( ) + "' )" );

	    id = (int) ( (org.gjt.mm.mysql.Statement) stmt ).getLastInsertID( );
	    stmt.close( );

	    for ( int x = 0; x < p.getOptions( ).size( ); x ++ ) {
		PollOption po = (PollOption) p.getOptions( ).elementAt( x );
		stmt = conn.createStatement( );
		stmt.executeQuery( "insert into pollOptions " +
				   " ( pollId, answer, ordinal, votes ) " +
				   "values " +
				   " ( " + id + ", '" + CommonRoutines.escapeTics( po.getAnswer( ) ) + "', " +
				   x + ", 0 )" );
		stmt.close( );
	    }

	}

	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: insertPoll( ): sql exception: " + e );
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

    public static void updatePoll( Poll p )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );

	    stmt.executeQuery( "update polls set " +
			       " publishDate = '" + p.getPublishDate( ) + "', " +
			       " unpublishDate = '" + p.getUnpublishDate( ) + "', " +
			       " question = '" + CommonRoutines.escapeTics( p.getQuestion( ) ) + "', " +
			       " userId = " + p.getUserId( ) + ", " +
			       " state = '" + p.getState( ) + "', " +
			       " priorityId = " + p.getPriorityId( ) + ", " +
			       " resultsPublic = '" + p.getResultsPublic( ) + "' " +
			       "where id = " + p.getId( ) );

	    stmt.executeQuery( "delete from pollOptions where pollId = " + p.getId( ) );

	    for ( int x = 0; x < p.getOptions( ).size( ); x ++ ) {
		PollOption po = (PollOption) p.getOptions( ).elementAt( x );
		stmt.executeQuery( "insert into pollOptions " +
				   " ( pollId, answer, ordinal, votes ) " +
				   "values " +
				   " ( " + p.getId( ) + ", '" + po.getAnswer( ) + "', " +
				   x + ", 0 )" );
	    }

	    stmt.close( );

	}

	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: updatePoll( ): sql exception: " + e );
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

    public static void setPollState( int pollId, String state )
    {
	DBPool pool;
	Connection conn = null;
	Statement stmt;

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );

	    stmt.executeQuery( "update polls set " +
			       " state = '" + state + "' " +
			       "where id = " + pollId );

	    stmt.close( );

	}

	catch (SQLException e) {
	    System.err.println( "DBRoutines.java: setPollState( pollId, state ): sql exception: " + e );
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

}
