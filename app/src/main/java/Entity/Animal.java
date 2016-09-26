package Entity;

/**
 * Created by liangchenzhou on 19/07/16.
 */
public class Animal {
    private String animalId;
    private String catalogNumber;
    private String taxonConceptGuid;
    private String scientificName;
    private String taxonRank;
    private String vernacularName;
    private String climateWatch;
    private String kingdom;
    private String phylum;
    private String classMatched;
    private String order;
    private String family;
    private String genus;
    private String species;
    private String institutionCode;
    private String collectionCode;
    private String locality;
    private float latitudeOriginal;
    private float longitudeOriginal;
    private String geodeticDatum;
    private float latitudeProcessed;
    private float longitudeProcessed;
    private String coordinateUncertaintyinMeters;
    private String country;
    private String state;
    private String localGovernmentAreas;
    private String collector;
    private String year;
    private String month;
    private String eventDate;
    private String basisOfRecordOriginal;
    private String basisOfRecordProcessed;
    private String sex;
    private String outlierForLayer;
    private String taxonIdentificationIssue;
    private String dataResourceId;
    private String dataResourceName;
    private String altitudeValue;
    private String occurrenceStatus;
    private String coordinatePrecision;
    private String countryInferredFromCoordinates;
    private String coordinatesConvertedtoWgs;
    private String suspectedoutlier;
    private String firstOftheCentury;
    private String firstOftheMonth;
    private String firstOftheYear;
    private String geodeticDatumAssumedWgs;
    private String homonymIssues;
    private String imcompleteCollectionDate;
    private String possibleDuplicateRecord;
    private String invalidCollectionDate;
    private String nameNotInNationalChecklists;
    private String nameNotRecognised;
    private String cultivatedEscapee;
    private String coordinatesDontMatchSuppliedState;
    private String coordinateUncertaintyNotValid;
    private String unRecognizedGeodeticDatum;

    public Animal(){}

    public Animal(String animalId, String catalogNumber, String taxonConceptGuid, String scientificName, String vernacularName,
                  String climateWatch, String kingdom, String classMatched, String order, String family, String genus, String species,
                  float latitudeProcessed, float longitudeProcessed, String year, String month, String eventDate) {
        this.animalId = animalId;
        this.catalogNumber = catalogNumber;
        this.taxonConceptGuid = taxonConceptGuid;
        this.scientificName = scientificName;
        this.vernacularName = vernacularName;
        this.climateWatch = climateWatch;
        this.kingdom = kingdom;
        this.classMatched = classMatched;
        this.order = order;
        this.family = family;
        this.genus = genus;
        this.species = species;
        this.latitudeProcessed = latitudeProcessed;
        this.longitudeProcessed = longitudeProcessed;
        this.year = year;
        this.month = month;
        this.eventDate = eventDate;
    }

    public Animal(String animalId, String catalogNumber, String taxonConceptGuid, String scientificName, String taxonRank,
                  String vernacularName, String climateWatch, String kingdom, String phylum, String classMatched, String order,
                  String family, String genus, String species, String institutionCode, String collectionCode, String locality,
                  float latitudeOriginal, float longitudeOriginal, String geodeticDatum, float latitudeProcessed, float longitudeProcessed,
                  String coordinateUncertaintyinMeters, String country, String state, String localGovernmentAreas, String collector,
                  String year, String month, String eventDate, String basisOfRecordOriginal, String basisOfRecordProcessed, String sex,
                  String outlierForLayer, String taxonIdentificationIssue, String dataResourceId, String dataResourceName, String altitudeValue,
                  String occurrenceStatus, String coordinatePrecision, String countryInferredFromCoordinates, String coordinatesConvertedtoWgs,
                  String suspectedoutlier, String firstOftheCentury, String firstOftheMonth, String firstOftheYear, String geodeticDatumAssumedWgs,
                  String homonymIssues, String imcompleteCollectionDate, String possibleDuplicateRecord, String invalidCollectionDate,
                  String nameNotInNationalChecklists, String nameNotRecognised, String cultivatedEscapee, String coordinatesDontMatchSuppliedState,
                  String coordinateUncertaintyNotValid, String unRecognizedGeodeticDatum) {
        this.animalId = animalId;
        this.catalogNumber = catalogNumber;
        this.taxonConceptGuid = taxonConceptGuid;
        this.scientificName = scientificName;
        this.taxonRank = taxonRank;
        this.vernacularName = vernacularName;
        this.climateWatch = climateWatch;
        this.kingdom = kingdom;
        this.phylum = phylum;
        this.classMatched = classMatched;
        this.order = order;
        this.family = family;
        this.genus = genus;
        this.species = species;
        this.institutionCode = institutionCode;
        this.collectionCode = collectionCode;
        this.locality = locality;
        this.latitudeOriginal = latitudeOriginal;
        this.longitudeOriginal = longitudeOriginal;
        this.geodeticDatum = geodeticDatum;
        this.latitudeProcessed = latitudeProcessed;
        this.longitudeProcessed = longitudeProcessed;
        this.coordinateUncertaintyinMeters = coordinateUncertaintyinMeters;
        this.country = country;
        this.state = state;
        this.localGovernmentAreas = localGovernmentAreas;
        this.collector = collector;
        this.year = year;
        this.month = month;
        this.eventDate = eventDate;
        this.basisOfRecordOriginal = basisOfRecordOriginal;
        this.basisOfRecordProcessed = basisOfRecordProcessed;
        this.sex = sex;
        this.outlierForLayer = outlierForLayer;
        this.taxonIdentificationIssue = taxonIdentificationIssue;
        this.dataResourceId = dataResourceId;
        this.dataResourceName = dataResourceName;
        this.altitudeValue = altitudeValue;
        this.occurrenceStatus = occurrenceStatus;
        this.coordinatePrecision = coordinatePrecision;
        this.countryInferredFromCoordinates = countryInferredFromCoordinates;
        this.coordinatesConvertedtoWgs = coordinatesConvertedtoWgs;
        this.suspectedoutlier = suspectedoutlier;
        this.firstOftheCentury = firstOftheCentury;
        this.firstOftheMonth = firstOftheMonth;
        this.firstOftheYear = firstOftheYear;
        this.geodeticDatumAssumedWgs = geodeticDatumAssumedWgs;
        this.homonymIssues = homonymIssues;
        this.imcompleteCollectionDate = imcompleteCollectionDate;
        this.possibleDuplicateRecord = possibleDuplicateRecord;
        this.invalidCollectionDate = invalidCollectionDate;
        this.nameNotInNationalChecklists = nameNotInNationalChecklists;
        this.nameNotRecognised = nameNotRecognised;
        this.cultivatedEscapee = cultivatedEscapee;
        this.coordinatesDontMatchSuppliedState = coordinatesDontMatchSuppliedState;
        this.coordinateUncertaintyNotValid = coordinateUncertaintyNotValid;
        this.unRecognizedGeodeticDatum = unRecognizedGeodeticDatum;
    }

    public String getAnimalId() {
        return animalId;
    }

    public void setAnimalId(String animalId) {
        this.animalId = animalId;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        this.catalogNumber = catalogNumber;
    }

    public String getTaxonConceptGuid() {
        return taxonConceptGuid;
    }

    public void setTaxonConceptGuid(String taxonConceptGuid) {
        this.taxonConceptGuid = taxonConceptGuid;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getTaxonRank() {
        return taxonRank;
    }

    public void setTaxonRank(String taxonRank) {
        this.taxonRank = taxonRank;
    }

    public String getVernacularName() {
        return vernacularName;
    }

    public void setVernacularName(String vernacularName) {
        this.vernacularName = vernacularName;
    }

    public String getClimateWatch() {
        return climateWatch;
    }

    public void setClimateWatch(String climateWatch) {
        this.climateWatch = climateWatch;
    }

    public String getKingdom() {
        return kingdom;
    }

    public void setKingdom(String kingdom) {
        this.kingdom = kingdom;
    }

    public String getPhylum() {
        return phylum;
    }

    public void setPhylum(String phylum) {
        this.phylum = phylum;
    }

    public String getClassMatched() {
        return classMatched;
    }

    public void setClassMatched(String classMatched) {
        this.classMatched = classMatched;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getCollectionCode() {
        return collectionCode;
    }

    public void setCollectionCode(String collectionCode) {
        this.collectionCode = collectionCode;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public float getLatitudeOriginal() {
        return latitudeOriginal;
    }

    public void setLatitudeOriginal(float latitudeOriginal) {
        this.latitudeOriginal = latitudeOriginal;
    }

    public float getLongitudeOriginal() {
        return longitudeOriginal;
    }

    public void setLongitudeOriginal(float longitudeOriginal) {
        this.longitudeOriginal = longitudeOriginal;
    }

    public String getGeodeticDatum() {
        return geodeticDatum;
    }

    public void setGeodeticDatum(String geodeticDatum) {
        this.geodeticDatum = geodeticDatum;
    }

    public float getLatitudeProcessed() {
        return latitudeProcessed;
    }

    public void setLatitudeProcessed(float latitudeProcessed) {
        this.latitudeProcessed = latitudeProcessed;
    }

    public float getLongitudeProcessed() {
        return longitudeProcessed;
    }

    public void setLongitudeProcessed(float longitudeProcessed) {
        this.longitudeProcessed = longitudeProcessed;
    }

    public String getCoordinateUncertaintyinMeters() {
        return coordinateUncertaintyinMeters;
    }

    public void setCoordinateUncertaintyinMeters(String coordinateUncertaintyinMeters) {
        this.coordinateUncertaintyinMeters = coordinateUncertaintyinMeters;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocalGovernmentAreas() {
        return localGovernmentAreas;
    }

    public void setLocalGovernmentAreas(String localGovernmentAreas) {
        this.localGovernmentAreas = localGovernmentAreas;
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = collector;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getBasisOfRecordOriginal() {
        return basisOfRecordOriginal;
    }

    public void setBasisOfRecordOriginal(String basisOfRecordOriginal) {
        this.basisOfRecordOriginal = basisOfRecordOriginal;
    }

    public String getBasisOfRecordProcessed() {
        return basisOfRecordProcessed;
    }

    public void setBasisOfRecordProcessed(String basisOfRecordProcessed) {
        this.basisOfRecordProcessed = basisOfRecordProcessed;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getOutlierForLayer() {
        return outlierForLayer;
    }

    public void setOutlierForLayer(String outlierForLayer) {
        this.outlierForLayer = outlierForLayer;
    }

    public String getTaxonIdentificationIssue() {
        return taxonIdentificationIssue;
    }

    public void setTaxonIdentificationIssue(String taxonIdentificationIssue) {
        this.taxonIdentificationIssue = taxonIdentificationIssue;
    }

    public String getDataResourceId() {
        return dataResourceId;
    }

    public void setDataResourceId(String dataResourceId) {
        this.dataResourceId = dataResourceId;
    }

    public String getDataResourceName() {
        return dataResourceName;
    }

    public void setDataResourceName(String dataResourceName) {
        this.dataResourceName = dataResourceName;
    }

    public String getAltitudeValue() {
        return altitudeValue;
    }

    public void setAltitudeValue(String altitudeValue) {
        this.altitudeValue = altitudeValue;
    }

    public String getOccurrenceStatus() {
        return occurrenceStatus;
    }

    public void setOccurrenceStatus(String occurrenceStatus) {
        this.occurrenceStatus = occurrenceStatus;
    }

    public String getCoordinatePrecision() {
        return coordinatePrecision;
    }

    public void setCoordinatePrecision(String coordinatePrecision) {
        this.coordinatePrecision = coordinatePrecision;
    }

    public String getCountryInferredFromCoordinates() {
        return countryInferredFromCoordinates;
    }

    public void setCountryInferredFromCoordinates(String countryInferredFromCoordinates) {
        this.countryInferredFromCoordinates = countryInferredFromCoordinates;
    }

    public String getCoordinatesConvertedtoWgs() {
        return coordinatesConvertedtoWgs;
    }

    public void setCoordinatesConvertedtoWgs(String coordinatesConvertedtoWgs) {
        this.coordinatesConvertedtoWgs = coordinatesConvertedtoWgs;
    }

    public String getSuspectedoutlier() {
        return suspectedoutlier;
    }

    public void setSuspectedoutlier(String suspectedoutlier) {
        this.suspectedoutlier = suspectedoutlier;
    }

    public String getFirstOftheCentury() {
        return firstOftheCentury;
    }

    public void setFirstOftheCentury(String firstOftheCentury) {
        this.firstOftheCentury = firstOftheCentury;
    }

    public String getFirstOftheMonth() {
        return firstOftheMonth;
    }

    public void setFirstOftheMonth(String firstOftheMonth) {
        this.firstOftheMonth = firstOftheMonth;
    }

    public String getFirstOftheYear() {
        return firstOftheYear;
    }

    public void setFirstOftheYear(String firstOftheYear) {
        this.firstOftheYear = firstOftheYear;
    }

    public String getGeodeticDatumAssumedWgs() {
        return geodeticDatumAssumedWgs;
    }

    public void setGeodeticDatumAssumedWgs(String geodeticDatumAssumedWgs) {
        this.geodeticDatumAssumedWgs = geodeticDatumAssumedWgs;
    }

    public String getHomonymIssues() {
        return homonymIssues;
    }

    public void setHomonymIssues(String homonymIssues) {
        this.homonymIssues = homonymIssues;
    }

    public String getImcompleteCollectionDate() {
        return imcompleteCollectionDate;
    }

    public void setImcompleteCollectionDate(String imcompleteCollectionDate) {
        this.imcompleteCollectionDate = imcompleteCollectionDate;
    }

    public String getPossibleDuplicateRecord() {
        return possibleDuplicateRecord;
    }

    public void setPossibleDuplicateRecord(String possibleDuplicateRecord) {
        this.possibleDuplicateRecord = possibleDuplicateRecord;
    }

    public String getInvalidCollectionDate() {
        return invalidCollectionDate;
    }

    public void setInvalidCollectionDate(String invalidCollectionDate) {
        this.invalidCollectionDate = invalidCollectionDate;
    }

    public String getNameNotInNationalChecklists() {
        return nameNotInNationalChecklists;
    }

    public void setNameNotInNationalChecklists(String nameNotInNationalChecklists) {
        this.nameNotInNationalChecklists = nameNotInNationalChecklists;
    }

    public String getNameNotRecognised() {
        return nameNotRecognised;
    }

    public void setNameNotRecognised(String nameNotRecognised) {
        this.nameNotRecognised = nameNotRecognised;
    }

    public String getCultivatedEscapee() {
        return cultivatedEscapee;
    }

    public void setCultivatedEscapee(String cultivatedEscapee) {
        this.cultivatedEscapee = cultivatedEscapee;
    }

    public String getCoordinatesDontMatchSuppliedState() {
        return coordinatesDontMatchSuppliedState;
    }

    public void setCoordinatesDontMatchSuppliedState(String coordinatesDontMatchSuppliedState) {
        this.coordinatesDontMatchSuppliedState = coordinatesDontMatchSuppliedState;
    }

    public String getCoordinateUncertaintyNotValid() {
        return coordinateUncertaintyNotValid;
    }

    public void setCoordinateUncertaintyNotValid(String coordinateUncertaintyNotValid) {
        this.coordinateUncertaintyNotValid = coordinateUncertaintyNotValid;
    }

    public String getUnRecognizedGeodeticDatum() {
        return unRecognizedGeodeticDatum;
    }

    public void setUnRecognizedGeodeticDatum(String unRecognizedGeodeticDatum) {
        this.unRecognizedGeodeticDatum = unRecognizedGeodeticDatum;
    }
}
