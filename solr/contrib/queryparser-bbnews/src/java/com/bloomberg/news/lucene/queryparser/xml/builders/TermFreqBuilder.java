package com.bloomberg.news.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.w3c.dom.Element;

import com.bloomberg.news.lucene.search.IntegerRange;
import com.bloomberg.news.lucene.search.TermFreqQuery;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class TermFreqBuilder implements QueryBuilder {

  private final QueryBuilder queryBuilder;


  public TermFreqBuilder(QueryBuilder queryBuilder) {
    this.queryBuilder = queryBuilder;
  }

  @Override
  public Query getQuery(Element e) throws ParserException {
    return build(queryBuilder.getQuery(e), e);
  }

  private IntegerRange getTF(Element e) {
    String minTF_str = DOMUtils.getAttribute(e, "minTF", null);
    String maxTF_str = DOMUtils.getAttribute(e, "maxTF", null);

    return new IntegerRange(
        (minTF_str == null ? null : Integer.parseInt(minTF_str)),
        (maxTF_str == null ? null : Integer.parseInt(maxTF_str)));
  }

  private Query build(Query q, Element e) throws ParserException {
    IntegerRange termFreqRange = getTF(e);

    if (q instanceof TermQuery) {
      return new TermFreqQuery((TermQuery)q, termFreqRange);
    } else if (q instanceof BooleanQuery) {
      BooleanQuery oldbq = (BooleanQuery)q;
      BooleanQuery.Builder bq = new BooleanQuery.Builder();
      for (BooleanClause bc : oldbq.clauses()) {
        Query subq = bc.getQuery();
        if (subq instanceof TermQuery) {
          bq.add(new TermFreqQuery((TermQuery)subq, termFreqRange), bc.getOccur() );
        } else {
          throw new ParserException("Sub-Query is of unsupported type: "+subq);
        }
      }
      return bq.build();
    } else {
      throw new ParserException("Query is of unsupported type: "+q);
    }
  }
}
