# VoronoiDiagram

[繁體中文](./doc/readme_multi-language/README_TC.md) | English | [Español](./doc/readme_multi-language/README_ES.md)

voronoi diagram with JavaFx

## Execution Guide

1. Download [Latest Release](https://github.com/dockyu/VoronoiDiagram/releases/latest)
2. Extract the zip
3. Execute `VoronoiDiagram_<version>/bin/VoronoiDiagramApp.bat`

## Algorithm
Divide and Conquer

### Divide
divide all generator points to a complete tree

### Merge

1. find upper tangent and lower tangent with Convex Hull algorithm
2. Iteratively find the mid-perpendicular line from upper tangent to lower tangent
    + delete vertex and edge right from intersection in left voronoi diagram
    + delete vertex and edge left from intersection in right voronoi diagram

### Convex Hull
+ divide and conquer
+ record the rightmost and leftmost point in a convex hull
    + connect rightmost point at left and leftmost point at right as a temp tangent
    + use temp tangent find upper and lower tangent step by step

## Time Complexity
time complexity is **`O(log(n))`**

## Demo
|[170 points](test/170_points.txt)|[205 points](test/205_points.txt)|
|-|-|
|![170 points](doc/pic/170GP.png)|![205 points](doc/pic/205GP.png)|