package bfst17.AddressHandling;
/**
 * Beskrivelse: Addresse klassen som symboliserer en addresse i programmet
 */
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Address implements Serializable {
    private final String street, house, postcode, city;

    /**
     * Beskrivelse: Den private Addresse konstruktor, der gør at du kun kan initialisere et addresse object gennem den indre builder klasse
     * @param _street
     * @param _house
     * @param _postcode
     * @param _city
     */
    private Address(String _street, String _house, String _postcode, String _city) {
        street = _street;
        house = _house;
        postcode = _postcode;
        city = _city;
    }

    /**
     * Beskrivelse: Retunerer en String sammensat af gaden og huset
     * @return: String
     */
    public String getStreetAndHouseNum() {
        return street + " " + house;
    }

    /**
     * Beskrivelse: Retunerer en String sammensat af postkoden og byen
     * @return: String
     */
    public String getPostcodeAndCity() {
        return postcode + " " + city;
    }

    /**
     * Beskrivelse: toString metoden der laver addressen om til en string ved at sammensætte alle felterne til en string
     * @return: STring
     */
    public String toString() {
        String streetString = "";
        String houseString = "";
        String postcodeString = "";
        String cityString = "";
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
        return (streetString + " " + houseString +  " " +
                postcodeString + " " + cityString).trim();
    }

    /**
     * Beskrivelse: Builder klassen som bruges til at initialisere et addresse objekt
     */
    public static class Builder {
        private String street, house, postcode, city;

        /**
         * Beskrivelse: sætter street feltet til det givne gadenavn givet i parameteren
         * @param _street
         */
        public Builder street(String _street) {
            street = _street;
            return this;
        }

        /**
         * Beskrivelse: house street feltet til det givne husnummer givet i parameteren
         * @param _house
         */
        public Builder house(String _house) {
            house = _house;
            return this;
        }

        /**
         * Beskrivelse: sætter postkode feltet til den givne postkode givet i parameteren
         * @param _postcode
         */
        public Builder postcode(String _postcode) {
            postcode = _postcode;
            return this;
        }

        /**
         * Beskrivelse: sætter by feltet til det givne bynavn givet i parameteren
         * @param _city
         */
        public Builder city(String _city) {
            city = _city;
            return this;
        }

        /**
         * Beskrivelse: bygger den givne addresse ved at kalde dens konsturktor
         * @return: Addresse
         */
        public Address build() {
            return new Address(street, house, postcode, city);
        }
    }
}

