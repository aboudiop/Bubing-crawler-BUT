package it.unimi.di.law.warc.records;

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

// RELEASE-STATUS: DIST

import it.unimi.di.law.warc.io.WarcFormatException;
import it.unimi.di.law.warc.util.BoundSessionInputBuffer;
import it.unimi.di.law.warc.util.ByteArraySessionOutputBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.impl.io.DefaultHttpRequestParser;
import org.apache.http.impl.io.DefaultHttpRequestWriter;
import org.apache.http.message.HeaderGroup;

/** An implementation of {@link WarcRecord} corresponding to a {@link WarcRecord.Type#REQUEST} record type. */
public class HttpRequestWarcRecord extends AbstractWarcRecord implements HttpRequest {

	public static final String HTTP_REQUEST_MSGTYPE = "application/http;msgtype=request";
	
	private final ProtocolVersion protocolVersion;
	private final RequestLine requestLine;

	public HttpRequestWarcRecord( final URI targetURI, final HttpRequest request ) {
		this( null, targetURI, request );
	}

	public static HttpRequestWarcRecord fromPayload( final HeaderGroup warcHeaders, final BoundSessionInputBuffer payloadBuffer ) throws IOException {
		return new HttpRequestWarcRecord( warcHeaders, null, readPayload( payloadBuffer ) );
	}

	private HttpRequestWarcRecord( final HeaderGroup warcHeaders, final URI targetURI, final HttpRequest request ) {
		super( targetURI, warcHeaders );
		getWarcTargetURI(); // Check correct initialization 
		this.warcHeaders.updateHeader( Type.warcHeader( Type.REQUEST ) );
		this.warcHeaders.updateHeader( new WarcHeader( WarcHeader.Name.CONTENT_TYPE, HTTP_REQUEST_MSGTYPE ) );
		this.protocolVersion = request.getProtocolVersion();
		this.requestLine = request.getRequestLine();
		this.setHeaders( request.getAllHeaders() );
	}

	private static HttpRequest readPayload( final BoundSessionInputBuffer buffer ) throws IOException {
		DefaultHttpRequestParser requestParser = new DefaultHttpRequestParser( buffer );
		try {
			return requestParser.parse();
		} catch ( HttpException e ) {
			throw new WarcFormatException( "Can't parse the request", e );
		}
	}

	@Override
	public ProtocolVersion getProtocolVersion() {
		return this.protocolVersion;
	}

	@Override
	public RequestLine getRequestLine() {
		return requestLine;
	}

	@Override
	protected InputStream writePayload( ByteArraySessionOutputBuffer buffer ) throws IOException {
		DefaultHttpRequestWriter pw = new DefaultHttpRequestWriter( buffer );
		try {
			pw.write( this );
		} catch ( HttpException e ) {
			throw new RuntimeException( "Unexpected HtthException", e );
		}
		buffer.contentLength( buffer.size() );
		return buffer.toInputStream();
	}

	@Override
	public String toString() {
		return
			"Warc headers: " + Arrays.toString( warcHeaders.getAllHeaders() ) +
			"\nRequest line: " + this.requestLine +
			"\nRequest headers: " + Arrays.toString( this.getAllHeaders() );
	}

}
