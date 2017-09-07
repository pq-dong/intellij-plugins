package com.jetbrains.lang.dart;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.jetbrains.lang.dart.analyzer.DartAnalysisServerService;
import com.jetbrains.lang.dart.projectWizard.DartModuleBuilder;
import com.jetbrains.lang.dart.sdk.DartSdk;
import com.jetbrains.lang.dart.sdk.DartSdkLibUtil;
import org.jetbrains.annotations.NotNull;


public class DartDumbAwareStartupActivity implements StartupActivity, DumbAware {
  @Override
  public void runActivity(@NotNull final Project project) {
    if (DartModuleBuilder.isPubGetScheduledForNewlyCreatedProject(project)) {
      // We want to start Analysis Server after initial 'pub get' is finished, this will be done in DartPubActionBase
      return;
    }

    final DartSdk sdk = DartSdk.getDartSdk(project);
    if (sdk == null) return;

    for (final Module module : ModuleManager.getInstance(project).getModules()) {
      if (DartSdkLibUtil.isDartSdkEnabled(module)) {
        ReadAction.run(() -> DartAnalysisServerService.getInstance(project).serverReadyForRequest(project));
        break;
      }
    }
  }
}
