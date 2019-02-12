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
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPointHandler;
import org.pentaho.di.core.extension.KettleExtensionPoint;
import org.pentaho.di.quicksearch.result.SearchResult;
import org.pentaho.di.quicksearch.ui.util.Images;
import org.pentaho.di.repository.RepositoryMeta;

/**
 * Created by bmorrise on 4/16/18.
 */
public class RepositoryResult implements SearchResult {

  private Image REPO_IMAGE = Images.getImage( RepositoryResult.class, "slave.svg", 24 );

  private RepositoryMeta repositoryMeta;

  public RepositoryResult( RepositoryMeta repositoryMeta ) {
    this.repositoryMeta = repositoryMeta;
  }

  @Override
  public String getId() {
    return null;
  }

  @Override
  public String getName() {
    return repositoryMeta.getName();
  }

  @Override
  public String getDescription() {
    return repositoryMeta.getName();
  }

  @Override
  public void execute() {
    try {
      ExtensionPointHandler.callExtensionPoint( null, KettleExtensionPoint.RequestLoginToRepository.id,
              repositoryMeta );
    } catch ( KettleException ignored ) {

    }
  }

  @Override
  public Image getImage() {
    return REPO_IMAGE;
  }
}
