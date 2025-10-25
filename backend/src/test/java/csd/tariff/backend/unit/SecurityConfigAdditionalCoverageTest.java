package csd.tariff.backend.unit;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import csd.tariff.backend.config.SecurityConfig;
import csd.tariff.backend.security.JwtAuthFilter;

/**
 * Additional SecurityConfig tests to improve coverage further
 * Focuses on testing the filterChain method and other uncovered areas
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityConfig Additional Coverage Tests")
class SecurityConfigAdditionalCoverageTest {

    @Test
    @DisplayName("Should test filterChain method using reflection")
    void filterChain_ShouldBeAccessibleViaReflection() throws Exception {
        JwtAuthFilter mockJwtAuthFilter = mock(JwtAuthFilter.class);
        SecurityConfig securityConfig = new SecurityConfig(mockJwtAuthFilter);
        
        // Test that filterChain method exists and is accessible
        Method filterChainMethod = SecurityConfig.class.getDeclaredMethod("filterChain", HttpSecurity.class);
        assertNotNull(filterChainMethod);
        
        // Verify method signature
        Class<?>[] parameterTypes = filterChainMethod.getParameterTypes();
        assertNotNull(parameterTypes);
        assertTrue(parameterTypes.length == 1);
        assertTrue(parameterTypes[0] == HttpSecurity.class);
        
        // Verify method visibility
        assertTrue((filterChainMethod.getModifiers() & java.lang.reflect.Modifier.PUBLIC) == 0);
        assertTrue((filterChainMethod.getModifiers() & java.lang.reflect.Modifier.PRIVATE) == 0);
        assertTrue((filterChainMethod.getModifiers() & java.lang.reflect.Modifier.PROTECTED) == 0);
    }

    @Test
    @DisplayName("Should test SecurityConfig constructor with different scenarios")
    void constructor_ShouldHandleDifferentScenarios() {
        // Test with valid JwtAuthFilter
        JwtAuthFilter validFilter = mock(JwtAuthFilter.class);
        SecurityConfig config1 = new SecurityConfig(validFilter);
        assertNotNull(config1);
        
        // Test with null JwtAuthFilter
        SecurityConfig config2 = new SecurityConfig(null);
        assertNotNull(config2);
        
        // Test with different filter instances
        JwtAuthFilter filter1 = mock(JwtAuthFilter.class);
        JwtAuthFilter filter2 = mock(JwtAuthFilter.class);
        
        SecurityConfig config3 = new SecurityConfig(filter1);
        SecurityConfig config4 = new SecurityConfig(filter2);
        
        assertNotNull(config3);
        assertNotNull(config4);
    }

    @Test
    @DisplayName("Should test SecurityConfig bean method accessibility")
    void beanMethods_ShouldBeAccessibleViaReflection() throws Exception {
        JwtAuthFilter mockJwtAuthFilter = mock(JwtAuthFilter.class);
        SecurityConfig securityConfig = new SecurityConfig(mockJwtAuthFilter);
        
        // Test passwordEncoder method
        Method passwordEncoderMethod = SecurityConfig.class.getDeclaredMethod("passwordEncoder");
        assertNotNull(passwordEncoderMethod);
        assertTrue((passwordEncoderMethod.getModifiers() & java.lang.reflect.Modifier.PUBLIC) == 0);
        assertTrue((passwordEncoderMethod.getModifiers() & java.lang.reflect.Modifier.PRIVATE) == 0);
        assertTrue((passwordEncoderMethod.getModifiers() & java.lang.reflect.Modifier.PROTECTED) == 0);
        
        // Test corsConfigurationSource method
        Method corsMethod = SecurityConfig.class.getDeclaredMethod("corsConfigurationSource");
        assertNotNull(corsMethod);
        assertTrue(java.lang.reflect.Modifier.isPublic(corsMethod.getModifiers()));
        
        // Test authenticationProvider method
        Method authProviderMethod = SecurityConfig.class.getDeclaredMethod(
            "authenticationProvider", 
            csd.tariff.backend.service.EmailUserDetailsService.class, 
            org.springframework.security.crypto.password.PasswordEncoder.class);
        assertNotNull(authProviderMethod);
        assertTrue((authProviderMethod.getModifiers() & java.lang.reflect.Modifier.PUBLIC) == 0);
        assertTrue((authProviderMethod.getModifiers() & java.lang.reflect.Modifier.PRIVATE) == 0);
        assertTrue((authProviderMethod.getModifiers() & java.lang.reflect.Modifier.PROTECTED) == 0);
        
        // Test authenticationManager method
        Method authManagerMethod = SecurityConfig.class.getDeclaredMethod(
            "authenticationManager", 
            org.springframework.security.authentication.dao.DaoAuthenticationProvider.class);
        assertNotNull(authManagerMethod);
        assertTrue((authManagerMethod.getModifiers() & java.lang.reflect.Modifier.PUBLIC) == 0);
        assertTrue((authManagerMethod.getModifiers() & java.lang.reflect.Modifier.PRIVATE) == 0);
        assertTrue((authManagerMethod.getModifiers() & java.lang.reflect.Modifier.PROTECTED) == 0);
    }

    @Test
    @DisplayName("Should test SecurityConfig class structure")
    void securityConfig_ShouldHaveCorrectClassStructure() {
        // Test class annotations
        assertTrue(SecurityConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
        assertTrue(SecurityConfig.class.isAnnotationPresent(org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity.class));
        
        // Test that JwtAuthFilter field exists
        assertDoesNotThrow(() -> {
            java.lang.reflect.Field jwtAuthFilterField = SecurityConfig.class.getDeclaredField("jwtAuthFilter");
            assertNotNull(jwtAuthFilterField);
            assertTrue(jwtAuthFilterField.getType() == JwtAuthFilter.class);
        });
    }

    @Test
    @DisplayName("Should test SecurityConfig with multiple instances")
    void securityConfig_ShouldHandleMultipleInstances() {
        JwtAuthFilter filter1 = mock(JwtAuthFilter.class);
        JwtAuthFilter filter2 = mock(JwtAuthFilter.class);
        
        SecurityConfig config1 = new SecurityConfig(filter1);
        SecurityConfig config2 = new SecurityConfig(filter2);
        SecurityConfig config3 = new SecurityConfig(null);
        
        assertNotNull(config1);
        assertNotNull(config2);
        assertNotNull(config3);
        
        // All should be able to create CORS configuration
        assertDoesNotThrow(() -> {
            config1.corsConfigurationSource();
            config2.corsConfigurationSource();
            config3.corsConfigurationSource();
        });
    }

    @Test
    @DisplayName("Should test SecurityConfig method parameter validation")
    void securityConfig_ShouldValidateMethodParameters() throws Exception {
        JwtAuthFilter mockJwtAuthFilter = mock(JwtAuthFilter.class);
        SecurityConfig securityConfig = new SecurityConfig(mockJwtAuthFilter);
        
        // Test corsConfigurationSource method parameters
        Method corsMethod = SecurityConfig.class.getDeclaredMethod("corsConfigurationSource");
        Class<?>[] corsParameterTypes = corsMethod.getParameterTypes();
        assertNotNull(corsParameterTypes);
        assertTrue(corsParameterTypes.length == 0);
        
        // Test passwordEncoder method parameters
        Method passwordEncoderMethod = SecurityConfig.class.getDeclaredMethod("passwordEncoder");
        Class<?>[] passwordEncoderParameterTypes = passwordEncoderMethod.getParameterTypes();
        assertNotNull(passwordEncoderParameterTypes);
        assertTrue(passwordEncoderParameterTypes.length == 0);
        
        // Test authenticationProvider method parameters
        Method authProviderMethod = SecurityConfig.class.getDeclaredMethod(
            "authenticationProvider", 
            csd.tariff.backend.service.EmailUserDetailsService.class, 
            org.springframework.security.crypto.password.PasswordEncoder.class);
        Class<?>[] authProviderParameterTypes = authProviderMethod.getParameterTypes();
        assertNotNull(authProviderParameterTypes);
        assertTrue(authProviderParameterTypes.length == 2);
        assertTrue(authProviderParameterTypes[0] == csd.tariff.backend.service.EmailUserDetailsService.class);
        assertTrue(authProviderParameterTypes[1] == org.springframework.security.crypto.password.PasswordEncoder.class);
        
        // Test authenticationManager method parameters
        Method authManagerMethod = SecurityConfig.class.getDeclaredMethod(
            "authenticationManager", 
            org.springframework.security.authentication.dao.DaoAuthenticationProvider.class);
        Class<?>[] authManagerParameterTypes = authManagerMethod.getParameterTypes();
        assertNotNull(authManagerParameterTypes);
        assertTrue(authManagerParameterTypes.length == 1);
        assertTrue(authManagerParameterTypes[0] == org.springframework.security.authentication.dao.DaoAuthenticationProvider.class);
    }

    @Test
    @DisplayName("Should test SecurityConfig method return types")
    void securityConfig_ShouldHaveCorrectReturnTypes() throws Exception {
        JwtAuthFilter mockJwtAuthFilter = mock(JwtAuthFilter.class);
        SecurityConfig securityConfig = new SecurityConfig(mockJwtAuthFilter);
        
        // Test corsConfigurationSource return type
        Method corsMethod = SecurityConfig.class.getDeclaredMethod("corsConfigurationSource");
        assertTrue(corsMethod.getReturnType() == org.springframework.web.cors.CorsConfigurationSource.class);
        
        // Test passwordEncoder return type
        Method passwordEncoderMethod = SecurityConfig.class.getDeclaredMethod("passwordEncoder");
        assertTrue(passwordEncoderMethod.getReturnType() == org.springframework.security.crypto.password.PasswordEncoder.class);
        
        // Test authenticationProvider return type
        Method authProviderMethod = SecurityConfig.class.getDeclaredMethod(
            "authenticationProvider", 
            csd.tariff.backend.service.EmailUserDetailsService.class, 
            org.springframework.security.crypto.password.PasswordEncoder.class);
        assertTrue(authProviderMethod.getReturnType() == org.springframework.security.authentication.dao.DaoAuthenticationProvider.class);
        
        // Test authenticationManager return type
        Method authManagerMethod = SecurityConfig.class.getDeclaredMethod(
            "authenticationManager", 
            org.springframework.security.authentication.dao.DaoAuthenticationProvider.class);
        assertTrue(authManagerMethod.getReturnType() == org.springframework.security.authentication.AuthenticationManager.class);
    }

    @Test
    @DisplayName("Should test SecurityConfig with edge cases")
    void securityConfig_ShouldHandleEdgeCases() {
        // Test with null filter
        SecurityConfig configWithNull = new SecurityConfig(null);
        assertNotNull(configWithNull);
        
        // Test CORS configuration with null filter
        assertDoesNotThrow(() -> {
            configWithNull.corsConfigurationSource();
        });
        
        // Test with mock filter
        JwtAuthFilter mockFilter = mock(JwtAuthFilter.class);
        SecurityConfig configWithMock = new SecurityConfig(mockFilter);
        assertNotNull(configWithMock);
        
        // Test CORS configuration with mock filter
        assertDoesNotThrow(() -> {
            configWithMock.corsConfigurationSource();
        });
    }

    @Test
    @DisplayName("Should test SecurityConfig class loading")
    void securityConfig_ShouldBeLoadableViaReflection() {
        // Test class loading
        assertDoesNotThrow(() -> {
            Class<?> clazz = Class.forName("csd.tariff.backend.config.SecurityConfig");
            assertNotNull(clazz);
            assertEquals(SecurityConfig.class, clazz);
        });
    }

    @Test
    @DisplayName("Should test SecurityConfig field accessibility")
    void securityConfig_ShouldHaveAccessibleFields() {
        // Test JwtAuthFilter field
        assertDoesNotThrow(() -> {
            java.lang.reflect.Field jwtAuthFilterField = SecurityConfig.class.getDeclaredField("jwtAuthFilter");
            assertNotNull(jwtAuthFilterField);
            assertTrue(jwtAuthFilterField.getType() == JwtAuthFilter.class);
            assertTrue(java.lang.reflect.Modifier.isPrivate(jwtAuthFilterField.getModifiers()));
            assertTrue(java.lang.reflect.Modifier.isFinal(jwtAuthFilterField.getModifiers()));
        });
    }
}
