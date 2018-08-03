/*
 * Copyright (C) 2018 The Android parsePage Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tools.idea.resourceExplorer.sketchImporter;

import com.android.tools.idea.resourceExplorer.sketchImporter.structure.DrawableShape;
import com.android.tools.idea.resourceExplorer.sketchImporter.structure.SketchArtboard;
import com.android.tools.idea.resourceExplorer.sketchImporter.structure.SketchPage;
import com.google.common.collect.ImmutableList;
import org.jetbrains.android.AndroidTestBase;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class PathsTest {

  @Test
  public void linePathTest() {

    SketchPage sketchPage = SketchParser.parsePage(AndroidTestBase.getTestDataPath() + "/sketch/" + "paths_linePath.json");

    List<String> artboardPaths = getFirstArtboardPaths(sketchPage);

    assertEquals("M5.0,12.0 L11.0,8.0 ", artboardPaths.get(0));
  }

  @Test
  public void curvePathTest() {

    SketchPage sketchPage = SketchParser.parsePage(AndroidTestBase.getTestDataPath() + "/sketch/" + "paths_curvePath.json");

    List<String> artboardPaths = getFirstArtboardPaths(sketchPage);

    assertEquals("M4.0,10.0 C4.0,7.0 5.0,6.0 8.0,6.0 C11.0,6.0 12.0,7.0 12.0,10.0 ", artboardPaths.get(0));
  }

  @Test
  public void rectanglePathTest() {
    SketchPage sketchPage = SketchParser.parsePage(AndroidTestBase.getTestDataPath() + "/sketch/" + "paths_rectanglePath.json");

    List<String> artboardPaths = getFirstArtboardPaths(sketchPage);

    assertEquals("M26.0,1.0 L103.0,1.0 L103.0,21.0 L26.0,21.0 z", artboardPaths.get(0));
  }

  @Test

  public void roundRectanglePathTest() {
    SketchPage sketchPage = SketchParser.parsePage(AndroidTestBase.getTestDataPath() + "/sketch/" + "paths_roundRectanglePath.json");

    List<String> artboardPaths = getFirstArtboardPaths(sketchPage);


    assertEquals("M30.0,20.0 L30.0,20.0 L90.0,20.0 Q110.0,20.0 110.0,40.0 L110.0,80.0 Q110.0,120.0 70.0,120.0 Q30.0,120.0 30.0,80.0 z",
                 artboardPaths.get(0));
  }

  @Test
  public void singleShapePathTest() {
    SketchPage sketchPage = SketchParser.parsePage(AndroidTestBase.getTestDataPath() + "/sketch/" + "paths_singleShapePath.json");

    List<String> artboardPaths = getFirstArtboardPaths(sketchPage);

    assertEquals("M149.0,59.0 L201.0,304.0 L97.0,304.0 ", artboardPaths.get(0));
  }

  @Test
  public void shapeUnionTest() {
    SketchPage sketchPage = SketchParser.parsePage(AndroidTestBase.getTestDataPath() + "/sketch/" + "paths_shapeUnion.json");

    List<String> artboardPaths = getFirstArtboardPaths(sketchPage);

    assertEquals(
      "M268.5,34.0 C227.22681106905614,34.0 194.76227556749063,66.29839590772073 194.50158121846573,107.5 L50.0,107.5 L50.0,325.5 L226.5027875876822,325.5 C226.5009307973233,325.66640960999274 226.5,325.83307787808866 226.5,326.0 C226.5,348.68 244.98000000000002,368.0 268.5,368.0 C291.18,368.0 310.5,348.68 310.5,326.0 C310.5,302.48 291.18,284.0 268.5,284.0 C268.3329080451577,284.0 268.1660704609547,284.00093269234424 267.99949206635785,284.0027932580658 L268.0,284.0027932580658 L268.0,181.9983471275387 L268.00000002679803,181.9983471275387 C268.166521767314,181.99944843113553 268.33318893989207,182.0 268.5,182.0 C308.46000000000004,182.0 342.5,147.96 342.5,108.0 C342.5,66.56 308.46000000000004,34.0 268.5,34.0 z",
      artboardPaths.get(0));
  }

  @Test
  public void shapeSubtractionTest() {
    SketchPage sketchPage = SketchParser.parsePage(AndroidTestBase.getTestDataPath() + "/sketch/" + "paths_shapeSubstraction.json");

    List<String> artboardPaths = getFirstArtboardPaths(sketchPage);

    assertEquals(
      "M50.0,107.5 L50.0,325.5 L226.5027875876822,325.5 C226.76070136586222,302.3852035272229 244.88476870409968,284.26096876789927 267.99949206635785,284.0027932580658 L268.0,284.0027932580658 L268.0,181.9983471275387 L268.00000002679803,181.9983471275387 C226.79839592172561,181.7258573333742 194.5,147.79914647775308 194.5,108.0 C194.5,107.83318893094386 194.50052758766608,107.66652174943303 194.50158121846573,107.5 z",

      artboardPaths.get(0));
  }

  @Test
  public void shapeDifferenceTest() {
    SketchPage sketchPage = SketchParser.parsePage(AndroidTestBase.getTestDataPath() + "/sketch/" + "paths_shapeDifference.json");

    List<String> artboardPaths = getFirstArtboardPaths(sketchPage);

    assertEquals(
      "M268.5,34.0 C227.22681106905614,34.0 194.76227556749063,66.29839590772073 194.50158121846573,107.5 L268.0,107.5 L268.0,181.99834712720335 L267.99999997609257,181.99834712720335 C226.79839589522658,181.72585730553925 194.5,147.79914646142657 194.5,108.0 C194.5,107.83318893094386 194.50052758766608,107.66652174943303 194.50158121846573,107.5 L50.0,107.5 L50.0,325.5 L226.5027875876822,325.5 C226.76070136586222,302.3852035272229 244.88476870409968,284.26096876789927 267.99949206635785,284.0027932580658 L268.0,284.0027932580658 L268.0,181.9983471275387 L268.00000002679803,181.9983471275387 C268.166521767314,181.99944843113553 268.33318893989207,182.0 268.5,182.0 C308.46000000000004,182.0 342.5,147.96 342.5,108.0 C342.5,66.56 308.46000000000004,34.0 268.5,34.0 zM268.5,284.0 C268.3329080451577,284.0 268.1660704609547,284.00093269234424 267.99949206635785,284.0027932580658 L268.0,284.0027932580658 L268.0,325.5 L226.5027875876822,325.5 C226.5009307973233,325.66640960999274 226.5,325.83307787808866 226.5,326.0 C226.5,348.68 244.98000000000002,368.0 268.5,368.0 C291.18,368.0 310.5,348.68 310.5,326.0 C310.5,302.48 291.18,284.0 268.5,284.0 z",
      artboardPaths.get(0));
  }

  @Test
  public void shapeIntersectTest() {

    SketchPage sketchPage = SketchParser.parsePage(AndroidTestBase.getTestDataPath() + "/sketch/" + "paths_shapeIntersect.json");

    List<String> artboardPaths = getFirstArtboardPaths(sketchPage);

    assertEquals(
      "M194.50158121846576,107.5 L194.50158121846573,107.5 C194.50052758766608,107.66652174943303 194.5,107.83318893094386 194.5,108.0 C194.5,147.79914646142657 226.79839589522658,181.72585730553925 267.99999997609257,181.99834712720335 L268.0,181.99834712720335 L268.0,107.5 z",
      artboardPaths.get(0));
  }

  @Test
  public void combinationsSingleArtboardTest() {

    SketchPage sketchPage = SketchParser.parsePage(AndroidTestBase.getTestDataPath() + "/sketch/" + "paths_combinationsSingleArtboard.json");

    List<String> artboardPaths = getFirstArtboardPaths(sketchPage);

    assertEquals(
      "M71.0,180.0 C94.19595949160001,180.0 113.0,198.8040405084 113.0,222.0 C113.0,245.1959594916 94.19595949160001,264.0 71.0,264.0 C47.8040405084,264.0 29.0,245.1959594916 29.0,222.0 C29.0,198.8040405084 47.8040405084,180.0 71.0,180.0 zM292.0,225.0 C290.53687702269923,225.0 289.0912280038278,225.0748149097022 287.66666981740974,225.22082785508263 L287.666704987032,225.22082785508263 C296.67716365264556,237.48052750929673 302.0,252.61865623396764 302.0,269.0 C302.0,283.72161440201893 297.70112376503965,297.4391724612367 290.29021176429956,308.9658337084727 L290.29025900147064,308.9658337084727 C290.8573512194103,308.98853894809037 291.42733717365394,309.0 292.0,309.0 C315.1959594916,309.0 334.0,290.1959594916 334.0,267.0 C334.0,243.8040405084 315.1959594916,225.0 292.0,225.0 zM10.0,163.99999999999997 L10.0,382.47307526750274 L77.00260938615055,382.47307526750274 C77.00087131065143,382.315590047711 77.0,382.1578967766088 77.0,382.0 C77.0,358.8040405084 95.80404050839999,340.0 119.0,340.0 C142.1959594916,340.0 161.0,358.8040405084 161.0,382.0 C161.0,382.1578967766088 160.99912868934857,382.315590047711 160.99739061384943,382.47307526750274 L228.4730752675027,382.47307526750274 L228.4730752675027,342.9985195540348 L228.47307524465882,342.9985195540348 C254.42448203866866,342.83605474284224 277.2069316341925,329.31508870534464 290.29021176429956,308.9658337084727 L290.29025900147064,308.9658337084727 C267.8870316698388,308.06885323742677 250.0,289.6232966652539 250.0,267.0 C250.0,245.26716348570076 266.50666436957954,227.38966500695207 287.66666981740974,225.22082785508263 L287.666704987032,225.22082785508263 C274.28424119848603,207.01254426269597 252.7673911474541,195.1536782265297 228.4734076148015,195.00148252744913 L228.4730752675027,195.00148252744913 L228.4730752675027,163.99999999999997 z",
      artboardPaths.get(0)); 
    assertEquals(
      "M347.0,206.0 C370.1959594916,206.0 389.0,187.1959594916 389.0,164.0 C389.0,140.8040405084 370.1959594916,122.0 347.0,122.0 C323.8040405084,122.0 305.0,140.8040405084 305.0,164.0 C305.0,187.1959594916 323.8040405084,206.0 347.0,206.0 ",
      artboardPaths.get(1));
    assertEquals(
      "M263.0,106.0 C281.2253967434,106.0 296.0,91.22539674340001 296.0,73.0 C296.0,54.7746032566 281.2253967434,40.0 263.0,40.0 C244.7746032566,40.0 230.0,54.7746032566 230.0,73.0 C230.0,91.22539674340001 244.7746032566,106.0 263.0,106.0 ",
      artboardPaths.get(2));
    assertEquals(
      "M188.439999999999,106.0 C169.41931321688756,106.0 154.0,120.66715159655159 154.0,138.75999999999908 C154.0,141.96863986191275 154.48494567559146,145.06953972081692 155.38892208715313,147.9999999999987 L181.2799999999991,147.9999999999987 Q189.2799999999991,147.9999999999987 189.2799999999991,139.9999999999987 L189.2799999999991,106.00955561879559 L189.28009526648805,106.00955561879559 C189.00087392515613,106.00319726636273 188.7208294910957,106.0 188.439999999999,106.0 z",
      artboardPaths.get(3));
  }

  @Test
  public void combinationsMultipleArtboardsTest() {

    SketchPage sketchPage = SketchParser.parsePage(AndroidTestBase.getTestDataPath() + "/sketch/" + "paths_combinationsMultipleArtboards.json");

    List<SketchArtboard> artboards = sketchPage.getArtboards();

    List<DrawableShape> firstArtboardShapes = artboards.get(0).getShapes();

    assertEquals(
      "M71.0,180.0 C94.19595949160001,180.0 113.0,198.8040405084 113.0,222.0 C113.0,245.1959594916 94.19595949160001,264.0 71.0,264.0 C47.8040405084,264.0 29.0,245.1959594916 29.0,222.0 C29.0,198.8040405084 47.8040405084,180.0 71.0,180.0 zM292.0,225.0 C290.53687702269923,225.0 289.0912280038278,225.0748149097022 287.66666981740974,225.22082785508263 L287.666704987032,225.22082785508263 C296.67716365264556,237.48052750929673 302.0,252.61865623396764 302.0,269.0 C302.0,283.72161440201893 297.70112376503965,297.4391724612367 290.29021176429956,308.9658337084727 L290.29025900147064,308.9658337084727 C290.8573512194103,308.98853894809037 291.42733717365394,309.0 292.0,309.0 C315.1959594916,309.0 334.0,290.1959594916 334.0,267.0 C334.0,243.8040405084 315.1959594916,225.0 292.0,225.0 zM10.0,163.99999999999997 L10.0,382.47307526750274 L77.00260938615055,382.47307526750274 C77.00087131065143,382.315590047711 77.0,382.1578967766088 77.0,382.0 C77.0,358.8040405084 95.80404050839999,340.0 119.0,340.0 C142.1959594916,340.0 161.0,358.8040405084 161.0,382.0 C161.0,382.1578967766088 160.99912868934857,382.315590047711 160.99739061384943,382.47307526750274 L228.4730752675027,382.47307526750274 L228.4730752675027,342.9985195540348 L228.47307524465882,342.9985195540348 C254.42448203866866,342.83605474284224 277.2069316341925,329.31508870534464 290.29021176429956,308.9658337084727 L290.29025900147064,308.9658337084727 C267.8870316698388,308.06885323742677 250.0,289.6232966652539 250.0,267.0 C250.0,245.26716348570076 266.50666436957954,227.38966500695207 287.66666981740974,225.22082785508263 L287.666704987032,225.22082785508263 C274.28424119848603,207.01254426269597 252.7673911474541,195.1536782265297 228.4734076148015,195.00148252744913 L228.4730752675027,195.00148252744913 L228.4730752675027,163.99999999999997 z",
      firstArtboardShapes.get(0).getPathData());
    assertEquals(
      "M347.0,206.0 C370.1959594916,206.0 389.0,187.1959594916 389.0,164.0 C389.0,140.8040405084 370.1959594916,122.0 347.0,122.0 C323.8040405084,122.0 305.0,140.8040405084 305.0,164.0 C305.0,187.1959594916 323.8040405084,206.0 347.0,206.0 ",
      firstArtboardShapes.get(1).getPathData());
    assertEquals(
      "M263.0,106.0 C281.2253967434,106.0 296.0,91.22539674340001 296.0,73.0 C296.0,54.7746032566 281.2253967434,40.0 263.0,40.0 C244.7746032566,40.0 230.0,54.7746032566 230.0,73.0 C230.0,91.22539674340001 244.7746032566,106.0 263.0,106.0 ",
      firstArtboardShapes.get(2).getPathData());
    assertEquals(
      "M188.439999999999,106.0 C169.41931321688756,106.0 154.0,120.66715159655159 154.0,138.75999999999908 C154.0,141.96863986191275 154.48494567559146,145.06953972081692 155.38892208715313,147.9999999999987 L181.2799999999991,147.9999999999987 Q189.2799999999991,147.9999999999987 189.2799999999991,139.9999999999987 L189.2799999999991,106.00955561879559 L189.28009526648805,106.00955561879559 C189.00087392515613,106.00319726636273 188.7208294910957,106.0 188.439999999999,106.0 z",
      firstArtboardShapes.get(3).getPathData());

    List<DrawableShape> secondArtboardPaths = artboards.get(1).getShapes();

    assertEquals(
      "M71.0,180.0 C94.19595949160001,180.0 113.0,198.8040405084 113.0,222.0 C113.0,245.1959594916 94.19595949160001,264.0 71.0,264.0 C47.8040405084,264.0 29.0,245.1959594916 29.0,222.0 C29.0,198.8040405084 47.8040405084,180.0 71.0,180.0 zM292.0,225.0 C290.53687702269923,225.0 289.0912280038278,225.0748149097022 287.66666981740974,225.22082785508263 L287.666704987032,225.22082785508263 C296.67716365264556,237.48052750929673 302.0,252.61865623396764 302.0,269.0 C302.0,283.72161440201893 297.70112376503965,297.4391724612367 290.29021176429956,308.9658337084727 L290.29025900147064,308.9658337084727 C290.8573512194103,308.98853894809037 291.42733717365394,309.0 292.0,309.0 C315.1959594916,309.0 334.0,290.1959594916 334.0,267.0 C334.0,243.8040405084 315.1959594916,225.0 292.0,225.0 zM10.0,163.99999999999997 L10.0,382.47307526750274 L77.00260938615055,382.47307526750274 C77.00087131065143,382.315590047711 77.0,382.1578967766088 77.0,382.0 C77.0,358.8040405084 95.80404050839999,340.0 119.0,340.0 C142.1959594916,340.0 161.0,358.8040405084 161.0,382.0 C161.0,382.1578967766088 160.99912868934857,382.315590047711 160.99739061384943,382.47307526750274 L228.4730752675027,382.47307526750274 L228.4730752675027,342.9985195540348 L228.47307524465882,342.9985195540348 C254.42448203866866,342.83605474284224 277.2069316341925,329.31508870534464 290.29021176429956,308.9658337084727 L290.29025900147064,308.9658337084727 C267.8870316698388,308.06885323742677 250.0,289.6232966652539 250.0,267.0 C250.0,245.26716348570076 266.50666436957954,227.38966500695207 287.66666981740974,225.22082785508263 L287.666704987032,225.22082785508263 C274.28424119848603,207.01254426269597 252.7673911474541,195.1536782265297 228.4734076148015,195.00148252744913 L228.4730752675027,195.00148252744913 L228.4730752675027,163.99999999999997 z",
      secondArtboardPaths.get(0).getPathData());
    assertEquals(
      "M347.0,206.0 C370.1959594916,206.0 389.0,187.1959594916 389.0,164.0 C389.0,140.8040405084 370.1959594916,122.0 347.0,122.0 C323.8040405084,122.0 305.0,140.8040405084 305.0,164.0 C305.0,187.1959594916 323.8040405084,206.0 347.0,206.0 ",
      secondArtboardPaths.get(1).getPathData());
    assertEquals(
      "M263.0,106.0 C281.2253967434,106.0 296.0,91.22539674340001 296.0,73.0 C296.0,54.7746032566 281.2253967434,40.0 263.0,40.0 C244.7746032566,40.0 230.0,54.7746032566 230.0,73.0 C230.0,91.22539674340001 244.7746032566,106.0 263.0,106.0 ",
      secondArtboardPaths.get(2).getPathData());
    assertEquals(
      "M188.439999999999,106.0 C169.41931321688756,106.0 154.0,120.66715159655159 154.0,138.75999999999908 C154.0,141.96863986191275 154.48494567559146,145.06953972081692 155.38892208715313,147.9999999999987 L181.2799999999991,147.9999999999987 Q189.2799999999991,147.9999999999987 189.2799999999991,139.9999999999987 L189.2799999999991,106.00955561879559 L189.28009526648805,106.00955561879559 C189.00087392515613,106.00319726636273 188.7208294910957,106.0 188.439999999999,106.0 z",
      secondArtboardPaths.get(3).getPathData());
  }


  public List<String> getFirstArtboardPaths(SketchPage sketchPage) {
    List<SketchArtboard> artboards = sketchPage.getArtboards();
    if (!artboards.isEmpty()) {
      return artboards.get(0)
                      .getShapes()
                      .stream()
                      .map((shape) -> shape.getPathData())
                      .collect(Collectors.toList());
    }
    return ImmutableList.of();
  }
}
