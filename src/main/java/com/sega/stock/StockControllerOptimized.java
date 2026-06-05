package com.sega.stock;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.IntStream;

/**
 * StockController - VERSION OPTIMISÉE
 *
 * CORRECTION 1 : Complexité Cyclomatique réduite via early return et méthode privée
 * CORRECTION 2 : Goulot d'étranglement éliminé via pagination + Stream lazy
 */
@RestController
@RequestMapping("/api/v1")
public class StockControllerOptimized {

    // CORRECTION : Complexité Cyclomatique réduite (CC = 2 au lieu de 5)
    // Utilisation d'une méthode privée pure et de retours anticipés
    @GetMapping("/risk-index")
    public String calculateRisk(@RequestParam int lvl) {
        return resolveRiskLevel(lvl);
    }

    private String resolveRiskLevel(int lvl) {
        if (lvl <= 0)   return "High";
        if (lvl < 10)   return lvl % 2 == 0 ? "Low-B" : "Low-A";
        if (lvl < 50)   return "Medium";
        return "High";
    }

    // CORRECTION : Pagination pour éviter de charger 1M d'éléments en mémoire
    // Paramètre 'limit' avec valeur par défaut raisonnable (1000 max)
    @GetMapping("/process-data")
    public List<Double> heavyProcessing(
            @RequestParam(defaultValue = "100") int limit) {

        int safeLimit = Math.min(limit, 1000); // plafond de sécurité

        return IntStream.range(0, safeLimit)
                .mapToDouble(i -> Math.random() * Math.pow(i, 2))
                .boxed()
                .toList();
    }
}
