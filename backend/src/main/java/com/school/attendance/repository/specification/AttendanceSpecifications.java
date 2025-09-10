package com.school.attendance.repository.specification;

import com.school.attendance.entity.AttendanceRecord;
import com.school.attendance.entity.AttendanceRecord.AttendanceStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;
import java.time.LocalDate;

/**
 * JPA Specifications for optimized attendance queries
 * Helps avoid N+1 queries and provides type-safe query building
 */
public class AttendanceSpecifications {

    public static Specification<AttendanceRecord> hasStudentId(Long studentId) {
        return (root, query, criteriaBuilder) ->
                studentId == null ? null : criteriaBuilder.equal(root.get("student").get("id"), studentId);
    }

    public static Specification<AttendanceRecord> hasTeacherId(Long teacherId) {
        return (root, query, criteriaBuilder) ->
                teacherId == null ? null : criteriaBuilder.equal(root.get("teacher").get("id"), teacherId);
    }

    public static Specification<AttendanceRecord> hasDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return null;
            }
            if (startDate == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate);
            }
            if (endDate == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate);
            }
            return criteriaBuilder.between(root.get("date"), startDate, endDate);
        };
    }

    public static Specification<AttendanceRecord> hasDate(LocalDate date) {
        return (root, query, criteriaBuilder) ->
                date == null ? null : criteriaBuilder.equal(root.get("date"), date);
    }

    public static Specification<AttendanceRecord> hasStatus(AttendanceStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<AttendanceRecord> hasClassId(Long classId) {
        return (root, query, criteriaBuilder) ->
                classId == null ? null : criteriaBuilder.equal(root.get("student").get("classEntity").get("id"), classId);
    }

    /**
     * Optimized specification with JOIN FETCH to avoid N+1 queries
     */
    public static Specification<AttendanceRecord> withStudentAndTeacher() {
        return (root, query, criteriaBuilder) -> {
            if (query.getResultType() != Long.class) {
                root.fetch("student", JoinType.LEFT);
                root.fetch("teacher", JoinType.LEFT);
            }
            return null;
        };
    }

    /**
     * Complex specification for attendance summary queries
     */
    public static Specification<AttendanceRecord> forAttendanceSummary(Long studentId, LocalDate startDate, LocalDate endDate) {
        return Specification.where(hasStudentId(studentId))
                .and(hasDateBetween(startDate, endDate))
                .and(withStudentAndTeacher());
    }

    /**
     * Specification for daily attendance report
     */
    public static Specification<AttendanceRecord> forDailyReport(LocalDate date, Long classId) {
        return Specification.where(hasDate(date))
                .and(hasClassId(classId))
                .and(withStudentAndTeacher());
    }
}
