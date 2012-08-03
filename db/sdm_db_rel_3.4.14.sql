-- NSPSUPPORT-91, speed up queries for existing relationships
CREATE INDEX patientCpr ON AssignedDoctor (patientCpr);