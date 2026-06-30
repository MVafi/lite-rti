This application is **not** an RTI! It only serves as a socket to transfer NETN objects and interactions between federates

# NETN BUS

This is a test repo that tests the validity of a Fom based message bus, transfering NETN objects and interactions between federates, following the HLA structure. It does not enforce HLA standards, nor is it capable of advanced RTI features. The NETN Bus is not (and therefore does not replace) an RTI.

The NETN Bus should only care for FOM version, e.g. NETN4 or something else. It should not be interested in lrc version etc...