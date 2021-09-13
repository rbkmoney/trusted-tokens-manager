package com.rbkmoney.trusted.tokens.model;

import lombok.*;

import java.util.Map;

@Data
public class CardTokenData {
    public Map<String, CurrencyData> payments;
    public Map<String, CurrencyData> withdrawals;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CurrencyData {
        public Map<Integer, YearsData> years;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class YearsData {
        public long yearSum;
        public int yearCount;
        public Map<Integer, MonthsData> months;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthsData {
        public long monthSum;
        public int monthCount;
    }

}
