# geodesy
Comparing geodetic calulcations form a couple of different libaries - currently:
  * Geotools: http://geotools.org/
  * GeographicLib: http://geographiclib.sourceforge.net/
 
To run the tests (requires Java8):
```
mvn compile
mvn exec:exec
```
the results are printed to the console.  The image graphic will be in /target/$provider_name.png.

# Results (from travis run)
```
----------------------------------------------------------------------------------
Testing extents (this may take awhile)
----------------------------------------------------------------------------------
Results for: Geotools
  4338 exceptions occurred measuring distance from (0,0) to a 0.1 degree grid across the WGS:84 ellipsoid
Results for: GeographicLib
  0 exceptions occurred measuring distance from (0,0) to a 0.1 degree grid across the WGS:84 ellipsoid

----------------------------------------------------------------------------------
Direct Results
----------------------------------------------------------------------------------
Results for: Geotools
  Avg Time Per Conversion: 0.10827356839998109 mSec.
  Avg Longitude Error: 2.2961389963944247E-8 degrees (abs) longitude.
  Avg Latitude Error: 6.4808786613413035E-12 degrees (abs) lattitude.
Results for: GeographicLib
  Avg Time Per Conversion: 0.087290954084769 mSec.
  Avg Longitude Error: 1.8565406729781347E-11 degrees (abs) longitude.
  Avg Latitude Error: 7.683463537107237E-15 degrees (abs) lattitude.
----------------------------------------------------------------------------------
Inverse Results
----------------------------------------------------------------------------------
Results for: Geotools
  Avg Time Per Conversion: 0.11805557744747254 mSec.
  Avg Azimuth 1 Error: 2.70650828910131E-9 degrees (abs) heading.
  Avg Geodesic Distance Error: 1.139046250480835E-6 meters (abs).
Results for: GeographicLib
  Avg Time Per Conversion: 0.10033888687483253 mSec.
  Avg Azimuth 1 Error: 1.2366053228915025E-10 degrees (abs) heading.
  Avg Geodesic Distance Error: 1.1542975682065518E-9 meters (abs).
  ```

# Thoughts
  * So the GeographicLib instance is never slower than geotools 
    * Difference is probably not relevant on speed - but the fact that it's not slower is relevant.
  * GeographicLib is always more precise
    * Again, talking mm accuracy vs. nm accuracy.  Likely not a huge issue.
  * GeographicLib handles areas around -180/+180 without throwing an error
    * This is, I think, reasonably significant.  Without this there's no choice but to guard/check all input values (and you just aren't able to calculate distances, etc. with those values.
    
# Problem locations
Locations where geotools can't perform the inverse calculation with a starting point of (0,0)
(Locations shown in red)
![Image of Error range](https://raw.githubusercontent.com/chrisbennight/geodesy/master/src/main/resources/geotools-inverse-error.png)
