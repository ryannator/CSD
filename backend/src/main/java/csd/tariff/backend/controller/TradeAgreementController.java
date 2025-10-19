package csd.tariff.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import csd.tariff.backend.model.TradeAgreement;
import csd.tariff.backend.service.TradeAgreementService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/trade-agreements")
@CrossOrigin(origins = "*")
public class TradeAgreementController {

  @Autowired private TradeAgreementService tradeAgreementService;

  /** Get all trade agreements */
  @GetMapping
  public ResponseEntity<List<TradeAgreement>> getAllTradeAgreements() {
    List<TradeAgreement> agreements = tradeAgreementService.getAllTradeAgreements();
    return ResponseEntity.ok(agreements);
  }

  /** Get trade agreement by agreement code */
  @GetMapping("/{agreementCode}")
  public ResponseEntity<TradeAgreement> getTradeAgreementByCode(
      @PathVariable String agreementCode) {
    Optional<TradeAgreement> agreement =
        tradeAgreementService.getTradeAgreementByCode(agreementCode);
    return agreement.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  /** Create a new trade agreement */
  @PostMapping
  public ResponseEntity<TradeAgreement> createTradeAgreement(
      @Valid @RequestBody TradeAgreement tradeAgreement) {
    try {
      TradeAgreement created = tradeAgreementService.createTradeAgreement(tradeAgreement);
      return ResponseEntity.ok(created);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /** Update an existing trade agreement */
  @PutMapping("/{agreementCode}")
  public ResponseEntity<TradeAgreement> updateTradeAgreement(
      @PathVariable String agreementCode, @Valid @RequestBody TradeAgreement tradeAgreement) {
    try {
      TradeAgreement updated = tradeAgreementService.updateTradeAgreementByCode(agreementCode, tradeAgreement);
      return ResponseEntity.ok(updated);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /** Delete a trade agreement */
  @DeleteMapping("/{agreementCode}")
  public ResponseEntity<Void> deleteTradeAgreement(@PathVariable String agreementCode) {
    try {
      tradeAgreementService.deleteTradeAgreementByCode(agreementCode);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
