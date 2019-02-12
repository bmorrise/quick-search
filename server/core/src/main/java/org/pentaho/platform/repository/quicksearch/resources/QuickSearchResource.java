/*
Copyright 2019 Benjamin Morrise

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package org.pentaho.platform.repository.quicksearch.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pentaho.platform.quicksearch.client.Result;
import org.pentaho.platform.repository.quicksearch.client.elasticsearch.ElasticSearchClient;
import org.pentaho.platform.repository.quicksearch.service.QuickSearchService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by bmorrise on 3/20/18.
 */
@Path("/quick-search/api/datasource")
public class QuickSearchResource {

  private QuickSearchService quickSearchService;
  private ObjectMapper objectMapper;

  public QuickSearchResource( QuickSearchService quickSearchService ) {
    this.objectMapper = new ObjectMapper();
    this.quickSearchService = quickSearchService;
  }

  @GET
  @Path("/search")
  @Produces({APPLICATION_JSON})
  public Response doSearch( @QueryParam("id") String id, @QueryParam("filename") String filename, @QueryParam
          ("extension") String extension ) throws JsonProcessingException {
    Result result = quickSearchService.query( filename, extension );
    result.setId( id );
    return Response.ok( objectMapper.writeValueAsString( result ) ).build();
  }

  @GET
  @Path("/index")
  public Response doIndex() {
    quickSearchService.index();
    return Response.ok().build();
  }

  @GET
  @Path("/indexById")
  public Response doIndexById( @QueryParam("id") String id ) {
    quickSearchService.indexById( id );
    return Response.ok().build();
  }
}
