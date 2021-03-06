// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;

    // first iterate through and combine events that overlap
    // subtract combined events calendar from whole day
    // remove time ranges that are too short 

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        /* try to remove the timeRanges optional attendees can't make it to. If there's nothing left, ignore the optional attendees*/
        List<TimeRange> optionalAttendeesConsideredRanges = availableRanges(events, request, /* considerOptionalAttendees= */ true);

        if (!optionalAttendeesConsideredRanges.isEmpty()) { // if optional Attendees can attend
            return optionalAttendeesConsideredRanges;
        } else if (request.getAttendees().isEmpty()) { // no mandatory attendees requested
            return Collections.emptyList(); // return empty list
        } else {
            return availableRanges(events, request, false); // return mandatory attendees ranges only, ignoring optional attendees
        }

  }

  public List<TimeRange> availableRanges(Collection<Event> events, MeetingRequest request, boolean considerOptionalAttendees) {
        List<TimeRange> availableRanges = new ArrayList<>();
        availableRanges.add(TimeRange.WHOLE_DAY);

        Collection<TimeRange> bookedRanges = combinedRanges(events, request, considerOptionalAttendees);

        /* The bookedRanges is already sorted, so iterating from the earliest bookedRange, 
            each subsequent bookedRange will have a conflict with the latest available range, as we start with the whole day */
        for (TimeRange booked : bookedRanges) {
            TimeRange lastInAvailable = availableRanges.get(availableRanges.size()-1);
            List<TimeRange> newLastavailable = removeOverlap(lastInAvailable, booked);
            availableRanges.remove(lastInAvailable);
            availableRanges.addAll(newLastavailable);
        }

        // remove available TimeRanges too small
        List<TimeRange> toRemove = new ArrayList<>();
        for (int i = 0; i < availableRanges.size(); i++) {
            TimeRange available = availableRanges.get(i);            
            if (available.duration() < request.getDuration()) {
                toRemove.add(available);
            }
        }
        availableRanges.removeAll(toRemove);
        
        return availableRanges;

  }


/** 
*returns set of TimeRanges formed by all events that have mandatory attendees from the request with no overlap
* if mandatoryAttendees is true, we return the combinedRanges of mandatoryAttendees. else, return combinedRanges of optionalAttendees
*/
  public Collection<TimeRange> combinedRanges(Collection<Event> events, MeetingRequest request, boolean considerOptionalAttendees) {
        List<TimeRange> busyTimes = new ArrayList<>();
        Set<String> attendees = new HashSet<>(request.getAttendees());

        if (considerOptionalAttendees) {
            attendees.addAll(request.getOptionalAttendees());
        }
        
        for (Event event : events) {
            if (!Collections.disjoint(event.getAttendees(), attendees) ) { //events with request attendees only
                    busyTimes.add(event.getWhen());
            }
        }

        Collections.sort(busyTimes, TimeRange.ORDER_BY_START);

        List<TimeRange> combinedBusyTimes = new ArrayList<>();

        int start = -1;
        int latestEnd = 0;
        int end = 0;
        for (int i = 0; i < busyTimes.size(); i++) {
            if (start == -1) {
                start = busyTimes.get(i).start(); // set Start of TimeRange
            }

            end = busyTimes.get(i).end();
            latestEnd = Math.max(end, latestEnd);

            if (i == busyTimes.size()-1){ // On the last TimeRange
                end = busyTimes.get(i).end();
                combinedBusyTimes.add(TimeRange.fromStartEnd(start, latestEnd, false));
                break;
            }

            if (latestEnd > busyTimes.get(i+1).end()) {
                continue;
            }

            if (latestEnd < busyTimes.get(i+1).start()){
                combinedBusyTimes.add(TimeRange.fromStartEnd(start, latestEnd, false));
                start = -1; // reset start for a new TimeRange to add
            }
        }

        return combinedBusyTimes;
  }

/**
    available and booked will always overlap when this function is called
*/
  public List<TimeRange> removeOverlap(TimeRange available, TimeRange booked) {
        List<TimeRange> cleansed = new ArrayList<>();
    // Case 1: |-a-|
    //           |-b-|
    //
    // Case 2:    |-a-|
    //         |-b-|
    //
    // Case 3: |----a----|
    //            |-b-|
        if (available.contains(booked.start()) && !available.contains(booked.end())) { // case 1
            cleansed.add(available.fromStartEnd(available.start(), booked.start(), false));
        } else if (!available.contains(booked.start()) && available.contains(booked.end())) { // case 2
            cleansed.add(TimeRange.fromStartEnd(booked.end(), available.end(), false));
        } else {
            cleansed.add(TimeRange.fromStartEnd(available.start(), booked.start(), false));
            cleansed.add(TimeRange.fromStartEnd(booked.end(), available.end(), false));
        }
        return cleansed;
  }

}
