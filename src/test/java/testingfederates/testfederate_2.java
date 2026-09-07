package testingfederates;

import java.io.File;
import java.net.URL;
import java.util.Set;

import hla.rti1516e.CallbackModel;
import hla.rti1516e.OrderType;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.RTIexception;
import nl.literti.client.LiteRtiAmbassador;
import nl.tno.netn4.objects.BaseEntity;
import nl.tno.oorti.DefaultOOobjectFactory;
import nl.tno.oorti.NullOOFederateAmbassador;
import nl.tno.oorti.OORTIambassador;
import nl.tno.oorti.OOattribute;
import nl.tno.oorti.OOproperties;
import nl.tno.oorti.impl.OORTIambassadorImpl;

public class testfederate_2 extends NullOOFederateAmbassador {

  OORTIambassador oortiamb;

  public void start() throws Exception {
    // create the OORTI Ambassador
    // this.oortiamb = new OORTIfactory().getRtiAmbassador();

    // connect to the RTI in evoked mode
    // oortiamb.connect(this, CallbackModel.HLA_EVOKED);

    // create the OORTI Ambassador
    // this.oortiamb = new OORTIfactory().getRtiAmbassador();

    // Get LiteRti ambassador using LiteRtiAmbassador (lite-rti implementation)
    OORTIambassador oortiamb =
        new OORTIambassadorImpl(
            new LiteRtiAmbassador(), // RtiFactoryFactory.getRtiFactory().getRtiAmbassador()
            new DefaultOOobjectFactory(),
            new OOproperties());

    // connect to the RTI in evoked mode
    System.out.println("Connecting to the RTI...");
    oortiamb.connect(this, CallbackModel.HLA_IMMEDIATE); // Do immediate callbacks or evoked
    System.out.println("Connected to the RTI.");
    System.out.println("Sleeping for 5 seconds");

    // setup module URL
    URL fom =
        new File("src/test/java/testingfederates/config/NETN-Merged-FULL.xml").toURI().toURL();
    URL mim = new File("src/test/java/testingfederates/config/NETN-MIM.xml").toURI().toURL();

    // Attempt to create a new federation
    System.out.println("Try creating a new federation");
    try {
      oortiamb.createFederationExecution("TheWorld", new URL[] {fom, mim});
    } catch (Exception ex) {
      System.out.println("Federation already exists, continuing...");
    }

    Thread.sleep(3000);

    // join the federation execution
    System.out.println("Trying to join...");
    oortiamb.joinFederationExecution("fed_2", "TheWorld", new URL[] {fom, mim});

    // subscribe to BaseEntity object class
    oortiamb.subscribeObjectClass(BaseEntity.class);

    System.out.println("Subscribed to BaseEntity updates.");
    System.out.println("Waiting for BaseEntity updates...");
    Thread.sleep(50000);
    System.out.println("Manually disconnecting from the RTI...");
    oortiamb.disconnect();
    System.out.println("Disconnected from the RTI.");
  }

  @Override
  public void reflectAttributeValues(
      Object theObject,
      Set<OOattribute> theAttributes,
      byte[] userSuppliedTag,
      OrderType sentOrdering,
      TransportationTypeHandle theTransport,
      SupplementalReflectInfo reflectInfo)
      throws FederateInternalError {
    System.out.println(">>>>>>> Received attribute update for object: " + theObject);
    if (theObject instanceof BaseEntity) {
      BaseEntity entity = (BaseEntity) theObject;
      System.out.println(
          ">>>>>>>>> Received BaseEntity update: "
              + entity.getCallsign()
              + " (EntityID: "
              + entity.getEntityIdentifier()
              + ")");
    }
  }

  public static void main(String[] args) throws RTIexception, Exception {
    new testfederate_2().start();
  }
}
