package com.demetrio.bulber.engine;

import com.demetrio.bulber.engine.bulb.Request;

import java.util.List;

public class ResultAction {

    private Request request;
    private List<Runnable> uiTasks;

    public ResultAction(Request request, List<Runnable> uiTasks) {
        this.request = request;
        this.uiTasks = uiTasks;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public List<Runnable> getUiTasks() {
        return uiTasks;
    }

    public void setUiTasks(List<Runnable> uiTasks) {
        this.uiTasks = uiTasks;
    }
}
