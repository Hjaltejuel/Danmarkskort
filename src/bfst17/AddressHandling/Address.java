package bfst17.AddressHandling;
/**
 * Description: Addresse klassen som symboliserer en addresse i programmet
 */
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Address implements Serializable {
    private final String street, house, postcode, city;

    /**
     * Description: Den private Addresse konstruktor, der gør at du kun kan initialisere et addresse object gennem den indre builder klasse
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
     * Description: Retunerer en String sammensat af gaden og huset
     * @return: String
     */
    public String getStreetAndHouseNum() {
        return street + " " + house;
    }

    /**
     * Description: Retunerer en String sammensat af postkoden og byen
     * @return: String
     */
    public String getPostcodeAndCity() {
        return postcode + " " + city;
    }

    /**
     * Description: toString metoden der laver addressen om til en string ved at sammensætte alle felterne til en string
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
     * Description: Builder klassen som bruges til at initialisere et addresse objekt
     */
    public static class Builder {
        private String street, house, postcode, city;

        /**
         * Description: sætter street feltet til det givne gadenavn givet i parameteren
         * @param _street
         */
        public Builder street(String _street) {
            street = _street;
            return this;
        }

        /**
         * Description: house street feltet til det givne husnummer givet i parameteren
         * @param _house
         */
        public Builder house(String _house) {
            house = _house;
            return this;
        }

        /**
         * Description: sætter postkode feltet til den givne postkode givet i parameteren
         * @param _postcode
         */
        public Builder postcode(String _postcode) {
            postcode = _postcode;
            return this;
        }

        /**
         * Description: sætter by feltet til det givne bynavn givet i parameteren
         * @param _city
         */
        public Builder city(String _city) {
            city = _city;
            return this;
        }

        /**
         * Description: bygger den givne addresse ved at kalde dens konsturktor
         * @return: Addresse
         */
        public Address build() {
            return new Address(street, house, postcode, city);
        }
    }
}

