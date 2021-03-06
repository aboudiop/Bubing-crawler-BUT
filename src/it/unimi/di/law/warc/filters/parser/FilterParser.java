/* Generated By:JavaCC: Do not edit this line. FilterParser.java */
package it.unimi.di.law.warc.filters.parser;

/*
 * Copyright (C) 2004-2011 Paolo Boldi, Massimo Santini and Sebastiano Vigna
 *
 *  This library is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published by the Free
 *  Software Foundation; either version 2.1 of the License, or (at your option)
 *  any later version.
 *
 *  This library is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 */

// RELEASE-STATUS: DIST

import it.unimi.di.law.warc.filters.*;
import it.unimi.dsi.fastutil.objects.*;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;

/** A simple parser that transforms a filter expression into a filter.
 */
public class FilterParser<T> implements FilterParserConstants {

        private final static boolean DEBUG = false;

        private Class tClass;

        public FilterParser( Class<T> tClass ) {
                this( new java.io.StringReader( "" ) );
                this.tClass = tClass;
        }

        public Filter<T> parse( String filter ) throws ParseException {
                ReInit( new java.io.StringReader( filter ) );
                return start();
        }

/** Parser. */
  final public Filter<T> start() throws ParseException {
        Filter<T> res;
    res = or();
                {if (true) return res;}
    throw new Error("Missing return statement in function");
  }

  final public Filter<T> or() throws ParseException {
        Filter<T> res;
        ObjectArrayList<Filter<T>> qrm = new ObjectArrayList<Filter<T>>();
    res = and();
          qrm.add( res );
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OR:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      jj_consume_token(OR);
      res = and();
                    qrm.add( res );
    }
                if ( qrm.size() == 1 ) {if (true) return res;}
                {if (true) return Filters.or( (Filter<T>[])qrm.toArray( Filters.EMPTY_ARRAY ) );}
    throw new Error("Missing return statement in function");
  }

  final public Filter<T> and() throws ParseException {
        Filter<T> res;
        ObjectArrayList<Filter<T>> qrm = new ObjectArrayList<Filter<T>>();
    res = atom();
          qrm.add( res );
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case AND:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_2;
      }
      jj_consume_token(AND);
      res = atom();
                    qrm.add( res );
    }
                if ( qrm.size() == 1 ) {if (true) return res;}
                {if (true) return Filters.and( (Filter<T>[])qrm.toArray( Filters.EMPTY_ARRAY ) );}
    throw new Error("Missing return statement in function");
  }

  final public Filter<T> atom() throws ParseException {
        Filter<T> res;
        ObjectArrayList<Filter<T>> qrm = new ObjectArrayList<Filter<T>>();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TRUE:
    case FALSE:
    case OPENPAREN:
    case WORD:
      res = ground();
          {if (true) return res;}
      break;
    case NOT:
      jj_consume_token(NOT);
      res = ground();
          {if (true) return Filters.not( res );}
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public Filter<T> ground() throws ParseException {
        Filter<T> res;
        Token tclass, targs;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WORD:
      tclass = jj_consume_token(WORD);
      targs = jj_consume_token(ARGS);
                        try {
                                {if (true) return Filters.getFilterFromSpec( tclass.image, targs.image.substring( 1, targs.image.length() - 1 ).trim(), tClass );}
                        } catch ( ParseException e ) {
                                {if (true) throw e;}
                        } catch ( Exception e ) {
                                {if (true) throw new ParseException( e.toString() );}
                        }
      break;
    case TRUE:
      jj_consume_token(TRUE);
                        {if (true) return Filters.TRUE;}
      break;
    case FALSE:
      jj_consume_token(FALSE);
                        {if (true) return Filters.FALSE;}
      break;
    case OPENPAREN:
      jj_consume_token(OPENPAREN);
      res = start();
      jj_consume_token(CLOSEPAREN);
                        {if (true) return res;}
      break;
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public FilterParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[4];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x40,0x20,0x1780,0x1700,};
   }

  /** Constructor with InputStream. */
  public FilterParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public FilterParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new FilterParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public FilterParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new FilterParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public FilterParser(FilterParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(FilterParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[14];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 4; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 14; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
