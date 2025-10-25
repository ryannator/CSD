package csd.tariff.backend.unit;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import csd.tariff.backend.dto.RegistrationDTOs;
import csd.tariff.backend.dto.TariffCalculationRequest;
import csd.tariff.backend.dto.TariffCalculationResponse;
import csd.tariff.backend.model.User;

@DisplayName("DTO Branch Coverage Tests")
class DtoBranchTests {

    @Nested
    @DisplayName("TariffCalculationRequest Branch Tests")
    class TariffCalculationRequestBranchTests {

        @Test
        @DisplayName("Should handle different constructor branches")
        void tariffCalculationRequest_ShouldHandleDifferentConstructorBranches() {
            // Test default constructor branch
            TariffCalculationRequest request1 = new TariffCalculationRequest();
            assertNull(request1.getHtsCode());
            assertNull(request1.getOriginCountry());
            assertNull(request1.getDestinationCountry());
            assertNull(request1.getProductValue());
            assertNull(request1.getQuantity());
            assertEquals("USD", request1.getCurrency()); // Default value
            assertNull(request1.getTariffEffectiveDate());
            assertNull(request1.getTariffExpirationDate());

            // Test 6-parameter constructor branch
            TariffCalculationRequest request2 = new TariffCalculationRequest(
                "12345678", "US", "CA", new BigDecimal("1000.00"), 10, "CAD");
            assertEquals("12345678", request2.getHtsCode());
            assertEquals("US", request2.getOriginCountry());
            assertEquals("CA", request2.getDestinationCountry());
            assertEquals(new BigDecimal("1000.00"), request2.getProductValue());
            assertEquals(10, request2.getQuantity());
            assertEquals("CAD", request2.getCurrency());
            assertNull(request2.getTariffEffectiveDate());
            assertNull(request2.getTariffExpirationDate());

            // Test 8-parameter constructor branch
            TariffCalculationRequest request3 = new TariffCalculationRequest(
                "87654321", "MX", "US", new BigDecimal("2000.00"), 5, "MXN",
                LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
            assertEquals("87654321", request3.getHtsCode());
            assertEquals("MX", request3.getOriginCountry());
            assertEquals("US", request3.getDestinationCountry());
            assertEquals(new BigDecimal("2000.00"), request3.getProductValue());
            assertEquals(5, request3.getQuantity());
            assertEquals("MXN", request3.getCurrency());
            assertEquals(LocalDate.of(2023, 1, 1), request3.getTariffEffectiveDate());
            assertEquals(LocalDate.of(2023, 12, 31), request3.getTariffExpirationDate());
        }

        @Test
        @DisplayName("Should handle null and non-null field branches")
        void tariffCalculationRequest_ShouldHandleNullAndNonNullFieldBranches() {
            TariffCalculationRequest request = new TariffCalculationRequest();

            // Test null branches
            assertNull(request.getHtsCode());
            assertNull(request.getOriginCountry());
            assertNull(request.getDestinationCountry());
            assertNull(request.getProductValue());
            assertNull(request.getQuantity());
            assertNull(request.getTariffEffectiveDate());
            assertNull(request.getTariffExpirationDate());

            // Test non-null branches
            request.setHtsCode("11111111");
            request.setOriginCountry("DE");
            request.setDestinationCountry("FR");
            request.setProductValue(new BigDecimal("3000.00"));
            request.setQuantity(15);
            request.setCurrency("EUR");
            request.setTariffEffectiveDate(LocalDate.of(2023, 6, 1));
            request.setTariffExpirationDate(LocalDate.of(2023, 11, 30));

            assertEquals("11111111", request.getHtsCode());
            assertEquals("DE", request.getOriginCountry());
            assertEquals("FR", request.getDestinationCountry());
            assertEquals(new BigDecimal("3000.00"), request.getProductValue());
            assertEquals(15, request.getQuantity());
            assertEquals("EUR", request.getCurrency());
            assertEquals(LocalDate.of(2023, 6, 1), request.getTariffEffectiveDate());
            assertEquals(LocalDate.of(2023, 11, 30), request.getTariffExpirationDate());
        }

        @Test
        @DisplayName("Should handle toString method branches")
        void tariffCalculationRequest_ShouldHandleToStringBranches() {
            // Test with null fields
            TariffCalculationRequest request1 = new TariffCalculationRequest();
            String toString1 = request1.toString();
            assertTrue(toString1.contains("TariffCalculationRequest{"));
            assertTrue(toString1.contains("htsCode='null'"));
            assertTrue(toString1.contains("originCountry='null'"));
            assertTrue(toString1.contains("destinationCountry='null'"));
            assertTrue(toString1.contains("productValue=null"));
            assertTrue(toString1.contains("quantity=null"));
            assertTrue(toString1.contains("currency='USD'"));
            assertTrue(toString1.contains("tariffEffectiveDate=null"));
            assertTrue(toString1.contains("tariffExpirationDate=null"));

            // Test with non-null fields
            TariffCalculationRequest request2 = new TariffCalculationRequest(
                "12345678", "US", "CA", new BigDecimal("1000.00"), 10, "CAD",
                LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
            String toString2 = request2.toString();
            assertTrue(toString2.contains("TariffCalculationRequest{"));
            assertTrue(toString2.contains("htsCode='12345678'"));
            assertTrue(toString2.contains("originCountry='US'"));
            assertTrue(toString2.contains("destinationCountry='CA'"));
            assertTrue(toString2.contains("productValue=1000.00"));
            assertTrue(toString2.contains("quantity=10"));
            assertTrue(toString2.contains("currency='CAD'"));
            assertTrue(toString2.contains("tariffEffectiveDate=2023-01-01"));
            assertTrue(toString2.contains("tariffExpirationDate=2023-12-31"));
        }
    }

    @Nested
    @DisplayName("TariffCalculationResponse Branch Tests")
    class TariffCalculationResponseBranchTests {

        @Test
        @DisplayName("Should handle different constructor branches")
        void tariffCalculationResponse_ShouldHandleDifferentConstructorBranches() {
            // Test default constructor branch
            TariffCalculationResponse response1 = new TariffCalculationResponse();
            assertNotNull(response1.getCalculationTimestamp());
            assertNull(response1.getHtsCode());
            assertNull(response1.getProductDescription());
            assertNull(response1.getOriginCountry());
            assertNull(response1.getDestinationCountry());
            assertNull(response1.getProductValue());
            assertNull(response1.getQuantity());
            assertNull(response1.getCurrency());

            // Test parameterized constructor branch
            TariffCalculationResponse response2 = new TariffCalculationResponse(
                "12345678", "Test Product", "US", "CA", new BigDecimal("1000.00"), 10, "USD");
            assertNotNull(response2.getCalculationTimestamp());
            assertEquals("12345678", response2.getHtsCode());
            assertEquals("Test Product", response2.getProductDescription());
            assertEquals("US", response2.getOriginCountry());
            assertEquals("CA", response2.getDestinationCountry());
            assertEquals(new BigDecimal("1000.00"), response2.getProductValue());
            assertEquals(10, response2.getQuantity());
            assertEquals("USD", response2.getCurrency());
        }

        @Test
        @DisplayName("Should handle null and non-null field branches")
        void tariffCalculationResponse_ShouldHandleNullAndNonNullFieldBranches() {
            TariffCalculationResponse response = new TariffCalculationResponse();

            // Test null branches
            assertNull(response.getHtsCode());
            assertNull(response.getProductDescription());
            assertNull(response.getOriginCountry());
            assertNull(response.getDestinationCountry());
            assertNull(response.getProductValue());
            assertNull(response.getQuantity());
            assertNull(response.getCurrency());
            assertNull(response.getProgramType());
            assertNull(response.getProgramName());
            assertNull(response.getAppliedRateLabel());
            assertNull(response.getTotalTariffAmount());
            assertNull(response.getTotalImportPrice());
            assertNull(response.getSavingsVsMfn());
            assertNull(response.getApplicableAgreements());
            assertNull(response.getEffectiveDate());
            assertNull(response.getNotes());

            // Test non-null branches
            response.setHtsCode("87654321");
            response.setProductDescription("Updated Product");
            response.setOriginCountry("MX");
            response.setDestinationCountry("US");
            response.setProductValue(new BigDecimal("2000.00"));
            response.setQuantity(5);
            response.setCurrency("MXN");
            response.setProgramType("Preferential");
            response.setProgramName("USMCA");
            response.setAppliedRateLabel("5%");
            response.setTotalTariffAmount(new BigDecimal("100.00"));
            response.setTotalImportPrice(new BigDecimal("2100.00"));
            response.setSavingsVsMfn(new BigDecimal("50.00"));
            response.setEffectiveDate("2023-01-01");
            response.setNotes("Test notes");

            assertEquals("87654321", response.getHtsCode());
            assertEquals("Updated Product", response.getProductDescription());
            assertEquals("MX", response.getOriginCountry());
            assertEquals("US", response.getDestinationCountry());
            assertEquals(new BigDecimal("2000.00"), response.getProductValue());
            assertEquals(5, response.getQuantity());
            assertEquals("MXN", response.getCurrency());
            assertEquals("Preferential", response.getProgramType());
            assertEquals("USMCA", response.getProgramName());
            assertEquals("5%", response.getAppliedRateLabel());
            assertEquals(new BigDecimal("100.00"), response.getTotalTariffAmount());
            assertEquals(new BigDecimal("2100.00"), response.getTotalImportPrice());
            assertEquals(new BigDecimal("50.00"), response.getSavingsVsMfn());
            assertEquals("2023-01-01", response.getEffectiveDate());
            assertEquals("Test notes", response.getNotes());
        }
    }

    @Nested
    @DisplayName("RegistrationDTOs Branch Tests")
    class RegistrationDTOsBranchTests {

        @Test
        @DisplayName("Should handle SigninRequest record branches")
        void signinRequest_ShouldHandleDifferentBranches() {
            // Test with null values
            RegistrationDTOs.SigninRequest request1 = new RegistrationDTOs.SigninRequest(null, null);
            assertNull(request1.email());
            assertNull(request1.password());

            // Test with non-null values
            RegistrationDTOs.SigninRequest request2 = new RegistrationDTOs.SigninRequest("test@example.com", "password123");
            assertEquals("test@example.com", request2.email());
            assertEquals("password123", request2.password());

            // Test with empty strings
            RegistrationDTOs.SigninRequest request3 = new RegistrationDTOs.SigninRequest("", "");
            assertEquals("", request3.email());
            assertEquals("", request3.password());
        }

        @Test
        @DisplayName("Should handle SigninResponse record branches")
        void signinResponse_ShouldHandleDifferentBranches() {
            // Test with null token
            RegistrationDTOs.SigninResponse response1 = new RegistrationDTOs.SigninResponse(null);
            assertNull(response1.token());

            // Test with non-null token
            RegistrationDTOs.SigninResponse response2 = new RegistrationDTOs.SigninResponse("jwt-token-123");
            assertEquals("jwt-token-123", response2.token());

            // Test with empty token
            RegistrationDTOs.SigninResponse response3 = new RegistrationDTOs.SigninResponse("");
            assertEquals("", response3.token());
        }

        @Test
        @DisplayName("Should handle RegisterRequest record branches")
        void registerRequest_ShouldHandleDifferentBranches() {
            // Test with null values
            RegistrationDTOs.RegisterRequest request1 = new RegistrationDTOs.RegisterRequest(null, null, null);
            assertNull(request1.username());
            assertNull(request1.email());
            assertNull(request1.password());

            // Test with non-null values
            RegistrationDTOs.RegisterRequest request2 = new RegistrationDTOs.RegisterRequest("testuser", "test@example.com", "password123");
            assertEquals("testuser", request2.username());
            assertEquals("test@example.com", request2.email());
            assertEquals("password123", request2.password());

            // Test with empty strings
            RegistrationDTOs.RegisterRequest request3 = new RegistrationDTOs.RegisterRequest("", "", "");
            assertEquals("", request3.username());
            assertEquals("", request3.email());
            assertEquals("", request3.password());
        }

        @Test
        @DisplayName("Should handle UserResponse record branches")
        void userResponse_ShouldHandleDifferentBranches() {
            // Test with null values
            RegistrationDTOs.UserResponse response1 = new RegistrationDTOs.UserResponse(null, null, null, null);
            assertNull(response1.id());
            assertNull(response1.username());
            assertNull(response1.email());
            assertNull(response1.role());

            // Test with non-null values
            RegistrationDTOs.UserResponse response2 = new RegistrationDTOs.UserResponse(1L, "testuser", "test@example.com", User.Role.USER);
            assertEquals(1L, response2.id());
            assertEquals("testuser", response2.username());
            assertEquals("test@example.com", response2.email());
            assertEquals(User.Role.USER, response2.role());

            // Test with ADMIN role
            RegistrationDTOs.UserResponse response3 = new RegistrationDTOs.UserResponse(2L, "adminuser", "admin@example.com", User.Role.ADMIN);
            assertEquals(2L, response3.id());
            assertEquals("adminuser", response3.username());
            assertEquals("admin@example.com", response3.email());
            assertEquals(User.Role.ADMIN, response3.role());
        }

        @Test
        @DisplayName("Should handle RegisterResponse record branches")
        void registerResponse_ShouldHandleDifferentBranches() {
            // Test with null user
            RegistrationDTOs.RegisterResponse response1 = new RegistrationDTOs.RegisterResponse(null);
            assertNull(response1.user());

            // Test with non-null user
            RegistrationDTOs.UserResponse userResponse = new RegistrationDTOs.UserResponse(1L, "testuser", "test@example.com", User.Role.USER);
            RegistrationDTOs.RegisterResponse response2 = new RegistrationDTOs.RegisterResponse(userResponse);
            assertNotNull(response2.user());
            assertEquals(1L, response2.user().id());
            assertEquals("testuser", response2.user().username());
            assertEquals("test@example.com", response2.user().email());
            assertEquals(User.Role.USER, response2.user().role());
        }
    }
}
