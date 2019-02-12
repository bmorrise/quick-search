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

package org.pentaho.platform.repository.quicksearch.lifecycle;

import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPlatformReadyListener;
import org.pentaho.platform.api.engine.IPluginLifecycleListener;
import org.pentaho.platform.api.engine.PluginLifecycleException;
import org.pentaho.platform.api.engine.security.userroledao.IPentahoRole;
import org.pentaho.platform.api.engine.security.userroledao.IUserRoleDao;
import org.pentaho.platform.api.mt.ITenant;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.StandaloneSession;
import org.pentaho.platform.engine.core.system.TenantUtils;
import org.pentaho.platform.engine.core.system.UserSession;
import org.pentaho.platform.repository.quicksearch.client.elasticsearch.ElasticSearchClient;
import org.pentaho.platform.repository.quicksearch.service.QuickSearchService;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 3/20/18.
 */
public class QuickSearchLifecycleListener implements IPluginLifecycleListener, IPlatformReadyListener {

  private ElasticSearchClient elasticSearchClient;

  public QuickSearchLifecycleListener() {
    this.elasticSearchClient = new ElasticSearchClient();
  }

  @Override
  public void ready() throws PluginLifecycleException {
    elasticSearchClient.setup();
    elasticSearchClient.index();
  }

  @Override
  public void init() throws PluginLifecycleException {

  }

  @Override
  public void loaded() throws PluginLifecycleException {

  }

  @Override
  public void unLoaded() throws PluginLifecycleException {

  }
}
