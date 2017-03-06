package bfst17;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

    public class Address {
        private final String street;
        private final String house;
        private final String floor;
        private final String side;
        private final String postcode;
        private final String city;
        private static ArrayList<Pattern> patterns = new ArrayList();
        private static ArrayList<String> regList = new ArrayList();

        private Address(String _street, String _house, String _floor, String _side, String _postcode, String _city) {
            this.street = _street;
            this.house = _house;
            this.floor = _floor;
            this.side = _side;
            this.postcode = _postcode;
            this.city = _city;
            regList.clear();
            regList.add("(?<house>[0-9]+) (?<street>[A-Za-zæøåÆØÅé. ]+), (?<city>[A-Za-zæøåÆØÅ ]+) (?<postcode>[0-9]{4})");
            regList.add("(?<street>[A-Za-zæøåÆØÅé. ]+) (?<house>[0-9]+), (?<postcode>[0-9]{4}) (?<city>[A-Za-zæøåÆØÅ ]+)");
            regList.add("(?<street>[A-Za-zæøåÆØÅé ]+) (?<house>[0-9]+), (?<city>[A-Za-zæøåÆØÅ ]+) (?<postcode>[0-9]{4})");
            regList.add("(?<house>[0-9]+) (?<street>[A-Za-zæøåÆØÅé. ]+), (?<postcode>[0-9]{4}) (?<city>[A-Za-zæøåÆØÅ ]+)");
            regList.add("(?<postcode>[0-9]{4}) (?<city>[A-Za-zæøåÆØÅ ]+), (?<street>[A-Za-zæøåÆØÅé. ]+) (?<house>[0-9]+)");
            regList.add("(?<postcode>[0-9]{4}) (?<city>[A-Za-zæøåÆØÅ ]+), (?<house>[0-9]+) (?<street>[A-Za-zæøåÆØÅé. ]+)");
            regList.add("(?<city>[A-Za-zæøåÆØÅ ]+) (?<postcode>[0-9]{4}), (?<street>[A-Za-zæøåÆØÅé. ]+) (?<house>[0-9]+)");
            regList.add("(?<city>[A-Za-zæøåÆØÅ ]+) (?<postcode>[0-9]{4}), (?<house>[0-9]+) (?<street>[A-Za-zæøåÆØÅé. ]+) ");
            regList.add("(?<street>[A-Za-zæøåÆØÅé. ]+), (?<postcode>[0-9]{4}) (?<city>[A-Za-zæøåÆØÅ ]+)");
            regList.add("(?<street>[A-Za-zæøåÆØÅé. ]+), (?<city>[A-Za-zæøåÆØÅ ]+) (?<postcode>[0-9]{4})");
            regList.add("(?<city>[A-Za-zæøåÆØÅ ]+) (?<postcode>[0-9]{4}), (?<street>[A-Za-zæøåÆØÅé. ]+)");
            regList.add("(?<postcode>[0-9]{4}) (?<city>[A-Za-zæøåÆØÅ ]+), (?<street>[A-Za-zæøåÆØÅé ]+)");
            regList.add("(?<street>[A-Za-zæøåÆØÅé. ]+) (?<house>[0-9]+), (?<city>[A-Za-zæøåÆØÅ ]+)");
            regList.add("(?<house>[0-9]+) (?<street>[A-Za-zæøåÆØÅé. ]+), (?<city>[A-Za-zæøåÆØÅ ]+)");
            regList.add("(?<city>[A-Za-zæøåÆØÅ ]+), (?<house>[0-9]+) (?<street>[A-Za-zæøåÆØÅé. ]+)");
            regList.add("(?<city>[A-Za-zæøåÆØÅ ]+), (?<street>[A-Za-zæøåÆØÅé. ]+) (?<house>[0-9]+)");
            regList.add("(?<street>[A-Za-zæøåÆØÅé. ]+) (?<house>[0-9]+), (?<postcode>[0-9]{4})");
            regList.add("(?<house>[0-9]+) (?<street>[A-Za-zæøåÆØÅé. ]+), (?<postcode>[0-9]{4})");
            regList.add("(?<postcode>[0-9]{4}), (?<house>[0-9]+) (?<street>[A-Za-zæøåÆØÅé. ]+)");
            regList.add("(?<postcode>[0-9]{4}), (?<street>[A-Za-zæøåÆØÅé. ]+) (?<house>[0-9]+)");
            regList.add("(?<street>[A-Za-zæøåÆØÅé. ]+) (?<house>[0-9]+)");
            regList.add("(?<house>[0-9]+) (?<street>[A-Za-zæøåÆØÅé. ]+)");
            regList.add("(?<street>[A-Za-zæøåÆØÅé. ]+), (?<city>[A-Za-zæøåÆØÅ ]+)");
            regList.add("(?<city>[A-Za-zæøåÆØÅ ]+), (?<street>[A-Za-zæøåÆØÅé. ]+)");
            regList.add("(?<street>[A-Za-zæøåÆØÅé. ]+), (?<postcode>[0-9]{4})");
            regList.add("(?<postcode>[0-9]{4}), (?<street>[A-Za-zæøåÆØÅé. ]+)");
            regList.add("(?<postcode>[0-9]{4}) (?<city>[A-Za-zæøåÆØÅ ]+)");
            regList.add("(?<city>[A-Za-zæøåÆØÅ ]+) (?<postcode>[0-9]{4})");
            regList.add("(?<street>[A-Za-zæøåÆØÅé. ]+)");
            regList.add("(?<city>[A-Za-zæøåÆØÅ ]+)");
            regList.add("(?<postcode>[0-9]{4})");
            Iterator var7 = regList.iterator();

            while(var7.hasNext()) {
                String regex = (String)var7.next();
                patterns.add(Pattern.compile(regex));
            }

        }

        public String toString() {
            return this.street + " " + this.house + "\n" + this.postcode + " " + this.city;
        }

        public String street() {
            return this.street;
        }

        public String house() {
            return this.house;
        }

        public String floor() {
            return this.floor;
        }

        public String side() {
            return this.side;
        }

        public String postcode() {
            return this.postcode;
        }

        public String city() {
            return this.city;
        }

        public static Address parse(String s) {
            Address.Builder b = new Address.Builder();
            String[] patternFromStrings = new String[]{"street", "house", "postcode", "city"};
            String[] adressString = new String[4];
            String groupString = "";
            Iterator var5 = patterns.iterator();

            Matcher matcher;
            do {
                if(!var5.hasNext()) {
                    return b.build();
                }

                Pattern p = (Pattern)var5.next();
                matcher = p.matcher(s);
            } while(!matcher.matches());

            for(int i = 0; i < patternFromStrings.length; ++i) {
                try {
                    groupString = matcher.group(patternFromStrings[i]);
                } catch (IllegalArgumentException var10) {
                    groupString = null;
                }

                patternFromStrings[i] = groupString;
            }

            b.street(patternFromStrings[0]).house(patternFromStrings[1]).postcode(patternFromStrings[2]).city(patternFromStrings[3]);
            return b.build();
        }

        public static class Builder {
            private String street = "Unknown";
            private String house;
            private String floor;
            private String side;
            private String postcode;
            private String city;

            public Builder() {
            }

            public Address.Builder street(String _street) {
                this.street = _street;
                return this;
            }

            public Address.Builder house(String _house) {
                this.house = _house;
                return this;
            }

            public Address.Builder floor(String _floor) {
                this.floor = _floor;
                return this;
            }

            public Address.Builder side(String _side) {
                this.side = _side;
                return this;
            }

            public Address.Builder postcode(String _postcode) {
                this.postcode = _postcode;
                return this;
            }

            public Address.Builder city(String _city) {
                this.city = _city;
                return this;
            }

            public Address build() {
                return new Address(this.street, this.house, this.floor, this.side, this.postcode, this.city);
            }
        }
    }


