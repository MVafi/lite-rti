package nl.literti.client;

import java.net.URL;
import java.util.Arrays;
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
import nl.literti.utils.ParameterHandleValueMapFactoryImpl;

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
    throw new UnsupportedOperationException("Currently not supported call : connect(FederateAmbassador, CallbackModel, String)");
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

  @Override
  public void disconnect()
      throws FederateIsExecutionMember, CallNotAllowedFromWithinCallback, RTIinternalError {
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
    throw new UnsupportedOperationException("Currently not supported call : createFederationExecution(String, URL[], URL, String)");
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
    throw new UnsupportedOperationException("Currently not supported call : createFederationExecution(String, URL[], String)");
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
    URL[] combinedModules = Arrays.copyOf(fomModules, fomModules.length + 1);
    combinedModules[fomModules.length] = mimModule;
    this.createFederationExecution(federationExecutionName, combinedModules);
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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : createFederationExecution(String, URL)");
          }

  @Override
  public void destroyFederationExecution(String federationExecutionName)
      throws FederatesCurrentlyJoined,
          FederationExecutionDoesNotExist,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : destroyFederationExecution(String)");
          }

  @Override
  public void listFederationExecutions() throws NotConnected, RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : listFederationExecutions()");
  }

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
      clientContext.joinFederationExecution(
          federateType, federationExecutionName, additionalFomModules);
      System.out.println(
          "[LiteRtiAmbassador] Federation joined: "
              + federationExecutionName
              + " as federate type: "
              + federateType);

    } catch (Exception e) {
      throw new RTIinternalError(e.getMessage());
    }
    // TODO this should return a FederateHandle
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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : resignFederationExecution(ResignAction)");
          }

  @Override
  public void registerFederationSynchronizationPoint(
      String synchronizationPointLabel, byte[] userSuppliedTag)
      throws SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : registerFederationSynchronizationPoint(String, byte[])");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : registerFederationSynchronizationPoint(String, byte[], FederateHandleSet)");
          }

  @Override
  public void synchronizationPointAchieved(String synchronizationPointLabel)
      throws SynchronizationPointLabelNotAnnounced,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : synchronizationPointAchieved(String)");
          }

  @Override
  public void synchronizationPointAchieved(
      String synchronizationPointLabel, boolean successIndicator)
      throws SynchronizationPointLabelNotAnnounced,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : synchronizationPointAchieved(String, boolean)");
          }

  @Override
  public void requestFederationSave(String label)
      throws SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : requestFederationSave(String)");
          }

  @Override
  public void requestFederationSave(String label, LogicalTime theTime)
      throws LogicalTimeAlreadyPassed,
          InvalidLogicalTime,
          FederateUnableToUseTime,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : requestFederationSave(String, LogicalTime)");
          }

  @Override
  public void federateSaveBegun()
      throws SaveNotInitiated,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : federateSaveBegun()");
          }

  @Override
  public void federateSaveComplete()
      throws FederateHasNotBegunSave,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : federateSaveComplete()");
          }

  @Override
  public void federateSaveNotComplete()
      throws FederateHasNotBegunSave,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : federateSaveNotComplete()");
          }

  @Override
  public void abortFederationSave()
      throws SaveNotInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException("Currently not supported call : abortFederationSave()");
      }

  @Override
  public void queryFederationSaveStatus()
      throws RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException("Currently not supported call : queryFederationSaveStatus()");
      }

  @Override
  public void requestFederationRestore(String label)
      throws SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : requestFederationRestore(String)");
          }

  @Override
  public void federateRestoreComplete()
      throws RestoreNotRequested,
          SaveInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : federateRestoreComplete()");
          }

  @Override
  public void federateRestoreNotComplete()
      throws RestoreNotRequested,
          SaveInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : federateRestoreNotComplete()");
          }

  @Override
  public void abortFederationRestore()
      throws RestoreNotInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException("Currently not supported call : abortFederationRestore()");
      }

  @Override
  public void queryFederationRestoreStatus()
      throws SaveInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException("Currently not supported call : queryFederationRestoreStatus()");
      }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : unpublishObjectClass(ObjectClassHandle)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : unpublishObjectClassAttributes(ObjectClassHandle, AttributeHandleSet)");
          }

  @Override
  public void publishInteractionClass(InteractionClassHandle theInteraction)
      throws InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    if (theInteraction == null) {
      throw new RTIinternalError("InteractionClassHandle cannot be null");
    }
    clientContext.publishInteractionClassAttributes(theInteraction);
    }

  @Override
  public void unpublishInteractionClass(InteractionClassHandle theInteraction)
      throws InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : unpublishInteractionClass(InteractionClassHandle)");
          }

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
    // Validate no null attribute handles if set is provided, otherwise attributeList should be null
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
    throw new UnsupportedOperationException("Currently not supported call : subscribeObjectClassAttributes(ObjectClassHandle, AttributeHandleSet, String)");
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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : subscribeObjectClassAttributesPassively(ObjectClassHandle, AttributeHandleSet)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : subscribeObjectClassAttributesPassively(ObjectClassHandle, AttributeHandleSet, String)");
          }

  @Override
  public void unsubscribeObjectClass(ObjectClassHandle theClass)
      throws ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : unsubscribeObjectClass(ObjectClassHandle)");
          }

  @Override
  public void unsubscribeObjectClassAttributes(
      ObjectClassHandle theClass, AttributeHandleSet attributeList)
      throws AttributeNotDefined,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : unsubscribeObjectClassAttributes(ObjectClassHandle, AttributeHandleSet)");
          }

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
    if (theClass == null) {
      throw new RTIinternalError("ObjectClassHandle cannot be null");
    }
    clientContext.subscribeInteractionClass(theClass);
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
    throw new UnsupportedOperationException("Currently not supported call : subscribeInteractionClassPassively(InteractionClassHandle)");
  }

  @Override
  public void unsubscribeInteractionClass(InteractionClassHandle theClass)
      throws InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : unsubscribeInteractionClass(InteractionClassHandle)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : reserveObjectInstanceName(String)");
          }

  @Override
  public void releaseObjectInstanceName(String theObjectInstanceName)
      throws ObjectInstanceNameNotReserved,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : releaseObjectInstanceName(String)");
          }

  @Override
  public void reserveMultipleObjectInstanceName(Set<String> theObjectNames)
      throws IllegalName,
          NameSetWasEmpty,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : reserveMultipleObjectInstanceName(Set)");
          }

  @Override
  public void releaseMultipleObjectInstanceName(Set<String> theObjectNames)
      throws ObjectInstanceNameNotReserved,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : releaseMultipleObjectInstanceName(Set)");
          }

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
    throw new UnsupportedOperationException("Currently not supported call : registerObjectInstance(ObjectClassHandle, String)");
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
    throw new UnsupportedOperationException("Currently not supported call : updateAttributeValues(ObjectInstanceHandle, AttributeHandleValueMap, byte[], LogicalTime)");
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
          RTIinternalError {
            clientContext.sendInteraction(theInteraction, theParameters, userSuppliedTag);
          }

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
    throw new UnsupportedOperationException("Currently not supported call : sendInteraction(InteractionClassHandle, ParameterHandleValueMap, byte[], LogicalTime)");
  }

  @Override
  public void deleteObjectInstance(ObjectInstanceHandle objectHandle, byte[] userSuppliedTag)
      throws DeletePrivilegeNotHeld,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : deleteObjectInstance(ObjectInstanceHandle, byte[])");
          }

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
    throw new UnsupportedOperationException("Currently not supported call : deleteObjectInstance(ObjectInstanceHandle, byte[], LogicalTime)");
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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : localDeleteObjectInstance(ObjectInstanceHandle)");
          }

  @Override
  public void requestAttributeValueUpdate(
      ObjectInstanceHandle theObject, AttributeHandleSet theAttributes, byte[] userSuppliedTag)
      throws AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : requestAttributeValueUpdate(ObjectInstanceHandle, AttributeHandleSet, byte[])");
          }

  @Override
  public void requestAttributeValueUpdate(
      ObjectClassHandle theClass, AttributeHandleSet theAttributes, byte[] userSuppliedTag)
      throws AttributeNotDefined,
          ObjectClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : requestAttributeValueUpdate(ObjectClassHandle, AttributeHandleSet, byte[])");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : requestAttributeTransportationTypeChange(ObjectInstanceHandle, AttributeHandleSet, TransportationTypeHandle)");
          }

  @Override
  public void queryAttributeTransportationType(
      ObjectInstanceHandle theObject, AttributeHandle theAttribute)
      throws AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : queryAttributeTransportationType(ObjectInstanceHandle, AttributeHandle)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : requestInteractionTransportationTypeChange(InteractionClassHandle, TransportationTypeHandle)");
          }

  @Override
  public void queryInteractionTransportationType(
      FederateHandle theFederate, InteractionClassHandle theInteraction)
      throws InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : queryInteractionTransportationType(FederateHandle, InteractionClassHandle)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : unconditionalAttributeOwnershipDivestiture(ObjectInstanceHandle, AttributeHandleSet)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : negotiatedAttributeOwnershipDivestiture(ObjectInstanceHandle, AttributeHandleSet, byte[])");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : confirmDivestiture(ObjectInstanceHandle, AttributeHandleSet, byte[])");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : attributeOwnershipAcquisition(ObjectInstanceHandle, AttributeHandleSet, byte[])");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : attributeOwnershipAcquisitionIfAvailable(ObjectInstanceHandle, AttributeHandleSet)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : attributeOwnershipReleaseDenied(ObjectInstanceHandle, AttributeHandleSet)");
          }

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
    throw new UnsupportedOperationException("Currently not supported call : attributeOwnershipDivestitureIfWanted(ObjectInstanceHandle, AttributeHandleSet)");
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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : cancelNegotiatedAttributeOwnershipDivestiture(ObjectInstanceHandle, AttributeHandleSet)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : cancelAttributeOwnershipAcquisition(ObjectInstanceHandle, AttributeHandleSet)");
          }

  @Override
  public void queryAttributeOwnership(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
      throws AttributeNotDefined,
          ObjectInstanceNotKnown,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : queryAttributeOwnership(ObjectInstanceHandle, AttributeHandle)");
          }

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
    throw new UnsupportedOperationException("Currently not supported call : isAttributeOwnedByFederate(ObjectInstanceHandle, AttributeHandle)");
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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : enableTimeRegulation(LogicalTimeInterval)");
          }

  @Override
  public void disableTimeRegulation()
      throws TimeRegulationIsNotEnabled,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : disableTimeRegulation()");
          }

  @Override
  public void enableTimeConstrained()
      throws InTimeAdvancingState,
          RequestForTimeConstrainedPending,
          TimeConstrainedAlreadyEnabled,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : enableTimeConstrained()");
          }

  @Override
  public void disableTimeConstrained()
      throws TimeConstrainedIsNotEnabled,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : disableTimeConstrained()");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : timeAdvanceRequest(LogicalTime)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : timeAdvanceRequestAvailable(LogicalTime)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : nextMessageRequest(LogicalTime)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : nextMessageRequestAvailable(LogicalTime)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : flushQueueRequest(LogicalTime)");
          }

  @Override
  public void enableAsynchronousDelivery()
      throws AsynchronousDeliveryAlreadyEnabled,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : enableAsynchronousDelivery()");
          }

  @Override
  public void disableAsynchronousDelivery()
      throws AsynchronousDeliveryAlreadyDisabled,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : disableAsynchronousDelivery()");
          }

  @Override
  public TimeQueryReturn queryGALT()
      throws SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : queryGALT()");
  }

  @Override
  public LogicalTime queryLogicalTime()
      throws SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : queryLogicalTime()");
  }

  @Override
  public TimeQueryReturn queryLITS()
      throws SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : queryLITS()");
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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : modifyLookahead(LogicalTimeInterval)");
          }

  @Override
  public LogicalTimeInterval queryLookahead()
      throws TimeRegulationIsNotEnabled,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : queryLookahead()");
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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : retract(MessageRetractionHandle)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : changeAttributeOrderType(ObjectInstanceHandle, AttributeHandleSet, OrderType)");
          }

  @Override
  public void changeInteractionOrderType(InteractionClassHandle theClass, OrderType theType)
      throws InteractionClassNotPublished,
          InteractionClassNotDefined,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : changeInteractionOrderType(InteractionClassHandle, OrderType)");
          }

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
    throw new UnsupportedOperationException("Currently not supported call : createRegion(DimensionHandleSet)");
  }

  @Override
  public void commitRegionModifications(RegionHandleSet regions)
      throws RegionNotCreatedByThisFederate,
          InvalidRegion,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : commitRegionModifications(RegionHandleSet)");
          }

  @Override
  public void deleteRegion(RegionHandle theRegion)
      throws RegionInUseForUpdateOrSubscription,
          RegionNotCreatedByThisFederate,
          InvalidRegion,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : deleteRegion(RegionHandle)");
          }

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
    throw new UnsupportedOperationException("Currently not supported call : registerObjectInstanceWithRegions(ObjectClassHandle, AttributeSetRegionSetPairList)");
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
    throw new UnsupportedOperationException("Currently not supported call : registerObjectInstanceWithRegions(ObjectClassHandle, AttributeSetRegionSetPairList, String)");
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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : associateRegionsForUpdates(ObjectInstanceHandle, AttributeSetRegionSetPairList)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : unassociateRegionsForUpdates(ObjectInstanceHandle, AttributeSetRegionSetPairList)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : subscribeObjectClassAttributesWithRegions(ObjectClassHandle, AttributeSetRegionSetPairList)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : subscribeObjectClassAttributesWithRegions(ObjectClassHandle, AttributeSetRegionSetPairList, String)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : subscribeObjectClassAttributesPassivelyWithRegions(ObjectClassHandle, AttributeSetRegionSetPairList)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : subscribeObjectClassAttributesPassivelyWithRegions(ObjectClassHandle, AttributeSetRegionSetPairList, String)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : unsubscribeObjectClassAttributesWithRegions(ObjectClassHandle, AttributeSetRegionSetPairList)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : subscribeInteractionClassWithRegions(InteractionClassHandle, RegionHandleSet)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : subscribeInteractionClassPassivelyWithRegions(InteractionClassHandle, RegionHandleSet)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : unsubscribeInteractionClassWithRegions(InteractionClassHandle, RegionHandleSet)");
          }

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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : sendInteractionWithRegions(InteractionClassHandle, ParameterHandleValueMap, RegionHandleSet, byte[])");
          }

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
    throw new UnsupportedOperationException("Currently not supported call : sendInteractionWithRegions(InteractionClassHandle, ParameterHandleValueMap, RegionHandleSet, byte[], LogicalTime)");
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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : requestAttributeValueUpdateWithRegions(ObjectClassHandle, AttributeSetRegionSetPairList, byte[])");
          }

  //////////////////////////
  // RTI Support Services //
  //////////////////////////
  @Override
  public ResignAction getAutomaticResignDirective()
      throws FederateNotExecutionMember, NotConnected, RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getAutomaticResignDirective()");
  }

  @Override
  public void setAutomaticResignDirective(ResignAction resignAction)
      throws InvalidResignAction, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException("Currently not supported call : setAutomaticResignDirective(ResignAction)");
      }

  @Override
  public FederateHandle getFederateHandle(String theName)
      throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getFederateHandle(String)");
  }

  @Override
  public String getFederateName(FederateHandle theHandle)
      throws InvalidFederateHandle,
          FederateHandleNotKnown,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getFederateName(FederateHandle)");
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
    throw new UnsupportedOperationException("Currently not supported call : getObjectClassName(ObjectClassHandle)");
  }

  @Override
  public ObjectClassHandle getKnownObjectClassHandle(ObjectInstanceHandle theObject)
      throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getKnownObjectClassHandle(ObjectInstanceHandle)");
  }

  @Override
  public ObjectInstanceHandle getObjectInstanceHandle(String theName)
      throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getObjectInstanceHandle(String)");
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
        System.out.println(
            "[LiteRtiAmbassador] Retrieved object instance name: "
                + name
                + " for handle: "
                + theHandle);
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
    throw new UnsupportedOperationException("Currently not supported call : getAttributeName(ObjectClassHandle, AttributeHandle)");
  }

  @Override
  public double getUpdateRateValue(String updateRateDesignator)
      throws InvalidUpdateRateDesignator,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getUpdateRateValue(String)");
  }

  @Override
  public double getUpdateRateValueForAttribute(
      ObjectInstanceHandle theObject, AttributeHandle theAttribute)
      throws ObjectInstanceNotKnown,
          AttributeNotDefined,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getUpdateRateValueForAttribute(ObjectInstanceHandle, AttributeHandle)");
  }

  @Override
  public InteractionClassHandle getInteractionClassHandle(String theName) throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {

    try {
      return clientContext.getInteractionClassHandle(theName);
    } catch (NameNotFound e) {
      throw e;
    } catch (Exception e) {
      throw new RTIinternalError("Error looking up interaction class handle: " + e.getMessage(), e);
    }
  }

  @Override
  public String getInteractionClassName(InteractionClassHandle theHandle)
      throws InvalidInteractionClassHandle,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getInteractionClassName(InteractionClassHandle)");
  }

  @Override
  public ParameterHandle getParameterHandle(InteractionClassHandle whichClass, String theName)
      throws NameNotFound,
          InvalidInteractionClassHandle,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    try {
      return clientContext.getParameterHandle(whichClass, theName);
    } catch (NameNotFound e) {
      throw e;
    } catch (Exception e) {
      throw new RTIinternalError("Error looking up parameter handle: " + e.getMessage(), e);
    }
  }

  @Override
  public String getParameterName(InteractionClassHandle whichClass, ParameterHandle theHandle)
      throws InteractionParameterNotDefined,
          InvalidParameterHandle,
          InvalidInteractionClassHandle,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getParameterName(InteractionClassHandle, ParameterHandle)");
  }

  @Override
  public OrderType getOrderType(String theName)
      throws InvalidOrderName, FederateNotExecutionMember, NotConnected, RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getOrderType(String)");
  }

  @Override
  public String getOrderName(OrderType theType)
      throws InvalidOrderType, FederateNotExecutionMember, NotConnected, RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getOrderName(OrderType)");
  }

  @Override
  public TransportationTypeHandle getTransportationTypeHandle(String theName)
      throws InvalidTransportationName, FederateNotExecutionMember, NotConnected, RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getTransportationTypeHandle(String)");
  }

  @Override
  public String getTransportationTypeName(TransportationTypeHandle theHandle)
      throws InvalidTransportationType, FederateNotExecutionMember, NotConnected, RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getTransportationTypeName(TransportationTypeHandle)");
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
    throw new UnsupportedOperationException("Currently not supported call : getAvailableDimensionsForClassAttribute(ObjectClassHandle, AttributeHandle)");
  }

  @Override
  public DimensionHandleSet getAvailableDimensionsForInteractionClass(
      InteractionClassHandle theHandle)
      throws InvalidInteractionClassHandle,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getAvailableDimensionsForInteractionClass(InteractionClassHandle)");
  }

  @Override
  public DimensionHandle getDimensionHandle(String theName)
      throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getDimensionHandle(String)");
  }

  @Override
  public String getDimensionName(DimensionHandle theHandle)
      throws InvalidDimensionHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getDimensionName(DimensionHandle)");
  }

  @Override
  public long getDimensionUpperBound(DimensionHandle theHandle)
      throws InvalidDimensionHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getDimensionUpperBound(DimensionHandle)");
  }

  @Override
  public DimensionHandleSet getDimensionHandleSet(RegionHandle region)
      throws InvalidRegion,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : getDimensionHandleSet(RegionHandle)");
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
    throw new UnsupportedOperationException("Currently not supported call : getRangeBounds(RegionHandle, DimensionHandle)");
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
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : setRangeBounds(RegionHandle, DimensionHandle, RangeBounds)");
          }

  @Override
  public long normalizeFederateHandle(FederateHandle federateHandle)
      throws InvalidFederateHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : normalizeFederateHandle(FederateHandle)");
  }

  @Override
  public long normalizeServiceGroup(ServiceGroup group)
      throws InvalidServiceGroup, FederateNotExecutionMember, NotConnected, RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : normalizeServiceGroup(ServiceGroup)");
  }

  @Override
  public void enableObjectClassRelevanceAdvisorySwitch()
      throws ObjectClassRelevanceAdvisorySwitchIsOn,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : enableObjectClassRelevanceAdvisorySwitch()");
          }

  @Override
  public void disableObjectClassRelevanceAdvisorySwitch()
      throws ObjectClassRelevanceAdvisorySwitchIsOff,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : disableObjectClassRelevanceAdvisorySwitch()");
          }

  @Override
  public void enableAttributeRelevanceAdvisorySwitch()
      throws AttributeRelevanceAdvisorySwitchIsOn,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : enableAttributeRelevanceAdvisorySwitch()");
          }

  @Override
  public void disableAttributeRelevanceAdvisorySwitch()
      throws AttributeRelevanceAdvisorySwitchIsOff,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : disableAttributeRelevanceAdvisorySwitch()");
          }

  @Override
  public void enableAttributeScopeAdvisorySwitch()
      throws AttributeScopeAdvisorySwitchIsOn,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : enableAttributeScopeAdvisorySwitch()");
          }

  @Override
  public void disableAttributeScopeAdvisorySwitch()
      throws AttributeScopeAdvisorySwitchIsOff,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : disableAttributeScopeAdvisorySwitch()");
          }

  @Override
  public void enableInteractionRelevanceAdvisorySwitch()
      throws InteractionRelevanceAdvisorySwitchIsOn,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : enableInteractionRelevanceAdvisorySwitch()");
          }

  @Override
  public void disableInteractionRelevanceAdvisorySwitch()
      throws InteractionRelevanceAdvisorySwitchIsOff,
          SaveInProgress,
          RestoreInProgress,
          FederateNotExecutionMember,
          NotConnected,
          RTIinternalError {
            throw new UnsupportedOperationException("Currently not supported call : disableInteractionRelevanceAdvisorySwitch()");
          }

  @Override
  public boolean evokeCallback(double approximateMinimumTimeInSeconds)
      throws CallNotAllowedFromWithinCallback, RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : evokeCallback(double)");
  }

  @Override
  public boolean evokeMultipleCallbacks(
      double approximateMinimumTimeInSeconds, double approximateMaximumTimeInSeconds)
      throws CallNotAllowedFromWithinCallback, RTIinternalError {
    throw new UnsupportedOperationException("Currently not supported call : evokeMultipleCallbacks(double, double)");
  }

  @Override
  public void enableCallbacks() throws SaveInProgress, RestoreInProgress, RTIinternalError {
    // Allow the client to do callbacks on the FederateAmbassador
    clientContext.enableCallbacks();
  }

  @Override
  public void disableCallbacks() throws SaveInProgress, RestoreInProgress, RTIinternalError {
    // Do not allow the client to do callbacks on the FederateAmbassador, instead store the callbacks in a queue for later
    clientContext.disableCallbacks();
  }

  @Override
  public AttributeHandleFactory getAttributeHandleFactory()
      throws FederateNotExecutionMember, NotConnected {
    throw new UnsupportedOperationException("Currently not supported call : getAttributeHandleFactory()");
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
    throw new UnsupportedOperationException("Currently not supported call : getAttributeSetRegionSetPairListFactory()");
  }

  @Override
  public DimensionHandleFactory getDimensionHandleFactory()
      throws FederateNotExecutionMember, NotConnected {
    throw new UnsupportedOperationException("Currently not supported call : getDimensionHandleFactory()");
  }

  @Override
  public DimensionHandleSetFactory getDimensionHandleSetFactory()
      throws FederateNotExecutionMember, NotConnected {
    throw new UnsupportedOperationException("Currently not supported call : getDimensionHandleSetFactory()");
  }

  @Override
  public FederateHandleFactory getFederateHandleFactory()
      throws FederateNotExecutionMember, NotConnected {
    throw new UnsupportedOperationException("Currently not supported call : getFederateHandleFactory()");
  }

  @Override
  public FederateHandleSetFactory getFederateHandleSetFactory()
      throws FederateNotExecutionMember, NotConnected {
    throw new UnsupportedOperationException("Currently not supported call : getFederateHandleSetFactory()");
  }

  @Override
  public InteractionClassHandleFactory getInteractionClassHandleFactory()
      throws FederateNotExecutionMember, NotConnected {
    throw new UnsupportedOperationException("Currently not supported call : getInteractionClassHandleFactory()");
  }

  @Override
  public ObjectClassHandleFactory getObjectClassHandleFactory()
      throws FederateNotExecutionMember, NotConnected {
    throw new UnsupportedOperationException("Currently not supported call : getObjectClassHandleFactory()");
  }

  @Override
  public ObjectInstanceHandleFactory getObjectInstanceHandleFactory()
      throws FederateNotExecutionMember, NotConnected {
    throw new UnsupportedOperationException("Currently not supported call : getObjectInstanceHandleFactory()");
  }

  @Override
  public ParameterHandleFactory getParameterHandleFactory()
      throws FederateNotExecutionMember, NotConnected {
    throw new UnsupportedOperationException("Currently not supported call : getParameterHandleFactory()");
  }

  @Override
  public ParameterHandleValueMapFactory getParameterHandleValueMapFactory()
      throws FederateNotExecutionMember, NotConnected {
    return new ParameterHandleValueMapFactoryImpl();
  }

  @Override
  public RegionHandleSetFactory getRegionHandleSetFactory()
      throws FederateNotExecutionMember, NotConnected {
    throw new UnsupportedOperationException("Currently not supported call : getRegionHandleSetFactory()");
  }

  @Override
  public TransportationTypeHandleFactory getTransportationTypeHandleFactory()
      throws FederateNotExecutionMember, NotConnected {
    throw new UnsupportedOperationException("Currently not supported call : getTransportationTypeHandleFactory()");
  }

  @Override
  public String getHLAversion() {
    throw new UnsupportedOperationException("Currently not supported call : getHLAversion()");
  }

  @Override
  public LogicalTimeFactory getTimeFactory() throws FederateNotExecutionMember, NotConnected {
    throw new UnsupportedOperationException("Currently not supported call : getTimeFactory()");
  }
}
