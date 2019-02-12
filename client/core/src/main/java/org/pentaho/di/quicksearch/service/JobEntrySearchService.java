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

import org.pentaho.di.core.plugins.JobEntryPluginType;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.quicksearch.result.SearchResult;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.quicksearch.result.JobEntryResult;
import org.pentaho.di.quicksearch.result.JobEntrySpecialResult;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by bmorrise on 4/2/18.
 */
public class JobEntrySearchService implements QuickSearchService {

  private static final Class<?> PKG = JobEntrySearchService.class;
  private static final int MAX_RESULTS = 5;
  public static final String NAME = "JOB_ENTRY_SEARCH_SERVICE";

  private PluginRegistry pluginRegistry;
  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;
  private List<PluginInterface> search = new ArrayList<>();

  public JobEntrySearchService() {
    this.pluginRegistry = PluginRegistry.getInstance();
  }

  public JobEntrySearchService( PluginRegistry pluginRegistry ) {
    this.pluginRegistry = pluginRegistry;
  }

  @Override
  public void init() {
    final List<PluginInterface> baseEntries = pluginRegistry.getPlugins( JobEntryPluginType.class );
    final List<String> baseCategories = pluginRegistry.getCategories( JobEntryPluginType.class );
    for ( String baseCategory : baseCategories ) {
      search.addAll( baseEntries.stream().filter( baseEntry -> baseEntry.getCategory().equalsIgnoreCase( baseCategory
      ) ).sorted( Comparator.comparing( PluginInterface::getName ) ).collect( Collectors.toList() ) );
    }
  }

  @Override
  public void search( String term, SearchOptions searchOptions, ResponseListener responseListener ) {
    List<SearchResult> searchResults = new ArrayList<>();
    JobEntryCopy startEntry = JobMeta.createStartEntry();
    JobEntryCopy dummyEntry = JobMeta.createDummyEntry();
    if ( startEntry.getName().toLowerCase().contains( term.toLowerCase() ) ) {
      searchResults.add( new JobEntrySpecialResult( startEntry, GUIResource.getInstance().getImageStartMedium() ) );
    }
    if ( dummyEntry.getName().toLowerCase().contains( term.toLowerCase() ) ) {
      searchResults.add( new JobEntrySpecialResult( dummyEntry, GUIResource.getInstance().getImageDummyMedium() ) );
    }
    for ( PluginInterface plugin : search ) {
      if ( plugin.getName().toLowerCase().contains( term.toLowerCase() ) ) {
        SearchResult searchResult = new JobEntryResult( plugin );
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
    return spoonSupplier.get() != null && spoonSupplier.get().getActiveMeta() instanceof JobMeta;
  }

  @Override
  public String getLabel() {
    return BaseMessages.getString( PKG, "quicksearch.jobentries.service.label" );
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
