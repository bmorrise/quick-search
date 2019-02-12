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

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.quicksearch.result.SearchResult;
import org.pentaho.di.quicksearch.service.QuickSearchService;
import org.pentaho.di.quicksearch.service.ResponseListener;
import org.pentaho.di.quicksearch.service.SearchOptions;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.quicksearch.result.RepositoryResult;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 4/16/18.
 */
public class RepositorySearchService implements QuickSearchService {

  private static final Class<?> PKG = RepositorySearchService.class;
  public static final String NAME = "REPOSITORY_SEARCH_SERVICE";

  private RepositoriesMeta repositoriesMeta;
//  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;

  public RepositorySearchService() {
     repositoriesMeta = new RepositoriesMeta();
     try {
       repositoriesMeta.readData();
     } catch ( KettleException ignored ) {

     }
  }

  @Override
  public void search( String term, SearchOptions searchOptions, ResponseListener responseListener ) {
    int count = repositoriesMeta.nrRepositories();
    List<SearchResult> searchResults = new ArrayList<>();
    for ( int i = 0; i < count; i++ ) {
      RepositoryMeta repositoryMeta = repositoriesMeta.getRepository( i );
      if ( repositoryMeta.getName().toLowerCase().contains( term.toLowerCase() ) ) {
        searchResults.add( new RepositoryResult( repositoryMeta ) );
      }
    }
    responseListener.call( searchResults );
  }

  @Override
  public boolean isAvailable() {
//    return spoonSupplier.get().rep == null; // Add this line once the kettle extension point is available
    return false;
  }

  @Override
  public String getLabel() {
    return BaseMessages.getString( PKG, "quicksearch.repositories.service.label" );
  }

  @Override
  public int rank() {
    return 5;
  }

  @Override
  public void init() {

  }

  @Override
  public String getName() {
    return NAME;
  }
}
