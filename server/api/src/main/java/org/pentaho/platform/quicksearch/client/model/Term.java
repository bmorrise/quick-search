package org.pentaho.platform.quicksearch.client.model;

import org.pentaho.platform.quicksearch.client.model.compound.occurrence.Occurrence;

/**
 * Created by bmorrise on 3/21/18.
 */
public class Term extends ParamType implements Occurrence {
  public Term( String key, String value ) {
    super( key, value );
  }

  @Override
  public String getType() {
    return "term";
  }
}
