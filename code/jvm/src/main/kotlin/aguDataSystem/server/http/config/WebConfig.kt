package aguDataSystem.server.http.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Configuration for the web layer
 */
@Configuration
class WebConfig {

	/**
	 * Configures CORS
	 * TODO to be taken out in production, no need with docker setup
	 */
	@Bean
	fun corsConfigurer() = object : WebMvcConfigurer {
		override fun addCorsMappings(registry: CorsRegistry) {
			registry.addMapping("/**")
				.allowedOrigins("*")
				.allowedMethods("GET", "POST", "PUT", "DELETE")
		}
	}
}
