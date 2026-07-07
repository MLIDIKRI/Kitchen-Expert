package com.pakar.rekomendasimasakan.utils;

import com.pakar.rekomendasimasakan.models.Masakan;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForwardChainingEngine {

    public static List<Masakan> getRecommendations(List<Integer> selectedBahanIds, List<Masakan> allMasakan) {
        List<Masakan> results = new ArrayList<>();

        for (Masakan masakan : allMasakan) {
            int matchCount = 0;
            List<Integer> requiredBahanIds = masakan.getBahanIds();
            
            if (requiredBahanIds == null || requiredBahanIds.isEmpty()) continue;

            for (Integer requiredId : requiredBahanIds) {
                if (selectedBahanIds.contains(requiredId)) {
                    matchCount++;
                }
            }

            if (matchCount > 0) {
                double percentage = (double) matchCount / requiredBahanIds.size() * 100;
                masakan.setMatchPercentage(percentage);
                masakan.setMatchCount(matchCount);
                results.add(masakan);
            }
        }

        // Sort by percentage descending
        Collections.sort(results, (m1, m2) -> {
            int cmp = Double.compare(m2.getMatchPercentage(), m1.getMatchPercentage());
            if (cmp == 0) {
                return Integer.compare(m2.getMatchCount(), m1.getMatchCount());
            }
            return cmp;
        });

        return results;
    }
}