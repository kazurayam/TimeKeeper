package com.kazurayam.timekeeper;

public class ReportOptions {

    public static final ReportOptions DEFAULT =
            new ReportOptions.Builder().build();

    private final Boolean requireDescription;

    private final Boolean requireLegend;

    private final Boolean requireGraph;

    private ReportOptions(Builder b) {
        this.requireDescription = b.requireDescription;
        this.requireLegend = b.requireLegend;
        this.requireGraph = b.requireGraph;
    }

    public Boolean requireDescription() {
        return this.requireDescription;
    }

    public Boolean requireLegend() {
        return this.requireLegend;
    }

    public Boolean requireGraph() {
        return this.requireGraph;
    }

    public static class Builder {
        private Boolean requireDescription;
        private Boolean requireLegend;
        private Boolean requireGraph;

        public Builder() {
            this.requireDescription = true;
            this.requireLegend = true;
            this.requireGraph = true;
        }

        public Builder noDescription() {
            this.requireDescription = false;
            return this;
        }

        public Builder noLegend() {
            this.requireLegend = false;
            return this;
        }

        public Builder noGraph() {
            this.requireGraph = false;
            return this;
        }

        public ReportOptions build() {
            return new ReportOptions(this);
        }
    }
}
