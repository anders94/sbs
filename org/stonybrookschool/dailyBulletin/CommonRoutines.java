package org.stonybrookschool.dailyBulletin;

import java.io.*;
import java.util.*;
import java.text.*;

public class CommonRoutines
{

    public static String getEdition( String machineIp )
    {
        String edition = "parent";
	StringTokenizer st = new StringTokenizer( machineIp, "." );
	if ( st.countTokens( ) == 4 ) {
	    int a = Integer.parseInt( st.nextToken( ) );
	    int b = Integer.parseInt( st.nextToken( ) );
	    int c = Integer.parseInt( st.nextToken( ) );
	    int d = Integer.parseInt( st.nextToken( ) );
	    
	    if ( a == 10 ) {
		if ( ( b == 1 ) || ( b == 2 ) ) {
		    edition = "staff";
		}
	    }
	    else {
		if ( ( a == 192 ) && ( b == 168 ) ) {
		    if ( c < 99 ) {
			edition = "student";
		    }
		    else {
			edition = "staff";
		    }
		}
		else {
		    if ( ( a == 207 ) && ( b == 10 ) && ( c == 128 ) ) {
			if ( ( d > 64 ) && ( d < 95 ) ) {
			    edition = "staff";
			}
		    }
		}
	    }
	}
	else {
	    edition = "bad ip";
	}

	return ( edition );

    }

    public static String escapeHTML( String html )
    {
	html = replace( html, '<', "&lt;" );
	html = replace( html, '>', "&gt;" );
	while ( html.lastIndexOf( "\n" ) == html.length( ) - 1 ||
		html.lastIndexOf( "\r" ) == html.length( ) - 1 ||
		html.lastIndexOf( "\t" ) == html.length( ) - 1 ||
		html.lastIndexOf( " " ) == html.length( ) - 1 ) {
	    html = html.substring( 0, html.length( ) - 1 );

	}
	html = replace( html, '\n', "<br>\n" );

	return( html );
    }

    public static String unescapeHTML( String html )
    {
	html = replace( html, "<br>\n", "\n" );

	return( html );
    }

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

}





