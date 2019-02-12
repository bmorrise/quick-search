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

import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.quicksearch.result.SearchResult;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.quicksearch.result.StepResult;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by bmorrise on 4/2/18.
 */
public class StepSearchService implements QuickSearchService {

  private static final Class<?> PKG = StepSearchService.class;
  private static final int MAX_RESULTS = 5;
  public static final String NAME = "STEP_SEARCH_SERVICE";

  private PluginRegistry pluginRegistry;
  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;
  private List<PluginInterface> search = new ArrayList<>();

  public StepSearchService() {
    this.pluginRegistry = PluginRegistry.getInstance();
  }

  public StepSearchService( PluginRegistry pluginRegistry ) {
    this.pluginRegistry = pluginRegistry;
  }

  @Override
  public void init() {
    final List<PluginInterface> baseSteps = pluginRegistry.getPlugins( StepPluginType.class );
    final List<String> baseCategories = pluginRegistry.getCategories( StepPluginType.class );
    for ( String baseCategory : baseCategories ) {
      search.addAll( baseSteps.stream().filter( baseStep -> baseStep.getCategory().equalsIgnoreCase( baseCategory ) )
              .sorted( Comparator.comparing( PluginInterface::getName ) ).collect( Collectors.toList() ) );
    }
  }

  @Override
  public void search( String term, SearchOptions searchOptions, ResponseListener responseListener ) {
    List<SearchResult> searchResults = new ArrayList<>();
    for ( PluginInterface plugin : search ) {
      if ( plugin.getName().toLowerCase().contains( term.toLowerCase() ) ) {
        SearchResult searchResult = new StepResult( plugin );
        searchResults.add( searchResult );
        if ( searchResults.size() >= MAX_RESULTS ) {
          break;
        }
      }
    }
    responseListener.call( searchResults );
  }

  @Override
  public boolean isAvailable() {
    return spoonSupplier.get() != null && spoonSupplier.get().getActiveMeta() instanceof TransMeta;
  }

  @Override
  public String getLabel() {
    return BaseMessages.getString( PKG, "quicksearch.steps.service.label" );
  }

  @Override
  public int rank() {
    return 1;
  }

  @Override
  public String getName() {
    return NAME;
  }
}
