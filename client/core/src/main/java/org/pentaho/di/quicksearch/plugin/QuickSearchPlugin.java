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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.quicksearch.service.FileSearchService;
import org.pentaho.di.quicksearch.service.QuickSearchService;
import org.pentaho.di.quicksearch.ui.QuickSearchDialog;
import org.pentaho.di.ui.core.dialog.ShowMessageDialog;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.SpoonLifecycleListener;
import org.pentaho.di.ui.spoon.SpoonPerspective;
import org.pentaho.di.ui.spoon.SpoonPlugin;
import org.pentaho.di.ui.spoon.SpoonPluginCategories;
import org.pentaho.di.ui.spoon.SpoonPluginInterface;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 3/21/18.
 */
@SpoonPlugin( id = "quicksearch-plugin", image = "" )
@SpoonPluginCategories( {"spoon"} )
public class QuickSearchPlugin extends AbstractXulEventHandler implements SpoonPluginInterface {

  private static final Class<?> PKG = QuickSearchPlugin.class;

  private static final String SPOON_CATEGORY = "spoon";
  private QuickSearchDialog quickSearchDialog;
  final List<QuickSearchService> services;
  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;

  public QuickSearchPlugin( List<QuickSearchService> services ) {
    this.services = services;
  }

  @Override
  public void applyToContainer( String category, XulDomContainer xulDomContainer ) throws XulException {
    if ( category.equals( SPOON_CATEGORY ) ) {
      xulDomContainer.registerClassLoader( getClass().getClassLoader() );
      xulDomContainer.loadOverlay( "spoon_overlay.xul" );
      setName( "quickSearchMenuHandler" );
      xulDomContainer.addEventHandler( this );
      Spoon.getInstance().enableMenus();
    }
  }

  public void openQuickSearch() {
    if ( quickSearchDialog == null || quickSearchDialog.isDisposed() ) {
      quickSearchDialog = new QuickSearchDialog( Spoon.getInstance().getShell(), services );
      quickSearchDialog.open();
    }
  }

  public void initQuickSearch() {
    if ( spoonSupplier.get().rep != null && spoonSupplier.get().rep.getUserInfo().isAdmin() ) {
      QuickSearchService service = services.stream().filter( quickSearchService -> quickSearchService.getName()
              .equals( FileSearchService.NAME ) ).findFirst().orElse( null );
      if ( service != null ) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit( () -> {
          Display display = spoonSupplier.get().getShell().getDisplay();
          display.asyncExec( () -> new ShowMessageDialog( spoonSupplier.get().getShell(), SWT.ICON_INFORMATION | SWT
                  .OK, BaseMessages.getString( PKG, "QuickSearch.Index.Title" ), BaseMessages.getString( PKG,
                  "QuickSearch.Index.Message" ) ).open() );
          service.init();
          display.asyncExec( () -> new ShowMessageDialog( spoonSupplier.get().getShell(), SWT.ICON_INFORMATION | SWT
                  .OK, BaseMessages.getString( PKG, "QuickSearch.IndexComplete.Title" ), BaseMessages.getString( PKG,
                  "QuickSearch.IndexComplete.Message" ) ).open() );
        } );
      } else {
        new ShowMessageDialog( spoonSupplier.get().getShell(), SWT.ICON_ERROR | SWT.OK, BaseMessages.getString( PKG,
                "QuickSearch.NotFound.Title" ), BaseMessages.getString( PKG, "QuickSearch.NotFound.Message" ) ).open();
      }
    } else {
      new ShowMessageDialog( spoonSupplier.get().getShell(), SWT.ICON_ERROR | SWT.OK, BaseMessages.getString( PKG,
              "QuickSearch.NonAdmin.Title" ), BaseMessages.getString( PKG, "QuickSearch.NonAdmin.Message" ) ).open();
    }
  }

  @Override
  public SpoonLifecycleListener getLifecycleListener() {
    return null;
  }

  @Override
  public SpoonPerspective getPerspective() {
    return null;
  }

}
