package org.pentaho.platform.quicksearch.client.model;

/**
 * Created by bmorrise on 1/31/19.
 */
public abstract  class Single<T> {

  public Single( T value ) {
    this.value = value;
  }

  private T value;

  public T getValue() {
    return value;
  }

  public void setValue( T value ) {
    this.value = value;
  }

  public abstract String getType();
}
