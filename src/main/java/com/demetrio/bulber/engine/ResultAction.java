package com.demetrio.bulber.engine;

import java.util.List;

public class ResultAction {

    private String command;
    private List<Runnable> uiTasks;

    public ResultAction(String command, List<Runnable> uiTasks) {
        this.command = command;
        this.uiTasks = uiTasks;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<Runnable> getUiTasks() {
        return uiTasks;
    }

    public void setUiTasks(List<Runnable> uiTasks) {
        this.uiTasks = uiTasks;
    }
}
