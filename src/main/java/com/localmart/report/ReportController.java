package com.localmart.report;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportRepository reportRepository;

    @GetMapping
    public List<Report> getReports(@RequestParam(required = false) Report.ReporterType reporterType,
                                   @RequestParam(required = false) Report.Status status) {
        if (reporterType != null) {
            return reportRepository.findByReporterTypeOrderByCreatedAtDesc(reporterType);
        }
        if (status != null) {
            return reportRepository.findByStatusOrderByCreatedAtDesc(status);
        }
        return reportRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Report> submitReport(@RequestBody Report report) {
        if (report.getStatus() == null) {
            report.setStatus(Report.Status.open);
        }
        // If the user is authenticated as a customer, record their id as reporterId.
        // In this simple environment without security context, keep existing reporterId if provided.
        // TODO: integrate with authentication to set reporterId automatically when available.
        return ResponseEntity.ok(reportRepository.save(report));
    }
}
