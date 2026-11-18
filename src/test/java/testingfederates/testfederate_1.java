package testingfederates;

import java.io.File;
import java.net.URL;
import java.util.UUID;

import hla.rti1516e.CallbackModel;
import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.exceptions.RTIexception;
import nl.tno.netn4.datatypes.EntityTypeStruct;
import nl.tno.netn4.datatypes.EquipmentSymbolAmplificationStruct;
import nl.tno.netn4.datatypes.GeodeticPoint;
import nl.tno.netn4.datatypes.SupplyStatusStruct;
import nl.tno.netn4.datatypes.SymbolAmplificationVariant;
import nl.tno.netn4.datatypes.SymbolStruct;
import nl.tno.netn4.datatypes.SymbolTypeEnum;
import nl.tno.netn4.interactions.SetSuppliesStatus;
import nl.tno.netn4.objects.BaseEntity;
import nl.tno.netn4.objects.Equipment;
import nl.tno.oorti.DefaultOOobjectFactory;
import nl.tno.oorti.NullOOFederateAmbassador;
import nl.tno.oorti.OORTIambassador;
import nl.tno.oorti.OOproperties;
import nl.tno.oorti.impl.OORTIambassadorImpl;

public class testfederate_1 extends NullOOFederateAmbassador {

  OORTIambassador oortiamb;

  public void start() throws Exception {
    // Select LiteRTI explicitly, even when other RTI providers are on the classpath.
    RtiFactory factory = RtiFactoryFactory.getRtiFactory();
    OORTIambassador oortiamb =
        new OORTIambassadorImpl(
        factory.getRtiAmbassador(), new DefaultOOobjectFactory(), new OOproperties());

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

    oortiamb.publishObjectClass(Equipment.class);
    oortiamb.publishObjectClass(BaseEntity.class);
    oortiamb.subscribeObjectClass(BaseEntity.class);
    oortiamb.publishInteractionClass(SetSuppliesStatus.class);
    oortiamb.subscribeInteractionClass(SetSuppliesStatus.class);

    // Run tests
    // this.sendObjectUpdate(oortiamb);
    this.createEquipment(oortiamb);
    this.sendInteractions(oortiamb);

    // // resign and disconnect
    // oortiamb.resignFederationExecution(ResignAction.NO_ACTION);

    System.out.println("Waiting till shutdown...");
    Thread.sleep(50000);
    System.out.println("Manually disconnecting from the RTI...");
    oortiamb.disconnect();
    System.out.println("Disconnected from the RTI.");
  }

  public static void main(String[] args) throws RTIexception, Exception {
    new testfederate_1().start();
  }

  public void createEquipment(OORTIambassador oortiamb) throws Exception {
    System.err.println("Creating Equipment: MQ9 Reaper 1");

    // Create Equipment instance with properties from XML
    Equipment equipment = new Equipment();
    equipment.setName("MQ9 Reaper 1");

    // Set symbol information
    SymbolStruct symbol = new SymbolStruct();
    symbol.setId("SFAPMFQA-------");
    SymbolAmplificationVariant amplification = new SymbolAmplificationVariant();
    amplification.setSymbolType(SymbolTypeEnum.Equipment);
    EquipmentSymbolAmplificationStruct equipmentSymbol = new EquipmentSymbolAmplificationStruct();
    equipmentSymbol.setQuantity("1");
    equipmentSymbol.setStaffComments("1");
    equipmentSymbol.setAdditionalInformation("1");
    equipmentSymbol.setEvaluationRating("1");
    equipmentSymbol.setSignatureEquipment("1");
    equipmentSymbol.setHostile("1");
    equipmentSymbol.setIFF__SIF__AIS("1");
    equipmentSymbol.setUniqueDesignation("1");
    equipmentSymbol.setEquipmentType("1");
    equipmentSymbol.setEngagementBarText("1");

    amplification.setEquipmentSymbol(equipmentSymbol);
    symbol.setAmplification(amplification);
    equipment.setSymbol(symbol);

    // Set location (GDC coordinates)
    GeodeticPoint location = new GeodeticPoint();
    location.setLatitude(1.0466343);
    location.setLongitude(0.89164746);
    location.setAltitude(0.0);
    equipment.setLocation(location);
    
    // Set EntityType
    EntityTypeStruct entityType = new EntityTypeStruct();
    entityType.setEntityKind((byte) 1);
    entityType.setDomain((byte) 3);
    entityType.setCountryCode((short) 0);
    entityType.setCategory((byte) 0);
    entityType.setSubcategory((byte) 0);
    entityType.setSpecific((byte) 0);
    entityType.setExtra((byte) 0);
    equipment.setEntityType(entityType);

    equipment.setOrganization(UUID.fromString("a6ac89d6-4fee-22dd-be56-cc52ac120002"));

    // Register and update the equipment object
    oortiamb.registerObjectInstance(equipment);
    oortiamb.updateAttributeValues(equipment, null);

    System.err.println("Created Equipment with name: " + equipment.getName());
  }

  public void sendInteractions(OORTIambassador oortiamb) throws Exception {
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

    // Keep sending interactions
    while (true) {
      Thread.sleep(3000);
      oortiamb.sendInteraction(action, null);
      System.err.println("Sent SetSuppliesStatus interaction");
      sendObjectUpdate(oortiamb);
    }
  }

  public void sendObjectUpdate(OORTIambassador oortiamb) throws Exception {
    System.err.println("Object Update");

    BaseEntity entity = new BaseEntity();
    entity.setCallsign("Test-entity-from-EE");
    oortiamb.registerObjectInstance(entity);
    oortiamb.updateAttributeValues(entity, null);

    int updateCount = 0;
    // while (true) {
    //   Thread.sleep(3000);
    //   updateCount++;
    //   entity.setCallsign("Test-entity-from-EE-" + updateCount);
    //   oortiamb.updateAttributeValues(entity, null);
    //   System.err.println("Updated callsign to: " + entity.getCallsign());
    // }
  }
}
