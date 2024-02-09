import java.text.*
import java.time.*
import java.time.format.*
import java.time.format.DateTimeFormatter


def LOCALE_NL() {
    new Locale('nl', 'NL')
}

// Helper method to add a sheet to the "typesFound" collector
def registerType(type) {
    //_log.error("Entered regiserType with type = ${type}")

    withTransformationContext {
        def c = _.context.collector(it)
        if (!c['typesFound'].values()) {
            //_log.error("Creating typesFound set")
    		c['typesFound'] << new HashSet()
	    }

        c['typesFound'].values()[0].add(type)
        //_log.error("typesFound is now ${c['typesFound'].values()}")
    }
}

// Returns a closure that tests that a value is not empty
def exists() {
    { args ->
        if (args['value']) {
            return true
        }

        false
    }
}

def isEmpty() {
    { args ->
        if (!args['value']) {
            return true
        }

        false
    }
}

def isEqualTo(value) {
    { args, v ->
        args['value'] == v
    }.rcurry(value)
}

def isNotEqualTo(value) {
    { args, v ->
        args['value'] != v
    }.rcurry(value)
}

// Returns a closure that tests if the value of a field
// is unique within that field of a sheet
def isUnique() {
    { args ->
        def result = true
        def val = args['value']
        def sh = args['sheet']
        def f = args['field']

        withTransformationContext {
            def c = _.context.collector(it)
            
            def collectedValues = c.uniqueChecks[sh][f].values()
            if (collectedValues?.contains(val)) {
                result = false
            }
            
            c.uniqueChecks[sh][f] << val
        }

        result
    }    
}

// Returns a closure that tests if a value is not too long
def isNotTooLong(maxLength) {
    { len, args ->
        _log.info("Length: ${args['value']?.length()}")
        args['value']?.length() <= len
    }.curry(maxLength)    
}

// Returns a closure that tests if a value is >= 0
def isGreaterThanOrEqual0() {
    { args ->
	    parseNumber(args['value']) >= 0
    }
}

// Returns a closure that tests if a value is a valid date string
def isValidDate(format) {
    { args ->
        try {
			def f = DateTimeFormatter.ofPattern(format)
            LocalDate.parse(args['value'], f)
        } catch (Exception e) {
            return false
        }

        true
    }
}

def isValidInteger() {
    { args ->
        args['value'].matches('\\-?\\d+')
    }
}

def isValidCost() {
    { args ->
        args['value'].matches('\\d+(\\,\\d{2})?')
    }
}

def isValidNumber() {
    { args ->
        try {
            parseNumber(args['value'])
        }
        catch (Exception e) {
            return false
        }

        true
     }  
}

def hasNumberOfDigits(numberOfDigitsList) {
    { digits, args ->
		def result = false
		digits.each{ d ->	
			if (args['value'].length() == d){
				result = true
			}
		}
		result
    }.curry(numberOfDigitsList)
}

// Returns a closure that tests if a value is valid HTTP(S) or FTP URL
def isValidUrl() {
    { args ->
        def maybeUrl = args['value'].toLowerCase()

        // simplified URL check regex
        maybeUrl.matches('^(https?|ftp)://[^\\s/$.?#].[^\\s]*$')
    }
}

// Returns a closure that tests if a value is valid URL
// and starts with the given String
def isValidUrlThatStartsWith(mustStartWith) {
    { args, s ->
        def maybeUrl = args['value']
        def isValid = true

        maybeUrl?.startsWith(s) ? isValidUrl().call(args) : false
    }.rcurry(mustStartWith)
}

def isValidCode(codelist, codeInvalid, multipleAllowed) {
    { cl, code, multi, args ->
        def value = args['value']
        def result = true

        if (multi) {
            def badValues = []
            value.split('; ').each {
                if (!cl.contains(it)) {
                    badValues << it
                    result = false
                }
            }

            if (badValues.size() > 0) {
                _log.error("${code}: Illegal values: ${badValues.join(', ')} [${args['sheet']}]")
            }
        }
        else {
			result = cl.contains(value)
        }

        result
    }.curry(codelist).curry(codeInvalid).curry(multipleAllowed)
}

// Returns a closure that tests if a ;-separated list contains a
// specific value
def hasValue(specialValue) {
    { args, sv ->
        sanitize(args['value'])?.split('; ')?.contains(sv)
    }.rcurry(specialValue)
}

def listContainsValue(list) {
    { args, l -> 
        l.contains(args['value'])
    }.rcurry(list)
}

// Returns a closure that tests if a value is present in a
// ;-separated list and if it is, whether it's the only value
// in the list
def isOnlyValueIfPresent(specialValue) {
    { args, sv ->
        def values = sanitize(args['value']).split('; ')
        
        if (values.contains(sv)) {
            return values.size() == 1 && values[0] == sv
        }

        true
    }.rcurry(specialValue)
}

def CODELIST_Boolean() {
    ['Yes', 'No']
}

def CODELIST_Boolean_DE() {
    ['Ja', 'Nein']
}

def CODELIST_NoiseSourceValue() {
    ['agglomerationAir', 'agglomerationIndustry', 'agglomerationRailway', 'agglomerationRoad', 'agglomerationMajorRoad', 'agglomerationMajorRailway', 'agglomerationMajorAirport']
}

def CODELIST_PrioritisationCriteriaValue() {
    ['cost-benefits', 'numberOfExposedPeople', 'levelOfNoiseExposure']
}


def CODELIST_AirportMeasureValue() {
    ['bufferZones', 'buildingInsulationMeasure', 'changeInEmissionLevels', 'closedInfrastructure', 'closureAirport',
    'closureRoute', 'closureRunway', 'communication', 'communityEngagement', 'complaintManagement', 'curfewHours',
    'educationAwarenessActivities', 'greenArea', 'greenNoiseBarrier', 'informationDissemination',
    'infrastructureChange', 'landUsePlanning', 'managementAirTrafficRoutes', 'managementOfAirTrafficOperations',
    'managementRunwayGroundOperations', 'managementTakeoffLanding', 'measureAtPath', 'measureAtSource',
    'measuresForBehaviouralChange', 'newInfrastructure', 'newRoute', 'newRunway', 'noMeasure', 'noiseBarrier',
    'noiseBarrierMeasure', 'noiseQualityArea', 'otherInsulation', 'planningAndOrdinance', 'promotingOtherModes',
    'quietAirplanes', 'quietArea', 'respiteAndNoiseSharing', 'sensitiveAreaPlanning', 'soundscape', 'timeRestriction',
    'urbanPlanning', 'windowInsulation']
}

def CODELIST_RailMeasureValue() {
    ['bansReroutingFreight', 'bansReroutingPassenger', 'bufferZones', 'buildingInsulationMeasure',
    'changeInEmissionLevels', 'closedInfrastructure', 'closureRailwayRoute', 'closureStation', 'communication',
    'communityEngagement', 'complaintManagement', 'educationAwarenessActivities', 'greenArea', 'greenNoiseBarrier',
    'informationDissemination', 'infrastructureChange', 'landUsePlanning', 'lowNoiseBrakes', 'measureAtPath',
    'measureAtSource', 'measuresForBehaviouralChange', 'newBypassViaduct', 'newInfrastructure', 'newRoute',
    'newTunnel', 'noiseBarrier', 'noiseBarrierMeasure', 'noiseQualityArea', 'otherInsulation',
    'otherTrafficManagementMeasure', 'planningAndOrdinance', 'promotingOtherModes', 'quietArea', 'quietEngines',
    'railTrack', 'reductionRailTracks', 'renewalFleet', 'retrofittingWheels', 'sensitiveAreaPlanning', 'soundscape',
    'speedReduction', 'speedReductionMeasure', 'timeRestriction', 'timeRestrictionFreight', 'timeRestrictionPassenger',
    'trackAccessCharges', 'trafficCalmedZones', 'urbanPlanning', 'windowInsulation', 'noMeasure']    
}

def CODELIST_RoadMeasureValue() {
    ['bansReroutingHGV', 'bansReroutingPassenger', 'bufferZones', 'buildingInsulationMeasure',
    'changeInEmissionLevels', 'closedInfrastructure', 'closureRoad', 'communication', 'communityEngagement',
    'complaintManagement', 'congestionCharges', 'cyclingWalkingIncrease', 'educationAwarenessActivities', 'exhaust',
    'greenArea', 'greenNoiseBarrier', 'informationDissemination', 'infrastructureChange', 'landUsePlanning',
    'lowNoiseTyres', 'measureAtPath', 'measureAtSource', 'measuresForBehaviouralChange', 'newInfrastructure',
    'newTunnel', 'noMeasure', 'noiseBarrier', 'noiseBarrierMeasure', 'noiseQualityArea', 'otherInsulation',
    'otherTrafficManagementMeasure', 'parkingManagement', 'physicalTrafficCalming', 'planningAndOrdinance',
    'promotingCarSharing', 'promotingPublicTransport', 'promotingQuietMobility', 'publicTransportIncrease',
    'quietArea', 'quietEngines', 'quietPublicTransport', 'reductionTrafficFlows', 'roadSurface',
    'roundaboutsJunctions', 'sensitiveAreaPlanning', 'smartMobility', 'soundscape', 'speedReduction',
    'speedReductionMeasure', 'timeRestriction', 'timeRestrictionHGV', 'timeRestrictionPassenger',
    'trafficCalmedZones', 'urbanPlanning', 'windowInsulation', 'newBypassBridgeRoad']
}

def CODELIST_IndustryMeasureValue() {
    ['bufferZones', 'buildingInsulationMeasure', 'changeinEmissionLevels', 'closedInfrastructure', 'closureIndustry',
    'communication', 'communityEngagement', 'complaintManagement', 'educationAwarenessActivities', 'enclosure',
    'greenArea', 'greenNoiseBarrier', 'informationDissemination', 'infrastructureChange', 'landUsePlanning',
    'measureAtPath', 'measureAtSource', 'measuresForBehaviouralChange', 'newInfrastructure', 'noMeasure',
    'noiseBarrier', 'noiseBarrierMeasure', 'noiseQualityArea', 'operatingTimeRestriction', 'otherInsulation',
    'planningAndOrdinance', 'quietArea', 'quietOperations', 'relocationIndustry', 'sensitiveAreaPlanning',
    'soundscape', 'timeRestriction', 'urbanPlanning', 'windowInsulation']    
}

def CODELIST_ConsultationMeansValue() {
    ['advertisement', 'focusGroup', 'informationCampaign', 'meeting', 'publicEvent', 'survey', 'workshop']
}

def CODELIST_EvaluationMechanismValue() {
    ['calculation', 'measurements', 'survey_enquiry']
}

def CODELIST_StakeholderValue() {
	['NGOs', 'citizens' ,'governmentBodies', 'privateSector']
}

def CODELIST_EvaluationMechanismValue_DE() {
	['Berechnung', 'Messung' , 'Umfrage/Befragung']
}

def CODELIST_CitationLevelValue() {
	['sub-national', 'national' , 'international', 'european']
}

def CODELIST_CitationTypeValue() {
	['documentCitation', 'legislationCitation', 'resourceCitation']
}

def CODELIST_kvkNr() {
    ['27381083', '30277029', '30279619', '30279625', '30280353', '32160938', '32164984', '32166112', '32168837',
    '32169164', '32170443', '34359304', '34359313', '34362237', '34363858', '34365024', '34366966', '34367942',
    '34368749', '34369366', '34371711', '34374815', '34379987', '34381346', '37159392', '37159666', '50025929',
    '50070525', '50115634', '50778692', '50875825', '50991663', '51332493', '51488744', '51681897', '51896125',
    '62252046', '62255398', '64935345', '73540374', '73541532', '73558907', '85028827']
}

def prefixListItems(list, prefix) {
    list.collect { "${prefix}${it}" as String }
}

def parseNumber(String maybeNumber, Locale locale = Locale.getDefault()) {
    NumberFormat format = NumberFormat.getInstance(locale)
    format.parse(maybeNumber)
}

// Tries to remove line breaks from String values. If the input is not a String,
// the value is returned unchanged.
def sanitize(Object value) {
    try {
        // using try instead of e.g. `(value instanceof String)` because the
        // instanceof operator doesn't seem to work correctly in the sandbox
        return value.replaceAll('[\\n\\r]', '')
    }
    catch (Throwable t) {
        // Ignore
    }

    value
}

def generalCheck(String codeInvalid, String sheet, List fields, String message, Closure validator, String idField = null) {
	def alignmentMode = _project.vars.ALIGNMENT_MODE
	withTransformationContext {
		def c = _.context.collector(it)
		
		def values = []
		fields.each { f ->
			switch (alignmentMode){
				case 'aggregation':
					values << sanitize(_source.p[f].value()).toString()
					break
				case 'validation':
					values << sanitize(c[sheet][field].value())
					break
			}
		}

		def valid = validator.call(['value':values.join('+'), 'sheet':sheet, 'field':fields.join('+')])
		
		if (!valid) {
			_log.error("${codeInvalid}: The values ${values} for the fields ${fields} ${message} [${sheet}]")
		}

		valid
	}
}

def generalCheck(String codeInvalid, String codeMissing, String sheet, String field, String message, Closure validator, String idField = null) {
	def alignmentMode = _project.vars.ALIGNMENT_MODE
	withTransformationContext {
		def c = _.context.collector(it)
		def value
		def id
		switch (alignmentMode){
			case 'aggregation':
				value = sanitize(_source.p[field].value())
				id = idField ? "[${idField} = ${_source.p[idField].value()}]" : ''
				break
			case 'validation':
				value = sanitize(c[sheet][field].value())
				id = idField ? "[${idField} = ${c[sheet][idField].value()}]" : ''
				break
		}

		if (!value && !codeMissing) {
			// Value is not provided and is optional
			return
		}

		def valid = validator.call(['value':value.toString(), 'sheet':sheet, 'field':field])

		if (value) {
			if (!valid) {
				_log.error("${codeInvalid}: The value \'${value}\' for ${field} ${message} [${sheet}]${id}")
			}
		}
		else if (codeMissing) {
			_log.error("${codeMissing}: The value for ${field} must not be missing or empty [${sheet}]${id}")
		}
		
		valid
	}
}




def codelistCheck(String codeInvalid, String codeMissing, String sheet, String field, List codelist, boolean multipleAllowed = false, String idField = null) {
    generalCheck(codeInvalid, codeMissing, sheet, field,
        multipleAllowed ? "contains elements that are not a valid member of the codelist [${codelist.join(', ')}]" : "is not a valid member of the codelist [${codelist.join(', ')}]", 
        isValidCode(codelist, codeInvalid, multipleAllowed))
}

def dateCheck(String codeInvalid, String codeMissing, String sheet, String field, String dateFormat) {
    generalCheck(codeInvalid, codeMissing, sheet, field, 'is not a valid date', isValidDate(dateFormat))
}

def existsCheck(String codeMissing, String sheet, String field) {
    generalCheck(null, codeMissing, sheet, field, null, exists())
}

def urlCheck(String codeInvalid, String codeMissing, String sheet, String field, String mustStartWith = null) {
    generalCheck(codeInvalid, codeMissing, sheet, field, 'is not a valid URL',
        mustStartWith ? isValidUrlThatStartsWith(mustStartWith) : isValidUrl())
}

def costCheck(String codeInvalid, String codeMissing, String sheet, String field) {
    generalCheck(codeInvalid, codeMissing, sheet, field, 'is not a valid whole or decimal number', isValidNumber())
}

def numberCheck(String codeInvalid, String codeMissing, String sheet, String field, Locale locale = Locale.getDefault()) {
    generalCheck(codeInvalid, codeMissing, sheet, field, 'is not a valid whole or decimal number', isValidNumber())
}

def integerCheck(String codeInvalid, String codeMissing, String sheet, String field) {
    generalCheck(codeInvalid, codeMissing, sheet, field, 'is not a valid whole number', isValidInteger())
}

def freetextCheck(String codeInvalid, String codeMissing, String sheet, String field, long maxLength = _project.vars.MAX_FREETEXT_LENGTH as long) {
    generalCheck(codeInvalid, codeMissing, sheet, field, "is too long (max. allowed length is ${maxLength})", isNotTooLong(maxLength))
}

def customCheck(String codeInvalid, String codeMissing, String sheet, field, String message, validator) {
    generalCheck(codeInvalid, codeMissing, sheet, field, message, validator)
}

def conditionalCheck(String code, String sheet, String fieldA, String fieldB, String message, validatorA, validatorB, String idField = null) {
	def alignmentMode = _project.vars.ALIGNMENT_MODE
	withTransformationContext {
		def c = _.context.collector(it)
		def a
		def b
		def id
		switch (alignmentMode){
			case 'aggregation':
				a = sanitize(_source.p[fieldA].value())
				b = sanitize(_source.p[fieldB].value())
				id = idField ? "[${idField} = ${_source.p[idField].value()}]" : ""
				break
			case 'validation':
				a = sanitize(c[sheet][fieldA].value())
				b = sanitize(c[sheet][fieldB].value())
				id = idField ? "[${idField} = ${c[sheet][idField].value()}]" : ""
				break
		}
	
		def validA = validatorA.call(['value':a, 'sheet':sheet, 'field':fieldA])

		def validB = validatorB.call(['value':b, 'sheet':sheet, 'field':fieldB])

		if (validA && !validB) {
			_log.error("${code}: ${message} [${sheet}][${fieldA} = ${a}]$id")
		}
	}
}

def conditionalCheck(String code, String sheet, String fieldA, String fieldB, String idField = null) {
    conditionalCheck(code, sheet, fieldA, fieldB, "If ${fieldA} is reported, ${fieldB} has to be provided", exists(), exists(), idField)
}

def conditionalCheck(String code, String sheet, String fieldA, String fieldB, List fieldAConditionalValues, String idField = null) {
    conditionalCheck(code, sheet, fieldA, fieldB,
        "The field ${fieldB} has to be provided if ${fieldA} is one of ${fieldAConditionalValues}",
        listContainsValue(fieldAConditionalValues), exists(), idField)
}

def conditionalCheck(String code, String sheet, String fieldA, List<String> fieldB, List fieldAConditionalValues, String idField = null) {
    fieldB?.each { b ->
        conditionalCheck(code, sheet, fieldA, b, fieldAConditionalValues, idField)
    }
}

// Applies a validator to a list of fields. If it returns false for all fields, the check fails (OR).
def orCheck(String code, String sheet, List fields, String message, validator) {
	def alignmentMode = _project.vars.ALIGNMENT_MODE
	withTransformationContext {
		def c = _.context.collector(it)
		
		def matches = false
		def fieldsAndValues = ''
		fields?.each { f ->
			def value
			switch (alignmentMode){
				case 'aggregation':
					value = sanitize(_source.p[f].value())
					break
				case 'validation':
					value = sanitize(c[sheet][f].value())
					break
			}		
			fieldsAndValues = "${fieldsAndValues}[${f} = ${value}]"
			matches |= validator.call(['value':value, 'sheet':sheet, 'field':f])
		}

		if (!matches) {
			_log.error("${code}: ${message} [${sheet}]$fieldsAndValues")
		}
	}
}

// Applies a validator to a list of fields. If it returns true for all fields, the check fails (NAND).
def nandCheck(String code, String sheet, List fields, String message, validator) {
    def alignmentMode = _project.vars.ALIGNMENT_MODE
	withTransformationContext {
		def c = _.context.collector(it)
		
		def matches = true
		def fieldsAndValues = ''
		fields?.each { f ->
			def value
			switch (alignmentMode){
				case 'aggregation':
					value = sanitize(_source.p[f].value())
					break
				case 'validation':
					value = sanitize(c[sheet][f].value())
					break
			}	
			fieldsAndValues = "${fieldsAndValues}[${f} = ${value}]"
			matches &= validator.call(['value':value, 'sheet':sheet, 'field':f])
		}

		if (matches) {
			_log.error("${code}: ${message} [${sheet}]$fieldsAndValues")
		}
	}
}

def nandCheck(String code, String sheet, String fieldA, String fieldB, String message, validatorA, validatorB) {
	def alignmentMode = _project.vars.ALIGNMENT_MODE
	withTransformationContext {
		def c = _.context.collector(it)
		
		def a
		def b
		switch (alignmentMode){
			case 'aggregation':
				a = sanitize(_source.p[fieldA].value())
				b = sanitize(_source.p[fieldB].value())
				break
			case 'validation':
				a = sanitize(c[sheet][fieldA].value())
				b = sanitize(c[sheet][fieldB].value())
				break
		}
	
		def validA = validatorA.call(['value':a, 'sheet':sheet, 'field':fieldA])

		def validB = validatorB.call(['value':b, 'sheet':sheet, 'field':fieldB])

		if (validA && validB) {
			_log.error("${code}: ${message} [${sheet}][${fieldA} = ${a}][${fieldB} = ${b}]")
		}
	}
}

def nandCheck(String code, String sheet, String fieldA, String fieldB) {
    nandCheck(code, sheet, fieldA, fieldB, "If ${fieldA} is reported, ${fieldB} must not be reported", exists(), exists())    
}

def nandCheck(String code, String sheet, String fieldA, String fieldB, List fieldAConditionalValues) {
    nandCheck(code, sheet, fieldA, fieldB,
        "The field ${fieldB} must not be provided if ${fieldA} is one of ${fieldAConditionalValues}",
        listContainsValue(fieldAConditionalValues), exists())
}

def nandCheck(String code, String sheet, String fieldA, List<String> fieldB, List fieldAConditionalValues) {
    fieldB?.each { b ->
        nandCheck(code, sheet, fieldA, b, fieldAConditionalValues)
    }
}

def xnorCheck(String code, String sheet, String fieldA, String fieldB, String message, validatorA, validatorB) {
    def alignmentMode = _project.vars.ALIGNMENT_MODE
	withTransformationContext {
		def c = _.context.collector(it)
		
		def a
		def b
		switch (alignmentMode){
			case 'aggregation':
				a = sanitize(_source.p[fieldA].value())
				b = sanitize(_source.p[fieldB].value())
				break
				
			case 'validation':
				a = sanitize(c[sheet][fieldA].value())
				b = sanitize(c[sheet][fieldB].value())
				break
		}
		
		def validA = validatorA.call(['value':a, 'sheet':sheet, 'field':fieldA])

		def validB = validatorB.call(['value':b, 'sheet':sheet, 'field':fieldB])

		if ((validA && !validB) || (!validA && validB)) { // hack to avoid call to java.lang.Boolean.compareTo
			_log.error("${code}: The values for ${fieldA} and ${fieldB} ${message} [${sheet}][${fieldA} = ${a}][${fieldB} = ${b}]")
		}
	}
}

def xnorCheck(String code, String sheet, String fieldA, String fieldB) {
    xnorCheck(code, sheet, fieldA, fieldB, 'must be either both provided or both not provided', exists(), exists())
}

def uniqueCheck(String code, String sheet, String field) {
    generalCheck(code, null, sheet, field, 'must be unique within the table', isUnique())
}

def uniqueCheck(String code, String sheet, List fields) {
    generalCheck(code, sheet, fields, 'must be unique within the table', isUnique())
}
