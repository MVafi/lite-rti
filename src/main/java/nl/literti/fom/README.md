Generally alot of files are direclty coppied from portico

TODO : investigate and clean up files since i'm not sure whether everything is required for our usecase

Goal of this folder is to eventually make this a seperate lib if possible

## Datatypes

When parsing a FOM, a delayed binding mechanism is used with respect to the datatypes. If a datatype has not been resolved before, the UnresolvedDatatype will be given to the ..., where after completing the full FOM, the UnresolvedDatatypes are resolved.

Spatial filtering using the space attribute has not been implemented
Objects have been implemented only