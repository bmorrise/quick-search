package org.pentaho.di.quicksearch.service;

/**
 * Created by bmorrise on 3/22/18.
 */
public interface QuickSearchService {
  void search( String term, SearchOptions searchOptions, ResponseListener responseListener );
  String getName();
  boolean isAvailable();
  String getLabel();
  int rank();
  void init();
}
