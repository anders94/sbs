package org.stonybrookschool.dailyBulletin.data;

import java.io.*;
import java.sql.*;
import java.util.*;

import com.caucho.sql.DBPool;

import org.stonybrookschool.dailyBulletin.*;
import org.stonybrookschool.dailyBulletin.objects.*;

public class LogDB {
    protected static String poolString = "jdbc/dailyBulletin";

    public static void addLogLine( int logStepId, String unescapedLogData ) {
	DBPool pool;
	Connection conn = null;
	Statement stmt;
	String logData = CommonRoutines.escapeTics( unescapedLogData );

	try {
	    pool = DBPool.getPool( poolString );
	    conn = pool.getConnection( );
	    stmt = conn.createStatement( );
	    stmt.executeUpdate( "insert into logs ( logStepId, dateCreated, logData ) values ( " + logStepId + ", now( ), '" + logData + "' )" );
	    stmt.close( );

	}
	catch (SQLException e) {
	    System.err.println( "LogDB.java: addLogLine( ): sql exception: " + e );
	}
	finally {
	    try {
		if ( conn != null ) {
		    conn.close( );
		}
	    }
	    catch ( SQLException e ) {
	    }
	}

    }

}
