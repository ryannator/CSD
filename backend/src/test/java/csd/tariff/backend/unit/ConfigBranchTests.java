package csd.tariff.backend.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import csd.tariff.backend.config.OpenApiConfig;
import csd.tariff.backend.config.SecurityConfig;
import csd.tariff.backend.security.JwtAuthFilter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;

@DisplayName("Configuration Branch Tests")
public class ConfigBranchTests {

    @Nested
    @DisplayName("OpenApiConfig Branch Tests")
    class OpenApiConfigBranchTests {

        @Test
        @DisplayName("Should test OpenAPI configuration branches")
        void openApiConfig_ShouldTestConfigurationBranches() {
            OpenApiConfig config = new OpenApiConfig();
            OpenAPI openAPI = config.api();
            
            assertNotNull(openAPI);
            assertNotNull(openAPI.getComponents());
            assertNotNull(openAPI.getComponents().getSecuritySchemes());
            
            // Test security scheme configuration
            SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("bearer-jwt");
            assertNotNull(securityScheme);
            assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
            assertEquals("bearer", securityScheme.getScheme());
            assertEquals("JWT", securityScheme.getBearerFormat());
        }

        @Test
        @DisplayName("Should handle different security scheme branches")
        void openApiConfig_ShouldHandleDifferentSecuritySchemeBranches() {
            OpenApiConfig config = new OpenApiConfig();
            OpenAPI openAPI = config.api();
            
            // Test that the security scheme is properly configured
            SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get("bearer-jwt");
            
            // Test HTTP type branch
            assertEquals(SecurityScheme.Type.HTTP, scheme.getType());
            
            // Test bearer scheme branch
            assertEquals("bearer", scheme.getScheme());
            
            // Test JWT bearer format branch
            assertEquals("JWT", scheme.getBearerFormat());
        }

        @Test
        @DisplayName("Should handle multiple OpenAPI bean calls")
        void openApiConfig_ShouldHandleMultipleBeanCalls() {
            OpenApiConfig config = new OpenApiConfig();
            
            // Test multiple calls to the same bean method
            OpenAPI openAPI1 = config.api();
            OpenAPI openAPI2 = config.api();
            
            // Both should be valid but different instances
            assertNotNull(openAPI1);
            assertNotNull(openAPI2);
            assertNotNull(openAPI1.getComponents());
            assertNotNull(openAPI2.getComponents());
            
            // Both should have the same security scheme configuration
            assertTrue(openAPI1.getComponents().getSecuritySchemes().containsKey("bearer-jwt"));
            assertTrue(openAPI2.getComponents().getSecuritySchemes().containsKey("bearer-jwt"));
        }

        @Test
        @DisplayName("Should test OpenAPI security requirements")
        void openApiConfig_ShouldTestSecurityRequirements() {
            OpenApiConfig config = new OpenApiConfig();
            OpenAPI openAPI = config.api();
            
            assertNotNull(openAPI);
            assertNotNull(openAPI.getSecurity());
            assertTrue(openAPI.getSecurity().size() > 0);
            
            // Test that security requirement contains the bearer-jwt scheme
            io.swagger.v3.oas.models.security.SecurityRequirement securityReq = openAPI.getSecurity().get(0);
            assertNotNull(securityReq);
            assertTrue(securityReq.containsKey("bearer-jwt"));
        }

        @Test
        @DisplayName("Should test OpenAPI components structure")
        void openApiConfig_ShouldTestComponentsStructure() {
            OpenApiConfig config = new OpenApiConfig();
            OpenAPI openAPI = config.api();
            
            assertNotNull(openAPI);
            assertNotNull(openAPI.getComponents());
            assertNotNull(openAPI.getComponents().getSecuritySchemes());
            
            // Test that security schemes map contains exactly one entry
            assertEquals(1, openAPI.getComponents().getSecuritySchemes().size());
            assertTrue(openAPI.getComponents().getSecuritySchemes().containsKey("bearer-jwt"));
        }

        @Test
        @DisplayName("Should test OpenAPI security scheme properties")
        void openApiConfig_ShouldTestSecuritySchemeProperties() {
            OpenApiConfig config = new OpenApiConfig();
            OpenAPI openAPI = config.api();
            
            SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get("bearer-jwt");
            assertNotNull(scheme);
            
            // Test all properties of the security scheme
            assertEquals("bearer-jwt", scheme.getName());
            assertEquals(SecurityScheme.Type.HTTP, scheme.getType());
            assertEquals("bearer", scheme.getScheme());
            assertEquals("JWT", scheme.getBearerFormat());
            
            // Test that scheme is properly configured for HTTP bearer authentication
            assertNotNull(scheme.getType());
            assertNotNull(scheme.getScheme());
            assertNotNull(scheme.getBearerFormat());
        }

        @Test
        @DisplayName("Should test OpenAPI with different config instances")
        void openApiConfig_ShouldTestWithDifferentConfigInstances() {
            OpenApiConfig config1 = new OpenApiConfig();
            OpenApiConfig config2 = new OpenApiConfig();
            
            OpenAPI openAPI1 = config1.api();
            OpenAPI openAPI2 = config2.api();
            
            assertNotNull(openAPI1);
            assertNotNull(openAPI2);
            
            // Both should have identical structure
            assertNotNull(openAPI1.getComponents());
            assertNotNull(openAPI2.getComponents());
            assertNotNull(openAPI1.getComponents().getSecuritySchemes());
            assertNotNull(openAPI2.getComponents().getSecuritySchemes());
            
            // Both should have the same security scheme
            assertTrue(openAPI1.getComponents().getSecuritySchemes().containsKey("bearer-jwt"));
            assertTrue(openAPI2.getComponents().getSecuritySchemes().containsKey("bearer-jwt"));
            
            // Both should have security requirements
            assertNotNull(openAPI1.getSecurity());
            assertNotNull(openAPI2.getSecurity());
            assertTrue(openAPI1.getSecurity().size() > 0);
            assertTrue(openAPI2.getSecurity().size() > 0);
        }

        @Test
        @DisplayName("Should test OpenAPI bean method consistency")
        void openApiConfig_ShouldTestBeanMethodConsistency() {
            OpenApiConfig config = new OpenApiConfig();
            
            // Call the bean method multiple times
            OpenAPI openAPI1 = config.api();
            OpenAPI openAPI2 = config.api();
            OpenAPI openAPI3 = config.api();
            
            // All should be valid
            assertNotNull(openAPI1);
            assertNotNull(openAPI2);
            assertNotNull(openAPI3);
            
            // All should have the same structure
            assertNotNull(openAPI1.getComponents());
            assertNotNull(openAPI2.getComponents());
            assertNotNull(openAPI3.getComponents());
            
            // All should have security schemes
            assertTrue(openAPI1.getComponents().getSecuritySchemes().containsKey("bearer-jwt"));
            assertTrue(openAPI2.getComponents().getSecuritySchemes().containsKey("bearer-jwt"));
            assertTrue(openAPI3.getComponents().getSecuritySchemes().containsKey("bearer-jwt"));
            
            // All should have security requirements
            assertNotNull(openAPI1.getSecurity());
            assertNotNull(openAPI2.getSecurity());
            assertNotNull(openAPI3.getSecurity());
        }
    }

    @Nested
    @DisplayName("SecurityConfig Branch Tests")
    class SecurityConfigBranchTests {

        private final JwtAuthFilter jwtAuthFilter = mock(JwtAuthFilter.class);

        @Test
        @DisplayName("Should test SecurityConfig constructor branches")
        void securityConfig_ShouldTestConstructorBranches() {
            // Test constructor with valid JwtAuthFilter
            SecurityConfig config1 = new SecurityConfig(jwtAuthFilter);
            assertNotNull(config1);

            // Test constructor with null JwtAuthFilter
            SecurityConfig config2 = new SecurityConfig(null);
            assertNotNull(config2);

            // Both should be able to create CORS configuration
            CorsConfigurationSource corsSource1 = config1.corsConfigurationSource();
            CorsConfigurationSource corsSource2 = config2.corsConfigurationSource();

            assertNotNull(corsSource1);
            assertNotNull(corsSource2);

            // Both should return identical CORS configurations
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");
            CorsConfiguration corsConfig1 = corsSource1.getCorsConfiguration(request);
            CorsConfiguration corsConfig2 = corsSource2.getCorsConfiguration(request);

            assertNotNull(corsConfig1);
            assertNotNull(corsConfig2);

            assertEquals(corsConfig1.getAllowedOrigins(), corsConfig2.getAllowedOrigins());
            assertEquals(corsConfig1.getAllowedMethods(), corsConfig2.getAllowedMethods());
            assertEquals(corsConfig1.getAllowedHeaders(), corsConfig2.getAllowedHeaders());
            assertEquals(corsConfig1.getAllowCredentials(), corsConfig2.getAllowCredentials());
        }

        @Test
        @DisplayName("Should test SecurityConfig with different request paths")
        void securityConfig_ShouldTestWithDifferentRequestPaths() {
            SecurityConfig config = new SecurityConfig(jwtAuthFilter);
            CorsConfigurationSource corsSource = config.corsConfigurationSource();

            // Test different request paths
            String[] paths = {"/api/test", "/api/users", "/auth/login", "/swagger-ui/index.html"};
            
            for (String path : paths) {
                MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
                CorsConfiguration corsConfig = corsSource.getCorsConfiguration(request);
                
                assertNotNull(corsConfig);
                assertTrue(corsConfig.getAllowedOrigins().contains("http://localhost:5173"));
                assertTrue(corsConfig.getAllowedMethods().contains("GET"));
                assertTrue(corsConfig.getAllowedHeaders().contains("*"));
                assertTrue(corsConfig.getAllowCredentials());
            }
        }

        @Test
        @DisplayName("Should test SecurityConfig with different HTTP methods")
        void securityConfig_ShouldTestWithDifferentHttpMethods() {
            SecurityConfig config = new SecurityConfig(jwtAuthFilter);
            CorsConfigurationSource corsSource = config.corsConfigurationSource();

            // Test different HTTP methods
            String[] methods = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};
            
            for (String method : methods) {
                MockHttpServletRequest request = new MockHttpServletRequest(method, "/api/test");
                CorsConfiguration corsConfig = corsSource.getCorsConfiguration(request);
                
                assertNotNull(corsConfig);
                assertTrue(corsConfig.getAllowedOrigins().contains("http://localhost:5173"));
                assertTrue(corsConfig.getAllowedMethods().contains(method));
                assertTrue(corsConfig.getAllowedHeaders().contains("*"));
                assertTrue(corsConfig.getAllowCredentials());
            }
        }

        @Test
        @DisplayName("Should test SecurityConfig with different headers")
        void securityConfig_ShouldTestWithDifferentHeaders() {
            SecurityConfig config = new SecurityConfig(jwtAuthFilter);
            CorsConfigurationSource corsSource = config.corsConfigurationSource();

            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");
            
            // Test with different headers
            request.addHeader("Authorization", "Bearer token123");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Accept", "application/json");
            
            CorsConfiguration corsConfig = corsSource.getCorsConfiguration(request);
            
            assertNotNull(corsConfig);
            assertTrue(corsConfig.getAllowedOrigins().contains("http://localhost:5173"));
            assertTrue(corsConfig.getAllowedMethods().contains("GET"));
            assertTrue(corsConfig.getAllowedHeaders().contains("*"));
            assertTrue(corsConfig.getAllowCredentials());
        }

        @Test
        @DisplayName("Should test SecurityConfig with different origins")
        void securityConfig_ShouldTestWithDifferentOrigins() {
            SecurityConfig config = new SecurityConfig(jwtAuthFilter);
            CorsConfigurationSource corsSource = config.corsConfigurationSource();

            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");
            
            // Test with different origins
            request.addHeader("Origin", "http://localhost:5173");
            
            CorsConfiguration corsConfig = corsSource.getCorsConfiguration(request);
            
            assertNotNull(corsConfig);
            assertTrue(corsConfig.getAllowedOrigins().contains("http://localhost:5173"));
            assertTrue(corsConfig.getAllowedMethods().contains("GET"));
            assertTrue(corsConfig.getAllowedHeaders().contains("*"));
            assertTrue(corsConfig.getAllowCredentials());
        }

        @Test
        @DisplayName("Should test SecurityConfig with multiple SecurityConfig instances")
        void securityConfig_ShouldTestWithMultipleSecurityConfigInstances() {
            SecurityConfig config1 = new SecurityConfig(jwtAuthFilter);
            SecurityConfig config2 = new SecurityConfig(jwtAuthFilter);
            
            assertNotNull(config1);
            assertNotNull(config2);

            // Both should be able to create CORS configuration
            CorsConfigurationSource corsSource1 = config1.corsConfigurationSource();
            CorsConfigurationSource corsSource2 = config2.corsConfigurationSource();

            assertNotNull(corsSource1);
            assertNotNull(corsSource2);

            // Test with same request to ensure consistency
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");
            CorsConfiguration corsConfig1 = corsSource1.getCorsConfiguration(request);
            CorsConfiguration corsConfig2 = corsSource2.getCorsConfiguration(request);

            assertNotNull(corsConfig1);
            assertNotNull(corsConfig2);

            // Both should have identical CORS configuration
            assertEquals(corsConfig1.getAllowedOrigins(), corsConfig2.getAllowedOrigins());
            assertEquals(corsConfig1.getAllowedMethods(), corsConfig2.getAllowedMethods());
            assertEquals(corsConfig1.getAllowedHeaders(), corsConfig2.getAllowedHeaders());
            assertEquals(corsConfig1.getAllowCredentials(), corsConfig2.getAllowCredentials());
        }

        @Test
        @DisplayName("Should test SecurityConfig CORS configuration with edge cases")
        void securityConfig_ShouldTestCorsConfigurationWithEdgeCases() {
            SecurityConfig config = new SecurityConfig(jwtAuthFilter);
            CorsConfigurationSource corsSource = config.corsConfigurationSource();

            // Test with empty path
            MockHttpServletRequest emptyRequest = new MockHttpServletRequest("GET", "");
            CorsConfiguration emptyCorsConfig = corsSource.getCorsConfiguration(emptyRequest);
            assertNotNull(emptyCorsConfig);
            assertTrue(emptyCorsConfig.getAllowedOrigins().contains("http://localhost:5173"));

            // Test with root path
            MockHttpServletRequest rootRequest = new MockHttpServletRequest("GET", "/");
            CorsConfiguration rootCorsConfig = corsSource.getCorsConfiguration(rootRequest);
            assertNotNull(rootCorsConfig);
            assertTrue(rootCorsConfig.getAllowedOrigins().contains("http://localhost:5173"));

            // Test with very long path
            MockHttpServletRequest longPathRequest = new MockHttpServletRequest("GET", "/api/very/long/path/with/many/segments");
            CorsConfiguration longPathCorsConfig = corsSource.getCorsConfiguration(longPathRequest);
            assertNotNull(longPathCorsConfig);
            assertTrue(longPathCorsConfig.getAllowedOrigins().contains("http://localhost:5173"));
        }

        @Test
        @DisplayName("Should test SecurityConfig CORS configuration with special characters")
        void securityConfig_ShouldTestCorsConfigurationWithSpecialCharacters() {
            SecurityConfig config = new SecurityConfig(jwtAuthFilter);
            CorsConfigurationSource corsSource = config.corsConfigurationSource();

            // Test with paths containing special characters
            String[] specialPaths = {
                "/api/test?param=value",
                "/api/test#fragment",
                "/api/test with spaces",
                "/api/test-with-dashes",
                "/api/test_with_underscores",
                "/api/test.with.dots",
                "/api/test%20encoded",
                "/api/test+plus"
            };

            for (String path : specialPaths) {
                MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
                CorsConfiguration corsConfig = corsSource.getCorsConfiguration(request);
                
                assertNotNull(corsConfig);
                assertTrue(corsConfig.getAllowedOrigins().contains("http://localhost:5173"));
                assertTrue(corsConfig.getAllowedMethods().contains("GET"));
                assertTrue(corsConfig.getAllowedHeaders().contains("*"));
                assertTrue(corsConfig.getAllowCredentials());
            }
        }

        @Test
        @DisplayName("Should test SecurityConfig CORS configuration with different origins")
        void securityConfig_ShouldTestCorsConfigurationWithDifferentOrigins() {
            SecurityConfig config = new SecurityConfig(jwtAuthFilter);
            CorsConfigurationSource corsSource = config.corsConfigurationSource();

            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");
            
            // Test with different origin headers
            String[] origins = {
                "http://localhost:5173",
                "https://delightful-sand-0bf2f5b10.1.azurestaticapps.net",
                "http://localhost:3000",
                "https://example.com",
                "http://test.example.com:8080"
            };

            for (String origin : origins) {
                request.addHeader("Origin", origin);
                CorsConfiguration corsConfig = corsSource.getCorsConfiguration(request);
                
                assertNotNull(corsConfig);
                // Should allow the configured origins
                if (origin.equals("http://localhost:5173") || 
                    origin.equals("https://delightful-sand-0bf2f5b10.1.azurestaticapps.net")) {
                    assertTrue(corsConfig.getAllowedOrigins().contains(origin));
                }
                assertTrue(corsConfig.getAllowedMethods().contains("GET"));
                assertTrue(corsConfig.getAllowedHeaders().contains("*"));
                assertTrue(corsConfig.getAllowCredentials());
            }
        }
    }
}
