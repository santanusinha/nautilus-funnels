package io.appform.nautilus.funnel.tenant;

import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * Detailed information about tenant
 */
@Data
@Builder
@ToString
@EqualsAndHashCode
public class TenantInfo {
    private Map<String, Map<String, String>> attributes;
    @Singular
    private List<String> states;

}
