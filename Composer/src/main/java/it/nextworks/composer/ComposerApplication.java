/*
* Copyright 2018 Nextworks s.r.l.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package it.nextworks.composer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

import it.nextworks.composer.plugins.catalogue.Catalogue;

@SpringBootApplication
@ComponentScan(basePackages = {"it.nextworks.composer","it.nextworks.sdk"})
@EntityScan({"it.nextworks.sdk", "it.nextworks.composer.plugins.catalogue"})
@EnableJpaRepositories("it.nextworks.composer.executor.repositories")
public class ComposerApplication {
	
	@Value("${crossorigin.origin}")
	public static String crossOrigin;
	
	
	@Value("${catalogue.host}")
	public String hostname;
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
	
	@Bean
	public Catalogue catalogue() {
		return new Catalogue("5g-catalogue", hostname, false, "admin", "admin");
	}
	
	public static void main(String[] args) {
		SpringApplication.run(ComposerApplication.class, args);
		
	}
}
