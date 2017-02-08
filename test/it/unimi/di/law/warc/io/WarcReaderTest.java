package it.unimi.di.law.warc.io;

/*		 
 * Copyright (C) 2013 Paolo Boldi, Massimo Santini, and Sebastiano Vigna 
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

//RELEASE-STATUS: DIST

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import it.unimi.di.law.TestUtil;
import it.unimi.di.law.warc.records.HttpRequestWarcRecord;
import it.unimi.di.law.warc.records.HttpResponseWarcRecord;
import it.unimi.di.law.warc.records.InfoWarcRecord;
import it.unimi.di.law.warc.records.InputStreamTestMocks;
import it.unimi.di.law.warc.records.WarcRecord;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.io.SessionInputBuffer;
import org.junit.Test;

public class WarcReaderTest {

	@Test
	public void testSingleInfo() throws IOException {
		String path = TestUtil.getTestFile( WarcReader.class, "warcinfo.warc", false );
		WarcReader wr = new UncompressedWarcReader( new FileInputStream( path ) );
		InfoWarcRecord actual = (InfoWarcRecord) wr.read();
		InputStreamTestMocks.InfoWarcRecord expected = new InputStreamTestMocks.InfoWarcRecord( path );
		assertEquals( InputStreamTestMocks.EMPTY_DIFFS, expected.infoDiffs( actual ) );
		assertEquals( InputStreamTestMocks.EMPTY_DIFFS, expected.warcHeadersDiffs( actual ) );
		assertEquals( actual.getWarcDate(), expected.getDate() );
		assertEquals( actual.getWarcRecordId(), expected.getUUID() );
	}

	@Test
	public void testSingleRequest() throws IOException {
		String path = TestUtil.getTestFile( WarcReader.class, "request.warc", false );
		WarcReader wr = new UncompressedWarcReader( new FileInputStream( path ) );
		HttpRequestWarcRecord actual = (HttpRequestWarcRecord) wr.read();
		InputStreamTestMocks.HttpRequestWarcRecord expected = new InputStreamTestMocks.HttpRequestWarcRecord( path );
		assertEquals( InputStreamTestMocks.EMPTY_DIFFS, expected.headerDiffs( actual ) );
		assertEquals( InputStreamTestMocks.EMPTY_DIFFS, expected.warcHeadersDiffs( actual ) );
		assertEquals( actual.getWarcDate(), expected.getDate() );
		assertEquals( actual.getWarcRecordId(), expected.getUUID() );
	}

	@Test
	public void testSingleResponse() throws IOException {
		String path = TestUtil.getTestFile( WarcReader.class, "response.warc", false );
		WarcReader wr = new UncompressedWarcReader( new FileInputStream( path ) );
		HttpResponseWarcRecord actual = (HttpResponseWarcRecord) wr.read();
		InputStreamTestMocks.HttpResponseWarcRecord expected = new InputStreamTestMocks.HttpResponseWarcRecord( path );
		assertEquals( InputStreamTestMocks.EMPTY_DIFFS, expected.headerDiffs( actual ) );
		assertEquals( InputStreamTestMocks.EMPTY_DIFFS, expected.warcHeadersDiffs( actual ) );
		assertTrue( expected.entityEquals( actual ) );
		assertEquals( actual.getWarcDate(), expected.getDate() );
		assertEquals( actual.getWarcRecordId(), expected.getUUID() );
	}

	public static void assertMockEquals( String expectedPath, List<WarcRecord> actuals ) throws IOException {
		SessionInputBuffer buffer = InputStreamTestMocks.warpSessionInputBuffer( new FileInputStream( expectedPath ) );
		List<InputStreamTestMocks.WarcRecord> expected = new ArrayList<InputStreamTestMocks.WarcRecord>();

		expected.add( new InputStreamTestMocks.InfoWarcRecord( buffer ) );
		buffer.readLine();
		buffer.readLine();
		try {
			for ( ;; ) {
				expected.add( new InputStreamTestMocks.HttpRequestWarcRecord( buffer ) );
				buffer.readLine();
				buffer.readLine();
				expected.add( new InputStreamTestMocks.HttpResponseWarcRecord( buffer ) );
				buffer.readLine();
				buffer.readLine();
			}
		} catch ( EOFException e ) {}

		Iterator<InputStreamTestMocks.WarcRecord> ei = expected.iterator();
		Iterator<WarcRecord> ai = actuals.iterator();
		while( ei.hasNext() && ai.hasNext() ) {
			WarcRecord actual = ai.next();
			if ( actual instanceof InfoWarcRecord ) {
				InputStreamTestMocks.InfoWarcRecord m = (InputStreamTestMocks.InfoWarcRecord) ei.next();
				assertEquals( InputStreamTestMocks.EMPTY_DIFFS, m.infoDiffs( (InfoWarcRecord)actual ) );
				assertEquals( actual.getWarcDate(), m.getDate() );
				assertEquals( actual.getWarcRecordId(), m.getUUID() );
			} else if ( actual instanceof HttpRequestWarcRecord ) {
				InputStreamTestMocks.HttpRequestWarcRecord m = (InputStreamTestMocks.HttpRequestWarcRecord) ei.next();
				assertEquals( InputStreamTestMocks.EMPTY_DIFFS, m.headerDiffs( (HttpRequestWarcRecord)actual ) );
				assertEquals( actual.getWarcDate(), m.getDate() );
				assertEquals( actual.getWarcRecordId(), m.getUUID() );
			} else if ( actual instanceof HttpResponseWarcRecord ) {
				InputStreamTestMocks.HttpResponseWarcRecord m = (InputStreamTestMocks.HttpResponseWarcRecord) ei.next();
				assertEquals( InputStreamTestMocks.EMPTY_DIFFS, m.headerDiffs( (HttpResponseWarcRecord)actual ) );
				assertEquals( actual.getWarcDate(), m.getDate() );
				assertEquals( actual.getWarcRecordId(), m.getUUID() );
			}
		}
		assertEquals( Boolean.valueOf( ei.hasNext() ), Boolean.valueOf( ai.hasNext() ) );
	}

	@Test
	public void testMultiple() throws IOException {
		String path = TestUtil.getTestFile( WarcReader.class, "misc.warc", false );

		WarcReader wr = new UncompressedWarcReader( new FileInputStream( path ) );
		List<WarcRecord> actual = new ArrayList<WarcRecord>();
		for ( ;; ) {
			WarcRecord ret = wr.read();
			if ( ret == null ) break;
			actual.add( ret );
		}

		assertMockEquals( path, actual );
	}

}
