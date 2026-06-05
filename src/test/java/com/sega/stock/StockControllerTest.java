package com.sega.stock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {StockController.class, StockControllerOptimized.class})
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ---- Tests sur le contrôleur original ----

    @Test
    void testRiskLowB() throws Exception {
        mockMvc.perform(get("/api/v1/risk-index").param("lvl", "4"))
               .andExpect(status().isOk())
               .andExpect(content().string("Low-B"));
    }

    @Test
    void testRiskLowA() throws Exception {
        mockMvc.perform(get("/api/v1/risk-index").param("lvl", "3"))
               .andExpect(status().isOk())
               .andExpect(content().string("Low-A"));
    }

    @Test
    void testRiskMedium() throws Exception {
        mockMvc.perform(get("/api/v1/risk-index").param("lvl", "25"))
               .andExpect(status().isOk())
               .andExpect(content().string("Medium"));
    }

    @Test
    void testRiskHigh_negativeValue() throws Exception {
        mockMvc.perform(get("/api/v1/risk-index").param("lvl", "-5"))
               .andExpect(status().isOk())
               .andExpect(content().string("High"));
    }

    @Test
    void testRiskHigh_largeValue() throws Exception {
        mockMvc.perform(get("/api/v1/risk-index").param("lvl", "100"))
               .andExpect(status().isOk())
               .andExpect(content().string("High"));
    }

    // ---- Tests sur le contrôleur optimisé ----

    @Test
    void testOptimizedProcessDataDefaultLimit() throws Exception {
        mockMvc.perform(get("/api/v1/process-data"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(100));
    }

    @Test
    void testOptimizedProcessDataCustomLimit() throws Exception {
        mockMvc.perform(get("/api/v1/process-data").param("limit", "50"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(50));
    }

    @Test
    void testOptimizedProcessDataMaxCap() throws Exception {
        // Demande 5000 mais doit être plafonné à 1000
        mockMvc.perform(get("/api/v1/process-data").param("limit", "5000"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(1000));
    }
}
