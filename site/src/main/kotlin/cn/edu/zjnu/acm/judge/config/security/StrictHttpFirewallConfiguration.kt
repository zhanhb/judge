package cn.edu.zjnu.acm.judge.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.web.firewall.StrictHttpFirewall

/**
 * @author zhanhb
 */
@Configuration
class StrictHttpFirewallConfiguration {

    @Bean
    fun httpFirewall(): StrictHttpFirewall {
        val firewall = StrictHttpFirewall()
        firewall.setAllowSemicolon(true)
        firewall.setAllowUrlEncodedPercent(true)
        return firewall
    }

}
