/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */
package org.elasticsearch.client.ml.job.stats;

import org.elasticsearch.xcontent.XContentParser;
import org.elasticsearch.test.AbstractXContentTestCase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ForecastStatsTests extends AbstractXContentTestCase<ForecastStats> {

    @Override
    public ForecastStats createTestInstance() {
        if (randomBoolean()) {
            return createRandom(1, 22);
        }
        return new ForecastStats(0, null,null,null,null);
    }

    @Override
    protected ForecastStats doParseInstance(XContentParser parser) throws IOException {
        return ForecastStats.PARSER.parse(parser, null);
    }

    @Override
    protected boolean supportsUnknownFields() {
        return true;
    }

    @Override
    protected Predicate<String> getRandomFieldsExcludeFilter() {
        return field -> field.isEmpty() == false;
    }

    public static ForecastStats createRandom(long minTotal, long maxTotal) {
        return new ForecastStats(
            randomLongBetween(minTotal, maxTotal),
            SimpleStatsTests.createRandom(),
            SimpleStatsTests.createRandom(),
            SimpleStatsTests.createRandom(),
            createCountStats());
    }

    private static Map<String, Long> createCountStats() {
        Map<String, Long> countStats = new HashMap<>();
        for (int i = 0; i < randomInt(10); ++i) {
            countStats.put(randomAlphaOfLengthBetween(1, 20), randomLongBetween(1L, 100L));
        }
        return countStats;
    }
}
