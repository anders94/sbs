package org.stonybrookschool.mockMarket;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.stonybrookschool.mockMarket.data.*;
import org.stonybrookschool.mockMarket.objects.*;


/* Handler is the traffic cop for the mockMarket system. It handles
 * all get and post requests farming out all real logic to other
 * classes in the system. Handler is the only servlet in the system.
 */

public class handler extends HttpServlet
{
    private boolean debug       = true;
    private String templateBase = "/mockMarket/templates/";

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
        String template = null;

	res.setContentType( "text/html" );

	if ( req.getParameter( "page" ) == null ) {
	    template = doLogin( req, res );
	}
	else {
	    if ( req.getParameter( "page" ).equals( "processLogin" ) )
		template = doProcessLogin( req, res );
	    else {
		User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.mockMarket.objects.user" );
		if ( u != null ) { // if the session contains a user object (ie: is logged in)
		    if ( req.getParameter( "page" ).equals( "login" ) )
			template = doLogin( req, res );
		    if ( req.getParameter( "page" ).equals( "processLogin" ) )
			template = doProcessLogin( req, res );
		    if ( req.getParameter( "page" ).equals( "portfolio" ) )
			template = doPortfolio( req, res );
		    if ( req.getParameter( "page" ).equals( "trade" ) )
                        template = doTrade( req, res );
		    if ( req.getParameter( "page" ).equals( "tradeConfirm" ) )
                        template = doTradeConfirm( req, res );
		    if ( req.getParameter( "page" ).equals( "orders" ) )
                        template = doOrders( req, res );
		    if ( req.getParameter( "page" ).equals( "cancel" ) )
                        template = doCancel( req, res );
		    if ( req.getParameter( "page" ).equals( "logout" ) )
			template = doLogout( req, res );
		}
		else {
		    System.err.println( "\nhandler.java: invalid session\n" );
		    template = doLogin( req, res );
		}
	    }
	}

	// execute the jsp page
	RequestDispatcher rd = req.getRequestDispatcher( template );

	if ( rd == null ) {
	    rd = req.getRequestDispatcher( templateBase + "error.jsp" );

	    if (rd == null) {
		System.err.println( "\nhandler.java: no default error template!\n" );
		throw new ServletException( "default error template not found!" );
	    }
	}

	rd.include( req, res );

    }

    private String doLogin( HttpServletRequest req, HttpServletResponse res )
    {
	Vector uv = DBRoutines.getStandings( 10 );
	req.setAttribute( "userStandingsVector", uv );

	String template = templateBase + "login.jsp";

	return( template );
    }

    private String doProcessLogin( HttpServletRequest req, HttpServletResponse res )
    {
	String username = "";
	String password = "";
	String template;

	if ( req.getParameter( "username" ) != null )
	    username = req.getParameter( "username" );
	if ( req.getParameter( "password" ) != null )
	    password = req.getParameter( "password" );

	if ( DBRoutines.checkPassword( username, password ) ) {
	    User u = DBRoutines.getUser( username );
	    req.getSession( ).setAttribute( "org.stonybrookschool.mockMarket.objects.user", u );

	    //	    if ( "t".equals( u.getFaculty( ) ) )
	    //	template = doAdminTools( req, res );
	    //else

	    template = doPortfolio( req, res );
	}
	else {
	    req.getSession( ).setAttribute( "org.stonybrookschool.mockMarket.objects.user", null );
	    req.setAttribute( "error", "incorrect username or password" );
	    template = doLogin( req, res );
	}

	return( template );
    }

    private String doPortfolio( HttpServletRequest req, HttpServletResponse res )
    {
        String template = templateBase + "portfolio.jsp";
	User u1 = (User) req.getSession( ).getAttribute( "org.stonybrookschool.mockMarket.objects.user" );
	User u2 = DBRoutines.getUser( u1.getUsername( ) );
	req.getSession( ).setAttribute( "org.stonybrookschool.mockMarket.objects.user", u2 );
	Vector pv = DBRoutines.getPortfolio( u2.getId( ) );

	req.setAttribute( "portfolioVector", pv );
        return( template );
    }

    private String doTrade( HttpServletRequest req, HttpServletResponse res )
    {
	// update user object incase cash account has changed
	User u1 = (User) req.getSession( ).getAttribute( "org.stonybrookschool.mockMarket.objects.user" );
	User u2 = DBRoutines.getUser( u1.getUsername( ) );
	req.getSession( ).setAttribute( "org.stonybrookschool.mockMarket.objects.user", u2 );

	String template = templateBase + "trade.jsp";
	return( template );
    }

    private String doTradeConfirm( HttpServletRequest req, HttpServletResponse res )
    {
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.mockMarket.objects.user" );
	Order o = new Order( );
	Float quantity = new Float( req.getParameter( "quantity" ) );

	o.setUserId( u.getId( ) );
	o.setAction( req.getParameter( "action" ) );
	o.setSymbol( req.getParameter( "symbol" ) );
	o.setStatus( "open" );
	o.setQuantity( quantity.floatValue( ) );

	DBRoutines.insertOrder( o );

	String template = templateBase + "tradeConfirm.jsp";
	return( template );
    }

    private String doOrders( HttpServletRequest req, HttpServletResponse res )
    {
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.mockMarket.objects.user" );
	Vector ov = DBRoutines.getOrders( u.getId( ) );
	req.setAttribute( "ordersVector", ov );

	String template = templateBase + "orders.jsp";
	return( template );
    }

    private String doCancel( HttpServletRequest req, HttpServletResponse res )
    {
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.mockMarket.objects.user" );
	DBRoutines.cancelOrder( u.getId( ), Integer.parseInt( req.getParameter( "id" ) ) );

	return( doOrders( req, res ) );
    }

    private String doLogout( HttpServletRequest req, HttpServletResponse res )
    {
	req.getSession( ).setAttribute( "org.stonybrookschool.mockMarket.objects.user", null );
	req.setAttribute( "error", "you have successfully logged out" );

	return( doLogin( req, res ) );
    }

}
