package org.pentaho.platform.quicksearch.client.model;

/**
 * Created by bmorrise on 3/20/18.
 */
public abstract class ParamType<T> {
  private String key;
  private T value;

  public ParamType( String key, T value ) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey( String key ) {
    this.key = key;
  }

  public T getValue() {
    return value;
  }

  public void setValue( T value ) {
    this.value = value;
  }

  public abstract String getType();
}
