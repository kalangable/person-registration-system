package com.akstack.foundation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Aplicação principal do Foundation Service.
 * 
 * <p>Este serviço implementa o padrão Party Model para gerenciamento
 * unificado de pessoas físicas e jurídicas.</p>
 * 
 * <p><strong>FASE 1</strong>: API REST monolítica com:</p>
 * <ul>
 *   <li>Party Model (3 tabelas: party, person, organization)</li>
 *   <li>CRUD operations com validação</li>
 *   <li>Um documento de identificação primário por party</li>
 *   <li>Soft delete e trilha de auditoria</li>
 * </ul>
 * 
 * @author AkStack Team
 * @version 1.0.0-SNAPSHOT
 * @since 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class FoundationApplication {

    /**
     * Método principal para iniciar a aplicação Spring Boot.
     * 
     * @param args argumentos da linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(FoundationApplication.class, args);
    }

}
