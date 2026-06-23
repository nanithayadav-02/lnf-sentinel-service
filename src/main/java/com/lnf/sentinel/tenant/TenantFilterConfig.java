package com.lnf.sentinel.tenant;

import com.lnf.tenant.core.filter.TenantFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TenantFilterConfig {

    @Value("${lnf.tenant.enabled}")
    private boolean tenantEnabled;
    @Value("${lnf.tenant.default}")
    private String defaultTenantId;
    @Value("${lnf.tenant.databaseName}")
    private String databaseName;
    private static final Long UNRESOLVABLE = -1L;

    @Bean
    public FilterRegistrationBean<TenantFilter> tenantFilter() {
        FilterRegistrationBean<TenantFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TenantFilter(tenantEnabled, defaultTenantId, databaseName));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

}