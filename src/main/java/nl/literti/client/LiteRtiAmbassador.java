package nl.literti.client;

import java.net.URL;
import java.util.Set;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleFactory;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleSetFactory;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.AttributeHandleValueMapFactory;
import hla.rti1516e.AttributeSetRegionSetPairList;
import hla.rti1516e.AttributeSetRegionSetPairListFactory;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.DimensionHandle;
import hla.rti1516e.DimensionHandleFactory;
import hla.rti1516e.DimensionHandleSet;
import hla.rti1516e.DimensionHandleSetFactory;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.FederateHandleFactory;
import hla.rti1516e.FederateHandleSet;
import hla.rti1516e.FederateHandleSetFactory;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.InteractionClassHandleFactory;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.LogicalTimeFactory;
import hla.rti1516e.LogicalTimeInterval;
import hla.rti1516e.MessageRetractionHandle;
import hla.rti1516e.MessageRetractionReturn;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectClassHandleFactory;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.ObjectInstanceHandleFactory;
import hla.rti1516e.OrderType;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleFactory;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.ParameterHandleValueMapFactory;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.RangeBounds;
import hla.rti1516e.RegionHandle;
import hla.rti1516e.RegionHandleSet;
import hla.rti1516e.RegionHandleSetFactory;
import hla.rti1516e.ResignAction;
import hla.rti1516e.ServiceGroup;
import hla.rti1516e.TimeQueryReturn;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.TransportationTypeHandleFactory;
import hla.rti1516e.exceptions.AlreadyConnected;
import hla.rti1516e.exceptions.AsynchronousDeliveryAlreadyDisabled;
import hla.rti1516e.exceptions.AsynchronousDeliveryAlreadyEnabled;
import hla.rti1516e.exceptions.AttributeAcquisitionWasNotRequested;
import hla.rti1516e.exceptions.AttributeAlreadyBeingAcquired;
import hla.rti1516e.exceptions.AttributeAlreadyBeingChanged;
import hla.rti1516e.exceptions.AttributeAlreadyBeingDivested;
import hla.rti1516e.exceptions.AttributeAlreadyOwned;
import hla.rti1516e.exceptions.AttributeDivestitureWasNotRequested;
import hla.rti1516e.exceptions.AttributeNotDefined;
import hla.rti1516e.exceptions.AttributeNotOwned;
import hla.rti1516e.exceptions.AttributeNotPublished;
import hla.rti1516e.exceptions.AttributeRelevanceAdvisorySwitchIsOff;
import hla.rti1516e.exceptions.AttributeRelevanceAdvisorySwitchIsOn;
import hla.rti1516e.exceptions.AttributeScopeAdvisorySwitchIsOff;
import hla.rti1516e.exceptions.AttributeScopeAdvisorySwitchIsOn;
import hla.rti1516e.exceptions.CallNotAllowedFromWithinCallback;
import hla.rti1516e.exceptions.ConnectionFailed;
import hla.rti1516e.exceptions.CouldNotCreateLogicalTimeFactory;
import hla.rti1516e.exceptions.CouldNotOpenFDD;
import hla.rti1516e.exceptions.CouldNotOpenMIM;
import hla.rti1516e.exceptions.DeletePrivilegeNotHeld;
import hla.rti1516e.exceptions.DesignatorIsHLAstandardMIM;
import hla.rti1516e.exceptions.ErrorReadingFDD;
import hla.rti1516e.exceptions.ErrorReadingMIM;
import hla.rti1516e.exceptions.FederateAlreadyExecutionMember;
import hla.rti1516e.exceptions.FederateHandleNotKnown;
import hla.rti1516e.exceptions.FederateHasNotBegunSave;
import hla.rti1516e.exceptions.FederateIsExecutionMember;
import hla.rti1516e.exceptions.FederateNameAlreadyInUse;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.FederateOwnsAttributes;
import hla.rti1516e.exceptions.FederateServiceInvocationsAreBeingReportedViaMOM;
import hla.rti1516e.exceptions.FederateUnableToUseTime;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.IllegalName;
import hla.rti1516e.exceptions.InTimeAdvancingState;
import hla.rti1516e.exceptions.InconsistentFDD;
import hla.rti1516e.exceptions.InteractionClassAlreadyBeingChanged;
import hla.rti1516e.exceptions.InteractionClassNotDefined;
import hla.rti1516e.exceptions.InteractionClassNotPublished;
import hla.rti1516e.exceptions.InteractionParameterNotDefined;
import hla.rti1516e.exceptions.InteractionRelevanceAdvisorySwitchIsOff;
import hla.rti1516e.exceptions.InteractionRelevanceAdvisorySwitchIsOn;
import hla.rti1516e.exceptions.InvalidAttributeHandle;
import hla.rti1516e.exceptions.InvalidDimensionHandle;
import hla.rti1516e.exceptions.InvalidFederateHandle;
import hla.rti1516e.exceptions.InvalidInteractionClassHandle;
import hla.rti1516e.exceptions.InvalidLocalSettingsDesignator;
import hla.rti1516e.exceptions.InvalidLogicalTime;
import hla.rti1516e.exceptions.InvalidLookahead;
import hla.rti1516e.exceptions.InvalidMessageRetractionHandle;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.InvalidOrderName;
import hla.rti1516e.exceptions.InvalidOrderType;
import hla.rti1516e.exceptions.InvalidParameterHandle;
import hla.rti1516e.exceptions.InvalidRangeBound;
import hla.rti1516e.exceptions.InvalidRegion;
import hla.rti1516e.exceptions.InvalidRegionContext;
import hla.rti1516e.exceptions.InvalidResignAction;
import hla.rti1516e.exceptions.InvalidServiceGroup;
import hla.rti1516e.exceptions.InvalidTransportationName;
import hla.rti1516e.exceptions.InvalidTransportationType;
import hla.rti1516e.exceptions.InvalidUpdateRateDesignator;
import hla.rti1516e.exceptions.LogicalTimeAlreadyPassed;
import hla.rti1516e.exceptions.MessageCanNoLongerBeRetracted;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NameSetWasEmpty;
import hla.rti1516e.exceptions.NoAcquisitionPending;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.ObjectClassNotDefined;
import hla.rti1516e.exceptions.ObjectClassNotPublished;
import hla.rti1516e.exceptions.ObjectClassRelevanceAdvisorySwitchIsOff;
import hla.rti1516e.exceptions.ObjectClassRelevanceAdvisorySwitchIsOn;
import hla.rti1516e.exceptions.ObjectInstanceNameInUse;
import hla.rti1516e.exceptions.ObjectInstanceNameNotReserved;
import hla.rti1516e.exceptions.ObjectInstanceNotKnown;
import hla.rti1516e.exceptions.OwnershipAcquisitionPending;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RegionDoesNotContainSpecifiedDimension;
import hla.rti1516e.exceptions.RegionInUseForUpdateOrSubscription;
import hla.rti1516e.exceptions.RegionNotCreatedByThisFederate;
import hla.rti1516e.exceptions.RequestForTimeConstrainedPending;
import hla.rti1516e.exceptions.RequestForTimeRegulationPending;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.RestoreNotInProgress;
import hla.rti1516e.exceptions.RestoreNotRequested;
import hla.rti1516e.exceptions.SaveInProgress;
import hla.rti1516e.exceptions.SaveNotInProgress;
import hla.rti1516e.exceptions.SaveNotInitiated;
import hla.rti1516e.exceptions.SynchronizationPointLabelNotAnnounced;
import hla.rti1516e.exceptions.TimeConstrainedAlreadyEnabled;
import hla.rti1516e.exceptions.TimeConstrainedIsNotEnabled;
import hla.rti1516e.exceptions.TimeRegulationAlreadyEnabled;
import hla.rti1516e.exceptions.TimeRegulationIsNotEnabled;
import hla.rti1516e.exceptions.UnsupportedCallbackModel;
import nl.literti.utils.AttributeHandleSetFactoryImpl;
import nl.literti.utils.AttributeHandleValueMapFactoryImpl;

public class LiteRtiAmbassador implements RTIambassador {
  // Todo: remove oorti logic that is inherited
  // todo: remove all output debug lines

  private final LiteRtiClientContext clientContext;

  public LiteRtiAmbassador() {
    this.clientContext = new LiteRtiClientContext();
  }

  ////////////////////////////////////
  // Federation Management Services //
  ////////////////////////////////////
  @Override
  public void connect(
      FederateAmbassador federateReference,
      CallbackModel callbackModel,
      String localSettingsDesignator)
      throws ConnectionFailed,
          InvalidLocalSettingsDesignator,
          UnsupportedCallbackModel,
          AlreadyConnected,
          CallNotAllowedFromWithinCallback,
          RTIinternalError {
    throw new UnsupportedOperationException("Current method is currently unsupported 1");
  }

  @Override
  public void connect(FederateAmbassador federateReference, CallbackModel callbackModel)
      throws ConnectionFailed,
          InvalidLocalSettingsDesignator,
          UnsupportedCallbackModel,
          AlreadyConnected,
          CallNotAllowedFromWithinCallback,
          RTIinternalError {

    // Used as UUID for the connected federate, not very robust but sufficient for now
    String federateName =
        federateReference.getClass().getName() + "@" + System.identityHashCode(federateReference);

    System.out.println("[Ambassador] Connecting federate: " + federateName);  

    int maxRetries = 5;
    int retryDelay = 2000; // milliseconds

    for (int attempt = 1; attempt <= maxRetries; attempt++) {
      if (clientContext.connectFederate(federateName)) {

        // Store the federate ambassador reference in the client context for callbacks
        System.out.println("[Ambassador] Setting FederateAmbassador for: " + federateName);  
        clientContext.setFederateAmbassador(federateReference);
        return;
      }

      if (attempt < maxRetries) {
        System.out.println(
            "[LiteRtiAmbassador] Connection attempt "
                + attempt
                + " failed. Retrying in "
                + retryDelay
                + "ms...");
        try {
          Thread.sleep(retryDelay);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new ConnectionFailed("Connection interrupted: " + e.getMessage());
        }
      }
    }

    throw new ConnectionFailed(
        "Could not connect to LiteRti server after " + maxRetries + " attempts.");
  }

  // OORTI method
  // @Override
  // public void connect(OOFederateAmbassador federateReference, CallbackModel callbackModel)
  //     throws ConnectionFailed,
  //         InvalidLocalSettingsDesignator,
  //         UnsupportedCallbackModel,
  //         AlreadyConnected,
  //         CallNotAllowedFromWithinCallback,
  //         RTIinternalError {}

  @Override
  public void disconnect()
      throws FederateIsExecutionMember, CallNotAllowedFromWithinCallback, RTIinternalError {
    System.out.println("Disconnect call 1");
    clientContext.disconnect();
  }

  @Override
  public void createFederationExecution(
      String federationExecutionName,
      URL[] fomModules,
      URL mimModule,
      String logicalTimeImplementationName)
      throws CouldNotCreateLogicalTimeFactory,
          InconsistentFDD,
          ErrorReadingFDD,
          CouldNotOpenFDD,
          ErrorReadingMIM,
          CouldNotOpenMIM,
          DesignatorIsHLAstandardMIM,
          FederationExecutionAlreadyExists,
          NotConnected,
          RTIinternalError {
    throw new UnsupportedOperationException("Current method is currently unsupported 2");
  }

  @Override
  public void createFederationExecution(
      String federationExecutionName, URL[] fomModules, String logicalTimeImplementationName)
      throws CouldNotCreateLogicalTimeFactory,
          InconsistentFDD,
          ErrorReadingFDD,
          CouldNotOpenFDD,
          FederationExecutionAlreadyExists,
          NotConnected,
          RTIinternalError {
    throw new UnsupportedOperationException("Current method is currently unsupported 3");
  }

  @Override
  public void createFederationExecution(
      String federationExecutionName, URL[] fomModules, URL mimModule)
      throws InconsistentFDD,
          ErrorReadingFDD,
          CouldNotOpenFDD,
          ErrorReadingMIM,
          CouldNotOpenMIM,
          DesignatorIsHLAstandardMIM,
          FederationExecutionAlreadyExists,
          NotConnected,
          RTIinternalError {
    throw new UnsupportedOperationException("Current method is currently unsupported 4");
  }

  @Override
  public void createFederationExecution(String federationExecutionName, URL[] fomModules)
      throws FederationExecutionAlreadyExists, NotConnected, RTIinternalError {
    try {
      clientContext.createFederationExecution(federationExecutionName, fomModules);
      System.out.println("[LiteRtiAmbassador] Federation created: " + federationExecutionName);
    } catch (Exception e) {
      throw new RTIinternalError(e.getMessage());
    }
  }

  @Override
  public void createFederationExecution(String federationExecutionName, URL fomModule)
      throws InconsistentFDD,
          ErrorReadingFDD,
          CouldNotOpenFDD,
          FederationExecutionAlreadyExists,
          NotConnected,
          RTIinternalError {}

  @Override
  public void destroyFederationExecution(String federationExecutionName)
      throws FederatesCurrentlyJoined,
          FederationExecutionDoesNotExist,
          NotConnected,
          RTIinternalError {}

  @Override
  public void listFederationExecutions() throws NotConnected, RTIinternalError {}

  @Override
  public FederateHandle joinFederationExecution(
      String federateName,
      String federateType,
      String federationExecutionName,
      URL[] additionalFomModules)
      throws CouldNotCreateLogicalTimeFactory,
          FederateNameAlreadyInUse,
          FederationExecutionDoesNotExist,
          InconsistentFDD,
          ErrorReadingFDD,
          CouldNotOpenFDD,
          SaveInProgress,
          RestoreInProgress,
          FederateAlreadyExecutionMember,
          NotConnected,
          CallNotAllowedFromWithinCallback,
          RTIinternalError {
    return joinFederationExecution(federateType, federationExecutionName, additionalFomModules);
  }

  @Override
  public FederateHandle joinFederationExecution(
      String federateType, String federationExecutionName, URL[] additionalFomModules)
      throws CouldNotCreateLogicalTimeFactory,
          FederationExecutionDoesNotExist,
          InconsistentFDD,
          ErrorReadingFDD,
          CouldNotOpenFDD,
          SaveInProgress,
          RestoreInProgress,
          FederateAlreadyExecutionMember,
          NotConnected,
          CallNotAllowedFromWithinCallback,
          RTIinternalError {

    // For now, just join (no FDD/MIM parsing)
    try {
        clientContext.joinFederationExecution(federateType, federationExecutionName, additionalFomModules);
      System.out.println(
          "[LiteRtiAmbassador] Federation joined: "
              + federationExecutionName
              + " as federate type: "
              + federateType);
    
    } catch (Exception e) {
      throw new RTIinternalError(e.getMessage());
    }
    return null; // Return null for now, as we don't have a proper FederateHandle
  }

  @Override
  public FederateHandle joinFederationExecution(
      String federateName, String federateType, String federationExecutionName)
      throws CouldNotCreateLogicalTimeFactory,
          FederateNameAlreadyInUse,
          FederationExecutionDoesNotExist,
          SaveInProgress,
          RestoreInProgress,
          FederateAlreadyExecutionMember,
          NotConnected,
          CallNotAllowedFromWithinCallback,
          RTIinternalError {
    try {
        return joinFederationExecution(federateType, federationExecutionName, new URL[] {});
    } catch (Exception e) {
        throw new RTIinternalError(e.getMessage());
    }
  }

  @Override
  public FederateHandle joinFederationExecution(String federateType, String federationExecutionName)
      throws CouldNotCreateLogicalTimeFactory,
          FederationExecutionDoesNotExist,
          SaveInProgress,
          RestoreInProgress,
          FederateAlreadyExecutionMember,
          NotConnected,
          CallNotAllowedFromWithinCallback,
          RTIinternalError {
    try {
        return joinFederationExecution(federateType, federationExecutionName, new URL[] {});
    } catch (Exception e) {
        throw new RTIinternalError(e.getMessage());
    }
  }

  @Override
  public void resignFederationExecution(ResignAction resignAction)
      throws InvalidResignAction,
          OwnershipAcquisitionPending,
          FederateOwnsAttributes,
          FederateNotExecutionMember,
          NotConnected,
          CallNotAllowedFromWithinCallback,
          RTIinternalError {}

  @Override
  public void registerFederationSynchronizationPoint(
      String synchronizationPointLabel, byte[] userSuppliedTag)
      throws SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void registerFederationSynchronizationPoint(
      String synchronizationPointLabel,
      byte[] userSuppliedTag,
      FederateHandleSet synchronizationSet)
      throws InvalidFederateHandle,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void synchronizationPointAchieved(String synchronizationPointLabel)
      throws SynchronizationPointLabelNotAnnounced,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void synchronizationPointAchieved(
      String synchronizationPointLabel, boolean successIndicator)
      throws SynchronizationPointLabelNotAnnounced,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void requestFederationSave(String label)
      throws SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void requestFederationSave(String label, LogicalTime theTime)
      throws LogicalTimeAlreadyPassed,
          InvalidLogicalTime,
          FederateUnableToUseTime,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void federateSaveBegun()
      throws SaveNotInitiated,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void federateSaveComplete()
      throws FederateHasNotBegunSave,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void federateSaveNotComplete()
      throws FederateHasNotBegunSave,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void abortFederationSave()
      throws SaveNotInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {}

  @Override
  public void queryFederationSaveStatus()
      throws RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {}

  @Override
  public void requestFederationRestore(String label)
      throws SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void federateRestoreComplete()
      throws RestoreNotRequested,
          SaveInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void federateRestoreNotComplete()
      throws RestoreNotRequested,
          SaveInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void abortFederationRestore()
      throws RestoreNotInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {}

  @Override
  public void queryFederationRestoreStatus()
      throws SaveInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {}

  /////////////////////////////////////
  // Declaration Management Services //
  /////////////////////////////////////
  @Override
  public void publishObjectClassAttributes(
      ObjectClassHandle theClass, AttributeHandleSet attributeList)
      throws AttributeNotDefined,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    if (theClass == null) {
      throw new RTIinternalError("ObjectClassHandle cannot be null");
    }
    // Validate no null attribute handles if set is provided
    if (attributeList != null && !attributeList.isEmpty()) {
      for (AttributeHandle attr : attributeList) {
        if (attr == null) {
          throw new RTIinternalError("AttributeHandle cannot be null in AttributeHandleSet");
        }
      }
    }
    System.out.println("OLD HLA PUBLISH CALL");
    clientContext.publishObjectClassAttributes(theClass, attributeList);
  }

  @Override
  public void unpublishObjectClass(ObjectClassHandle theClass)
      throws OwnershipAcquisitionPending,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void unpublishObjectClassAttributes(
      ObjectClassHandle theClass, AttributeHandleSet attributeList)
      throws OwnershipAcquisitionPending,
          AttributeNotDefined,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void publishInteractionClass(InteractionClassHandle theInteraction)
      throws InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void unpublishInteractionClass(InteractionClassHandle theInteraction)
      throws InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void subscribeObjectClassAttributes(
      ObjectClassHandle theClass, AttributeHandleSet attributeList)
      throws AttributeNotDefined,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {

    if (theClass == null) {
      throw new RTIinternalError("ObjectClassHandle cannot be null");
    }
    // Validate no null attribute handles if set is provided
    if (attributeList != null && !attributeList.isEmpty()) {
      for (AttributeHandle attr : attributeList) {
        if (attr == null) {
          throw new RTIinternalError("AttributeHandle cannot be null in AttributeHandleSet");
        }
      }
    }
    clientContext.subscribeObjectClassAttributes(theClass, attributeList);
  }

  @Override
  public void subscribeObjectClassAttributes(
      ObjectClassHandle theClass, AttributeHandleSet attributeList, String updateRateDesignator)
      throws AttributeNotDefined,
          ObjectClassNotDefined,
          InvalidUpdateRateDesignator,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    System.out.println("## not supported ## needs future support if the OORTI uses it");
  }

  @Override
  public void subscribeObjectClassAttributesPassively(
      ObjectClassHandle theClass, AttributeHandleSet attributeList)
      throws AttributeNotDefined,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void subscribeObjectClassAttributesPassively(
      ObjectClassHandle theClass, AttributeHandleSet attributeList, String updateRateDesignator)
      throws AttributeNotDefined,
          ObjectClassNotDefined,
          InvalidUpdateRateDesignator,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void unsubscribeObjectClass(ObjectClassHandle theClass)
      throws ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void unsubscribeObjectClassAttributes(
      ObjectClassHandle theClass, AttributeHandleSet attributeList)
      throws AttributeNotDefined,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  // Standard RTI Method
  @Override
  public void subscribeInteractionClass(InteractionClassHandle theClass)
      throws FederateServiceInvocationsAreBeingReportedViaMOM,
          InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    System.out.println("standard RTI call? 1");
    // Non oorti logic
  }

  // Standard RTI Method
  @Override
  public void subscribeInteractionClassPassively(InteractionClassHandle theClass)
      throws FederateServiceInvocationsAreBeingReportedViaMOM,
          InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    throw new RTIinternalError("Not implemented 1");
  }

  @Override
  public void unsubscribeInteractionClass(InteractionClassHandle theClass)
      throws InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  ////////////////////////////////
  // Object Management Services //
  ////////////////////////////////
  @Override
  public void reserveObjectInstanceName(String theObjectName)
      throws IllegalName,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void releaseObjectInstanceName(String theObjectInstanceName)
      throws ObjectInstanceNameNotReserved,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void reserveMultipleObjectInstanceName(Set<String> theObjectNames)
      throws IllegalName,
          NameSetWasEmpty,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void releaseMultipleObjectInstanceName(Set<String> theObjectNames)
      throws ObjectInstanceNameNotReserved,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public ObjectInstanceHandle registerObjectInstance(ObjectClassHandle theClass)
      throws ObjectClassNotPublished,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    
    if (theClass == null) {
      throw new RTIinternalError("ObjectClassHandle cannot be null");
    }

    try {
      // Delegate to socket client to register object instance
      return clientContext.registerObjectInstance(theClass, theClass.toString());
      
    } catch (RuntimeException e) {
      // Convert RuntimeException to appropriate RTI exception
      String errorMsg = e.getMessage();
      if (errorMsg != null && errorMsg.contains("has not published")) {
        throw new ObjectClassNotPublished("Object class has not been published by this federate");
      }
      throw new RTIinternalError("Failed to register object instance: " + errorMsg);
    }
  }

  @Override
  public ObjectInstanceHandle registerObjectInstance(
      ObjectClassHandle theClass, String theObjectName)
      throws ObjectInstanceNameInUse,
          ObjectInstanceNameNotReserved,
          ObjectClassNotPublished,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    
    throw new RTIinternalError("Not implemented method");
    // if (theClass == null) {
    //   throw new RTIinternalError("ObjectClassHandle cannot be null");
    // }

    // if (theObjectName == null || theObjectName.isEmpty()) {
    //   throw new RTIinternalError("Object instance name cannot be null or empty");
    // }

    // try {
    //   // Delegate to socket client to register object instance with a specific name
    //   return socketClient.registerObjectInstance(theClass, theObjectName);
    // } catch (RuntimeException e) {
    //   // Convert RuntimeException to appropriate RTI exception
    //   String errorMsg = e.getMessage();
    //   if (errorMsg != null && errorMsg.contains("has not published")) {
    //     throw new ObjectClassNotPublished("Object class has not been published by this federate");
    //   }
    //   if (errorMsg != null && errorMsg.contains("already in use")) {
    //     throw new ObjectInstanceNameInUse("Object instance name '" + theObjectName + "' is already in use");
    //   }
    //   throw new RTIinternalError("Failed to register object instance: " + errorMsg);
    // }
  }

  @Override
  public void updateAttributeValues(
      ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes, byte[] userSuppliedTag)
      throws AttributeNotOwned,
          AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            clientContext.updateAttributeValues(theObject, theAttributes, userSuppliedTag);
          }

  @Override
  public MessageRetractionReturn updateAttributeValues(
      ObjectInstanceHandle theObject,
      AttributeHandleValueMap theAttributes,
      byte[] userSuppliedTag,
      LogicalTime theTime)
      throws InvalidLogicalTime,
          AttributeNotOwned,
          AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    throw new RTIinternalError("updateAttributeValues with LogicalTime not implemented");
  }

  @Override
  public void sendInteraction(
      InteractionClassHandle theInteraction,
      ParameterHandleValueMap theParameters,
      byte[] userSuppliedTag)
      throws InteractionClassNotPublished,
          InteractionParameterNotDefined,
          InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public MessageRetractionReturn sendInteraction(
      InteractionClassHandle theInteraction,
      ParameterHandleValueMap theParameters,
      byte[] userSuppliedTag,
      LogicalTime theTime)
      throws InvalidLogicalTime,
          InteractionClassNotPublished,
          InteractionParameterNotDefined,
          InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public void deleteObjectInstance(ObjectInstanceHandle objectHandle, byte[] userSuppliedTag)
      throws DeletePrivilegeNotHeld,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public MessageRetractionReturn deleteObjectInstance(
      ObjectInstanceHandle objectHandle, byte[] userSuppliedTag, LogicalTime theTime)
      throws InvalidLogicalTime,
          DeletePrivilegeNotHeld,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public void localDeleteObjectInstance(ObjectInstanceHandle objectHandle)
      throws OwnershipAcquisitionPending,
          FederateOwnsAttributes,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void requestAttributeValueUpdate(
      ObjectInstanceHandle theObject, AttributeHandleSet theAttributes, byte[] userSuppliedTag)
      throws AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void requestAttributeValueUpdate(
      ObjectClassHandle theClass, AttributeHandleSet theAttributes, byte[] userSuppliedTag)
      throws AttributeNotDefined,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void requestAttributeTransportationTypeChange(
      ObjectInstanceHandle theObject,
      AttributeHandleSet theAttributes,
      TransportationTypeHandle theType)
      throws AttributeAlreadyBeingChanged,
          AttributeNotOwned,
          AttributeNotDefined,
          ObjectInstanceNotKnown,
          InvalidTransportationType,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void queryAttributeTransportationType(
      ObjectInstanceHandle theObject, AttributeHandle theAttribute)
      throws AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void requestInteractionTransportationTypeChange(
      InteractionClassHandle theClass, TransportationTypeHandle theType)
      throws InteractionClassAlreadyBeingChanged,
          InteractionClassNotPublished,
          InteractionClassNotDefined,
          InvalidTransportationType,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void queryInteractionTransportationType(
      FederateHandle theFederate, InteractionClassHandle theInteraction)
      throws InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  ///////////////////////////////////
  // Ownership Management Services //
  ///////////////////////////////////
  @Override
  public void unconditionalAttributeOwnershipDivestiture(
      ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
      throws AttributeNotOwned,
          AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void negotiatedAttributeOwnershipDivestiture(
      ObjectInstanceHandle theObject, AttributeHandleSet theAttributes, byte[] userSuppliedTag)
      throws AttributeAlreadyBeingDivested,
          AttributeNotOwned,
          AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void confirmDivestiture(
      ObjectInstanceHandle theObject, AttributeHandleSet theAttributes, byte[] userSuppliedTag)
      throws NoAcquisitionPending,
          AttributeDivestitureWasNotRequested,
          AttributeNotOwned,
          AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void attributeOwnershipAcquisition(
      ObjectInstanceHandle theObject, AttributeHandleSet desiredAttributes, byte[] userSuppliedTag)
      throws AttributeNotPublished,
          ObjectClassNotPublished,
          FederateOwnsAttributes,
          AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void attributeOwnershipAcquisitionIfAvailable(
      ObjectInstanceHandle theObject, AttributeHandleSet desiredAttributes)
      throws AttributeAlreadyBeingAcquired,
          AttributeNotPublished,
          ObjectClassNotPublished,
          FederateOwnsAttributes,
          AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void attributeOwnershipReleaseDenied(
      ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
      throws AttributeNotOwned,
          AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public AttributeHandleSet attributeOwnershipDivestitureIfWanted(
      ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
      throws AttributeNotOwned,
          AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public void cancelNegotiatedAttributeOwnershipDivestiture(
      ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
      throws AttributeDivestitureWasNotRequested,
          AttributeNotOwned,
          AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void cancelAttributeOwnershipAcquisition(
      ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
      throws AttributeAcquisitionWasNotRequested,
          AttributeAlreadyOwned,
          AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void queryAttributeOwnership(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
      throws AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public boolean isAttributeOwnedByFederate(
      ObjectInstanceHandle theObject, AttributeHandle theAttribute)
      throws AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return false;
  }

  //////////////////////////////
  // Time Management Services //
  //////////////////////////////
  @Override
  public void enableTimeRegulation(LogicalTimeInterval theLookahead)
      throws InvalidLookahead,
          InTimeAdvancingState,
          RequestForTimeRegulationPending,
          TimeRegulationAlreadyEnabled,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void disableTimeRegulation()
      throws TimeRegulationIsNotEnabled,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void enableTimeConstrained()
      throws InTimeAdvancingState,
          RequestForTimeConstrainedPending,
          TimeConstrainedAlreadyEnabled,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void disableTimeConstrained()
      throws TimeConstrainedIsNotEnabled,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void timeAdvanceRequest(LogicalTime theTime)
      throws LogicalTimeAlreadyPassed,
          InvalidLogicalTime,
          InTimeAdvancingState,
          RequestForTimeRegulationPending,
          RequestForTimeConstrainedPending,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void timeAdvanceRequestAvailable(LogicalTime theTime)
      throws LogicalTimeAlreadyPassed,
          InvalidLogicalTime,
          InTimeAdvancingState,
          RequestForTimeRegulationPending,
          RequestForTimeConstrainedPending,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void nextMessageRequest(LogicalTime theTime)
      throws LogicalTimeAlreadyPassed,
          InvalidLogicalTime,
          InTimeAdvancingState,
          RequestForTimeRegulationPending,
          RequestForTimeConstrainedPending,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void nextMessageRequestAvailable(LogicalTime theTime)
      throws LogicalTimeAlreadyPassed,
          InvalidLogicalTime,
          InTimeAdvancingState,
          RequestForTimeRegulationPending,
          RequestForTimeConstrainedPending,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void flushQueueRequest(LogicalTime theTime)
      throws LogicalTimeAlreadyPassed,
          InvalidLogicalTime,
          InTimeAdvancingState,
          RequestForTimeRegulationPending,
          RequestForTimeConstrainedPending,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void enableAsynchronousDelivery()
      throws AsynchronousDeliveryAlreadyEnabled,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void disableAsynchronousDelivery()
      throws AsynchronousDeliveryAlreadyDisabled,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public TimeQueryReturn queryGALT()
      throws SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public LogicalTime queryLogicalTime()
      throws SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public TimeQueryReturn queryLITS()
      throws SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public void modifyLookahead(LogicalTimeInterval theLookahead)
      throws InvalidLookahead,
          InTimeAdvancingState,
          TimeRegulationIsNotEnabled,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public LogicalTimeInterval queryLookahead()
      throws TimeRegulationIsNotEnabled,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public void retract(MessageRetractionHandle theHandle)
      throws MessageCanNoLongerBeRetracted,
          InvalidMessageRetractionHandle,
          TimeRegulationIsNotEnabled,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void changeAttributeOrderType(
      ObjectInstanceHandle theObject, AttributeHandleSet theAttributes, OrderType theType)
      throws AttributeNotOwned,
          AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void changeInteractionOrderType(InteractionClassHandle theClass, OrderType theType)
      throws InteractionClassNotPublished,
          InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  //////////////////////////////////
  // Data Distribution Management //
  //////////////////////////////////
  @Override
  public RegionHandle createRegion(DimensionHandleSet dimensions)
      throws InvalidDimensionHandle,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public void commitRegionModifications(RegionHandleSet regions)
      throws RegionNotCreatedByThisFederate,
          InvalidRegion,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void deleteRegion(RegionHandle theRegion)
      throws RegionInUseForUpdateOrSubscription,
          RegionNotCreatedByThisFederate,
          InvalidRegion,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public ObjectInstanceHandle registerObjectInstanceWithRegions(
      ObjectClassHandle theClass, AttributeSetRegionSetPairList attributesAndRegions)
      throws InvalidRegionContext,
          RegionNotCreatedByThisFederate,
          InvalidRegion,
          AttributeNotPublished,
          ObjectClassNotPublished,
          AttributeNotDefined,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public ObjectInstanceHandle registerObjectInstanceWithRegions(
      ObjectClassHandle theClass,
      AttributeSetRegionSetPairList attributesAndRegions,
      String theObject)
      throws ObjectInstanceNameInUse,
          ObjectInstanceNameNotReserved,
          InvalidRegionContext,
          RegionNotCreatedByThisFederate,
          InvalidRegion,
          AttributeNotPublished,
          ObjectClassNotPublished,
          AttributeNotDefined,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public void associateRegionsForUpdates(
      ObjectInstanceHandle theObject, AttributeSetRegionSetPairList attributesAndRegions)
      throws InvalidRegionContext,
          RegionNotCreatedByThisFederate,
          InvalidRegion,
          AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void unassociateRegionsForUpdates(
      ObjectInstanceHandle theObject, AttributeSetRegionSetPairList attributesAndRegions)
      throws RegionNotCreatedByThisFederate,
          InvalidRegion,
          AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void subscribeObjectClassAttributesWithRegions(
      ObjectClassHandle theClass, AttributeSetRegionSetPairList attributesAndRegions)
      throws InvalidRegionContext,
          RegionNotCreatedByThisFederate,
          InvalidRegion,
          AttributeNotDefined,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void subscribeObjectClassAttributesWithRegions(
      ObjectClassHandle theClass,
      AttributeSetRegionSetPairList attributesAndRegions,
      String updateRateDesignator)
      throws InvalidRegionContext,
          RegionNotCreatedByThisFederate,
          InvalidRegion,
          AttributeNotDefined,
          ObjectClassNotDefined,
          InvalidUpdateRateDesignator,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void subscribeObjectClassAttributesPassivelyWithRegions(
      ObjectClassHandle theClass, AttributeSetRegionSetPairList attributesAndRegions)
      throws InvalidRegionContext,
          RegionNotCreatedByThisFederate,
          InvalidRegion,
          AttributeNotDefined,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void subscribeObjectClassAttributesPassivelyWithRegions(
      ObjectClassHandle theClass,
      AttributeSetRegionSetPairList attributesAndRegions,
      String updateRateDesignator)
      throws InvalidRegionContext,
          RegionNotCreatedByThisFederate,
          InvalidRegion,
          AttributeNotDefined,
          ObjectClassNotDefined,
          InvalidUpdateRateDesignator,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void unsubscribeObjectClassAttributesWithRegions(
      ObjectClassHandle theClass, AttributeSetRegionSetPairList attributesAndRegions)
      throws RegionNotCreatedByThisFederate,
          InvalidRegion,
          AttributeNotDefined,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void subscribeInteractionClassWithRegions(
      InteractionClassHandle theClass, RegionHandleSet regions)
      throws FederateServiceInvocationsAreBeingReportedViaMOM,
          InvalidRegionContext,
          RegionNotCreatedByThisFederate,
          InvalidRegion,
          InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void subscribeInteractionClassPassivelyWithRegions(
      InteractionClassHandle theClass, RegionHandleSet regions)
      throws FederateServiceInvocationsAreBeingReportedViaMOM,
          InvalidRegionContext,
          RegionNotCreatedByThisFederate,
          InvalidRegion,
          InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void unsubscribeInteractionClassWithRegions(
      InteractionClassHandle theClass, RegionHandleSet regions)
      throws RegionNotCreatedByThisFederate,
          InvalidRegion,
          InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void sendInteractionWithRegions(
      InteractionClassHandle theInteraction,
      ParameterHandleValueMap theParameters,
      RegionHandleSet regions,
      byte[] userSuppliedTag)
      throws InvalidRegionContext,
          RegionNotCreatedByThisFederate,
          InvalidRegion,
          InteractionClassNotPublished,
          InteractionParameterNotDefined,
          InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public MessageRetractionReturn sendInteractionWithRegions(
      InteractionClassHandle theInteraction,
      ParameterHandleValueMap theParameters,
      RegionHandleSet regions,
      byte[] userSuppliedTag,
      LogicalTime theTime)
      throws InvalidLogicalTime,
          InvalidRegionContext,
          RegionNotCreatedByThisFederate,
          InvalidRegion,
          InteractionClassNotPublished,
          InteractionParameterNotDefined,
          InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public void requestAttributeValueUpdateWithRegions(
      ObjectClassHandle theClass,
      AttributeSetRegionSetPairList attributesAndRegions,
      byte[] userSuppliedTag)
      throws InvalidRegionContext,
          RegionNotCreatedByThisFederate,
          InvalidRegion,
          AttributeNotDefined,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  //////////////////////////
  // RTI Support Services //
  //////////////////////////
  @Override
  public ResignAction getAutomaticResignDirective()
      throws FederateNotExecutionMember, NotConnected, RTIinternalError {
    return null;
  }

  @Override
  public void setAutomaticResignDirective(ResignAction resignAction)
      throws InvalidResignAction, FederateNotExecutionMember, NotConnected, RTIinternalError {}

  @Override
  public FederateHandle getFederateHandle(String theName)
      throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
    return null;
  }

  @Override
  public String getFederateName(FederateHandle theHandle)
      throws InvalidFederateHandle,
          FederateHandleNotKnown,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public ObjectClassHandle getObjectClassHandle(String theName)
      throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
    
    try {
      return clientContext.getObjectClassHandle(theName);
    } catch (NameNotFound e) {
      throw e;
    } catch (Exception e) {
      throw new RTIinternalError("Error looking up object class handle: " + e.getMessage(), e);
    }
  }

  @Override
  public String getObjectClassName(ObjectClassHandle theHandle)
      throws InvalidObjectClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    return null;
  }

  @Override
  public ObjectClassHandle getKnownObjectClassHandle(ObjectInstanceHandle theObject)
      throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
    return null;
  }

  @Override
  public ObjectInstanceHandle getObjectInstanceHandle(String theName)
      throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
    return null;
  }

  @Override
  public String getObjectInstanceName(ObjectInstanceHandle theHandle)
      throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
    try {
      if (theHandle == null) {
        throw new RTIinternalError("ObjectInstanceHandle cannot be null");
      }
      
      // Query server for the object instance name
      String name = clientContext.getObjectInstanceName(theHandle);
      if (name != null) {
        System.out.println("[LiteRtiAmbassador] Retrieved object instance name: " + name + " for handle: " + theHandle);
        return name;
      } else {
        throw new ObjectInstanceNotKnown("Object instance with handle " + theHandle + " not found");
      }
    } catch (ObjectInstanceNotKnown e) {
      throw e;
    } catch (Exception e) {
      throw new RTIinternalError("Error getting object instance name: " + e.getMessage(), e);
    }
  }

  @Override
  public AttributeHandle getAttributeHandle(ObjectClassHandle whichClass, String theName)
      throws NameNotFound,
          InvalidObjectClassHandle,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    try {
      return clientContext.getAttributeHandle(whichClass, theName);
    } catch (NameNotFound e) {
      throw e;
    } catch (Exception e) {
      throw new RTIinternalError("Error looking up attribute handle: " + e.getMessage(), e);
    }
  }

  @Override
  public String getAttributeName(ObjectClassHandle whichClass, AttributeHandle theHandle)
      throws AttributeNotDefined,
          InvalidAttributeHandle,
          InvalidObjectClassHandle,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public double getUpdateRateValue(String updateRateDesignator)
      throws InvalidUpdateRateDesignator,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return 0.0;
  }

  @Override
  public double getUpdateRateValueForAttribute(
      ObjectInstanceHandle theObject, AttributeHandle theAttribute)
      throws ObjectInstanceNotKnown,
          AttributeNotDefined,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return 0.0;
  }

  @Override
  public InteractionClassHandle getInteractionClassHandle(String theName)
      throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {

    // map string of class to its numeric value

    // 1 check joined
    // 2 FOM lookup: Queries the Federated Object Model (FOM) to find the interaction class by name
    // 3 Return handle: Returns the handle wrapped in a version-specific type (or as int for HLA
    // 1.3)

    System.out.println("HIERrrrrrrrrrrrrr 1");

    // int handle = socketClient.getInteractionClassHandle(theName);
    int handle = -1;

    // Check for error
    if (handle == -1) {
      throw new NameNotFound("Interaction class not found: " + theName);
    }
    if (true) {
      throw new UnsupportedOperationException("Current method is currently unsupported 5");
    }

    // Wrap the integer handle in an InteractionClassHandleImpl and return it
    return new nl.literti.impl.InteractionClassHandleImpl(handle);
  }

  @Override
  public String getInteractionClassName(InteractionClassHandle theHandle)
      throws InvalidInteractionClassHandle,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    if (true) {
      throw new UnsupportedOperationException("Current method is currently unsupported 6");
    }

    if (theHandle instanceof nl.literti.impl.InteractionClassHandleImpl) {
      int handle = ((nl.literti.impl.InteractionClassHandleImpl) theHandle).getHandle();
      // For now, we can't reverse-lookup the name from handle without server support
      // This would require adding GET_INTERACTION_CLASS_NAME message type
      return null;
    }
    return null;
  }

  @Override
  public ParameterHandle getParameterHandle(InteractionClassHandle whichClass, String theName)
      throws NameNotFound,
          InvalidInteractionClassHandle,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    System.out.println("HIERrrrrrrrrrrrrr1 get param handle");
    // int handle = socketClient.getParameterHandle(theName);
    int handle = -1;
    if (handle == -1) {
      throw new NameNotFound("Parameter not found: " + theName);
    }
    return new nl.literti.impl.ParameterHandleImpl(handle);
  }

  @Override
  public String getParameterName(InteractionClassHandle whichClass, ParameterHandle theHandle)
      throws InteractionParameterNotDefined,
          InvalidParameterHandle,
          InvalidInteractionClassHandle,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    if (true) {
      throw new UnsupportedOperationException("Current method is currently unsupported 7");
    }
    if (theHandle instanceof nl.literti.impl.ParameterHandleImpl) {
      int handle = ((nl.literti.impl.ParameterHandleImpl) theHandle).getHandle();
      // For now, we can't reverse-lookup the name from handle without server support
      // This would require adding GET_PARAMETER_NAME message type
      return null;
    }
    return null;
  }

  @Override
  public OrderType getOrderType(String theName)
      throws InvalidOrderName, FederateNotExecutionMember, NotConnected, RTIinternalError {
    return null;
  }

  @Override
  public String getOrderName(OrderType theType)
      throws InvalidOrderType, FederateNotExecutionMember, NotConnected, RTIinternalError {
    return null;
  }

  @Override
  public TransportationTypeHandle getTransportationTypeHandle(String theName)
      throws InvalidTransportationName, FederateNotExecutionMember, NotConnected, RTIinternalError {
    return null;
  }

  @Override
  public String getTransportationTypeName(TransportationTypeHandle theHandle)
      throws InvalidTransportationType, FederateNotExecutionMember, NotConnected, RTIinternalError {
    return null;
  }

  @Override
  public DimensionHandleSet getAvailableDimensionsForClassAttribute(
      ObjectClassHandle whichClass, AttributeHandle theHandle)
      throws AttributeNotDefined,
          InvalidAttributeHandle,
          InvalidObjectClassHandle,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public DimensionHandleSet getAvailableDimensionsForInteractionClass(
      InteractionClassHandle theHandle)
      throws InvalidInteractionClassHandle,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public DimensionHandle getDimensionHandle(String theName)
      throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
    return null;
  }

  @Override
  public String getDimensionName(DimensionHandle theHandle)
      throws InvalidDimensionHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    return null;
  }

  @Override
  public long getDimensionUpperBound(DimensionHandle theHandle)
      throws InvalidDimensionHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    return 0;
  }

  @Override
  public DimensionHandleSet getDimensionHandleSet(RegionHandle region)
      throws InvalidRegion,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public RangeBounds getRangeBounds(RegionHandle region, DimensionHandle dimension)
      throws RegionDoesNotContainSpecifiedDimension,
          InvalidRegion,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    return null;
  }

  @Override
  public void setRangeBounds(RegionHandle region, DimensionHandle dimension, RangeBounds bounds)
      throws InvalidRangeBound,
          RegionDoesNotContainSpecifiedDimension,
          RegionNotCreatedByThisFederate,
          InvalidRegion,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public long normalizeFederateHandle(FederateHandle federateHandle)
      throws InvalidFederateHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    return 0;
  }

  @Override
  public long normalizeServiceGroup(ServiceGroup group)
      throws InvalidServiceGroup, FederateNotExecutionMember, NotConnected, RTIinternalError {
    return 0;
  }

  @Override
  public void enableObjectClassRelevanceAdvisorySwitch()
      throws ObjectClassRelevanceAdvisorySwitchIsOn,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void disableObjectClassRelevanceAdvisorySwitch()
      throws ObjectClassRelevanceAdvisorySwitchIsOff,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void enableAttributeRelevanceAdvisorySwitch()
      throws AttributeRelevanceAdvisorySwitchIsOn,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void disableAttributeRelevanceAdvisorySwitch()
      throws AttributeRelevanceAdvisorySwitchIsOff,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void enableAttributeScopeAdvisorySwitch()
      throws AttributeScopeAdvisorySwitchIsOn,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void disableAttributeScopeAdvisorySwitch()
      throws AttributeScopeAdvisorySwitchIsOff,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void enableInteractionRelevanceAdvisorySwitch()
      throws InteractionRelevanceAdvisorySwitchIsOn,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public void disableInteractionRelevanceAdvisorySwitch()
      throws InteractionRelevanceAdvisorySwitchIsOff,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {}

  @Override
  public boolean evokeCallback(double approximateMinimumTimeInSeconds)
      throws CallNotAllowedFromWithinCallback, RTIinternalError {
    return false;
  }

  @Override
  public boolean evokeMultipleCallbacks(
      double approximateMinimumTimeInSeconds, double approximateMaximumTimeInSeconds)
      throws CallNotAllowedFromWithinCallback, RTIinternalError {
    return false;
  }

  @Override
  public void enableCallbacks() throws SaveInProgress, RestoreInProgress, RTIinternalError {}

  @Override
  public void disableCallbacks() throws SaveInProgress, RestoreInProgress, RTIinternalError {}

  @Override
  public AttributeHandleFactory getAttributeHandleFactory()
      throws FederateNotExecutionMember, NotConnected {
    return null;
  }

  @Override
  public AttributeHandleSetFactory getAttributeHandleSetFactory()
      throws FederateNotExecutionMember, NotConnected {
    return new AttributeHandleSetFactoryImpl();
  }

  @Override
  public AttributeHandleValueMapFactory getAttributeHandleValueMapFactory()
      throws FederateNotExecutionMember, NotConnected {
    return new AttributeHandleValueMapFactoryImpl();
  }

  @Override
  public AttributeSetRegionSetPairListFactory getAttributeSetRegionSetPairListFactory()
      throws FederateNotExecutionMember, NotConnected {
    return null;
  }

  @Override
  public DimensionHandleFactory getDimensionHandleFactory()
      throws FederateNotExecutionMember, NotConnected {
    return null;
  }

  @Override
  public DimensionHandleSetFactory getDimensionHandleSetFactory()
      throws FederateNotExecutionMember, NotConnected {
    return null;
  }

  @Override
  public FederateHandleFactory getFederateHandleFactory()
      throws FederateNotExecutionMember, NotConnected {
    return null;
  }

  @Override
  public FederateHandleSetFactory getFederateHandleSetFactory()
      throws FederateNotExecutionMember, NotConnected {
    return null;
  }

  @Override
  public InteractionClassHandleFactory getInteractionClassHandleFactory()
      throws FederateNotExecutionMember, NotConnected {

    System.out.println("HIERrrrrrrrrrrrrr 2");

    if (true) {
      throw new UnsupportedOperationException("Current method is currently unsupported 8");
    }
    return null;
  }

  @Override
  public ObjectClassHandleFactory getObjectClassHandleFactory()
      throws FederateNotExecutionMember, NotConnected {
    return null;
  }

  @Override
  public ObjectInstanceHandleFactory getObjectInstanceHandleFactory()
      throws FederateNotExecutionMember, NotConnected {
    return null;
  }

  @Override
  public ParameterHandleFactory getParameterHandleFactory()
      throws FederateNotExecutionMember, NotConnected {
    return null;
  }

  @Override
  public ParameterHandleValueMapFactory getParameterHandleValueMapFactory()
      throws FederateNotExecutionMember, NotConnected {
    return null;
  }

  @Override
  public RegionHandleSetFactory getRegionHandleSetFactory()
      throws FederateNotExecutionMember, NotConnected {
    return null;
  }

  @Override
  public TransportationTypeHandleFactory getTransportationTypeHandleFactory()
      throws FederateNotExecutionMember, NotConnected {
    return null;
  }

  @Override
  public String getHLAversion() {
    return null;
  }

  @Override
  public LogicalTimeFactory getTimeFactory() throws FederateNotExecutionMember, NotConnected {
    return null;
  }

}
