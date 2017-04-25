package it.unimi.di.law.warc.io.gzarc;

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

import static org.junit.Assert.assertArrayEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import it.unimi.di.law.bubing.util.Util;
import it.unimi.di.law.warc.io.gzarc.GZIPArchive.ReadEntry.LazyInflater;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import it.unimi.dsi.fastutil.longs.LongBigArrayBigList;

public class GZIPArchiveReaderTest {

    public static final String ARCHIVE_PATH = "/tmp/archive-random.gz";
    public static final int ARCHIVE_SIZE = 10;
    public static final byte[] EXPECTED_MAGIC = Util.toByteArray("MAGIC");

    @BeforeClass
    public static void setUp() throws IOException {
	GZIPArchiveWriterTest.writeArchive(ARCHIVE_PATH, ARCHIVE_SIZE, true);
    }

    @Ignore("Let's concentrate on cached")
    @Test
    public void reverseGetEntry() throws IOException {
	final LongBigArrayBigList pos = GZIPIndexer.index(new FileInputStream(ARCHIVE_PATH));
	GZIPArchive.ReadEntry re;
	FastBufferedInputStream fis = new FastBufferedInputStream(new FileInputStream(ARCHIVE_PATH));
	GZIPArchiveReader gzar = new GZIPArchiveReader(fis);
	byte[] actualMagic = new byte[EXPECTED_MAGIC.length];
	for (int i = (int) pos.size64() - 1; i >= 0; i--) {
	    gzar.position(pos.getLong(i));
	    re = gzar.getEntry();
	    if (re == null) break;
	    LazyInflater lin = re.lazyInflater;
	    InputStream in = lin.get();
	    in.read(actualMagic);
	    assertArrayEquals(EXPECTED_MAGIC, actualMagic);
	    for (int j = 0; j < (i + 1) * 512; j++) in.read();
	    lin.consume();
	    
	}
	fis.close();
    }

    @Ignore("Let's concentrate on cached")
    @Test
    public void partialRead() throws IOException {
	FileInputStream fis = new FileInputStream(ARCHIVE_PATH);
	GZIPArchiveReader gzar = new GZIPArchiveReader(fis);
	byte[] actualMagic = new byte[EXPECTED_MAGIC.length];
	for (int i = 0; i < ARCHIVE_SIZE + 1; i++) {
	    GZIPArchive.ReadEntry re = gzar.getEntry();
	    if (re == null) break;
	    LazyInflater lin = re.lazyInflater;
	    InputStream in = lin.get();
	    in.read(actualMagic);
	    assertArrayEquals(EXPECTED_MAGIC, actualMagic);
	    for (int j = 0; j < (i + 1) * 512; j++) in.read();
	    lin.consume();
	}
	fis.close();
    }

    @Ignore("Let's concentrate on cached")
    @Test
    public void fullRead() throws IOException {
	FileInputStream fis = new FileInputStream(ARCHIVE_PATH);
	GZIPArchiveReader gzar = new GZIPArchiveReader(fis);
	byte[] actualMagic = new byte[EXPECTED_MAGIC.length];
	fis = new FileInputStream(ARCHIVE_PATH);
	gzar = new GZIPArchiveReader(fis);
	for (int i = 0; i < ARCHIVE_SIZE + 1; i++) {
	    GZIPArchive.ReadEntry re = gzar.getEntry();
	    if (re == null) break;
	    LazyInflater lin = re.lazyInflater;
	    InputStream in = lin.get();
	    in.read(actualMagic);
	    assertArrayEquals(EXPECTED_MAGIC, actualMagic);
	    while (in.read() != -1);
	    lin.consume();
	}
	fis.close();
    }
    
    @Ignore("Let's concentrate on cached")
    @Test
    public void skip() throws IOException {
	FileInputStream fis = new FileInputStream(ARCHIVE_PATH);
	GZIPArchiveReader gzar = new GZIPArchiveReader(fis);
	byte[] actualMagic = new byte[EXPECTED_MAGIC.length];
	fis = new FileInputStream(ARCHIVE_PATH);
	gzar = new GZIPArchiveReader(fis);
	for (int i = 0; i < ARCHIVE_SIZE + 1; i++) {
	    GZIPArchive.ReadEntry re = gzar.getEntry();
	    if (re == null) break;
	    LazyInflater lin = re.lazyInflater;
	    InputStream in = lin.get();
	    in.read(actualMagic);
	    assertArrayEquals(EXPECTED_MAGIC, actualMagic);
	    in.skip(Long.MAX_VALUE);
	    lin.consume();
	}
	fis.close();
    }

    @Test
    public void cached() throws IOException {
	FileInputStream fis = new FileInputStream(ARCHIVE_PATH);
	GZIPArchiveReader gzar = new GZIPArchiveReader(fis);
	byte[] actualMagic = new byte[EXPECTED_MAGIC.length];
	fis = new FileInputStream(ARCHIVE_PATH);
	gzar = new GZIPArchiveReader(fis);
	for (int i = 0; i < ARCHIVE_SIZE + 1; i++) {
	    GZIPArchive.ReadEntry re = gzar.getEntry(true);
	    if (re == null) break;
	    LazyInflater lin = re.lazyInflater;
	    for (int repeat = 0; repeat < 3; repeat++) {
		InputStream in = lin.get(); // can get as many times you want
		in.read(actualMagic);
		assertArrayEquals(EXPECTED_MAGIC, actualMagic);
		in.skip(Long.MAX_VALUE);
	    }
	    lin.consume(); // must consume just once!
	}
	fis.close();
    }

}
