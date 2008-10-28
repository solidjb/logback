/**
 * Logback: the generic, reliable, fast and flexible logging framework.
 * 
 * Copyright (C) 1999-2006, QOS.ch
 * 
 * This library is free software, you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation.
 */

package ch.qos.logback.core.pattern.parser;

import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.core.pattern.util.AlmostAsIsEscapeUtil;

import junit.framework.TestCase;

public class TokenStreamTest extends TestCase {

  public TokenStreamTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testEmpty() throws ScanException {
    List tl = new TokenStream("").tokenize();
    List witness = new ArrayList();
    assertEquals(witness, tl);
  }

  public void testSingleLiteral() throws ScanException {
    List tl = new TokenStream("hello").tokenize();
    List<Token> witness = new ArrayList<Token>();
    witness.add(new Token(Token.LITERAL, "hello"));
    assertEquals(witness, tl);
  }

  public void testLiteralWithPercent() throws ScanException {
    {
      List tl = new TokenStream("hello\\%world").tokenize();

      List<Token> witness = new ArrayList<Token>();
      witness.add(new Token(Token.LITERAL, "hello%world"));
      assertEquals(witness, tl);
    }
    {
      List tl = new TokenStream("hello\\%").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(new Token(Token.LITERAL, "hello%"));
      assertEquals(witness, tl);
    }

    {
      List tl = new TokenStream("\\%").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(new Token(Token.LITERAL, "%"));
      assertEquals(witness, tl);
    }
  }

  public void testBasic() throws ScanException {

    // test "%c"
    {
      List tl = new TokenStream("%c").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(Token.PERCENT_TOKEN);
      witness.add(new Token(Token.KEYWORD, "c"));
      assertEquals(witness, tl);
    }

    {
      // test "xyz%-34c"
      List tl = new TokenStream("%a%b").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(Token.PERCENT_TOKEN);
      witness.add(new Token(Token.KEYWORD, "a"));
      witness.add(Token.PERCENT_TOKEN);
      witness.add(new Token(Token.KEYWORD, "b"));
      assertEquals(witness, tl);
    }

    {
      // test "xyz%-34c"
      List tl = new TokenStream("xyz%-34c").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(new Token(Token.LITERAL, "xyz"));
      witness.add(Token.PERCENT_TOKEN);
      witness.add(new Token(Token.FORMAT_MODIFIER, "-34"));
      witness.add(new Token(Token.KEYWORD, "c"));
      assertEquals(witness, tl);
    }
  }

  public void testComplexNR() throws ScanException {
    List tl = new TokenStream("%d{1234} [%34.-67toto] %n").tokenize();
    List<Token> witness = new ArrayList<Token>();
    witness.add(Token.PERCENT_TOKEN);
    witness.add(new Token(Token.KEYWORD, "d"));
    witness.add(new Token(Token.OPTION, "1234"));
    witness.add(new Token(Token.LITERAL, " ["));
    witness.add(Token.PERCENT_TOKEN);
    witness.add(new Token(Token.FORMAT_MODIFIER, "34.-67"));
    witness.add(new Token(Token.KEYWORD, "toto"));
    witness.add(new Token(Token.LITERAL, "] "));
    witness.add(Token.PERCENT_TOKEN);
    witness.add(new Token(Token.KEYWORD, "n"));
    assertEquals(witness, tl);
  }

  public void testEmptyP() throws ScanException {
    List tl = new TokenStream("()").tokenize();
    List<Token> witness = new ArrayList<Token>();
    witness.add(new Token(Token.LITERAL, "("));
    witness.add(Token.RIGHT_PARENTHESIS_TOKEN);
    assertEquals(witness, tl);
  }

  public void testEmptyP2() throws ScanException {
    List tl = new TokenStream("%()").tokenize();
    List<Token> witness = new ArrayList<Token>();
    witness.add(Token.PERCENT_TOKEN);
    witness.add(Token.LEFT_PARENTHESIS_TOKEN);
    witness.add(Token.RIGHT_PARENTHESIS_TOKEN);
    assertEquals(witness, tl);
  }

  public void testEscape() throws ScanException {
    {
      List tl = new TokenStream("\\%").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(new Token(Token.LITERAL, "%"));
      assertEquals(witness, tl);
    }

    {
      List tl = new TokenStream("\\%\\(\\t\\)\\r\\n").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(new Token(Token.LITERAL, "%(\t)\r\n"));
      assertEquals(witness, tl);
    }

    {
      List tl = new TokenStream("\\\\%x").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(new Token(Token.LITERAL, "\\"));
      witness.add(Token.PERCENT_TOKEN);
      witness.add(new Token(Token.KEYWORD, "x"));
      assertEquals(witness, tl);
    }

    {
      List tl = new TokenStream("%x\\)").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(Token.PERCENT_TOKEN);
      witness.add(new Token(Token.KEYWORD, "x"));
      witness.add(new Token(Token.LITERAL, ")"));
      assertEquals(witness, tl);
    }

    {
      List tl = new TokenStream("%x\\_a").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(Token.PERCENT_TOKEN);
      witness.add(new Token(Token.KEYWORD, "x"));
      witness.add(new Token(Token.LITERAL, "a"));
      assertEquals(witness, tl);
    }
    {
      List tl = new TokenStream("%x\\_%b").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(Token.PERCENT_TOKEN);
      witness.add(new Token(Token.KEYWORD, "x"));
      witness.add(Token.PERCENT_TOKEN);
      witness.add(new Token(Token.KEYWORD, "b"));
      assertEquals(witness, tl);
    }
  }

  public void testOptions() throws ScanException {
    {
      List tl = new TokenStream("%x{t}").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(Token.PERCENT_TOKEN);
      witness.add(new Token(Token.KEYWORD, "x"));
      witness.add(new Token(Token.OPTION, "t"));
      assertEquals(witness, tl);
    }

    {
      List tl = new TokenStream("%x{t,y}").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(Token.PERCENT_TOKEN);
      witness.add(new Token(Token.KEYWORD, "x"));
      witness.add(new Token(Token.OPTION, "t,y"));
      assertEquals(witness, tl);
    }

    {
      List tl = new TokenStream("%x{\"hello world.\", \"12y  \"}").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(Token.PERCENT_TOKEN);
      witness.add(new Token(Token.KEYWORD, "x"));
      witness.add(new Token(Token.OPTION, "\"hello world.\", \"12y  \""));
      assertEquals(witness, tl);
    }

    {
      List tl = new TokenStream("%x{opt\\}}").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(Token.PERCENT_TOKEN);
      witness.add(new Token(Token.KEYWORD, "x"));
      witness.add(new Token(Token.OPTION, "opt}"));
      assertEquals(witness, tl);
    }
  }

  public void testSimpleP() throws ScanException {
    List tl = new TokenStream("%(hello %class{.4?})").tokenize();
    List<Token> witness = new ArrayList<Token>();
    witness.add(Token.PERCENT_TOKEN);
    witness.add(Token.LEFT_PARENTHESIS_TOKEN);
    witness.add(new Token(Token.LITERAL, "hello "));
    witness.add(Token.PERCENT_TOKEN);
    witness.add(new Token(Token.KEYWORD, "class"));
    witness.add(new Token(Token.OPTION, ".4?"));
    witness.add(Token.RIGHT_PARENTHESIS_TOKEN);
    assertEquals(witness, tl);
  }

  public void testSimpleP2() throws ScanException {
    List tl = new TokenStream("X %a %-12.550(hello %class{.4?})").tokenize();
    List<Token> witness = new ArrayList<Token>();
    witness.add(new Token(Token.LITERAL, "X "));
    witness.add(Token.PERCENT_TOKEN);
    witness.add(new Token(Token.KEYWORD, "a"));
    witness.add(new Token(Token.LITERAL, " "));
    witness.add(Token.PERCENT_TOKEN);
    witness.add(new Token(Token.FORMAT_MODIFIER, "-12.550"));
    witness.add(Token.LEFT_PARENTHESIS_TOKEN);
    witness.add(new Token(Token.LITERAL, "hello "));
    witness.add(Token.PERCENT_TOKEN);
    witness.add(new Token(Token.KEYWORD, "class"));
    witness.add(new Token(Token.OPTION, ".4?"));
    witness.add(Token.RIGHT_PARENTHESIS_TOKEN);
    assertEquals(witness, tl);
  }

  public void testMultipleRecursion() throws ScanException {
    List tl = new TokenStream("%-1(%d %45(%class %file))").tokenize();
    List<Token> witness = new ArrayList<Token>();
    witness.add(Token.PERCENT_TOKEN);
    witness.add(new Token(Token.FORMAT_MODIFIER, "-1"));
    witness.add(Token.LEFT_PARENTHESIS_TOKEN);
    witness.add(Token.PERCENT_TOKEN);
    witness.add(new Token(Token.KEYWORD, "d"));
    witness.add(new Token(Token.LITERAL, " "));
    witness.add(Token.PERCENT_TOKEN);
    witness.add(new Token(Token.FORMAT_MODIFIER, "45"));
    witness.add(Token.LEFT_PARENTHESIS_TOKEN);
    witness.add(Token.PERCENT_TOKEN);
    witness.add(new Token(Token.KEYWORD, "class"));
    witness.add(new Token(Token.LITERAL, " "));
    witness.add(Token.PERCENT_TOKEN);
    witness.add(new Token(Token.KEYWORD, "file"));
    witness.add(Token.RIGHT_PARENTHESIS_TOKEN);
    witness.add(Token.RIGHT_PARENTHESIS_TOKEN);

    assertEquals(witness, tl);
  }

  public void testNested() throws ScanException {
    List tl = new TokenStream("%(%a%(%b))").tokenize();
    List<Token> witness = new ArrayList<Token>();
    witness.add(Token.PERCENT_TOKEN);
    witness.add(Token.LEFT_PARENTHESIS_TOKEN);
    witness.add(Token.PERCENT_TOKEN);
    witness.add(new Token(Token.KEYWORD, "a"));
    witness.add(Token.PERCENT_TOKEN);
    witness.add(Token.LEFT_PARENTHESIS_TOKEN);
    witness.add(Token.PERCENT_TOKEN);
    witness.add(new Token(Token.KEYWORD, "b"));
    witness.add(Token.RIGHT_PARENTHESIS_TOKEN);
    witness.add(Token.RIGHT_PARENTHESIS_TOKEN);

    assertEquals(witness, tl);

  }

  public void testEscapedParanteheses() throws ScanException {
    {
      List tl = new TokenStream("\\(%h\\)").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(new Token(Token.LITERAL, "("));
      witness.add(Token.PERCENT_TOKEN);
      witness.add(new Token(Token.KEYWORD, "h"));
      witness.add(new Token(Token.LITERAL, ")"));
      assertEquals(witness, tl);
    }
    {
      List tl = new TokenStream("(%h\\)").tokenize();
      List<Token> witness = new ArrayList<Token>();
      witness.add(new Token(Token.LITERAL, "("));
      witness.add(Token.PERCENT_TOKEN);
      witness.add(new Token(Token.KEYWORD, "h"));
      witness.add(new Token(Token.LITERAL, ")"));
      assertEquals(witness, tl);
    }
  }

  public void testWindowsLikeBackSlashes() throws ScanException {
    {
      List tl = new TokenStream("c:\\hello\\world.%i", new AlmostAsIsEscapeUtil())
          .tokenize();

      List<Token> witness = new ArrayList<Token>();
      witness.add(new Token(Token.LITERAL, "c:\\hello\\world."));
      witness.add(Token.PERCENT_TOKEN);
      witness.add(new Token(Token.KEYWORD, "i"));
      assertEquals(witness, tl);
    }
  }
}