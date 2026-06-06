package com.sega.stock;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/v1")
public class StockController {

    @GetMapping("/risk-index")
    public String calculateRisk(@RequestParam int lvl) {
        return resolveRiskLevel(lvl);
    }

    private String resolveRiskLevel(int lvl) {
        if (lvl <= 0)  return "High";
        if (lvl < 10)  return lvl % 2 == 0 ? "Low-B" : "Low-A";
        if (lvl < 50)  return "Medium";
        return "High";
    }

    @GetMapping("/process-data")
    public List<Double> heavyProcessing(
            @RequestParam(defaultValue = "100") int limit) {
        int safeLimit = Math.min(limit, 1000);
        return IntStream.range(0, safeLimit)
                .mapToDouble(i -> Math.random() * Math.pow(i, 2))
                .boxed()
                .toList();
    }
}