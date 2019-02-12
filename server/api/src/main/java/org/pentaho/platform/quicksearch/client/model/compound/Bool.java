package org.pentaho.platform.quicksearch.client.model.compound;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.pentaho.platform.quicksearch.client.model.compound.occurrence.Must;
import org.pentaho.platform.quicksearch.client.model.compound.occurrence.Occurrence;
import org.pentaho.platform.quicksearch.client.model.compound.occurrence.Should;

/**
 * Created by bmorrise on 3/20/18.
 */
public class Bool implements Occurrence {

  @JsonInclude( JsonInclude.Include.NON_NULL )
  private Must must;

  @JsonInclude( JsonInclude.Include.NON_NULL )
  private Should should;

  public Bool( Builder builder ) {
    this.must = builder.must;
    this.should = builder.should;
  }

  public Must getMust() {
    return must;
  }

  public void setMust( Must must ) {
    this.must = must;
  }

  public Should getShould() {
    return should;
  }

  public void setShould( Should should ) {
    this.should = should;
  }

  public static class Builder {
    private Must must;
    private Should should;

    public Builder must( Must must ) {
      this.must = must;
      return this;
    }

    public Builder should( Should should ) {
      this.should = should;
      return this;
    }

    public Bool build() {
      return new Bool( this );
    }
  }
}
