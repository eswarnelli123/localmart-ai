package com.localmart.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByReporterTypeOrderByCreatedAtDesc(Report.ReporterType reporterType);
    List<Report> findByStatusOrderByCreatedAtDesc(Report.Status status);
}
