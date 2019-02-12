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
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.quicksearch.result.SearchResult;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.job.JobGraph;

import java.util.function.Supplier;

/**
 * Created by bmorrise on 4/17/18.
 */
public class JobEntrySpecialResult implements SearchResult {

  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;
  private JobEntryCopy jobEntryCopy;
  private Image image;

  public JobEntrySpecialResult( JobEntryCopy jobEntryCopy, Image image ) {
    this.jobEntryCopy = jobEntryCopy;
    this.image = image;
  }

  @Override
  public String getId() {
    return null;
  }

  @Override
  public String getName() {
    return jobEntryCopy.getName();
  }

  @Override
  public String getDescription() {
    return jobEntryCopy.getDescription();
  }

  @Override
  public void execute() {
    JobGraph jobGraph = spoonSupplier.get().getActiveJobGraph();
    if ( jobGraph != null ) {
      jobGraph.addJobEntryToChain( jobEntryCopy.getName(), false );
    }
  }

  @Override
  public Image getImage() {
    return image;
  }
}
