package de.dhbw.domain.report;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// these might be subject to change, as different types might be needed
public class Report {
    private UUID reportId;
    private ReportType type;
    private LocalDate generatedDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String title;
    private String summary;
    private Map<String, Object> data;
    private String generatedBy;

    public Report() {
        this.generatedDate = LocalDate.now();
        this.data = new HashMap<>();
    }

    public Report(UUID reportId, ReportType type, String title) {
        this();
        this.reportId = reportId;
        this.type = type;
        this.title = title;
    }

    // Getters and Setters
    public UUID getReportId() {
        return reportId;
    }

    public void setReportId(UUID reportId) {
        this.reportId = reportId;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDate generatedDate) {
        this.generatedDate = generatedDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public void addDataPoint(String key, Object value) {
        this.data.put(key, value);
    }

    public Object getDataPoint(String key) {
        return this.data.get(key);
    }
