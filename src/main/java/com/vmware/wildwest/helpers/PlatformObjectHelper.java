package com.vmware.wildwest.helpers;

import com.vmware.wildwest.models.PlatformObject;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.ClientBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlatformObjectHelper {

	private ApiClient client;
	private CoreV1Api api;
	private String namespace;

	public PlatformObjectHelper() {
		try {

			// Get the current namespace
			namespace = System.getenv("K8S_NAMESPACE");
			if (namespace == null || "".equals(namespace)) {
				namespace = "default";
			}

			// Let's establish a connection to the API server
			client = ClientBuilder.cluster().build();
			// set the global default api-client to the in-cluster one from above
			Configuration.setDefaultApiClient(client);
			// the CoreV1Api loads default api-client from global configuration.
			api = new CoreV1Api();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<PlatformObject> getPlatformObjects() {

		ArrayList<PlatformObject> platformObjects = new ArrayList<>();
		platformObjects.addAll(this.getPods());
		platformObjects.addAll(this.getPVs());
		platformObjects.addAll(this.getServices());

		return platformObjects;

	}

	public PlatformObject getRandomPlatformObject() {
		List<PlatformObject> theObjects = this.getPlatformObjects();
		

		return theObjects.get(new Random().nextInt(theObjects.size()));
	}

	private List<PlatformObject> getPods() {
		ArrayList<PlatformObject> thePods = new ArrayList<>();
		try {
			V1PodList pods = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null);
			for (V1Pod item : pods.getItems()) {
				thePods.add(new PlatformObject(item.getMetadata().getUid(), item.getMetadata().getName(), "POD"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return thePods;
	}


	private List<PlatformObject> getPVs() {
		ArrayList<PlatformObject> thePVs = new ArrayList<>();
		try {
			V1PersistentVolumeClaimList pvs = api.listNamespacedPersistentVolumeClaim(namespace, true, null,null,null,null,null
			,null,null,false);

			for (V1PersistentVolumeClaim item : pvs.getItems()) {
				thePVs.add(new PlatformObject(item.getMetadata().getUid(), item.getMetadata().getName(), "PVC"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return thePVs;
	}

	private List<PlatformObject> getServices() {
		ArrayList<PlatformObject> theServices = new ArrayList<>();
		try {
			V1ServiceList services = api.listNamespacedService(namespace, true, null, null, null, null, null, null, null, null);

			for (V1Service item : services.getItems()) {
				theServices.add(new PlatformObject(item.getMetadata().getUid(), item.getMetadata().getName(), "SERVICE"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return theServices;
	}

	public void deletePlatformObject(String gameID, String objectID, String objectType, String objectName) {
		try {

			switch (objectType) {
				case "POD":
					//client.pods().withName(objectName).delete();
					api.deleteNamespacedPod(objectName, namespace, null, null, null, null, null, null);
					break;
				case "SERVICE":
					//client.builds().withName(objectName).delete();
					
					break;
				case "PVC":
					//client.builds().withName(objectName).delete();
					api.deleteNamespacedPersistentVolumeClaim(objectName, namespace, null, null, null, null, null, null);
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
