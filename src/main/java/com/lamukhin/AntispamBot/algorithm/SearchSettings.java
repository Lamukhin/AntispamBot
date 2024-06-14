package com.lamukhin.AntispamBot.algorithm;

import org.springframework.stereotype.Component;

/*
    You can change the search settings using this component without restarting app.
 */

@Component
public class SearchSettings {
    private double coefForCurrentWord = 0.85d;
    private double coefForLowerLimit = 0.35d;
    private double coefForShortMessage = 0.9d;
    private Segment segmentForShort = new Segment(4, 6);
    private double coefForMiddleMessage = 0.7d;
    private Segment segmentForMiddle = new Segment(7, 20);
    private double coefForLongMessage = 0.6d;
    private Segment segmentForLong = new Segment(21, Integer.MAX_VALUE);

    public final class Segment {
        private int start;
        private int end;

        private Segment(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public boolean isInSegment(int amountOfWords) {
            return (this.start <= amountOfWords) && (this.end >= amountOfWords);
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public void setEnd(int end) {
            this.end = end;
        }
    }

    public double getCoefForCurrentWord() {
        return coefForCurrentWord;
    }

    public double getCoefForShortMessage() {
        return coefForShortMessage;
    }

    public Segment getSegmentForShort() {
        return segmentForShort;
    }

    public double getCoefForMiddleMessage() {
        return coefForMiddleMessage;
    }

    public Segment getSegmentForMiddle() {
        return segmentForMiddle;
    }

    public double getCoefForLongMessage() {
        return coefForLongMessage;
    }

    public Segment getSegmentForLong() {
        return segmentForLong;
    }

    public void setCoefForCurrentWord(double coefForCurrentWord) {
        this.coefForCurrentWord = coefForCurrentWord;
    }

    public void setCoefForShortMessage(double coefForShortMessage) {
        this.coefForShortMessage = coefForShortMessage;
    }

    public void setCoefForMiddleMessage(double coefForMiddleMessage) {
        this.coefForMiddleMessage = coefForMiddleMessage;
    }
    public double getCoefForLowerLimit() {
        return coefForLowerLimit;
    }
    public void setCoefForLowerLimit(double coefForLowerLimit) {
        this.coefForLowerLimit = coefForLowerLimit;
    }


    public void setCoefForLongMessage(double coefForLongMessage) {
        this.coefForLongMessage = coefForLongMessage;
    }
}
