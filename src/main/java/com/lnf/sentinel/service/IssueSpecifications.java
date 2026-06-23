package com.lnf.sentinel.service;

import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.enums.IssueStatus;
import com.lnf.sentinel.model.enums.Severity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class IssueSpecifications {

    private IssueSpecifications() {
    }

    public static Specification<Issue> tenantName(String tenantName) {
        return (root, query, cb) ->
                tenantName == null ? cb.conjunction() : cb.equal(root.get("tenantName"), tenantName);
    }

    public static Specification<Issue> tenantCode(String tenantCode) {
        return (root, query, cb) ->
                tenantCode == null ? cb.conjunction() : cb.equal(root.get("tenantCode"), tenantCode);
    }

    public static Specification<Issue> status(IssueStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Issue> severity(Severity severity) {
        return (root, query, cb) ->
                severity == null ? cb.conjunction() : cb.equal(root.get("severity"), severity);
    }

    public static Specification<Issue> assigneeId(UUID assigneeId) {
        return (root, query, cb) ->
                assigneeId == null ? cb.conjunction() : cb.equal(root.get("assigneeId"), assigneeId);
    }

    public static Specification<Issue> search(String term) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(term)) {
                return cb.conjunction();
            }
            String like = "%" + term.toLowerCase() + "%";
            List<Predicate> ors = new ArrayList<>();
            ors.add(cb.like(cb.lower(root.get("issueKey")), like));
            ors.add(cb.like(cb.lower(root.get("title")), like));
            ors.add(cb.like(cb.lower(root.get("description")), like));
            ors.add(cb.like(cb.lower(root.get("affectedService")), like));
            ors.add(cb.like(cb.lower(root.get("status")), like));
            ors.add(cb.like(cb.lower(root.get("severity")), like));
            ors.add(cb.like(cb.lower(root.get("summary")), like));
            ors.add(cb.like(cb.lower(root.get("tenantCode")), like));
            ors.add(cb.like(cb.lower(root.get("tenantName")), like));

            return cb.or(ors.toArray(new Predicate[0]));
        };
    }
}
