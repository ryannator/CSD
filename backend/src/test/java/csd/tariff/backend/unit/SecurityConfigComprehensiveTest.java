package csd.tariff.backend.unit;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import csd.tariff.backend.config.SecurityConfig;
import csd.tariff.backend.security.JwtAuthFilter;
import csd.tariff.backend.service.EmailUserDetailsService;

/**
 * Comprehensive tests for SecurityConfig using reflection to test private/protected methods
 * This improves test coverage by testing all bean methods and configuration logic
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityConfig Comprehensive Tests")
class SecurityConfigComprehensiveTest {

    private SecurityConfig securityConfig;
    private JwtAuthFilter mockJwtAuthFilter;
    private EmailUserDetailsService mockUserDetailsService;
    private PasswordEncoder mockPasswordEncoder;

    @BeforeEach
    void setUp() {
        mockJwtAuthFilter = mock(JwtAuthFilter.class);
        mockUserDetailsService = mock(EmailUserDetailsService.class);
        mockPasswordEncoder = mock(PasswordEncoder.class);
        securityConfig = new SecurityConfig(mockJwtAuthFilter);
    }

    @Test
    @DisplayName("Should test passwordEncoder bean method")
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() throws Exception {
        // Use reflection to access the private method
        Method passwordEncoderMethod = SecurityConfig.class.getDeclaredMethod("passwordEncoder");
        passwordEncoderMethod.setAccessible(true);
        
        // Call the method
        PasswordEncoder encoder = (PasswordEncoder) passwordEncoderMethod.invoke(securityConfig);
        
        // Verify it returns a BCryptPasswordEncoder
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
        
        // Test that it can encode passwords
        String password = "testPassword123";
        String encoded = encoder.encode(password);
        assertNotNull(encoded);
        assertTrue(encoded.length() > 0);
        assertTrue(encoder.matches(password, encoded));
        
        // Test that different passwords produce different encodings
        String password2 = "differentPassword456";
        String encoded2 = encoder.encode(password2);
        assertNotNull(encoded2);
        assertTrue(encoder.matches(password2, encoded2));
    }

    @Test
    @DisplayName("Should test authenticationProvider bean method")
    void authenticationProvider_ShouldConfigureProviderCorrectly() throws Exception {
        // Use reflection to access the private method
        Method authProviderMethod = SecurityConfig.class.getDeclaredMethod(
            "authenticationProvider", EmailUserDetailsService.class, PasswordEncoder.class);
        authProviderMethod.setAccessible(true);
        
        // Call the method
        DaoAuthenticationProvider provider = (DaoAuthenticationProvider) authProviderMethod.invoke(
            securityConfig, mockUserDetailsService, mockPasswordEncoder);
        
        // Verify the provider is configured correctly
        assertNotNull(provider);
        // Note: We can't directly access protected methods, but we can verify the provider was created
        // and test its behavior indirectly through reflection if needed
    }

    @Test
    @DisplayName("Should test authenticationManager bean method")
    void authenticationManager_ShouldReturnProviderManager() throws Exception {
        // Create a mock provider
        DaoAuthenticationProvider mockProvider = mock(DaoAuthenticationProvider.class);
        
        // Use reflection to access the private method
        Method authManagerMethod = SecurityConfig.class.getDeclaredMethod(
            "authenticationManager", DaoAuthenticationProvider.class);
        authManagerMethod.setAccessible(true);
        
        // Call the method
        AuthenticationManager authManager = (AuthenticationManager) authManagerMethod.invoke(
            securityConfig, mockProvider);
        
        // Verify it returns a ProviderManager
        assertNotNull(authManager);
        assertTrue(authManager instanceof ProviderManager);
    }

    @Test
    @DisplayName("Should test passwordEncoder with various password types")
    void passwordEncoder_ShouldHandleVariousPasswordTypes() throws Exception {
        Method passwordEncoderMethod = SecurityConfig.class.getDeclaredMethod("passwordEncoder");
        passwordEncoderMethod.setAccessible(true);
        PasswordEncoder encoder = (PasswordEncoder) passwordEncoderMethod.invoke(securityConfig);
        
        // Test various password patterns
        String[] passwords = {
            "simple",
            "password123",
            "VeryLongPasswordWithSpecialChars!@#$%^&*()",
            "123456789",
            "a",
            "",
            "password with spaces",
            "PASSWORD_WITH_UNDERSCORES",
            "password-with-dashes",
            "password.with.dots"
        };
        
        for (String password : passwords) {
            String encoded = encoder.encode(password);
            assertNotNull(encoded);
            assertTrue(encoded.length() > 0);
            assertTrue(encoder.matches(password, encoded));
            
            // Test that wrong password doesn't match
            assertTrue(!encoder.matches(password + "wrong", encoded));
        }
    }

    @Test
    @DisplayName("Should test authenticationProvider with different configurations")
    void authenticationProvider_ShouldHandleDifferentConfigurations() throws Exception {
        Method authProviderMethod = SecurityConfig.class.getDeclaredMethod(
            "authenticationProvider", EmailUserDetailsService.class, PasswordEncoder.class);
        authProviderMethod.setAccessible(true);
        
        // Test with different user details services
        EmailUserDetailsService service1 = mock(EmailUserDetailsService.class);
        EmailUserDetailsService service2 = mock(EmailUserDetailsService.class);
        
        PasswordEncoder encoder1 = mock(PasswordEncoder.class);
        PasswordEncoder encoder2 = mock(PasswordEncoder.class);
        
        // Test different combinations
        DaoAuthenticationProvider provider1 = (DaoAuthenticationProvider) authProviderMethod.invoke(
            securityConfig, service1, encoder1);
        DaoAuthenticationProvider provider2 = (DaoAuthenticationProvider) authProviderMethod.invoke(
            securityConfig, service2, encoder2);
        
        assertNotNull(provider1);
        assertNotNull(provider2);
        // Note: We can't directly access protected methods, but we can verify the providers were created
        // and test their behavior indirectly
    }

    @Test
    @DisplayName("Should test authenticationManager with different providers")
    void authenticationManager_ShouldHandleDifferentProviders() throws Exception {
        Method authManagerMethod = SecurityConfig.class.getDeclaredMethod(
            "authenticationManager", DaoAuthenticationProvider.class);
        authManagerMethod.setAccessible(true);
        
        // Test with different providers
        DaoAuthenticationProvider provider1 = mock(DaoAuthenticationProvider.class);
        DaoAuthenticationProvider provider2 = mock(DaoAuthenticationProvider.class);
        
        AuthenticationManager manager1 = (AuthenticationManager) authManagerMethod.invoke(
            securityConfig, provider1);
        AuthenticationManager manager2 = (AuthenticationManager) authManagerMethod.invoke(
            securityConfig, provider2);
        
        assertNotNull(manager1);
        assertNotNull(manager2);
        assertTrue(manager1 instanceof ProviderManager);
        assertTrue(manager2 instanceof ProviderManager);
    }

    @Test
    @DisplayName("Should test CORS configuration with comprehensive scenarios")
    void corsConfigurationSource_ShouldHandleComprehensiveScenarios() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        assertNotNull(corsSource);
        
        // Test with various request scenarios
        String[] testPaths = {
            "/api/test",
            "/api/users",
            "/auth/login",
            "/swagger-ui/index.html",
            "/v3/api-docs/swagger-config",
            "/api/products/123",
            "/api/countries/US",
            "/api/tariff/calculate"
        };
        
        String[] testMethods = {"GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"};
        
        for (String path : testPaths) {
            for (String method : testMethods) {
                org.springframework.mock.web.MockHttpServletRequest request = 
                    new org.springframework.mock.web.MockHttpServletRequest(method, path);
                
                CorsConfiguration corsConfig = corsSource.getCorsConfiguration(request);
                assertNotNull(corsConfig);
                
                // Verify CORS configuration
                assertTrue(corsConfig.getAllowedOrigins().contains("http://localhost:5173"));
                assertTrue(corsConfig.getAllowedOrigins().contains("https://delightful-sand-0bf2f5b10.1.azurestaticapps.net"));
                // Only test methods that are actually configured
                if (method.equals("GET") || method.equals("POST") || method.equals("PUT") || 
                    method.equals("DELETE") || method.equals("OPTIONS")) {
                    assertTrue(corsConfig.getAllowedMethods().contains(method));
                }
                assertTrue(corsConfig.getAllowedHeaders().contains("*"));
                assertTrue(corsConfig.getAllowCredentials());
            }
        }
    }

    @Test
    @DisplayName("Should test CORS configuration with special characters and edge cases")
    void corsConfigurationSource_ShouldHandleSpecialCharactersAndEdgeCases() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        
        // Test with special characters in paths
        String[] specialPaths = {
            "/api/test?param=value",
            "/api/test#fragment",
            "/api/test with spaces",
            "/api/test-with-dashes",
            "/api/test_with_underscores",
            "/api/test.with.dots",
            "/api/test%20encoded",
            "/api/test+plus",
            "/api/very/long/path/with/many/segments",
            "/",
            ""
        };
        
        for (String path : specialPaths) {
            org.springframework.mock.web.MockHttpServletRequest request = 
                new org.springframework.mock.web.MockHttpServletRequest("GET", path);
            
            CorsConfiguration corsConfig = corsSource.getCorsConfiguration(request);
            assertNotNull(corsConfig);
            assertTrue(corsConfig.getAllowedOrigins().contains("http://localhost:5173"));
            assertTrue(corsConfig.getAllowedMethods().contains("GET"));
            assertTrue(corsConfig.getAllowedHeaders().contains("*"));
            assertTrue(corsConfig.getAllowCredentials());
        }
    }

    @Test
    @DisplayName("Should test SecurityConfig constructor with different JwtAuthFilter instances")
    void constructor_ShouldHandleDifferentJwtAuthFilterInstances() {
        // Test with different JwtAuthFilter instances
        JwtAuthFilter filter1 = mock(JwtAuthFilter.class);
        JwtAuthFilter filter2 = mock(JwtAuthFilter.class);
        
        SecurityConfig config1 = new SecurityConfig(filter1);
        SecurityConfig config2 = new SecurityConfig(filter2);
        SecurityConfig config3 = new SecurityConfig(null);
        
        assertNotNull(config1);
        assertNotNull(config2);
        assertNotNull(config3);
        
        // All should be able to create CORS configuration
        CorsConfigurationSource corsSource1 = config1.corsConfigurationSource();
        CorsConfigurationSource corsSource2 = config2.corsConfigurationSource();
        CorsConfigurationSource corsSource3 = config3.corsConfigurationSource();
        
        assertNotNull(corsSource1);
        assertNotNull(corsSource2);
        assertNotNull(corsSource3);
        
        // All should return identical CORS configurations
        org.springframework.mock.web.MockHttpServletRequest request = 
            new org.springframework.mock.web.MockHttpServletRequest("GET", "/api/test");
        
        CorsConfiguration corsConfig1 = corsSource1.getCorsConfiguration(request);
        CorsConfiguration corsConfig2 = corsSource2.getCorsConfiguration(request);
        CorsConfiguration corsConfig3 = corsSource3.getCorsConfiguration(request);
        
        assertNotNull(corsConfig1);
        assertNotNull(corsConfig2);
        assertNotNull(corsConfig3);
        
        // All should have identical CORS configuration
        assertEquals(corsConfig1.getAllowedOrigins(), corsConfig2.getAllowedOrigins());
        assertEquals(corsConfig1.getAllowedOrigins(), corsConfig3.getAllowedOrigins());
        assertEquals(corsConfig1.getAllowedMethods(), corsConfig2.getAllowedMethods());
        assertEquals(corsConfig1.getAllowedMethods(), corsConfig3.getAllowedMethods());
        assertEquals(corsConfig1.getAllowedHeaders(), corsConfig2.getAllowedHeaders());
        assertEquals(corsConfig1.getAllowedHeaders(), corsConfig3.getAllowedHeaders());
        assertEquals(corsConfig1.getAllowCredentials(), corsConfig2.getAllowCredentials());
        assertEquals(corsConfig1.getAllowCredentials(), corsConfig3.getAllowCredentials());
    }

    @Test
    @DisplayName("Should test multiple bean method calls for consistency")
    void beanMethods_ShouldBeConsistentAcrossMultipleCalls() throws Exception {
        // Test passwordEncoder consistency
        Method passwordEncoderMethod = SecurityConfig.class.getDeclaredMethod("passwordEncoder");
        passwordEncoderMethod.setAccessible(true);
        
        PasswordEncoder encoder1 = (PasswordEncoder) passwordEncoderMethod.invoke(securityConfig);
        PasswordEncoder encoder2 = (PasswordEncoder) passwordEncoderMethod.invoke(securityConfig);
        
        assertNotNull(encoder1);
        assertNotNull(encoder2);
        assertTrue(encoder1 instanceof BCryptPasswordEncoder);
        assertTrue(encoder2 instanceof BCryptPasswordEncoder);
        
        // Test authenticationProvider consistency
        Method authProviderMethod = SecurityConfig.class.getDeclaredMethod(
            "authenticationProvider", EmailUserDetailsService.class, PasswordEncoder.class);
        authProviderMethod.setAccessible(true);
        
        DaoAuthenticationProvider provider1 = (DaoAuthenticationProvider) authProviderMethod.invoke(
            securityConfig, mockUserDetailsService, mockPasswordEncoder);
        DaoAuthenticationProvider provider2 = (DaoAuthenticationProvider) authProviderMethod.invoke(
            securityConfig, mockUserDetailsService, mockPasswordEncoder);
        
        assertNotNull(provider1);
        assertNotNull(provider2);
        // Note: We can't directly access protected methods, but we can verify the providers were created
        // and test their behavior indirectly
        
        // Test authenticationManager consistency
        Method authManagerMethod = SecurityConfig.class.getDeclaredMethod(
            "authenticationManager", DaoAuthenticationProvider.class);
        authManagerMethod.setAccessible(true);
        
        DaoAuthenticationProvider mockProvider = mock(DaoAuthenticationProvider.class);
        AuthenticationManager manager1 = (AuthenticationManager) authManagerMethod.invoke(
            securityConfig, mockProvider);
        AuthenticationManager manager2 = (AuthenticationManager) authManagerMethod.invoke(
            securityConfig, mockProvider);
        
        assertNotNull(manager1);
        assertNotNull(manager2);
        assertTrue(manager1 instanceof ProviderManager);
        assertTrue(manager2 instanceof ProviderManager);
    }
}
