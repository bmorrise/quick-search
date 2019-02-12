package org.pentaho.di.quicksearch.ui.util;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Created by bmorrise on 3/28/18.
 */
public class Colors {

  public static Color LIGHT_GRAY = getGray();

  public static Color WHITE = getWhite();

  public static Color getGray() {
    return new Color( Display.getCurrent(), 245, 245, 245 );
  }

  public static Color getWhite() {
    return new Color( Display.getCurrent(), 255, 255, 255 );
  }
}
