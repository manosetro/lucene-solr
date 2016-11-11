package org.apache.lucene.queryparser.xml;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.queries.FilterClause;
import org.apache.lucene.queries.TermFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.MatchAllDocsFilter;
import org.apache.lucene.search.Query;

import java.io.IOException;


public class TestCoreParserBooleanFilterBuilder extends TestBBCoreParser {

  public void testBooleanFilterXML() throws ParserException, IOException {
    Query q = parse("BooleanFilter.xml");
    dumpResults("Boolean filter", q, 5);
  }

  public void testBooleanFilterwithMatchAllDocsFilter() throws ParserException, IOException {

    String text = "<BooleanFilter fieldName='content' disableCoord='true'>"
        + "<Clause occurs='should'><TermFilter>janeiro</TermFilter></Clause>"
        + "<Clause occurs='should'><MatchAllDocsFilter/></Clause></BooleanFilter>";

    Filter f = parseFilterXML(text);
    assertTrue("Expecting a TermFilter, but resulted in " + f.getClass(), f instanceof TermFilter);

    text = "<BooleanFilter fieldName='content' disableCoord='true'>"
        + "<Clause occurs='must'><TermFilter>rio</TermFilter></Clause>"
        + "<Clause occurs='should'><MatchAllDocsFilter/></Clause></BooleanFilter>";
    f = parseFilterXML(text);
    assertTrue("Expecting a TermFilter, but resulted in " + f.getClass(), f instanceof TermFilter);

    text = "<BooleanFilter fieldName='content' disableCoord='true'>"
        + "<Clause occurs='must'><TermFilter>rio</TermFilter></Clause>"
        + "<Clause occurs='must'><TermFilter>janeiro</TermFilter></Clause>"
        + "<Clause occurs='must'><TermFilter>summit</TermFilter></Clause>"
        + "<Clause occurs='should'><MatchAllDocsFilter/></Clause></BooleanFilter>";
    f = parseFilterXML(text);
    assertTrue("Expecting a BooleanFilter, but resulted in " + f.getClass(), f instanceof BooleanFilter);
    BooleanFilter bf = (BooleanFilter)f;
    int size = bf.clauses().size();
    assertTrue("Expecting 3 clauses, but resulted in " + size, size == 3);
    for(FilterClause fc : bf.clauses())
    {
      assertFalse("Not expecting MatchAllDocsQuery ", fc.getFilter() instanceof MatchAllDocsFilter);
    }

    text = "<BooleanFilter fieldName='content' disableCoord='true'>"
        + "<Clause occurs='must'><MatchAllDocsFilter/></Clause>"
        + "<Clause occurs='should'><MatchAllDocsFilter/></Clause></BooleanFilter>";
    f = parseFilterXML(text);
    assertTrue("Expecting a MatchAllDocsFilter, but resulted in " + f.getClass(), f instanceof MatchAllDocsFilter);

    text = "<BooleanFilter fieldName='content' disableCoord='true'>"
        + "<Clause occurs='must'><MatchAllDocsFilter/></Clause>"
        + "<Clause occurs='mustnot'><TermFilter>summit</TermFilter></Clause></BooleanFilter>";
    f = parseFilterXML(text);
    assertTrue("Expecting a BooleanFilter, but resulted in " + f.getClass(), f instanceof BooleanFilter);
    bf = (BooleanFilter)f;
    size = bf.clauses().size();
    assertTrue("Expecting 2 clauses, but resulted in " + size, size == 2);
    boolean bMatchAllDocsFound = false;
    for(FilterClause fc : bf.clauses())
    {
      bMatchAllDocsFound |= fc.getFilter() instanceof MatchAllDocsFilter;
    }
    assertTrue("Expecting MatchAllDocsFilter ", bMatchAllDocsFound);
  }
}
