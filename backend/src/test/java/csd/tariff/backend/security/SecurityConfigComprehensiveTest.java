package csd.tariff.backend.security;

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
 * Comprehensive SecurityConfig tests
 * Combines basic tests, reflection-based tests, and additional coverage tests
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

    // ===== BASIC CONSTRUCTOR TESTS =====

    @Test
    @DisplayName("Should create SecurityConfig with JwtAuthFilter")
    void shouldCreateSecurityConfigWithJwtAuthFilter() {
        JwtAuthFilter jwtAuthFilter = mock(JwtAuthFilter.class);
        SecurityConfig securityConfig = new SecurityConfig(jwtAuthFilter);
        assertNotNull(securityConfig);
    }

    @Test
    @DisplayName("Should create SecurityConfig with null JwtAuthFilter")
    void shouldCreateSecurityConfigWithNullJwtAuthFilter() {
        SecurityConfig securityConfig = new SecurityConfig(null);
        assertNotNull(securityConfig);
    }

    // ===== PASSWORD ENCODER TESTS =====

    @Test
    @DisplayName("Should handle password encoding with BCrypt")
    void shouldHandlePasswordEncodingWithBCrypt() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String rawPassword = "testPassword123";
        String encodedPassword = encoder.encode(rawPassword);
        
        assertNotNull(encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
    }

    @Test
    @DisplayName("Should handle different password encodings")
    void shouldHandleDifferentPasswordEncodings() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password1 = "password1";
        String password2 = "password2";
        
        String encoded1 = encoder.encode(password1);
        String encoded2 = encoder.encode(password2);
        
        assertTrue(encoder.matches(password1, encoded1));
        assertTrue(encoder.matches(password2, encoded2));
        assertTrue(!encoder.matches(password1, encoded2));
        assertTrue(!encoder.matches(password2, encoded1));
    }

    @Test
    @DisplayName("Should handle password encoding edge cases")
    void shouldHandlePasswordEncodingEdgeCases() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Test empty password
        String emptyPassword = "";
        String encodedEmpty = encoder.encode(emptyPassword);
        assertNotNull(encodedEmpty);
        assertTrue(encoder.matches(emptyPassword, encodedEmpty));
        
        // Test long password (but within BCrypt's 72-byte limit)
        String longPassword = "a".repeat(70); // 70 characters, well within 72-byte limit
        String encodedLong = encoder.encode(longPassword);
        assertNotNull(encodedLong);
        assertTrue(encoder.matches(longPassword, encodedLong));
    }

    @Test
    @DisplayName("Should handle special characters in passwords")
    void shouldHandleSpecialCharactersInPasswords() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String specialPassword = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~";
        String encodedSpecial = encoder.encode(specialPassword);
        assertNotNull(encodedSpecial);
        assertTrue(encoder.matches(specialPassword, encodedSpecial));
    }

    @Test
    @DisplayName("Should handle BCryptPasswordEncoder creation")
    void shouldHandleBCryptPasswordEncoderCreation() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    // ===== REFLECTION-BASED BEAN TESTS =====

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
    void authenticationProvider_ShouldReturnDaoAuthenticationProvider() throws Exception {
        // Use reflection to access the private method
        Method authProviderMethod = SecurityConfig.class.getDeclaredMethod(
            "authenticationProvider", 
            EmailUserDetailsService.class, 
            PasswordEncoder.class);
        authProviderMethod.setAccessible(true);
        
        // Call the method
        DaoAuthenticationProvider provider = (DaoAuthenticationProvider) authProviderMethod.invoke(
            securityConfig, mockUserDetailsService, mockPasswordEncoder);
        
        // Verify it returns a DaoAuthenticationProvider
        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);
    }

    @Test
    @DisplayName("Should test authenticationManager bean method")
    void authenticationManager_ShouldReturnProviderManager() throws Exception {
        // Use reflection to access the private method
        Method authManagerMethod = SecurityConfig.class.getDeclaredMethod(
            "authenticationManager", 
            DaoAuthenticationProvider.class);
        authManagerMethod.setAccessible(true);
        
        // Create a mock DaoAuthenticationProvider
        DaoAuthenticationProvider mockProvider = mock(DaoAuthenticationProvider.class);
        
        // Call the method
        AuthenticationManager manager = (AuthenticationManager) authManagerMethod.invoke(
            securityConfig, mockProvider);
        
        // Verify it returns a ProviderManager
        assertNotNull(manager);
        assertTrue(manager instanceof ProviderManager);
    }

    // ===== CORS CONFIGURATION TESTS =====

    @Test
    @DisplayName("Should handle CORS configuration")
    void shouldHandleCorsConfiguration() {
        CorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        assertNotNull(source);
        assertTrue(source instanceof org.springframework.web.cors.UrlBasedCorsConfigurationSource);
    }

    @Test
    @DisplayName("Should test CORS configuration with different paths")
    void corsConfigurationSource_ShouldHandleDifferentPaths() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        assertNotNull(corsSource);

        // Test with different request paths
        String[] testPaths = {
            "/api/test",
            "/api/products",
            "/api/tariff/calculate",
            "/api/auth/login",
            "/api/users/profile"
        };

        for (String path : testPaths) {
            org.springframework.mock.web.MockHttpServletRequest request = 
                new org.springframework.mock.web.MockHttpServletRequest("GET", path);
            CorsConfiguration corsConfig = corsSource.getCorsConfiguration(request);
            
            assertNotNull(corsConfig);
            assertTrue(corsConfig.getAllowedOrigins().contains("http://localhost:5173"));
            assertTrue(corsConfig.getAllowedMethods().contains("GET"));
            assertTrue(corsConfig.getAllowedMethods().contains("POST"));
            assertTrue(corsConfig.getAllowedMethods().contains("PUT"));
            assertTrue(corsConfig.getAllowedMethods().contains("DELETE"));
            assertTrue(corsConfig.getAllowedMethods().contains("OPTIONS"));
        }
    }

    @Test
    @DisplayName("Should test CORS configuration with edge cases")
    void corsConfigurationSource_ShouldHandleEdgeCases() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        assertNotNull(corsSource);

        // Test with empty path
        org.springframework.mock.web.MockHttpServletRequest emptyRequest = 
            new org.springframework.mock.web.MockHttpServletRequest("GET", "");
        CorsConfiguration emptyCorsConfig = corsSource.getCorsConfiguration(emptyRequest);
        assertNotNull(emptyCorsConfig);
        assertTrue(emptyCorsConfig.getAllowedOrigins().contains("http://localhost:5173"));

        // Test with root path
        org.springframework.mock.web.MockHttpServletRequest rootRequest = 
            new org.springframework.mock.web.MockHttpServletRequest("GET", "/");
        CorsConfiguration rootCorsConfig = corsSource.getCorsConfiguration(rootRequest);
        assertNotNull(rootCorsConfig);
        assertTrue(rootCorsConfig.getAllowedOrigins().contains("http://localhost:5173"));

        // Test with very long path
        org.springframework.mock.web.MockHttpServletRequest longPathRequest = 
            new org.springframework.mock.web.MockHttpServletRequest("GET", "/api/very/long/path/with/many/segments");
        CorsConfiguration longPathCorsConfig = corsSource.getCorsConfiguration(longPathRequest);
        assertNotNull(longPathCorsConfig);
        assertTrue(longPathCorsConfig.getAllowedOrigins().contains("http://localhost:5173"));
    }

    // ===== METHOD STRUCTURE TESTS =====

    @Test
    @DisplayName("Should test filterChain method visibility and signature")
    void filterChain_ShouldHaveCorrectVisibilityAndSignature() throws NoSuchMethodException {
        Method filterChainMethod = SecurityConfig.class.getDeclaredMethod("filterChain", 
            org.springframework.security.config.annotation.web.builders.HttpSecurity.class);
        assertNotNull(filterChainMethod);
        
        // Check that it's package-private (not public, private, or protected)
        int modifiers = filterChainMethod.getModifiers();
        assertTrue((modifiers & java.lang.reflect.Modifier.PUBLIC) == 0);
        assertTrue((modifiers & java.lang.reflect.Modifier.PRIVATE) == 0);
        assertTrue((modifiers & java.lang.reflect.Modifier.PROTECTED) == 0);
        
        assertEquals(org.springframework.security.web.SecurityFilterChain.class, 
            filterChainMethod.getReturnType());
    }

    @Test
    @DisplayName("Should test SecurityConfig class structure")
    void securityConfig_ShouldHaveCorrectClassStructure() {
        assertNotNull(SecurityConfig.class);
        assertTrue(java.lang.reflect.Modifier.isPublic(SecurityConfig.class.getModifiers()));
        assertTrue(!java.lang.reflect.Modifier.isAbstract(SecurityConfig.class.getModifiers()));
        assertTrue(!java.lang.reflect.Modifier.isInterface(SecurityConfig.class.getModifiers()));
    }

    @Test
    @DisplayName("Should test bean method visibility and return types")
    void beanMethods_ShouldHaveCorrectVisibilityAndReturnTypes() throws NoSuchMethodException {
        // Test passwordEncoder method
        Method passwordEncoderMethod = SecurityConfig.class.getDeclaredMethod("passwordEncoder");
        assertNotNull(passwordEncoderMethod);
        assertTrue((passwordEncoderMethod.getModifiers() & java.lang.reflect.Modifier.PUBLIC) == 0);
        assertTrue((passwordEncoderMethod.getModifiers() & java.lang.reflect.Modifier.PRIVATE) == 0);
        assertTrue((passwordEncoderMethod.getModifiers() & java.lang.reflect.Modifier.PROTECTED) == 0);
        assertEquals(PasswordEncoder.class, passwordEncoderMethod.getReturnType());

        // Test corsConfigurationSource method
        Method corsMethod = SecurityConfig.class.getDeclaredMethod("corsConfigurationSource");
        assertNotNull(corsMethod);
        assertTrue(java.lang.reflect.Modifier.isPublic(corsMethod.getModifiers()));
        assertEquals(CorsConfigurationSource.class, corsMethod.getReturnType());

        // Test authenticationProvider method
        Method authProviderMethod = SecurityConfig.class.getDeclaredMethod(
            "authenticationProvider",
            EmailUserDetailsService.class,
            PasswordEncoder.class);
        assertNotNull(authProviderMethod);
        assertTrue((authProviderMethod.getModifiers() & java.lang.reflect.Modifier.PUBLIC) == 0);
        assertTrue((authProviderMethod.getModifiers() & java.lang.reflect.Modifier.PRIVATE) == 0);
        assertTrue((authProviderMethod.getModifiers() & java.lang.reflect.Modifier.PROTECTED) == 0);
        assertEquals(DaoAuthenticationProvider.class, authProviderMethod.getReturnType());

        // Test authenticationManager method
        Method authManagerMethod = SecurityConfig.class.getDeclaredMethod(
            "authenticationManager",
            DaoAuthenticationProvider.class);
        assertNotNull(authManagerMethod);
        assertTrue((authManagerMethod.getModifiers() & java.lang.reflect.Modifier.PUBLIC) == 0);
        assertTrue((authManagerMethod.getModifiers() & java.lang.reflect.Modifier.PRIVATE) == 0);
        assertTrue((authManagerMethod.getModifiers() & java.lang.reflect.Modifier.PROTECTED) == 0);
        assertEquals(AuthenticationManager.class, authManagerMethod.getReturnType());
    }
}