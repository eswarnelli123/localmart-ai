package com.localmart.util;

public class MapsLinkParser {
    // Try multiple patterns used by Google Maps URLs to extract lat,lng
    public static double[] parseLatLng(String urlOrText) {
        if (urlOrText == null) return null;
        try {
            String s = urlOrText;
            // pattern: /@lat,lng,zoom
            int at = s.indexOf("/@");
            if (at >= 0) {
                String part = s.substring(at + 2);
                String[] pieces = part.split(",");
                if (pieces.length >= 2) {
                    double lat = Double.parseDouble(pieces[0]);
                    double lng = Double.parseDouble(pieces[1]);
                    return new double[]{lat, lng};
                }
            }
            // pattern: ?q=lat,lng
            int q = s.indexOf("?q=");
            if (q >= 0) {
                String part = s.substring(q + 3);
                String[] pieces = part.split("&");
                String coords = pieces[0];
                if (coords.contains(",")) {
                    String[] xy = coords.split(",");
                    double lat = Double.parseDouble(xy[0]);
                    double lng = Double.parseDouble(xy[1]);
                    return new double[]{lat, lng};
                }
            }
            // pattern: !3dlat!4dlong
            int i3d = s.indexOf("!3d");
            int i4d = s.indexOf("!4d");
            if (i3d >= 0 && i4d >= 0) {
                String latStr = s.substring(i3d + 3, i4d);
                int end = s.indexOf('!', i4d + 3);
                String lngStr = end > i4d ? s.substring(i4d + 3, end) : s.substring(i4d + 3);
                double lat = Double.parseDouble(latStr);
                double lng = Double.parseDouble(lngStr);
                return new double[]{lat, lng};
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
