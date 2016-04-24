package com.example.inspection.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sur.Vival on 13/3/2016.
 */
public class RecentJob {
    private Processing processing;
    private History history;
    private List<Processing> processings;
    private List<History> histories;

    public RecentJob() {
        processings = new ArrayList<>(100);
        histories = new ArrayList<>(100);
    }

    public Processing getProcessing() {
        return processing;
    }

    public void setProcessing(Processing processing) {
        this.processing = processing;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public RecentJob(History history, Processing processing) {

        this.history = history;
        this.processing = processing;
    }

    public RecentJob(List<Processing> processings, List<History> histories) {
        this.processings = processings;
        this.histories = histories;
    }

    public List<Processing> getProcessings() {
        return processings;
    }

    public void setProcessings(List<Processing> processings) {
        this.processings = processings;
    }

    public List<History> getHistories() {
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }
}
