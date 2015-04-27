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

# Results (i7-5920k)
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
  Avg Time Per Conversion: 0.00213 mSec. [0.04612] stdev.
  Avg Longitude Error: 2.2961e-08 degrees (abs) longitude  [1.5186e-07] stdev.
  Avg Latitude Error: 6.4809e-12 degrees (abs) longitude  [8.1105e-12] stdev.
Results for: GeographicLib
  Avg Time Per Conversion: 0.00143 mSec. [0.04469] stdev.
  Avg Longitude Error: 1.8565e-11 degrees (abs) longitude  [1.4621e-10] stdev.
  Avg Latitude Error: 7.6835e-15 degrees (abs) longitude  [8.5479e-15] stdev.
  
----------------------------------------------------------------------------------
Inverse Results
----------------------------------------------------------------------------------
Results for: Geotools
  Avg Time Per Conversion: 0.00386 mSec. [0.06198] stdev.
  Avg Azimuth 1 Error: 2.7065e-09 degrees (abs) heading [3.8673e-07] stdev.
  Avg Geodesic Distance Error: 1.1390e-06 meters (abs). [1.5695e-06] stdev.
Results for: GeographicLib
  Avg Time Per Conversion: 0.00260 mSec. [0.09223] stdev.
  Avg Azimuth 1 Error: 1.2366e-10 degrees (abs) heading [3.2249e-08] stdev.
  Avg Geodesic Distance Error: 1.1543e-09 meters (abs). [1.3611e-09] stdev.
  ```

# Thoughts
  * So the GeographicLib instance is never slower than geotools 
    * Difference is probably not relevant on speed - but the fact that it's not slower is relevant.
  * GeographicLib is always more precise
    * Again, talking mm accuracy vs. nm accuracy.  Likely not a huge issue.
  * GeographicLib handles areas around -180/+180 without throwing an error
    * This is, I think, reasonably significant.  Without this there's no choice but to guard/check all input values (and you just aren't able to calculate distances, etc. with those values.
    
# Problem locations
Looks like at first glances just an antipodal problem, which is a known issue for the vicenty method.
Locations where geotools can't perform the inverse calculation shown in red.  Origin in (lon, lat) order.
## Origin (0,0)
![Image of Error range](https://raw.githubusercontent.com/chrisbennight/geodesy/master/src/main/resources/geotools-inverse-error-0-0-origin.png)
## Origin (0,90)
![Image of Error range](https://raw.githubusercontent.com/chrisbennight/geodesy/master/src/main/resources/geotools-inverse-error-0-90-origin.png)
## Origin (0,-90)
![Image of Error range](https://raw.githubusercontent.com/chrisbennight/geodesy/master/src/main/resources/geotools-inverse-error-0-Minus90-origin.png)
##Origin (45,45)
![Image of Error range](https://raw.githubusercontent.com/chrisbennight/geodesy/master/src/main/resources/geotools-inverse-error-45-45-origin.png)
