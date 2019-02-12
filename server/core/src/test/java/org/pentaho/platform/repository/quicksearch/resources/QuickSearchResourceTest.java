package org.pentaho.platform.repository.quicksearch.resources;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.security.userroledao.IPentahoRole;
import org.pentaho.platform.repository.quicksearch.client.elasticsearch.ElasticSearchClient;
import org.pentaho.platform.repository.quicksearch.service.QuickSearchService;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bmorrise on 3/20/18.
 */
public class QuickSearchResourceTest {

  private QuickSearchResource quickSearchResource;

  @Before
  public void setup() {
    IPentahoSession session = mock( IPentahoSession.class );
    when( session.getName() ).thenReturn( "suzy" );

    Supplier<IPentahoSession> sessionSupplier = () -> session;
    Supplier<Boolean> adminSupplier = () -> false;

    QuickSearchService quickSearchService = new QuickSearchService( new ElasticSearchClient(), sessionSupplier,
            adminSupplier, s -> Collections.EMPTY_LIST );
    quickSearchResource = new QuickSearchResource( quickSearchService );
  }

  @Test
  public void testSearch() throws Exception {
    Response response = quickSearchResource.doSearch( UUID.randomUUID().toString(), "*Trans*", "ktr" );
    System.out.println( response.getEntity() );
  }

  @Test
  public void testSetup() throws Exception {
    ElasticSearchClient elasticSearchClient = new ElasticSearchClient();
    elasticSearchClient.setup();
  }

}
