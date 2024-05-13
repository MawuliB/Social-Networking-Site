package com.mawuli.sns;

import com.mawuli.sns.security.domain.entities.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import com.mawuli.sns.security.repositories.RoleRepository;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class SnsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnsApplication.class, args);
	}
		@Bean
		public CommandLineRunner runner (RoleRepository roleRepository){
			return args -> {
				if(roleRepository.findByName("USER").isEmpty()){
					roleRepository.save(Role.builder().name("USER").description("User role").build());
				}
			};
		}


}
