package com.localmart.history;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_history")
@Data
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_history_id")
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "search_query", nullable = false)
    private String searchQuery;

    @Column(name = "filters", columnDefinition = "json")
    private String filters;

    @Column(name = "result_count")
    private Integer resultCount;

    @Column(name = "searched_at")
    private LocalDateTime searchedAt = LocalDateTime.now();
}
