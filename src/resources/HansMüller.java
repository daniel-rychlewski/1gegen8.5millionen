package resources;

import java.util.ListResourceBundle;

public class HansMüller extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"natelnummer", "0795001234"},
                {"vorname", "Hans"},
                {"nachname", "Müller"},
                {"strasse", "Z%FCrichstrasse+2"},
                {"plz", "3000"},
                {"stadt", "Bern"},
        };
    }
}