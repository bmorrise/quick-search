package org.pentaho.platform.quicksearch.client.model;

import org.pentaho.platform.quicksearch.client.model.compound.occurrence.Occurrence;

/**
 * Created by bmorrise on 1/30/19.
 */
public class Bool extends Single<org.pentaho.platform.quicksearch.client.model.compound.Bool> implements Occurrence {
  public Bool( org.pentaho.platform.quicksearch.client.model.compound.Bool value ) {
    super( value );
  }

  @Override
  public String getType() {
    return "bool";
  }
}
