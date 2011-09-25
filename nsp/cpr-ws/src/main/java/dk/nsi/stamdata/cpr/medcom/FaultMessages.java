package dk.nsi.stamdata.cpr.medcom;

public interface FaultMessages
{
	String INTERNAL_SERVER_ERROR = "Internal Server Error";
    String NO_DATA_FOUND_FAULT_MSG = "Ingen data fundet";
    String CALLER_NOT_AUTHORIZED = "CVR fra SOSI ID-Kort er ikke autoriseret til at foretage cpr-opslag";
}