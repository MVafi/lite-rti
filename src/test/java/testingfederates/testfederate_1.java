package testingfederates;

import hla.rti1516e.CallbackModel;
import hla.rti1516e.OrderType;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.RTIexception;
import java.io.File;
import java.net.URL;
import java.util.Set;
import java.util.UUID;
import nl.literti.client.LiteRtiAmbassador;
import nl.tno.netn4.datatypes.EntityTypeStruct;
import nl.tno.netn4.datatypes.SupplyStatusStruct;
import nl.tno.netn4.interactions.SetSuppliesStatus;
import nl.tno.netn4.objects.BaseEntity;
import nl.tno.oorti.DefaultOOobjectFactory;
import nl.tno.oorti.NullOOFederateAmbassador;
import nl.tno.oorti.OORTIambassador;
import nl.tno.oorti.OOparameter;
import nl.tno.oorti.OOproperties;
import nl.tno.oorti.impl.OORTIambassadorImpl;

public class testfederate_1 extends NullOOFederateAmbassador {

  OORTIambassador oortiamb;

  public void start() throws Exception {
    // Get LiteRti ambassador using LiteRtiAmbassador (lite-rti implementation)
    OORTIambassador oortiamb =
        new OORTIambassadorImpl(
            new LiteRtiAmbassador(), new DefaultOOobjectFactory(), new OOproperties());

    // connect to the RTI in evoked mode
    System.out.println("Connecting to the RTI...");
    oortiamb.connect(this, CallbackModel.HLA_IMMEDIATE); // Do immediate callbacks or evoked
    System.out.println("Connected to the RTI.");
    System.out.println("Sleeping for 1 seconds");
    Thread.sleep(1000);

    // setup module URL
    URL fom =
        new File("src/test/java/testingfederates/config/NETN-Merged-FULL.xml").toURI().toURL();
    URL mim = new File("src/test/java/testingfederates/config/NETN-MIM.xml").toURI().toURL();

    // Attempt to create a new federation
    // System.out.println("Try creating a new federation");
    try {
      oortiamb.createFederationExecution("TheWorld", new URL[] {fom, mim});
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("Federation already exists, continuing...");
    }

    // // join the federation execution
    // System.out.println("Trying to join...");
    oortiamb.joinFederationExecution("fed_1", "TheWorld", new URL[] {fom, mim});
    Thread.sleep(1000);

    // publish and subscribe to the class of interest
    System.out.println("Trying to subscribe");
    oortiamb.publishObjectClass(BaseEntity.class);
    oortiamb.subscribeObjectClass(BaseEntity.class);
    // // oortiamb.subscribeInteractionClass(SetSuppliesStatus.class);
    // // oortiamb.publishInteractionClass(SetSuppliesStatus.class);

    // // Run tests
    // // this.runTests(oortiamb);
    // this.sendObjectUpdate(oortiamb);

    // // resign and disconnect
    // oortiamb.resignFederationExecution(ResignAction.NO_ACTION);

    System.out.println("Waiting till shutdown...");
    Thread.sleep(50000);
    System.out.println("Manually disconnecting from the RTI...");
    oortiamb.disconnect();
    System.out.println("Disconnected from the RTI.");
  }

  @Override
  public void receiveInteraction(
      Object theInteraction,
      Set<OOparameter> theParameters,
      byte[] userSuppliedTag,
      OrderType sentOrdering,
      TransportationTypeHandle theTransport,
      SupplementalReceiveInfo receiveInfo)
      throws FederateInternalError {
    System.out.println("Received Interaction");
  }

  public static void main(String[] args) throws RTIexception, Exception {
    new testfederate_1().start();
  }

  public void runTests(OORTIambassador oortiamb) throws Exception {
    System.err.println("RUNNING");

    // Send SetSuppliesStatus Interactoin
    SetSuppliesStatus action = new SetSuppliesStatus();
    action.setEntity(UUID.fromString("a6ac89d6-4fee-22dd-be56-cc52ac120002"));

    SupplyStatusStruct sss = new SupplyStatusStruct();
    EntityTypeStruct ets = new EntityTypeStruct();
    ets.setEntityKind((byte) 1);
    ets.setDomain((byte) 1);
    ets.setCountryCode((byte) 1);
    ets.setCategory((byte) 1);
    ets.setSubcategory((byte) 1);
    ets.setSpecific((byte) 1);
    ets.setExtra((byte) 1);
    sss.setQuantity(10);
    sss.setSupplyType(ets);
    action.setSupplyStatus(sss);

    System.err.println("Sending SetSuppliesStatus interaction");
    oortiamb.sendInteraction(action, null);

    // Keep receiving messages
    while (true) {
      oortiamb.evokeCallback(1);
    }
  }

  public void sendObjectUpdate(OORTIambassador oortiamb) throws Exception {
    System.err.println("Object Update");

    BaseEntity entity = new BaseEntity();
    entity.setCallsign("Test-entity-from-EE");
    oortiamb.registerObjectInstance(entity);
    oortiamb.updateAttributeValues(entity, null);

    // int updateCount = 0;
    // while (true) {
    //   Thread.sleep(3000);
    //   updateCount++;
    //   entity.setCallsign("Test-entity-from-EE-" + updateCount);
    //   oortiamb.updateAttributeValues(entity, null);
    //   System.err.println("Updated callsign to: " + entity.getCallsign());
    // }
  }
}
