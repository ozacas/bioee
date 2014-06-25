
This eclipse workspace contains two projects:

1) mascotee - a project to provide web service endpoints (JAX-WS ie. SOAP and JAX-RS ie. REST) to a mascot (www.matrixscience.com) server. It works by running a TomEE+ server on the head node of the mascot server and it connects to the HTTP CGI interface to lodge jobs, check results, get mascot configuration etc.

2) msconvertee - a project similar in spirit to mascotee which provides web service endpoints to Proteowizard's msconvert. This provides the ability to do
data conversion, filtering and compression of vendor-specific instrument data before searching with Mascot. Note that the server must have appropriate software installed for the given vendor - see the proteowizard.sourceforge.net documentation for more details.


Andrew Cassin
acassin at unimelb dot edu dot au
25th June 2014


