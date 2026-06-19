package com.lnf.sentinel.domain;

import com.lnf.sentinel.domain.enums.Severity;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/** Pure unit test (no Spring context): SLA targets per severity. */
class SeverityTest {

    @Test
    void slaTargetsMatchPolicy() {
        assertThat(Severity.S1_CRITICAL.slaTarget()).isEqualTo(Duration.ofHours(4));
        assertThat(Severity.S2_HIGH.slaTarget()).isEqualTo(Duration.ofHours(8));
        assertThat(Severity.S3_MEDIUM.slaTarget()).isEqualTo(Duration.ofHours(24));
        assertThat(Severity.S4_LOW.slaTarget()).isEqualTo(Duration.ofHours(72));
    }
}
