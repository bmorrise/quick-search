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

package org.pentaho.di.quicksearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.quicksearch.result.FileResult;
import org.pentaho.di.quicksearch.result.SearchResult;
import org.pentaho.di.quicksearch.result.SearchResults;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.ui.spoon.Spoon;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 3/22/18.
 */
public class FileSearchService implements QuickSearchService {

  private static final Class<?> PKG = FileSearchService.class;
  private static final String SEARCH_ENDPOINT = "/plugin/quick-search/api/datasource/search";
  private static final String INDEX_BY_ID_ENDPOINT = "/plugin/quick-search/api/datasource/indexById";
  private static final String INDEX_ENDPOINT = "/plugin/quick-search/api/datasource/index";
  private static final String DEFAULT_USER = "admin";
  private static final String DEFAULT_PASSWORD = "password";
  private static final String DEFAULT_URL = "http://localhost:8080/pentaho";
  public static final String NAME = "FILE_SEARCH_SERVICE";

  private ObjectMapper objectMapper;
  private String latest;
  private CloseableHttpAsyncClient asyncClient;

  public FileSearchService() {
    objectMapper = new ObjectMapper();
    asyncClient = getClient();
  }

  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;

  @Override
  public boolean isAvailable() {
    Repository repository = spoonSupplier.get().rep;
    return repository != null && repository.getRepositoryMeta().getId().equals( "PentahoEnterpriseRepository" );
  }

  @Override
  public String getLabel() {
    return BaseMessages.getString( PKG, "quicksearch.files.service.label" );
  }

  @Override
  public int rank() {
    return 10;
  }

  @Override
  public void init() {
    if ( spoonSupplier.get() != null && spoonSupplier.get().rep != null && spoonSupplier.get().rep.getUserInfo()
            .isAdmin() ) {
      index();
    }
  }

  public void index() {
    try {
      HttpGet httpGet = new HttpGet( buildUrl( INDEX_ENDPOINT ) );
      CloseableHttpAsyncClient asyncClient = getClient();
      Future<HttpResponse> httpResponseFuture = asyncClient.execute( httpGet, null );
      HttpResponse httpResponse = httpResponseFuture.get();
      httpResponse.getEntity();
      asyncClient.close();
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }

  public String buildUrl( String endpoint ) {
    RepositoryMeta repositoryMeta = spoonSupplier.get().rep.getRepositoryMeta();
    String url = DEFAULT_URL;
    try {
      Method method = repositoryMeta.getClass().getMethod( "getRepositoryLocation" );
      Object object = method.invoke( repositoryMeta );
      Method method1 = object.getClass().getMethod( "getUrl" );
      url = (String) method1.invoke( object );
    } catch ( Exception e ) {
      e.printStackTrace();
    }
    return url + endpoint;
  }

  public void indexById( String id, ResponseListener listener ) {
    try {
      HttpGet httpGet = new HttpGet( buildUrl( INDEX_BY_ID_ENDPOINT ) + "?id=" + URLEncoder.encode( id, "UTF-8" ) );
      CloseableHttpAsyncClient asyncClient = getClient();
      Future<HttpResponse> httpResponseFuture = asyncClient.execute( httpGet, null );
      HttpResponse httpResponse = httpResponseFuture.get();
      httpResponse.getEntity();
      listener.call( null );
      asyncClient.close();
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }

  @Override
  public void search( String filename, SearchOptions searchOptions, ResponseListener listener ) {
    latest = UUID.randomUUID().toString();
    asyncClient = getClient();
    try {
      HttpGet httpGet = new HttpGet( buildUrl( SEARCH_ENDPOINT ) + "?filename=*" + URLEncoder.encode( filename, "UTF-8" ) +
              "*&extension=" + URLEncoder.encode( searchOptions.getExtension(), "UTF-8" ) + "&id=" + URLEncoder
              .encode( latest, "UTF-8" ) );

      asyncClient.execute( httpGet, new FutureCallback<HttpResponse>() {
        @Override
        public void completed( HttpResponse httpResponse ) {
          HttpEntity entity = httpResponse.getEntity();
          try {
            SearchResults searchResults = createFiles( IOUtils.toString( entity.getContent() ) );
            // Ignore anything that isn't the latest call
            if ( searchResults.getId().equals( latest ) ) {
              listener.call( searchResults.getSearchResults() );
            }
          } catch ( IOException e ) {
            listener.call( null );
          }
        }

        @Override
        public void failed( Exception e ) {
          e.printStackTrace();
        }

        @Override
        public void cancelled() {

        }
      });
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }

  private SearchResults createFiles( String json ) {
    SearchResults searchResults = new SearchResults();
    JSONParser jsonParser = new JSONParser();
    try {
      JSONObject jsonObject = (JSONObject) jsonParser.parse( json );
      String id = (String) jsonObject.get( "id" );
      searchResults.setId( id );

      List<SearchResult> files = new ArrayList<>();
      JSONArray jsonArray = (JSONArray) jsonObject.get( "fileList" );
      for ( Object fileJson : jsonArray ) {
        try {
          FileResult fileResult = objectMapper.readValue( ((JSONObject) fileJson).toJSONString(), FileResult.class );
          files.add( fileResult );
        } catch ( IOException e ) {
          // Do nothing
        }
      }
      searchResults.setSearchResults( files );
    } catch ( ParseException e ) {
      //Ignore
    }
    return searchResults;
  }

  protected CloseableHttpAsyncClient getClient() {
    String username = DEFAULT_USER;
    String password = DEFAULT_PASSWORD;
    if ( spoonSupplier.get() != null && spoonSupplier.get().rep != null ) {
      Repository repository = spoonSupplier.get().rep;
      username = repository.getUserInfo().getName();
      password = repository.getUserInfo().getPassword();
    }

    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials( username, password );
    credentialsProvider.setCredentials( AuthScope.ANY, credentials );

    CloseableHttpAsyncClient httpClient = HttpAsyncClientBuilder.create().setDefaultCredentialsProvider(
            credentialsProvider ).build();
    httpClient.start();

    return httpClient;
  }

  public void close() {
    try {
      asyncClient.close();
    } catch ( IOException e ) {
      // Ignore
    }
  }

  @Override
  public String getName() {
    return NAME;
  }
}
