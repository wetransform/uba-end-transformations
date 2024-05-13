import eu.esdihumboldt.hale.common.lookup.*;
import eu.esdihumboldt.hale.common.core.io.*;
import java.text.SimpleDateFormat;

def getLookupTableValue(value, tableId) {
	// retrieve Lookup table
	LookupService ls = _serviceProvider.getService(LookupService.class)
	LookupTableInfo lithTable = ls.getTable(tableId)
	def table = lithTable.getTable()

	// get value from Lookup table
	def result = table.lookup(Value.of(value))
	return result
}

def getLookupTableKeys(tableId) {
	// retrieve Lookup table
	LookupService ls = _serviceProvider.getService(LookupService.class)
	LookupTableInfo lithTable = ls.getTable(tableId)
	def table = lithTable.getTable()

	// get all keys from Lookup table
	def keys = table.getKeys()
	
	return keys
}

def CODELIST_RoadMeasureText_DE() {
	['Maßnahmen am Straßenbelag', 'Lärmarme Reifen', 'Leise Motoren', 'Maßnahmen an der Auspuffanlage', 'Umrüstung auf leisere öffentliche Verkehrsmittel und Komponenten', 
	'Zeitliche Beschränkung für LKW', 'Zeitliche Beschränkung für PKW', 'Verringerung der Fahrgeschwindigkeit und Lichtsignalsteuerung', 
	'Kreisverkehre und Kreuzungen', 'Bauliche Maßnahmen zur Verkehrsberuhigung', 'Ausweisung von verkehrsberuhigten Zonen', 'Stärkung des öffentlichen Verkehrs', 
	'Verbesserung der Infrastruktur für Radfahrer und Fußgänger', 'Intelligente Mobilität', 'Veränderung/Reduzierung der Fahrspuren', 'Fahrverbote und Umleitungen für LKW', 
	'Fahrverbote und Umleitungen für PKW', 'Parkraumbewirtschaftung', 'City-Maut', 'Lärmschutzwände und Instandhaltung', 'Grüne Lärmschutzwände und Instandhaltung', 
	'Schallschutzfenster', 'Sonstige Maßnahmen zur Schalldämmung', 'Flächennutzungsplanung/Bauleitplanung', 'Lärmreduzierung für sensible Gebiete', 
	'Abstandsflächen/Pufferzonen', 'Verfügbarkeit von ruhigen Gebieten', 'Verfügbarkeit von Grünflächen', 'Maßnahmen zur Verbesserung des akustischen Raumes', 
	'Neubau von Umgehungsstraßen oder -brücken', 'Neubau von Tunneln', 'Sperrung von Straßen', 'Vermittlung von Informationen', 'Beschwerdemanagement', 
	'Förderung der lärmarmen Mobilität', 'Förderung des öffentlichen Verkehrs', 'Förderung von Carsharing', 'Bildungs- und Aufklärungsaktivitäten']
}

def CODELIST_IndustryMeasureText_DE() {
	['Geräuscharmer Betrieb', 'Zeitliche Beschränkung für den Betrieb', 'Lärmschutzwände und Instandhaltung', 'Grüne Lärmschutzwände und Instandhaltung', 'Einhausungen', 'Schallschutzfenster', 'Sonstige Maßnahmen zur Schalldämmung', 'Flächennutzungsplanung/Bauleitplanung', 'Lärmreduzierung für sensible Gebiete', 'Abstandsflächen/Pufferzonen', 'Verfügbarkeit von ruhigen Gebieten', 'Verfügbarkeit von Grünflächen', 'Maßnahmen zur Verbesserung des akustischen Raumes', 'Standortwechsel von Industrieanlagen', 'Schließung von Industrieanlagen', 'Vermittlung von Informationen', 'Beschwerdemanagement', 'Bildungs- und Aufklärungsaktivitäten']
}

def CODELIST_AirportMeasureText_DE() {
	['Lärmarme Flugzeuge', 'Zeitliche Betriebsbeschränkungen', 'Lärmpausen und Lärmkontingente', 'Flugrouten', 'Start- und Landeverfahren', 'Flughafenbetrieb', 'Lärmschutzwände und Instandhaltung', 'Grüne Lärmschutzwände und Instandhaltung', 'Schallschutzfenster', 'Sonstige Maßnahmen zur Schalldämmung', 'Flächennutzungsplanung/Bauleitplanung', 'Lärmreduzierung für sensible Gebiete', 'Abstandsflächen/Pufferzonen', 'Verfügbarkeit von ruhigen Gebieten', 'Verfügbarkeit von Grünflächen', 'Maßnahmen zur Verbesserung des akustischen Raumes', 'Neue Flugroute', 'Neue Start- und Landebahn', 'Aufhebung Flugroute', 'Sperrung Start- und Landebahn', 'Schließung Flughafen', 'Vermittlung von Informationen', 'Beschwerdemanagement', 'Bildungs- und Aufklärungsaktivitäten', 'Förderung anderer Verkehrsträger']
}

def CODELIST_RailMeasureText_DE() {
	['Maßnahmen am Gleis', 'Umrüstung von Rädern oder Radkomponenten', 'Geräuscharme Bremsen', 'Geräuscharme Motoren', 'Erneuerung des Fuhrparks', 'Zeitliche Beschränkung für den Güterverkehr', 'Zeitliche Beschränkung für den Personenverkehr', 'Verringerung der Fahrgeschwindigkeit und Signalsteuerung', 'Ausweisung von verkehrsberuhigten Zonen für Schienenverkehr', 'Veränderung/Reduzierung der Gleisanlagen', 'Trassenpreise', 'Fahrverbote und Umleitung von Güterverkehren', 'Fahrverbote und Umleitung von Personenverkehren', 'Lärmschutzwände und Instandhaltung', 'Grüne Lärmschutzwände und Instandhaltung', 'Schallschutzfenster', 'Sonstige Maßnahmen zur Schalldämmung', 'Flächennutzungsplanung/Bauleitplanung', 'Lärmreduzierung für sensible Gebiete', 'Abstandsflächen/Pufferzonen', 'Verfügbarkeit von ruhigen Gebieten', 'Verfügbarkeit von Grünflächen', 'Maßnahmen zur Verbesserung des akustischen Raumes', 'Neubau von Strecken', 'Neue Eisenbahnumfahrung/neues Brückenbauwerk', 'Neubau von Tunneln', 'Stilllegung einer Schienenstrecke', 'Stilllegung eines Bahnhofs', 'Vermittlung von Informationen', 'Beschwerdemanagement', 'Bildungs- und Aufklärungsaktivitäten', 'Förderung anderer Verkehrsträger']
}

def CODELIST_PrioritisationCriteriaText_DE() {
	['Kosten-Nutzen-Analysen', 'Höhe der Lärmbelastung', 'Zahl der lärmbelasteten Menschen']
}

def CODELIST_ConsultationMeansText_DE() {
	['Anzeigen/Werbung', 'Ansprache verschiedener Interessenträger', 'Informationskampagne', 'Besprechungen/Sitzungen', 'Öffentliche Veranstaltung',
	'Umfrage', 'Workshop']
}

def CODELIST_stakeholdersTypeText_DE() {
	['Bürger:innen', 'Nichtstaatliche Organisationen', 'Staatliche Stellen', 'Privatwirtschaft']
}

def classifyListValues (valueList, tableId) {
	def valueList_classified = []
	valueList.each{v ->
		def classifiedValue = getLookupTableValue(v, tableId)
		valueList_classified.add(classifiedValue)
	}
	return valueList_classified
}

def classifyJaNein (value) {
	switch(value) {
		case 'Ja':
			return 'Yes'
		case 'Nein':
			return 'No'
	}
}

def formatDate(dateString) {
	// Input date format dd.MM.yyyy as string, output yyy-MM-dd
	if (dateString) {
		try {
			def parser = new SimpleDateFormat('dd.MM.yyyy')
			def parsedDate = parser.parse(dateString)
			def formatter = new SimpleDateFormat('yyyy-MM-dd')
			def newDate = formatter.format(parsedDate)
			return newDate.toString()
		} catch (Exception e) {
            return null
        }
	}
}