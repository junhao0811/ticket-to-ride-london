package model.board;

/** Centre position and rotation of a single train-car slot within a route. */
public record SegmentPoint(double x, double y, double rotationRadians) {}
