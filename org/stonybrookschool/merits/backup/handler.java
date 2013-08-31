package org.stonybrookschool.merits;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.caucho.jdbc.mysql.Driver;

import org.stonybrookschool.merits.objects.*;


/* Handler is the traffic cop for the merits system. It handles
 * all get and post requests farming out all real logic to other
 * classes in the system. Handler is the main servlet in the system.
 */

public class handler extends HttpServlet
{
    private boolean debug = true;

    public void doGet (HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        handle(req, res);
    }

    public void doPost (HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        handle(req, res);
    }

    public void handle (HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        String template;
	String error;
        Vector vect = new Vector( );

        try {
	    Class.forName( "com.caucho.jdbc.mysql.Driver" );

	    if ( debug ) System.err.println( "handler: connection" );
            Connection con = DriverManager.getConnection( "jdbc:mysql-caucho://localhost:3306/merits", "merits", "sbm3rits" );

	    if ( debug ) System.err.println( "handler: create statement" );
            Statement stmt = con.createStatement( );

	    if ( debug ) System.err.println( "handler: execute query" );
	    ResultSet rset = stmt.executeQuery( "select name from test order by name" );

            while (rset.next ())
		vect.addElement( new String( rset.getString( 1 ) ) );

	    rset.close( );
            stmt.close( );

        }
	catch (ClassNotFoundException cnfe) {
	    System.err.println("couldn't load database driver: " + cnfe.getMessage());
	}
        catch ( SQLException sqle ) {
            System.err.println("handler: sql exception: "+ sqle);
        }

	req.setAttribute( "vect", vect );

	if ( req.getSession().getAttribute( "userId" ) != null )
	    error = "have a session";
	else
	    error = "don't have a session";

        req.setAttribute( "error", error );

	if ( req.getParameter( "template" ) != null )
	    template = req.getParameter( "template" );
	else
	    template = "/merits/templates/handler.jsp";

	// execute the jsp page
	RequestDispatcher rd = req.getRequestDispatcher( template );

	if ( rd == null ) {
	    rd = req.getRequestDispatcher( "/error.jsp" );

	    if (rd == null) {
		System.err.println( "handler: default error template not found!" );
		throw new ServletException( "default error template not found!" );
	    }
	}

	rd.include( req, res );

    }

}
