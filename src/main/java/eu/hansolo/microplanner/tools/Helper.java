/*
 * Copyright (c) 2018 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.microplanner.tools;

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Helper {
    public  static final double  MIN_FONT_SIZE = 5;
    public  static final double  HALF_PI       = Math.PI * 0.5;
    public  static final double  TWO_PI        = Math.PI + Math.PI;
    public  static final double  THREE_PI      = TWO_PI + Math.PI;
    private static final double  EPSILON       = 1E-6;
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
    private static final Matcher FLOAT_MATCHER = FLOAT_PATTERN.matcher("");
    private static final Pattern HEX_PATTERN   = Pattern.compile("#?([A-Fa-f0-9]{8}|[A-Fa-f0-9]{6})");
    private static final Matcher HEX_MATCHER   = HEX_PATTERN.matcher("");

    public static final <T extends Number> T clamp(final T min, final T max, final T value) {
        if (value.doubleValue() < min.doubleValue()) return min;
        if (value.doubleValue() > max.doubleValue()) return max;
        return value;
    }

    public static final int clamp(final int min, final int max, final int value) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
    public static final long clamp(final long min, final long max, final long value) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
    public static final double clamp(final double min, final double max, final double value) {
        if (Double.compare(value, min) < 0) return min;
        if (Double.compare(value, max) > 0) return max;
        return value;
    }

    public static final double clampMin(final double min, final double value) {
        if (value < min) return min;
        return value;
    }
    public static final double clampMax(final double max, final double value) {
        if (value > max) return max;
        return value;
    }

    public static final double round(final double value, final int precision) {
        final int SCALE = (int) Math.pow(10, precision);
        return (double) Math.round(value * SCALE) / SCALE;
    }

    public static final double roundTo(final double value, final double target) { return target * (Math.round(value / target)); }

    public static final double roundToHalf(final double value) { return Math.round(value * 2) / 2.0; }

    public static final double nearest(final double smaller, final double value, final double larger) {
        return (value - smaller) < (larger - value) ? smaller : larger;
    }

    public static int roundDoubleToInt(final double value){
        double dAbs = Math.abs(value);
        int    i      = (int) dAbs;
        double result = dAbs - (double) i;
        if (result < 0.5) {
            return value < 0 ? -i : i;
        } else {
            return value < 0 ? -(i + 1) : i + 1;
        }
    }

    public static final double[] calcAutoScale(final double minValue, final double maxValue) {
        double maxNoOfMajorTicks = 10;
        double maxNoOfMinorTicks = 10;
        double niceMinValue;
        double niceMaxValue;
        double niceRange;
        double majorTickSpace;
        double minorTickSpace;
        niceRange      = (calcNiceNumber((maxValue - minValue), false));
        majorTickSpace = calcNiceNumber(niceRange / (maxNoOfMajorTicks - 1), true);
        niceMinValue   = (Math.floor(minValue / majorTickSpace) * majorTickSpace);
        niceMaxValue   = (Math.ceil(maxValue / majorTickSpace) * majorTickSpace);
        minorTickSpace = calcNiceNumber(majorTickSpace / (maxNoOfMinorTicks - 1), true);
        return new double[]{ niceMinValue, niceMaxValue, majorTickSpace, minorTickSpace };
    }

    /**
     * Can be used to implement discrete steps e.g. on a slider.
     * @param minValue          The min value of the range
     * @param maxValue          The max value of the range
     * @param value             The value to snap
     * @param newMinorTickCount The number of ticks between 2 major tick marks
     * @param newMajorTickUnit  The distance between 2 major tick marks
     * @return The value snapped to the next tick mark defined by the given parameters
     */
    public static double snapToTicks(final double minValue, final double maxValue, final double value, final int newMinorTickCount, final double newMajorTickUnit) {
        double v = value;
        int    minorTickCount = clamp(0, 10, newMinorTickCount);
        double majorTickUnit  = Double.compare(newMajorTickUnit, 0.0) <= 0 ? 0.25 : newMajorTickUnit;
        double tickSpacing;

        if (minorTickCount != 0) {
            tickSpacing = majorTickUnit / (Math.max(minorTickCount, 0) + 1);
        } else {
            tickSpacing = majorTickUnit;
        }

        int    prevTick      = (int) ((v - minValue) / tickSpacing);
        double prevTickValue = prevTick * tickSpacing + minValue;
        double nextTickValue = (prevTick + 1) * tickSpacing + minValue;

        v = nearest(prevTickValue, v, nextTickValue);

        return clamp(minValue, maxValue, v);
    }

    /**
     * Returns a "niceScaling" number approximately equal to the range.
     * Rounds the number if ROUND == true.
     * Takes the ceiling if ROUND = false.
     *
     * @param range the value range (maxValue - minValue)
     * @param round whether to round the result or ceil
     * @return a "niceScaling" number to be used for the value range
     */
    public static final double calcNiceNumber(final double range, final boolean round) {
        double niceFraction;
        double exponent = Math.floor(Math.log10(range));   // exponent of range
        double fraction = range / Math.pow(10, exponent);  // fractional part of range

        if (round) {
            if (Double.compare(fraction, 1.5) < 0) {
                niceFraction = 1;
            } else if (Double.compare(fraction, 3)  < 0) {
                niceFraction = 2;
            } else if (Double.compare(fraction, 7) < 0) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        } else {
            if (Double.compare(fraction, 1) <= 0) {
                niceFraction = 1;
            } else if (Double.compare(fraction, 2) <= 0) {
                niceFraction = 2;
            } else if (Double.compare(fraction, 5) <= 0) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        }
        return niceFraction * Math.pow(10, exponent);
    }

    public static final DateTimeFormatter getDateFormat(final Locale locale) {
        if (Locale.US == locale) {
            return DateTimeFormatter.ofPattern("MM/dd/YYYY");
        } else if (Locale.CHINA == locale) {
            return DateTimeFormatter.ofPattern("YYYY.MM.dd");
        } else {
            return DateTimeFormatter.ofPattern("dd.MM.YYYY");
        }
    }
    public static final DateTimeFormatter getLocalizedDateFormat(final Locale locale) {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale);
    }

    public static final ThreadFactory getThreadFactory(final String threadName, final boolean isDaemon) {
        return runnable -> {
            Thread thread = new Thread(runnable, threadName);
            thread.setDaemon(isDaemon);
            return thread;
        };
    }

    public static final void stopTask(ScheduledFuture<?> task) {
        if (null == task) return;
        task.cancel(true);
        task = null;
    }

    public static final String normalize(final String text) {
        String normalized = text.replaceAll("\u00fc", "ue")
                                .replaceAll("\u00f6", "oe")
                                .replaceAll("\u00e4", "ae")
                                .replaceAll("\u00df", "ss");

        normalized = normalized.replaceAll("\u00dc(?=[a-z\u00fc\u00f6\u00e4\u00df ])", "Ue")
                               .replaceAll("\u00d6(?=[a-z\u00fc\u00f6\u00e4\u00df ])", "Oe")
                               .replaceAll("\u00c4(?=[a-z\u00fc\u00f6\u00e4\u00df ])", "Ae");

        normalized = normalized.replaceAll("\u00dc", "UE")
                               .replaceAll("\u00d6", "OE")
                               .replaceAll("\u00c4", "AE");
        return normalized;
    }

    public static final boolean equals(final double a, final double b) { return a == b || Math.abs(a - b) < EPSILON; }
    public static final boolean biggerThan(final double a, final double b) { return (a - b) > EPSILON; }
    public static final boolean lessThan(final double a, final double b) { return (b - a) > EPSILON; }

    private static final Properties readProperties(final String fileName) {
        final ClassLoader LOADER     = Thread.currentThread().getContextClassLoader();
        final Properties  PROPERTIES = new Properties();
        try(InputStream resourceStream = LOADER.getResourceAsStream(fileName)) {
            PROPERTIES.load(resourceStream);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return PROPERTIES;
    }


    public static final boolean isInRectangle(final double x, final double y,
                                              final double minX, final double minY,
                                              final double maxX, final double maxY) {
        return (Double.compare(x, minX) >= 0 &&
                Double.compare(y, minY) >= 0 &&
                Double.compare(x, maxX) <= 0 &&
                Double.compare(y, maxY) <= 0);
    }

    public static final boolean isInEllipse(final double x, final double y,
                                            final double centerX, final double centerY,
                                            final double radiusX, final double radiusY) {
        return Double.compare(((((x - centerX) * (x - centerX)) / (radiusX * radiusX)) +
                               (((y - centerY) * (y - centerY)) / (radiusY * radiusY))), 1) <= 0.0;
    }

    public static final boolean isInCircle(final double x, final double y, final double centerX, final double centerY, final double radius) {
        double deltaX = centerX - x;
        double deltaY = centerY - y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY) < radius;
    }

    public static final boolean isInPolygon(final double x, final double y, final List<Point> pointsOfPolygon) {
        int noOfPointsInPolygon = pointsOfPolygon.size();
        double[] pointsX = new double[noOfPointsInPolygon];
        double[] pointsY = new double[noOfPointsInPolygon];
        for ( int i = 0 ; i < noOfPointsInPolygon ; i++) {
            Point p = pointsOfPolygon.get(i);
            pointsX[i] = p.getX();
            pointsY[i] = p.getY();
        }
        return isInPolygon(x, y, noOfPointsInPolygon, pointsX, pointsY);
    }
    public static final boolean isInPolygon(final double x, final double y, final int noOfPointsInPolygon, final double[] pointsX, final double[] pointsY) {
        if (noOfPointsInPolygon != pointsX.length || noOfPointsInPolygon != pointsY.length) { return false; }
        boolean inside = false;
        for (int i = 0, j = noOfPointsInPolygon - 1; i < noOfPointsInPolygon ; j = i++) {
            if (((pointsY[i] > y) != (pointsY[j] > y)) && (x < (pointsX[j] - pointsX[i]) * (y - pointsY[i]) / (pointsY[j] - pointsY[i]) + pointsX[i])) {
                inside = !inside;
            }
        }
        return inside;
    }

    public static <T extends Point> boolean isPointInPolygon(final T p, final ArrayList<T> points) {
        boolean inside = false;
        int     size   = points.size();
        double  x      = p.getX();
        double  y      = p.getY();

        for (int i = 0, j = size - 1 ; i < size ; j = i++) {
            if ((points.get(i).getY() > y) != (points.get(j).getY() > y) &&
                (x < (points.get(j).getX() - points.get(i).getX()) * (y - points.get(i).getY()) / (points.get(j).getY() - points.get(i).getY()) + points.get(i).getX())) {
                inside = !inside;
            }
        }
        return inside;
    }


    public static final boolean isInRingSegment(final double x, final double y,
                                                final double centerX, final double centerY,
                                                final double outerRadius, final double innerRadius,
                                                final double newStartAngle, final double segmentAngle) {
        double angleOffset = 90.0;
        double pointRadius = Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
        double pointAngle  = getAngleFromXY(x, y, centerX, centerY, angleOffset);
        double startAngle  = angleOffset - newStartAngle;
        double endAngle    = startAngle + segmentAngle;

        return (Double.compare(pointRadius, innerRadius) >= 0 &&
                Double.compare(pointRadius, outerRadius) <= 0 &&
                Double.compare(pointAngle, startAngle) >= 0 &&
                Double.compare(pointAngle, endAngle) <= 0);
    }

    public static final double distance(final Point p1, final Point p2) {
        return distance(p1.x, p1.y, p2.x, p2.y);
    }
    public static final double distance(final double p1X, final double p1Y, final double p2X, final double p2Y) {
        return Math.sqrt((p2X - p1X) * (p2X - p1X) + (p2Y - p1Y) * (p2Y - p1Y));
    }

    public static double euclideanDistance(final Point p1, final Point p2) { return euclideanDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY()); }
    public static double euclideanDistance(final double x1, final double y1, final double x2, final double y2) {
        double deltaX = (x2 - x1);
        double deltaY = (y2 - y1);
        return (deltaX * deltaX) + (deltaY * deltaY);
    }

    public static final Point pointOnLine(final double p1X, final double p1Y, final double p2X, final double p2Y, final double distanceToP2) {
        double distanceP1P2 = distance(p1X, p1Y, p2X, p2Y);
        double t = distanceToP2 / distanceP1P2;
        return new Point((1 - t) * p1X + t * p2X, (1 - t) * p1Y + t * p2Y);
    }

    public static int checkLineCircleCollision(final Point p1, final Point p2, final double centerX, final double centerY, final double radius) {
        return checkLineCircleCollision(p1.x, p1.y, p2.x, p2.y, centerX, centerY, radius);
    }
    public static int checkLineCircleCollision(final double p1X, final double p1Y, final double p2X, final double p2Y, final double centerX, final double centerY, final double radius) {
        double A = (p1Y - p2Y);
        double B = (p2X - p1X);
        double C = (p1X * p2Y - p2X * p1Y);

        return checkCollision(A, B, C, centerX, centerY, radius);
    }
    public static int checkCollision(final double a, final double b, final double c, final double centerX, final double centerY, final double radius) {
        // Finding the distance of line from center.
        double dist = (Math.abs(a * centerX + b * centerY + c)) / Math.sqrt(a * a + b * b);
        dist = round(dist, 1);
        if (radius > dist) {
            return 1;  // intersect
        } else if (radius < dist) {
            return -1; // outside
        } else {
            return 0;  // touch
        }
    }

    public static final double getAngleFromXY(final double x, final double y, final double centerX, final double centerY) {
        return getAngleFromXY(x, y, centerX, centerY, 90.0);
    }
    public static final double getAngleFromXY(final double x, final double y, final double centerX, final double centerY, final double angleOffset) {
        // For ANGLE_OFFSET =  0 -> Angle of 0 is at 3 o'clock
        // For ANGLE_OFFSET = 90  ->Angle of 0 is at 12 o'clock
        double deltaX      = x - centerX;
        double deltaY      = y - centerY;
        double radius      = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        double nx          = deltaX / radius;
        double ny          = deltaY / radius;
        double theta       = Math.atan2(ny, nx);
        theta              = Double.compare(theta, 0.0) >= 0 ? Math.toDegrees(theta) : Math.toDegrees((theta)) + 360.0;
        double angle       = (theta + angleOffset) % 360;
        return angle;
    }

    public static final double[] rotatePointAroundRotationCenter(final double x, final double y, final double rX, final double rY, final double angle) {
        final double rad = Math.toRadians(angle);
        final double sin = Math.sin(rad);
        final double cos = Math.cos(rad);
        final double nX  = rX + (x - rX) * cos - (y - rY) * sin;
        final double nY  = rY + (x - rX) * sin + (y - rY) * cos;
        return new double[] { nX, nY };
    }

    public static final Point getPointBetweenP1AndP2(final Point p1, final Point p2) {
        double[] xy = getPointBetweenP1AndP2(p1.x, p1.y, p2.x, p2.y);
        return new Point(xy[0], xy[1]);
    }
    public static final double[] getPointBetweenP1AndP2(final double p1X, final double p1Y, final double p2X, final double p2Y) {
        return new double[] { (p1X + p2X) * 0.5, (p1Y + p2Y) * 0.5 };
    }

    public static int getDegrees(final double decDeg) { return (int) decDeg; }
    public static int getMinutes(final double decDeg) { return (int) ((decDeg - getDegrees(decDeg)) * 60); }
    public static double getSeconds(final double decDeg) { return (((decDeg - getDegrees(decDeg)) * 60) - getMinutes(decDeg)) * 60; }

    public static double getDecimalDeg(final int degrees, final int minutes, final double seconds) {
        return (((seconds / 60) + minutes) / 60) + degrees;
    }

    public static final <T> Predicate<T> not(Predicate<T> predicate) { return predicate.negate(); }

    public static final double[] getPointsXFromPoints(final List<Point> points) {
        int size = points.size();
        double[] pointsX = new double[size];
        for (int i = 0 ; i < size ; i++) { pointsX[i] = points.get(i).getX(); }
        return pointsX;
    }
    public static final double[] getPointsYFromPoints(final List<Point> points) {
        int size = points.size();
        double[] pointsY = new double[size];
        for (int i = 0 ; i < size ; i++) { pointsY[i] = points.get(i).getY(); }
        return pointsY;
    }

    public static double getNumberFromText(final String text) {
        FLOAT_MATCHER.reset(text);
        String result = "";
        try {
            while(FLOAT_MATCHER.find()) {
                result = FLOAT_MATCHER.group(0);
            }
        } catch (IllegalStateException ex) {
            return 0;
        }
        return Double.parseDouble(result);
    }

    public static final String getHexColorFromString(final String text) {
        HEX_MATCHER.reset(text);
        String result = "";
        try {
            while (HEX_MATCHER.find()) {
                result = HEX_MATCHER.group(0);
            }
        } catch (IllegalStateException ex) {
            return "-";
        }
        return result;
    }
}
