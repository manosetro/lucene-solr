package com.bloomberg.news.lucene.queryparser.xml.builders;

import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanBoostQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.queryparser.xml.builders.SpanQueryBuilder;
import org.w3c.dom.Element;

import com.bloomberg.news.lucene.queryparser.xml.SingleTermProcessor;
import com.bloomberg.news.lucene.queryparser.xml.TermBuilder;

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

/**
 * Builder for {@link TermQuery}
 */
public class BBTermQueryBuilder implements QueryBuilder, SpanQueryBuilder {

  protected final TermBuilder termBuilder;

  public BBTermQueryBuilder(TermBuilder termBuilder) {
    this.termBuilder = termBuilder;
  }

  @Override
  public Query getQuery(Element e) throws ParserException {
    SingleTermProcessor tp = new SingleTermProcessor();
    String field = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
    //extract the value and fail if there is no value.
    //This is a query builder for one and only one term
    String value =  DOMUtils.getNonBlankTextOrFail(e);
    this.termBuilder.extractTerms(tp, field, value);

    try {
      Query q = new TermQuery(tp.getTerm());
      float boost = DOMUtils.getAttribute(e, "boost", 1.0f);
      if (boost != 1f) {
        q = new BoostQuery(q, boost);
      }
      return q;
    } catch (ParserException ex){
      throw new ParserException(ex.getMessage() + " field:" + field
          + " value:" + value + ". Check the query analyzer configured on this field." );
    }
  }

  @Override
  public SpanQuery getSpanQuery(Element e) throws ParserException {
    SingleTermProcessor tp = new SingleTermProcessor();
    String field = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
    //extract the value and fail if there is no value.
    //This is a query builder for one and only one term
    String value =  DOMUtils.getNonBlankTextOrFail(e);
    this.termBuilder.extractTerms(tp, field, value);

    try {
      SpanQuery q = new SpanTermQuery(tp.getTerm());
      float boost = DOMUtils.getAttribute(e, "boost", 1.0f);
      if (boost != 1f) {
        q = new SpanBoostQuery(q, boost);
      }
      return q;
    } catch (ParserException ex){
      throw new ParserException(ex.getMessage() + " field:" + field
          + " value:" + value + ". Check the query analyzer configured on this field." );
    }
  }

}
