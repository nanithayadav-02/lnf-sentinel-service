package com.lnf.sentinel.service;

import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.enums.IssueStatus;
import com.lnf.sentinel.model.enums.Severity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/** Composable JPA Specifications used to filter issues. */
public final class IssueSpecifications {

    private IssueSpecifications() {}

    public static Specification<Issue> tenantId(Long tenantId) {
        return (root, query, cb) ->
                tenantId == null ? cb.conjunction() : cb.equal(root.get("tenantId"), tenantId);
    }

    public static Specification<Issue> status(IssueStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Issue> severity(Severity severity) {
        return (root, query, cb) ->
                severity == null ? cb.conjunction() : cb.equal(root.get("severity"), severity);
    }

    public static Specification<Issue> assigneeId(Long assigneeId) {
        return (root, query, cb) ->
                assigneeId == null ? cb.conjunction() : cb.equal(root.get("assigneeId"), assigneeId);
    }

    /** Case-insensitive match across key, title, description and affected service. */
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
            return cb.or(ors.toArray(new Predicate[0]));
        };
    }
}
