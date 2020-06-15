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
import java.util.HashSet;

    // first iterate through and combine events that overlap
    // subtract combined events calendar from whole day
    // remove time ranges that are too short 

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        Collection<TimeRange> avaliableRanges = new HashSet<TimeRange>();
        Collection<TimeRange> tempAvaliable = new HashSet<TimeRange>(); 
        avaliableRanges.add(TimeRange.WHOLE_DAY);
        Collection<TimeRange> bookedRanges = combinedMandatoryRanges(events, request);

        for (TimeRange booked : bookedRanges) {
            for (TimeRange avaliable : avaliableRanges) {
                if (avaliable.overlaps(booked)) {
                    tempAvaliable = removeOverlap(avaliable, booked);
                    removeOverlap(avaliable, booked);
                    avaliableRanges.remove(avaliable);
                    avaliableRanges.addAll(tempAvaliable);
                }
            }            
        }

        // remove TimeRanges too small
        for (TimeRange avaliable : avaliableRanges) {
            if (avaliable.duration() < request.getDuration()) {
                avaliableRanges.remove(avaliable);
            }
        }

        return avaliableRanges;
  }

  public Collection<TimeRange> removeOverlap(TimeRange avaliable, TimeRange booked) {
        Collection<TimeRange> cleansed = new HashSet<TimeRange>();
    // Case 1: |-a-|
    //           |-b-|
    //
    // Case 2:    |-a-|
    //         |-b-|
    //
    // Case 3: |----a----|
    //            |-b-|
        if (avaliable.contains(booked.start()) && !avaliable.contains(booked.end())) {
            cleansed.add(avaliable.fromStartEnd(avaliable.start(), booked.start(), false));
        } else if (!avaliable.contains(booked.start()) && avaliable.contains(booked.end())) {
            cleansed.add(TimeRange.fromStartEnd(booked.end() + 1, avaliable.end(), true)); // double check this "+ 1"
        } else {
            cleansed.add(TimeRange.fromStartEnd(avaliable.start(), booked.start(), false));
            cleansed.add(TimeRange.fromStartEnd(booked.end() + 1, avaliable.end(), true)); // doublecheck the "+1"
        }
        return cleansed;
  }


// returns set of TimeRanges formed by all events that have mandatory attendees from the request with no overlap
  public Collection<TimeRange> combinedMandatoryRanges(Collection<Event> events, MeetingRequest request) {
        Collection<TimeRange> eventRanges = new HashSet<TimeRange>(); // not sure if need to declare new
        for (Event event : events) {
            if (!Collections.disjoint(event.getAttendees(), request.getAttendees()) ) { //events with mandatory attendees only
                eventRanges.add(event.getWhen());
            }
        }

        Collection<TimeRange> combinedEvents = new HashSet<TimeRange>();

        // remove overlapping events from mandatory attendees and return combined TimeRanges 
        for (TimeRange uncombinedRange : eventRanges) {
            for (TimeRange combinedRange : combinedEvents) {
                if (combinedRange.overlaps(uncombinedRange)) {
                    eventRanges.add(combineOverlappingTimeRanges(uncombinedRange, combinedRange));
                }
            }
        }
        return combinedEvents;
  }

// takes in two overlapping Time Ranges and combines them, returning one TimeRange
  public TimeRange combineOverlappingTimeRanges(TimeRange a, TimeRange b) {
      if (a.end() < b.start() || b.end() < a.start()){
          throw new IllegalArgumentException("TimeRanges don't overlap! cannot combine");
      }
      int start = (a.start() < b.start() )? a.start() : b.start();
      int end = (a.end() < b.end() )? b.end() : a.end();

      return TimeRange.fromStartEnd(start, end, true);
  }

}
