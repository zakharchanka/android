/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tools.idea.gradle.structure.daemon.analysis

import com.android.tools.idea.gradle.structure.model.PsIssue
import com.android.tools.idea.gradle.structure.model.PsIssueCollection
import com.android.tools.idea.gradle.structure.model.PsModel
import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.intellij.util.ui.UIUtil

abstract class PsModelAnalyzer<T : PsModel>(parentDisposable: Disposable) {
  var disposed: Boolean = false

  init {
    Disposer.register(parentDisposable, Disposable { disposed = true })
  }

  abstract val supportedModelType: Class<T>

  fun analyze(model: T, issueCollection: PsIssueCollection) {
    assert(supportedModelType.isInstance(model))
    UIUtil.invokeAndWaitIfNeeded(Runnable {
      if (!disposed)
        analyze(supportedModelType.cast(model)).forEach { issueCollection.add(it) }
    })
  }

  abstract fun analyze(model: T): Sequence<PsIssue>
}
