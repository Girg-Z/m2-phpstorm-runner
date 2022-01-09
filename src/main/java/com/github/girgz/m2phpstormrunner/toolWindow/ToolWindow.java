package com.github.girgz.m2phpstormrunner.toolWindow;

import com.github.girgz.m2phpstormrunner.ui.window.CacheStatus;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.intellij.execution.*;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.ui.treeStructure.Tree;
import com.jetbrains.php.run.script.PhpScriptRunConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class ToolWindow {

    private JPanel toolWindowContent;
    private JTree tree1;

    public JPanel getContent() {
        return toolWindowContent;
    }

    private void createUIComponents() {
        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("bin/magento");

        //create the child nodes
        DefaultMutableTreeNode cacheNode = new DefaultMutableTreeNode("Cache");
        cacheNode.add(new DefaultMutableTreeNode("clean"));
        cacheNode.add(new DefaultMutableTreeNode("flush"));
        cacheNode.add(new DefaultMutableTreeNode("status"));
        cacheNode.add(new DefaultMutableTreeNode("disable"));
        cacheNode.add(new DefaultMutableTreeNode("enable"));

        DefaultMutableTreeNode fruitNode = new DefaultMutableTreeNode("Setup");

        //add the child nodes to the root node
        root.add(cacheNode);
        root.add(fruitNode);

        //create the tree by passing in the root node
        Tree tree = new Tree(root);

        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                            tree.getLastSelectedPathComponent();
                    if (node == null) return;
                    Object nodeInfo = node.getUserObject();
                    // Cast nodeInfo to your object and do whatever you want
                    executeCommand2();
                }
            }
        });
        this.tree1 = tree;
    }

    private void executeCommand() {
        DataContext dataContext = DataManager.getInstance().getDataContext();
        Project project = (Project) dataContext.getData(PlatformDataKeys.PROJECT);
        assert project != null;

        ConfigurationType type = ConfigurationTypeUtil.findConfigurationType("PhpLocalRunConfigurationType");
        RunManager runManager = RunManager.getInstance(project);
        RunnerAndConfigurationSettings runnerAndConfigurationSettings = runManager.createRunConfiguration("Magento Runner", type.getConfigurationFactories()[0]);
        PhpScriptRunConfiguration conf = (PhpScriptRunConfiguration) runnerAndConfigurationSettings.getConfiguration();
        conf.getSettings().setPath("\\\\wsl$\\Ubuntu-20.04\\var\\www\\html\\test\\bin\\magento");

        // runnerAndConfigurationSettings.setActivateToolWindowBeforeRun(false); // Cool

        ProgramRunnerUtil.executeConfiguration(runnerAndConfigurationSettings, DefaultRunExecutor.getRunExecutorInstance());
    }

    private void executeCommand2() {
        DataContext dataContext = DataManager.getInstance().getDataContext();
        Project project = (Project) dataContext.getData(PlatformDataKeys.PROJECT);
        assert project != null;

        ConfigurationType type = ConfigurationTypeUtil.findConfigurationType("PhpLocalRunConfigurationType");
        RunManager runManager = RunManager.getInstance(project);
        RunnerAndConfigurationSettings runnerAndConfigurationSettings = runManager.createRunConfiguration("Magento Runner", type.getConfigurationFactories()[0]);

        Executor executor = DefaultRunExecutor.getRunExecutorInstance();
        PhpScriptRunConfiguration conf = (PhpScriptRunConfiguration) runnerAndConfigurationSettings.getConfiguration();
        conf.getSettings().setPath("\\\\wsl$\\Ubuntu-20.04\\var\\www\\html\\test\\bin\\magento");
        conf.getSettings().setScriptParameters("cache:status");
        ProgramRunner runner = RunnerRegistry.getInstance().getRunner(executor.getId(), conf);
        ExecutionEnvironment environment = new ExecutionEnvironment(executor, runner, runnerAndConfigurationSettings, project);

        try {
            runner.execute(environment, new Callback());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static class Callback implements ProgramRunner.Callback {

        @Override
        public void processStarted(RunContentDescriptor descriptor) {
            descriptor.getProcessHandler().addProcessListener(
                    new ProcessAdapter() {
                        private String fullOutput = "";

                        @Override
                        public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                            // This will be executed multiple times (1: input, 2: output, 3: exit code)
                            String text = event.getText();
                            if (outputType == ProcessOutputTypes.STDOUT) {
                                this.fullOutput += text;
                            }
                        }

                        @Override
                        public void processTerminated(@NotNull ProcessEvent event) {
                            // Todo: check exit code
                            parseCacheStatus(fullOutput);
                        }

                        private void parseCacheStatus(String processOutput) {
                            List<String> lines = Lists.newArrayList(Splitter.on("\n").split(processOutput));
                            lines.remove(0);
                            lines.remove(lines.size() - 1);
                            Map<String, Boolean> cacheStatus = new LinkedHashMap<>();
                            for (String line : lines) {
                                line = line.replaceAll("\\s", "");
                                String[] split = line.split(":");
                                cacheStatus.put(split[0], split[1].equals("1"));
                            }

                            // https://plugins.jetbrains.com/docs/intellij/general-threading-rules.html#modality-and-invokelater
                            // DialogWrapper can only be used in event dispatch thread
                            ApplicationManager.getApplication().invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new CacheStatus(cacheStatus).showAndGet();
                                }
                            });
                        }
                    }
            );
        }
    }
}

