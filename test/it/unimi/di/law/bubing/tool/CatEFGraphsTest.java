package it.unimi.di.law.bubing.tool;

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

//RELEASE-STATUS: DIST

import static org.junit.Assert.assertEquals;
import it.unimi.di.law.bubing.util.BURL;
import it.unimi.dsi.big.webgraph.EFGraph;
import it.unimi.dsi.big.webgraph.ImmutableGraph;
import it.unimi.dsi.big.webgraph.LazyLongIterator;
import it.unimi.dsi.big.webgraph.NodeIterator;
import it.unimi.dsi.webgraph.ArrayListMutableGraph;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.JSAPException;


/** A class to test {@link BURL}. */

public class CatEFGraphsTest {
	
	public static final Logger LOGGER = LoggerFactory.getLogger( CatEFGraphsTest.class );

	private File dir;

	/** Wraps an {@link ImmutableGraph} with, say, <var>N</var> nodes, but simulates that it has a different number of nodes, say <var>n</var> (specified
	 *  at construction time, and usually not larger than <var>N</var>). By contract, all arcs of the underlying graph have a source less than <var>n</var> whereas the target is less than <var>N</var>. */
	public static class XImmutableGraph extends ImmutableGraph {
		
		private ImmutableGraph graph;
		private int n;

		public XImmutableGraph( final ImmutableGraph graph, final int n ) {
			this.graph = graph;
			this.n = n;
		}

		@Override
		public long numNodes() {
			return n;
		}

		@Override
		public boolean randomAccess() {
			return graph.randomAccess();
		}

		@Override
		public long outdegree( long x ) {
			return graph.outdegree( x );
		}

		@Override
		public ImmutableGraph copy() {
			return new XImmutableGraph( graph.copy(), n );
		}
		
		public long numArcs() {
			return graph.numArcs();
		}

		public LazyLongIterator successors( long x ) {
			return graph.successors(x);
		}

		public long[][] successorBigArray( long x ) {
			return graph.successorBigArray(x);
		}

		public NodeIterator nodeIterator() {
			return new NodeIterator() {
				int i = 0;
				final NodeIterator nodeIterator = graph.nodeIterator();
				@Override
				public boolean hasNext() {
					return i < n; 
				}
				
				@Override
				public long outdegree() {
					return nodeIterator.outdegree();
				}
				
				@Override
				public LazyLongIterator successors() {
					return nodeIterator.successors();
				}
				
				@Override
				public long nextLong() {
					if ( ! hasNext() ) throw new IllegalArgumentException();
					i++;
					return nodeIterator.nextLong();
				}
			};
		}
		
	}
	
	
	@Before
	public void setUp() throws IOException {
		dir = File.createTempFile( CatEFGraphsTest.class.getName() + "-", "-temp" );
		dir.delete();
		dir.mkdir();
	}
	
	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory( dir );
	}
	
	
	@Test
	public void testVanilla() throws IOException, JSAPException {
		// (0,1) (0,2) (0,3) (1,7)
		ImmutableGraph graph1 = new XImmutableGraph( ImmutableGraph.wrap( new ArrayListMutableGraph( 8, new int[][] { { 0, 1 }, { 0, 2 }, { 0, 3 }, { 1, 7 } } ).immutableView() ), 4 );
		// (4,2) (4,3) (7,4) (7,6)
		ImmutableGraph graph2 = new XImmutableGraph( ImmutableGraph.wrap( new ArrayListMutableGraph( 8, new int[][] { { 0, 2 }, { 0, 3 }, { 3, 4 }, { 3, 6 } } ).immutableView() ), 4 );
		ImmutableGraph graphResult = ImmutableGraph.wrap( new ArrayListMutableGraph( 8, new int[][] { { 0, 1 }, { 0, 2 }, { 0, 3 }, { 1, 7 }, { 4, 2 }, { 4, 3 }, { 7, 4 }, { 7, 6 } } ).immutableView() );
		
		File ef1 = new File( dir, "graph1.ef" ); 
		EFGraph.store( graph1, 8, ef1.getAbsolutePath(), null );
		File ef2 = new File( dir, "graph2.ef" ); 
		EFGraph.store( graph2, 8, ef2.getAbsolutePath(), null );

		File result = new File( dir, "result-ef" ); 
		CatEFGraphs.main( new String[] { "-m", result.getAbsolutePath(), ef1.getAbsolutePath(), ef2.getAbsolutePath() } );
		assertEquals( graphResult, ImmutableGraph.load( result.getAbsolutePath() ) );
		result = new File( dir, "result-unmapped-ef" ); 
		CatEFGraphs.main( new String[] { result.getAbsolutePath(), ef1.getAbsolutePath(), ef2.getAbsolutePath() } );
		assertEquals( graphResult, ImmutableGraph.load( result.getAbsolutePath() ) );
	}

	@Test(expected=IllegalArgumentException.class)
	public void testWrongBound() throws IOException, JSAPException {
		// (0,1) (0,2) (0,3) (1,7)
		ImmutableGraph graph1 = new XImmutableGraph( ImmutableGraph.wrap( new ArrayListMutableGraph( 8, new int[][] { { 0, 1 }, { 0, 2 }, { 0, 3 }, { 1, 7 } } ).immutableView() ), 4 );
		// (4,2) (4,3) (7,4) (7,6)
		ImmutableGraph graph2 = new XImmutableGraph( ImmutableGraph.wrap( new ArrayListMutableGraph( 8, new int[][] { { 0, 2 }, { 0, 3 }, { 3, 4 }, { 3, 6 } } ).immutableView() ), 4 );
		File ef1 = new File( dir, "graph1.ef" ); 
		EFGraph.store( graph1, 10, ef1.getAbsolutePath(), null );
		File ef2 = new File( dir, "graph2.ef" ); 
		EFGraph.store( graph2, 8, ef2.getAbsolutePath(), null );
		File result = new File( dir, "result-ef" ); result.delete();
		CatEFGraphs.main( new String[] { "-m", result.getAbsolutePath(), ef1.getAbsolutePath(), ef2.getAbsolutePath() } );
	}


}
