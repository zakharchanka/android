/*
 * Copyright (C) 2019 The Android Open Source Project
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
package com.android.tools.adtui.actions

import com.android.tools.adtui.ZOOMABLE_KEY
import com.intellij.openapi.actionSystem.AnActionEvent

object ZoomActualAction : SetZoomAction(ZoomType.ACTUAL) {
  // TODO: register shortcuts
  override fun update(event: AnActionEvent) {
    super.update(event)
    val zoomable = event.getData(ZOOMABLE_KEY)
    event.presentation.isEnabled = zoomable?.let {
      // TODO: add canZoomActual method to Zoomable instead of trying infer if the action is enabled.
      (zoomable.canZoomIn() && zoomable.scale < 1) || (zoomable.canZoomOut() && zoomable.scale > 1)
    }?: false
  }
}