package com.blogosphere.blog.dao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SomeTest {
	  public static void main(String[] args) {
		    Set<String> allZones = ZoneId.getAvailableZoneIds();
		    List<String> zoneList = new ArrayList<String>(allZones);
		    Collections.sort(zoneList);

		    LocalDateTime dt = LocalDateTime.now();
		    for (String s : zoneList) {
		      ZoneId zone = ZoneId.of(s);
		      ZonedDateTime zdt = dt.atZone(zone);
		      ZoneOffset offset = zdt.getOffset();
		      String out = String.format("%35s %10s%n", zone, offset);
		      System.out.println(out);
		    }
		    
		    System.out.println(ZonedDateTime.now(ZoneId.of("America/Los_Angeles")));
		  }
}
