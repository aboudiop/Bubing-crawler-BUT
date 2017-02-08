package it.unimi.di.law.warc.filters;

/*		 
 * Copyright (C) 2004-2013 Paolo Boldi, Massimo Santini, and Sebastiano Vigna 
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

// RELEASE-STATUS: DIST

/** A filter accepting only http responses whose content stream (in ISO-8859-1 encoding) matches a regular expression. */
public class ResponseMatches extends AbstractFilter<HttpResponse> {
	
	private final Pattern pattern;

	public ResponseMatches( final Pattern pattern ) {
		this.pattern = pattern;
	}
	
	/** Checks whether the response associated with this page matches (in ISO-8859-1 encoding)
	 * the regular expression provided at construction time.
	 * 
	 * @return <code>true</code> if the response associated with this page matches (in ISO-8859-1 encoding)
	 * the regular expression provided at construction time.
	 * @throws NullPointerException if the page has no byte content. 
	 */ 
	public boolean apply( final HttpResponse httpResponse ) {
		try {
			final InputStream content = httpResponse.getEntity().getContent();
			return pattern.matcher( IOUtils.toString( content, Charsets.ISO_8859_1 ) ).matches();
		}
		catch( IOException shouldntReallyHappen ) {
			throw new RuntimeException( shouldntReallyHappen );
		}
	}

	/**
	 * Get a new content matcher that will accept only responses whose content stream matches the regular expression.
	 * 
	 * @param spec a {@link java.util.regex} regular expression.
	 * @return a new content matcher that will accept only responses whose content stream matches the regular expression. 
	 */
	public static ResponseMatches valueOf( final String spec ) {
		return new ResponseMatches( Pattern.compile( spec ) );
	}
	
	/**
	 * A string representation of the state of this filter.
	 * 
	 * @return the class name of this + "()"
	 */
	public String toString() {
		return getClass().getSimpleName() + "(" + pattern.toString() + ")";
	}

	@Override
	public Filter<HttpResponse> copy() {
		return new ResponseMatches( pattern );
	}
}
