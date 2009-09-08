/*
 * (c) Copyright 2009 University of Bristol
 * All rights reserved.
 * [See end of file]
 */
package net.rootdev.javardfa;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.sail.SailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Damian Steer <pldms@mac.com>
 */
@RunWith(Parameterized.class)
public class RDFaConformance {

   final static Logger log = LoggerFactory.getLogger(RDFaConformance.class);
   final static String ManifestURI =
           "http://www.w3.org/2006/07/SWD/RDFa/testsuite/xhtml1-testcases/rdfa-xhtml1-test-manifest.rdf";

   @Parameters
   public static Collection<String[]> testFiles()
           throws URISyntaxException, IOException, SailException, RepositoryException, RDFParseException, MalformedQueryException, QueryEvaluationException {
      RepositoryConnection conn = SesameScratch.fetchResource(URI.create(ManifestURI).toURL(), RDFFormat.RDFXML);
      URL testsURL = RDFaConformance.class.getResource("/manifest-extract.rq");
      String testsQueryStr = SesameScratch.getContentAsString(testsURL);
      System.out.println("query string=" + testsQueryStr);

      TupleQuery testsQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, testsQueryStr);

      Collection<String[]> tests = new ArrayList<String[]>();
      TupleQueryResult results = testsQuery.evaluate();


      if (!results.hasNext()) {
         throw new RuntimeException("No results");
      }
      while (results.hasNext()) {
         BindingSet soln = results.next();
         String[] params = new String[6];
         params[0] = soln.getBinding("test").getValue().stringValue();
         params[1] = soln.getBinding("title").getValue().stringValue();
         params[2] = soln.getBinding("purpose").getValue().stringValue();
         params[3] = soln.getBinding("input").getValue().stringValue();
         params[4] = soln.getBinding("query").getValue().stringValue();
         // getBoolean not working??
         //boolean expected = (soln.contains("expect")) ?
         //    soln.getLiteral("expect").getBoolean() : true;
         params[5] = soln.hasBinding("expect") ? soln.getBinding("expect").getValue().stringValue() : "true";
         tests.add(params);
      }

      return tests;
   }
   private final String test;
   private final String title;
   private final String purpose;
   private final String input;
   private final String query;
   private final boolean expected;

   public RDFaConformance(String test, String title,
           String purpose, String input, String query, String expected) {
      this.test = test;
      this.title = title;
      this.purpose = purpose;
      this.input = input;
      this.query = query;
      this.expected = Boolean.valueOf(expected);
      /* If you want it to go slowwwwww */
   }

   @Test
   public void compare() {

      System.err.println("------ running test: " + test + "-----");
      try {
         RepositoryConnection conn = SesameScratch.fetchResource(new URL(input), RDFFormat.forMIMEType("application/xhtml+xml"));
         String queryStr = SesameScratch.getContentAsString(new URL(query));
         BooleanQuery q = conn.prepareBooleanQuery(QueryLanguage.SPARQL, queryStr);
         if (q.evaluate() == expected) {
         } else {
            StringBuffer message = new StringBuffer(this.toString() + "\n");
            message.append("DETAILS ---- RDF ---\n");
            StringWriter rdfWriter = new StringWriter(1024);
            conn.export(new TurtleWriter(rdfWriter));
            message.append(rdfWriter);
            message.append("\n------ Query ------");
            message.append(queryStr);
            message.append("\n-----------------------");
            fail(message.toString());
         }
      } catch (Exception e) {
         fail(this.toString() +
                 "cought exception:" + e.toString());
      }

   }

   @Override
   public String toString() {
      return "test:      <" + test + ">\n" +
              "title:    " + title + "\n" +
              "purpose:  " + purpose + "\n" +
              "input:    <" + input + ">\n" +
              "query:    " + query + "\n" +
              ((expected) ? "expected: " + expected + "\n" : "");
   }
}

/*
 * (c) Copyright 2009 University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
