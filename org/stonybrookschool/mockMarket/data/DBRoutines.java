package org.stonybrookschool.mockMarket.data;

import java.io.*;
import java.sql.*;
import java.util.*;

import com.caucho.sql.DBPool;

import org.stonybrookschool.mockMarket.objects.*;

/* database contains all the methods used for database access */

public class DBRoutines
{

    protected static String poolString = "jdbc/mockMarket";

    public static boolean checkPassword( String username, String password )
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
	    rs   = stmt.executeQuery( "select password from users " +
				      "where username='" + username + "'" );

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
            rs   = stmt.executeQuery( "select id, first, last, " +
				      " date_format( date, '%W, %M %e, %Y %l:%i %p'), " +
				      " email, superuser, " +
				      " cash, worth, date_format( worthDate, '%W, %M %e, %Y %l:%i %p') " +
				      "from users " +
                                      "where username='" + username + "'" );

            if ( rs.next( ) ) {
		u.setId( rs.getInt( 1 ) );
		u.setUsername( username );
		u.setFirst( rs.getString( 2 ) );
		u.setLast( rs.getString( 3 ) );
		u.setDate( rs.getString( 4 ).toLowerCase( ) );
		u.setEmail( rs.getString( 5 ) );
		u.setSuperuser( rs.getString( 6 ) );
		u.setCash( rs.getFloat( 7 ) );
		u.setWorth( rs.getFloat( 8 ) );
		u.setWorthDate( rs.getString( 9 ) );
	    }

            rs.close();
            stmt.close();

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

    public static Vector getPortfolio( int userId )
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
            rs   = stmt.executeQuery( "select s.id, s.symbol, s.company, " + 
				      " date_format( time, '%W, %M %e, %Y %l:%i %p'), " +
				      " s.price, s.pointChange, p.quantity " +
				      "from symbols s, portfolios p " +
				      "where p.userId = " + userId + " and " +
				      " s.id = p.symbolId" );

	    while ( rs.next( ) ) {
		pv.addElement( new PortfolioItem( rs.getInt( 1 ),
						  rs.getString( 2 ),
						  rs.getString( 3 ),
						  rs.getString( 4 ),
						  rs.getFloat( 5 ),
						  rs.getFloat( 6 ),
						  rs.getFloat( 7 ) ) );
	    }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getPortfolio( userId ): sql exception: " + e );
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

    public static Vector getOrders( int userId )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
	Vector ov = new Vector( );

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select id, date_format( time, '%W, %M %e, %Y %l:%i %p'), " +
				      " userId, action, symbol, status, quantity, " +
				      " date_format( transactionTime, '%W, %M %e, %Y %l:%i %p'), " +
				      " price, comments " +
				      "from orders " +
				      "where userId = " + userId +  " " + 
				      // " time > now( ) - 1209600 " + // 2 weeks old
				      "order by time desc" );

	    while ( rs.next( ) ) {
		ov.addElement( new Order( rs.getInt( 1 ),
					  rs.getString( 2 ),
					  rs.getInt( 3 ),
					  rs.getString( 4 ),
					  rs.getString( 5 ),
					  rs.getString( 6 ),
					  rs.getFloat( 7 ),
					  rs.getString( 8 ),
					  rs.getFloat( 9 ),
					  rs.getString( 10 ) ) );
	    }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getOrders( userId ): sql exception: " + e );
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
         
	return ( ov );

    }

    public static void insertOrder( Order o )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            stmt.executeUpdate( "insert into orders values ( null, now(), " +
				o.getUserId( ) + ", '" + o.getAction( ) + "', '" +
				o.getSymbol( ) + "', " + o.getQuantity( ) + 
				", 0.0, null, 'open', 'user submitted' )" );
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: insertOrder( ): sql exception: " + e );
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

    public static void cancelOrder( int userId, int orderId )
    {
        DBPool pool;
        Connection conn = null;
        Statement stmt;
	ResultSet rs;

        try {
            pool = DBPool.getPool( poolString );
            conn = pool.getConnection( );
            stmt = conn.createStatement( );
            rs   = stmt.executeQuery( "select status " +
				      "from orders " +
				      "where userId = " + userId + " and " +
				      " id = " + orderId );
	    if ( rs.next( ) && rs.getString( 1 ).equals( "open" ) ) {
		    stmt.executeUpdate( "update orders " +
					"set status = 'canceled', " +
					" comments = 'canceled before completion' " +
					"where id = " + orderId );
	    }
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: cancelOrder( ): sql exception: " + e );
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

    public static Vector getStandings( int limit )
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
            rs   = stmt.executeQuery( "select id, first, last, date, username, " +
				      " email, superuser, cash, worth, worthDate " + 
				      "from users " +
				      "where cash != 10000 and " +
				      " worth != 10000 " +
				      "order by worth desc " +
				      "limit " + limit );

	    while ( rs.next( ) ) {
		uv.addElement( new User( rs.getInt( 1 ),
					 rs.getString( 2 ),
					 rs.getString( 3 ),
					 rs.getString( 4 ),
					 rs.getString( 5 ),
					 rs.getString( 6 ),
					 rs.getString( 7 ),
					 rs.getFloat( 8 ),
					 rs.getFloat( 9 ),
					 rs.getString( 10 ) ) );
	    }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getStandings( limit ): sql exception: " + e );
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
         
	return( uv );

    }

    public static Vector getStandings( String group, int limit )
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
            rs   = stmt.executeQuery( "select u.id, u.first, u.last, u.date, u.username, " +
				      " u.email, u.superuser, u.cash, u.worth, u.worthDate " + 
				      "from users u, usersGroups ug, groups g " +
				      "where g.name = '" + group + "' and " +
				      " ug.groupId = g.id and " +
				      " ug.userId = u.id and " +
				      " u.cash != 10000 and " +
				      " u.worth != 10000 " +
				      "order by u.worth desc " +
				      "limit " + limit );

	    while ( rs.next( ) ) {
		uv.addElement( new User( rs.getInt( 1 ),
					 rs.getString( 2 ),
					 rs.getString( 3 ),
					 rs.getString( 4 ),
					 rs.getString( 5 ),
					 rs.getString( 6 ),
					 rs.getString( 7 ),
					 rs.getFloat( 8 ),
					 rs.getFloat( 9 ),
					 rs.getString( 10 ) ) );
	    }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getStandings( group, limit ): sql exception: " + e );
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
         
	return( uv );

    }

    public static Vector getStandings( int groupId, int limit )
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
            rs   = stmt.executeQuery( "select u.id, u.first, u.last, u.date, u.username, " +
				      " u.email, u.superuser, u.cash, u.worth, u.worthDate " + 
				      "from users u, usersGroups ug " +
				      "where ug.groupId = " + groupId + " and " +
				      " ug.userId = u.id and " +
				      " u.cash != 10000 and " +
				      " u.worth != 10000 " +
				      "order by u.worth desc " +
				      "limit " + limit );

	    while ( rs.next( ) ) {
		uv.addElement( new User( rs.getInt( 1 ),
					 rs.getString( 2 ),
					 rs.getString( 3 ),
					 rs.getString( 4 ),
					 rs.getString( 5 ),
					 rs.getString( 6 ),
					 rs.getString( 7 ),
					 rs.getFloat( 8 ),
					 rs.getFloat( 9 ),
					 rs.getString( 10 ) ) );
	    }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getStandings( groupId, limit ): sql exception: " + e );
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
         
	return( uv );

    }

    public static Vector getGroups(  )
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
            rs   = stmt.executeQuery( "select id, name, ordinal " +
				      "from groups " +
				      "order by ordinal" );
	    while ( rs.next( ) ) {
		gv.addElement( new Group( rs.getInt( 1 ),
					  rs.getString( 2 ),
					  rs.getInt( 3 ) ) );
	    }

            rs.close();
            stmt.close();

        }
        catch (SQLException e) {
            System.err.println( "DBRoutines.java: getGroup( ): sql exception: " + e );
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
         
	return( gv );

    }

}
