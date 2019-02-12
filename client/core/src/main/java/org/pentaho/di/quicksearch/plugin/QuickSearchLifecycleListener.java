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

package org.pentaho.di.quicksearch.plugin;

import org.pentaho.di.core.annotations.LifecyclePlugin;
import org.pentaho.di.core.lifecycle.LifeEventHandler;
import org.pentaho.di.core.lifecycle.LifecycleException;
import org.pentaho.di.core.lifecycle.LifecycleListener;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.quicksearch.service.QuickSearchService;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 4/2/18.
 */
@LifecyclePlugin(id = "QuickSearchLifecycleListener")
public class QuickSearchLifecycleListener implements LifecycleListener {

  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;
  private final List<QuickSearchService> services;

  public QuickSearchLifecycleListener( List<QuickSearchService> services ) {
    this.services = services;
  }

  @Override
  public void onStart( LifeEventHandler lifeEventHandler ) throws LifecycleException {
    for ( QuickSearchService service : services ) {
      service.init();
    }
  }

  @Override
  public void onExit( LifeEventHandler lifeEventHandler ) throws LifecycleException {

  }
}
