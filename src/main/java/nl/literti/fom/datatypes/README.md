This folder contains the different FOM datatypes. The carriers that contain the data of thes datatypes are located in the folder carriers.

The FOM contains different datatypes. When parsing not all of these datatypes are known upfront. Unkown datatyeps are therefore given the Unresolved Datatype or Enumerator, where these are later resolved by the DatatypeLinker

Currently the FOM is hierachically initialized, which is solved using an algorithm for the MIM. This can cause more issues in the future when have more FOM's that depend on each other.