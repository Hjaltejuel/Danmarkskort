package bfst17;

import javafx.geometry.Side;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.regex.*;

public class Address {
    private final String street, house, floor, side, postcode, city;
    private static HashMap<Integer, String> postCodeHashMap;

    private Address(String _street, String _house, String _floor, String _side, String _postcode, String _city) {
        street = _street;
        house = _house;
        floor = _floor;
        side = _side;
        postcode = _postcode;
        if (_city == null && _postcode != null) {
            city = getCityFromPostCode(_postcode);
        } else {
            city = _city;
        }


    }

    public String toString() {
        String floorSideString = "";
        String streetString = "";
        String houseString = "";
        String postcodeString = "";
        String cityString = "";

        if (floor != null) {
            floorSideString += " " + floor + ".";
        }
        if (side != null) {
            floorSideString += " " + side;
        }
        if (street != null) {
            streetString = street;
        }
        if(house != null) {
            houseString = house;
        }
        if(postcode != null) {
            postcodeString = postcode;
        }
        if(city != null){
            cityString = city;
        }
        return (streetString + " " + houseString + floorSideString + " " +
                postcodeString + " " + cityString).trim();
    }

    public static class Builder {
        private String street, house, floor, side, postcode, city;

        public Builder street(String _street) {
            street = _street;
            return this;
        }

        public Builder house(String _house) {
            house = _house;
            return this;
        }

        public Builder floor(String _floor) {
            floor = _floor;
            return this;
        }

        public Builder side(String _side) {
            side = _side;
            return this;
        }

        public Builder postcode(String _postcode) {
            postcode = _postcode;
            return this;
        }

        public Builder city(String _city) {
            city = _city;
            return this;
        }

        public Address build() {
            return new Address(street, house, floor, side, postcode, city);
        }
    }

    private final static String postNumreRegex = "(?<postnr>[0-9]{4}) (?<city>.*)";
    private final static Pattern postNumrePattern = Pattern.compile(postNumreRegex);

    private String getCityFromPostCode(String postCode) {
        //Init postcode HashMap
        if (postCodeHashMap == null) {
            postCodeHashMap = new HashMap<>();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/postnumre.txt"), "UTF-8"));
                String lineRead = reader.readLine();
                Matcher match;
                while (lineRead != null) {
                    match = postNumrePattern.matcher(lineRead);
                    if (match.matches()) {
                        postCodeHashMap.put(Integer.parseInt(match.group("postnr")), match.group("city"));
                    }
                    lineRead = reader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            int _postCode = Integer.parseInt(postCode);
            return postCodeHashMap.get(_postCode);
        } catch (Exception e) {
            return null;
        }


    }

    public String street() {
        return street;
    }

    public String house() {
        return house;
    }

    public String floor() {
        return floor;
    }

    public String side() {
        return side;
    }

    public String postcode() {
        return postcode;
    }

    public String city() {
        return city;
    }
    //regList.clear();
    //regList.add(;

    private static ArrayList<Pattern> patterns = new ArrayList();
    private static Pattern[] regList = new Pattern[]{

            Pattern.compile("(?<street>^[\\p{L} ]+) +(?<house>[0-9]+[\\p{L}]?) +(?<floor>[0-9]+) *. *(?<side>[th|mf|tv]+) *, +(?<postcode>[0-9]{4}) +(?<city>[\\p{L} ])"),
            Pattern.compile("(?<street>^[\\p{L} ]+) +(?<house>[0-9]+[\\p{L}]?) +(?<floor>[0-9]+) *. *(?<side>[th|mf|tv]+) *, +(?<postcode>[0-9]{4})"),
            Pattern.compile("(?<street>^[\\p{L} ]+) +(?<house>[0-9]+[\\p{L}]?) +(?<floor>[0-9]+) *, +(?<postcode>[0-9]{4}) +(?<city>[\\p{L} ]+)"),
            Pattern.compile("(?<street>^[\\p{L} ]+) +(?<house>[0-9]+[\\p{L}]?) +(?<floor>[0-9]+) *, +(?<postcode>[0-9]{4})"),
            Pattern.compile("(?<street>^[\\p{L} ]+) +(?<house>[0-9]+[\\p{L}]?) *, +(?<postcode>[0-9]{4}) +(?<city>[\\p{L} ]+)"),
            Pattern.compile("(?<street>^[\\p{L} ]+) +(?<house>[0-9]+[\\p{L}]?) *, +(?<postcode>[0-9]{4})"),
            Pattern.compile("(?<street>^[\\p{L} ]+) +(?<house>[0-9]+[\\p{L}]?) *, +(?<city>[\\p{L} ]+)"),
            Pattern.compile("(?<street>^[\\p{L} ]+) +(?<house>[0-9]+[\\p{L}]?)"),
            Pattern.compile("(?<city>[\\p{L} ]+)"),

    };

    public static Address parse(String s) {
        Builder b = new Builder();
        Matcher matcher;
        for (Pattern pattern : regList) {
            matcher = pattern.matcher(s);
            if(matcher.matches()) {
                if (pattern.toString().toLowerCase().contains("street")) {
                    b.street(matcher.group("street"));
                }
                if (pattern.toString().toLowerCase().contains("house")) {
                    b.house(matcher.group("house"));
                }
                if (pattern.toString().toLowerCase().contains("floor")) {
                    b.floor(matcher.group("floor"));
                }
                if (pattern.toString().toLowerCase().contains("side")) {
                    b.side(matcher.group("side"));
                }
                if (pattern.toString().toLowerCase().contains("postcode")) {
                    b.postcode(matcher.group("postcode"));
                }
                if (pattern.toString().toLowerCase().contains("city")) {
                    b.city(matcher.group("city"));
                }
            }

        }

        return b.build();
    }
}

