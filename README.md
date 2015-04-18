# geodesy
Comparing geodetic calulcations form a couple of different libaries - currently:
  * Geotools: http://geotools.org/
  * GeographicLib: http://geographiclib.sourceforge.net/

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

