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

package org.pentaho.platform.repository.quicksearch.service;

import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.security.userroledao.IPentahoRole;
import org.pentaho.platform.api.engine.security.userroledao.IUserRoleDao;
import org.pentaho.platform.api.mt.ITenant;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.TenantUtils;
import org.pentaho.platform.quicksearch.client.Result;
import org.pentaho.platform.quicksearch.client.SearchClient;
import org.pentaho.platform.quicksearch.client.model.Query;
import org.pentaho.platform.quicksearch.client.model.RegExp;
import org.pentaho.platform.quicksearch.client.model.Request;
import org.pentaho.platform.quicksearch.client.model.Sort;
import org.pentaho.platform.quicksearch.client.model.Term;
import org.pentaho.platform.quicksearch.client.model.Wildcard;
import org.pentaho.platform.quicksearch.client.model.compound.Bool;
import org.pentaho.platform.quicksearch.client.model.compound.occurrence.Must;
import org.pentaho.platform.quicksearch.client.model.compound.occurrence.Should;
import org.pentaho.platform.web.http.api.resources.utils.SystemUtils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 3/21/18.
 */
public class QuickSearchService {

  private SearchClient client;
  private Supplier<IPentahoSession> pentahoSessionSupplier;
  private Supplier<Boolean> adminSupplier;
  private Function<String, List<IPentahoRole>> rolesProvider;

  public QuickSearchService( SearchClient client ) {
    this.client = client;
    this.pentahoSessionSupplier = PentahoSessionHolder::getSession;
    this.adminSupplier = SystemUtils::canAdminister;
    this.rolesProvider = username -> {
      IUserRoleDao roleDao = PentahoSystem.get( IUserRoleDao.class );
      ITenant tenant = TenantUtils.getCurrentTenant();
      return roleDao.getUserRoles( tenant, username );
    };
  }

  public QuickSearchService( SearchClient client, Supplier<IPentahoSession> pentahoSessionSupplier, Supplier<Boolean> adminSupplier, Function<String, List<IPentahoRole>> rolesProvider ) {
    this( client );
    this.pentahoSessionSupplier = pentahoSessionSupplier;
    this.adminSupplier = adminSupplier;
    this.rolesProvider = rolesProvider;
  }

  public Result query( String filename, String extension ) {

    String username = pentahoSessionSupplier.get().getName();
    boolean isAdmin = adminSupplier.get();

    Must.Builder mustBuilder = new Must.Builder()
            .occurrence( new Wildcard( "name", filename.toLowerCase() ) );

    if ( !isAdmin ) {
      Should.Builder shouldBuilder = new Should.Builder()
              .occurrence( new Term( "users", username ) )
              .occurrence( new Term( "roles", "Authenticated" ) );

      for ( IPentahoRole role : rolesProvider.apply( username ) ) {
        shouldBuilder.occurrence( new Term( "roles", role.getName() ) );
      }

      Bool.Builder builder = new Bool.Builder().should( shouldBuilder.build() );
      mustBuilder.occurrence( new org.pentaho.platform.quicksearch.client.model.Bool( builder.build() ) );
    }
    if ( extension != null ) {
      mustBuilder.occurrence( new RegExp( "extension", extension ) );
    }

    Request request = new Request.Builder()
            .from( 0 )
            .size( 10 )
            .query( new Query.Builder()
                    .bool( new Bool.Builder()
                            .must( mustBuilder.build() )
                            .build() )
                    .build() )
            .sort( new Sort( "name", "asc" ) )
            .build();

    return client.search( request );
  }

  public void indexById( String id ) {
    client.indexById( id );
  }

  public void index() {
    client.index();
  }
}
