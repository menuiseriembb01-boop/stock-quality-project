package com.sega.stock;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class StockController {

    // DEFECT : Complexité cyclomatique élevée
    @GetMapping("/risk-index")
    public String calculateRisk(@RequestParam int lvl) {
        if (lvl > 0) {
            if (lvl < 10) {
                if (lvl % 2 == 0) return "Low-B";
                return "Low-A";
            } else if (lvl < 50) {
                return "Medium";
            }
        }
        return "High";
    }

    // DEFECT : Goulot d'étranglement performance
    @GetMapping("/process-data")
    public List<Double> heavyProcessing() {
        List<Double> results = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            results.add(Math.random() * Math.pow(i, 2));
        }
        return results;
    }
}