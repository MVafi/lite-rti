package federates;

import java.util.Set;
import java.util.UUID;

import hla.rti1516e.CallbackModel;
import hla.rti1516e.OrderType;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.RTIexception;
import nl.tno.netn4.datatypes.EntityTypeStruct;
import nl.tno.netn4.datatypes.SupplyStatusStruct;
import nl.tno.netn4.interactions.SetSuppliesStatus;
import nl.tno.netnbus.client.NetnBusAmbassador;
import nl.tno.oorti.NullOOFederateAmbassador;
import nl.tno.oorti.OORTIambassador;
import nl.tno.oorti.OOparameter;

public class void_testfederate extends NullOOFederateAmbassador {

  OORTIambassador oortiamb;

  public void start() throws Exception {
    // create the OORTI Ambassador
    // this.oortiamb = new OORTIfactory().getRtiAmbassador();

    // Use the NETN bus ambassador, instead of the rti ambassador, to connect to the NETN Bus
    OORTIambassador oortiamb = new NetnBusAmbassador();

    // connect to the RTI in evoked mode
    System.out.println("Connecting to the RTI...");
    oortiamb.connect(this, CallbackModel.HLA_EVOKED);
    System.out.println("Sleeping for 10 seconds");
    Thread.sleep(10000);
    System.out.println("Connected to the RTI.");

    // // setup module URL
    // URL fom = new File("config/NETN-Merged-FULL.xml").toURI().toURL();
    // URL mim = new File("config/NETN-MIM.xml").toURI().toURL();

    // // Attempt to create a new federation
    // try {
    //   oortiamb.createFederationExecution("TheWorld", new URL[] {fom, mim});
    // } catch (Exception ex) {
    //   System.out.println("Federation already exists, continuing...");
    // }

    // // join the federation execution
    // oortiamb.joinFederationExecution("TestFedEC", "TheWorld", new URL[] {fom, mim});

    // // publish and subscribe to the class of interest
    // oortiamb.subscribeInteractionClass(SetSuppliesStatus.class);
    // oortiamb.publishInteractionClass(SetSuppliesStatus.class);

    // // Run tests
    // this.runTests(oortiamb);

    // // resign and disconnect
    // oortiamb.resignFederationExecution(ResignAction.NO_ACTION);
    // oortiamb.disconnect();
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
    new void_testfederate().start();
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
}
