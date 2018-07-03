package me.alexand.scat.statistic.common.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс, описывающий доменное имя, которое необходимо отслеживать
 * Доменное имя задается в виде регулярного выражения стадарта POSIX
 *
 * @author asidorov84@gmail.com
 */
public class TrackedDomain {
    private final String regexPattern;
    private final boolean isActive;
    private final LocalDateTime dateAdded;

    private TrackedDomain(TrackedDomain.Builder builder) {
        this.regexPattern = builder.regexPattern;
        this.isActive = builder.isActive;
        this.dateAdded = builder.dateAdded;
    }

    public static TrackedDomain.Builder builder() {
        return new TrackedDomain.Builder();
    }

    public String getRegexPattern() {
        return regexPattern;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackedDomain that = (TrackedDomain) o;
        return isActive == that.isActive &&
                Objects.equals(regexPattern, that.regexPattern) &&
                Objects.equals(dateAdded, that.dateAdded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regexPattern, isActive, dateAdded);
    }

    @Override
    public String toString() {
        return "TrackedDomain{" +
                "regexPattern='" + regexPattern + '\'' +
                ", isActive=" + isActive +
                ", dateAdded=" + dateAdded +
                '}';
    }

    public static class Builder {
        private String regexPattern;
        private boolean isActive;
        private LocalDateTime dateAdded;

        private Builder() {
        }

        public Builder regexPattern(String regexPattern) {
            this.regexPattern = regexPattern;
            return this;
        }

        public Builder active(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder dateAdded(LocalDateTime dateAdded) {
            this.dateAdded = dateAdded;
            return this;
        }

        public TrackedDomain build() {
            return new TrackedDomain(this);
        }
    }
}