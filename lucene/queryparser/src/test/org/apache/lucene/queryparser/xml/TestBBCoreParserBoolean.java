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

package org.apache.lucene.queryparser.xml;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.xml.builders.BBBooleanQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.GenericTextQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.NearQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.WildcardNearQueryBuilder;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanQuery;
import java.io.IOException;


public class TestBBCoreParserBoolean extends TestCoreParser {

  protected CoreParser newCoreParser(String defaultField, Analyzer analyzer) {
    final CoreParser coreParser = new CoreParser(defaultField, analyzer);

    // the query builder to be tested
    {
      String queryName = "BooleanQuery";
      BBBooleanQueryBuilder builder = new BBBooleanQueryBuilder(coreParser.queryFactory, coreParser.spanFactory);
      coreParser.addQueryBuilder(queryName, builder);
      coreParser.addSpanBuilder(queryName, builder);
    }

    // some additional builders to help
    {
      String queryName = "GenericTextQuery";
      GenericTextQueryBuilder builder = new GenericTextQueryBuilder(analyzer);
      coreParser.addQueryBuilder(queryName, builder);
      // This does not implement SpanQueryBuilder yet
      //coreParser.addSpanBuilder(queryName, builder);
    }
    {
      String queryName = "NearQuery";
      NearQueryBuilder builder = new NearQueryBuilder(coreParser.spanFactory);
      coreParser.addQueryBuilder(queryName, builder);
      coreParser.addSpanBuilder(queryName, builder);
    }
    {
      String queryName = "WildcardNearQuery";
      WildcardNearQueryBuilder builder = new WildcardNearQueryBuilder(analyzer);
      coreParser.addQueryBuilder(queryName, builder);
      coreParser.addSpanBuilder(queryName, builder);
    }

    return coreParser;
  }

  public void testBooleanQueryTripleShouldWildcardNearQuery() throws Exception {
    final Query q = parse("BBBooleanQueryTripleShouldWildcardNearQuery.xml");
    final int size = ((BooleanQuery)q).clauses().size();
    assertTrue("Expecting 2 clauses, but resulted in " + size, size == 2);
    final BooleanQuery bq = (BooleanQuery)q;
    for(BooleanClause bc : bq.clauses())
    {
      assertFalse("Not expecting MatchAllDocsQuery ",bc.getQuery() instanceof MatchAllDocsQuery);
    }
  }

  public void testBooleanQueryMustShouldWildcardNearQuery() throws ParserException, IOException {
    final Query q = parse("BBBooleanQueryMustShouldWildcardNearQuery.xml");
    assertTrue("Expecting a SpanQuery, but resulted in " + q.getClass(), q instanceof SpanQuery);
  }

  public void testBooleanQueryMustMustShouldWildcardNearQuery() throws Exception {
    final Query q = parse("BBBooleanQueryMustMustShouldWildcardNearQuery.xml");
    assertTrue("Expecting a BooleanQuery, but resulted in " + q.getClass(), q instanceof BooleanQuery);
    final BooleanQuery bq = (BooleanQuery)q;
    final int size = bq.clauses().size();
    assertTrue("Expecting 2 clauses, but resulted in " + size, size == 2);
    for(BooleanClause bc : bq.clauses())
    {
      assertFalse("Not expecting MatchAllDocsQuery ", bc.getQuery() instanceof MatchAllDocsQuery);
    }
  }

  public void testBooleanQueryMatchAllDocsQueryWildcardNearQuery() throws Exception {
    final Query q = parse("BBBooleanQueryMatchAllDocsQueryWildcardNearQuery.xml");
    assertTrue("Expecting a MatchAllDocsQuery, but resulted in " + q.getClass(), q instanceof MatchAllDocsQuery);
  }

  public void testBooleanQueryMatchAllDocsQueryTermQuery() throws Exception {
    final Query q = parse("BBBooleanQueryMatchAllDocsQueryTermQuery.xml");
    assertTrue("Expecting a BooleanQuery, but resulted in " + q.getClass(), q instanceof BooleanQuery);
    final BooleanQuery bq = (BooleanQuery)q;
    final int size = bq.clauses().size();
    assertTrue("Expecting 2 clauses, but resulted in " + size, size == 2);
    boolean bMatchAllDocsFound = false;
    for(BooleanClause bc : bq.clauses())
    {
      bMatchAllDocsFound |= bc.getQuery() instanceof MatchAllDocsQuery;
    }
    assertTrue("Expecting MatchAllDocsQuery ", bMatchAllDocsFound);
  }

  public void testBooleanQueryDedupe() throws ParserException, IOException {
    Query query = parse("BBBooleanQueryDedupe.xml");
    Query resultQuery = parse("BBBooleanQueryDedupeResult.xml");
    assertEquals(resultQuery, query);
  }
}
