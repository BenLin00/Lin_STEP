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
import java.util.List;

    // first iterate through and combine events that overlap
    // subtract combined events calendar from whole day
    // remove time ranges that are too short 

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        List<TimeRange> avaliableRanges = new ArrayList<TimeRange>();
        avaliableRanges.add(TimeRange.WHOLE_DAY);
        Collection<TimeRange> bookedRanges = combinedMandatoryRanges(events, request);

        for (TimeRange booked : bookedRanges) {
            TimeRange lastInAvaliable = avaliableRanges.get(avaliableRanges.size()-1);
            if (lastInAvaliable.overlaps(booked)){
                List<TimeRange> newLastAvaliable = removeOverlap(lastInAvaliable, booked);
                avaliableRanges.remove(lastInAvaliable);
                avaliableRanges.addAll(newLastAvaliable);
                Collections.sort(avaliableRanges, TimeRange.ORDER_BY_START);
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

// returns set of TimeRanges formed by all events that have mandatory attendees from the request with no overlap
  public Collection<TimeRange> combinedMandatoryRanges(Collection<Event> events, MeetingRequest request) {
        List<TimeRange> busyTimes = new ArrayList<TimeRange>();
        for (Event event : events) {
            if (!Collections.disjoint(event.getAttendees(), request.getAttendees()) ) { //events with mandatory attendees only
                busyTimes.add(event.getWhen());
                System.out.println(event.getWhen());
            }
        }
        Collections.sort(busyTimes, TimeRange.ORDER_BY_START);

        List<TimeRange> combinedBusyTimes = new ArrayList<TimeRange>();

        int i = 0;
        int start = -1;
        int end;
        while (i < busyTimes.size()) {
            if (start == -1) {
                start = busyTimes.get(i).start(); // set Start of TimeRange
            }
            
            if (i == busyTimes.size()-1){ // On the last TimeRange
                end = busyTimes.get(i).end();
                combinedBusyTimes.add(TimeRange.fromStartEnd(start, end, true));
                break;
            }

            if (busyTimes.get(i).end() > busyTimes.get(i+1).start()){ // combine with next TimeRange
                i++;
                continue;
            } else {
                end = busyTimes.get(i).end();
                combinedBusyTimes.add(TimeRange.fromStartEnd(start, end, true));
                start = -1; // reset start for a new TimeRange to add
                i++;
            }
        }

        return combinedBusyTimes;
  }


  public List<TimeRange> removeOverlap(TimeRange avaliable, TimeRange booked) {
        List<TimeRange> cleansed = new ArrayList<TimeRange>();
    // Case 1: |-a-|
    //           |-b-|
    //
    // Case 2:    |-a-|
    //         |-b-|
    //
    // Case 3: |----a----|
    //            |-b-|
        if (avaliable.contains(booked.start()) && !avaliable.contains(booked.end())) { // case 1
            cleansed.add(avaliable.fromStartEnd(avaliable.start(), booked.start(), false));
        } else if (!avaliable.contains(booked.start()) && avaliable.contains(booked.end())) { // case 2
            cleansed.add(TimeRange.fromStartEnd(booked.end(), avaliable.end(), false));
        } else {
            cleansed.add(TimeRange.fromStartEnd(avaliable.start(), booked.start(), false));
            cleansed.add(TimeRange.fromStartEnd(booked.end(), avaliable.end(), false));
        }
        return cleansed;
  }

}
