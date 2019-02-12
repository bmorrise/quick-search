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

package org.pentaho.di.quicksearch.result;

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.quicksearch.ui.util.Images;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.trans.TransGraph;

import java.util.function.Supplier;

/**
 * Created by bmorrise on 4/2/18.
 */
public class StepResult implements SearchResult {

  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;
  private PluginInterface pluginInterface;

  public StepResult( PluginInterface pluginInterface ) {
    this.pluginInterface = pluginInterface;
  }

  @Override
  public String getId() {
    return null;
  }

  @Override
  public String getName() {
    return pluginInterface.getName();
  }

  @Override
  public String getDescription() {
    return pluginInterface.getDescription();
  }

  @Override
  public void execute() {
    TransGraph transGraph = spoonSupplier.get().getActiveTransGraph();
    if ( transGraph != null ) {
      transGraph.addStepToChain( pluginInterface, false );
    }
  }

  @Override
  public Image getImage() {
    ClassLoader classLoader = null;
    try {
      classLoader = PluginRegistry.getInstance().getClassLoader( pluginInterface );
    } catch ( KettlePluginException e ) {
      // Just let it fail
    }
    return Images.getImage( classLoader, pluginInterface.getImageFile(), 24 );
  }
}
