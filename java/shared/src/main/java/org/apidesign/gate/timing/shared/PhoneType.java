package org.apidesign.gate.timing.shared;

import java.util.List;
import net.java.html.json.ComputedProperty;
import net.java.html.json.Model;
import net.java.html.json.Property;

public enum PhoneType {
    HOME, WORK, MOBILE;
}

@Model(className = "Event", properties = {
    @Property(name = "id", type=int.class),
    @Property(name = "when", type=long.class),
    @Property(name = "type", type=String.class),
})
final class Events {
    static String twoDigits(long value) {
        if (value < 10) {
            return "0" + value;
        }
        return "" + value;
    }

    @ComputedProperty
    static String shortWhen(long when) {
        long inSec = when / 1000L;
        long hourMinSec = inSec % 3600L;
        long min = hourMinSec / 60L;
        long sec = hourMinSec % 60L;
        return twoDigits(min) + "'" + twoDigits(sec) + "''";
    }
}

@Model(className = "Contact", properties = {
    @Property(name = "id", type = String.class),
    @Property(name = "firstName", type = String.class),
    @Property(name = "lastName", type = String.class),
    @Property(name = "address", type = Address.class),
    @Property(name = "phones", type = Phone.class, array=true)
})
final class Contacts {
    @ComputedProperty static String fullName(
        String firstName, String lastName
    ) {
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }
    
    @ComputedProperty static String callInfo(
        List<Phone> phones
    ) {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (Phone p : phones) {
            sb.append(sep).append(p.getNumber());
            sep = ", ";
        }
        return sb.toString();
    }
    
    @ComputedProperty static String validate(
        String firstName, String lastName, Address address, List<Phone> phones
    ) {
        String res = null;
        if (firstName == null || firstName.isEmpty()) {
            res = "Specify first name";
        }
        if (res == null && (lastName == null || lastName.isEmpty())) {
            res = "Specify last name";
        }
        if (res == null && address != null) {
            res = address.getValidate();
        }
        if (phones != null) for (Phone phone : phones) {
            if (res == null) {
                res = phone.getValidate();
            }
        }
        return res;
    }
    
    @Model(className = "Address", properties = {
        @Property(name = "street", type = String.class),
        @Property(name = "town", type = String.class)
    })
    static class AddressImpl {
        @ComputedProperty static String validate(
            String town, String street
        ) {
            if (street == null || street.isEmpty()) {
                return "Specify street";
            }
            if (town == null || town.isEmpty()) {
                return "Specify town";
            }
            return null;
        }
    }
    @Model(className = "Phone", properties = {
        @Property(name = "number", type = String.class),
        @Property(name = "type", type = PhoneType.class)
    })
    static class PhoneImpl {
        @ComputedProperty static String validate(String number) {
            if (number == null) {
                return "Phone not specified!";
            }
            if (!number.startsWith("+")) {
                return "Get ready for internation calls!";
            }
            for (int i = 1; i < number.length(); i++) {
                if (Character.isDigit(number.charAt(i))) {
                    continue;
                }
                if (number.charAt(i) == ' ' && i > 1) {
                    continue;
                }
                return "Only numbers and spaces allowed after +";
            }
            return null;
        }
    }
}
