/*
 * (c) Copyright 2009 University of Bristol
 * All rights reserved.
 * [See end of file]
 */
package rdfa;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import net.rootdev.javardfa.RDFaHtmlParserFactory;
import net.rootdev.javardfa.RDFaXHtmlParserFactory;
import net.rootdev.javardfa.SesameUtils;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.turtle.TurtleWriter;

/**
 * Simple command line tool
 *
 * @author pldms
 */
public class parse {

   public static void main(String... args) throws Exception {
      if (args.length == 0) usage();

      RDFFormat format = RDFaXHtmlParserFactory.rdfa_xhtml_Format;

      List<String> uris = new LinkedList<String>();

      for (int i = 0; i < args.length; i++) {
         if ("--help".equalsIgnoreCase(args[i])) usage();
         else if ("--format".equalsIgnoreCase(args[i])) {
            if (i + 1 >= args.length) usage();
            String f = args[++i];
            if ("XHTML".equalsIgnoreCase(f)) {
               format = RDFaXHtmlParserFactory.rdfa_xhtml_Format;
            } else if ("HTML".equalsIgnoreCase(f)) {
               format = RDFaHtmlParserFactory.rdfa_html_Format;
            } else usage();
         } else uris.add(args[i]);
      }


      for (String uri : uris) {
         RepositoryConnection conn = SesameUtils.fetchResource(new URL(uri), format);
         TurtleWriter tw = new TurtleWriter(System.out);
         tw.handleNamespace("foaf", "http://xmlns.com/foaf/0.1/");
         tw.handleNamespace("geo", "http://www.geonames.org/ontology/");
         tw.handleNamespace("rel", "http://purl.org/vocab/relationship/");
         tw.handleNamespace("cert", "http://www.w3.org/ns/auth/cert#");
         tw.handleNamespace("rsa", "http://www.w3.org/ns/auth/rsa#");
         tw.handleNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
         conn.export(tw);
      }
   }

   private static void usage() {
      System.err.println("rdfa.parse [--format XHTML|HTML] <url> [...]");
      System.exit(0);
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
