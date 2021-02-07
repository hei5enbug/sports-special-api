package com.bongmany.sportsspecialapi.config

import com.bongmany.sportsspecialapi.SecurityInformation
import org.jasypt.encryption.StringEncryptor
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JasyptConfig {

    @Bean("jasyptStringEncryptor")
    fun stringEncrptor(): StringEncryptor {
        val encryptor = PooledPBEStringEncryptor()
        val config = SimpleStringPBEConfig()
        config.password = SecurityInformation.JasyptKey
        config.poolSize = 1
        encryptor.setConfig(config)
        return encryptor
    }
}