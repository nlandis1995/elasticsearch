/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */
package org.elasticsearch.client.ml.calendars;

import org.elasticsearch.client.common.TimeUtil;
import org.elasticsearch.core.Nullable;
import org.elasticsearch.xcontent.ParseField;
import org.elasticsearch.xcontent.ConstructingObjectParser;
import org.elasticsearch.xcontent.ObjectParser;
import org.elasticsearch.xcontent.ToXContentObject;
import org.elasticsearch.xcontent.XContentBuilder;


import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public class ScheduledEvent implements ToXContentObject {

    public static final ParseField DESCRIPTION = new ParseField("description");
    public static final ParseField START_TIME = new ParseField("start_time");
    public static final ParseField END_TIME = new ParseField("end_time");
    public static final ParseField EVENT_ID = new ParseField("event_id");
    public static final String SCHEDULED_EVENT_TYPE = "scheduled_event";

    public static final ConstructingObjectParser<ScheduledEvent, Void> PARSER =
            new ConstructingObjectParser<>(SCHEDULED_EVENT_TYPE, true, a ->
                    new ScheduledEvent((String) a[0], (Date) a[1], (Date) a[2], (String) a[3], (String) a[4]));

    static {
        PARSER.declareString(ConstructingObjectParser.constructorArg(), DESCRIPTION);
        PARSER.declareField(ConstructingObjectParser.constructorArg(),(p) -> TimeUtil.parseTimeField(p, START_TIME.getPreferredName()),
                START_TIME, ObjectParser.ValueType.VALUE);
        PARSER.declareField(ConstructingObjectParser.constructorArg(),(p) -> TimeUtil.parseTimeField(p, END_TIME.getPreferredName()),
                END_TIME, ObjectParser.ValueType.VALUE);
        PARSER.declareString(ConstructingObjectParser.constructorArg(), Calendar.ID);
        PARSER.declareString(ConstructingObjectParser.optionalConstructorArg(), EVENT_ID);
    }

    private final String description;
    private final Date startTime;
    private final Date endTime;
    private final String calendarId;
    private final String eventId;

    ScheduledEvent(String description, Date startTime, Date endTime, String calendarId, @Nullable String eventId) {
        this.description = Objects.requireNonNull(description);
        this.startTime = Objects.requireNonNull(startTime);
        this.endTime = Objects.requireNonNull(endTime);
        this.calendarId = Objects.requireNonNull(calendarId);
        this.eventId = eventId;
    }

    public String getDescription() {
        return description;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public String getEventId() {
        return eventId;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field(DESCRIPTION.getPreferredName(), description);
        builder.timeField(START_TIME.getPreferredName(), START_TIME.getPreferredName() + "_string", startTime.getTime());
        builder.timeField(END_TIME.getPreferredName(), END_TIME.getPreferredName() + "_string", endTime.getTime());
        builder.field(Calendar.ID.getPreferredName(), calendarId);
        if (eventId != null) {
            builder.field(EVENT_ID.getPreferredName(), eventId);
        }
        builder.endObject();
        return builder;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ScheduledEvent other = (ScheduledEvent) obj;
        return Objects.equals(this.description, other.description)
                && Objects.equals(this.startTime, other.startTime)
                && Objects.equals(this.endTime, other.endTime)
                && Objects.equals(this.calendarId, other.calendarId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, startTime, endTime, calendarId);
    }
}
