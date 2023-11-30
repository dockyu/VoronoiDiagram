# Diagrama de Voronoi

[繁體中文](./README_TC.md) | [English](../../README.md) | Español

Diagrama de Voronoi con JavaFx

## Guía de Ejecución

1. Descargar [Última Versión](https://github.com/dockyu/VoronoiDiagram/releases/latest)
2. Extraer el archivo zip
3. Ejecutar `VoronoiDiagram_<version>/bin/VoronoiDiagramApp.bat`

## Algoritmo
Divide y Vencerás

### Dividir
Dividir todos los puntos generadores en un árbol completo

### Combinar

1. Encontrar la tangente superior e inferior con el algoritmo de Convex Hull
2. Encontrar iterativamente la línea perpendicular media desde la tangente superior hasta la inferior
    + Eliminar el vértice y el borde a la derecha de la intersección en el diagrama de Voronoi izquierdo
    + Eliminar el vértice y el borde a la izquierda de la intersección en el diagrama de Voronoi derecho

### Convex Hull
+ Dividir y vencer
+ Registrar el punto más a la derecha y más a la izquierda en un Convex Hull
    + Conectar el punto más a la derecha a la izquierda y el punto más a la izquierda a la derecha como una tangente temporal
    + Usar la tangente temporal para encontrar la tangente superior e inferior paso a paso

## Complejidad Temporal
La complejidad temporal es **`O(log(n))`**

## Demostración
|[170 puntos](../../test/170_points.txt)|[205 puntos](../../test/205_points.txt)|
|-|-|
|![170 puntos](../pic/170GP.png)|![205 puntos](../pic/205GP.png)|
