/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */
package org.elasticsearch.client.ml;

import org.elasticsearch.core.Nullable;
import org.elasticsearch.xcontent.ParseField;
import org.elasticsearch.xcontent.ConstructingObjectParser;
import org.elasticsearch.xcontent.ToXContentObject;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentParser;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

/**
 * Response object containing flush acknowledgement and additional data
 */
public class FlushJobResponse implements ToXContentObject {

    public static final ParseField FLUSHED = new ParseField("flushed");
    public static final ParseField LAST_FINALIZED_BUCKET_END = new ParseField("last_finalized_bucket_end");

    public static final ConstructingObjectParser<FlushJobResponse, Void> PARSER =
        new ConstructingObjectParser<>("flush_job_response",
            true,
            (a) -> {
                boolean flushed = (boolean) a[0];
                Date date = a[1] == null ? null : new Date((long) a[1]);
                return new FlushJobResponse(flushed, date);
            });

    static {
        PARSER.declareBoolean(ConstructingObjectParser.constructorArg(), FLUSHED);
        PARSER.declareLong(ConstructingObjectParser.optionalConstructorArg(), LAST_FINALIZED_BUCKET_END);
    }

    public static FlushJobResponse fromXContent(XContentParser parser) throws IOException {
        return PARSER.parse(parser, null);
    }

    private final boolean flushed;
    private final Date lastFinalizedBucketEnd;

    public FlushJobResponse(boolean flushed, @Nullable Date lastFinalizedBucketEnd) {
        this.flushed = flushed;
        this.lastFinalizedBucketEnd = lastFinalizedBucketEnd;
    }

    /**
     * Was the job successfully flushed or not
     */
    public boolean isFlushed() {
        return flushed;
    }

    /**
     * Provides the timestamp (in milliseconds-since-the-epoch) of the end of the last bucket that was processed.
     */
    @Nullable
    public Date getLastFinalizedBucketEnd() {
        return lastFinalizedBucketEnd;
    }

    @Override
    public int hashCode() {
        return Objects.hash(flushed, lastFinalizedBucketEnd);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        FlushJobResponse that = (FlushJobResponse) other;
        return that.flushed == flushed && Objects.equals(lastFinalizedBucketEnd, that.lastFinalizedBucketEnd);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field(FLUSHED.getPreferredName(), flushed);
        if (lastFinalizedBucketEnd != null) {
            builder.timeField(LAST_FINALIZED_BUCKET_END.getPreferredName(),
                LAST_FINALIZED_BUCKET_END.getPreferredName() + "_string", lastFinalizedBucketEnd.getTime());
        }
        builder.endObject();
        return builder;
    }
}
